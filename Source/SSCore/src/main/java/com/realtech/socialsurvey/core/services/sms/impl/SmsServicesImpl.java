package com.realtech.socialsurvey.core.services.sms.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.entities.SmsEntity;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiIntegrationBuilder;
import com.realtech.socialsurvey.core.services.generator.UrlService;
import com.realtech.socialsurvey.core.services.sms.SmsServices;
import com.realtech.socialsurvey.core.services.stream.StreamMessagesService;

import retrofit.client.Response;

@Component
public class SmsServicesImpl implements SmsServices {

	public static final Logger LOG = LoggerFactory.getLogger(SmsServicesImpl.class);

	@Autowired
	private UrlService urlService;

	@Autowired
	private StreamApiIntegrationBuilder streamApiIntegrationBuilder;

	@Autowired
	private StreamMessagesService streamMessagesService;

	private boolean saveMessageToStreamLater(SmsEntity smsEntity) {
		return streamMessagesService.saveFailedStreamSmsMessages(smsEntity);
	}

	@Override
	public boolean sendSmsReminder( String configuredSmsText, String surveyLink, String customerFirstName,
			String agentFirstName, SmsEntity smsEntity, boolean saveToStreamLater ) throws InvalidInputException {
		
		LOG.debug( "SmsServicesImpl : sendSmsReminder method started." );
		
		String shortSurveyLink = urlService.shortenUrl( surveyLink, smsEntity.getRandomUUID() );
		String smsBody = replaceLegendsOfSmsBody( configuredSmsText, customerFirstName, agentFirstName );
		smsEntity.setSurveyUrl( shortSurveyLink );
		smsEntity.setSmsText( smsBody );
		smsEntity.setSmsCategory( "SurveyReminder" );
		
		try {

		    Response response = streamApiIntegrationBuilder.getStreamApi().streamSmsMessage( smsEntity );
		    
		    if(response.getStatus() != 201) {
		        LOG.error( "Response from stream sms is not ok" );
		        
		        if(saveToStreamLater) {
		            saveMessageToStreamLater( smsEntity );
		        }
	            
	            return false;
		    } 
		    return true;
		}
		catch ( Exception e ) {
			LOG.error( "Could not stream sms", e );
			saveMessageToStreamLater( smsEntity );
		}
        return false;
	}

	private String replaceLegendsOfSmsBody(String configuredSmsText, String customerFirstName, String agentFirstName ) {

		configuredSmsText = configuredSmsText.replace("[CustomerFirstName]", customerFirstName);
		configuredSmsText = configuredSmsText.replace("[AgentFirstName]", agentFirstName);
		return configuredSmsText;
	}
}
