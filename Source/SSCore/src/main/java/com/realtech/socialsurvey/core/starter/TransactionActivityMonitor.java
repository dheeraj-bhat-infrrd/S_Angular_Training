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
import com.realtech.socialsurvey.core.services.reportingmanagement.ReportingDashboardManagement;


public class TransactionActivityMonitor extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( TransactionActivityMonitor.class );

    private ReportingDashboardManagement reportingDashboardManagement;
    
    private BatchTrackerService batchTrackerService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        // initialize the dependencies
        LOG.info( "Executing TransactionMonitorBatchStarter" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        
        try {
            
         // update last start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_TRANSACTION_ACTIVITY_MONITOR, CommonConstants.BATCH_NAME_TRANSACTION_ACTIVITY_MONITOR );

            reportingDashboardManagement.getCompaniesWithNotransactions();
            reportingDashboardManagement.getCompaniesWithHighNotProcessedTransactions();
            reportingDashboardManagement.getCompaniesWithLowVolumeOfTransactions();
            reportingDashboardManagement.updateTransactionMonitorAlertsForCompanies();
            
            //updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_TRANSACTION_ACTIVITY_MONITOR );
            
        } catch ( Exception e ) {
            LOG.error( "Error in TransactionMonitorBatchStarter", e );
            try {
                //update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_TRANSACTION_ACTIVITY_MONITOR,
                    e.getMessage() );
                //send report bug mail to admin
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_TRANSACTION_ACTIVITY_MONITOR,
                    System.currentTimeMillis(), e );
            } catch ( NoRecordsFetchedException | InvalidInputException e1 ) {
                LOG.error( "Error while updating error message in TransactionMonitorBatchStarter " );
            } catch ( UndeliveredEmailException e1 ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }

    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        reportingDashboardManagement = (ReportingDashboardManagement) jobMap.get( "reportingDashboardManagement" );
        batchTrackerService = (BatchTrackerService) jobMap.get( "batchTrackerService" );        
    }


}
