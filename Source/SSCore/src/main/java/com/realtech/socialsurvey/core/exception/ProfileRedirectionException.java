package com.realtech.socialsurvey.core.exception;

/**
 * Exception class for handling situation where no records are fetched from the database.
 */
public class ProfileRedirectionException extends NonFatalException {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public ProfileRedirectionException() {
		super();
	}

	public ProfileRedirectionException(String message) {
		super(message);
	}

	public ProfileRedirectionException(String message, Throwable thrw) {
		super(message, thrw);
	}

	public ProfileRedirectionException(String message, String errorCode) {
		super(message, errorCode);
	}

	public ProfileRedirectionException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}

}
