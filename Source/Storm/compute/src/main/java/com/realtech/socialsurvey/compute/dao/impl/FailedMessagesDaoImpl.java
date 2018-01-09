package com.realtech.socialsurvey.compute.dao.impl;

import com.realtech.socialsurvey.compute.entities.FailedReportRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.MongoDB;
import com.realtech.socialsurvey.compute.dao.FailedMessagesDao;
import com.realtech.socialsurvey.compute.entities.FailedEmailMessage;
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
    public boolean insertFailedFailedSocialPost( FailedSocialPost failedSocialPost )
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

}
