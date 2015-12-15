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
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;


/**
 * 
 * @author rohit
 *
 */


public class SolrReviewCountUpdater extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( SolrReviewCountUpdater.class );

    private BatchTrackerService batchTrackerService;

    private SolrSearchService solrSearchService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "executing SolrReviewCountUpdater" );
        try {
            initializeDependencies( jobExecutionContext.getMergedJobDataMap() );

            //getting last run end time of batch and update last start time
            long lastRunEndTime = batchTrackerService
                .getLastRunEndTimeAndUpdateLastStartTimeByBatchType( CommonConstants.BATCH_TYPE_REVIEW_COUNT_UPDATER  , CommonConstants.BATCH_NAME_REVIEW_COUNT_UPDATER );
            //get user id list for them review count will be updated
            List<Long> userIdList = batchTrackerService.getUserIdListToBeUpdated( lastRunEndTime );
            //getting no of reviews for the agents 
            Map<Long, Integer> agentsReviewCount;
            try {
                agentsReviewCount = batchTrackerService.getReviewCountForAgents( userIdList );
            } catch ( ParseException e ) {
                LOG.error( "Error while parsing the data fetched from mongo for survey count", e );
                throw e;
            }
            if ( agentsReviewCount != null && !agentsReviewCount.isEmpty() )
                solrSearchService.updateCompletedSurveyCountForMultipleUserInSolr( agentsReviewCount );


            //updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_REVIEW_COUNT_UPDATER );
        } catch ( Exception e ) {
            LOG.error( "Error in solr review count updater", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_REVIEW_COUNT_UPDATER,
                    e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_REVIEW_COUNT_UPDATER,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in batch tracker " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }

    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );
        solrSearchService = (SolrSearchService) jobMap.get( "solrSearchService" );
    }

}
