package com.realtech.socialsurvey.core.services.organizationmanagement;

import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Exception regarding format of mq messages
 *
 */
public class ProfileNotFoundException extends NonFatalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7937879423871686132L;
	
	public ProfileNotFoundException() {
		super();
	}

	public ProfileNotFoundException(String message) {
		super(message);
	}

	public ProfileNotFoundException(String message, Throwable thrw) {
		super(message, thrw);
	}
	
	public ProfileNotFoundException(String message, String errorCode) {
		super(message,errorCode);
	}

	public ProfileNotFoundException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}

}
