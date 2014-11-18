package com.realtech.socialsurvey.core.services.mail;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.SmtpSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

// JIRA: SS-7: By RM02: BOC

/**
 * Class with utility methods to send mails
 */
public final class EmailSender {

	private static final Logger LOG = LoggerFactory.getLogger(EmailSender.class);

	/**
	 * Method to send mail with provided email entity and smtp settings
	 * 
	 * @param emailEntity
	 * @param smtpSettings
	 * @throws InvalidInputException
	 * @throws UndeliveredEmailException
	 */
	public void sendMail(EmailEntity emailEntity, SmtpSettings smtpSettings) throws InvalidInputException, UndeliveredEmailException {
		if (emailEntity == null) {
			throw new InvalidInputException("Email entity is null for sending mail");
		}
		if (smtpSettings == null) {
			throw new InvalidInputException("Smtp settings is null for sending mail");
		}
		LOG.info("Method sendMail called with smtpSettings : " + smtpSettings + " and emailEntity : " + emailEntity);

		int port = smtpSettings.getMailPort();

		if (emailEntity.getSenderEmailId() == null || emailEntity.getSenderEmailId().isEmpty()) {
			throw new InvalidInputException("Sender email id is not valid for sending mail");
		}
		if (emailEntity.getSenderName() == null || emailEntity.getSenderName().isEmpty()) {
			throw new InvalidInputException("Sender name is not valid for sending mail");
		}
		if (emailEntity.getSenderPassword() == null || emailEntity.getSenderPassword().isEmpty()) {
			throw new InvalidInputException("Sender password is not valid for sending mail");
		}
		List<String> recipients = emailEntity.getRecipients();
		if (recipients == null || recipients.isEmpty()) {
			throw new InvalidInputException("Recipient list is empty for sending mail");
		}

		Properties properties = new Properties();
		properties.put("mail.smtp.auth", SmtpSettings.MAIL_SMTP_AUTH);
		properties.put("mail.smtp.starttls.enable", SmtpSettings.MAIL_SMTP_STARTTLS_ENABLE);
		LOG.debug("Preparing session object for sending mail");
		Session mailSession = Session.getInstance(properties);
		try {
			LOG.debug("Preparing transport object for sending mail");
			Transport transport = mailSession.getTransport(SmtpSettings.MAIL_TRANSPORT);
			transport.connect(smtpSettings.getMailHost(), port, emailEntity.getSenderEmailId(), emailEntity.getSenderPassword());
			LOG.trace("Connection successful");
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

			// Setting up new MimeMessage
			Message message = new MimeMessage(mailSession);
			message.setFrom(new InternetAddress(emailEntity.getSenderEmailId(), emailEntity.getSenderName()));

			// Adding the recipients addresses for sending mail as per the recipient type
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
	}
	
}
