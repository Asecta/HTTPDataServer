package io.asecta.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import io.asecta.core.dependencyinjection.annotations.Inject;
import io.asecta.rest.authentication.Accessor;
import io.asecta.rest.authentication.IAuthenticator;
import io.asecta.rest.authentication.Protected;
import io.asecta.rest.router.IRoute;
import io.asecta.rest.router.ResponsePayload;
import io.asecta.rest.router.Status;
import io.asecta.rest.router.requesthandler.RequestPayload;
import io.asecta.rest.router.requesthandler.exception.InvalidBodyException;
import io.asecta.rest.router.responsehandler.IResponseHandler;
import io.asecta.rest.router.responsehandler.IResponseHeaderHandler;

public class SessionRouter extends AbstractHandler {

	// Grab core handlers from DI.
	@Inject private IAuthenticator authenticator;
	@Inject private IResponseHandler responseHandler;
	@Inject private IResponseHeaderHandler responseHeaderHandler;

	private Map<String, IRoute> routes = new HashMap<>();

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest httpServletRequest,
			HttpServletResponse response) throws IOException, ServletException {

		// Initialise payloads
		RequestPayload requestPayload = new RequestPayload(baseRequest, httpServletRequest);
		ResponsePayload payload = new ResponsePayload();

		// Match Response headers to Jetty
		responseHeaderHandler.setResponseHeaders(response);

		// CORS hotfix implementation
		// TODO: Clean up implementation
		if (requestPayload.getMethod().equals("OPTIONS")) {
			baseRequest.setHandled(true);
			return;
		}

		// Get the current accessor from the authenticator
		Accessor accessor = authenticator.authenticate(baseRequest, httpServletRequest, response);

		// Find the route being targeted.
		String[] pathParams = target.substring(1).toUpperCase().split("[/]");
		IRoute route = findRoute(pathParams);
		handleRoute(pathParams, route, accessor, requestPayload, payload);

		// Pass to response handler to parse output
		responseHandler.handle(requestPayload, response, payload);
	}

	// Parses the input so it can be easily ingested by the route handler.
	public void handleRoute(String[] pathParams, IRoute route, Accessor accessor, RequestPayload requestPayload,
			ResponsePayload payload) {
		// Check if route exists
		if (route == null) {
			payload.setStatus(Status.NOT_FOUND);
			return;
		}

		// Check if the route finds the payload acceptable
		if (!route.isAcceptable(requestPayload)) {
			payload.setMessage("Request Not Acceptable");
			payload.setStatus(Status.NOT_ACCEPTABLE);
		}

		// Check if the request body can be parsed
		if (!requestPayload.parseBody()) {
			payload.setStatus(Status.BAD_REQUEST);
			payload.setMessage("Invalid Body JSON");
			return;
		}

		// Check if the accessor has permission to access the route (If the route is
		// protected)
		if (route instanceof Protected) {
			Protected protectedRoute = (Protected) route;

			if (accessor == null) {
				payload.setStatus(Status.UNAUTHORIZED);
				payload.setMessage("You don't have permission to access this.");
				return;
			}

			if (!protectedRoute.canAccess(accessor)) {
				payload.setStatus(Status.FORBIDDEN);
				payload.setMessage("You don't have permission to access this.");
				return;
			}
		}

		// Pass the request to the route for handling.
		try {
			
			System.out.println("Accessor " + accessor.getUsername() + " requesting...");
			
			route.handle(pathParams, accessor, requestPayload, requestPayload.getBody(), payload);
		} catch (InvalidBodyException e) {
			payload.setStatus(e.getStatus());
			payload.setMessage(e.getMessage());
		}
	}

	public IRoute findRoute(String[] pathParams) {
		// Extract root path parameter
		String rootPath = pathParams[0];

		// Search for matching root
		for (String route : routes.keySet()) {
			if (route.equals(rootPath)) {
				return routes.get(route);
			}
		}

		// Return null if not found
		return null;
	}

	public void addRoute(IRoute route) {
		String target = route.getBaseRoute().toUpperCase();
		target = target.startsWith("/") ? target.substring(1) : target;
		routes.put(target, route);
		System.out.println("[SESSION ROUTER] Added Base Route: " + route.getBaseRoute());
	}
}