package io.asecta.rest.router.responsehandler;

import javax.servlet.http.HttpServletResponse;

public interface IResponseHeaderHandler {
	public void setResponseHeaders(HttpServletResponse response);
}
