package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.AbusiveSurveyReportWrapper;
import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.ApiRequestDetails;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public interface SurveyDetailsDao
{

    public SurveyDetails getSurveyByAgentIdAndCustomerEmail( long agentId, String customerEmail, String firstName,
        String lastName );


    public void insertSurveyDetails( SurveyDetails surveyDetails );


    public void updateEmailForExistingFeedback( long agentId, String customerEmail );


    public void updateCustomerResponse( String surveyId, SurveyResponse surveyResponse, int stage );


    public void updateGatewayAnswer( String surveyId, String mood, String review, boolean isAbusive, String agreedToShare, double score, double npsScore  );


//    public double updateFinalScore( String surveyId );


    public void updateSurveyAsClicked( String surveyMongoId );


    public Map<String, Long> getCountOfCustomersByMood( String columnName, long columnValue );


    public Map<String, Long> getCountOfCustomersByReminderMails( String columnName, long columnValue );


    public Map<String, Long> getCountOfCustomersByStage( String columnName, long columnValue );


    public long getTotalSurveyCountByMonth( int year, int month );


    public long getSocialPostsCount( String columnName, long columnValue, int numberOfDays );


    public Map<String, Long> getCountOfSurveyInitiators( String columnName, long columnValue );


    public double getRatingForPastNdays( String columnName, long columnValue, int noOfDays, boolean aggregateAbusive,
        boolean realtechAdmin, boolean includeZillow, long zillowReviewCount, double zillowTotalReviewScore );


    public long getIncompleteSurveyCount( String columnName, long columnValue, int noOfDays );


    public long getCompletedSurveyCount( String columnName, long columnValue, int noOfDays );


    public long getClickedSurveyCount( String columnName, long columnValue, int noOfDays, boolean filterAbusive );


    public List<SurveyDetails> getFeedbacks( String columnName, long columNValue, int start, int rows, double startScore,
        double limitScore, boolean fetchAbusive, Date startDate, Date endDate, String sortCriteria, List<String> surveySources, String order );


    public long getFeedBacksCount( String columnName, long columnValue, double startScore, double limitScore,
        boolean fetchAbusive, boolean notRecommended, boolean includeZillow, long zillowReviewCount );


    public List<SurveyDetails> getIncompleteSurvey( String columnName, long columNValue, int start, int rows, double startScore,
        double limitScore, Date startDate, Date endDate );


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
    public long getCompletedSurveyCount( String organizationUnitColumn, long organizationUnitColumnValue, Timestamp startDate,
        Timestamp endDate, boolean filterAbusive, boolean filterZillowReviews ) throws InvalidInputException;


    long get3rdPartyImportCount( String organizationUnitColumn, long organizationUnitColumnValue, Timestamp startDate,
        Timestamp endDate, boolean filterAbusive ) throws InvalidInputException;


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
    public Map<Integer, Integer> getCompletedSurveyAggregationCount( String organizationUnitColumn,
        long organizationUnitColumnValue, Timestamp startDate, Timestamp endDate, String aggregateBy )
        throws InvalidInputException;


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
    public Map<Integer, Integer> getClickedSurveyAggregationCount( String organizationUnitColumn,
        long organizationUnitColumnValue, Timestamp startDate, Timestamp endDate, String aggregateBy )
        throws InvalidInputException;


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
    public Map<Integer, Integer> getSocialPostsAggregationCount( String organizationUnitColumn,
        long organizationUnitColumnValue, Timestamp startDate, Timestamp endDate, String aggregateBy )
        throws InvalidInputException;


    public List<SurveyDetails> getIncompleteSurveyCustomers( long companyId, int surveyReminderInterval, int maxReminders );


    public void updateReminderCount( List<Long> agentId, List<String> customers );


    public List<SurveyDetails> getIncompleteSocialPostCustomersEmail( long companyId, int surveyReminderInterval,
        int maxReminders );


    public void changeStatusOfSurvey( String surveyId, boolean editable );


    public void updateReminderCountForSocialPost( Long agentId, String customerEmail );


    public void getAverageScore( Date startDate, Date endDate, Map<Long, AgentRankingReport> agentReportData, String columnName,
        long columnValue, boolean fetchAbusive );


    public void getCompletedSurveysCount( Date startDate, Date endDate, Map<Long, AgentRankingReport> agentReportData,
        String colunmName, long columnValue, boolean fetchAbusive );


    public SurveyDetails getSurveyBySourceSourceIdAndMongoCollection( String surveySourceId, long iden, String collectionName );


    void updateSurveyAsAbusive( String surveyMongoId, String reporterEmail, String reporterName, String reportReason  );


    public List<AbusiveSurveyReportWrapper> getSurveysReporetedAsAbusive( int start, int rows );


    public long getSurveysReporetedAsAbusiveCount();


    void updateZillowCallCount();


    int fetchZillowCallCount();


    void resetZillowCallCount();


    public void updateSurveyDetails( SurveyDetails surveyDetails );


    public List<SurveyDetails> getSurveyDetailsByAgentAndCompany( long companyId );


    public long getSocialPostsCountBasedOnHierarchy( int numberOfDays, String collectionName, long collectionId,
        boolean fetchAbusive, boolean forStatistics );


    public void updateSurveyAsUnderResolution( String surveyId );
    
    public void updateSurveyAsAbusiveNotify( String surveyId );


    public List<AbusiveSurveyReportWrapper> getSurveysReporetedAsAbusive( long companyId, int start, int rows );


    public long getSurveysUnderResolutionCount( long companyId );


    public List<SurveyDetails> getSurveysUnderResolution( long companyId, int start, int rows );


    long getSurveysReporetedAsAbusiveCount( long companyId );


    public Map<Long, Integer> getSurveyCountForAgents( List<Long> agentIdList, boolean fetchAbusive ) throws ParseException;


    public List<Long> getEntityIdListForModifiedReview( String columnName, long modifiedAfter );


    public void updateSurveyAsUnAbusive( String surveyMongoId );


    public SurveyDetails getSurveyBySurveyMongoId( String surveyMongoId );


    /**
     * Method to remove surveys from mongo by SurveyPreInitiation
     * @param surveys
     */
    public void deleteSurveysBySurveyPreInitiation( List<SurveyPreInitiation> surveys );


    public void deleteIncompleteSurveysForAgent( long agentId ) throws InvalidInputException;


    public void updateAgentInfoInSurveys( long fromUserId, User toUser, UserProfile toUserProfile )
        throws InvalidInputException;


    public void removeExistingZillowSurveysByEntity( String entityType, long entityId );


    public SurveyDetails getZillowReviewByQueryMap( Map<String, Object> queries ) throws InvalidInputException;


    public void resetShowSurveyOnUIPropertyForNonLatestReviews( String columnName, long id, List<String> latestSurveyIdList );


    public long getCompletedSurveyCountForStatistics( String organizationUnitColumn, long organizationUnitColumnValue,
        Timestamp startDate, Timestamp endDate, boolean filterAbusive ) throws InvalidInputException;


    public long getZillowImportCount( String organizationUnitColumn, long organizationUnitColumnValue, Timestamp startDate,
        Timestamp endDate, boolean filterAbusive ) throws InvalidInputException;


    public List<SurveyDetails> getFeedbacksForReports( String columnName, long columnValue, int start, int rows,
        double startScore, double limitScore, boolean fetchAbusive, Date startDate, Date endDate, String sortCriteria );


    public void updateZillowSummaryInExistingSurveyDetails( SurveyDetails surveyDetails );


    Map<Long, Date> getLatestCompletedSurveyDateForAgents( long companyId );


    SurveyDetails getSurveyByAgentIdAndCustomerEmailAndNoOfDays( long agentId, String customerEmail, String firstName,
        String lastName, int noOfDays );


    SurveyDetails getSurveyBySurveyPreIntitiationId( long surveyPreIntitiationId );


    void updateSurveyDetailsBySurveyId( SurveyDetails surveyDetails );


    public void updateModifiedDateForSurvey( String surveyId, Date date );


    /**
     * 
     * @param start
     * @param batchSize
     * @param stage
     * @return
     */
    public List<SurveyDetails> getFilteredSurveys( int start, int batchSize, long companyId , String mood , Long startSurveyID , Date startReviewDate , Date startTransactionDate , List<Long> userIds,  boolean isRetaken );

    /**
     * 
     * @param companyId
     * @param status
     * @return
     */
    public Long getFilteredSurveyCount( long companyId , String mood ,  Long startSurveyID, Date startReviewDate , Date startTransactionDate , List<Long> userIds, boolean isRetaken );


    void insertApiRequestDetails( ApiRequestDetails apiRequestDetails );


    public Map<Long, Long> getTotalReviewsCountForAllUsersOfCompany( long companyId );


    public Map<Long, Long> getSocialSurveyReviewsCountForAllUsersOfCompany( long companyId );


    public Map<Long, Long> getZillowReviewsCountForAllUsersOfCompany( long companyId );


    public Map<Long, Long> getAbusiveReviewsCountForAllUsersOfCompany( long companyId );


    public Map<Long, Long> getThirdPartyReviewsCountForAllUsersOfCompany( long companyId );


    public List<SurveyDetails> getAllSurveys( int start, int rows );


    public void updateSurveySourceIdInMongo( SurveyDetails survey );


    void updateZillowSourceIdInExistingSurveyDetails( SurveyDetails surveyDetails );


    void updateTransactionDateInExistingSurveyDetails( SurveyDetails surveyDetails );


    void updateZillowSurveyUpdatedDateInExistingSurveyDetails( SurveyDetails surveyDetails );


    void updateBranchIdRegionIdForAllSurveysOfAgent( long agentId, long branchId, long regionId );
    
    
    public List<SurveyDetails> getSurveyDetailsForUser( long userId );


    public List<SurveyDetails> getSurveyDetailsForRegionOnly( long regionId );


    public List<SurveyDetails> getSurveyDetailsForBranchOnly( long branchId );


    void moveSurveysAlongWithUser( long agentId, long branchId, long regionId, long companyId );


    void updateAgentIdInSurveyDetail( SurveyDetails surveyDetails );


    void disconnectSurveysFromWithUser( long agentId );


    void updateRegionIdForAllSurveysOfBranch( long branchId, long regionId );


    void updateSurveyDetailsForRetake( SurveyDetails surveyDetails );

    public Float getFilteredSurveyAvgScore( long companyId, String mood, Long startSurveyID, Date startReviewDate,
        Date startTransactionDate, List<Long> userIds, boolean isRetaken );
    
    public List<SurveyDetails> getSurveyDetailsForCompanyAndQuestion(long companyId, String question);

	void updateCustomerResponse(String surveyId, SurveyResponse surveyResponse);


	public void updateSurveyNPSScore(SurveyDetails surveyDetails);


	void updateSourceDetailInExistingSurveyDetails(SurveyDetails surveyDetails);


    public void updateAgentInfoInSurveyBySPI( long surveyPreInitiationId, User toUser, UserProfile toUserProfile )
        throws InvalidInputException;


    public SurveyDetails getsurveyFromSurveyPreinitiationId( long surveyPreinitiationId );


    public long getSimpleFeedBacksCount( String columnName, long columnValue, double startScore, double limitScore,
        boolean fetchAbusive );


    public List<String> getDistinctValues( String queryKey, Object value, String field );


	/**
	 * @param surveyId
	 * @return
	 */
	public List<SurveyResponse> getSurveyRatingResponse(String surveyId);

}