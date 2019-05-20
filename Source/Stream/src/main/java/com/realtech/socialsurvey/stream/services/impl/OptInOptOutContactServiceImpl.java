package com.realtech.socialsurvey.stream.services.impl;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.realtech.socialsurvey.stream.common.FailedMessageConstants;
import com.realtech.socialsurvey.stream.messages.SmsInfo;
import com.realtech.socialsurvey.stream.services.OptInOptOutContactService;

@Service
public class OptInOptOutContactServiceImpl implements OptInOptOutContactService {

	private static final Logger LOG = LoggerFactory.getLogger( OptInOptOutContactServiceImpl.class );
	
	private KafkaTemplate<String, String> kafkaContactOptTemplate;
	
	@Autowired
    @Qualifier ( "contactOptTemplate")
	public void setKafkaContactOptTemplate(KafkaTemplate<String, String> kafkaContactOptTemplate) {
		
		this.kafkaContactOptTemplate = kafkaContactOptTemplate;
	}

	@Override
	public void processIncomingMessage(String contactNumber, String messageBody) throws InterruptedException, ExecutionException, TimeoutException {

		
		LOG.debug( "OptInOptOutContactServiceImpl : processIncomingMessage() started for contactNumber : {} and messageBody : {}", contactNumber, messageBody );
		
		int messageType = validateMessageBody( messageBody );
		SmsInfo smsInfo = new SmsInfo();
		smsInfo.setRandomUUID(UUID.randomUUID().toString() );
		smsInfo.setRecipientContactNumber( contactNumber );
		smsInfo.setIncomingMessageBody( messageBody );
		if( messageType == 1 ) {
			
			smsInfo.setSmsCategory( FailedMessageConstants.SMS_CATEGORY_UNSUBSCRIBE );
			smsInfo.setSmsText( FailedMessageConstants.RESUBSCRIBE_SMS_CONTENT );
		}
		else if( messageType == 2 ) {
			
			smsInfo.setSmsCategory( FailedMessageConstants.SMS_CATEGORY_RESUBSCRIBE );
			smsInfo.setSmsText( FailedMessageConstants.UNSUBSCRIBE_SMS_CONTENT );
		}
		else {
			
			LOG.warn( "Invalid message contents" );
			smsInfo.setSmsCategory( FailedMessageConstants.SMS_CATEGORY_INVALID );
			smsInfo.setSmsText( FailedMessageConstants.INVALID_SMS_CONTENT );
		}
		
		kafkaContactOptTemplate.send( new GenericMessage<>( smsInfo ) ).get( 60, TimeUnit.SECONDS );
		
		LOG.debug( "OptInOptOutContactServiceImpl : processIncomingMessage() finished for contactNumber : {} and messageBody : {}", contactNumber, messageBody );
	}
	
	private int validateMessageBody( String messageBody ) {
		
		if( !StringUtils.isEmpty( messageBody ) ) {
			
			List<String> wordList = Arrays.asList( messageBody.toLowerCase().split(" ") );
			if( wordList.contains( FailedMessageConstants.STOP ) ) {
				return 1;
			}
			else if( wordList.contains( FailedMessageConstants.START ) ) {
				
				return 2;
			}
		}
		return 0;
	}
}
