package io.asecta.rest.router.autorouter;

import java.lang.invoke.MethodHandle;

import io.asecta.rest.authentication.Accessor;
import io.asecta.rest.router.IRoute;
import io.asecta.rest.router.ResponsePayload;
import io.asecta.rest.router.requesthandler.RequestBody;
import io.asecta.rest.router.requesthandler.RequestPayload;

public class AutoRouteNode implements IRoute {

	private IRoute parent;
	private String baseRoute;

	private MethodHandle handler;

	public AutoRouteNode(IRoute parent, String baseRoute, MethodHandle handler) {
		this.parent = parent;
		this.baseRoute = baseRoute;
		this.handler = handler;
	}

	@Override
	public void handle(String[] pathParams, Accessor accessor, RequestPayload request, RequestBody body, ResponsePayload payload) {
		try {
			handler.invoke(parent, pathParams, accessor, request, body, payload);
			return;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Route unhandled");
	}

	@Override
	public String getBaseRoute() {
		return baseRoute;
	}

	@Override
	public boolean isAcceptable(RequestPayload request) {
		return true; 
	}

}