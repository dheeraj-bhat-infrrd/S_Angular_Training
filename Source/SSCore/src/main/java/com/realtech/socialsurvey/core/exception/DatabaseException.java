package com.realtech.socialsurvey.core.exception;

/**
 * Exception class for handling database related errors.
 */
public class DatabaseException extends FatalException{

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 3766107935557465321L;
	
	public DatabaseException() {
		super();
	}

	public DatabaseException(String message) {
		super(message);
	}

	public DatabaseException(String message, Throwable thrw) {
		super(message, thrw);
	}
}
