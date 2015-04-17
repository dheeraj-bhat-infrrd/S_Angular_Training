package com.realtech.socialsurvey.core.exception;

/**
 * Exception class for handling branch addition errors
 */
public class NoContextFoundException extends NonFatalException {

	private static final long serialVersionUID = 2015020702364453334L;

	public NoContextFoundException() {
		super();
	}

	public NoContextFoundException(String message) {
		super(message);
	}

	public NoContextFoundException(String message, Throwable thrw) {
		super(message, thrw);
	}

	public NoContextFoundException(String message, String errorCode) {
		super(message, errorCode);
	}

	public NoContextFoundException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}
}