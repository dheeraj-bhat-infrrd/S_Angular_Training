package com.realtech.socialsurvey.core.services.stream.impl;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.api.builder.SSApiBatchIntegrationBuilder;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.StreamFailureDao;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.SendGridEventEntity;
import com.realtech.socialsurvey.core.entities.SmsEntity;
import com.realtech.socialsurvey.core.entities.TransactionSourceFtp;
import com.realtech.socialsurvey.core.entities.UserEvent;
import com.realtech.socialsurvey.core.entities.ftp.FtpUploadRequest;
import com.realtech.socialsurvey.core.entities.integration.stream.FailedStreamMessage;
import com.realtech.socialsurvey.core.exception.SSAPIBatchException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiConnectException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiIntegrationBuilder;
import com.realtech.socialsurvey.core.services.stream.StreamMessagesService;
import com.realtech.socialsurvey.core.vo.SmsVO;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


/**
 * Implementation for streaming messages
 * @author nishit
 *
 */
@Service
public class StreamMessagesServiceImpl implements StreamMessagesService
{


    private static final Logger LOG = LoggerFactory.getLogger( StreamMessagesServiceImpl.class );

    private StreamFailureDao streamFailureDao;

    private StreamApiIntegrationBuilder streamApiIntegrationBuilder;

    private SSApiBatchIntegrationBuilder sSApiBatchIntegrationBuilder;


    @Autowired
    public void setStreamFailureDao( StreamFailureDao streamFailureDao )
    {
        this.streamFailureDao = streamFailureDao;
    }


    @Autowired
    public void setStreamApiIntegrationBuilder( StreamApiIntegrationBuilder streamApiIntegrationBuilder )
    {
        this.streamApiIntegrationBuilder = streamApiIntegrationBuilder;
    }


    @Autowired
    public void setSSApiIntegrationBuilder( SSApiBatchIntegrationBuilder sSApiBatchIntegrationBuilder )
    {
        this.sSApiBatchIntegrationBuilder = sSApiBatchIntegrationBuilder;
    }


    @Override
    public boolean saveFailedStreamEmailMessages( EmailEntity emailEntity )
    {
        LOG.debug( "Saving failed email message" );
        return streamFailureDao.insertFailedEmailMessage( emailEntity );
    }
    
    @Override
    public boolean saveFailedStreamSmsMessages( SmsEntity smsEntity )
    {
    	LOG.info( "Saving failed sms message, SmsEntity - {}", smsEntity );
        return streamFailureDao.insertFailedSmsMessage( smsEntity );
    }
    
    @Override
    public boolean saveFailedSmsInTopology( SmsVO smsVO ) {
    	
    	LOG.debug( "Saving failed sms message" );
    	return streamFailureDao.insertFailedSmsOfTopology( smsVO );
    }


    @Override
    public boolean saveFailedStreamClickEvent( SendGridEventEntity sendGridEventEntity )
    {
        LOG.debug( "Saving failed click event" );
        return streamFailureDao.insertFailedClickEvent( sendGridEventEntity );
    }


    @Override
    public List<EmailEntity> getAllFailedStreamEmailMsgs( int start, int batchSize )
    {
        LOG.debug( "Getting all failed stream email messages" );
        return streamFailureDao.getAllFailedStreamEmailMessages( start, batchSize );
    }
    
    @SuppressWarnings("rawtypes")
	@Override
    public List<FailedStreamMessage> getAllFailedStreamSms( int start, int batchSize )
    {
        LOG.debug( "Getting all failed stream sms" );
        return streamFailureDao.getAllFailedStreamSms( start, batchSize );
    }


    @Override
    public List<FailedStreamMessage<FtpUploadRequest>> getFtpFailedStreamMessages( int start, int batchSize )
    {
        LOG.debug( "Getting all failed stream messages" );
        return streamFailureDao.getFtpFailedStreamMessages( start, batchSize );
    }


    @Override
    public void deleteFailedStreamMsg( String id )
    {
        LOG.debug( "Deleting stream message with id {}", id );
        streamFailureDao.deleteFailedStreamMsg( id );
    }

    @Override
    public FailedStreamMessage<FtpUploadRequest> getFailedStreamMsg( String id )
    {
        LOG.debug( "getting stream message with id {}", id );
        return streamFailureDao.getFailedStreamMessage( id );
    }


    @Override
    public void updateRetryFailedForStreamMsg( String id )
    {
        LOG.debug( "update retry failed to stream message with id " );
        streamFailureDao.updateRetryFailedForStreamMsg( id, true );
    }


    @Override
    public <T> boolean saveFailedStreamMessage( FailedStreamMessage<T> failedStreamMessage )
    {
        LOG.debug( "Saving failed stream message" );
        return streamFailureDao.insertFailedStreamMessage( failedStreamMessage );
    }


    @Override
    public void startFailedStreamMessagesRetry()
    {
        LOG.debug( "method startFailedStreamMessagesRetry() called" );
        retryFailedEmailMessages();
        retryFailedFtpUploadMessages();
        LOG.debug( "method startFailedStreamMessagesRetry() finished" );
    }
    
    @Override
    public void startFailedStreamSmsRetry()
    {
        LOG.debug( "method startFailedStreamSmsRetry() called" );
        retryFailedSms();
        LOG.debug( "method startFailedStreamSmsRetry() finished" );
    }
    
    @SuppressWarnings("rawtypes")
	private void retryFailedSms()
    {
        LOG.debug( "method retryFailedSms() called" );
        int startIndex = 0;
        List<FailedStreamMessage> failedStreamSms = null;

        do {
            //get failed sms in batch
        	failedStreamSms = getAllFailedStreamSms( startIndex, CommonConstants.FAILED_STREAM_MSGS_BATCH_SIZE );

            LOG.info( "Processing next {} failed stream sms", ( failedStreamSms != null ? failedStreamSms.size() : null ) );
            //process each message
            for ( FailedStreamMessage failedstreamSms : failedStreamSms ) {
                try {
                    //send and delete sms to stream api again
                    LOG.info( "Processing failed sms with id {}", failedstreamSms.getId() );
                    
                    Object message = failedstreamSms.getMessage();
                    
                    if( message instanceof SmsEntity ) {                    	
                    	streamApiIntegrationBuilder.getStreamApi().streamSmsMessage( (SmsEntity) message );
                    }
                    else {
                    	streamApiIntegrationBuilder.getStreamApi().streamSmsVoMessage( (SmsVO) message );
                    }

                    deleteFailedStreamMsg( failedstreamSms.getId() );
                    LOG.info( "Successfully processed and deleted failed sms with id {}", failedstreamSms.getId() );

                } catch ( StreamApiException | StreamApiConnectException e ) {
                    LOG.error( "Could not reprocess sms with id {}", failedstreamSms.getId() );
                    //updated retry failed in database
                    updateRetryFailedForStreamMsg( failedstreamSms.getId() );
                    LOG.info( "Successfully updated retry flag for failed sms with id {}", failedstreamSms.getId() );
                }
            }

        } while ( failedStreamSms != null && failedStreamSms.size() == CommonConstants.FAILED_STREAM_MSGS_BATCH_SIZE );
        LOG.debug( "method retryFailedSms() finished" );
    }


    private void retryFailedFtpUploadMessages()
    {
        LOG.debug( "method retryFailedFtpUploadMessages() called" );
        int startIndex = 0;
        List<FailedStreamMessage<FtpUploadRequest>> failedstreamFtpRequests = null;
        try {
            do {
                //get failed message in batch
                failedstreamFtpRequests = getAllFtpFailedStreamFtpMessages( startIndex,
                    CommonConstants.FAILED_STREAM_MSGS_BATCH_SIZE );

                // return if no failed messages are found
                if ( failedstreamFtpRequests == null || failedstreamFtpRequests.isEmpty() ) {
                    LOG.debug( "No messages found, method retryFailedFtpUploadMessages() finished" );
                    return;
                }

                LOG.info( "Processing next {} failed stream FTP uploads", failedstreamFtpRequests.size() );

                //process each message
                for ( FailedStreamMessage<FtpUploadRequest> failedstreamFtpUpload : failedstreamFtpRequests ) {
                    try {

                        // check if the FTP connection is active
                        TransactionSourceFtp ftpConnection = getFtpConnection( failedstreamFtpUpload.getCompanyId(),
                            failedstreamFtpUpload.getMessage().getFtpId() );

                        if ( StringUtils.equals( CommonConstants.STATUS_DELETED_MONGO, ftpConnection.getStatus() ) ) {
                            continue;
                        }

                        //send to stream api again
                        LOG.info( "Processing failed FTP upload request with id {}", failedstreamFtpUpload.getId() );
                        streamApiIntegrationBuilder.getStreamApi()
                            .sendsurveyTransactionRequest( failedstreamFtpUpload.getMessage() );

                        // delete the failed message
                        deleteFtpFailedStreamFtpMessage( failedstreamFtpUpload.getId() );
                        LOG.info( "Successfully processed and deleted failed FTP upload request with id {}",
                            failedstreamFtpUpload.getId() );

                    } catch ( StreamApiException | StreamApiConnectException error ) {

                        LOG.error( "Could not repost FTP upload request with id {}", failedstreamFtpUpload.getId(), error );

                        //updated retry failed in database
                        updateRetryFailedForFtpFailedMessage( failedstreamFtpUpload.getId() );
                        LOG.info( "Successfully updated retry flag for failed FTP upload request with id {}",
                            failedstreamFtpUpload.getId() );
                    }
                }

            } while ( failedstreamFtpRequests.size() == CommonConstants.FAILED_STREAM_MSGS_BATCH_SIZE );
        } catch ( SSAPIBatchException apiError ) {
            LOG.error( "SSAPI is not running, can't retry Failed FTP messages, aborting" );
        }
        LOG.debug( "method retryFailedFtpUploadMessages() finished" );
    }


    private List<FailedStreamMessage<FtpUploadRequest>> getAllFtpFailedStreamFtpMessages( int startIndex, int batchSize )
    {
        LOG.trace( "method getAllFtpFailedStreamFtpMessages() called" );
        Response response = sSApiBatchIntegrationBuilder.getIntegrationApi()
            .getFailedFtpStreamMessage( FtpUploadRequest.class.getName(), startIndex, batchSize );

        if ( response == null || response.getStatus() != 200 ) {
            LOG.warn( "Unable to get failed ftp stream messages in batch" );
            throw new SSAPIBatchException( "Unable to get failed ftp stream messages in batch" );
        }

        // get the response body
        String responseString = StringEscapeUtils
            .unescapeJava( new String( ( (TypedByteArray) response.getBody() ).getBytes() ) );

        return new Gson().fromJson( StringUtils.strip( responseString, "\"" ),
            new TypeToken<List<FailedStreamMessage<FtpUploadRequest>>>() {}.getType() );
    }


    private boolean deleteFtpFailedStreamFtpMessage( String id )
    {
        LOG.trace( "method deleteFtpFailedStreamFtpMessage() called" );
        Response response = sSApiBatchIntegrationBuilder.getIntegrationApi().deleteFailedFtpStreamMessage( id );

        if ( response == null || response.getStatus() != 200 ) {
            LOG.warn( "Unable to delete ftp stream message" );
            throw new SSAPIBatchException( "Unable to delete ftp stream message" );
        }

        return true;
    }


    private boolean updateRetryFailedForFtpFailedMessage( String id )
    {
        LOG.trace( "method updateRetryFailedForFtpFailedMessage() called" );
        Response response = sSApiBatchIntegrationBuilder.getIntegrationApi().updateRetryFailedForFailedFtpStreamMessage( id,
            StringUtils.EMPTY );

        if ( response == null || response.getStatus() != 200 ) {
            LOG.warn( "Unable to update rety failed for ftp stream message: {}", id );
            throw new SSAPIBatchException( "Unable to update rety failed for ftp stream message" );
        }

        return true;
    }


    private TransactionSourceFtp getFtpConnection( long companyId, long ftpId )
    {
        LOG.trace( "method getFtpConnection() called" );
        Response response = sSApiBatchIntegrationBuilder.getIntegrationApi().getFtpCrm( companyId, ftpId );

        if ( response == null || response.getStatus() != 200 ) {
            LOG.warn( "Unable to get ftp details for company: {}, ftp ID: {}", companyId, ftpId );
            throw new SSAPIBatchException( "Unable to get ftp details for the company" );
        }

        // get the response body
        String responseString = StringEscapeUtils
            .unescapeJava( new String( ( (TypedByteArray) response.getBody() ).getBytes() ) );

        return new Gson().fromJson( StringUtils.strip( responseString, "\"" ),
            new TypeToken<TransactionSourceFtp>() {}.getType() );
    }


    private void retryFailedEmailMessages()
    {
        LOG.debug( "method processFailedEmailMessages() called" );
        int startIndex = 0;
        List<EmailEntity> failedstreamEmails = null;

        do {
            //get failed message in batch
            failedstreamEmails = getAllFailedStreamEmailMsgs( startIndex, CommonConstants.FAILED_STREAM_MSGS_BATCH_SIZE );

            LOG.info( "Processing next {} failed stream emails", failedstreamEmails.size() );
            //process each message
            for ( EmailEntity failedstreamEmail : failedstreamEmails ) {
                try {
                    //send and delete email to stream api again
                    LOG.info( "Processing failed email with id {}", failedstreamEmail.get_id() );
                    streamApiIntegrationBuilder.getStreamApi().streamEmailMessage( failedstreamEmail );

                    deleteFailedStreamMsg( failedstreamEmail.get_id() );
                    LOG.info( "Successfully processed and deleted failed email with id {}", failedstreamEmail.get_id() );

                } catch ( StreamApiException | StreamApiConnectException e ) {
                    LOG.error( "Could not reprocess email with id {}", failedstreamEmail.get_id() );
                    //updated retry failed in database
                    updateRetryFailedForStreamMsg( failedstreamEmail.get_id() );
                    LOG.info( "Successfully updated retry flag for failed email with id {}", failedstreamEmail.get_id() );
                }
            }

        } while ( failedstreamEmails.size() == CommonConstants.FAILED_STREAM_MSGS_BATCH_SIZE );
        LOG.debug( "method processFailedEmailMessages() finished" );
    }
    
    @Override
    public boolean saveStreamUserEvent( UserEvent userEvent )
    {
        LOG.debug( "save failed user event with id {}", userEvent.getUserEventId() );   
        return streamFailureDao.saveFailedUserEvent( userEvent );
    }
}