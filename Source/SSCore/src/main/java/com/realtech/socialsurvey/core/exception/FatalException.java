package com.realtech.socialsurvey.core.exception;
// JIRA: SS-4: By RM04: BOC

/**
 * Typically, a critical exception, which occurs when the application cannot
 * recover from the cause that affected this. This is base exception which needs
 * to be extended by all the similar exception scenarios.
 * 
 */
public class FatalException extends RuntimeException {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = -8364684006386852393L;

	public FatalException() {
		super();
	}

	public FatalException(String message) {
		super(message);
	}

	public FatalException(String message, Throwable thrw) {
		super(message, thrw);
	}

}

// JIRA: SS-4: By RM04: EOC