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

	public void updateGatewayAnswer(long agentId, String customerEmail, String mood, String review, boolean isAbusive);

	public void updateFinalScore(long agentId, String customerEmail);

	public void updateSurveyAsClicked(long agentId, String customerEmail);

	public Map<String, Long> getCountOfCustomersByMood(String columnName, long columnValue);

	public Map<String, Long> getCountOfCustomersByReminderMails(String columnName, long columnValue);

	public Map<String, Long> getCountOfCustomersByStage(String columnName, long columnValue);

	public long getTotalSurveyCountByMonth(int year, int month);

	public long getSocialPostsCount(String columnName, long columnValue, int numberOfDays);

	public Map<String, Long> getCountOfSurveyInitiators(String columnName, long columnValue);

	public double getRatingForPastNdays(String columnName, long columnValue, int noOfDays, boolean aggregateAbusive);

	public long getIncompleteSurveyCount(String columnName, long columnValue, int noOfDays);

	public long getCompletedSurveyCount(String columnName, long columnValue, int noOfDays);

	public long getSentSurveyCount(String columnName, long columnValue, int noOfDays);

	public long getClickedSurveyCount(String columnName, long columnValue, int noOfDays);

	public List<SurveyDetails> getFeedbacks(String columnName, long columNValue, int start, int rows, double startScore, double limitScore,
			boolean fetchAbusive);

	public long getFeedBacksCount(String columnName, long columnValue, double startScore, double limitScore, boolean fetchAbusive);

	public List<SurveyDetails> getIncompleteSurvey(String columnName, long columNValue, int start, int rows, double startScore, double limitScore);

	public void updateReminderCount(long agentId, String customerEmail);

}
