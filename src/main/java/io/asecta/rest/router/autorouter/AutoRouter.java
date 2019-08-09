package io.asecta.rest.router.autorouter;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.asecta.core.dependencyinjection.Initializable;
import io.asecta.core.utils.Reflect;
import io.asecta.rest.authentication.Accessor;
import io.asecta.rest.router.IRoute;
import io.asecta.rest.router.ResponsePayload;
import io.asecta.rest.router.Status;
import io.asecta.rest.router.requesthandler.RequestBody;
import io.asecta.rest.router.requesthandler.RequestPayload;
import io.asecta.rest.router.requesthandler.exception.InvalidBodyException;

public abstract class AutoRouter implements IRoute, Initializable {

	private Map<String, IRoute> routes;

	public abstract void handleBase(String[] pathParams, Accessor accessor, RequestPayload request, RequestBody body,
			ResponsePayload payload) throws InvalidBodyException;

	@Override
	public void handle(String[] pathParams, Accessor accessor, RequestPayload request, RequestBody body,
			ResponsePayload payload) throws InvalidBodyException {

		if (pathParams.length == 1) {
			handleBase(pathParams, accessor, request, body, payload);
			return;
		}

		IRoute route = findRoute(pathParams[1]);

		if (route == null) {
			payload.setStatus(Status.NOT_FOUND);
			return;
		}

		route.handle(pathParams, accessor, request, body, payload);
	}

	@Override
	public void initialize() {
		System.out.println("Initializing Autorouter for " + getBaseRoute());
		routes = new HashMap<>();

		List<Method> methods = Reflect.getAnnotatedMethods(getClass(), Mapping.class);

		for (Method method : methods) {
			IRoute route = unreflect(method);
			routes.put(route.getBaseRoute().toUpperCase(), route);
			System.out.println("  Initializing: " + route.getBaseRoute());
		}
	}

	// Inwraps the method into an iRoute
	public IRoute unreflect(Method method) {
		IRoute route = null;
		try {
			// Uses Methodhandle's for faster execution .
			MethodHandle handler = MethodHandles.lookup().unreflect(method);
			Mapping anno = method.getAnnotation(Mapping.class);
			route = new AutoRouteNode(this, anno.value(), handler);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return route;
	}

	public IRoute findRoute(String path) {
		for (String route : routes.keySet()) {
			if (route.equals(path)) {
				return routes.get(route);
			}
		}
		return null;
	}
}
