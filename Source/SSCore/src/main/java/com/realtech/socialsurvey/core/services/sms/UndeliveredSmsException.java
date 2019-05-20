package com.realtech.socialsurvey.core.services.sms;

import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Exception class for handling failure in delivery of sms
 */
public class UndeliveredSmsException extends NonFatalException{

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = -215264659528341674L;

	public UndeliveredSmsException() {
		super();
	}

	public UndeliveredSmsException(String message) {
		super(message);
	}

	public UndeliveredSmsException(String message, Throwable thrw) {
		super(message, thrw);
	}

	public UndeliveredSmsException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}

	public UndeliveredSmsException(String message, String errorCode) {
		super(message, errorCode);
	}

}
