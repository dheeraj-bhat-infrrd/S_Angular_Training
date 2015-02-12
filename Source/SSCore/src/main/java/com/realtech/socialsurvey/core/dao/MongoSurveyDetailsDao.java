package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyResponse;

public interface MongoSurveyDetailsDao {

	public void insertSurveyDetails(SurveyDetails surveyDetails);

	public void updateEmailForExistingFeedback(long agentId, String customerEmail);

	public void updateCustomerResponse(long agentId, String customerEmail, SurveyResponse surveyResponse, int stage);

	public void updateGatewayAnswer(long agentId, String customerEmail, String mood, String review);

	public void updateFinalScore(long agentId, String customerEmail);

}
