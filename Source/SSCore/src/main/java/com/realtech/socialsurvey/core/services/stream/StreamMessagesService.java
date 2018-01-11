package com.realtech.socialsurvey.core.services.stream;

import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.SendGridEventEntity;

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
}
