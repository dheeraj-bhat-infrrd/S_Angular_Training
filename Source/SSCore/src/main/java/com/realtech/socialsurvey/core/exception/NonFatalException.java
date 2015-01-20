package com.realtech.socialsurvey.core.exception;

// JIRA: SS-4: By RM04: BOC
/**
 * Typically, a non-critical exception, which which can be rectified by user intervention. This is
 * base exception which needs to be extended by all the similar exception scenarios.
 */
public class NonFatalException extends Exception {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 5677764982124422018L;

	private String errorCode;

	public NonFatalException() {
		super();
	}

	public NonFatalException(String message) {
		super(message);
	}

	public NonFatalException(String message, Throwable thrw) {
		super(message, thrw);
	}

	public NonFatalException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public NonFatalException(String message, String errorCode, Throwable thrw) {
		super(message, thrw);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

}

// JIRA: SS-4: By RM04: EOC
