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

	public void sendSubscriptionChargeUnsuccessfulEmail(String recipientMailId,String name,String retryDays) throws InvalidInputException, UndeliveredEmailException;
	
	public void sendFatalExceptionEmail(String recipientMailId,String stackTrace) throws InvalidInputException, UndeliveredEmailException;
	public void sendVerificationMail(String url, String recipientMailId, String recipientName) throws InvalidInputException,
	UndeliveredEmailException;

	public void sendRetryChargeEmail(String recipientMailId, String displayName, String retries) throws InvalidInputException, UndeliveredEmailException;

	public void sendRetryExhaustedEmail(String recipientMailId, String displayName) throws InvalidInputException, UndeliveredEmailException;
	
	public void sendEmailSendingFailureMail(String recipientMailId,String destinationMailId,String displayName,String stackTrace) throws InvalidInputException, UndeliveredEmailException;

}
