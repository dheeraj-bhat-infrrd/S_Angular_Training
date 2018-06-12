package com.realtech.socialsurvey.core.services.reportingmanagement;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.BranchRankingReportMonth;
import com.realtech.socialsurvey.core.entities.BranchRankingReportYear;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyDetailsReport;
import com.realtech.socialsurvey.core.entities.DigestRequestData;
import com.realtech.socialsurvey.core.entities.GenericReportingObject;
import com.realtech.socialsurvey.core.entities.Digest;
import com.realtech.socialsurvey.core.entities.MonthlyDigestAggregate;
import com.realtech.socialsurvey.core.entities.NpsReportMonth;
import com.realtech.socialsurvey.core.entities.NpsReportWeek;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.RankingRequirements;
import com.realtech.socialsurvey.core.entities.SurveyResultsReportVO;
import com.realtech.socialsurvey.core.entities.UserRanking;
import com.realtech.socialsurvey.core.entities.ReportingSurveyPreInititation;
import com.realtech.socialsurvey.core.entities.SurveyInvitationEmailCountMonth;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.vo.SurveyTransactionReportVO;
import com.realtech.socialsurvey.core.vo.SurveyInvitationEmailCountVO;


public interface ReportingDashboardManagement
{
    public void createEntryInFileUploadForReporting( int reportId, Date startDate, Date endDate, Long entityId,
        String entityType, Company company, Long adminUserid, int actualTimeZoneOffset, GenericReportingObject genericReportingObject )
        throws InvalidInputException, NoRecordsFetchedException, FileNotFoundException, IOException;


    public List<List<Object>> getSurveyStatsReport( Long entityId, String entityType );


    public List<List<Object>> getRecentActivityList( Long entityId, String entityType, int startIndex, int batchSize )
        throws InvalidInputException;


    public Long getRecentActivityCount( Long entityId, String entityType );


    String generateSurveyStatsForReporting( Long entityId, String entityType, Long userId )
        throws UnsupportedEncodingException, NonFatalException;


    List<List<Object>> getUserAdoptionReport( Long entityId, String entityType );


    String generateUserAdoptionForReporting( Long entityId, String entityType, Long userId )
        throws UnsupportedEncodingException, NonFatalException;


    void deleteRecentActivity( Long fileUploadId );


    List<List<Object>> getCompanyUserReport( Long entityId, String entityType );


    String generateCompanyUserForReporting( Long entityId, String entityType, Long userId )
        throws UnsupportedEncodingException, NonFatalException;


    /**
     * @param entityId
     * @param entityType
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     * @throws UnsupportedEncodingException
     * @throws NonFatalException
     * @throws ParseException
     */
    public String generateSurveyResultsReport( Long entityId, String entityType, Long userId, Timestamp startDate,
        Timestamp endDate ) throws UnsupportedEncodingException, NonFatalException, ParseException;


    List<SurveyTransactionReportVO> getSurveyTransactionReport( Long entityId, String entityType, int month, int year );


    String generateSurveyTransactionForReporting( Long entityId, String entityType, Long userId, Timestamp startDate ) 
    		throws UnsupportedEncodingException, NonFatalException;


    List<List<Object>> getUserRankingThisYear( String entityType, Long entityId, int year, int startIndex, int batchSize );


    List<List<Object>> getUserRankingThisMonth( String entityType, Long entityId, int month, int year, int startIndex,
        int batchSize );


    List<List<Object>> getUserRankingPastMonth( String entityType, Long entityId, int month, int year, int startIndex,
        int batchSize );


    List<List<Object>> getUserRankingPastYear( String entityType, Long entityId, int year, int startIndex, int batchSize );


    Map<String, Object> fetchRankingCountThisYear( long entityId, String entityType, int year, int BatchSize )
        throws NonFatalException;


    Map<String, Object> fetchRankingCountThisMonth( long entityId, String entityType, int year, int month, int BatchSize )
        throws NonFatalException;


    Map<String, Object> fetchRankingCountPastYear( long entityId, String entityType, int year, int BatchSize )
        throws NonFatalException;


    Map<String, Object> fetchRankingCountPastMonth( long entityId, String entityType, int year, int month, int BatchSize )
        throws NonFatalException;


    Map<String, Object> fetchRankingRankCountThisYear( long userId, long entityId, String entityType, int year, int BatchSize )
        throws NonFatalException;


    Map<String, Object> fetchRankingRankCountThisMonth( long userId, long entityId, String entityType, int year, int month,
        int BatchSize ) throws NonFatalException;


    Map<String, Object> fetchRankingRankCountPastYear( long userId, long entityId, String entityType, int year, int BatchSize )
        throws NonFatalException;


    Map<String, Object> fetchRankingRankCountPastMonth( long userId, long entityId, String entityType, int year, int month,
        int BatchSize ) throws NonFatalException;


    Map<String, Object> fetchRankingRankCountPastYears( long userId, long entityId, String entityType, int BatchSize )
        throws NonFatalException;


    Map<String, Object> fetchRankingCountPastYears( long entityId, String entityType, int BatchSize ) throws NonFatalException;


    List<List<Object>> getUserRankingPastYears( String entityType, Long entityId, int startIndex, int batchSize );


    RankingRequirements updateRankingRequirements( int minimumRegistrationDays, float minimumCompletedPercentage,
        int minReviews, double monthOffset, int yearOffset );


    RankingRequirements updateRankingRequirementsMongo( String collection, OrganizationUnitSettings unitSettings,
        RankingRequirements rankingRequirements ) throws InvalidInputException;


    Long getRegionIdFromBranchId( long branchId );


    List<List<Object>> getUserRankingReportForYear( Long entityId, String entityType, int year );


    List<List<Object>> getUserRankingReportForMonth( Long entityId, String entityType, int year, int month );


    String generateUserRankingForReporting( Long entityId, String entityType, Long userId, Timestamp startDate, int type )
        throws UnsupportedEncodingException, NonFatalException;

    public MonthlyDigestAggregate prepareMonthlyDigestMailData( String profileLevel, long entityId, String entityName, int monthUnderConcern, int year ) throws InvalidInputException, NoRecordsFetchedException, UndeliveredEmailException;

    List<List<Object>> getScoreStatsForOverall( Long entityId, String entityType, int currentMonth, int currentYear );


    List<List<Object>> getScoreStatsForQuestion( Long entityId, String entityType, int currentMonth, int currentYear );


    public Map<Integer, Digest> getDigestDataForLastFourMonths( String profileLevel, String entityName, long entityId, int monthUnderConcern, int year )
        throws InvalidInputException, NoRecordsFetchedException;

    public List<UserRanking> getTopTenUserRankingsThisMonthForAHierarchy( String profileLevel, long entityId,
        int monthUnderConcern, int year ) throws InvalidInputException;


    public void startMonthlyDigestProcess();


    public boolean updateSendDigestMailToggle( String entityType, long entityId, boolean sendMonthlyDigestMail ) throws InvalidInputException, NoRecordsFetchedException;


    public List<DigestRequestData> getEntitiesOptedForDigestMail( int startIndex, int batchSize, String profileLevel ) throws InvalidInputException;


    /**
     * method to get maximum question number for company
     * based on time frame
     * @param entityId
     * @param startDate
     * @param endDate
     * @return
     */
    public int getMaxQuestionForSurveyResultsReport( String entityType, Long entityId, Timestamp startDate, Timestamp endDate );


    /**
     * This is the service class method for SurveyResultsReport.
     * This method calls the respective DAO for each report and returns a generic class.
     * @param entityType
     * @param entityId
     * @param startDate
     * @param endDate
     * @param startIndex
     * @param batchSize
     * @return
     */
    public Map<String, SurveyResultsReportVO> getSurveyResultsReport( String entityType, Long entityId, Timestamp startDate,
        Timestamp endDate, int startIndex, int batchSize );

	/**
	 * This method validates the entityType and entityId for social survey admin
	 * and then generates the Company Details Report.
	 * 
	 * @param entityId
	 * @param startIndex
	 * @param batchSize
	 * @return List of CompanyDetailsReport.
	 * @throws InvalidInputException 
	 */
	public List<CompanyDetailsReport> getCompanyDetailsReport(Long entityId, int startIndex, int batchSize) throws InvalidInputException;

	/**
	 * @param profileValue
	 * @param profileLevel
	 * @return
	 */
	public String generateCompanyDetailsReport(long profileValue, String profileLevel)throws UnsupportedEncodingException, NonFatalException;


    public void getCompaniesWithNotransactions();


    public List<String> getTransactionMonitorMailList();


    public void getCompaniesWithHighNotProcessedTransactions();


    public void getCompaniesWithLowVolumeOfTransactions();


    /**
     * 
     * @param entityId
     * @param entityType
     * @param startDate
     * @param endDate
     * @param startIndex
     * @param batchSize
     * @return
     * @throws InvalidInputException
     */
    public List<ReportingSurveyPreInititation> getIncompleteSurvey( long entityId, String entityType, Date startDate, Date endDate,
        int startIndex, int batchSize ) throws InvalidInputException;

    /**
     * Method to generate incomplete survey results report.
     * @param entityId
     * @param entityType
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     * @throws UnsupportedEncodingException
     * @throws NonFatalException
     * @throws ParseException
     */

    public String generateIncompleteSurveyResultsReport( Long entityId, String entityType, Long userId, Timestamp startDate, Timestamp endDate )
        throws UnsupportedEncodingException, NonFatalException, ParseException;

	/**
	 * This method returns the latest record for account statistics report in file upload.
	 * @param reportId
	 * @return
	 */
	public Object getAccountStatisticsRecentActivity(Long reportId);
	
	/**
	 * Method to get nps report for a week
	 * @param companyId
	 * @param week
	 * @param year
	 * @return
	 * @throws InvalidInputException 
	 */
	public List<NpsReportWeek> getNpsReportForAWeek(long companyId, int week, int year) throws InvalidInputException;
	
	/**
	 * Method to get nps report for a month
	 * @param companyId
	 * @param month
	 * @param year
	 * @return
	 * @throws InvalidInputException 
	 */
	public List<NpsReportMonth> getNpsReportForAMonth(long companyId, int month, int year) throws InvalidInputException;
	
	/**
	 * Method to generate nps report for a week or month
	 * @param profileValue
	 * @param profileLevel
	 * @param startDate
	 * @param type
	 * @return
	 * @throws NonFatalException 
	 * @throws UnsupportedEncodingException 
	 */
	public String generateNpsReportForWeekOrMonth(long profileValue, String profileLevel, Timestamp startDate, int type)throws ParseException, UnsupportedEncodingException, NonFatalException;
	
    void updateTransactionMonitorAlertsForCompanies() throws InvalidInputException;

    /**
     * Updated the fileUpload status of given fileUploadId
     * @param filUploadId
     * @param status
     */
    public int updateFileUploadStatus(long filUploadId, int status) throws InvalidInputException;


    /**
     * @param fileUploadId
     * @param status
     * @param location
     * @return
     */
    public int updateFileUploadStatusAndFileName( long fileUploadId, int status, String location ) throws InvalidInputException;


	/**
	 * @param profileValue
	 * @param profileLevel
	 * @param adminUserId
	 * @param startDate
	 * @return
	 * @throws NonFatalException 
	 * @throws UnsupportedEncodingException 
	 */
	public String generateBranchRankingReportMonth(long profileValue, String profileLevel, long adminUserId,
			Timestamp startDate) throws UnsupportedEncodingException, NonFatalException;


	/**
	 * @param profileValue
	 * @param profileLevel
	 * @param adminUserId
	 * @param startDate
	 * @return
	 * @throws InvalidInputException 
	 * @throws NonFatalException 
	 * @throws UnsupportedEncodingException 
	 */
	public String generateBranchRankingReportYear(long profileValue, String profileLevel, long adminUserId,
			Timestamp startDate) throws InvalidInputException, UnsupportedEncodingException, NonFatalException;


	public List<BranchRankingReportMonth> getBranchRankingReportForMonth(long companyId, int month, int year) throws InvalidInputException;


	public List<BranchRankingReportYear> getBranchRankingReportForYear(long companyId, int year) throws InvalidInputException;


	public List<SurveyInvitationEmailCountMonth> getReceivedCountsMonth(long startDateInGmt, long endDateInGmt, int startIndex, int batchSize) throws ParseException;


	public boolean saveEmailCountMonthData(List<SurveyInvitationEmailCountMonth> agentEmailCountsMonth);


	public String generateSurveyInvitationEmailReport(long companyId, String entityType, long adminUserId,
			Timestamp startDate) throws UnsupportedEncodingException, NonFatalException;


	public List<SurveyInvitationEmailCountVO> getSurveyInvitationEmailReportForMonth(long companyId, int month, int year);


	public List<SurveyInvitationEmailCountMonth> getAllTimeDataForSurveyInvitationMail(int startIndex, int batchSize);


	public List<SurveyInvitationEmailCountVO> getDataForSurveyInvitationMail(int month, int year, long companyId);
    
    /**
     * Method to get social monitor flag
     * @param companyId
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    public boolean isSocialMonitorEnabled( long companyId ) throws InvalidInputException, NoRecordsFetchedException;
    
    /**
     * Method to get recent activity list for Social Monitor
     * @param entityId
     * @param entityType
     * @param startIndex
     * @param batchSize
     * @return
     * @throws InvalidInputException
     */
    public List<List<Object>> getRecentActivityListForSocialMonitor( Long entityId, String entityType, int startIndex, int batchSize )
        throws InvalidInputException;
    
    /**
     * Method to get count of recent activity list for Social Monitor
     * @param entityId
     * @param entityType
     * @return
     */
    public Long getRecentActivityCountForSocialMonitor( Long entityId, String entityType );

}
