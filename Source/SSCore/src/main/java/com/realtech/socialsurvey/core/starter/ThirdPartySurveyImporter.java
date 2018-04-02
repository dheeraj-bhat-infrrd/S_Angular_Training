package com.realtech.socialsurvey.core.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;


@Component
public class ThirdPartySurveyImporter implements Runnable
{
    public static final Logger LOG = LoggerFactory.getLogger( ThirdPartySurveyImporter.class );

    @Autowired
    private SurveyHandler surveyHandler;


    @Override
    public void run()
    {
        while ( true ) {
            LOG.info( "3rd party Importer started" );
            surveyHandler.begin3rdPartySurveyImport();

            try {
                Thread.sleep( 1000 * 60 * 5 );
            } catch ( InterruptedException e ) {
                LOG.warn( "Thread interrupted" );
                break;
            }
        }
    }
}
