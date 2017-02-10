package com.realtech.socialsurvey.core.services.organizationmanagement;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;


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
    public long getAllSurveyCount( String columnName, long columnValue, int numberOfDays ) throws InvalidInputException;


    /**
     * Gets completed survey count
     * @param columnName
     * @param columnValue
     * @param numberOfDays
     * @return
     * @throws InvalidInputException
     */
    public long getCompleteSurveyCount( String columnName, long columnValue, int numberOfDays ) throws InvalidInputException;


    public long getClickedSurveyCountForPastNdays( String columnName, long columnValue, int numberOfDays )
        throws InvalidInputException;


    public double getSurveyScore( String columnName, long columnValue, int numberOfDays, boolean realtechAdmin )
        throws InvalidInputException;


    public int getProfileCompletionPercentage( User user, String columnName, long columnValue,
        OrganizationUnitSettings unitSettings ) throws InvalidInputException;


    public int getBadges( double surveyScore, int surveyCount, int socialPosts, int profileCompleteness )
        throws InvalidInputException;


    public Map<String, Map<Integer, Integer>> getSurveyDetailsForGraph( String columnName, long columnValue, int numberOfDays,
        boolean realtechAdmin ) throws ParseException, InvalidInputException;


    public XSSFWorkbook downloadIncompleteSurveyData( List<SurveyPreInitiation> surveyDetails, String fileLocation )
        throws IOException, InvalidInputException;


    public XSSFWorkbook downloadSocialMonitorData( List<SocialPost> socialPosts, String fileName ) throws InvalidInputException;


    public XSSFWorkbook downloadCustomerSurveyResultsData( List<SurveyDetails> surveyDetails, String fileName,
        String profileLevel, long companyId ) throws IOException, InvalidInputException;


    public XSSFWorkbook downloadAgentRankingData( List<AgentRankingReport> agentDetails, String fileLocation )
        throws IOException, InvalidInputException;


    public long getSocialPostsForPastNdaysWithHierarchy( String coumnName, long columnValue, int numberOfDays )
        throws InvalidInputException;


    public XSSFWorkbook downloadUserAdoptionReportData( long companyId )
        throws InvalidInputException, NoRecordsFetchedException;


    public List<FileUpload> getReportsToBeSent() throws NoRecordsFetchedException;


    /**
     * Method to delete surveys from mongo given the survey preinitiation details
     * @param surveys
     */
    void deleteSurveyDetailsByPreInitiation( List<SurveyPreInitiation> surveys );


    public XSSFWorkbook downloadCompanyHierarchyReportData( long companyId )
        throws InvalidInputException, NoRecordsFetchedException;


    public long getZillowImportCount( String columnName, long columnValue, int numberOfDays ) throws InvalidInputException;


    public long getAllSurveyCountForStatistics( String columnName, long columnValue, int numberOfDays )
        throws InvalidInputException;


    public long getSocialPostsForPastNdaysWithHierarchyForStatistics( String columnName, long columnValue, int numberOfDays )
        throws InvalidInputException;


    public long get3rdPartyImportCount( String columnName, long columnValue, int numberOfDays ) throws InvalidInputException;


    /**
     * 
     * @param companyId
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     * @throws ProfileNotFoundException
     */
    Map<Integer, List<Object>> downloadCompanyUsersReportData( long companyId )
        throws InvalidInputException, NoRecordsFetchedException, ProfileNotFoundException;


    void generateCompanyReportAndMail( Map<Integer, List<Object>> usersData, String recipientMailId, String recipientName,
        Company company ) throws InvalidInputException, UndeliveredEmailException;


    List<FileUpload> getActiveBillingReports() throws NoRecordsFetchedException;


    /**
     * Method to generate and mail the company hierarchy report
     * @param companyId
     * @param recipientMailId
     * @param recipientName
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     * @throws UndeliveredEmailException
     */
    public void generateCompanyHierarchyReportAndMail( long companyId, String recipientMailId, String recipientName )
        throws InvalidInputException, NoRecordsFetchedException, UndeliveredEmailException;


    public void generateCompanyRegistrationReportAndMail( Timestamp startDate, Timestamp endDate, String recipientMailId,
        String recipientName ) throws InvalidInputException, UndeliveredEmailException;
}
// JIRA SS-137 BY RM05:EOC