package com.realtech.socialsurvey.core.starter;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchTracker.BatchTrackerService;


/**
 * 
 * @author rohit
 *
 */


public class SolrReviewCountUpdater extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( SolrReviewCountUpdater.class );

    private BatchTrackerService batchTrackerService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "executing SolrReviewCountUpdater" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        try {
            //getting last run time of batch
            long lastRunTime = batchTrackerService.getLastRunTimeByBatchType( CommonConstants.BATCH_TYPE_REVIEW_COUNT_UPDATER );
            //get user id list for them review count will be updated
            List<Long> userIdList = batchTrackerService.getUserIdListToBeUpdated( lastRunTime );
            //getting no of reviews for the agents 
            Map<Long, Integer> agentsReviewCount = batchTrackerService.getReviewCountForAgents( userIdList );

            batchTrackerService.updateReviewCountForAgentsInSolr( agentsReviewCount );

            //updating last run time for batch in database
            batchTrackerService.updateModifiedOnColumnByBatchType( CommonConstants.BATCH_TYPE_REVIEW_COUNT_UPDATER );
        }catch ( InvalidInputException e ) {
            LOG.error( "Invalid input exception caught" , e );
        } catch ( NoRecordsFetchedException e ) {
            LOG.error( "No entry found for batch tracker in database" , e );
        } catch ( ParseException e ) {
            LOG.error( "Error while parsing the data fetched from mongo for survey count" , e );
        } 

    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );

    }

}
