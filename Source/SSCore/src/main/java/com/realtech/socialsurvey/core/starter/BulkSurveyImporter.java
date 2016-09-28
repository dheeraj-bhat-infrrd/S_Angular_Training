package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;


public class BulkSurveyImporter extends QuartzJobBean
{
    public static final Logger LOG = LoggerFactory.getLogger( BillingReportGenerator.class );

    private SurveyHandler surveyHandler;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "Bulk Survey Importer started" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        surveyHandler.begin3rdPartySurveyImport();
        LOG.info( "Bulk Survey Importer finished" );
    }

    private void initializeDependencies( JobDataMap jobMap )
    {
        surveyHandler = (SurveyHandler) jobMap.get( "surveyHandler" );
    }


}
