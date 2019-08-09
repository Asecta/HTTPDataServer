package io.asecta.rest.router.requesthandler.exception;

import io.asecta.rest.router.Status;

public class InvalidBodyException extends Exception {

	private static final long serialVersionUID = -4986909066050064104L;
	private Status status;
	private String message;

	public InvalidBodyException(Status status, String message) {
		this.status = status;
		this.message = message;
	}

	public Status getStatus() {
		return status;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
