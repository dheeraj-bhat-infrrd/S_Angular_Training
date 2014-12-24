package com.realtech.socialsurvey.core.exception;

/**
 * Exception class for unsuccessful retry of subscription charge.
 *
 */
public class RetryUnsuccessfulException extends NonFatalException{
	
	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public RetryUnsuccessfulException() {
		super();
	}

	public RetryUnsuccessfulException(String message) {
		super(message);
	}

	public RetryUnsuccessfulException(String message, Throwable thrw) {
		super(message, thrw);
	}

	public RetryUnsuccessfulException(String message, String errorCode) {
		super(message, errorCode);
	}

	public RetryUnsuccessfulException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}

}
