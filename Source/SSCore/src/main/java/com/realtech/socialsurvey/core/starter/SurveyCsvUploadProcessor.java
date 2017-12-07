package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;


@Component
public class SurveyCsvUploadProcessor extends QuartzJobBean
{
    public static final Logger LOG = LoggerFactory.getLogger( SurveyCsvUploadProcessor.class );

    private SurveyHandler surveyHandler;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext )
    {
        LOG.info( "Executing SurveyCsvUploadProcessor" );

        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        surveyHandler.processActiveSurveyCsvUploads();
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        surveyHandler = (SurveyHandler) jobMap.get( "surveyHandler" );
    }
}
