package com.realtech.socialsurvey.core.services.mail.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.EmailTemplateConstants;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.FileContentReplacements;
import com.realtech.socialsurvey.core.enums.EmailHeader;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailSender;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.mq.ProducerForQueue;
import com.realtech.socialsurvey.core.utils.PropertyFileReader;

// JIRA: SS-7: By RM02: BOC
/**
 * Implementation file for the email services
 */
@Component
public class EmailServicesImpl implements EmailServices {

	public static final Logger LOG = LoggerFactory.getLogger(EmailServicesImpl.class);

	@Autowired
	private ProducerForQueue queueProducer;

	@Autowired
	private EmailSender emailSender;

	@Autowired
	private PropertyFileReader propertyReader;

	@Value("${MAX_PAYMENT_RETRIES}")
	private int maxPaymentRetries;
	
	@Value("${SENDGRID_SENDER_USERNAME}")
	private String sendgridSenderUsername;
	
	@Value("${SENDGRID_SENDER_PASSWORD}")
	private String sendgridSenderPassword;
	
	@Value("${SENDGRID_SENDER_NAME}")
	private String sendgridSenderName;

	@Async
	@Override
	public void queueRegistrationInviteMail(String url, String recipientMailId, String firstName, String lastName) throws InvalidInputException {
		LOG.info("Method for queueing registration invite mail called with url : " + url + " firstName :" + firstName + " and lastName : " + lastName);
		if (url == null || url.isEmpty()) {
			LOG.error("Url is empty or null for sending registration invite mail ");
			throw new InvalidInputException("Url is empty or null for sending registration invite mail ");
		}
		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending registration invite mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending registration invite mail ");
		}
		if (firstName == null || firstName.isEmpty()) {
			LOG.error("Firstname is empty or null for sending registration invite mail ");
			throw new InvalidInputException("Firstname is empty or null for sending registration invite mail ");
		}
		// format for the registration mail is RECIPIENT^^<comman separated
		// recipients>$$URL^^<URL>$$FIRSTNAME^^<firstName>$$LASTNAME^^<lastName>
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append("RECIPIENT^^").append(recipientMailId);
		contentBuilder.append("$$").append("URL^^").append(url);
		contentBuilder.append("$$").append("FIRSTNAME^^").append(firstName);
		contentBuilder.append("$$").append("LASTNAME^^").append(lastName);
		LOG.debug("queueing content: " + contentBuilder.toString());
		queueProducer.queueEmail(EmailHeader.REGISTRATION, contentBuilder.toString());
		LOG.info("Queued the registration mail");
	}

	/**
	 * Method to send registration invite mail to a single recipient
	 * 
	 * @throws InvalidInputException
	 * @throws UndeliveredEmailException
	 */
	@Async
	@Override
	public void sendRegistrationInviteMail(String url, String recipientMailId, String firstName, String lastName) throws InvalidInputException,
			UndeliveredEmailException {
		LOG.info("Method for sending registration invite mail called with url : " + url + " firstName :" + firstName + " and lastName : " + lastName);
		if (url == null || url.isEmpty()) {
			LOG.error("Url is empty or null for sending registration invite mail ");
			throw new InvalidInputException("Url is empty or null for sending registration invite mail ");
		}
		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending registration invite mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending registration invite mail ");
		}
		if (firstName == null || firstName.isEmpty()) {
			LOG.error("Firstname is empty or null for sending registration invite mail ");
			throw new InvalidInputException("Firstname is empty or null for sending registration invite mail ");
		}

		EmailEntity emailEntity = prepareEmailEntityForSendingEmail(recipientMailId);

		String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.REGISTRATION_INVITATION_MAIL_SUBJECT;

		// Preparing full name of the recipient
		String fullName = firstName;
		if (lastName != null && !lastName.isEmpty()) {
			fullName = firstName + " " + lastName;
		}
		FileContentReplacements messageBodyReplacements = new FileContentReplacements();
		messageBodyReplacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.REGISTRATION_INVITATION_MAIL_BODY);

		/**
		 * Sequence of the replacement arguments in the list should be same as their sequence of
		 * occurrence in the template
		 */
		messageBodyReplacements.setReplacementArgs(Arrays.asList(fullName, url));

		LOG.debug("Calling email sender to send mail");
		emailSender.sendEmailWithBodyReplacements(emailEntity, subjectFileName, messageBodyReplacements);

		LOG.info("Successfully sent registration invite mail");

	}

	@Async
	@Override
	public void queueResetPasswordEmail(String url, String recipientMailId, String name) throws InvalidInputException {
		LOG.info("Method to queue Email to reset the password link with URL : " + url + "\t and Recipients Mail ID : " + recipientMailId);

		// check if the passed parameters are null or empty

		if (url == null || url.isEmpty()) {
			LOG.error("URL generated can not be null or empty");
			throw new InvalidInputException("URL generated can not be null or empty");
		}

		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipients Email Id can not be null or empty");
			throw new InvalidInputException("Recipients Email Id can not be null or empty");
		}

		if (name == null || name.isEmpty()) {
			LOG.error("Recipients Name can not be null or empty");
			throw new InvalidInputException("Recipients Name can not be null or empty");
		}
		// format for the reset password mail is RECIPIENT^^<comman separated
		// recipients>$$URL^^<URL>$$NAME^^<name>
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append("RECIPIENT^^").append(recipientMailId);
		contentBuilder.append("$$").append("URL^^").append(url);
		contentBuilder.append("$$").append("NAME^^").append(name);
		LOG.debug("queueing content: " + contentBuilder.toString());
		queueProducer.queueEmail(EmailHeader.RESET_PASSWORD, contentBuilder.toString());
		LOG.info("Queued the send reset password mail");
	}

	/**
	 * Sends a reset password link to the user
	 * 
	 * @param url
	 * @param recipientMailId
	 * @throws InvalidInputException
	 * @throws UndeliveredEmailException
	 */
	@Async
	@Override
	public void sendResetPasswordEmail(String url, String recipientMailId, String name) throws InvalidInputException, UndeliveredEmailException {
		LOG.info("Method to send Email to reset the password link with URL : " + url + "\t and Recipients Mail ID : " + recipientMailId);

		// check if the passed parameters are null or empty

		if (url == null || url.isEmpty()) {
			LOG.error("URL generated can not be null or empty");
			throw new InvalidInputException("URL generated can not be null or empty");
		}

		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipients Email Id can not be null or empty");
			throw new InvalidInputException("Recipients Email Id can not be null or empty");
		}

		if (name == null || name.isEmpty()) {
			LOG.error("Recipients Name can not be null or empty");
			throw new InvalidInputException("Recipients Name can not be null or empty");
		}
		EmailEntity emailEntity = prepareEmailEntityForSendingEmail(recipientMailId);
		String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.RESET_PASSWORD_MAIL_SUBJECT;
		FileContentReplacements messageBodyReplacements = new FileContentReplacements();
		messageBodyReplacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.RESET_PASSWORD_MAIL_BODY);

		messageBodyReplacements.setReplacementArgs(Arrays.asList(name, url));

		LOG.debug("Calling email sender to send mail");
		emailSender.sendEmailWithBodyReplacements(emailEntity, subjectFileName, messageBodyReplacements);

		LOG.info("Successfully sent reset password mail");
	}

	/**
	 * Method to prepare email entity required to send email
	 * 
	 * @param recipientMailId
	 * @return
	 */
	private EmailEntity prepareEmailEntityForSendingEmail(String recipientMailId) {
		LOG.debug("Preparing email entity for registration invitation for recipientMailId " + recipientMailId);
		List<String> recipients = new ArrayList<String>();
		recipients.add(recipientMailId);

		EmailEntity emailEntity = new EmailEntity();
		emailEntity.setRecipients(recipients);
		emailEntity.setSenderEmailId(sendgridSenderUsername);
		emailEntity.setSenderPassword(sendgridSenderPassword);
		emailEntity.setSenderName(sendgridSenderName);
		emailEntity.setRecipientType(EmailEntity.RECIPIENT_TYPE_TO);

		LOG.debug("Prepared email entity for registrationInvite");
		return emailEntity;
	}

	@Async
	@Override
	public void queueSubscriptionChargeUnsuccessfulEmail(String recipientMailId, String name, String retryDays) throws InvalidInputException {
		LOG.info("Method to send subscription charge unsuccessful mail to : " + name);
		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending unsuccessful subscription charge mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending subscription charge mail ");
		}
		if (name == null || name.isEmpty()) {
			LOG.error("Name is empty or null for sending subscription charge mail ");
			throw new InvalidInputException("Name is empty or null for sending subscription charge mail ");
		}
		// format for the subscription charge unsuccessful mail is RECIPIENT^^<comman separated
		// recipients>$$NAME^^<name>$$RETRYDAYS^^<retryDays>
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append("RECIPIENT^^").append(recipientMailId);
		contentBuilder.append("$$").append("NAME^^").append(name);
		contentBuilder.append("$$").append("RETRYDAYS^^").append(retryDays);
		LOG.debug("queueing content: " + contentBuilder.toString());
		queueProducer.queueEmail(EmailHeader.SUBSCRIPTION_CHARGE_UNSUCESSFUL, contentBuilder.toString());
		LOG.info("Queued the subscription charge unsuccessful mail");
	}

	/**
	 * Sends a mail to the user when his subscription payment fails.
	 * 
	 * @param recipientMailId
	 *            ,name,retryDays
	 * @return
	 */
	@Async
	@Override
	public void sendSubscriptionChargeUnsuccessfulEmail(String recipientMailId, String name, String retryDays) throws InvalidInputException,
			UndeliveredEmailException {

		LOG.info("Method to send subscription charge unsuccessful mail to : " + name);
		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending unsuccessful subscription charge mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending subscription charge mail ");
		}
		if (name == null || name.isEmpty()) {
			LOG.error("Name is empty or null for sending subscription charge mail ");
			throw new InvalidInputException("Name is empty or null for sending subscription charge mail ");
		}

		LOG.debug("Executing sendSubscriptionChargeUnsuccessfulEmail() with parameters : " + recipientMailId + ", " + name + ", " + retryDays);

		EmailEntity emailEntity = prepareEmailEntityForSendingEmail(recipientMailId);

		String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SUBSCRIPTION_UNSUCCESSFUL_MAIL_SUBJECT;

		/**
		 * Sequence of the replacement arguments in the list should be same as their sequence of
		 * occurrence in the template
		 */
		FileContentReplacements messageBodyReplacements = new FileContentReplacements();
		messageBodyReplacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER
				+ EmailTemplateConstants.SUBSCRIPTION_UNSUCCESSFUL_MAIL_BODY);

		messageBodyReplacements.setReplacementArgs(Arrays.asList(name, retryDays));

		LOG.info("Sending the mail.");
		emailSender.sendEmailWithBodyReplacements(emailEntity, subjectFileName, messageBodyReplacements);
		LOG.info("Mail successfully sent!");

	}

	@Async
	@Override
	public void queueEmailVerificationMail(String url, String recipientMailId, String recipientName) throws InvalidInputException {
		LOG.info("Method to queue verification mail called for url : " + url + " recipientMailId : " + recipientMailId);
		if (url == null || url.isEmpty()) {
			throw new InvalidInputException("URL generated can not be null or empty");
		}
		if (recipientMailId == null || recipientMailId.isEmpty()) {
			throw new InvalidInputException("Recipients Email Id can not be null or empty");
		}
		if (recipientName == null || recipientName.isEmpty()) {
			throw new InvalidInputException("Recipients Name can not be null or empty");
		}

		// format for the registration mail is RECIPIENT^^<comman separated
		// recipients>$$URL^^<URL>$$NAME^^<recipientName>
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append("RECIPIENT^^").append(recipientMailId);
		contentBuilder.append("$$").append("URL^^").append(url);
		contentBuilder.append("$$").append("NAME^^").append(recipientName);

		LOG.debug("queueing content: " + contentBuilder.toString());
		queueProducer.queueEmail(EmailHeader.EMAIL_VERFICATION, contentBuilder.toString());
		LOG.info("Queued the email verification mail");
	}

	/**
	 * Method to send mail with verification link to verify the account
	 * 
	 * @param url
	 * @param recipientMailId
	 * @param recipientName
	 * @throws InvalidInputException
	 * @throws UndeliveredEmailException
	 */
	@Async
	@Override
	public void sendEmailVerificationMail(String url, String recipientMailId, String recipientName) throws InvalidInputException,
			UndeliveredEmailException {
		LOG.info("Method to send verification mail called for url : " + url + " recipientMailId : " + recipientMailId);
		if (url == null || url.isEmpty()) {
			throw new InvalidInputException("URL generated can not be null or empty");
		}
		if (recipientMailId == null || recipientMailId.isEmpty()) {
			throw new InvalidInputException("Recipients Email Id can not be null or empty");
		}
		if (recipientName == null || recipientName.isEmpty()) {
			throw new InvalidInputException("Recipients Name can not be null or empty");
		}

		EmailEntity emailEntity = prepareEmailEntityForVerificationMail(recipientMailId);
		String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.EMAIL_VERIFICATION_MAIL_SUBJECT;
		FileContentReplacements fileContentReplacements = new FileContentReplacements();
		fileContentReplacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.EMAIL_VERIFICATION_MAIL_BODY);

		/**
		 * order of arguments should be same as in the template
		 */
		fileContentReplacements.setReplacementArgs(Arrays.asList(recipientName, url));
		LOG.debug("Calling email sender to send verification mail");
		emailSender.sendEmailWithBodyReplacements(emailEntity, subjectFileName, fileContentReplacements);

		LOG.info("Successfully sent verification mail");
	}

	@Async
	@Override
	public void queueVerificationMail(String url, String recipientMailId, String recipientName) throws InvalidInputException {
		LOG.info("Method to queue verification mail called for url : " + url + " recipientMailId : " + recipientMailId);

		if (url == null || url.isEmpty()) {
			throw new InvalidInputException("URL generated can not be null or empty");
		}

		if (recipientMailId == null || recipientMailId.isEmpty()) {
			throw new InvalidInputException("Recipients Email Id can not be null or empty");
		}

		if (recipientName == null || recipientName.isEmpty()) {
			throw new InvalidInputException("Recipients Name can not be null or empty");
		}

		// format for the registration mail is RECIPIENT^^<comman separated
		// recipients>$$URL^^<URL>$$NAME^^<recipientName>
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append("RECIPIENT^^").append(recipientMailId);
		contentBuilder.append("$$").append("URL^^").append(url);
		contentBuilder.append("$$").append("NAME^^").append(recipientName);
		LOG.debug("queueing content: " + contentBuilder.toString());
		queueProducer.queueEmail(EmailHeader.VERFICATION, contentBuilder.toString());
		LOG.info("Queued the verification mail");
	}

	/**
	 * Method to send mail with verification link to verify the account
	 * 
	 * @param url
	 * @param recipientMailId
	 * @param recipientName
	 * @throws InvalidInputException
	 * @throws UndeliveredEmailException
	 */
	@Async
	@Override
	public void sendVerificationMail(String url, String recipientMailId, String recipientName) throws InvalidInputException,
			UndeliveredEmailException {
		LOG.info("Method to send verification mail called for url : " + url + " recipientMailId : " + recipientMailId);

		if (url == null || url.isEmpty()) {
			throw new InvalidInputException("URL generated can not be null or empty");
		}

		if (recipientMailId == null || recipientMailId.isEmpty()) {
			throw new InvalidInputException("Recipients Email Id can not be null or empty");
		}

		if (recipientName == null || recipientName.isEmpty()) {
			throw new InvalidInputException("Recipients Name can not be null or empty");
		}

		EmailEntity emailEntity = prepareEmailEntityForVerificationMail(recipientMailId);
		String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.VERIFICATION_MAIL_SUBJECT;
		FileContentReplacements fileContentReplacements = new FileContentReplacements();
		fileContentReplacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.VERIFICATION_MAIL_BODY);

		/**
		 * order of arguments should be same as in the template
		 */
		fileContentReplacements.setReplacementArgs(Arrays.asList(recipientName, url));
		LOG.debug("Calling email sender to send verification mail");
		emailSender.sendEmailWithBodyReplacements(emailEntity, subjectFileName, fileContentReplacements);

		LOG.info("Successfully sent verification mail");
	}

	@Async
	@Override
	public void queueRegistrationCompletionEmail(String url, String recipientMailId, String name) throws InvalidInputException {
		LOG.info("Method to queue Email to complete registration link with URL : " + url + "\t and Recipients Mail ID : " + recipientMailId);

		// check if the passed parameters are null or empty

		if (url == null || url.isEmpty()) {
			LOG.error("URL generated can not be null or empty");
			throw new InvalidInputException("URL generated can not be null or empty");
		}

		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipients Email Id can not be null or empty");
			throw new InvalidInputException("Recipients Email Id can not be null or empty");
		}

		if (name == null || name.isEmpty()) {
			LOG.error("Recipients Name can not be null or empty");
			throw new InvalidInputException("Recipients Name can not be null or empty");
		}

		// format for the registration mail is RECIPIENT^^<comman separated
		// recipients>$$URL^^<URL>$$NAME^^<name>
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append("RECIPIENT^^").append(recipientMailId);
		contentBuilder.append("$$").append("URL^^").append(url);
		contentBuilder.append("$$").append("NAME^^").append(name);
		LOG.debug("queueing content: " + contentBuilder.toString());
		queueProducer.queueEmail(EmailHeader.REGISTRATION_COMPLETE, contentBuilder.toString());
		LOG.info("Queued the registration complete mail");
	}

	// JIRA SS-42 by RM-05 : BOC
	/**
	 * Sends a link to new user to complete registration.
	 * 
	 * @param url
	 * @param recipientMailId
	 * @throws InvalidInputException
	 * @throws UndeliveredEmailException
	 */
	@Async
	@Override
	public void sendRegistrationCompletionEmail(String url, String recipientMailId, String name) throws InvalidInputException,
			UndeliveredEmailException {
		LOG.info("Method to send Email to complete registration link with URL : " + url + "\t and Recipients Mail ID : " + recipientMailId);

		// check if the passed parameters are null or empty

		if (url == null || url.isEmpty()) {
			LOG.error("URL generated can not be null or empty");
			throw new InvalidInputException("URL generated can not be null or empty");
		}

		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipients Email Id can not be null or empty");
			throw new InvalidInputException("Recipients Email Id can not be null or empty");
		}

		if (name == null || name.isEmpty()) {
			LOG.error("Recipients Name can not be null or empty");
			throw new InvalidInputException("Recipients Name can not be null or empty");
		}
		EmailEntity emailEntity = prepareEmailEntityForSendingEmail(recipientMailId);
		String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.COMPLETE_REGISTRATION_MAIL_SUBJECT;
		FileContentReplacements messageBodyReplacements = new FileContentReplacements();
		messageBodyReplacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.COMPLETE_REGISTRATION_MAIL_BODY);

		messageBodyReplacements.setReplacementArgs(Arrays.asList(name, url));

		LOG.debug("Calling email sender to send mail");
		emailSender.sendEmailWithBodyReplacements(emailEntity, subjectFileName, messageBodyReplacements);

		LOG.info("Successfully sent mail for registraion completion.");
	}

	// JIRA SS-42 by RM-05 : EOC

	/**
	 * Method to prepare email entity for verification mail
	 * 
	 * @param recipientMailId
	 * @return
	 */
	private EmailEntity prepareEmailEntityForVerificationMail(String recipientMailId) {
		LOG.debug("Preparing email entity for verification mail for recipientMailId " + recipientMailId);
		List<String> recipients = new ArrayList<String>();
		recipients.add(recipientMailId);

		EmailEntity emailEntity = new EmailEntity();
		emailEntity.setRecipients(recipients);
		emailEntity.setSenderEmailId(propertyReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE, CommonConstants.SENDGRID_SENDER_USERNAME));
		emailEntity.setSenderPassword(propertyReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE, CommonConstants.SENDGRID_SENDER_PASSWORD));
		emailEntity.setSenderName(propertyReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE, CommonConstants.SENDGRID_SENDER_NAME));
		emailEntity.setRecipientType(EmailEntity.RECIPIENT_TYPE_TO);

		LOG.debug("Prepared email entity for verification mail");
		return emailEntity;
	}

	@Async
	@Override
	public void sendFatalExceptionEmail(String recipientMailId, String stackTrace) throws InvalidInputException, UndeliveredEmailException {

		LOG.info("Sending FatalException email to the admin.");

		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending fatal exception mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending fatal exception mail ");
		}

		EmailEntity emailEntity = prepareEmailEntityForSendingEmail(recipientMailId);

		String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.FATAL_EXCEPTION_MAIL_SUBJECT;

		FileContentReplacements messageBodyReplacements = new FileContentReplacements();
		messageBodyReplacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.FATAL_EXCEPTION_MAIL_BODY);

		messageBodyReplacements.setReplacementArgs(Arrays.asList(stackTrace));

		LOG.debug("Calling email sender to send mail");
		emailSender.sendEmailWithBodyReplacements(emailEntity, subjectFileName, messageBodyReplacements);

		LOG.info("Successfully sent fatal exception mail");

	}

	@Async
	@Override
	public void sendEmailSendingFailureMail(String recipientMailId, String destinationMailId, String displayName, String stackTrace)
			throws InvalidInputException, UndeliveredEmailException {

		LOG.info("Sending email to the admin on failure of sending mail to customer");

		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sendEmailSendingFailureMail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sendEmailSendingFailureMail ");
		}

		if (destinationMailId == null || destinationMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sendEmailSendingFailureMail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sendEmailSendingFailureMail ");
		}

		if (displayName == null || displayName.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sendEmailSendingFailureMail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sendEmailSendingFailureMail ");
		}

		if (stackTrace == null || stackTrace.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sendEmailSendingFailureMail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sendEmailSendingFailureMail ");
		}

		EmailEntity emailEntity = prepareEmailEntityForSendingEmail(recipientMailId);

		String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.EMAIL_SENDING_FAILURE_MAIL_SUBJECT;

		FileContentReplacements messageBodyReplacements = new FileContentReplacements();
		messageBodyReplacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.EMAIL_SENDING_FAILURE_MAIL_BODY);

		messageBodyReplacements.setReplacementArgs(Arrays.asList(displayName, destinationMailId, stackTrace));

		LOG.debug("Calling email sender to send mail");
		emailSender.sendEmailWithBodyReplacements(emailEntity, subjectFileName, messageBodyReplacements);

		LOG.info("Successfully sent EmailSendingFailureMail");

	}

	@Async
	@Override
	public void queueRetryChargeEmail(String recipientMailId, String displayName, String retries) throws InvalidInputException {
		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending retry charge mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending retry charge mail ");
		}

		if (displayName == null || displayName.isEmpty()) {
			LOG.error("displayName parameter is empty or null for sending retry charge mail ");
			throw new InvalidInputException("displayName parameter is empty or null for sending retry charge mail ");
		}

		if (retries == null || retries.isEmpty()) {
			LOG.error("retries is empty or null for sending retry charge mail ");
			throw new InvalidInputException("retries parameter is empty or null for sending retry charge mail ");
		}

		LOG.info("Queueing retry charge email to : " + recipientMailId);

		// format for the registration mail is RECIPIENT^^<comman separated
		// recipients>$$NAME^^<displayName>$$RETRIES^^<retries>
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append("RECIPIENT^^").append(recipientMailId);
		contentBuilder.append("$$").append("NAME^^").append(displayName);
		contentBuilder.append("$$").append("RETRIES^^").append(retries);
		LOG.debug("queueing content: " + contentBuilder.toString());
		queueProducer.queueEmail(EmailHeader.RETRY_CHARGE, contentBuilder.toString());
		LOG.info("Queued the retry charged mail");

	}

	@Async
	@Override
	public void sendRetryChargeEmail(String recipientMailId, String displayName, String retries) throws InvalidInputException,
			UndeliveredEmailException {

		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending retry charge mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending retry charge mail ");
		}

		if (displayName == null || displayName.isEmpty()) {
			LOG.error("displayName parameter is empty or null for sending retry charge mail ");
			throw new InvalidInputException("displayName parameter is empty or null for sending retry charge mail ");
		}

		if (retries == null || retries.isEmpty()) {
			LOG.error("retries is empty or null for sending retry charge mail ");
			throw new InvalidInputException("retries parameter is empty or null for sending retry charge mail ");
		}

		LOG.info("Sending retry charge email to : " + recipientMailId);

		EmailEntity emailEntity = prepareEmailEntityForSendingEmail(recipientMailId);

		String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.RETRY_CHARGE_MAIL_SUBJECT;

		FileContentReplacements messageBodyReplacements = new FileContentReplacements();
		messageBodyReplacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.RETRY_CHARGE_MAIL_BODY);

		messageBodyReplacements.setReplacementArgs(Arrays.asList(displayName, retries, String.valueOf(maxPaymentRetries)));

		LOG.debug("Calling email sender to send mail");
		emailSender.sendEmailWithBodyReplacements(emailEntity, subjectFileName, messageBodyReplacements);

		LOG.info("Successfully sent retry charge mail");

	}

	@Async
	@Override
	public void queueRetryExhaustedEmail(String recipientMailId, String displayName) throws InvalidInputException {
		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending retries exhausted mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending retries exhausted mail ");
		}

		if (displayName == null || displayName.isEmpty()) {
			LOG.error("displayName parameter is empty or null for sending retry exhausted mail ");
			throw new InvalidInputException("displayName parameter is empty or null for sending retry exhausted mail ");
		}

		LOG.info("Queueing retries exhausted email to : " + recipientMailId);

		// format for the retry exhausted mail is RECIPIENT^^<comman separated
		// recipients>$$NAME^^<displayName>
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append("RECIPIENT^^").append(recipientMailId);
		contentBuilder.append("$$").append("NAME^^").append(displayName);
		LOG.debug("queueing content: " + contentBuilder.toString());
		queueProducer.queueEmail(EmailHeader.RETRY_EXHAUSTED, contentBuilder.toString());
		LOG.info("Queued the retry charged mail");

	}

	@Async
	@Override
	public void sendRetryExhaustedEmail(String recipientMailId, String displayName) throws InvalidInputException, UndeliveredEmailException {

		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending retries exhausted mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending retries exhausted mail ");
		}

		if (displayName == null || displayName.isEmpty()) {
			LOG.error("displayName parameter is empty or null for sending retry exhausted mail ");
			throw new InvalidInputException("displayName parameter is empty or null for sending retry exhausted mail ");
		}

		LOG.info("Sending retries exhausted email to : " + recipientMailId);

		EmailEntity emailEntity = prepareEmailEntityForSendingEmail(recipientMailId);

		String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.RETRIES_EXHAUSTED_MAIL_SUBJECT;

		FileContentReplacements messageBodyReplacements = new FileContentReplacements();
		messageBodyReplacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.RETRIES_EXHAUSTED_MAIL_BODY);

		messageBodyReplacements.setReplacementArgs(Arrays.asList(displayName));

		LOG.debug("Calling email sender to send mail");
		emailSender.sendEmailWithBodyReplacements(emailEntity, subjectFileName, messageBodyReplacements);

		LOG.info("Successfully sent retries exhausted mail");

	}

	@Async
	@Override
	public void queueAccountDisabledMail(String recipientMailId, String displayName) throws InvalidInputException {
		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending retries exhausted mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending retries exhausted mail ");
		}

		if (displayName == null || displayName.isEmpty()) {
			LOG.error("displayName parameter is empty or null for sending retry exhausted mail ");
			throw new InvalidInputException("displayName parameter is empty or null for sending retry exhausted mail ");
		}

		LOG.info("Queueing account disabled email to : " + recipientMailId);
		// format for the account disabled mail is RECIPIENT^^<comman separated
		// recipients>$$NAME^^<displayName>
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append("RECIPIENT^^").append(recipientMailId);
		contentBuilder.append("$$").append("NAME^^").append(displayName);
		LOG.debug("queueing content: " + contentBuilder.toString());
		queueProducer.queueEmail(EmailHeader.ACCOUNT_DISABLED, contentBuilder.toString());
		LOG.info("Queued the account disabled mail");
	}

	@Async
	@Override
	public void sendAccountDisabledMail(String recipientMailId, String displayName) throws InvalidInputException, UndeliveredEmailException {

		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending retries exhausted mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending retries exhausted mail ");
		}

		if (displayName == null || displayName.isEmpty()) {
			LOG.error("displayName parameter is empty or null for sending retry exhausted mail ");
			throw new InvalidInputException("displayName parameter is empty or null for sending retry exhausted mail ");
		}

		LOG.info("Sending account disabled email to : " + recipientMailId);

		EmailEntity emailEntity = prepareEmailEntityForSendingEmail(recipientMailId);

		String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ACCOUNT_DISABLED_MAIL_SUBJECT;

		FileContentReplacements messageBodyReplacements = new FileContentReplacements();
		messageBodyReplacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ACCOUNT_DISABLED_MAIL_BODY);

		messageBodyReplacements.setReplacementArgs(Arrays.asList(displayName));

		LOG.debug("Calling email sender to send mail");
		emailSender.sendEmailWithBodyReplacements(emailEntity, subjectFileName, messageBodyReplacements);

		LOG.info("Successfully sent account disabled mail");

	}

	@Async
	@Override
	public void queueAccountUpgradeMail(String recipientMailId, String displayName) throws InvalidInputException {
		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending account upgrade mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending account upgrade mail ");
		}

		if (displayName == null || displayName.isEmpty()) {
			LOG.error("displayName parameter is empty or null for sending account upgrade mail ");
			throw new InvalidInputException("displayName parameter is empty or null for sending account upgrade mail ");
		}

		LOG.info("Queueing account upgrade email to : " + recipientMailId);

		// format for the account upgrade mail is RECIPIENT^^<comman separated
		// recipients>$$NAME^^<displayName>
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append("RECIPIENT^^").append(recipientMailId);
		contentBuilder.append("$$").append("NAME^^").append(displayName);
		LOG.debug("queueing content: " + contentBuilder.toString());
		queueProducer.queueEmail(EmailHeader.ACCOUNT_UPGRADE, contentBuilder.toString());
		LOG.info("Queued the account upgrade mail");

	}

	@Async
	@Override
	public void sendAccountUpgradeMail(String recipientMailId, String displayName) throws InvalidInputException, UndeliveredEmailException {

		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending account upgrade mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending account upgrade mail ");
		}

		if (displayName == null || displayName.isEmpty()) {
			LOG.error("displayName parameter is empty or null for sending account upgrade mail ");
			throw new InvalidInputException("displayName parameter is empty or null for sending account upgrade mail ");
		}

		LOG.info("Sending account upgrade email to : " + recipientMailId);

		EmailEntity emailEntity = prepareEmailEntityForSendingEmail(recipientMailId);

		String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ACCOUNT_UPGRADE_MAIL_SUBJECT;

		FileContentReplacements messageBodyReplacements = new FileContentReplacements();
		messageBodyReplacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.ACCOUNT_UPGRADE_MAIL_BODY);

		messageBodyReplacements.setReplacementArgs(Arrays.asList(displayName));

		LOG.debug("Calling email sender to send mail");
		emailSender.sendEmailWithBodyReplacements(emailEntity, subjectFileName, messageBodyReplacements);

		LOG.info("Successfully sent account upgrade mail");
	}

	@Async
	@Override
	public void queueSurveyCompletionMail(String recipientMailId, String displayName, String agentName) throws InvalidInputException {
		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending survey completion mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending survey completion mail ");
		}

		if (displayName == null || displayName.isEmpty()) {
			LOG.error("displayName parameter is empty or null for sending account upgrade mail ");
			throw new InvalidInputException("displayName parameter is empty or null for sending survey completion mail ");
		}

		LOG.info("Queueing survey completion email to : " + recipientMailId);

		// format for the survey complete mail is RECIPIENT^^<comman separated
		// recipients>$$NAME^^<displayName>$$AGENTNAME^^<agentName>
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append("RECIPIENT^^").append(recipientMailId);
		contentBuilder.append("$$").append("NAME^^").append(displayName);
		contentBuilder.append("$$").append("AGENTNAME^^").append(agentName);
		LOG.debug("queueing content: " + contentBuilder.toString());
		queueProducer.queueEmail(EmailHeader.ACCOUNT_UPGRADE, contentBuilder.toString());
		LOG.info("Queued the survey completion mail");

	}

	@Async
	@Override
	public void sendSurveyCompletionMail(String recipientMailId, String displayName, String agentName) throws InvalidInputException,
			UndeliveredEmailException {

		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending survey completion mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending survey completion mail ");
		}

		if (displayName == null || displayName.isEmpty()) {
			LOG.error("displayName parameter is empty or null for sending account upgrade mail ");
			throw new InvalidInputException("displayName parameter is empty or null for sending survey completion mail ");
		}

		LOG.info("Sending survey completion email to : " + recipientMailId);

		EmailEntity emailEntity = prepareEmailEntityForSendingEmail(recipientMailId);

		String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLETION_MAIL_SUBJECT;

		FileContentReplacements messageBodyReplacements = new FileContentReplacements();
		messageBodyReplacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLETION_MAIL_BODY);

		messageBodyReplacements.setReplacementArgs(Arrays.asList(displayName, agentName));

		LOG.debug("Calling email sender to send mail");
		emailSender.sendEmailWithBodyReplacements(emailEntity, subjectFileName, messageBodyReplacements);

		LOG.info("Successfully sent survey completion mail");
	}

	@Async
	@Override
	public void queueSurveyReminderMail(String recipientMailId, String displayName, String agentName, String link) throws InvalidInputException {
		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending survey completion mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending survey completion mail ");
		}

		if (displayName == null || displayName.isEmpty()) {
			LOG.error("displayName parameter is empty or null for sending account upgrade mail ");
			throw new InvalidInputException("displayName parameter is empty or null for sending survey completion mail ");
		}

		LOG.info("Queueing survey reminder email to : " + recipientMailId);

		// format for the survey complete mail is RECIPIENT^^<comman separated
		// recipients>$$NAME^^<displayName>$$AGENTNAME^^<agentName>
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append("RECIPIENT^^").append(recipientMailId);
		contentBuilder.append("$$").append("NAME^^").append(displayName);
		contentBuilder.append("$$").append("AGENTNAME^^").append(agentName);
		contentBuilder.append("$$").append("LINK^^").append(link);
		LOG.debug("queueing content: " + contentBuilder.toString());
		queueProducer.queueEmail(EmailHeader.ACCOUNT_UPGRADE, contentBuilder.toString());
		LOG.info("Queued the survey completion mail");
	}

	@Async
	@Override
	public void sendSurveyReminderMail(String recipientMailId, String displayName, String agentName, String link) throws InvalidInputException,
			UndeliveredEmailException {

		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending survey completion mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending survey completion mail ");
		}

		if (displayName == null || displayName.isEmpty()) {
			LOG.error("displayName parameter is empty or null for sending account upgrade mail ");
			throw new InvalidInputException("displayName parameter is empty or null for sending survey completion mail ");
		}

		LOG.info("Sending survey reminder email to : " + recipientMailId);

		EmailEntity emailEntity = prepareEmailEntityForSendingEmail(recipientMailId);

		String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_REMINDER_MAIL_SUBJECT;

		FileContentReplacements messageBodyReplacements = new FileContentReplacements();
		messageBodyReplacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_REMINDER_MAIL_BODY);

		messageBodyReplacements.setReplacementArgs(Arrays.asList(displayName, agentName, link));

		LOG.debug("Calling email sender to send mail");
		emailSender.sendEmailWithBodyReplacements(emailEntity, subjectFileName, messageBodyReplacements);

		LOG.info("Successfully sent survey completion mail");
	}

	@Async
	@Override
	public void queueSurveyCompletionMailToAdmins(String recipientMailId, String customerName, String agentName, String mood)
			throws InvalidInputException {
		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending survey completion mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending survey completion mail ");
		}

		if (customerName == null || customerName.isEmpty()) {
			LOG.error("displayName parameter is empty or null for sending account upgrade mail ");
			throw new InvalidInputException("displayName parameter is empty or null for sending survey completion mail ");
		}

		LOG.info("Queueing survey completion email to : " + recipientMailId);

		// format for the survey complete mail is RECIPIENT^^<comman separated
		// recipients>$$NAME^^<displayName>$$AGENTNAME^^<agentName>
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append("RECIPIENT^^").append(recipientMailId);
		contentBuilder.append("$$").append("NAME^^").append(customerName);
		contentBuilder.append("$$").append("AGENTNAME^^").append(agentName);
		LOG.debug("queueing content: " + contentBuilder.toString());
		queueProducer.queueEmail(EmailHeader.ACCOUNT_UPGRADE, contentBuilder.toString());
		LOG.info("Queued the survey completion mail");

	}

	@Async
	@Override
	public void sendSurveyCompletionMailToAdmins(String recipientMailId, String customerName, String agentName, String mood)
			throws InvalidInputException, UndeliveredEmailException {

		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending survey completion mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending survey completion mail ");
		}

		if (customerName == null || customerName.isEmpty()) {
			LOG.error("displayName parameter is empty or null for sending account upgrade mail ");
			throw new InvalidInputException("displayName parameter is empty or null for sending survey completion mail ");
		}

		LOG.info("Sending survey completion email to : " + recipientMailId);

		EmailEntity emailEntity = prepareEmailEntityForSendingEmail(recipientMailId);

		String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLETION_ADMINS_MAIL_SUBJECT;

		FileContentReplacements messageBodyReplacements = new FileContentReplacements();
		messageBodyReplacements
				.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SURVEY_COMPLETION_ADMINS_MAIL_BODY);

		messageBodyReplacements.setReplacementArgs(Arrays.asList(customerName, agentName, mood));

		LOG.debug("Calling email sender to send mail");
		emailSender.sendEmailWithBodyReplacements(emailEntity, subjectFileName, messageBodyReplacements);

		LOG.info("Successfully sent survey completion mail");
	}
	
	@Async
	@Override
	public void queueSocialPostReminderMail(String recipientMailId, String displayName, String agentName, String links) throws InvalidInputException {
		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending survey completion mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending survey completion mail ");
		}

		if (displayName == null || displayName.isEmpty()) {
			LOG.error("displayName parameter is empty or null for sending account upgrade mail ");
			throw new InvalidInputException("displayName parameter is empty or null for sending survey completion mail ");
		}

		LOG.info("Queueing survey reminder email to : " + recipientMailId);

		// format for the survey complete mail is RECIPIENT^^<comman separated
		// recipients>$$NAME^^<displayName>$$AGENTNAME^^<agentName>
		StringBuilder contentBuilder = new StringBuilder();
		contentBuilder.append("RECIPIENT^^").append(recipientMailId);
		contentBuilder.append("$$").append("NAME^^").append(displayName);
		contentBuilder.append("$$").append("AGENTNAME^^").append(agentName);
		LOG.debug("queueing content: " + contentBuilder.toString());
		queueProducer.queueEmail(EmailHeader.ACCOUNT_UPGRADE, contentBuilder.toString());
		LOG.info("Queued the survey completion mail");

	}

	@Async
	@Override
	public void sendSocialPostReminderMail(String recipientMailId, String displayName, String agentName, String links) throws InvalidInputException,
			UndeliveredEmailException {

		if (recipientMailId == null || recipientMailId.isEmpty()) {
			LOG.error("Recipient email Id is empty or null for sending survey completion mail ");
			throw new InvalidInputException("Recipient email Id is empty or null for sending survey completion mail ");
		}

		if (displayName == null || displayName.isEmpty()) {
			LOG.error("displayName parameter is empty or null for sending account upgrade mail ");
			throw new InvalidInputException("displayName parameter is empty or null for sending survey completion mail ");
		}

		LOG.info("Sending survey reminder email to : " + recipientMailId);

		EmailEntity emailEntity = prepareEmailEntityForSendingEmail(recipientMailId);

		String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SOCIALPOST_REMINDER_MAIL_SUBJECT;

		FileContentReplacements messageBodyReplacements = new FileContentReplacements();
		messageBodyReplacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.SOCIALPOST_REMINDER_MAIL_BODY);

		messageBodyReplacements.setReplacementArgs(Arrays.asList(displayName, agentName));

		LOG.debug("Calling email sender to send mail");
		emailSender.sendEmailWithBodyReplacements(emailEntity, subjectFileName, messageBodyReplacements);

		LOG.info("Successfully sent survey completion mail");
	}
	
	/**
	 * Sends the message from the contact us page as a mail to the respective admin or agent
	 * @param recipientEmailId
	 * @param displayName
	 * @param senderEmailId
	 * @param message
	 * @throws InvalidInputException
	 * @throws UndeliveredEmailException
	 */
	@Async
	@Override
	public void sendContactUsMail(String recipientEmailId, String displayName, String senderName,
			String senderEmailId, String message) throws InvalidInputException, UndeliveredEmailException {
		
		if( recipientEmailId == null || recipientEmailId.isEmpty()){
			LOG.error("Recipient email id is null or empty!");
			throw new InvalidInputException("Recipient email id is null or empty!");
		}
		if( displayName == null || displayName.isEmpty()){
			LOG.error("displayName is null or empty!");
			throw new InvalidInputException("displayName is null or empty!");
		}
		if( senderName == null || senderName.isEmpty()){
			LOG.error("senderName is null or empty!");
			throw new InvalidInputException("senderName is null or empty!");
		}
		if( senderEmailId == null || senderEmailId.isEmpty()){
			LOG.error("senderEmailId is null or empty!");
			throw new InvalidInputException("senderEmailId is null or empty!");
		}
		if( message == null || message.isEmpty()){
			LOG.error("message is null or empty!");
			throw new InvalidInputException("message is null or empty!");
		}		
		
		LOG.info("Sending contact us email to : " + recipientEmailId);

		EmailEntity emailEntity = prepareEmailEntityForSendingEmail(recipientEmailId);

		String subjectFileName = EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.CONTACT_US_MAIL_SUBJECT;

		FileContentReplacements messageBodyReplacements = new FileContentReplacements();
		messageBodyReplacements.setFileName(EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.CONTACT_US_MAIL_BODY);

		messageBodyReplacements.setReplacementArgs(Arrays.asList(displayName,senderName,senderEmailId,message));

		LOG.debug("Calling email sender to send mail");
		emailSender.sendEmailWithBodyReplacements(emailEntity, subjectFileName, messageBodyReplacements);

		LOG.info("Successfully sent contact us mail");
	}

}
// JIRA: SS-7: By RM02: EOC