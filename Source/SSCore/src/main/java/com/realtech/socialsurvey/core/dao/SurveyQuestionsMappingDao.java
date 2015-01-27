package com.realtech.socialsurvey.core.dao;

import java.util.List;
import com.realtech.socialsurvey.core.entities.Survey;
import com.realtech.socialsurvey.core.entities.SurveyQuestionsMapping;
import com.realtech.socialsurvey.core.entities.User;

/*
 * This interface contains methods which are required for queries and criteria on User table.
 */
public interface SurveyQuestionsMappingDao extends GenericDao<SurveyQuestionsMapping, Long> {

	/**
	 * Method to return all active Survey Question in ascending order
	 */
	public List<SurveyQuestionsMapping> fetchActiveSurveyQuestions(User user, Survey survey);
}
