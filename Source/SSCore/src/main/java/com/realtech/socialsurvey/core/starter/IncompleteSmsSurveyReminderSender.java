package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;

public class IncompleteSmsSurveyReminderSender extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( IncompleteSmsSurveyReminderSender.class );

    private UserManagementService userManagementService;

    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing IncompleteSmsSurveyReminderSender" );
        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        userManagementService.incompleteSurveyReminderSenderOverSms();
    }

    private void initializeDependencies( JobDataMap jobMap )
    {
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );
    }
}