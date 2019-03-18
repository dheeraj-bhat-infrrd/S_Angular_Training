package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.services.generator.SEOSiteMapService;


/**
 * Started job to generate sitemap for the LO search
 */
@Component ( "seositemapcreatorjob")
public class SEOSiteMapCreatorJob extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( SEOSiteMapCreatorJob.class );

    private SEOSiteMapService seositeMapService;

    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Starting up the SEOSiteMapCreatorJob." );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        seositeMapService.seoSiteMapGenerator();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
    	seositeMapService = (SEOSiteMapService) jobMap.get( "seositeMapService" );

    }

}
