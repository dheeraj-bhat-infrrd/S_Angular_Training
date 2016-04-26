package com.realtech.socialsurvey.core.starter;

import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;


public class DeactivatedAccountPurger extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( DeactivatedAccountPurger.class );

    private OrganizationManagementService organizationManagementService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing DeactivatedAccountPurger" );
        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        organizationManagementService.deactivatedAccountPurger();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
    }
}