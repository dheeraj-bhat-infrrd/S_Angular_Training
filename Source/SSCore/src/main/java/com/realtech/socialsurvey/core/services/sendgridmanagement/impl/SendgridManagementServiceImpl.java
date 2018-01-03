package com.realtech.socialsurvey.core.services.sendgridmanagement.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.integration.sendgrid.SendgridIntegrationApi;
import com.realtech.socialsurvey.core.integration.sendgrid.SendgridIntegrationApiBuilder;
import com.realtech.socialsurvey.core.services.sendgridmanagement.SendgridManagementService;
import com.realtech.socialsurvey.core.vo.SendgridUnsubscribeVO;

@Component
public class SendgridManagementServiceImpl   implements SendgridManagementService
{
    
    private static final Logger LOG = LoggerFactory.getLogger( SendgridManagementServiceImpl.class );
    
    //FOR SOCIALSURVEY.ME SENDGRID ACCOUNT
    @Value ( "${SENDGRID_SENDER_SOCIALSURVEYME_USERNAME}")
    private String sendGridUserName;

    @Value ( "${SENDGRID_SENDER_SOCIALSURVEYME_PASSWORD}")
    private String sendGridPassword;
    
    //FOR SOCIALSURVEY.US SENDGRID ACCOUNT
    @Value ( "${SENDGRID_SENDER_SOCIALSURVEYUS_USERNAME}")
    private String sendGridSocialSurveyUsUserName;

    @Value ( "${SENDGRID_SENDER_SOCIALSURVEYUS_PASSWORD}")
    private String sendGridSocialSurveyUsPassword;
    
    @Autowired
    SendgridIntegrationApiBuilder sendgridIntegrationApiBuilder;
    
    @Override
    public void addNewEmailToUnsubscribeList(String emailId) throws NonFatalException
    {
        LOG.info( "method addNewEmailToUnsubscribeList started for email id {}" + emailId );
        try {
            SendgridIntegrationApi sendgridIntegrationApi = sendgridIntegrationApiBuilder.getSendgridIntegrationApi();
            Response response = sendgridIntegrationApi.unsubscribeEmail( sendGridUserName, sendGridPassword, emailId );
            
            
            String responseString = null;
            if ( response != null ) {
                responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
            }

            String responseString2 = null;
            Response response2 = sendgridIntegrationApi.unsubscribeEmail( sendGridSocialSurveyUsUserName,
                sendGridSocialSurveyUsPassword, emailId );
            if ( response2 != null ) {
                responseString2 = new String( ( (TypedByteArray) response2.getBody() ).getBytes() );
            }
        }catch(Exception e){
            LOG.error( "Error while unsubscribing emails." );
            throw new NonFatalException(e.getMessage());
        }
        
        LOG.info( "method addNewEmailToUnsubscribeList finished for email id {}" + emailId );
        
    }


    @Override
    public void removewEmailFromUnsubscribeList(String emailId) throws NonFatalException
    {
        LOG.info( "method removewEmailFromUnsubscribeList started for email id {}" + emailId );
        try {
            SendgridIntegrationApi sendgridIntegrationApi = sendgridIntegrationApiBuilder.getSendgridIntegrationApi();
            Response response = sendgridIntegrationApi.resubscribeEmail( sendGridUserName, sendGridPassword, emailId );
            
            
            String responseString = null;
            if ( response != null ) {
                responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
            }

            String responseString2 = null;
            Response response2 = sendgridIntegrationApi.resubscribeEmail( sendGridSocialSurveyUsUserName,
                sendGridSocialSurveyUsPassword, emailId );
            if ( response2 != null ) {
                responseString2 = new String( ( (TypedByteArray) response2.getBody() ).getBytes() );
            }
        }catch(Exception e){
            LOG.error( "Error while resubscribing emails." );
            throw new NonFatalException(e.getMessage());
        }
        
        LOG.info( "method removewEmailFromUnsubscribeList finished for email id {}" + emailId );
        
    }
    
    
    @Override
    public List<SendgridUnsubscribeVO> getUnsubscribedEmailList() throws NonFatalException
    {
        LOG.info( "method getUnsubscribedEmailList started" );
        
        List<SendgridUnsubscribeVO> sendgridUnsubscribeVOs = null;
        try {
            SendgridIntegrationApi sendgridIntegrationApi = sendgridIntegrationApiBuilder.getSendgridIntegrationApi();
            Response response = sendgridIntegrationApi.getUnsubscribeEmails( sendGridUserName, sendGridPassword, 1 );
            
            
            String responseString = null;
            if ( response != null ) 
                responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
            sendgridUnsubscribeVOs = new ObjectMapper().readValue( responseString, new TypeReference<List<SendgridUnsubscribeVO>>() {} );
            
        }catch(Exception e){
            LOG.error( "Error while getUnsubscribedEmailList." );
            throw new NonFatalException(e.getMessage());
        }
        
        LOG.info( "method getUnsubscribedEmailList finished" );
        return sendgridUnsubscribeVOs;
    }
}
