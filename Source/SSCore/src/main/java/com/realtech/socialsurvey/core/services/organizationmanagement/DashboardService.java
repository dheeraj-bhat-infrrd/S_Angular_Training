package com.realtech.socialsurvey.core.services.organizationmanagement;

import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserSettings;


// JIRA SS-137 BY RM05:BOC
/**
 * Interface with methods declared to show dash board of user.
 */
public interface DashboardService {

	public long getAllSurveyCountForPastNdays(String columnName, long columnValue, int numberOfDays);

	public long getCompletedSurveyCountForPastNdays(String columnName, long columnValue, int numberOfDays);

	public long getClickedSurveyCountForPastNdays(String columnName, long columnValue, int numberOfDays);

	public long getSocialPostsForPastNdays(String columnName, long columnValue, int numberOfDays);

	public double getSurveyScore(String columnName, long columnValue, int numberOfDays);

	public int getProfileCompletionPercentage(User user, String columnName, long columnValue, UserSettings userSettings);
	
	public int getBadges(int surveyScore, int surveyCount, int socialPosts, int profileCompleteness);

}
// JIRA SS-137 BY RM05:EOC
