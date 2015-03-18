package com.realtech.socialsurvey.core.services.organizationmanagement;

import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Exception class to handle errors caused while assigning a user to a particular hierarchy level
 */
public class UserAssignmentException extends NonFatalException {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 1L;

	public UserAssignmentException() {
		super();
	}

	public UserAssignmentException(String message) {
		super(message);
	}

	public UserAssignmentException(String message, Throwable thrw) {
		super(message, thrw);
	}

	public UserAssignmentException(String message, String errorCode) {
		super(message, errorCode);
	}

	public UserAssignmentException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}

}
