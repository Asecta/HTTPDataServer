package io.asecta.rest.router.responsehandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import io.asecta.core.dependencyinjection.annotations.Inject;
import io.asecta.rest.router.ResponsePayload;
import io.asecta.rest.router.requesthandler.RequestPayload;

public class JsonResponseHandler implements IResponseHandler {

	@Inject
	private Gson gson;

	@Override
	public void handle(RequestPayload request, HttpServletResponse response, ResponsePayload payload)
			throws IOException, ServletException {
		String responseString;

		System.out.println("RESPONSEHANDLER");
		System.out.println("Handling Response for " + request.getRequestURI());

		System.out.println("Payload:");
		System.out.println("  " + payload.getContentType());
		System.out.println("  " + payload.isImage());

		if (payload.isImage()) {
			byte[] bytearr = (byte[]) payload.getContent();
			response.getOutputStream().write(bytearr);
			response.setHeader("Content-Length", String.valueOf(bytearr.length));
		} else {
			response.getWriter().println(gson.toJson(payload.getPayload()));
		}

		response.setContentType(payload.getContentType());
		response.setStatus(200);
		request.setHandled(true);
	}
}
