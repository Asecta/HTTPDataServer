package io.asecta.rest.router.requesthandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Request;

public class RequestPayload {

	private Request baseRequest;
	private HttpServletRequest httpServletRequest;

	private RequestBody body;

	public RequestPayload(Request baseRequest, HttpServletRequest httpServletRequest) {
		this.baseRequest = baseRequest;
		this.httpServletRequest = httpServletRequest;
	}

	private String raw;

	public boolean parseBody() {
		try {
			body = new RequestBody(getRawBody());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getRawBody() {
		if (raw == null) {
			try {
				raw = baseRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return raw;
	}

	public RequestBody getBody() {
		return body;
	}

	public Request getBaseRequest() {
		return baseRequest;
	}

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	public Object getAttribute(String name) {
		return httpServletRequest.getAttribute(name);
	}

	public Cookie[] getCookies() {
		return httpServletRequest.getCookies();
	}

	public Enumeration<String> getAttributeNames() {
		return httpServletRequest.getAttributeNames();
	}

	public long getDateHeader(String name) {
		return httpServletRequest.getDateHeader(name);
	}

	public String getCharacterEncoding() {
		return httpServletRequest.getCharacterEncoding();
	}

	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		httpServletRequest.setCharacterEncoding(env);
	}

	public int getContentLength() {
		return httpServletRequest.getContentLength();
	}

	public String getHeader(String name) {
		return httpServletRequest.getHeader(name);
	}

	public long getContentLengthLong() {
		return httpServletRequest.getContentLengthLong();
	}

	public Enumeration<String> getHeaders(String name) {
		return httpServletRequest.getHeaders(name);
	}

	public String getContentType() {
		return httpServletRequest.getContentType();
	}

	public String getParameter(String name) {
		return httpServletRequest.getParameter(name);
	}

	public Enumeration<String> getHeaderNames() {
		return httpServletRequest.getHeaderNames();
	}

	public Enumeration<String> getParameterNames() {
		return httpServletRequest.getParameterNames();
	}

	public String getMethod() {
		return httpServletRequest.getMethod();
	}

	public String[] getParameterValues(String name) {
		return httpServletRequest.getParameterValues(name);
	}

	public Map<String, String[]> getParameterMap() {
		return httpServletRequest.getParameterMap();
	}

	public String getProtocol() {
		return httpServletRequest.getProtocol();
	}

	public String getContextPath() {
		return httpServletRequest.getContextPath();
	}

	public String getServerName() {
		return httpServletRequest.getServerName();
	}

	public int getServerPort() {
		return httpServletRequest.getServerPort();
	}

	public BufferedReader getReader() throws IOException {
		return httpServletRequest.getReader();
	}

	public String getRemoteAddr() {
		return httpServletRequest.getRemoteAddr();
	}

	public String getRemoteHost() {
		return httpServletRequest.getRemoteHost();
	}

	public void setAttribute(String name, Object o) {
		httpServletRequest.setAttribute(name, o);
	}

	public String getRequestURI() {
		return httpServletRequest.getRequestURI();
	}

	public StringBuffer getRequestURL() {
		return httpServletRequest.getRequestURL();
	}

	public int getRemotePort() {
		return httpServletRequest.getRemotePort();
	}

	public HttpSession getSession(boolean create) {
		return httpServletRequest.getSession(create);
	}

	public ServletContext getServletContext() {
		return httpServletRequest.getServletContext();
	}

	public HttpSession getSession() {
		return httpServletRequest.getSession();
	}

	public boolean isAsyncStarted() {
		return httpServletRequest.isAsyncStarted();
	}

	public boolean isAsyncSupported() {
		return httpServletRequest.isAsyncSupported();
	}

	public void setHandled(boolean b) {
		baseRequest.setHandled(b);
	}
}
