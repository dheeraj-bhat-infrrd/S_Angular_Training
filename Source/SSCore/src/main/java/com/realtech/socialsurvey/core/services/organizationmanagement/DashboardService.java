package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
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

	public int getBadges(double surveyScore, int surveyCount, int socialPosts, int profileCompleteness);

	public XSSFWorkbook downloadCompleteSurveyData(List<SurveyDetails> surveyDetails, String fileLocation) throws IOException;

	public XSSFWorkbook downloadIncompleteSurveyData(List<SurveyPreInitiation> surveyDetails, String fileLocation) throws IOException;

	public Map<String, Map<String, Long>> getSurveyDetailsForGraph(String columnName, long columnValue, String reportType) throws ParseException;

}
// JIRA SS-137 BY RM05:EOC