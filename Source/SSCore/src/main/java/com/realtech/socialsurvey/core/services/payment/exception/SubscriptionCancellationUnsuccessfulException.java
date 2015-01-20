package com.realtech.socialsurvey.core.services.payment.exception;
// RM03
import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Exception class for unsuccessful cancellation of subscription. 
 */
public class SubscriptionCancellationUnsuccessfulException extends NonFatalException{
	
	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public SubscriptionCancellationUnsuccessfulException() {
		super();
	}

	public SubscriptionCancellationUnsuccessfulException(String message) {
		super(message);
	}

	public SubscriptionCancellationUnsuccessfulException(String message, Throwable thrw) {
		super(message, thrw);
	}

	public SubscriptionCancellationUnsuccessfulException(String message, String errorCode) {
		super(message, errorCode);
	}

	public SubscriptionCancellationUnsuccessfulException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}

}
