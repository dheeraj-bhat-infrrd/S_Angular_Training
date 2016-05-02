package com.realtech.socialsurvey.core.starter;

import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;


/**
 * One time class to synchronize the hierarchy settings
 */
public class HierarchySettingsCorrector extends QuartzJobBean
{

    private static final Logger LOG = LoggerFactory.getLogger( HierarchySettingsCorrector.class );

    private OrganizationManagementService organizationManagementService;



    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        organizationManagementService.hierarchySettingsCorrector();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
    }
}
