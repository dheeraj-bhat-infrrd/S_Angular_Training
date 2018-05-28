package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.services.socialmonitor.feed.SocialFeedService;


public class RetryFailedSocialFeeds extends QuartzJobBean
{
    public static final Logger LOG = LoggerFactory.getLogger( RetryFailedSocialFeeds.class );

    private SocialFeedService socialFeedService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Retrying failed social feed for social monitor" );
        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        socialFeedService.retryFailedSocialFeeds();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        socialFeedService = (SocialFeedService) jobMap.get( "socialFeedService" );
    }

}