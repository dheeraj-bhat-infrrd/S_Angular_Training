package com.realtech.socialsurvey.core.handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.starter.ThirdPartySurveyImportor;


@Component
public class SurveyImportHandler
{
    private ExecutorService executor;

    @Autowired
    private ThirdPartySurveyImportor thirdPartySurveyImportor;


    public void start3rdPartySurveyImport()
    {
        executor = Executors.newFixedThreadPool( 1 );
        executor.execute( thirdPartySurveyImportor );
    }
}
