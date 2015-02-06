package com.realtech.socialsurvey.core.services.surveybuilder.impl;

import java.util.List;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;

// JIRA SS-119 by RM-05:BOC
@Component
public class SurveyHandlerImpl implements SurveyHandler {
	/**
	 * Method to store question and answer format into mongo.
	 * 
	 * @param agentId
	 * @throws InvalidInputException
	 */
	public List<SurveyQuestionDetails> storeInitialSurveyAnswers(SurveyDetails surveyDetails) throws InvalidInputException {

		return null;
	}
}
// JIRA SS-119 by RM-05:EOC
