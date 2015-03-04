package com.realtech.socialsurvey.core.services.mq;

import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Exception regarding format of mq messages
 *
 */
public class InvalidMessageFormatException extends NonFatalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7937879423871686132L;
	
	public InvalidMessageFormatException() {
		super();
	}

	public InvalidMessageFormatException(String message) {
		super(message);
	}

	public InvalidMessageFormatException(String message, Throwable thrw) {
		super(message, thrw);
	}
	
	public InvalidMessageFormatException(String message, String errorCode) {
		super(message,errorCode);
	}

	public InvalidMessageFormatException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}

}
