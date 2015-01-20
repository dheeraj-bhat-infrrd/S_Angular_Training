package com.realtech.socialsurvey.core.exception;

/**
 * Exception class for handling situation where no records are fetched from the database.
 */
public class NoRecordsFetchedException extends NonFatalException {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public NoRecordsFetchedException() {
		super();
	}

	public NoRecordsFetchedException(String message) {
		super(message);
	}

	public NoRecordsFetchedException(String message, Throwable thrw) {
		super(message, thrw);
	}

	public NoRecordsFetchedException(String message, String errorCode) {
		super(message, errorCode);
	}

	public NoRecordsFetchedException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}

}
