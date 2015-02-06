package com.realtech.socialsurvey.core.services.payment.exception;
//RM03
import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Exception class for subscription failure.
 *
 */
public class SubscriptionUnsuccessfulException extends NonFatalException {
	
	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public SubscriptionUnsuccessfulException() {
		super();
	}

	public SubscriptionUnsuccessfulException(String message) {
		super(message);
	}

	public SubscriptionUnsuccessfulException(String message, Throwable thrw) {
		super(message, thrw);
	}
	
	public SubscriptionUnsuccessfulException(String message, String errorCode) {
		super(message,errorCode);
	}

	public SubscriptionUnsuccessfulException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}

}
