package com.realtech.socialsurvey.core.services.payment.exception;
// RM03
import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Exception class thrown when subscription upgrade is unsuccessful.
 *
 */
public class SubscriptionUpgradeUnsuccessfulException extends NonFatalException{
	
	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public SubscriptionUpgradeUnsuccessfulException() {
		super();
	}

	public SubscriptionUpgradeUnsuccessfulException(String message) {
		super(message);
	}

	public SubscriptionUpgradeUnsuccessfulException(String message, Throwable thrw) {
		super(message, thrw);
	}

	public SubscriptionUpgradeUnsuccessfulException(String message, String errorCode) {
		super(message, errorCode);
	}

	public SubscriptionUpgradeUnsuccessfulException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}
}
