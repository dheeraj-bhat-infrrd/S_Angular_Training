package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.realtech.socialsurvey.core.entities.AbusiveSurveyReportWrapper;
import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public interface SurveyDetailsDao
{

    public SurveyDetails getSurveyByAgentIdAndCustomerEmail( long agentId, String customerEmail, String firstName,
        String lastName );


    public void insertSurveyDetails( SurveyDetails surveyDetails );


    public void updateEmailForExistingFeedback( long agentId, String customerEmail );


    public void updateCustomerResponse( long agentId, String customerEmail, SurveyResponse surveyResponse, int stage );


    public void updateGatewayAnswer( long agentId, String customerEmail, String mood, String review, boolean isAbusive,
        String agreedToShare );


    public void updateFinalScore( long agentId, String customerEmail );


    public void updateSurveyAsClicked( long agentId, String customerEmail );


    public Map<String, Long> getCountOfCustomersByMood( String columnName, long columnValue );


    public Map<String, Long> getCountOfCustomersByReminderMails( String columnName, long columnValue );


    public Map<String, Long> getCountOfCustomersByStage( String columnName, long columnValue );


    public long getTotalSurveyCountByMonth( int year, int month );


    public long getSocialPostsCount( String columnName, long columnValue, int numberOfDays );


    public Map<String, Long> getCountOfSurveyInitiators( String columnName, long columnValue );


    public double getRatingForPastNdays( String columnName, long columnValue, int noOfDays, boolean aggregateAbusive,
        boolean realtechAdmin );


    public long getIncompleteSurveyCount( String columnName, long columnValue, int noOfDays );


    public long getCompletedSurveyCount( String columnName, long columnValue, int noOfDays );


    public long getClickedSurveyCount( String columnName, long columnValue, int noOfDays );


    public List<SurveyDetails> getFeedbacks( String columnName, long columNValue, int start, int rows, double startScore,
        double limitScore, boolean fetchAbusive, Date startDate, Date endDate, String sortCriteria );


    public long getFeedBacksCount( String columnName, long columnValue, double startScore, double limitScore,
        boolean fetchAbusive, boolean notRecommended );


    public List<SurveyDetails> getIncompleteSurvey( String columnName, long columNValue, int start, int rows,
        double startScore, double limitScore, Date startDate, Date endDate );


    public void updateReminderCount( long agentId, String customerEmail );

    /**
     * Get count for completed survey
     * @param organizationUnitColumn
     * @param organizationUnitColumnValue
     * @param startDate
     * @param endDate
     * @param filterAbusive
     * @return
     * @throws InvalidInputException
     */
    public long getCompletedSurveyCount(String organizationUnitColumn, long organizationUnitColumnValue, Timestamp startDate, Timestamp endDate, boolean filterAbusive) throws InvalidInputException;
    /**
     * Gets an aggregated count for completed survey
     * @param organizationUnitColumn
     * @param organizationUnitColumnValue
     * @param startDate
     * @param endDate
     * @param aggregateBy
     * @return
     * @throws InvalidInputException
     */
    public Map<Integer, Integer> getCompletedSurveyAggregationCount(String organizationUnitColumn, long organizationUnitColumnValue, Timestamp startDate, Timestamp endDate, String aggregateBy) throws InvalidInputException;
    
    /**
     * Get the aggregation of clicked surveys
     * @param organizationUnitColumn
     * @param organizationUnitColumnValue
     * @param startDate
     * @param endDate
     * @param aggregateBy
     * @return
     * @throws InvalidInputException
     */
    public Map<Integer, Integer> getClickedSurveyAggregationCount(String organizationUnitColumn, long organizationUnitColumnValue, Timestamp startDate, Timestamp endDate, String aggregateBy) throws InvalidInputException;
    
    /**
     * Get social posts aggregation
     * @param organizationUnitColumn
     * @param organizationUnitColumnValue
     * @param startDate
     * @param endDate
     * @param aggregateBy
     * @return
     * @throws InvalidInputException
     */
    public Map<Integer, Integer> getSocialPostsAggregationCount(String organizationUnitColumn, long organizationUnitColumnValue, Timestamp startDate, Timestamp endDate, String aggregateBy) throws InvalidInputException;
    
    public List<SurveyDetails> getIncompleteSurveyCustomers( long companyId, int surveyReminderInterval, int maxReminders );


    public void updateReminderCount( List<Long> agentId, List<String> customers );


    public List<SurveyDetails> getIncompleteSocialPostCustomersEmail( long companyId, int surveyReminderInterval,
        int maxReminders, float autopostScore );


    public void updateSharedOn( String socialSite, long agentId, String customerEmail );


    public void changeStatusOfSurvey( long agentId, String customerEmail, String firstName, String lastName, boolean editable );


    public void updateReminderCountForSocialPost( Long agentId, String customerEmail );


    public void getAverageScore( Date startDate, Date endDate, Map<Long, AgentRankingReport> agentReportData,
        String columnName, long columnValue, boolean fetchAbusive );


    public void getCompletedSurveysCount( Date startDate, Date endDate, Map<Long, AgentRankingReport> agentReportData,
        String colunmName, long columnValue, boolean fetchAbusive );


    public long noOfPreInitiatedSurveys( String columnName, long columnValue, Date startDate, Date endDate );


    public SurveyDetails getSurveyBySourceSourceIdAndMongoCollection( String surveySourceId, long iden, String collectionName );


    void updateSurveyAsAbusive( String surveyMongoId, String reporterEmail, String reporterName );


    void removeZillowSurveysByEntity( String entityType, long entityId );


    void removeExcessZillowSurveysByEntity( String entityType, long entityId );


    public List<AbusiveSurveyReportWrapper> getSurveysReporetedAsAbusive( int start, int rows );


    public long getSurveysReporetedAsAbusiveCount();


    void updateZillowCallCount();


    int fetchZillowCallCount();


    void resetZillowCallCount();


    public void updateSurveyDetails( SurveyDetails surveyDetails );


    public List<SurveyDetails> getSurveyDetailsByAgentAndCompany( long companyId );


    public long getSocialPostsCountBasedOnHierarchy( int numberOfDays, String collectionName, long collectionId );


    public void updateSurveyAsUnderResolution( String surveyId );


    public List<AbusiveSurveyReportWrapper> getSurveysReporetedAsAbusive( long companyId, int start, int rows );


    public long getSurveysUnderResolutionCount( long companyId );


    public List<SurveyDetails> getSurveysUnderResolution( long companyId, int start, int rows );


    long getSurveysReporetedAsAbusiveCount( long companyId );


    public Map<Long, Integer> getSurveyCountForAgents( List<Long> agentIdList, boolean fetchAbusive ) throws ParseException;


    public List<Long> getEntityIdListForModifiedReview( String columnName, long modifiedAfter );
}