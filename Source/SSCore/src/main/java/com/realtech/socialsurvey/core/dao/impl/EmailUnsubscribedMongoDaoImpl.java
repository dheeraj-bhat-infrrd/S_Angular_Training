/**
 * 
 */
package com.realtech.socialsurvey.core.dao.impl;


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
import com.realtech.socialsurvey.core.dao.EmailUnsubscribedMongoDao;
import com.realtech.socialsurvey.core.entities.UnsubscribedEmails;
import com.realtech.socialsurvey.core.services.mail.impl.EmailUnsubscribeServiceImpl;

/**
 * @author Subhrajit
 *
 */
@Repository
public class EmailUnsubscribedMongoDaoImpl implements EmailUnsubscribedMongoDao {
	
	@Autowired
	private MongoTemplate mongotemplate;
	
	private static final String UNSUBSCRIBED_EMAILS = "unsubscribed_emails";
	
	private static final Logger LOG = LoggerFactory.getLogger( EmailUnsubscribeServiceImpl.class );

	/* (non-Javadoc)
	 * @see com.realtech.socialsurvey.core.dao.EmailUnsubscribedMongoDao#insertUnsubscribedEmail(com.realtech.socialsurvey.core.entities.UnsubscribedEmails)
	 */
	@Override
	public void insertUnsubscribedEmail(UnsubscribedEmails email) {
		LOG.debug("Inserting to unsubscribed emails collection.");
		mongotemplate.insert(email, UNSUBSCRIBED_EMAILS);
	}

	@Override
	public UnsubscribedEmails fetchByEmailAndCompany(String emailId, long companyId, int level) {
		LOG.debug("Method to fetch record from unsubscribed email with company id and email id.");
		Query query = new Query();
		query.addCriteria(Criteria.where(CommonConstants.EMAIL_ID).is(emailId));
		query.addCriteria(Criteria.where(CommonConstants.COMPANY_ID_COLUMN).is(companyId));
		query.addCriteria( Criteria.where( CommonConstants.LEVEL_COLUMN ).is(level) );
		List<UnsubscribedEmails> emailList = (List<UnsubscribedEmails>) mongotemplate.find(query,
				UnsubscribedEmails.class, UNSUBSCRIBED_EMAILS);
		if (emailList != null && emailList.size() > 0) {
			LOG.debug("Entry found for specific emailId and companyId");
			return emailList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void update(UnsubscribedEmails emails) {
		LOG.debug("Method to update document in unsubscribed email collection.");
		Query query = new Query();
		query.addCriteria(Criteria.where(CommonConstants.EMAIL_ID).is(emails.getEmailId()));
		query.addCriteria(Criteria.where(CommonConstants.COMPANY_ID_COLUMN).is(emails.getCompanyId()));
		query.addCriteria( Criteria.where( CommonConstants.LEVEL_COLUMN ).is( emails.getLevel() ) );
		
		Update update = new Update();
		update.set(CommonConstants.STATUS_COLUMN, emails.getStatus());
		update.set(CommonConstants.MODIFIED_ON_COLUMN, emails.getModifiedOn());
		
		mongotemplate.updateFirst(query, update, UNSUBSCRIBED_EMAILS);
		LOG.debug("Document updated successfully");
	}

    @Override
    public List<UnsubscribedEmails> fetchAllByEmailId( String emailId )
    {
        LOG.debug("Method to fetch record from unsubscribed email with company id and email id with company level or globally.");
        Query query = new Query();
        query.addCriteria(Criteria.where(CommonConstants.EMAIL_ID).is(emailId));
        List<UnsubscribedEmails> emailList = (List<UnsubscribedEmails>) mongotemplate.find(query,
                UnsubscribedEmails.class, UNSUBSCRIBED_EMAILS);
        if (emailList != null && emailList.size() > 0) {
            LOG.debug("Entry found for specific emailId and companyId");
            return emailList;
        } else {
            return null;
        }
    }

}