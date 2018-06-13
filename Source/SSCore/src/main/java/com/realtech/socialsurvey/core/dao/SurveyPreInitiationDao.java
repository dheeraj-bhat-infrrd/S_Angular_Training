package com.realtech.socialsurvey.core.dao;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.integration.EngagementProcessingStatus;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.vo.SurveyPreInitiationList;


public interface SurveyPreInitiationDao extends GenericDao<SurveyPreInitiation, Long>
{
    /**
     * Gets the last run time for the source
     * 
     * @param source
     * @return
     * @throws InvalidInputException
     */
    public Timestamp getLastRunTime( String source ) throws InvalidInputException;


    /**
     * Gets a list of processed ids
     * 
     * @param source
     * @param timestamp
     * @return
     * @throws InvalidInputException
     */
    public List<EngagementProcessingStatus> getProcessedIds( String source, Timestamp timestamp ) throws InvalidInputException;


    public List<SurveyPreInitiation> getIncompleteSurvey( Timestamp startTime, Timestamp endTime, int start, int row,
        Set<Long> agentIds, boolean isCompanyAdmin, long companyId, boolean realtechAdmin ) throws InvalidInputException;


    public List<SurveyPreInitiation> getIncompleteSurveyForReminder( long companyId, int surveyReminderInterval,
        int maxReminders );


    public void getIncompleteSurveysCount( Date startDate, Date endDate, Map<Long, AgentRankingReport> agentReportData );


    public void deleteSurveysWithIds( Set<Long> incompleteSurveyIds );


    /**
     * Gets a count of incomplete sruvey
     * @param companyId
     * @param agentId
     * @param status
     * @param startDate
     * @param endDate
     * @param agentIds
     * @return
     */
    public long getIncompleteSurveyCount( long companyId, long agentId, int[] status, Timestamp startDate, Timestamp endDate,
        Set<Long> agentIds );


    /**
     * Gets a aggregated result of incomplete surveys
     * @param companyId
     * @param agentId
     * @param status
     * @param startDate
     * @param endDate
     * @param agentIds
     * @param aggregateBy
     * @return
     * @throws InvalidInputException
     */
    public Map<Integer, Integer> getIncompletSurveyAggregationCount( long companyId, long agentId, int status,
        Timestamp startDate, Timestamp endDate, Set<Long> agentIds, String aggregateBy ) throws InvalidInputException;


    /**
     * Method to fetch preinitiated surveys by IDs
     * 
     * @param incompleteSurveyIds
     * @return
     */
    List<SurveyPreInitiation> fetchSurveysByIds( Set<Long> incompleteSurveyIds );


    /**
     * Method to delete SurveyPreInitiation records for a specific agent ID
     * @param agentId
     * @throws InvalidInputException
     */
    public void deletePreInitiatedSurveysForAgent( long agentId, int status ) throws InvalidInputException;


    public void updateAgentInfoOfPreInitiatedSurveys( long fromUserId, User toUser ) throws InvalidInputException;


    List<SurveyPreInitiation> getUnmatchedPreInitiatedSurveys( long companyId, int start, int batch );


    List<SurveyPreInitiation> getProcessedPreInitiatedSurveys( long companyId, int start, int batch );


    void updateAgentIdOfPreInitiatedSurveysByAgentEmailAddress( User agent, String agentEmailAddress )
        throws InvalidInputException;


    void updateSurveyPreinitiationRecordsAsIgnored( String agentEmailAddress ) throws InvalidInputException;


    long getUnmatchedPreInitiatedSurveyCount( long companyId );


    long getProcessedPreInitiatedSurveyCount( long companyId );


    Map<Long, Date> getLatestSurveySentForAgent( long companyId );

    /**
     * 
     * @param agentId
     * @param customerEmail
     * @param noOfDays
     * @return
     * @throws DatabaseException
     */
    List<SurveyPreInitiation> getValidSurveyByAgentIdAndCustomeEmailForPastNDays( long agentId, String customerEmail, int noOfDays )
        throws DatabaseException;

    List<SurveyPreInitiation> getCorruptPreInitiatedSurveys( long companyId, int startIndex, int batchSize );


    long getCorruptPreInitiatedSurveyCount( long companyId );


    List<SurveyPreInitiation> getValidSurveyByAgentIdAndCustomeEmail( long agentId, String customerEmail ) throws DatabaseException;


    Map<Long, SurveyPreInitiation> getPreInitiatedSurveyForIds( List<Long> surveyPreinitiationIds );


    List<SurveyPreInitiation> getPreInitiatedSurveyForCompanyByCriteria( int start, int row, List<Long> userIds , Long startSurveyPreinitiationId, Timestamp startEngagementClosedTime ,  long companyId );


    void updateCompanyIdForAllRecordsForAgent( String  agentEmailId, long companyId );


    void disconnectSurveysFromAgent( long agentId );


	public List<Object[]> getReceivedCountForDate(String startDateStr, String endDateStr, int startIndex, int batchSize);


    void updateAgentInfoOfPreInitiatedSurvey( long surveyPreinitiatinId, User toUser ) throws InvalidInputException;
    /**
     * @param companyId
     * @param transactionEmail
     * @param start
     * @param batch
     * @return
     */
    public List<SurveyPreInitiation> getUnmatchedPreInitiatedSurveysForEmail( long companyId, String transactionEmail, int start,
        int batch );


    /**
     * @param companyId
     * @param transactionEmail
     * @return
     */
    public long getUnmatchedPreInitiatedSurveyForEmailCount( long companyId, String transactionEmail );
}