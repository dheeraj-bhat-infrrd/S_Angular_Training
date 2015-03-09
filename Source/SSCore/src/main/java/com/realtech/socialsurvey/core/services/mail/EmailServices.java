package com.realtech.socialsurvey.core.services.mail;

import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * Services for sending mails via application
 */
public interface EmailServices {

	/**
	 * Queues the registration invite mail
	 * @param url
	 * @param recipientMailId
	 * @param firstname
	 * @param lastName
	 * @throws InvalidInputException
	 */
	public void queueRegistrationInviteMail(String url, String recipientMailId, String firstName, String lastName) throws InvalidInputException;
	
	public void sendRegistrationInviteMail(String url, String recipientMailId, String firstname, String lastName) throws InvalidInputException,
			UndeliveredEmailException;

	
	/**
	 * Queues the reset password email
	 * @param url
	 * @param recipientMailId
	 * @param name
	 * @throws InvalidInputException
	 */
	public void queueResetPasswordEmail(String url, String recipientMailId, String name) throws InvalidInputException;
	// JIRA : SS-30 by RM-06

	public void sendResetPasswordEmail(String url, String recipientMailId, String name) throws InvalidInputException, UndeliveredEmailException;

	/**
	 * Queues the registration complete email
	 * @param url
	 * @param recipientMailId
	 * @param name
	 * @throws InvalidInputException
	 */
	public void queueRegistrationCompletionEmail(String url, String recipientMailId, String name) throws InvalidInputException;
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

	/**
	 * queues sunscription charge unsucessful email
	 * @param recipientMailId
	 * @param name
	 * @param retryDays
	 * @throws InvalidInputException
	 */
	public void queueSubscriptionChargeUnsuccessfulEmail(String recipientMailId, String name, String retryDays) throws InvalidInputException;
	// JIRA SS-42 by RM05 : EOC

	public void sendSubscriptionChargeUnsuccessfulEmail(String recipientMailId, String name, String retryDays) throws InvalidInputException,
			UndeliveredEmailException;

	public void sendFatalExceptionEmail(String recipientMailId, String stackTrace) throws InvalidInputException, UndeliveredEmailException;

	/**
	 * Queues the verification mail
	 * @param url
	 * @param recipientMailId
	 * @param recipientName
	 * @throws InvalidInputException
	 */
	public void queueVerificationMail(String url, String recipientMailId, String recipientName) throws InvalidInputException;
	
	public void sendVerificationMail(String url, String recipientMailId, String recipientName) throws InvalidInputException,
			UndeliveredEmailException;

	/**
	 * Queues the email verification mail
	 * @param url
	 * @param recipientMailId
	 * @param recipientName
	 * @throws InvalidInputException
	 */
	public void queueEmailVerificationMail(String url, String recipientMailId, String recipientName) throws InvalidInputException;

	public void sendEmailVerificationMail(String url, String recipientMailId, String recipientName) throws InvalidInputException,
	UndeliveredEmailException;
	
	/**
	 * Queues the retry charge email
	 * @param recipientMailId
	 * @param displayName
	 * @param retries
	 * @throws InvalidInputException
	 */
	public void queueRetryChargeEmail(String recipientMailId, String displayName, String retries) throws InvalidInputException;
	
	public void sendRetryChargeEmail(String recipientMailId, String displayName, String retries) throws InvalidInputException,
			UndeliveredEmailException;

	/**
	 * Queues the retry exhausted mail
	 * @param recipientMailId
	 * @param displayName
	 * @throws InvalidInputException
	 */
	public void queueRetryExhaustedEmail(String recipientMailId, String displayName) throws InvalidInputException;
	
	public void sendRetryExhaustedEmail(String recipientMailId, String displayName) throws InvalidInputException, UndeliveredEmailException;

	public void sendEmailSendingFailureMail(String recipientMailId, String destinationMailId, String displayName, String stackTrace)
			throws InvalidInputException, UndeliveredEmailException;

	/**
	 * Queues the account disabled mail
	 * @param recipientMailId
	 * @param displayName
	 * @throws InvalidInputException
	 */
	public void queueAccountDisabledMail(String recipientMailId, String displayName) throws InvalidInputException;
	
	public void sendAccountDisabledMail(String recipientMailId, String displayName) throws InvalidInputException, UndeliveredEmailException;
	
	/**
	 * Queues the account upgrade mail
	 * @param recipientMailId
	 * @param displayName
	 * @throws InvalidInputException
	 */
	public void queueAccountUpgradeMail(String recipientMailId, String displayName) throws InvalidInputException;

	public void sendAccountUpgradeMail(String recipientMailId, String displayName) throws InvalidInputException, UndeliveredEmailException;

	/**
	 * Queues the survey complete mail
	 * @param recipientMailId
	 * @param displayName
	 * @param agentName
	 * @throws InvalidInputException
	 */
	public void queueSurveyCompletionMail(String recipientMailId, String displayName, String agentName) throws InvalidInputException;
	
	public void sendSurveyCompletionMail(String recipientMailId, String displayName, String agentName) throws InvalidInputException,
			UndeliveredEmailException;
}
