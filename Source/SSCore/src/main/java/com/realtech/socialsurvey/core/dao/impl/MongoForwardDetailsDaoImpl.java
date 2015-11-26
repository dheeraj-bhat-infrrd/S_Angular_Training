package com.realtech.socialsurvey.core.dao.impl;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ForwardMailDetailsDao;
import com.realtech.socialsurvey.core.entities.ForwardMailDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


@Repository
public class MongoForwardDetailsDaoImpl implements ForwardMailDetailsDao
{
    private static final Logger LOG = LoggerFactory.getLogger( MongoForwardDetailsDaoImpl.class );
    public static final String FORWARD_MAIL_DETAILS_COLLECTION = "FORWARD_MAIL_DETAILS";

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public void insertForwardMailDetails( ForwardMailDetails forwardMailDetails ) throws InvalidInputException
    {
        if ( forwardMailDetails == null )
            throw new InvalidInputException( "Forward Mail Details passed cannot be null" );
        if ( forwardMailDetails.getSenderMailId() == null || forwardMailDetails.getSenderMailId().isEmpty() )
            throw new InvalidInputException( "Sender Mail Id in Forward Mail Details cannot be null or empty" );
        if ( forwardMailDetails.getRecipientMailId() == null || forwardMailDetails.getRecipientMailId().isEmpty() )
            throw new InvalidInputException( "Recipient Mail Id in Forward Mail Details cannot be null or empty" );
        if ( forwardMailDetails.getMessageId() == null || forwardMailDetails.getMessageId().isEmpty() )
            throw new InvalidInputException( "Message Id in Forward Mail Details cannot be null or empty" );

        Date createdDate = new Date();

        if ( forwardMailDetails.getCreatedOn() == null )
            forwardMailDetails.setCreatedOn( createdDate );

        if ( forwardMailDetails.getModifiedOn() == null )
            forwardMailDetails.setModifiedOn( createdDate );

        if ( forwardMailDetails.getCreatedBy() == null || forwardMailDetails.getCreatedBy().isEmpty() )
            forwardMailDetails.setCreatedBy( CommonConstants.ADMIN_USER_NAME );

        if ( forwardMailDetails.getModifiedBy() == null || forwardMailDetails.getModifiedBy().isEmpty() )
            forwardMailDetails.setModifiedBy( CommonConstants.ADMIN_USER_NAME );

        LOG.info( "Method insertForwardMailDetails() to insert forward mail details started." );
        mongoTemplate.insert( forwardMailDetails, FORWARD_MAIL_DETAILS_COLLECTION );
        LOG.info( "Method insertForwardMailDetails() to insert forward mail details finished." );
    }


    @Override
    public boolean checkIfForwardMailDetailsExist( String senderMailId, String recipientMailId, String messageId )
        throws InvalidInputException
    {
        if ( senderMailId == null || senderMailId.isEmpty() )
            throw new InvalidInputException( "Sender Mail Id passed cannot be null or empty" );
        if ( recipientMailId == null || recipientMailId.isEmpty() )
            throw new InvalidInputException( "Recipient Mail Id passed cannot be null or empty" );
        if ( messageId == null || messageId.isEmpty() )
            throw new InvalidInputException( "Message Id passed cannot be null or empty" );

        LOG.info( "Started Method checkIfForwardMailDetailsExist() to find forward mail details." );
        LOG.info( "Criteria for fetching details is [ messageId : " + messageId + "]" );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.MESSAGE_ID_COLUMN ).is( messageId ) );

        ForwardMailDetails forwardMailDetail = mongoTemplate.findOne( query, ForwardMailDetails.class,
            FORWARD_MAIL_DETAILS_COLLECTION );

        if ( forwardMailDetail == null || !forwardMailDetail.getSenderMailId().equalsIgnoreCase( senderMailId )
            || !forwardMailDetail.getRecipientMailId().equalsIgnoreCase( recipientMailId ) ) {
            LOG.info( "No forward mail details found for the criteria [ messageId : " + messageId + "]" );
            if ( forwardMailDetail != null ) {
                LOG.info( "Found Forward Mail Details for message id having different sender and recipient mail id" );
                LOG.info( "Current Mail Info : [senderMailId :" + senderMailId + " recipientMailId : " + recipientMailId
                    + " messageId : " + messageId );
                LOG.info( "Found Mail Info : [senderMailId :" + forwardMailDetail.getSenderMailId() + " recipientMailId : "
                    + forwardMailDetail.getRecipientMailId() + " messageId : " + forwardMailDetail.getMessageId() );
                throw new UnsupportedOperationException(
                    "Found Forward Mail Details for message id having different sender and recipient mail id " );
            }
            return false;
        }
        LOG.info( "Found forward mail details with id : " + forwardMailDetail.get_id() );
        if ( forwardMailDetail.getStatus() != CommonConstants.STATUS_ACCESSED )
            updateStatusOfForwarMailDetails( forwardMailDetail.get_id() );
        LOG.info( "Ended Method checkIfForwardMailDetailsExist() to find forward mail details." );
        return true;
    }


    private void updateStatusOfForwarMailDetails( String id ) throws InvalidInputException
    {
        if ( id == null || id.isEmpty() )
            throw new InvalidInputException( "Id passed cannot be null or empty" );

        LOG.info( "Started method updateStatusOfForwarMailDetails() to update status of forward mail details with id : " + id );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.DEFAULT_MONGO_ID_COLUMN ).is( id ) );

        Update update = new Update();
        update.set( CommonConstants.FORWARD_MAIL_DETAILS_STATUS_COLUMN, CommonConstants.STATUS_ACCESSED );
        update.set( CommonConstants.FORWARD_MAIL_DETAILS_MODIFIED_ON_COLUMN, new Date() );

        mongoTemplate.updateFirst( query, update, ForwardMailDetails.class, FORWARD_MAIL_DETAILS_COLLECTION );
        LOG.info( "Ended method updateStatusOfForwarMailDetails() to update status of forward mail details with id : " + id );


    }

}
