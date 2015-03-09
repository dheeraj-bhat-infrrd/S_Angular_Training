package com.realtech.socialsurvey.core.services.organizationmanagement;

// JIRA SS-137 BY RM05:BOC
/**
 * Interface with methods declared to show dash board of user.
 */
public interface DashboardService {

	public long getAllSurveyCountForPastNdays(String columnName, long columnValue, int numberOfDays);

	public long getCompletedSurveyCountForPastNdays(String columnName, long columnValue, int numberOfDays);

	public long getClickedSurveyCountForPastNdays(String columnName, long columnValue, int numberOfDays);

	public long getSocialPostsForPastNdays(String columnName, long columnValue, int numberOfDays);

	public double getSocialScore(String columnName, long columnValue, int numberOfDays);

}
// JIRA SS-137 BY RM05:EOC
