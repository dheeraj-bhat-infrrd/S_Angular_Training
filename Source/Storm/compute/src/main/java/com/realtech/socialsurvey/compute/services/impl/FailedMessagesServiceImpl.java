package com.realtech.socialsurvey.compute.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.FailedMessageConstants;
import com.realtech.socialsurvey.compute.dao.FailedMessagesDao;
import com.realtech.socialsurvey.compute.dao.impl.FailedMessagesDaoImpl;
import com.realtech.socialsurvey.compute.entities.EmailMessage;
import com.realtech.socialsurvey.compute.entities.FailedEmailMessage;
import com.realtech.socialsurvey.compute.entities.FailedReportRequest;
import com.realtech.socialsurvey.compute.entities.FailedSms;
import com.realtech.socialsurvey.compute.entities.FailedSocialPost;
import com.realtech.socialsurvey.compute.entities.FailedSurveyProcessor;
import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.entities.SmsInfo;
import com.realtech.socialsurvey.compute.entities.SurveyData;
import com.realtech.socialsurvey.compute.entities.UnsavedUserEvent;
import com.realtech.socialsurvey.compute.entities.UserEvent;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.utils.ThrowableUtils;


public class FailedMessagesServiceImpl implements FailedMessagesService
{

    private static final Logger LOG = LoggerFactory.getLogger( FailedMessagesServiceImpl.class );

    private FailedMessagesDao failedEmailMessagesDao;


    public FailedMessagesServiceImpl()
    {
        this.failedEmailMessagesDao = new FailedMessagesDaoImpl();
    }


    @Override
    public void insertPermanentlyFailedEmailMessage( EmailMessage emailMessage, Throwable thrw )
    {
        LOG.debug( "Adding a failed email message {}", emailMessage );
        LOG.trace( "Error encountered: {}", thrw );
        FailedEmailMessage failedEmailMessage = new FailedEmailMessage();
        failedEmailMessage.setMessageType( FailedMessageConstants.EMAIL_MESSAGES );
        failedEmailMessage.setRetryCounts( 0 );
        failedEmailMessage.setRetrySuccessful( false );
        failedEmailMessage.setWillRetry( false );
        failedEmailMessage.setPermanentFailure( true );
        failedEmailMessage.setData( emailMessage );
        failedEmailMessage.setErrorMessage( thrw.getMessage() );
        failedEmailMessage.setThrwStr( thrw.toString() );
        failedEmailMessage.setThrwStacktrace( ThrowableUtils.controlledStacktrace( thrw ) );
        LOG.debug( "Persisting failed email messages" );
        failedEmailMessagesDao.insertFailedEmailMessages( failedEmailMessage );
    }
    
    @Override
    public void insertPermanentlyFailedSms( SmsInfo smsInfo, Throwable thrw )
    {
        LOG.debug( "Adding a failed sms {}", smsInfo );
        LOG.trace( "Error encountered: {}", thrw );
        FailedSms failedSms = new FailedSms();
        failedSms.setMessageType( FailedMessageConstants.SMS );
        failedSms.setRetryCounts( 0 );
        failedSms.setRetrySuccessful( false );
        failedSms.setWillRetry( false );
        failedSms.setPermanentFailure( true );
        failedSms.setSmsEntity( smsInfo );
        failedSms.setErrorMessage( thrw.getMessage() );
        failedSms.setThrwStr( thrw.toString() );
        failedSms.setThrwStacktrace( ThrowableUtils.controlledStacktrace( thrw ) );
        LOG.debug( "Persisting failed sms" );
        failedEmailMessagesDao.insertFailedSms( failedSms );
    }
    
    @Override
    public void insertPermanentlyFailedSocialPost( SocialResponseObject<?> post, Throwable thrw )
    {
        LOG.debug( "Adding a failed email message {}", post );
        LOG.trace( "Error encountered: {}", thrw );
        FailedSocialPost failedSocialPost = new FailedSocialPost();
        failedSocialPost.setMessageType(FailedMessageConstants.SOCIAL_POST_MESSAGE);
        failedSocialPost.setRetryCounts( 0 );
        failedSocialPost.setRetrySuccessful( false );
        failedSocialPost.setWillRetry( false );
        failedSocialPost.setPermanentFailure( true );
        failedSocialPost.setData( post );
        failedSocialPost.setErrorMessage( thrw.getMessage() );
        failedSocialPost.setThrwStr( thrw.toString() );
        failedSocialPost.setThrwStacktrace( ThrowableUtils.controlledStacktrace( thrw ) );
        LOG.debug( "Persisting failed social post messages" );
        failedEmailMessagesDao.insertFailedSocialPost( failedSocialPost );
    }

    @Override
    public void insertTemporaryFailedSocialPost(SocialResponseObject<?> post) {
        LOG.debug( "Adding a temporary failed socialPost. This message will be retried" );
        FailedSocialPost failedSocialPost = new FailedSocialPost();
        failedSocialPost.setMessageType( FailedMessageConstants.SOCIAL_POST_MESSAGE);
        failedSocialPost.setRetryCounts( 0 );
        failedSocialPost.setRetrySuccessful( false );
        failedSocialPost.setWillRetry( true );
        failedSocialPost.setPermanentFailure( false );
        failedSocialPost.setData( post );
        LOG.debug( "Persisting temporarily failed social post" );
        failedEmailMessagesDao.insertFailedSocialPost( failedSocialPost );
    }


    @Override
    public void insertTemporaryFailedEmailMessage( EmailMessage emailMessage )
    {
        LOG.debug( "Adding a temporary failed messages. This message will be retried" );
        FailedEmailMessage failedEmailMessage = new FailedEmailMessage();
        failedEmailMessage.setMessageType( FailedMessageConstants.EMAIL_MESSAGES );
        failedEmailMessage.setRetryCounts( 0 );
        failedEmailMessage.setRetrySuccessful( false );
        failedEmailMessage.setWillRetry( true );
        failedEmailMessage.setPermanentFailure( false );
        failedEmailMessage.setData( emailMessage );
        LOG.debug( "Persisting temporarily failed email messages" );
        failedEmailMessagesDao.insertFailedEmailMessages( failedEmailMessage );
    }
    
    @Override
    public void insertTemporaryFailedSms( SmsInfo smsInfo )
    {
        LOG.debug( "Adding a temporary failed sms. This message will be retried" );
        FailedSms failedSms = new FailedSms();
        failedSms.setMessageType( FailedMessageConstants.SMS );
        failedSms.setRetryCounts( 0 );
        failedSms.setRetrySuccessful( false );
        failedSms.setWillRetry( true );
        failedSms.setPermanentFailure( false );
        failedSms.setSmsEntity( smsInfo );
        LOG.debug( "Persisting temporarily failed sms" );
        failedEmailMessagesDao.insertFailedSms( failedSms );
    }
    
    @Override
    public void insertTemporaryFailedSurveyProcessor( SurveyData surveyData )
    {
        LOG.debug( "Adding a temporary failed messages. This message will be retried" );
        FailedSurveyProcessor failedSurveyProcessor = new FailedSurveyProcessor();
        failedSurveyProcessor.setMessageType( FailedMessageConstants.SURVEY_PROCESSOR_MESSAGE );
        failedSurveyProcessor.setRetryCounts( 0 );
        failedSurveyProcessor.setRetrySuccessful( false );
        failedSurveyProcessor.setWillRetry( true );
        failedSurveyProcessor.setPermanentFailure( false );
        failedSurveyProcessor.setData( surveyData );
        LOG.debug( "Persisting temporarily failed surveyProcessor messages" );
        failedEmailMessagesDao.insertFailedSurveyProcessor(failedSurveyProcessor);
    }

    @Override
    public void insertPermanentlyFailedReportRequest(ReportRequest reportRequest, Throwable thrw)
    {
        LOG.debug( "Adding a failed report request {}", reportRequest );
        LOG.trace( "Error encountered: {}", thrw );
        FailedReportRequest failedReportRequest = new FailedReportRequest();
        failedReportRequest.setRetryCounts( 0 );
        failedReportRequest.setRetrySuccessful( false );
        failedReportRequest.setWillRetry( false );
        failedReportRequest.setPermanentFailure( true );
        failedReportRequest.setData( reportRequest );
        failedReportRequest.setErrorMessage( thrw.getMessage() );
        failedReportRequest.setThrwStr( thrw.toString() );
        failedReportRequest.setThrwStacktrace( ThrowableUtils.controlledStacktrace( thrw ) );
        LOG.debug( "Persisting failed report request" );
        failedEmailMessagesDao.insertFailedReportRequest( failedReportRequest );
    }

    @Override
    public void insertTemporaryFailedReportRequest( ReportRequest reportRequest )
    {
        LOG.debug( "Adding a temporary report request. This message will be retried" );
        FailedReportRequest failedReportRequest = new FailedReportRequest();
        failedReportRequest.setRetryCounts( 0 );
        failedReportRequest.setRetrySuccessful( false );
        failedReportRequest.setWillRetry( true );
        failedReportRequest.setPermanentFailure( false );
        failedReportRequest.setData( reportRequest );
        LOG.debug( "Persisting temporarily failed email messages" );
        failedEmailMessagesDao.insertFailedReportRequest( failedReportRequest );
    }

    @Override
    public int deleteFailedEmailMessage(String randomUUID) {
        LOG.debug("Deleting temporary failed email message with randomUUID {}", randomUUID);
        return failedEmailMessagesDao.deleteFailedEmailMessage(randomUUID);
    }
    
    @Override
    public int deleteFailedSms( String randomUUID ) {
        LOG.debug("Deleting temporary failed sms with randomUUID {}", randomUUID);
        return failedEmailMessagesDao.deleteFailedSms( randomUUID );
    }

    @Override
    public int updateFailedEmailMessageRetryCount(String randomUUID) {
        LOG.debug("Updating failed email message retry count with randonUUID {}", randomUUID);
        return failedEmailMessagesDao.updatedFailedEmailMessageRetryCount(randomUUID);
    }
    
    @Override
    public int updateFailedSmsRetryCount( String randomUUID ) {
        LOG.debug("Updating failed sms retry count with randonUUID {}", randomUUID);
        return failedEmailMessagesDao.updatedFailedSmsRetryCount(randomUUID);
    }
    
    
    @Override
    public boolean insertUnsavedUserEvent( UserEvent userEvent, boolean willRetry, int retryCount, boolean wasRetrySuccessful, boolean isPermanentlyFailed, Throwable thrw ) 
    {
        LOG.debug("saving failed user event info");
        
        if( userEvent == null ) {
            LOG.warn( "No user event specified" );
            return false;
        }
        
        LOG.debug( "Adding a unsaved user event {}", userEvent );
        
        UnsavedUserEvent unsavedEvent = new UnsavedUserEvent();
        unsavedEvent.setData( userEvent );
        unsavedEvent.setWillRetry( willRetry );
        unsavedEvent.setRetryCounts( retryCount );
        unsavedEvent.setRetrySuccessful( wasRetrySuccessful );
        unsavedEvent.setPermanentFailure( isPermanentlyFailed );
        
        
        if( thrw != null ) {
            LOG.trace( "Error encountered while saving : {}", thrw );
            unsavedEvent.setErrorMessage( thrw.getMessage() );
            unsavedEvent.setThrwStr( thrw.toString() );
            unsavedEvent.setThrwStacktrace( ThrowableUtils.controlledStacktrace( thrw ) );
        }
        
        if( !willRetry && isPermanentlyFailed ) {
            LOG.debug( "Persisting unsaved user event" );
        } else {
            LOG.debug( "Persisting temporarily unsaved user event" );
        }
        return failedEmailMessagesDao.insertUnsavedUserEvent( unsavedEvent );
    }

    @Override
    public int deleteFailedSocialPost(String postId) {
        LOG.debug("Deleting temporary failed social post with postId {}", postId);
        return failedEmailMessagesDao.deleteFailedSocialPost(postId);
    }

    @Override
    public int updateFailedSocialPostRetryCount(String postId) {
        LOG.debug("Updating failed social post retryCount having postId {}", postId);
        return failedEmailMessagesDao.updateFailedSocialPostRetryCount(postId);
    }
    
    @Override
    public int deleteFailedSurveyProcessor(long surveyId) {
        LOG.debug("Deleting temporary failed survey processor with surveyId {}", surveyId);
        return failedEmailMessagesDao.deleteFailedSurveyProcessor(surveyId);
    }
    
}
