package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.services.stream.StreamMessagesService;

public class FailedStreamSmsProcessor extends QuartzJobBean {

	public static final Logger LOG = LoggerFactory.getLogger( FailedStreamSmsProcessor.class );
	
	private StreamMessagesService streamMessagesService;
	 
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		
		LOG.info( "Job to process failed stream sms started" );
        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        streamMessagesService.startFailedStreamSmsRetry();
        LOG.info( "Job to process failed stream sms finished" );
	}
	
	private void initializeDependencies( JobDataMap jobMap )
    {
        streamMessagesService = (StreamMessagesService) jobMap.get( "streamMessagesService" );
    }
}
