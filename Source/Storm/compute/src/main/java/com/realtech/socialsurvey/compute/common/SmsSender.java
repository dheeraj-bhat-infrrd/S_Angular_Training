package com.realtech.socialsurvey.compute.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;

public class SmsSender {
    
    private String accountSid;
    private String authToken;
    private String fromNumber;
    private String statusCallbackUrl;
    
    public SmsSender() {
		
    	accountSid = LocalPropertyFileHandler.getInstance()
                .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.TWILIO_ACCOUNT_SID )
                .orElseGet( () -> "" );
    	
    	authToken = LocalPropertyFileHandler.getInstance()
                .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.TWILIO_AUTH_TOKEN )
                .orElseGet( () -> "" );
    	
    	fromNumber = LocalPropertyFileHandler.getInstance()
                .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.TWILIO_FROM_NUMBER )
                .orElseGet( () -> "" );
    	
    	statusCallbackUrl = LocalPropertyFileHandler.getInstance()
                .getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE, ComputeConstants.TWILIO_STATUS_CALLBACK_URL )
                .orElseGet( () -> "" );
	}
    
    public Message sendSms(String toNumber, String smsBody) throws TwilioRestException {
    	
    	TwilioRestClient client = new TwilioRestClient( accountSid, authToken );
        
        List<NameValuePair> params = new ArrayList<>();
        params.add( new BasicNameValuePair( ComputeConstants.TWILIO_BODY_PARAM_KEY, smsBody ) );
        params.add( new BasicNameValuePair( ComputeConstants.TWILIO_TO_PARAM_KEY, toNumber ) );
        params.add( new BasicNameValuePair( ComputeConstants.TWILIO_FROM_PARAM_KEY, fromNumber ) );
        params.add( new BasicNameValuePair( ComputeConstants.TWILIO_STATUS_CALLBACK_URL_PARAM_KEY, statusCallbackUrl ) );

        MessageFactory messageFactory = client.getAccount().getMessageFactory();
        return messageFactory.create( params );
    }	 
}
