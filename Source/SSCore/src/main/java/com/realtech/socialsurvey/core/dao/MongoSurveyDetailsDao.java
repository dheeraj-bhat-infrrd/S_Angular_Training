package com.realtech.socialsurvey.core.dao;

import java.util.Map;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyResponse;

public interface MongoSurveyDetailsDao {

	public void insertSurveyDetails(SurveyDetails surveyDetails);

	public void updateEmailForExistingFeedback(long agentId, String customerEmail);

	public void updateCustomerResponse(long agentId, String customerEmail, SurveyResponse surveyResponse, int stage);

	public void updateGatewayAnswer(long agentId, String customerEmail, String mood, String review);

	public void updateFinalScore(long agentId, String customerEmail);

	public double getRatingOfAgentForPastNdays(long agentId, int noOfDays);

	public Map<String, Long> getCountOfCustomersByMood();

	public Map<String, Long> getCountOfCustomersByMoodForAgent(long agentId);

	public Map<String, Long> getCountOfCustomersByMoodForBranch(long branchId);

	public Map<String, Long> getCountOfCustomersByMoodForRegion(long regionId);

	public Map<String, Long> getCountOfCustomersByMoodForCompany(long companyId);

	public Map<String, Long> getCountOfCustomersByReminderMails();

	public Map<String, Long> getCountOfCustomersByReminderMailsForAgent(long agentId);

	public Map<String, Long> getCountOfCustomersByReminderMailsForBranch(long branchId);

	public Map<String, Long> getCountOfCustomersByReminderMailsForRegion(long regionId);

	public Map<String, Long> getCountOfCustomersByReminderMailsForCompany(long companyId);

	public Map<String, Long> getCountOfCustomersByStage();

	public Map<String, Long> getCountOfCustomersByStageForAgent(long agentId);

	public Map<String, Long> getCountOfCustomersByStageForBranch(long branchId);

	public Map<String, Long> getCountOfCustomersByStageForRegion(long regionId);

	public Map<String, Long> getCountOfCustomersByStageForCompany(long companyId);

	public long getTotalSurveyCountByMonth(int year, int month);

	public Map<String, Long> getSocialPostsCount();

	public Map<String, Long> getSocialPostsCountForAgent(long agentId);

	public Map<String, Long> getSocialPostsCountForBranch(long branchId);

	public Map<String, Long> getSocialPostsCountForRegion(long regionId);

	public Map<String, Long> getSocialPostsCountForCompany(long companyId);

	public Map<String, Long> getCountOfSurveyInitiators(String columnName, long columnValue);

}
