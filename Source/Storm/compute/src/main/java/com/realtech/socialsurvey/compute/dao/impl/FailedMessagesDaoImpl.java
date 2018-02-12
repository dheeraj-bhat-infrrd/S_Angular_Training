package com.realtech.socialsurvey.compute.dao.impl;

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
}
