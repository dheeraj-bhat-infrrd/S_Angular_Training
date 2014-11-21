package com.realtech.socialsurvey.core.services.mail;

import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

// JIRA SS-7: By RM04
/**
 * Interface for email sending utility
 * 
 */
public interface EmailSender {

	/**
	 * Sends mail
	 */
	public void sendMail(EmailEntity emailEntity, String fileNameForMessageSubject, String fileNameForMessageBody) throws InvalidInputException,
			UndeliveredEmailException;
}
