package io.asecta.rest.router;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.MultiMap;

public class RestRequest {

	private Request wrapped;

	public Request getWrappedRequest() {
		return wrapped; 
	}
	
	public RestRequest(Request request) {
		wrapped = request;
	}

	public Object getAttribute(String name) {
		return wrapped.getAttribute(name);
	}

	public String getAuthType() {
		return wrapped.getAuthType();
	}

	public Cookie[] getCookies() {
		return wrapped.getCookies();
	}

	public Enumeration<String> getAttributeNames() {
		return wrapped.getAttributeNames();
	}

	public long getDateHeader(String name) {
		return wrapped.getDateHeader(name);
	}

	public String getCharacterEncoding() {
		return wrapped.getCharacterEncoding();
	}

	public int getContentLength() {
		return wrapped.getContentLength();
	}

	public MultiMap<String> getQueryParameters() {
		return wrapped.getQueryParameters();
	}

	public String getRealPath(String path) {
		return wrapped.getRealPath(path);
	}

	public StringBuilder getRootURL() {
		return wrapped.getRootURL();
	}

	public String getHeader(String name) {
		return wrapped.getHeader(name);
	}

	public Enumeration<String> getHeaders(String name) {
		return wrapped.getHeaders(name);
	}

	public String getContentType() {
		return wrapped.getContentType();
	}

	public String getParameter(String name) {
		return wrapped.getParameter(name);
	}

	public Enumeration<String> getHeaderNames() {
		return wrapped.getHeaderNames();
	}

	public Enumeration<String> getParameterNames() {
		return wrapped.getParameterNames();
	}

	public String getMethod() {
		return wrapped.getMethod();
	}

	public String[] getParameterValues(String name) {
		return wrapped.getParameterValues(name);
	}

	public Map<String, String[]> getParameterMap() {
		return wrapped.getParameterMap();
	}

	public String getProtocol() {
		return wrapped.getProtocol();
	}

	public String getQueryString() {
		return wrapped.getQueryString();
	}

	public String getRemoteUser() {
		return wrapped.getRemoteUser();
	}

	public String getRemoteAddr() {
		return wrapped.getRemoteAddr();
	}

	public String getRemoteHost() {
		return wrapped.getRemoteHost();
	}

	public String getRequestURI() {
		return wrapped.getRequestURI();
	}

	public StringBuffer getRequestURL() {
		return wrapped.getRequestURL();
	}

	public String getLocalAddr() {
		return wrapped.getLocalAddr();
	}

}
