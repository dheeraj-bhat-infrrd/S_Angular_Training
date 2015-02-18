package com.realtech.socialsurvey.core.services.mail;

import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * Services for sending mails via application
 */
public interface EmailServices {

	public void sendRegistrationInviteMail(String url, String recipientMailId, String firstname, String lastName) throws InvalidInputException,
			UndeliveredEmailException;

	// JIRA : SS-30 by RM-06

	public void sendResetPasswordEmail(String url, String recipientMailId, String name) throws InvalidInputException, UndeliveredEmailException;

	// JIRA SS-42 by RM05 : BOC
	/**
	 * Sends a link to new user to complete registration.
	 * 
	 * @param url
	 * @param recipientMailId
	 * @throws InvalidInputException
	 * @throws UndeliveredEmailException
	 */
	public void sendRegistrationCompletionEmail(String url, String recipientMailId, String name) throws InvalidInputException,
			UndeliveredEmailException;

	// JIRA SS-42 by RM05 : EOC

	public void sendSubscriptionChargeUnsuccessfulEmail(String recipientMailId, String name, String retryDays) throws InvalidInputException,
			UndeliveredEmailException;

	public void sendFatalExceptionEmail(String recipientMailId, String stackTrace) throws InvalidInputException, UndeliveredEmailException;

	public void sendVerificationMail(String url, String recipientMailId, String recipientName) throws InvalidInputException,
			UndeliveredEmailException;

	public void sendRetryChargeEmail(String recipientMailId, String displayName, String retries) throws InvalidInputException,
			UndeliveredEmailException;

	public void sendRetryExhaustedEmail(String recipientMailId, String displayName) throws InvalidInputException, UndeliveredEmailException;

	public void sendEmailSendingFailureMail(String recipientMailId, String destinationMailId, String displayName, String stackTrace)
			throws InvalidInputException, UndeliveredEmailException;

	public void sendAccountDisabledMail(String recipientMailId, String displayName) throws InvalidInputException, UndeliveredEmailException;

	public void sendAccountUpgradeMail(String recipientMailId, String displayName) throws InvalidInputException, UndeliveredEmailException;

	public void sendSurveyCompletionMail(String recipientMailId, String displayName, String agentName) throws InvalidInputException,
			UndeliveredEmailException;
}
