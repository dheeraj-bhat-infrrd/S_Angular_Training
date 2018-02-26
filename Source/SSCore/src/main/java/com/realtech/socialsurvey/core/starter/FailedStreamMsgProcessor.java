package com.realtech.socialsurvey.core.starter;

import java.util.List;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.integration.stream.StreamApiConnectException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiIntegrationBuilder;
import com.realtech.socialsurvey.core.services.stream.StreamMessagesService;

public class FailedStreamMsgProcessor extends QuartzJobBean 
{
	
	public static final Logger LOG = LoggerFactory.getLogger( FailedStreamMsgProcessor.class );

	private StreamMessagesService streamMessagesService;
	
	private StreamApiIntegrationBuilder streamApiIntegrationBuilder;
	
	
	
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		LOG.info("Job to process failed stream message started");
		// initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        
        int startIndex = 0;
	 	List<EmailEntity> failedstreamEmails = null;
	 	
	 	do{
	 		//get failed message in batch
	 		failedstreamEmails = streamMessagesService.getAllFailedStreamMsgs(startIndex, CommonConstants.FAILED_STREAM_MSGS_BATCH_SIZE);

			LOG.info("Processing next {} failed stream emails", failedstreamEmails.size());
			//process each message
			for(EmailEntity failedstreamEmail : failedstreamEmails) {
		 		try {		
		 			//send and delete email to stream api again
		 			LOG.info("Processing failed email with id {}", failedstreamEmail.get_id());
		            streamApiIntegrationBuilder.getStreamApi().streamEmailMessage( failedstreamEmail );
		            
		            streamMessagesService.deleteFailedStreamMsg(failedstreamEmail.get_id());
		            LOG.info("Successfully processed and deleted failed email with id {}", failedstreamEmail.get_id());
		        
		 		} catch ( StreamApiException | StreamApiConnectException e ) {
		            LOG.error( "Could not reprocess email with id {}", failedstreamEmail.get_id() );
		            //updated retry failed in database
		            streamMessagesService.updateRetryFailedForStreamMsg(failedstreamEmail.get_id());
		            LOG.info("Successfully updated retry flag for failed email with id {}", failedstreamEmail.get_id());
		        }
		 	}
		
	 	}while(failedstreamEmails != null && failedstreamEmails.size() ==  CommonConstants.FAILED_STREAM_MSGS_BATCH_SIZE );
	 		 	
		LOG.info("Job to process failed stream message finished");
	}

	
	private void initializeDependencies( JobDataMap jobMap )
    {
		streamMessagesService = (StreamMessagesService) jobMap.get( "streamMessagesService" );
		streamApiIntegrationBuilder = (StreamApiIntegrationBuilder) jobMap.get( "streamApiIntegrationBuilder" );
    }
}
