package com.realtech.socialsurvey.core.services.mail;

import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.FileContentReplacements;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

// JIRA SS-7: By RM04
/**
 * Interface for email sending utility
 */
public interface EmailSender {

	/**
	 * Sends mail with subject and body provided as raw text
	 */
	// public void sendMail(EmailEntity emailEntity) throws InvalidInputException,
	// UndeliveredEmailException;

	/**
	 * Sends mail with subject and body provided from templates and mail body replacements required
	 */
	public void sendEmailWithBodyReplacements(EmailEntity emailEntity, String subjectFileName, FileContentReplacements messageBodyReplacements)
			throws InvalidInputException, UndeliveredEmailException;

	public void sendEmailWithSubjectAndBodyReplacements(EmailEntity emailEntity, FileContentReplacements subjectReplacements,
			FileContentReplacements messageBodyReplacements) throws InvalidInputException, UndeliveredEmailException;

	public void sendEmail(EmailEntity emailEntity, String subject, String mailBody) throws InvalidInputException, UndeliveredEmailException;
}
