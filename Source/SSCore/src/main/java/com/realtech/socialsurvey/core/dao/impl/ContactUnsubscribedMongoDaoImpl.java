/**
 * 
 */
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

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ContactUnsubscribedMongoDao;
import com.realtech.socialsurvey.core.entities.UnsubscribedContacts;

/**
 * @author user345
 *
 */

@Repository
public class ContactUnsubscribedMongoDaoImpl implements ContactUnsubscribedMongoDao
{
    private static final Logger LOG = LoggerFactory.getLogger( ContactUnsubscribedMongoDaoImpl.class );

    @Autowired
    private MongoTemplate mongotemplate;
    
    private final String KEY_MODIFIED_BY = "modifiedBy";
    private final String KEY_MESSAGE_BODY = "incomingMessageBody";
    private final String key_OPTED_CONTACT_HISTORY = "optedContactHistory";

    @Override
    public UnsubscribedContacts fetchByContactNumberAndCompany( String contactNumber, long companyId, int level )
    {
        LOG.debug( "Method to fetch record from unsubscribed contact with contactNumber:{}, companyId:{}, level:{}",
            contactNumber, companyId, level );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.CONTACT_NUMBER ).is( contactNumber ) );
        query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( companyId ) );
        query.addCriteria( Criteria.where( CommonConstants.LEVEL_COLUMN ).is( level ) );
        List<UnsubscribedContacts> contactList = mongotemplate.find( query, UnsubscribedContacts.class, CommonConstants.UNSUBSCRIBED_CONTACTS );
        if ( contactList != null && !contactList.isEmpty() ) {
            LOG.debug( "Entry found for specific contactNumber and companyId" );
            return contactList.get( 0 );
        } else {
            return null;
        }
    }

    @Override
    public void update( UnsubscribedContacts contacts )
    {
        LOG.debug( "Method to update document in unsubscribed contactNumber collection" );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.CONTACT_NUMBER ).is( contacts.getcontactNumber() ) );
        query.addCriteria( Criteria.where( CommonConstants.COMPANY_ID_COLUMN ).is( contacts.getCompanyId() ) );
        query.addCriteria( Criteria.where( CommonConstants.LEVEL_COLUMN ).is( contacts.getLevel() ) );

        Update update = new Update();
        update.set( CommonConstants.STATUS_COLUMN, contacts.getStatus() );
        update.set( CommonConstants.MODIFIED_ON_COLUMN, contacts.getModifiedOn() );
        update.set( KEY_MODIFIED_BY, contacts.getModifiedBy() );
        update.set( KEY_MESSAGE_BODY, contacts.getIncomingMessageBody() );
        update.set( key_OPTED_CONTACT_HISTORY, contacts.getOptedContactHistory() );

        mongotemplate.updateFirst( query, update, CommonConstants.UNSUBSCRIBED_CONTACTS );
        LOG.debug( "Document updated successfully" );
    }

    @Override
    public void insertUnsubscribedContacts( UnsubscribedContacts contacts )
    {
        LOG.debug( "Inserting to unsubscribed contact collection." );
        mongotemplate.insert( contacts, CommonConstants.UNSUBSCRIBED_CONTACTS );
    }

    @Override
    public List<UnsubscribedContacts> fetchAllByContactNumber( String contactNumber )
    {
        LOG.debug(
            "Method to fetch record from unsubscribed contacts with company id and contact number with company level or globally." );
        Query query = new Query();
        query.addCriteria( Criteria.where( CommonConstants.CONTACT_NUMBER ).is( contactNumber ) );
        List<UnsubscribedContacts> contactList = mongotemplate.find( query, UnsubscribedContacts.class, CommonConstants.UNSUBSCRIBED_CONTACTS );
        if ( contactList != null && !contactList.isEmpty() ) {
            LOG.debug( "Entry found for specific contactNumber and companyId" );
            return contactList;
        } else {
            return Collections.emptyList();
        }
    }
}
