package com.realtech.socialsurvey.core.handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.starter.EmailProcessor;


@Component
public class EmailHandler
{

    public static final Logger LOG = LoggerFactory.getLogger( EmailHandler.class );


    private ExecutorService executor;

    @Autowired
    private EmailProcessor emailProcessor;


    public void startEmailProcessing()
    {
        executor = Executors.newFixedThreadPool( 1 );
        executor.execute( emailProcessor );
    }


}
