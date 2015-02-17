package com.realtech.socialsurvey.core.exception;

/**
 * JIRA:SS-117 by RM02 
 * Exception class to be used when validation of user input fails
 */
public class InputValidationException extends BaseRestException {

	/**
	 * Default serial version id
	 */
	private static final long serialVersionUID = 1L;

	public InputValidationException(ErrorCode errorCode, String debugMessage) {
		super();
		this.debugMessage = debugMessage;
		this.errorCode = errorCode;
	}

}
