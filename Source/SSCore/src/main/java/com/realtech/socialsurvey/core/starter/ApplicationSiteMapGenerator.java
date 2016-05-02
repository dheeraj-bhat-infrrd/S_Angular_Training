package com.realtech.socialsurvey.core.starter;

import java.io.File;

import com.realtech.socialsurvey.core.services.generator.SiteMapService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.sitemap.SiteMapGenerator;


/**
 * Started app to generate sitemap for the application
 */
@Component ( "appsitemapgenerator")
public class ApplicationSiteMapGenerator extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( ApplicationSiteMapGenerator.class );

    private SiteMapService siteMapService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Starting up the ApplicationSiteMapGenerator." );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        siteMapService.siteMapGenerator();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        siteMapService = (SiteMapService) jobMap.get( "siteMapService" );

    }

}
