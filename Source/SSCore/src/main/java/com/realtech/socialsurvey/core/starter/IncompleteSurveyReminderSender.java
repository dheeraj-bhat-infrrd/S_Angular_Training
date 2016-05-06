package com.realtech.socialsurvey.core.starter;

import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;


public class IncompleteSurveyReminderSender extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( IncompleteSurveyReminderSender.class );

    private UserManagementService userManagementService;

    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing IncompleteSurveyReminderSender" );
        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        userManagementService.incompleteSurveyReminderSender();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );
    }
}