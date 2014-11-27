package com.realtech.socialsurvey.core.services.generator;

import com.realtech.socialsurvey.core.exception.NonFatalException;

// JIRA: SS-6: By RM03

/**
 * Exception class for malformed Urls in the UrlGeneratorImpl class.
 */
public class InvalidUrlException extends NonFatalException {

	private static final long serialVersionUID = 1L;

	public InvalidUrlException() {
		super();
	}

	public InvalidUrlException(String message) {
		super(message);
	}

	public InvalidUrlException(String message, Throwable thrw) {
		super(message, thrw);
	}

	public InvalidUrlException(String message, String errorCode) {
		super(message, errorCode);
	}

	public InvalidUrlException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}

}
