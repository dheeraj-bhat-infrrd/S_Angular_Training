package com.realtech.socialsurvey.core.services.mail;

// JIRA: SS-7: By RM02: BOC
import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Exception class for handling failure in delivery of mails
 */
public class UndeliveredEmailException extends NonFatalException {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = -215264659528341674L;

	public UndeliveredEmailException() {
		super();
	}

	public UndeliveredEmailException(String message) {
		super(message);
	}

	public UndeliveredEmailException(String message, Throwable thrw) {
		super(message, thrw);
	}

	public UndeliveredEmailException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}

	public UndeliveredEmailException(String message, String errorCode) {
		super(message, errorCode);
	}

}
// JIRA: SS-7: By RM02: EOC