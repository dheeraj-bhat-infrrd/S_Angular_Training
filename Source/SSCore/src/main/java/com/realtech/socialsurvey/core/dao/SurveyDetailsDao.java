package com.realtech.socialsurvey.core.dao;

import java.util.List;
import java.util.Map;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyResponse;

public interface SurveyDetailsDao {

	public SurveyDetails getSurveyByAgentIdAndCustomerEmail(long agentId, String customerEmail);
	
	public void insertSurveyDetails(SurveyDetails surveyDetails);

	public void updateEmailForExistingFeedback(long agentId, String customerEmail);

	public void updateCustomerResponse(long agentId, String customerEmail, SurveyResponse surveyResponse, int stage);

	public void updateGatewayAnswer(long agentId, String customerEmail, String mood, String review);

	public void updateFinalScore(long agentId, String customerEmail);

	public Map<String, Long> getCountOfCustomersByMood(String columnName, long columnValue);

	public Map<String, Long> getCountOfCustomersByReminderMails(String columnName, long columnValue);

	public Map<String, Long> getCountOfCustomersByStage(String columnName, long columnValue);
	
	public long getTotalSurveyCountByMonth(int year, int month);

	public Map<String, Long> getSocialPostsCount(String columnName, long columnValue);

	public Map<String, Long> getCountOfSurveyInitiators(String columnName, long columnValue);

	public double getRatingForPastNdays(String columnName, long columnValue, int noOfDays);

	public long getIncompleteSurveyCount(String columnName, long columnValue);

	public long getCompletedSurveyCount(String columnName, long columnValue);

	public long getSentSurveyCount(String columnName, long columnValue);

	public List<SurveyDetails> getFeedbacks(String columnName, long columNValue, double startScore, double limitScore);
	
	public long getFeedBacksCount(String columnName,long columnValue,double startScore,double limitScore);

}
