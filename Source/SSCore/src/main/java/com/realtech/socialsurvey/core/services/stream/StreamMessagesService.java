package com.realtech.socialsurvey.core.services.stream;

import java.util.List;

import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.SendGridEventEntity;
import com.realtech.socialsurvey.core.entities.UserEvent;

/**
 * Handles stream messages
 * @author nishit
 *
 */
public interface StreamMessagesService
{

    /**
     * Saves email entity that failed streaming
     * @param emailEntity
     * @return
     */
    public boolean saveFailedStreamEmailMessages(EmailEntity emailEntity);

    /**
     * Saves click events that failed streaming
     * @param sendGridEventEntity
     * @return
     */
    public boolean saveFailedStreamClickEvent(SendGridEventEntity sendGridEventEntity);

    /**
     * 
     * @return
     */
	public List<EmailEntity> getAllFailedStreamMsgs(int start , int batchSize);

	/**
	 * 
	 * @param id
	 */
	public void deleteFailedStreamMsg(String id);

	/**
	 * 
	 * @param id
	 */
	public void updateRetryFailedForStreamMsg(String id);

	/**
	 * 
	 * @param userEvent
	 * @return
	 */
    public boolean saveStreamUserEvent( UserEvent userEvent );
}
