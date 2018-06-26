/**
 * 
 */
package com.realtech.socialsurvey.core.services.mail.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.EmailUnsubscribedMongoDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.UnsubscribedEmails;
import com.realtech.socialsurvey.core.services.mail.EmailUnsubscribeService;

/**
 * @author Subhrajit
 *
 */
@Service
public class EmailUnsubscribeServiceImpl implements EmailUnsubscribeService {
	
	private static final Logger LOG = LoggerFactory.getLogger( EmailUnsubscribeServiceImpl.class );
	
	@Autowired
	private EmailUnsubscribedMongoDao mongoDao;
	
	@Autowired
	private UserDao userDao;

	/* (non-Javadoc)
	 * @see com.realtech.socialsurvey.core.services.mail.EmailUnsubscribeService#unsubscribeEmail(java.lang.String)
	 */
	@Override
	public String unsubscribeEmail(long companyId, String emailId, long agentId) {
		LOG.info("Service method to unsubscribe email id.");
		
		if(companyId == 0) {
		    return globalUnsubscribe(companyId, emailId, agentId);
		} else {
		    return companyUnsubscribe(companyId, emailId, agentId);
		}
	}

	private String companyUnsubscribe( long companyId, String emailId, long agentId )
    {
        Date date = new Date();
        if(userDao.isEmailAlreadyTaken(emailId)) {
            LOG.debug("Email id belongs to social survey user or admin!!!");
            return CommonConstants.STATUS_SS_USER_ADMIN;
        }  else if(isUnsubscribed( emailId, companyId )) {
            LOG.debug("Email id has already been unsubscribed globally or for company !!!");
            return CommonConstants.STATUS_ALREADY_UNSUBSCRIBED;
        } else {
            UnsubscribedEmails email = mongoDao.fetchByEmailAndCompany( emailId, companyId, CommonConstants.LEVEL_COMPANY );
            if(email != null && email.getStatus() == CommonConstants.STATUS_RESUBSCRIBED) {
                email.setStatus(CommonConstants.STATUS_UNSUBSCRIBED);
                email.setModifiedOn(date.getTime());
                mongoDao.update(email);
                LOG.debug("Email id updated to unsubscribed successfully");
                return CommonConstants.STATUS_SUCCESS_UNSUBSCRIBE;
            } else {
                UnsubscribedEmails unsubscribedEmail = new UnsubscribedEmails();
                unsubscribedEmail.setCompanyId(companyId);
                unsubscribedEmail.setAgentId(agentId);
                unsubscribedEmail.setEmailId(emailId);
                unsubscribedEmail.setCreatedOn(date.getTime());
                unsubscribedEmail.setModifiedOn(date.getTime());
                unsubscribedEmail.setLevel(CommonConstants.LEVEL_COMPANY);
                unsubscribedEmail.setStatus(CommonConstants.STATUS_UNSUBSCRIBED);
                mongoDao.insertUnsubscribedEmail(unsubscribedEmail);
                LOG.debug("Email id saved to mongo unsubscribed collection succesfully.");
                return CommonConstants.STATUS_SUCCESS_UNSUBSCRIBE;
            }
        }
    }

    private String globalUnsubscribe( long companyId, String emailId, long agentId )
    {
        Date date = new Date();
        if(userDao.isEmailAlreadyTaken(emailId)) {
            LOG.warn("Email id belongs to social survey user or admin!!!");
            return CommonConstants.STATUS_SS_USER_ADMIN;
        } else {
            UnsubscribedEmails email = mongoDao.fetchByEmailAndCompany( emailId, companyId, CommonConstants.LEVEL_APPLICATION );
            if(email != null && email.getStatus() == CommonConstants.STATUS_UNSUBSCRIBED ) {
                LOG.warn("Email id has already been unsubscribed globally !!!");
                return CommonConstants.STATUS_ALREADY_UNSUBSCRIBED;
            } else if(email != null && email.getStatus() == CommonConstants.STATUS_RESUBSCRIBED) {
                email.setStatus(CommonConstants.STATUS_UNSUBSCRIBED);
                email.setModifiedOn(date.getTime());
                mongoDao.update(email);
                LOG.debug("Email id updated to unsubscribed successfully");
                return CommonConstants.STATUS_SUCCESS_UNSUBSCRIBE;
            } else {
                UnsubscribedEmails unsubscribedEmail = new UnsubscribedEmails();
                unsubscribedEmail.setCompanyId(companyId);
                unsubscribedEmail.setAgentId(agentId);
                unsubscribedEmail.setEmailId(emailId);
                unsubscribedEmail.setCreatedOn(date.getTime());
                unsubscribedEmail.setModifiedOn(date.getTime());
                unsubscribedEmail.setLevel(CommonConstants.LEVEL_APPLICATION);
                unsubscribedEmail.setStatus(CommonConstants.STATUS_UNSUBSCRIBED);
                mongoDao.insertUnsubscribedEmail(unsubscribedEmail);
                LOG.debug("Email id saved to mongo unsubscribed collection succesfully.");
                return CommonConstants.STATUS_SUCCESS_UNSUBSCRIBE;
            }
        }
    }

    @Override
	public String resubscribeEmail(long companyId, String emailId ) {
		LOG.info("Service method to unsubscribe email id.");
		if(companyId == 0 ) {
		    return globalResubscribe(emailId);
		} else {
		    return companyResubscribe(companyId, emailId);
		}
	}


    private String companyResubscribe( long companyId, String emailId )
    {
        Date date = new Date();
        UnsubscribedEmails emails = mongoDao.fetchByEmailAndCompany( emailId, companyId, CommonConstants.LEVEL_COMPANY );
        if ( emails != null && emails.getStatus() == CommonConstants.STATUS_UNSUBSCRIBED ) {
            emails.setStatus( CommonConstants.STATUS_RESUBSCRIBED );
            emails.setModifiedOn( date.getTime() );
            mongoDao.update( emails );
            return CommonConstants.STATUS_SUCCESS_RESUBSCRIBE;
        } else if ( emails != null && emails.getStatus() == CommonConstants.STATUS_RESUBSCRIBED ) {
            return CommonConstants.STATUS_ALREADY_RESUBSCRIBED;
        } else {
            return CommonConstants.STATUS_NOT_IN_UNSUBSCRIBED_LIST;
        }
    }


    private String globalResubscribe( String emailId )
    {
        Date date = new Date();
        List<UnsubscribedEmails> emails = mongoDao.fetchAllByEmailId( emailId );
        for ( UnsubscribedEmails email : emails ) {
            if ( email.getStatus() == CommonConstants.STATUS_UNSUBSCRIBED ) {
                email.setStatus( CommonConstants.STATUS_RESUBSCRIBED );
                email.setModifiedOn( date.getTime() );
                mongoDao.update( email );
            }
        }
        return CommonConstants.STATUS_SUCCESS_RESUBSCRIBE;
    }


    @Override
    public boolean isUnsubscribed( String emailId, long companyId )
    {
        return ( isGlobalUnsubscribe( emailId, companyId ) || isCompanyUnsubscribed( emailId, companyId ) );
    }


    private boolean isCompanyUnsubscribed( String emailId, long companyId )
    {
        UnsubscribedEmails email = mongoDao.fetchByEmailAndCompany( emailId, companyId, CommonConstants.LEVEL_COMPANY );
        if ( email != null && email.getStatus() == CommonConstants.STATUS_UNSUBSCRIBED ) {
            return true;
        } else {
            return false;
        }
    }


    private boolean isGlobalUnsubscribe( String emailId, long companyId )
    {
        List<UnsubscribedEmails> emails = mongoDao.fetchAllByEmailId( emailId );
        if ( emails != null && !emails.isEmpty() ) {
            for ( UnsubscribedEmails email : emails ) {
                if ( email.getStatus() == CommonConstants.STATUS_UNSUBSCRIBED
                    && email.getLevel() == CommonConstants.LEVEL_APPLICATION ) {
                    return true;
                }
            }
        }
        return false;
    }
}