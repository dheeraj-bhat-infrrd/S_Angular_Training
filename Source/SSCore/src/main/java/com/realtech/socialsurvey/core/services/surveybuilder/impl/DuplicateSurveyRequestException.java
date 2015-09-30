package com.realtech.socialsurvey.core.services.surveybuilder.impl;

import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Exception thrown when survey is sent to an already sent user
 *
 */
public class DuplicateSurveyRequestException extends NonFatalException {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public DuplicateSurveyRequestException() {
		super();
	}

	public DuplicateSurveyRequestException(String message) {
		super(message);
	}

	public DuplicateSurveyRequestException(String message, Throwable thrw) {
		super(message, thrw);
	}
	
	public DuplicateSurveyRequestException(String message, String errorCode) {
		super(message,errorCode);
	}

	public DuplicateSurveyRequestException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}
}
