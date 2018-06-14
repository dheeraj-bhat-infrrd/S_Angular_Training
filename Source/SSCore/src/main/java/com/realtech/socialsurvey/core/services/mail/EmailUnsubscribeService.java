/**
 * 
 */
package com.realtech.socialsurvey.core.services.mail;

/**
 * @author Subhrajit
 *
 */
public interface EmailUnsubscribeService {

	public String unsubscribeEmail(long companyId, String encryptedUrl, long agentId);

    public boolean isUnsubscribed( String emailId, long companyId );

    public String resubscribeEmail( long companyId, String emailId );
}
