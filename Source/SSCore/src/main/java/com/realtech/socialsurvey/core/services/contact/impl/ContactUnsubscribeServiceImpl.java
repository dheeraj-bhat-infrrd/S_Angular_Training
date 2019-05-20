/**
 * 
 */
package com.realtech.socialsurvey.core.services.contact.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ContactUnsubscribedMongoDao;
import com.realtech.socialsurvey.core.entities.OptedContactHistory;
import com.realtech.socialsurvey.core.entities.UnsubscribedContacts;
import com.realtech.socialsurvey.core.services.contact.ContactUnsubscribeService;

/**
 * @author user345
 *
 */

@Service
public class ContactUnsubscribeServiceImpl implements ContactUnsubscribeService
{
    private static final Logger LOG = LoggerFactory.getLogger( ContactUnsubscribeServiceImpl.class );

    @Autowired
    private ContactUnsubscribedMongoDao mongoDao;

    @Override
    public String unsubscribeContact( Long companyId, String contactNumber, Long agentId, int modifiedBy, String messageBody )
    {
        LOG.info( "Service method to unsubscribe contactNumber: {} ", contactNumber );
        if ( companyId == 0 ) {
            return globalUnsubscribeContact( companyId, contactNumber, agentId, modifiedBy, messageBody );
        } else {
            return companyUnsubscribeContact( companyId, contactNumber, agentId, modifiedBy, messageBody );
        }
    }

    @Override
    public String resubscribeContact( Long companyId, String contactNumber, int modifiedBy, String messageBody )
    {
        LOG.info( "Service method to unsubscribe contactNumber with agrs: companyId: {}, contactNumber: {}", companyId,
            contactNumber );
        if ( companyId == 0 ) {
            return globalResubscribe( contactNumber, modifiedBy, messageBody );
        } else {
            return companyResubscribe( companyId, contactNumber, modifiedBy, messageBody );
        }
    }

    private String companyUnsubscribeContact( Long companyId, String contactNumber, Long agentId, int modifiedBy, String messageBody )
    {
        Date date = new Date();
        if ( isUnsubscribed( companyId, contactNumber ) ) {
            LOG.info( "Contact number has already been unsubscribed globally or for company !!!" );
            return CommonConstants.CONTACT_STATUS_ALREADY_UNSUBSCRIBED;
        } else {
            UnsubscribedContacts contacts = mongoDao.fetchByContactNumberAndCompany( contactNumber, companyId,
                CommonConstants.LEVEL_COMPANY );
            if ( contacts != null && contacts.getStatus() == CommonConstants.STATUS_RESUBSCRIBED ) {
            	addOptedContactHistory( contacts );
                contacts.setStatus( CommonConstants.STATUS_UNSUBSCRIBED );
                contacts.setModifiedOn( date.getTime() );
                contacts.setModifiedBy( modifiedBy );
                contacts.setIncomingMessageBody( messageBody );
                mongoDao.update( contacts );
                LOG.info( "Contact number updated to unsubscribed successfully" );
                return CommonConstants.CONTACT_STATUS_SUCCESS_UNSUBSCRIBE;
            } else {
                UnsubscribedContacts unsubscribedContacts = new UnsubscribedContacts();
                unsubscribedContacts.setCompanyId( companyId );
                unsubscribedContacts.setAgentId( agentId );
                unsubscribedContacts.setContactNumber( contactNumber );
                unsubscribedContacts.setCreatedOn( date.getTime() );
                unsubscribedContacts.setModifiedOn( date.getTime() );
                unsubscribedContacts.setLevel( CommonConstants.LEVEL_COMPANY );
                unsubscribedContacts.setStatus( CommonConstants.STATUS_UNSUBSCRIBED );
                unsubscribedContacts.setModifiedBy( modifiedBy );
                unsubscribedContacts.setIncomingMessageBody( messageBody );
                mongoDao.insertUnsubscribedContacts( unsubscribedContacts );
                LOG.info( "Contact number saved to mongo unsubscribed collection succesfully." );
                return CommonConstants.CONTACT_STATUS_SUCCESS_UNSUBSCRIBE;
            }
        }
    }

    private String globalUnsubscribeContact( Long companyId, String contactNumber, Long agentId, int modifiedBy, String messageBody )
    {
        Date date = new Date();
        UnsubscribedContacts contacts = mongoDao.fetchByContactNumberAndCompany( contactNumber, companyId,
            CommonConstants.LEVEL_APPLICATION );
        if ( contacts != null && contacts.getStatus() == CommonConstants.STATUS_UNSUBSCRIBED ) {
            LOG.info( "Contact number has already been unsubscribed globally !!!" );
            return CommonConstants.CONTACT_STATUS_ALREADY_UNSUBSCRIBED;
        } else if ( contacts != null && contacts.getStatus() == CommonConstants.STATUS_RESUBSCRIBED ) {
        	addOptedContactHistory( contacts );
            contacts.setStatus( CommonConstants.STATUS_UNSUBSCRIBED );
            contacts.setModifiedOn( date.getTime() );
            contacts.setModifiedBy( modifiedBy );
            contacts.setIncomingMessageBody( messageBody );
            mongoDao.update( contacts );
            LOG.info( "Contact number updated to unsubscribed successfully" );
            return CommonConstants.CONTACT_STATUS_SUCCESS_UNSUBSCRIBE;
        } else {
            UnsubscribedContacts unsubscribedContacts = new UnsubscribedContacts();
            unsubscribedContacts.setCompanyId( companyId );
            unsubscribedContacts.setAgentId( agentId );
            unsubscribedContacts.setContactNumber( contactNumber );
            unsubscribedContacts.setCreatedOn( date.getTime() );
            unsubscribedContacts.setModifiedOn( date.getTime() );
            unsubscribedContacts.setLevel( CommonConstants.LEVEL_APPLICATION );
            unsubscribedContacts.setStatus( CommonConstants.STATUS_UNSUBSCRIBED );
            unsubscribedContacts.setModifiedBy( modifiedBy );
            unsubscribedContacts.setIncomingMessageBody( messageBody );
            mongoDao.insertUnsubscribedContacts( unsubscribedContacts );
            LOG.info( "Contact number saved to mongo unsubscribed collection succesfully." );
            return CommonConstants.CONTACT_STATUS_SUCCESS_UNSUBSCRIBE;
        }
    }

    @Override
    public boolean isUnsubscribed( long companyId, String contactNumber )
    {
        return ( isGlobalUnsubscribe( contactNumber ) || isCompanyUnsubscribed( contactNumber, companyId ) );
    }
    

    private boolean isCompanyUnsubscribed( String contactNumber, long companyId )
    {
        UnsubscribedContacts contacts = mongoDao.fetchByContactNumberAndCompany( contactNumber, companyId,
            CommonConstants.LEVEL_COMPANY );
        if ( contacts != null && contacts.getStatus() == CommonConstants.STATUS_UNSUBSCRIBED ) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isGlobalUnsubscribe( String contactNumber )
    {
        List<UnsubscribedContacts> contactList = mongoDao.fetchAllByContactNumber( contactNumber );
        if ( contactList != null && !contactList.isEmpty() ) {
            for ( UnsubscribedContacts contact : contactList ) {
                if ( contact.getStatus() == CommonConstants.STATUS_UNSUBSCRIBED
                    && contact.getLevel() == CommonConstants.LEVEL_APPLICATION ) {
                    return true;
                }
            }
        }
        return false;
    }

    private String globalResubscribe( String contactNumber, int modifiedBy, String messageBody )
    {
        Date date = new Date();
        List<UnsubscribedContacts> contactList = mongoDao.fetchAllByContactNumber( contactNumber );
        if( contactList.isEmpty() ) {
        	
        	return CommonConstants.CONTACT_STATUS_NOT_IN_UNSUBSCRIBED_LIST;
        }
        for ( UnsubscribedContacts contacts : contactList ) {
            if ( contacts.getStatus() == CommonConstants.STATUS_UNSUBSCRIBED ) {
            	addOptedContactHistory( contacts );
                contacts.setStatus( CommonConstants.STATUS_RESUBSCRIBED );
                contacts.setModifiedOn( date.getTime() );
                contacts.setModifiedBy( modifiedBy );
                contacts.setIncomingMessageBody( messageBody );
                mongoDao.update( contacts );
            }
        }
        return CommonConstants.CONTACT_STATUS_SUCCESS_RESUBSCRIBE;
    }

    private String companyResubscribe( long companyId, String contactNumber, int modifiedBy, String messageBody )
    {
        Date date = new Date();
        UnsubscribedContacts contacts = mongoDao.fetchByContactNumberAndCompany( contactNumber, companyId,
            CommonConstants.LEVEL_COMPANY );
        if ( contacts != null && contacts.getStatus() == CommonConstants.STATUS_UNSUBSCRIBED ) {
        	addOptedContactHistory( contacts );
            contacts.setStatus( CommonConstants.STATUS_RESUBSCRIBED );
            contacts.setModifiedOn( date.getTime() );
            contacts.setModifiedBy( modifiedBy );
            contacts.setIncomingMessageBody( messageBody );
            mongoDao.update( contacts );
            return CommonConstants.CONTACT_STATUS_SUCCESS_RESUBSCRIBE;
        } else if ( contacts != null && contacts.getStatus() == CommonConstants.STATUS_RESUBSCRIBED ) {
            return CommonConstants.CONTACT_STATUS_ALREADY_RESUBSCRIBED;
        } else {
            return CommonConstants.CONTACT_STATUS_NOT_IN_UNSUBSCRIBED_LIST;
        }
    }
    
    private void addOptedContactHistory( UnsubscribedContacts contacts ) {
    
    	OptedContactHistory contactHistory = new OptedContactHistory();
    	contactHistory.setLevel( contacts.getLevel() );
    	contactHistory.setStatus( contacts.getStatus() );
    	contactHistory.setModifiedOn( contacts.getModifiedOn() );
    	contactHistory.setModifiedBy( contacts.getModifiedBy() );
    	contactHistory.setIncomingMessageBody( contacts.getIncomingMessageBody() );
    	contacts.getOptedContactHistory().add( contactHistory );
    }
}
