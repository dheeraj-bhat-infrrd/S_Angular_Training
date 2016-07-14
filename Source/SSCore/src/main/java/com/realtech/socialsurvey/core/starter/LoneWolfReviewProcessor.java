package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.integration.lonewolf.LoneWolfIntegrationApi;
import com.realtech.socialsurvey.core.integration.lonewolf.LoneWolfIntergrationApiBuilder;


@Component ( "lonewolfreviewprocessor")
public class LoneWolfReviewProcessor extends QuartzJobBean
{
    private static final Logger LOG = LoggerFactory.getLogger( LoneWolfReviewProcessor.class );

    private LoneWolfIntergrationApiBuilder loneWolfIntegrationApiBuilder;

    private LoneWolfIntegrationApi loneWolfIntegrationApi;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        try {
            LOG.info( "Executing lonewolf review processor" );
            initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        } catch ( Exception ex ) {

        }
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        loneWolfIntegrationApiBuilder = (LoneWolfIntergrationApiBuilder) jobMap.get( "loneWolfIntegrationApiBuilder" );
        loneWolfIntegrationApi = loneWolfIntegrationApiBuilder.getLoneWolfIntegrationApi();
    }

}
