package com.realtech.socialsurvey.core.exception;

/**
 * Exception class for handling user addition errors
 */
public class UserAdditionException extends NonFatalException {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public UserAdditionException() {
		super();
	}

	public UserAdditionException(String message) {
		super(message);
	}

	public UserAdditionException(String message, Throwable thrw) {
		super(message, thrw);
	}
	
	public UserAdditionException(String message, String errorCode) {
		super(message,errorCode);
	}

	public UserAdditionException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}
}
