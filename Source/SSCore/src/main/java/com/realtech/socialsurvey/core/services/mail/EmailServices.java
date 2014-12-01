package com.realtech.socialsurvey.core.services.mail;

import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * Services for sending mails via application
 */
public interface EmailServices {

	public void sendRegistrationInviteMail(String url, String recipientMailId, String firstname, String lastName) throws InvalidInputException,
			UndeliveredEmailException;

	// JIRA : SS-30 by RM-06
	/**
	 * Sends a reset password link to the user
	 * 
	 * @param url
	 * @param recipientMailId
	 * @throws InvalidInputException
	 * @throws UndeliveredEmailException
	 */
	public void sendResetPasswordLink(String url, String recipientMailId, String name) throws InvalidInputException, UndeliveredEmailException;
}
