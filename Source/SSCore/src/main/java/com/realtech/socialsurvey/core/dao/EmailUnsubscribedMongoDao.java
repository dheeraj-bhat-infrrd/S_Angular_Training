/**
 * 
 */
package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UnsubscribedEmails;

/**
 * @author Subhrajit
 *
 */
public interface EmailUnsubscribedMongoDao {
	
	public void insertUnsubscribedEmail(UnsubscribedEmails email);

	public UnsubscribedEmails fetchByEmailAndCompany(String emailId, long companyId, int level);

	public void update(UnsubscribedEmails emails);

    public List<UnsubscribedEmails> fetchAllByEmailId( String emailId );

}