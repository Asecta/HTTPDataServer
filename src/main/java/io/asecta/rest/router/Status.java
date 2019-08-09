package io.asecta.rest.router;

import com.google.gson.JsonObject;

public enum Status {

	OK(200, "OK"),
	CREATED(201, "Created"),
	ACCEPTED(202, "Accepted"),
	NO_CONTENT(204, "No Content"),
	MOVED_PERMANENTLY(301, "Moved Permanently"),
	SEE_OTHER(303, "See Other"),
	NOT_MODIFIED(304, "Not Modified"),
	TEMPORARY_REDIRECT(307, "Temporary Redirect"),
	BAD_REQUEST(400, "Bad Request"),
	UNAUTHORIZED(401, "Unauthorized"),
	FORBIDDEN(403, "Forbidden"),
	NOT_FOUND(404, "Not Found"),
	NOT_ACCEPTABLE(406, "Not Acceptable"),
	CONFLICT(409, "Conflict"),
	GONE(410, "Gone"),
	PRECONDITION_FAILED(412, "Precondition Failed"),
	UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
	INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
	SERVICE_UNAVAILABLE(503, "Service Unavailable");

	private final int code;
	private final String message;
	private final Family family;

	public enum Family {
		INFORMATIONAL,
		SUCCESSFUL,
		REDIRECTION,
		CLIENT_ERROR,
		SERVER_ERROR,
		OTHER
	}

	Status(final int statusCode, final String reasonPhrase) {
		this.code = statusCode;
		this.message = reasonPhrase;
		switch (code / 100) {
		case 1:
			this.family = Family.INFORMATIONAL;
			break;
		case 2:
			this.family = Family.SUCCESSFUL;
			break;
		case 3:
			this.family = Family.REDIRECTION;
			break;
		case 4:
			this.family = Family.CLIENT_ERROR;
			break;
		case 5:
			this.family = Family.SERVER_ERROR;
			break;
		default:
			this.family = Family.OTHER;
			break;
		}
	}

	public Family getFamily() {
		return family;
	}

	public int getStatusCode() {
		return code;
	}

	public String getMessage() {
		return toString();
	}

	@Override
	public String toString() {
		return message;
	}

	public JsonObject asJSON() {
		JsonObject json = new JsonObject();
		json.addProperty("code", code);
		json.addProperty("message", message);
		json.addProperty("family", family.toString());
		return json;
	}

	public static Status fromStatusCode(final int statusCode) {
		for (Status s : Status.values()) {
			if (s.code == statusCode) {
				return s;
			}
		}
		return null;
	}
}
