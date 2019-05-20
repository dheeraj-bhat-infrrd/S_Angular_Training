package com.realtech.socialsurvey.core.services.surveybuilder.impl;

import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Exception thrown when survey contains duplicate contact.
 *
 */
public class DuplicateContactSurveyRequestException extends NonFatalException {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public DuplicateContactSurveyRequestException() {
		super();
	}

	public DuplicateContactSurveyRequestException(String message) {
		super(message);
	}

	public DuplicateContactSurveyRequestException(String message, Throwable thrw) {
		super(message, thrw);
	}
	
	public DuplicateContactSurveyRequestException(String message, String errorCode) {
		super(message,errorCode);
	}

	public DuplicateContactSurveyRequestException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}
}
