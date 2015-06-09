package com.realtech.socialsurvey.core.exception;

public class UserSessionInvalidateException extends RuntimeException {

	/**
	 * Default serial version uid
	 */
	private static final long serialVersionUID = 1L;

	public UserSessionInvalidateException() {
		super();
	}

	public UserSessionInvalidateException(String message) {
		super(message);
	}

	public UserSessionInvalidateException(String message, Throwable thrw) {
		super(message, thrw);
	}
}
