package io.asecta.rest.router.responsehandler.cors;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletResponse;

import io.asecta.core.dependencyinjection.Initializable;
import io.asecta.core.utils.CSVArrayList;
import io.asecta.rest.router.responsehandler.IResponseHeaderHandler;

public class BasicCorsResponseHeaderHandler implements IResponseHeaderHandler, Initializable {

	private static final String ACA_ORIGIN = "Access-Control-Allow-Origin";
	private static final String ACA_CREDENTIALS = "Access-Control-Allow-Credentials";
	private static final String ACA_METHODS = "Access-Control-Allow-Methods";
	private static final String ACA_HEADERS = "Access-Control-Allow-Headers";

	private Map<String, Supplier<String>> headerMap = new HashMap<>();
	private CSVArrayList<String> allowedHeaders = new CSVArrayList<>();
	private CSVArrayList<String> allowedMethods = new CSVArrayList<>();

	@Override
	public void initialize() {
		initDefaultAllowedHeaders();
		initDefaultAllowedMethods();
		initDefaultHeadersMap();
	}

	private void initDefaultAllowedHeaders() {
		addACAHeader("Access-Control-Allow-Headers");
		addACAHeader("Origin");
		addACAHeader("Accept");
		addACAHeader("X-Requested-With");
		addACAHeader("Content-Type");
		addACAHeader("Access-Control-Request-Headers");
		addACAHeader("Access-Control-Request-Method");
		addACAHeader("Access-Control-Allow-Origin");
	}

	private void initDefaultAllowedMethods() {
		addACAMethod("OPTIONS");
		addACAMethod("POST");
	}

	private void initDefaultHeadersMap() {
		addHeader(ACA_ORIGIN, "*");
		addHeader(ACA_CREDENTIALS, "true");
		addHeader(ACA_HEADERS, allowedHeaders);
		addHeader(ACA_METHODS, allowedMethods);
	}

	@Override
	public void setResponseHeaders(HttpServletResponse response) {
		for (String header : headerMap.keySet()) {
			response.addHeader(header, headerMap.get(header).get());
		}
	}

	public void setACAOrigin(String value) {
		headerMap.put(ACA_ORIGIN, () -> value);
	}

	public void setACACredentials(boolean value) {
		headerMap.put(ACA_CREDENTIALS, () -> Boolean.toString(value));
	}

	public void addACAHeader(String string) {
		allowedHeaders.add(string);
	}

	public void removeACAHeader(String string) {
		allowedHeaders.remove(string);
	}

	public void addACAMethod(String string) {
		allowedMethods.add(string);
	}

	public void removeACAMethod(String string) {
		allowedMethods.remove(string);
	}

	public void setACAHeaders(CSVArrayList<String> list) {
		allowedHeaders = list;
	}

	public void setACAMethods(CSVArrayList<String> list) {
		allowedMethods = list;
	}

	public List<String> getACAHeaders() {
		return allowedHeaders;
	}

	public List<String> getACAMethods() {
		return allowedMethods;
	}

	public void addHeader(String key, String value) {
		addHeader(key, () -> value);
	}

	public void addHeader(String key, Supplier<String> value) {
		headerMap.put(key, value);
	}

}
