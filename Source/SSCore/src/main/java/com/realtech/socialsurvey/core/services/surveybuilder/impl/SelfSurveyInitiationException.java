package com.realtech.socialsurvey.core.services.surveybuilder.impl;

import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Exception thrown when survey is initiated for self
 *
 */
public class SelfSurveyInitiationException extends NonFatalException {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public SelfSurveyInitiationException() {
		super();
	}

	public SelfSurveyInitiationException(String message) {
		super(message);
	}

	public SelfSurveyInitiationException(String message, Throwable thrw) {
		super(message, thrw);
	}
	
	public SelfSurveyInitiationException(String message, String errorCode) {
		super(message,errorCode);
	}

	public SelfSurveyInitiationException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}
}
