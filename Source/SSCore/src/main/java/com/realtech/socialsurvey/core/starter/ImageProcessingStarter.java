package com.realtech.socialsurvey.core.starter;

import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;


public class ImageProcessingStarter extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( ImageProcessingStarter.class );

    private OrganizationManagementService organizationManagementService;

    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "Starting processing of images" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        organizationManagementService.imageProcessorStarter();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
    }
}
