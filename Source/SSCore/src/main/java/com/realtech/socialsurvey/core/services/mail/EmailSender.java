package com.realtech.socialsurvey.core.services.mail;

import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.EmailObject;
import com.realtech.socialsurvey.core.entities.FileContentReplacements;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


// JIRA SS-7: By RM04
/**
 * Interface for email sending utility
 */
public interface EmailSender
{

    /**
     * Sends mail with subject and body provided as raw text
     */
    // public void sendMail(EmailEntity emailEntity) throws InvalidInputException,
    // UndeliveredEmailException;

    /**
     * Sends mail with subject and body provided from templates and mail body replacements required
     * @param emailEntity
     * @param subjectFileName
     * @param messageBodyReplacements
     * @param isImmediate
     * @param holdSendingMail - Mail will not be sent if the value is set to true. It will stay in the database till the flag is set back to false.
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendEmailWithBodyReplacements( EmailEntity emailEntity, String subjectFileName,
        FileContentReplacements messageBodyReplacements, boolean isImmediate, boolean holdSendingMail ) throws InvalidInputException,
        UndeliveredEmailException;


    /**
     * Sends email after replacing subject and body
     * @param emailEntity
     * @param subjectReplacements
     * @param messageBodyReplacements
     * @param isImmediate
     * @param holdSendingMail - Mail will not be sent if the value is set to true. It will stay in the database till the flag is set back to false.
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendEmailWithSubjectAndBodyReplacements( EmailEntity emailEntity, FileContentReplacements subjectReplacements,
        FileContentReplacements messageBodyReplacements, boolean isImmediate, boolean holdSendingMail ) throws InvalidInputException,
        UndeliveredEmailException;


    /**
     * Sends mail
     * @param emailEntity
     * @param subject
     * @param mailBody
     * @param isImmediate
     * @param holdSendingMail - Mail will not be sent if the value is set to true. It will stay in the database till the flag is set back to false.
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    public void sendEmail( EmailEntity emailEntity, String subject, String mailBody, boolean isImmediate, boolean holdSendingMail )
        throws InvalidInputException, UndeliveredEmailException;


    public void saveEmailInDb( EmailObject emailObject );


    boolean sendEmailByEmailEntity( EmailEntity emailEntity ) throws InvalidInputException;
}
