package com.realtech.socialsurvey.core.exception;
//JIRA SS-26 by RM05 BOC
/**
 * Exception class for handling invalid inputs detected across the application
 */
public class UserAlreadyExistsException extends NonFatalException {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public UserAlreadyExistsException() {
		super();
	}

	public UserAlreadyExistsException(String message) {
		super(message);
	}

	public UserAlreadyExistsException(String message, Throwable thrw) {
		super(message, thrw);
	}
	
	public UserAlreadyExistsException(String message, String errorCode) {
		super(message,errorCode);
	}

	public UserAlreadyExistsException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}
}
//JIRA SS-26 by RM05 EOC