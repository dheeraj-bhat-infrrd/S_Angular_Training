package com.realtech.socialsurvey.core.services.mail;

import java.util.List;

/**
 * Services for sending mails via application
 *
 */
public interface EmailServices {

	public void sendRegistrationInviteMail(String url, List<String> recipients);
}
