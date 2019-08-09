package io.asecta.rest.router.responsehandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import io.asecta.rest.router.ResponsePayload;
import io.asecta.rest.router.requesthandler.RequestPayload;

@FunctionalInterface
public interface IResponseHandler {
	public void handle(RequestPayload request, HttpServletResponse response, ResponsePayload payload)
			throws IOException, ServletException;
}
