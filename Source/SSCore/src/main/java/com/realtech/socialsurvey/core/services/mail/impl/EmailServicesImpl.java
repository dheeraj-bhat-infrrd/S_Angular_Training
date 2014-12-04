package com.realtech.socialsurvey.core.services.mail.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.EmailTemplateConstants;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.FileContentReplacements;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailSender;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.utils.PropertyFileReader;

// JIRA: SS-7: By RM02: BOC
/**
 * Implementation file for the email services
 */
@Component
public class EmailServicesImpl implements EmailServices {

	public static final Logger LOG = LoggerFactory.getLogger(EmailServicesImpl.class);

	@Autowired
	private EmailSender emailSender;

	@Autowired
	private PropertyFileReader propertyReader;

	/**
	 * Method to send registration invite mail to a single recipient
	 * 
	 * @throws InvalidInputException
	 * @throws UndeliveredEmailException
	 */
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

		EmailEntity emailEntity = prepareEmailEntityForRegistrationInvite(recipientMailId);

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

	/**
	 * Sends a reset password link to the user
	 * @param url
	 * @param recipientMailId
	 * @throws InvalidInputException
	 * @throws UndeliveredEmailException
	 */
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
		EmailEntity emailEntity = prepareEmailEntityForRegistrationInvite(recipientMailId);
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
	private EmailEntity prepareEmailEntityForRegistrationInvite(String recipientMailId) {
		LOG.debug("Preparing email entity for registration invitation for recipientMailId " + recipientMailId);
		List<String> recipients = new ArrayList<String>();
		recipients.add(recipientMailId);

		EmailEntity emailEntity = new EmailEntity();
		emailEntity.setRecipients(recipients);
		emailEntity.setSenderEmailId(propertyReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE, CommonConstants.SENDGRID_SENDER_USERNAME));
		emailEntity.setSenderPassword(propertyReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE, CommonConstants.SENDGRID_SENDER_PASSWORD));
		emailEntity.setSenderName(propertyReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE, CommonConstants.SENDGRID_SENDER_NAME));
		emailEntity.setRecipientType(EmailEntity.RECIPIENT_TYPE_TO);

		LOG.debug("Prepared email entity for registrationInvite");
		return emailEntity;
	}
}
// JIRA: SS-7: By RM02: EOC