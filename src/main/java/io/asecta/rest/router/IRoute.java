package io.asecta.rest.router;

import io.asecta.rest.authentication.Accessor;
import io.asecta.rest.router.requesthandler.RequestBody;
import io.asecta.rest.router.requesthandler.RequestPayload;
import io.asecta.rest.router.requesthandler.exception.InvalidBodyException;

public interface IRoute {

	public String getBaseRoute();

	public boolean isAcceptable(RequestPayload request);

	public void handle(String[] params, Accessor accessor, RequestPayload requestPayload, RequestBody body, ResponsePayload payload)
			throws InvalidBodyException;

}
