package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;


public class CrmDataAgentIdMapper extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( CrmDataAgentIdMapper.class );

    private UserManagementService userManagementService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        LOG.info( "Executing CrmDataAgentIdMapper" );
        userManagementService.crmDataAgentIdMApper();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );
    }
}
