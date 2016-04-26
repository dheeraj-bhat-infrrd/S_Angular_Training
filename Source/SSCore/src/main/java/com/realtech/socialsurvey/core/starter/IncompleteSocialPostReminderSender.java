package com.realtech.socialsurvey.core.starter;

import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;


public class IncompleteSocialPostReminderSender extends QuartzJobBean
{

    private SocialManagementService socialManagementService;

    public static final Logger LOG = LoggerFactory.getLogger( IncompleteSocialPostReminderSender.class );

    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing IncompleteSocialPostReminderSender" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        socialManagementService.imcompleteSocialPostReminderSender();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        socialManagementService = (SocialManagementService) jobMap.get( "socialManagementService" );
    }
}
