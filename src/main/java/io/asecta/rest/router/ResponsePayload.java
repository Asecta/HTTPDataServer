package io.asecta.rest.router;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.MimeTypes;;

public class ResponsePayload {

	private Map<String, Object> payload;
	private Status status;
	private boolean isImage = false;
	private String contentType = MimeTypes.Type.APPLICATION_JSON.asString();

	public ResponsePayload() {
		payload = new HashMap<>();
		status = Status.OK;
	}

	public boolean isImage() {
		return isImage;
	}

	public void setImage() {
		isImage = true;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

	public void add(String key, Object value) {
		payload.put(key, value);
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public void setContent(Object object) {
		payload.put("content", object);
	}

	public Object getContent() {
		return payload.get("content");
	}

	public void setMessage(String message) {
		payload.put("message", message);
	}

	public Map<String, Object> getPayload() {
		payload.put("status", status.asJSON());
		return payload;
	}

	public void clear() {
		payload.clear();
	}

	public void forbidden() {
		setStatus(Status.FORBIDDEN);
	}

}
