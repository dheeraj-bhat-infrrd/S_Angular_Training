package com.realtech.socialsurvey.core.exception;

/**
 * Exception class for handling branch addition errors
 */
public class BranchAdditionException extends NonFatalException {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public BranchAdditionException() {
		super();
	}

	public BranchAdditionException(String message) {
		super(message);
	}

	public BranchAdditionException(String message, Throwable thrw) {
		super(message, thrw);
	}
	
	public BranchAdditionException(String message, String errorCode) {
		super(message,errorCode);
	}

	public BranchAdditionException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}
}