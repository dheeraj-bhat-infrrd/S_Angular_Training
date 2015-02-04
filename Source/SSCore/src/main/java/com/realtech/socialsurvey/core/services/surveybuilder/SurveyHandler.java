package com.realtech.socialsurvey.core.services.surveybuilder;

import java.util.List;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface SurveyHandler {

	/**
	 * Method to store question and answer format into mongo.
	 * 
	 * @param agentId
	 * @throws InvalidInputException
	 */
	public List<SurveyQuestionDetails> storeInitialSurveyAnswers(SurveyDetails surveyDetails) throws InvalidInputException;
}
