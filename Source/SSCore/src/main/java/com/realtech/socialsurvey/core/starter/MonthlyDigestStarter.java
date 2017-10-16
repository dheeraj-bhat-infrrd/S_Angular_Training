package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.services.reportingmanagement.ReportingDashboardManagement;


public class MonthlyDigestStarter extends QuartzJobBean
{
    public static final Logger LOG = LoggerFactory.getLogger( MonthlyDigestStarter.class );

    private ReportingDashboardManagement reportingDashboardManagement;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "Executing MonthlyDigestStarter" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        reportingDashboardManagement.startMonthlyDigestProcess();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        reportingDashboardManagement = (ReportingDashboardManagement) jobMap.get( "reportingDashboardManagement" );
    }
}
