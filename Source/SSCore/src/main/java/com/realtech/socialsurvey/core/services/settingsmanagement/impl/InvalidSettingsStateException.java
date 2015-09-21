package com.realtech.socialsurvey.core.services.settingsmanagement.impl;

import com.realtech.socialsurvey.core.exception.NonFatalException;

public class InvalidSettingsStateException extends NonFatalException {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public InvalidSettingsStateException() {
		super();
	}

	public InvalidSettingsStateException(String message) {
		super(message);
	}

	public InvalidSettingsStateException(String message, Throwable thrw) {
		super(message, thrw);
	}

	public InvalidSettingsStateException(String message, String errorCode) {
		super(message, errorCode);
	}

	public InvalidSettingsStateException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}
}
