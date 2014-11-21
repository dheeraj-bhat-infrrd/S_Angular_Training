package com.realtech.socialsurvey.core.services.mail.impl;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.SmtpSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailSender;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;

// JIRA: SS-7: By RM02: BOC

/**
 * Class with utility methods to send mails
 */
@Component
public final class EmailSenderImpl implements EmailSender {

	private static final Logger LOG = LoggerFactory.getLogger(EmailSenderImpl.class);

	@Autowired
	private SmtpSettings smtpSettings;

	/**
	 * Method to send mail with provided email entity and smtp settings
	 * 
	 * @param emailEntity
	 * @param smtpSettings
	 * @throws InvalidInputException
	 * @throws UndeliveredEmailException
	 */
	@Override
	public void sendMail(EmailEntity emailEntity, String fileNameForMessageSubject, String fileNameForMessageBody) throws InvalidInputException, UndeliveredEmailException {
		if (emailEntity == null) {
			LOG.error("Email entity is null for sending mail");
			throw new InvalidInputException("Email entity is null for sending mail");
		}
		if (smtpSettings == null) {
			LOG.error("Smtp settings is null for sending mail");
			throw new InvalidInputException("Smtp settings is null for sending mail");
		}
		LOG.info("Method sendMail called with smtpSettings : " + smtpSettings + " and emailEntity : " + emailEntity);

		int port = smtpSettings.getMailPort();

		if (emailEntity.getSenderEmailId() == null || emailEntity.getSenderEmailId().isEmpty()) {
			LOG.error("Sender email id is not valid for sending mail");
			throw new InvalidInputException("Sender email id is not valid for sending mail");
		}
		if (emailEntity.getSenderName() == null || emailEntity.getSenderName().isEmpty()) {
			LOG.error("Sender name is not valid for sending mail");
			throw new InvalidInputException("Sender name is not valid for sending mail");
		}
		if (emailEntity.getSenderPassword() == null || emailEntity.getSenderPassword().isEmpty()) {
			LOG.error("Sender password is not valid for sending mail");
			throw new InvalidInputException("Sender password is not valid for sending mail");
		}
		List<String> recipients = emailEntity.getRecipients();
		if (recipients == null || recipients.isEmpty()) {
			LOG.error("Recipient list is empty for sending mail");
			throw new InvalidInputException("Recipient list is empty for sending mail");
		}
		// check if subject needs to be read from a file
		if (fileNameForMessageSubject != null && !fileNameForMessageSubject.isEmpty()) {
			emailEntity.setSubject(readSubjectFromFile());
		}
		
		if(fileNameForMessageBody != null && !fileNameForMessageBody.isEmpty()){
			emailEntity.setBody(readBodyFromFile());
		}
		
		// Create the session object
		Session session = createSession();
				
		try {
			LOG.debug("Preparing transport object for sending mail");
			Transport transport = session.getTransport(SmtpSettings.MAIL_TRANSPORT);
			transport.connect(smtpSettings.getMailHost(), port, emailEntity.getSenderEmailId(), emailEntity.getSenderPassword());
			LOG.trace("Connection successful");

			// Adding the recipients to address list
			Address[] addresses = createRecipientAddresses(recipients);

			// Setting up new MimeMessage
			Message message = createMessage(emailEntity, session, addresses);
			// Send the mail
			LOG.debug("Mail to be sent : " + emailEntity.getBody());
			transport.sendMessage(message, addresses);
			transport.close();

			LOG.info("Mail sent successfully. Returning from method sendMail");
		}
		catch (MessagingException e) {
			LOG.error("Messaging exception while sending mail", e);
			throw new UndeliveredEmailException("Error while sending email", e);
		}
		catch (UnsupportedEncodingException e) {
			LOG.error("Unsupported Encoding Exception while sending mail", e);
			throw new UndeliveredEmailException("Error while sending email", e);
		}
		catch (IllegalStateException e) {
			LOG.error("Illegal State Exception while sending mail", e);
			throw new UndeliveredEmailException("Illegal State Exception while sending mail", e);
		}
	}

	// creates a session object
	private Session createSession() {
		LOG.debug("Preparing session object for sending mail");
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", SmtpSettings.MAIL_SMTP_AUTH);
		properties.put("mail.smtp.starttls.enable", SmtpSettings.MAIL_SMTP_STARTTLS_ENABLE);
		Session mailSession = Session.getInstance(properties);
		LOG.debug("Returning the session object");
		return mailSession;
	}

	// creates addresses from the recipient list
	private Address[] createRecipientAddresses(List<String> recipients) throws AddressException {
		LOG.debug("Creating recipient addresses");
		StringBuilder recipientsSb = new StringBuilder();
		int count = 0;
		for (String recipientEmailId : recipients) {
			if (count != 0) {
				recipientsSb.append(",");
			}
			LOG.debug("Adding recipient : " + recipientEmailId);
			recipientsSb.append(recipientEmailId);
			count++;
		}
		LOG.debug("Recipients are : " + recipientsSb);

		// Adding the recipients to address list
		Address[] addresses = InternetAddress.parse(recipientsSb.toString());
		return addresses;
	}

	// created a Mime Message
	private Message createMessage(EmailEntity emailEntity, Session session, Address[] addresses) throws UnsupportedEncodingException, MessagingException,
			InvalidInputException {
		LOG.debug("Creating message");
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(emailEntity.getSenderEmailId(), emailEntity.getSenderName()));

		// Adding the recipients addresses for sending mail as per the
		// recipient type
		if (emailEntity.getRecipientType() == EmailEntity.RECIPIENT_TYPE_TO) {
			message.setRecipients(Message.RecipientType.TO, addresses);
		}
		else if (emailEntity.getRecipientType() == EmailEntity.RECIPIENT_TYPE_CC) {
			message.setRecipients(Message.RecipientType.CC, addresses);
		}
		else if (emailEntity.getRecipientType() == EmailEntity.RECIPIENT_TYPE_BCC) {
			message.setRecipients(Message.RecipientType.BCC, addresses);
		}
		else {
			LOG.error("Recipients type is not specified for sending mail");
			throw new InvalidInputException("Invalid recipient type found for sending mail");
		}

		// Set the subject of mail
		message.setSubject(emailEntity.getSubject());

		// Set the mail body
		message.setContent(emailEntity.getBody(), "text/html");
		return message;
	}

	private String readSubjectFromFile() {

		return null;
	}

	private String readBodyFromFile() {

		return null;
	}

}
