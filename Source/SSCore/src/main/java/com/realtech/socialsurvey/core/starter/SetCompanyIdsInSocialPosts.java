package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.utils.mongo.SetCompanyIdInSocialPosts;

public class SetCompanyIdsInSocialPosts extends QuartzJobBean
{
    public static final Logger LOG = LoggerFactory.getLogger( SetCompanyIdsInSocialPosts.class );
    private SetCompanyIdInSocialPosts setCompanyIdInSocialPosts;
        
    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "Executing SetCompanyIdInSocialPosts" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        setCompanyIdInSocialPosts.setCompanyIdInSocialPosts();
        LOG.info( "Finished executing SetCompanyIdInSocialPosts" );
    }

    private void initializeDependencies( JobDataMap jobMap )
    {
        setCompanyIdInSocialPosts = (SetCompanyIdInSocialPosts) jobMap.get( "setCompanyIdInSocialPosts" );
    }
}
