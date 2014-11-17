package com.realtech.socialsurvey.core.exception;

/**
 * Exception class for handling invalid inputs detected across the application
 */
public class InvalidInputException extends NonFatalException {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 1L;

	public InvalidInputException() {
		super();
	}

	public InvalidInputException(String message) {
		super(message);
	}

	public InvalidInputException(String message, Throwable thrw) {
		super(message, thrw);
	}
}
