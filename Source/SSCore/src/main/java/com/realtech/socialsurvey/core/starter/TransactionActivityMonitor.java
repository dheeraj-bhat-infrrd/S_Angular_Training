package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.services.reportingmanagement.ReportingDashboardManagement;


public class TransactionActivityMonitor extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( TransactionActivityMonitor.class );

    private ReportingDashboardManagement reportingDashboardManagement;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        // initialize the dependencies
        LOG.info( "Executing TransactionMonitorBatchStarter" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        LOG.info( "Executing CrmDataAgentIdMapper" );

        try {
            reportingDashboardManagement.getCompaniesWithNotransactions();
            reportingDashboardManagement.getCompaniesWithHighNotProcessedTransactions();
            reportingDashboardManagement.getCompaniesWithLowVolumeOfTransactions();
            reportingDashboardManagement.updateTransactionMonitorAlertsForCompanies();
        } catch ( Throwable thrw ) {
            LOG.error( "Transaction monitor exception", thrw );
        }

    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        reportingDashboardManagement = (ReportingDashboardManagement) jobMap.get( "reportingDashboardManagement" );
    }


}
