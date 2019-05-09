package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.*;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;


public interface SurveyDetailsDao
{

    public SurveyDetails getSurveyByAgentIdAndCustomerEmail( long agentId, String customerEmail, String firstName,
        String lastName );


    public void insertSurveyDetails( SurveyDetails surveyDetails );


    public void updateEmailForExistingFeedback( long agentId, String customerEmail );


    public void updateCustomerResponse( String surveyId, SurveyResponse surveyResponse, int stage );


    public void updateGatewayAnswer( String surveyId, String mood, String review, boolean isAbusive, String agreedToShare, double score, double npsScore, String profImageUrl  );


    public void updateSurveyAsClicked( String surveyMongoId );


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


    public long get3rdPartyImportCount( String organizationUnitColumn, long organizationUnitColumnValue, Timestamp startDate,
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


    public void updateSurveyAsAbusive( String surveyMongoId, String reporterEmail, String reporterName, String reportReason  );


    public List<AbusiveSurveyReportWrapper> getSurveysReporetedAsAbusive( int start, int rows );


    public void removeExistingZillowSurveysByEntity( String entityType, long entityId, String source );

    
    public long getSurveysReporetedAsAbusiveCount();


    public void updateZillowCallCount();


    public int fetchZillowCallCount();


    public void resetZillowCallCount();


    public void updateSurveyDetails( SurveyDetails surveyDetails );


    public List<SurveyDetails> getSurveyDetailsByAgentAndCompany( long companyId );


    public long getSocialPostsCountBasedOnHierarchy( int numberOfDays, String collectionName, long collectionId,
        boolean fetchAbusive, boolean forStatistics );


    public void updateSurveyAsUnderResolution( String surveyId );
    
    
    public void updateSurveyAsAbusiveNotify( String surveyId );


    public List<AbusiveSurveyReportWrapper> getSurveysReporetedAsAbusive( long companyId, int start, int rows );


    public long getSurveysUnderResolutionCount( long companyId );


    public List<SurveyDetails> getSurveysUnderResolution( long companyId, int start, int rows );


    public long getSurveysReporetedAsAbusiveCount( long companyId );


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


    public SurveyDetails getReviewByQueryMap( Map<String, Object> queries ) throws InvalidInputException;


    public void resetShowSurveyOnUIPropertyForNonLatestReviews( String columnName, long id, List<String> latestSurveyIdList );


    public long getCompletedSurveyCountForStatistics( String organizationUnitColumn, long organizationUnitColumnValue,
        Timestamp startDate, Timestamp endDate, boolean filterAbusive ) throws InvalidInputException;


    public long getZillowImportCount( String organizationUnitColumn, long organizationUnitColumnValue, Timestamp startDate,
        Timestamp endDate, boolean filterAbusive ) throws InvalidInputException;


    public List<SurveyDetails> getFeedbacksForReports( String columnName, long columnValue, int start, int rows,
        double startScore, double limitScore, boolean fetchAbusive, Date startDate, Date endDate, String sortCriteria );


    public void updateZillowSummaryInExistingSurveyDetails( SurveyDetails surveyDetails );


    public Map<Long, Date> getLatestCompletedSurveyDateForAgents( long companyId );


    public SurveyDetails getSurveyByAgentIdAndCustomerEmailAndNoOfDays( long agentId, String customerEmail, String firstName,
        String lastName, int noOfDays );


    public SurveyDetails getSurveyBySurveyPreIntitiationId( long surveyPreIntitiationId );


    public void updateSurveyDetailsBySurveyId( SurveyDetails surveyDetails );


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

    
    public Map<Long, Long> getTotalReviewsCountForAllUsersOfCompany( long companyId );


    public Map<Long, Long> getSocialSurveyReviewsCountForAllUsersOfCompany( long companyId );


    public Map<Long, Long> getZillowReviewsCountForAllUsersOfCompany( long companyId );


    public Map<Long, Long> getAbusiveReviewsCountForAllUsersOfCompany( long companyId );


    public Map<Long, Long> getThirdPartyReviewsCountForAllUsersOfCompany( long companyId );


    public List<SurveyDetails> getAllSurveys( int start, int rows );


    public void updateSurveySourceIdInMongo( SurveyDetails survey );


    public void updateZillowSourceIdInExistingSurveyDetails( SurveyDetails surveyDetails );


    public void updateTransactionDateInExistingSurveyDetails( SurveyDetails surveyDetails );


    public void updateZillowSurveyUpdatedDateInExistingSurveyDetails( SurveyDetails surveyDetails );


    public void updateBranchIdRegionIdForAllSurveysOfAgent( long agentId, long branchId, long regionId );
    
    
    public List<SurveyDetails> getSurveyDetailsForUser( long userId );


    public List<SurveyDetails> getSurveyDetailsForRegionOnly( long regionId );


    public List<SurveyDetails> getSurveyDetailsForBranchOnly( long branchId );


    public void moveSurveysAlongWithUser( long agentId, long branchId, long regionId, long companyId );


    public void updateAgentIdInSurveyDetail( SurveyDetails surveyDetails );


    public void disconnectSurveysFromWithUser( long agentId );


    public void updateRegionIdForAllSurveysOfBranch( long branchId, long regionId );


    public void updateSurveyDetailsForRetake( SurveyDetails surveyDetails );

    
    public Float getFilteredSurveyAvgScore( long companyId, String mood, Long startSurveyID, Date startReviewDate,
        Date startTransactionDate, List<Long> userIds, boolean isRetaken );
    
    
    public List<SurveyDetails> getSurveyDetailsForCompanyAndQuestion(long companyId, String question);

    
    public void updateCustomerResponse(String surveyId, SurveyResponse surveyResponse);


	public void updateSurveyNPSScore(SurveyDetails surveyDetails);


	public void updateSourceDetailInExistingSurveyDetails(SurveyDetails surveyDetails);


    public void updateAgentInfoInSurveyBySPI( long surveyPreInitiationId, User toUser, UserProfile toUserProfile )
        throws InvalidInputException;


    public SurveyDetails getsurveyFromSurveyPreinitiationId( long surveyPreinitiationId );


    public long getSimpleFeedBacksCount( String columnName, long columnValue, double startScore, double limitScore,
        boolean fetchAbusive );


    public List<String> getDistinctValues( String queryKey, Object value, String field );

    
    /**
     * Method to update the existing reviews
     * @param existingReviews
     */
    public int bulkUpdateReviews( List<SurveyDetails> existingReviews );

    
    public SurveyDetails fetchSurveyWithConditions( Map<String, Object> queryMap );


    /**
     * Insert to survey details collection in bulk.
     * @param surveyDetails
     */
    public void insertSurveyDetails( List<SurveyDetails> surveyDetails );

	/**
	 * @param surveyId
	 * @return
	 */
	public List<SurveyResponse> getSurveyRatingResponse(String surveyId);


	public List<SurveyDetails> getSurveyBySourceSourceIdAndMongoCollection( long iden, String collectionName );


	public List<SurveyDetails> fetchSurveyForParticularHierarchyAndSource( long entityId, String entityType, String source );
	
	
	public int updateSurveyDetailsFields( long surveyPreIntitiationId, int participantType, Date surveySentDate);

	
	public Map<String, Long> getSurveyCountForGatewayResponses(String entityType, long entityId);


	public double getAvgScoreForEntity(String entityType, long entityId);


	public SurveyDetails getLatestCompletedSurveyForEntity(String entityType, long entityId);
	
	
	public void upsertReviewReply( ReviewReply reviewReply, String surveyId );
	
	
	public void deleteReviewReply( String replyId, String surveyId);

	
	public ReviewReply getReviewReply(String replyId, String surveyId);
	/**
	 * @param surveyId
	 * @return
	 */
	public String getProfileImageUrl(String surveyId);

}