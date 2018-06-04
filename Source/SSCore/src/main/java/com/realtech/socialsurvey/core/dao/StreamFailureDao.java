package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.SendGridEventEntity;
import com.realtech.socialsurvey.core.entities.UserEvent;

/**
 * Handles stream failure messages
 * @author nishit
 *
 */
public interface StreamFailureDao
{
    /**
     * Inserts emails that failed to stream
     * @param emailEntity
     * @return
     */
    public boolean insertFailedEmailMessage(EmailEntity emailEntity);

    /**
     * Inserts the failed click events into mongo
     * @param sendGridEventEntity
     * @return
     */
    public boolean insertFailedClickEvent(SendGridEventEntity sendGridEventEntity);

    /**
     * 
     * @return
     */
    public List<EmailEntity> getAllFailedStreamMessages(int start , int batchSize);

	/**
	 * 
	 * @param id
	 */
	public void deleteFailedStreamMsg(String id);

	/**
	 * 
	 * @param id
	 * @param updatedValue
	 */
	public void updateRetryFailedForStreamMsg(String id, boolean updatedValue);

	/**
	 * 
	 * @param userEvent
	 * @return
	 */
    public boolean saveFailedUserEvent( UserEvent userEvent );
}
