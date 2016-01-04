package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.handler.BillingReportHandler;

@Component
public class BillingReportGenerator extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( BillingReportGenerator.class );

    private BillingReportHandler billingReportHandler;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "Executing BillingReportGenerator" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        billingReportHandler.startReportGeneration();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        billingReportHandler = (BillingReportHandler) jobMap.get( "billingReportHandler" );
    }
}
