package com.realtech.socialsurvey.core.starter;

import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;


/*
 * This class is responsible for getting all the profile images from Mongodb which point to Linkedin.
 * It stores into the Amazon server and updates the same in MongoDB.
 */
public class ImageLoader extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( ImageLoader.class );

    private ProfileManagementService profileManagementService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing ImageUploader" );
        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        profileManagementService.imageLoader();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        profileManagementService = (ProfileManagementService) jobMap.get( "profileManagementService" );
    }
}