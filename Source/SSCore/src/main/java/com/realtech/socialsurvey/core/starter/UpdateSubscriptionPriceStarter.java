package com.realtech.socialsurvey.core.starter;

import java.util.List;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.UsercountModificationNotification;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;


/**
 * Gets the list of users where number of user have been modified
 *
 */
@Component ( "updatesubscriptionprice")
public class UpdateSubscriptionPriceStarter extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( UpdateSubscriptionPriceStarter.class );

    @Autowired
    private ReviseSubscriptionPrice reviseSubscription;

    private BatchTrackerService batchTrackerService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        try {
            LOG.info( "ExecutingUpdateSubscriptionPriceStarter " );
            initializeDependencies( jobExecutionContext.getMergedJobDataMap() );

            //update last run start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_UPDATE_SUBSCRIPTION_PRICE_STARTER,
                CommonConstants.BATCH_NAME_UPDATE_SUBSCRIPTION_PRICE_STARTER );

            List<UsercountModificationNotification> userModificatonRecords = reviseSubscription
                .getCompaniesWithUserCountModified();
            if ( userModificatonRecords != null && !userModificatonRecords.isEmpty() ) {
                LOG.debug( "Found " + userModificatonRecords.size() + " to process" );
                for ( UsercountModificationNotification userModificationRecord : userModificatonRecords ) {
                    LOG.debug( "Fetching data for user modification record: "
                        + userModificationRecord.getUsercountModificationNotificationId() );
                    try {
                        reviseSubscription.processChargeOnSubscription( userModificationRecord );
                    } catch ( NonFatalException e ) {
                        LOG.error( "Could not process subscription for " + userModificationRecord.getCompany(), e );
                    } catch ( Exception e ) {
                        LOG.error( "Could not process subscription for " + userModificationRecord.getCompany(), e );
                    }
                }
            } else {
                LOG.info( "No records to modify subscription price" );
            }

            //Update last build time in batch tracker table
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_UPDATE_SUBSCRIPTION_PRICE_STARTER );
        } catch ( Exception e ) {
            LOG.error( "Error in UpdateSubscriptionPriceStarter", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType(
                    CommonConstants.BATCH_TYPE_UPDATE_SUBSCRIPTION_PRICE_STARTER, e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError(
                    CommonConstants.BATCH_NAME_UPDATE_SUBSCRIPTION_PRICE_STARTER, System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in UpdateSubscriptionPriceStarter " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        reviseSubscription = (ReviseSubscriptionPrice) jobMap.get( "reviseSubscription" );
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );
    }
}
