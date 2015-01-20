package com.realtech.socialsurvey.core.services.payment.exception;

import com.realtech.socialsurvey.core.exception.NonFatalException;

public class PaymentException extends NonFatalException {
	
	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public PaymentException() {
		super();
	}

	public PaymentException(String message) {
		super(message);
	}

	public PaymentException(String message, Throwable thrw) {
		super(message, thrw);
	}
	
	public PaymentException(String message, String errorCode) {
		super(message,errorCode);
	}

	public PaymentException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}

}
