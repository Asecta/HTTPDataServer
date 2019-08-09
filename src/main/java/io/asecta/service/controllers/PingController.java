package io.asecta.service.controllers;

import io.asecta.rest.authentication.Accessor;
import io.asecta.rest.router.ResponsePayload;
import io.asecta.rest.router.autorouter.AutoRouter;
import io.asecta.rest.router.autorouter.Mapping;
import io.asecta.rest.router.requesthandler.RequestBody;
import io.asecta.rest.router.requesthandler.RequestPayload;
import io.asecta.rest.router.requesthandler.exception.InvalidBodyException;

public class PingController extends AutoRouter {

	@Override
	public String getBaseRoute() {
		return "ping";
	}

	@Override
	public boolean isAcceptable(RequestPayload request) {
		return true;
	}

	@Override
	public void handleBase(String[] pathParams, Accessor accessor, RequestPayload request, RequestBody body, ResponsePayload payload)
			throws InvalidBodyException {
		request.getBody().allowNull();
		String name = request.getBody().getString("name");
		System.out.println("Got name " + name);
		payload.setContent(String.format("Hey, %s", name));
	}

	@Mapping("pong")
	public void handleList(String[] pathParams, Accessor accessor, RequestPayload request, RequestBody body, ResponsePayload payload)
			throws InvalidBodyException {
		payload.setContent("Ping pong");
	}
}
