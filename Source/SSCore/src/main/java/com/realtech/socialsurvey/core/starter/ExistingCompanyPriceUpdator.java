package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;


public class ExistingCompanyPriceUpdator extends QuartzJobBean
{
    private ReviseSubscriptionPrice reviseSubscription;


    private void initializeDependencies( JobDataMap jobMap )
    {
        reviseSubscription = (ReviseSubscriptionPrice) jobMap.get( "reviseSubscription" );
    }


    @Override
    protected void executeInternal( JobExecutionContext context ) throws JobExecutionException
    {
        initializeDependencies( context.getMergedJobDataMap() );
        reviseSubscription.updateSubscriptionPriceStarterForAllCompanies();
    }


}
