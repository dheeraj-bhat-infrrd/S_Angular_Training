package com.realtech.socialsurvey.core.services.batchtracker.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.entities.BatchTracker;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;


@Component
public class BatchTrackerServiceImpl implements BatchTrackerService
{
    public static final Logger LOG = LoggerFactory.getLogger( BatchTrackerServiceImpl.class );

    @Autowired
    private GenericDao<BatchTracker, Long> batchTrackerDao;

    @Autowired
    private SurveyDetailsDao surveyDetailsDao;

    @Autowired
    private SolrSearchService solrSearchService;
    
    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private EmailServices emailServices;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String applicationAdminEmail;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String applicationAdminName;


    /*
     * (non-Javadoc)
     * @see com.realtech.socialsurvey.core.services.batchTracker.BatchTrackerService#getLastRunTimeByBatchType(java.lang.String)
     */
    @Override
    @Transactional
    public long getLastRunEndTimeAndUpdateLastStartTimeByBatchType( String batchType  , String batchName)
    {
        LOG.debug( "method getLastRunEndTimeAndUpdateLastStartTimeByBatchType() started for batch type : " + batchType );
        List<BatchTracker> batchTrackerList = batchTrackerDao.findByColumn( BatchTracker.class,
            CommonConstants.BATCH_TYPE_COLUMN, batchType );

        BatchTracker batchTracker;
        long lastEndTime;
        long currentTime = System.currentTimeMillis();
        if ( batchTrackerList == null || batchTrackerList.isEmpty() ) {
            LOG.debug( "No record found in batch tracker for batch type : " + batchType );
            //create new batch tracker for the record
            batchTracker = new BatchTracker();
            batchTracker.setBatchType( batchType );
            batchTracker.setBatchName( batchName );
            batchTracker.setLastEndTime( new Timestamp( CommonConstants.EPOCH_TIME_IN_MILLIS ) );
            batchTracker.setCreatedOn( new Timestamp( currentTime ) );
            lastEndTime = CommonConstants.EPOCH_TIME_IN_MILLIS;
        } else {
            batchTracker = batchTrackerList.get( CommonConstants.INITIAL_INDEX );
            lastEndTime = batchTracker.getLastEndTime().getTime();
        }


        batchTracker.setLastStartTime( new Timestamp( currentTime ) );
        batchTracker.setModifiedOn( new Timestamp( currentTime ) );
        batchTracker.setModifiedBy( "ADMIN" );
        batchTrackerDao.saveOrUpdate( batchTracker );

        LOG.debug( "method getLastRunEndTimeAndUpdateLastStartTimeByBatchType() ended for batch type : " + batchType );
        return lastEndTime;

    }


    @Override
    @Transactional
    public long getLastRunEndTimeByBatchType( String batchType ) throws NoRecordsFetchedException, InvalidInputException
    {
        LOG.debug( "method getLastRunEndTimeByBatchType() started for batch type : " + batchType );
        if ( batchType == null || batchType.isEmpty() ) {
            throw new InvalidInputException( "passed parameter batchtype is incorrect" );
        }
        List<BatchTracker> batchTrackerList = batchTrackerDao.findByColumn( BatchTracker.class,
            CommonConstants.BATCH_TYPE_COLUMN, batchType );

        if ( batchTrackerList == null || batchTrackerList.isEmpty() ) {
            throw new NoRecordsFetchedException( "No record Fatched For batch type : " + batchType );
        }
        BatchTracker batchTracker = batchTrackerList.get( CommonConstants.INITIAL_INDEX );
        long lastEndTime = batchTracker.getLastEndTime().getTime();

        LOG.debug( "method getLastRunEndTimeByBatchType() ended for batch type : " + batchType );
        return lastEndTime;

    }


    @Override
    @Transactional
    public void updateErrorForBatchTrackerByBatchType( String batchType, String error ) throws NoRecordsFetchedException,
        InvalidInputException
    {
        LOG.debug( "method updateModifiedOnColumnByBatchType() started for batch type : " + batchType );
        if ( batchType == null || batchType.isEmpty() ) {
            throw new InvalidInputException( "passed parameter batchtype is incorrect" );
        }

        List<BatchTracker> batchTrackerList = batchTrackerDao.findByColumn( BatchTracker.class,
            CommonConstants.BATCH_TYPE_COLUMN, batchType );
        if ( batchTrackerList == null || batchTrackerList.size() <= 0
            || batchTrackerList.get( CommonConstants.INITIAL_INDEX ) == null ) {
            throw new NoRecordsFetchedException( "No record Fatched For batch type : " + batchType );
        }

        BatchTracker batchTracker = batchTrackerList.get( CommonConstants.INITIAL_INDEX );
        batchTracker.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        batchTracker.setError( "Error : " + error );
        batchTrackerDao.update( batchTracker );

        LOG.debug( "method updateModifiedOnColumnByBatchType() ended for batch type : " + batchType );

    }


    @Override
    @Transactional
    public void updateLastRunEndTimeByBatchType( String batchType ) throws NoRecordsFetchedException, InvalidInputException
    {
        LOG.debug( "method updateModifiedOnColumnByBatchType() started for batch type : " + batchType );
        if ( batchType == null || batchType.isEmpty() ) {
            throw new InvalidInputException( "passed parameter batchtype is incorrect" );
        }

        List<BatchTracker> batchTrackerList = batchTrackerDao.findByColumn( BatchTracker.class,
            CommonConstants.BATCH_TYPE_COLUMN, batchType );
        if ( batchTrackerList == null || batchTrackerList.size() <= 0
            || batchTrackerList.get( CommonConstants.INITIAL_INDEX ) == null ) {
            throw new NoRecordsFetchedException( "No record Fatched For batch type : " + batchType );
        }

        BatchTracker batchTracker = batchTrackerList.get( CommonConstants.INITIAL_INDEX );
        batchTracker.setLastEndTime( new Timestamp( System.currentTimeMillis() ) );
        batchTracker.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        batchTracker.setError( null );
        batchTrackerDao.update( batchTracker );

        LOG.debug( "method updateModifiedOnColumnByBatchType() ended for batch type : " + batchType );
    }


    @Override
    @Transactional
    public void sendMailToAdminRegardingBatchError( String  batchName, long lastRunTime ,Exception exception )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "method sendMailToAdminREgardingBatchError started() for batch :  " + batchName );
        if(exception == null){
            throw new InvalidInputException("Passed parameter exception is null");
        }
        String lastRunTimeStr = new Timestamp( lastRunTime ).toString();
        String stackTrace = ExceptionUtils.getStackTrace( exception );
        if(stackTrace != null)
            stackTrace.replaceAll( "\n", "<br>" );
        String errMsg = exception.getMessage();
        if(errMsg == null || errMsg.isEmpty()){
            errMsg = "No error Message Found";
        }
        emailServices.sendReportBugMailToAdminForExceptionInBatch( applicationAdminName, batchName, lastRunTimeStr, errMsg,
            stackTrace, applicationAdminEmail );
        LOG.debug( "method sendMailToAdminREgardingBatchError ended()" );
    }


    @Override
    @Transactional
    public List<Long> getUserIdListToBeUpdated( long modifiedOn )
    {
        LOG.debug( "method getReviewCountForAgentsByModifiedOn() started " );
        List<Long> agentsIdList = surveyDetailsDao.getEntityIdListForModifiedReview( CommonConstants.AGENT_ID_COLUMN,
            modifiedOn );
        LOG.debug( "method getReviewCountForAgentsByModifiedOn() ended " );
        return agentsIdList;
    }


    /*
     * (non-Javadoc)
     * @see com.realtech.socialsurvey.core.services.batchTracker.BatchTrackerService#getReviewCountForAgentsByModifiedOn(long)
     */
    @Override
    @Transactional
    public Map<Long, Integer> getReviewCountForAgents( List<Long> agentIdList ) throws ParseException, InvalidInputException
    {
        LOG.debug( "method getReviewCountForAgents() started " );
        //Get review count from mongodb
        Map<Long, Integer> agentsReviewCount = surveyDetailsDao.getSurveyCountForAgents( agentIdList, false );
        boolean areZillowReviewsPresent = true;
        Map<Long, Integer> agentsReviewCountFromZillow = null;
        //Get zillow review count 
        try {
            agentsReviewCountFromZillow = userManagementService.getUserIdReviewCountMapFromUserIdList( agentIdList );
        } catch ( InvalidInputException e ) {
            LOG.info( "None of the users have zillow reviews" );
            areZillowReviewsPresent = false;
        }

        //Add zillow review count to agentsReviewCount if present
        if ( areZillowReviewsPresent ) {
            for ( Long agentConnectedToZillow : agentsReviewCountFromZillow.keySet() ) {
                int currentCount = agentsReviewCount.get( agentConnectedToZillow );
                int zillowCount = agentsReviewCountFromZillow.get( agentConnectedToZillow );
                int totalReviewCount = currentCount + zillowCount;
                agentsReviewCount.put( agentConnectedToZillow, totalReviewCount );
            }
        }
        LOG.debug( "method getReviewCountForAgents() ended " );
        return agentsReviewCount;
    }

}
