package com.realtech.socialsurvey.core.services.surveybuilding;

import java.util.List;

import com.realtech.socialsurvey.core.entities.Survey;
import com.realtech.socialsurvey.core.entities.SurveyQuestion;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
import com.realtech.socialsurvey.core.entities.User;

public interface SurveyBuilder {

	public void createNewSurvey(User user,
			List<SurveyQuestionDetails> surveyQuestions, String surveyName);

	public void addQuestionsToExistingSurvey(User user, Survey survey,
			List<SurveyQuestionDetails> surveyQuestions);

	public void deactivateExistingSurveyMappings(User user,
			SurveyQuestion surveyQuestion);

}
