package com.realtech.socialsurvey.core.services.payment.exception;

import com.realtech.socialsurvey.core.exception.NonFatalException;
// RM03

/**
 * Exception class thrown if a particular subscription is due.
 *
 */
public class SubscriptionPastDueException extends NonFatalException {
	
	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public SubscriptionPastDueException() {
		super();
	}

	public SubscriptionPastDueException(String message) {
		super(message);
	}

	public SubscriptionPastDueException(String message, Throwable thrw) {
		super(message, thrw);
	}

	public SubscriptionPastDueException(String message, String errorCode) {
		super(message, errorCode);
	}

	public SubscriptionPastDueException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}

}
