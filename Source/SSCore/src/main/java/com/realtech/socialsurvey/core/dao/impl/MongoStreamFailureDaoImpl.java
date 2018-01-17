package com.realtech.socialsurvey.core.dao.impl;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.SendGridEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.realtech.socialsurvey.core.dao.StreamFailureDao;
import com.realtech.socialsurvey.core.entities.EmailEntity;

/**
 * Records failed stream messages in mongo collection 'FAILED_STREAM_MESSAGES'
 * @author nishit
 *
 */
@Repository
public class MongoStreamFailureDaoImpl implements StreamFailureDao
{
    
    private static final Logger LOG = LoggerFactory.getLogger( MongoStreamFailureDaoImpl.class );
    
    private static final String COLLECTION_NAME = "FAILED_STREAM_MESSAGES";
    private static final String FAILED_CLICK_EVENTS_COLLECTION = CommonConstants.FAILED_CLICK_EVENTS_COLLECTION;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public boolean insertFailedEmailMessage( EmailEntity emailEntity )
    {
        LOG.debug( "Inserting failed email message" );
        LOG.trace( "Inserting email entity into failed messages {}", emailEntity );
        mongoTemplate.insert( emailEntity, COLLECTION_NAME );
        LOG.debug( "Inserting failed email message." );
        return true;
    }

    @Override
    public boolean insertFailedClickEvent(SendGridEventEntity sendGridEventEntity) {
        LOG.debug( "Inserting failed click event" );
        LOG.trace( "Inserting click event into failed messages {}", sendGridEventEntity );
        mongoTemplate.insert( sendGridEventEntity, FAILED_CLICK_EVENTS_COLLECTION );
        LOG.debug( "Inserting failed click events" );
        return true;
    }

}
