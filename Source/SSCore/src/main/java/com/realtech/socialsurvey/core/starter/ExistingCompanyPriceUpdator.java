package com.realtech.socialsurvey.core.starter;

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


public class ExistingCompanyPriceUpdator extends QuartzJobBean
{
    public static final Logger LOG = LoggerFactory.getLogger( ExistingCompanyPriceUpdator.class );

    private ReviseSubscriptionPrice reviseSubscription;

   
    private BatchTrackerService batchTrackerService;


    private void initializeDependencies( JobDataMap jobMap )
    {
        reviseSubscription = (ReviseSubscriptionPrice) jobMap.get( "reviseSubscription" );
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );
    }


    @Override
    protected void executeInternal( JobExecutionContext context ) throws JobExecutionException
    {
        initializeDependencies( context.getMergedJobDataMap() );
        batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
            CommonConstants.BATCH_TYPE_UPDATE_SUBSCRIPTION_PRICE_STARTER,
            CommonConstants.BATCH_NAME_UPDATE_SUBSCRIPTION_PRICE_STARTER );
        LOG.info( "calling price updator" );
        reviseSubscription.processChargeForAllcompanies();
        LOG.info( " price updator finished" );
        try {
            batchTrackerService.getLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_UPDATE_SUBSCRIPTION_PRICE_STARTER );
        } catch ( NoRecordsFetchedException | InvalidInputException e ) {
            try {
                batchTrackerService.updateErrorForBatchTrackerByBatchType(
                    CommonConstants.BATCH_TYPE_UPDATE_SUBSCRIPTION_PRICE_STARTER, e.getMessage() );
                batchTrackerService.sendMailToAdminRegardingBatchError(
                    CommonConstants.BATCH_TYPE_UPDATE_SUBSCRIPTION_PRICE_STARTER, System.currentTimeMillis(), e );

            } catch ( NoRecordsFetchedException | InvalidInputException | UndeliveredEmailException e1 ) {
                
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }


}
