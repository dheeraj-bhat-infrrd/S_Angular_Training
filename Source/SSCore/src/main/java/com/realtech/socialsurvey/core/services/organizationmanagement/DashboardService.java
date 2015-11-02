package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


// JIRA SS-137 BY RM05:BOC
/**
 * Interface with methods declared to show dash board of user.
 */
public interface DashboardService
{
	/**
	 * Gets all survey count
	 * @param columnName
	 * @param columnValue
	 * @param numberOfDays
	 * @return
	 * @throws InvalidInputException
	 */
	public long getAllSurveyCount(String columnName, long columnValue, int numberOfDays) throws InvalidInputException;
	
	/**
	 * Gets completed survey count
	 * @param columnName
	 * @param columnValue
	 * @param numberOfDays
	 * @return
	 * @throws InvalidInputException
	 */
	public long getCompleteSurveyCount(String columnName, long columnValue, int numberOfDays) throws InvalidInputException;
	
    public long getClickedSurveyCountForPastNdays( String columnName, long columnValue, int numberOfDays );


    public double getSurveyScore( String columnName, long columnValue, int numberOfDays, boolean realtechAdmin );


    public int getProfileCompletionPercentage( User user, String columnName, long columnValue,
        OrganizationUnitSettings unitSettings );


    public int getBadges( double surveyScore, int surveyCount, int socialPosts, int profileCompleteness );


    public Map<String, Map<Integer, Integer>> getSurveyDetailsForGraph( String columnName, long columnValue, int numberOfDays,
        boolean realtechAdmin ) throws ParseException, InvalidInputException;


    public XSSFWorkbook downloadIncompleteSurveyData( List<SurveyPreInitiation> surveyDetails, String fileLocation )
        throws IOException;


    public XSSFWorkbook downloadSocialMonitorData( List<SocialPost> socialPosts, String fileName );


    public XSSFWorkbook downloadCustomerSurveyResultsData( List<SurveyDetails> surveyDetails, String fileName )
        throws IOException;


    public XSSFWorkbook downloadAgentRankingData( List<AgentRankingReport> agentDetails, String fileLocation )
        throws IOException;


    public long getSocialPostsForPastNdaysWithHierarchy( String coumnName, long columnValue, int numberOfDays );
}
// JIRA SS-137 BY RM05:EOC