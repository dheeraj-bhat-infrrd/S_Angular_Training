package com.realtech.socialsurvey.core.starter;

import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;


public class ZillowReviewProcessorAndAutoPostStarter extends QuartzJobBean
{
    public static final Logger LOG = LoggerFactory.getLogger( ZillowReviewProcessorAndAutoPostStarter.class );
    private SocialManagementService socialManagementService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing ZillowReviewFetchAndAutoPoster" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        socialManagementService.zillowReviewProcessorStarter();
    }

    private void initializeDependencies( JobDataMap jobMap )
    {
        socialManagementService = (SocialManagementService) jobMap.get( "socialManagementService" );
    }
}
