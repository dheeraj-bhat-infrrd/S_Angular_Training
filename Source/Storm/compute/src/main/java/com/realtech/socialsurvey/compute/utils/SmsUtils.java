package com.realtech.socialsurvey.compute.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.SmsSender;
import com.realtech.socialsurvey.compute.entities.SolrSmsWrapper;
import com.realtech.socialsurvey.compute.topology.bolts.smssender.exception.SmsProcessingException;
import com.realtech.socialsurvey.compute.topology.bolts.smssender.exception.TemporarySmsProcessingException;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.instance.Message;

public class SmsUtils {

	private static final Logger LOG = LoggerFactory.getLogger( SmsUtils.class );
	
	private SmsSender smsSender;

	public SmsUtils() {
		smsSender = new SmsSender();
	}

	public void sendSms( SolrSmsWrapper solrSmsWrapper )
	{
		try {
			Message response = smsSender.sendSms( solrSmsWrapper.getRecipientContactNumber(), solrSmsWrapper.getSmsText() );

			if ( response == null || !response.getStatus().equals( "queued" ) ) {
				LOG.warn( "Sending sms failed. Got status code {}", ( response != null ? response.getStatus() : null ) );
				throw new TemporarySmsProcessingException(
						"Sending sms failed. Got status code " + ( response != null ? response.getStatus() : null ) );
			} else {

				LOG.info( "Sms SID : {}", response.getSid() );

				solrSmsWrapper.setTwilioSmsId( response.getSid() );
				solrSmsWrapper.setSmsStatus( response.getStatus() );
				if( response.getDateCreated() != null ) {

					solrSmsWrapper.setSmsCreatedDate( ConversionUtils.convertEpochSecondToSolrTrieFormat( response.getDateCreated().getTime() ) );
				}
				if( response.getDateUpdated() != null ) {

					solrSmsWrapper.setSmsUpdatedDate( ConversionUtils.convertEpochSecondToSolrTrieFormat( response.getDateUpdated().getTime() ) );
				}
				if( response.getDateSent() != null ) {

					solrSmsWrapper.setSmsSentDate( ConversionUtils.convertEpochSecondToSolrTrieFormat( response.getDateSent().getTime() ) );
				}
				solrSmsWrapper.setNumberOfSegments( response.getNumSegments() );
			}
		} 
		catch( TwilioRestException twilioRestException ) {

			solrSmsWrapper.setErrorCode( "" + twilioRestException.getErrorCode() );
			solrSmsWrapper.setErrorMessage( twilioRestException.getErrorMessage() );
			throw new SmsProcessingException( "TwilioRestException while sending sms: ", twilioRestException );
		}
	}
}
