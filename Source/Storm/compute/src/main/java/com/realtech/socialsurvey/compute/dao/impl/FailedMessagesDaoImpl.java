package com.realtech.socialsurvey.compute.dao.impl;

import com.realtech.socialsurvey.compute.entities.*;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.WriteResult;
import com.realtech.socialsurvey.compute.common.MongoDB;
import com.realtech.socialsurvey.compute.dao.FailedMessagesDao;
import com.realtech.socialsurvey.compute.entities.FailedEmailMessage;
import com.realtech.socialsurvey.compute.entities.FailedReportRequest;
import com.realtech.socialsurvey.compute.entities.FailedSocialPost;
import com.realtech.socialsurvey.compute.entities.FailedSurveyProcessor;
import com.realtech.socialsurvey.compute.entities.UnsavedUserEvent;


/**
 * Failed email messages dao implementation
 * @author nishit
 *
 */
public class FailedMessagesDaoImpl implements FailedMessagesDao
{

    private static final Logger LOG = LoggerFactory.getLogger( FailedMessagesDaoImpl.class );

    private MongoDB mongoDB;


    public FailedMessagesDaoImpl()
    {
        this.mongoDB = new MongoDB();
    }


    @Override
    public boolean insertFailedEmailMessages( FailedEmailMessage failedEmailMessage )
    {
        LOG.debug( "Inserting failed email message: {}", failedEmailMessage );
        mongoDB.datastore().save( failedEmailMessage );
        return true;
    }

    @Override
    public boolean insertFailedSocialPost(FailedSocialPost failedSocialPost )
    {
        LOG.debug( "Inserting failed Social Post: {}", failedSocialPost );
        mongoDB.datastore().save( failedSocialPost );
        return true;
    }

    @Override
    public boolean insertFailedReportRequest(FailedReportRequest failedReportRequest) {
        LOG.debug( "Inserting failed report request: {}", failedReportRequest );
        mongoDB.datastore().save( failedReportRequest );
        return true;

    }
    
    @Override
    public boolean insertFailedSurveyProcessor(FailedSurveyProcessor failedSurveyProcessor) {
    	LOG.debug( "Inserting failed survey processor request: {}", failedSurveyProcessor );
    	Datastore datastore = mongoDB.datastore();
    	final Query<FailedSurveyProcessor> query = datastore.createQuery(FailedSurveyProcessor.class)
                .field("data.surveyId").equal(failedSurveyProcessor.getData().getSurveyId());
    	long numberOfRecords = datastore.getCount(query);
    	if(numberOfRecords  == 0) {
    		mongoDB.datastore().save( failedSurveyProcessor );
    	}
        return true;
    }

    @Override
    public int deleteFailedEmailMessage(String randomUUID) {
        LOG.debug("Deleting failed email message with randomUUID {}", randomUUID);
        Query<FailedEmailMessage> query = mongoDB.datastore().createQuery(FailedEmailMessage.class)
                .field("data.randomUUID").equal(randomUUID)
                .field("permanentFailure").equal(false);
        final WriteResult result = mongoDB.datastore().delete(query);
        return result.getN();
    }

    @Override
    public int updatedFailedEmailMessageRetryCount(String randomUUID) {
        final Datastore datastore = mongoDB.datastore();
        LOG.debug("Updating retryCount of email message with randomUUID {}", randomUUID);
        final Query<FailedEmailMessage> query = datastore.createQuery(FailedEmailMessage.class)
                .field("data.randomUUID").equal(randomUUID)
                .field("permanentFailure").equal(false);
        final UpdateOperations<FailedEmailMessage> updateOperations = datastore.createUpdateOperations(FailedEmailMessage.class)
                .inc("retryCounts", 1)
                .set("data.isRetried", true);
        final UpdateResults updateResults = datastore.update(query, updateOperations);
        return updateResults.getUpdatedCount();
    }


    @Override
    public int deleteFailedSocialPost(String postId) {
        LOG.debug("Deleting failed social post with postId {}", postId);
        Query<FailedSocialPost> query = mongoDB.datastore().createQuery(FailedSocialPost.class)
                .field("data.postId").equal(postId)
                .field("permanentFailure").equal(false);
        final WriteResult result = mongoDB.datastore().delete(query);
        return result.getN();
    }

    @Override
    public int updateFailedSocialPostRetryCount(String postId) {
        final Datastore datastore = mongoDB.datastore();
        LOG.debug("Updating retryCount of socialpost having postId {}", postId);
        final Query<FailedSocialPost> query = datastore.createQuery(FailedSocialPost.class)
                .field("data.postId").equal(postId)
                .field("permanentFailure").equal(false);
        final UpdateOperations<FailedSocialPost> updateOperations = datastore.createUpdateOperations(FailedSocialPost.class)
                .inc("retryCounts", 1)
                .set("data.isRetried", true);
        final UpdateResults updateResults = datastore.update(query, updateOperations);
        return updateResults.getUpdatedCount();
    }
    
    
    @Override
    public boolean insertUnsavedUserEvent( UnsavedUserEvent unsavedEvent )
    {
        LOG.debug( "Inserting unsaved user event : {}", unsavedEvent );
        mongoDB.datastore().save( unsavedEvent );
        return true;
    }
    
    @Override
    public int deleteFailedSurveyProcessor(long surveyId) {
        LOG.debug("Deleting failed survey processor with internal dataId", surveyId);
        Query<FailedSurveyProcessor> query = mongoDB.datastore().createQuery(FailedSurveyProcessor.class)
                .field("data.surveyId").equal(surveyId)
                .field("permanentFailure").equal(false);
        final WriteResult result = mongoDB.datastore().delete(query);
        return result.getN();
    }
}
