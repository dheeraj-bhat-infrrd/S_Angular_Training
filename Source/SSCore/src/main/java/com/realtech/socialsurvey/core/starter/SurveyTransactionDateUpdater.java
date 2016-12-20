package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;


public class SurveyTransactionDateUpdater extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( SurveyTransactionDateUpdater.class );

    private SurveyHandler surveyHandler;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "Survey Source Id updater started" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        surveyHandler.updateSurveyTransactionDateInMongo();
        LOG.info( "Survey Source Id updater finished" );
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        surveyHandler = (SurveyHandler) jobMap.get( "surveyHandler" );
    }


}
