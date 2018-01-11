package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.SendGridEventEntity;

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
}
