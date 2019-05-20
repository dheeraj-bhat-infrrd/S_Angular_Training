/**
 * 
 */
package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.UnsubscribedContacts;

/**
 * @author user345
 *
 */

public interface ContactUnsubscribedMongoDao
{
    /**
     * @param contactNumber
     * @param companyId
     * @param level
     * @return
     */
    public UnsubscribedContacts fetchByContactNumberAndCompany( String contactNumber, long companyId, int level );

    /**
     * @param contacts
     */
    public void update( UnsubscribedContacts contacts );

    /**
     * @param contacts
     */
    public void insertUnsubscribedContacts( UnsubscribedContacts contacts );

    /**
     * @param contactNumber
     * @return
     */

	List<UnsubscribedContacts> fetchAllByContactNumber(String contactNumber);

}
