package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.services.stream.StreamMessagesService;


public class FailedStreamMsgProcessor extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( FailedStreamMsgProcessor.class );

    private StreamMessagesService streamMessagesService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "Job to process failed stream message started" );
        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        streamMessagesService.startFailedStreamMessagesRetry();
        LOG.info( "Job to process failed stream message finished" );
    }


    private void initializeDependencies( JobDataMap jobMap )
    {
        streamMessagesService = (StreamMessagesService) jobMap.get( "streamMessagesService" );
    }
}