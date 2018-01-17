package com.realtech.socialsurvey.compute.services.impl;

import com.realtech.socialsurvey.compute.common.FailedMessageConstants;
import com.realtech.socialsurvey.compute.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.dao.FailedMessagesDao;
import com.realtech.socialsurvey.compute.dao.impl.FailedMessagesDaoImpl;
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
    public void insertPermanentlyFailedSocialPost( SocialPost post, Throwable thrw )
    {
        LOG.debug( "Adding a failed email message {}", post );
        LOG.trace( "Error encountered: {}", thrw );
        FailedSocialPost failedSocialPost = new FailedSocialPost();
        failedSocialPost.setRetryCounts( 0 );
        failedSocialPost.setRetrySuccessful( false );
        failedSocialPost.setWillRetry( false );
        failedSocialPost.setPermanentFailure( true );
        failedSocialPost.setData( post );
        failedSocialPost.setErrorMessage( thrw.getMessage() );
        failedSocialPost.setThrwStr( thrw.toString() );
        failedSocialPost.setThrwStacktrace( ThrowableUtils.controlledStacktrace( thrw ) );
        LOG.debug( "Persisting failed social post messages" );
        failedEmailMessagesDao.insertFailedFailedSocialPost( failedSocialPost );
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

}
