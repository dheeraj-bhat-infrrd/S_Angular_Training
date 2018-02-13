package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;

public class ManualSurveySourceUpdater  extends QuartzJobBean
{
    public static final Logger LOG = LoggerFactory.getLogger( ManualSurveySourceUpdater.class );

    private SurveyHandler surveyHandler;
    
    private void initializeDependencies( JobDataMap jobMap )
    {
    	surveyHandler = (SurveyHandler) jobMap.get( "surveyHandler" );
    }
    
    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        LOG.info( "Executing ManualSurveySourceUpdater" );
        surveyHandler.mapSourceInManualSurvey();
    }
}
