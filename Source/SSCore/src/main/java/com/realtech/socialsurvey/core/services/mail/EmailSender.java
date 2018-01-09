package com.realtech.socialsurvey.core.services.mail;

import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


// JIRA SS-7: By RM04
/**
 * Interface for email sending utility
 */
public interface EmailSender
{

    /**
     * Sends mail based on
     * @param emailEntity
     * @param sendMailToSalesLead
     * @return
     * @throws InvalidInputException
     */
    boolean sendEmailByEmailEntity( EmailEntity emailEntity, boolean sendMailToSalesLead ) throws InvalidInputException;

}
