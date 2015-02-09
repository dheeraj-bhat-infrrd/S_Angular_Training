package com.realtech.socialsurvey.core.services.payment.exception;
// RM03
import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Exception class for unsuccessful card update.
 *
 */
public class CardUpdateUnsuccessfulException extends NonFatalException {
	
	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public CardUpdateUnsuccessfulException() {
		super();
	}

	public CardUpdateUnsuccessfulException(String message) {
		super(message);
	}

	public CardUpdateUnsuccessfulException(String message, Throwable thrw) {
		super(message, thrw);
	}
	
	public CardUpdateUnsuccessfulException(String message, String errorCode) {
		super(message,errorCode);
	}

	public CardUpdateUnsuccessfulException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}

}
