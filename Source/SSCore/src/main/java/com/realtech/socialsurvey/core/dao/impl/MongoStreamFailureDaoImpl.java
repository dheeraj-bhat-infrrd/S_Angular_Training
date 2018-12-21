package com.realtech.socialsurvey.core.dao.impl;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.StreamFailureDao;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.SendGridEventEntity;
import com.realtech.socialsurvey.core.entities.UserEvent;
import com.realtech.socialsurvey.core.entities.ftp.FtpUploadRequest;
import com.realtech.socialsurvey.core.entities.integration.stream.FailedStreamMessage;

/**
 * Records failed stream messages in mongo collection 'FAILED_STREAM_MESSAGES'
 * @author nishit
 *
 */
@Repository
public class MongoStreamFailureDaoImpl implements StreamFailureDao
{

    private static final Logger LOG = LoggerFactory.getLogger( MongoStreamFailureDaoImpl.class );

    private static final String FAILED_STREAM_MESSAGES_COLLECTION = "FAILED_STREAM_MESSAGES";
    private static final String FAILED_CLICK_EVENTS_COLLECTION = CommonConstants.FAILED_CLICK_EVENTS_COLLECTION;
    private static final String FAILED_USER_EVENT_COLLECTION = "FAILED_USER_EVENT";
    private static final String KEY_STREAM_RETRY_FAILED = "streamRetryFailed";
    private static final String KEY_MESSAGE_CLASS = "messageClass";
    
    
    
    
    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public boolean insertFailedEmailMessage( EmailEntity emailEntity )
    {
        LOG.debug( "Inserting failed email message" );
        LOG.trace( "Inserting email entity into failed messages {}", emailEntity );
        mongoTemplate.insert( emailEntity, FAILED_STREAM_MESSAGES_COLLECTION );
        LOG.debug( "Inserting failed email message." );
        return true;
    }


    @Override
    public boolean insertFailedClickEvent( SendGridEventEntity sendGridEventEntity )
    {
        LOG.debug( "Inserting failed click event" );
        LOG.trace( "Inserting click event into failed messages {}", sendGridEventEntity );
        mongoTemplate.insert( sendGridEventEntity, FAILED_CLICK_EVENTS_COLLECTION );
        LOG.debug( "Inserting failed click events" );
        return true;
    }


    @Override
    public List<EmailEntity> getAllFailedStreamEmailMessages( int start, int batchSize )
    {
        LOG.debug( "Method getAllFailedStreamMessages() started for start index {} and batch size {}", start, batchSize );
        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_STREAM_RETRY_FAILED ).ne( true ) );

        query.addCriteria( Criteria.where( "messageType" ).is( "EMAIL_MESSAGES" ) );

        if ( start > 0 )
            query.skip( start );

        if ( batchSize > 0 )
            query.limit( batchSize );

        LOG.info("Query is " + query);
        List<EmailEntity> failedStreamMsgs = mongoTemplate.find( query, EmailEntity.class, FAILED_STREAM_MESSAGES_COLLECTION );
        LOG.debug( "Method getAllFailedStreamMessages() finished for start index {} and batch size {}", start, batchSize );
        return failedStreamMsgs;
    }


    @Override
    public void deleteFailedStreamMsg( String id )
    {
        LOG.debug( "Method deleteFailedStreamMsg() started for id {}", id );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( id ) );
        mongoTemplate.remove( query, FAILED_STREAM_MESSAGES_COLLECTION );
        LOG.debug( "Method deleteFailedStreamMsg() finished for id {}", id );

    }


    @Override
    public void updateRetryFailedForStreamMsg( String id, boolean updatedValue )
    {
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( id ) );
        Update update = new Update();
        update.set( KEY_STREAM_RETRY_FAILED, updatedValue );
        LOG.debug( "Updating retry flag for failed stream message" );
        mongoTemplate.updateFirst( query, update, FAILED_STREAM_MESSAGES_COLLECTION );
        LOG.debug( "Updated retry flag for failed stream message" );

    }


    @Override
    public <T> boolean insertFailedStreamMessage( FailedStreamMessage<T> failedStreamMessage )
    {
        LOG.debug( "Inserting failed stream message" );
        LOG.trace( "Inserting into FAILED_STREAM_MESSAGES : {}", failedStreamMessage );
        mongoTemplate.insert( failedStreamMessage, FAILED_STREAM_MESSAGES_COLLECTION );
        LOG.debug( "Inserted failed stream message." );
        return true;
    }


    @SuppressWarnings ( { "rawtypes" })
    @Override
    public List<FailedStreamMessage<FtpUploadRequest>> getFtpFailedStreamMessages( int start, int batchSize )
    {
        LOG.debug( "Method getAllFailedStreamMessages() started for start index: {} and batch size: {}",
            start, batchSize );

        Query query = new Query();
        query.addCriteria( Criteria.where( KEY_STREAM_RETRY_FAILED ).ne( true ) );

        query.addCriteria( Criteria.where( KEY_MESSAGE_CLASS ).is( FtpUploadRequest.class.getName() ) );

        if ( start > 0 )
            query.skip( start );

        if ( batchSize > 0 )
            query.limit( batchSize );

        List<FailedStreamMessage> failedStreamMsgs = mongoTemplate.find( query, FailedStreamMessage.class,
            FAILED_STREAM_MESSAGES_COLLECTION );

        if ( failedStreamMsgs == null ) {
            failedStreamMsgs = Collections.emptyList();
        }

        Gson gson = new Gson();
        
        LOG.debug( "Method getAllFailedStreamMessages() finished for start index: {} and batch size: {}",
            start, batchSize );
        String string = gson.toJson( failedStreamMsgs, new TypeToken<List<FailedStreamMessage<FtpUploadRequest>>>() {}.getType() );
        return gson.fromJson( string, new TypeToken<List<FailedStreamMessage<FtpUploadRequest>>>() {}.getType() );
    }


    @SuppressWarnings ( { "rawtypes", "unchecked" })
    @Override
    public FailedStreamMessage getFailedStreamMessage( String id )
    {
        LOG.debug( "Method getFailedStreamMessage() started for id {}", id );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( id ) );
        FailedStreamMessage failedStreamMessage = mongoTemplate.findOne( query, FailedStreamMessage.class,
            FAILED_STREAM_MESSAGES_COLLECTION );
        LOG.debug( "Method getFailedStreamMessage() finished for id {}", id );
        return failedStreamMessage;
    }


    @Override
    public boolean saveFailedUserEvent( UserEvent userEvent )
    {
        LOG.debug( "Inserting failed user event" );
        LOG.trace( "Inserting user event into failed messages {}", userEvent );
        mongoTemplate.insert( userEvent, FAILED_USER_EVENT_COLLECTION );
        LOG.debug( "Inserted failed used event" );
        return true;
    }
}
