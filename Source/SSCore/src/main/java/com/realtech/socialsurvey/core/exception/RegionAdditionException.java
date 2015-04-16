package com.realtech.socialsurvey.core.exception;

/**
 * Exception class for handling region addition errors
 */
public class RegionAdditionException extends NonFatalException {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public RegionAdditionException() {
		super();
	}

	public RegionAdditionException(String message) {
		super(message);
	}

	public RegionAdditionException(String message, Throwable thrw) {
		super(message, thrw);
	}
	
	public RegionAdditionException(String message, String errorCode) {
		super(message,errorCode);
	}

	public RegionAdditionException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}
}
