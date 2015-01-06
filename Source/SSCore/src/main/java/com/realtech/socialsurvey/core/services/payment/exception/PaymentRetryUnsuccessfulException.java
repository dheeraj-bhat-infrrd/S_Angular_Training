package com.realtech.socialsurvey.core.services.payment.exception;

import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Exception class for unsuccessful retry of subscription charge.
 *
 */
public class PaymentRetryUnsuccessfulException extends NonFatalException{
	
	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public PaymentRetryUnsuccessfulException() {
		super();
	}

	public PaymentRetryUnsuccessfulException(String message) {
		super(message);
	}

	public PaymentRetryUnsuccessfulException(String message, Throwable thrw) {
		super(message, thrw);
	}

	public PaymentRetryUnsuccessfulException(String message, String errorCode) {
		super(message, errorCode);
	}

	public PaymentRetryUnsuccessfulException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}

}
