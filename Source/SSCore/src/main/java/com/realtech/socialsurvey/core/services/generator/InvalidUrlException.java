package com.realtech.socialsurvey.core.services.generator;

import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * @author RM03
 * JIRA Ticket SS-6
 */
public class InvalidUrlException extends NonFatalException {

	/**
	 * 
	 */
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

}
