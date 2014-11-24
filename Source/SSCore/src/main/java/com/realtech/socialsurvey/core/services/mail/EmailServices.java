package com.realtech.socialsurvey.core.services.mail;

import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * Services for sending mails via application
 */
public interface EmailServices {

	public void sendRegistrationInviteMail(String url, String recipientMailId, String firstname, String lastName) throws InvalidInputException,
			UndeliveredEmailException;
}
