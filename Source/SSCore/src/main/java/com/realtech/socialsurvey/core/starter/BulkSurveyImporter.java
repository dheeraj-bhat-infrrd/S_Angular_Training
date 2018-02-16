package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.handler.SurveyImportHandler;

public class BulkSurveyImporter extends QuartzJobBean
{
    public static final Logger LOG = LoggerFactory.getLogger( BulkSurveyImporter.class );

    private SurveyImportHandler surveyImportHandler;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "Bulk Survey Importer started" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        surveyImportHandler.start3rdPartySurveyImport();
        LOG.info( "Bulk Survey Importer finished" );
    }

    private void initializeDependencies( JobDataMap jobMap )
    {
        surveyImportHandler = (SurveyImportHandler) jobMap.get( "surveyImportHandler" );
    }


}
