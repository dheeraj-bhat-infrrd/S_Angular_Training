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

	private EmailSender() {}

	/**
	 * Method to send mail with provided email entity and smtp settings
	 * 
	 * @param emailEntity
	 * @param smtpSettings
	 * @throws InvalidInputException
	 * @throws UndeliveredEmailException
	 */
	public static void sendMail(EmailEntity emailEntity, SmtpSettings smtpSettings) throws InvalidInputException, UndeliveredEmailException {
		LOG.info("Method sendMail called with smtpSettings : " + smtpSettings + " and emailEntity : " + emailEntity);
		int port = -1;
		try {
			port = Integer.parseInt(smtpSettings.getMailPort());
		}
		catch (NumberFormatException e) {
			LOG.error("Error in sendMail while parsing port .Reason :" + e.getMessage(), e);
			throw new InvalidInputException("Invalid port number while preparing mail to send", e);
		}
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", SmtpSettings.MAIL_SMTP_AUTH);
		properties.put("mail.smtp.starttls.enable", SmtpSettings.MAIL_SMTP_STARTTLS_ENABLE);
		Session mailSession = Session.getInstance(properties);
		try {
			Transport transport = mailSession.getTransport(SmtpSettings.MAIL_TRANSPORT);
			transport.connect(smtpSettings.getMailHost(), port, emailEntity.getSenderEmailId(), emailEntity.getSenderPassword());
			List<String> recipients = emailEntity.getRecipients();
			StringBuilder recipientsSb = null;
			if (recipients == null || recipients.isEmpty()) {
				throw new InvalidInputException("Recipient list is empty for sending mail");
			}
			recipientsSb = new StringBuilder();
			int count = 0;
			for (String recipientEmailId : recipients) {
				if (count != 0) {
					recipientsSb.append(",");
				}
				recipientsSb.append(recipientEmailId);
			}

			// Adding the recipients to address list
			Address[] addresses = InternetAddress.parse(recipientsSb.toString());

			// Setting up new MimeMessage
			Message message = new MimeMessage(mailSession);
			if (emailEntity.getSenderEmailId() != null && !emailEntity.getSenderEmailId().isEmpty() && emailEntity.getSenderName() != null
					&& !emailEntity.getSenderName().isEmpty()) {
				message.setFrom(new InternetAddress(emailEntity.getSenderEmailId(), emailEntity.getSenderName()));
			}
			else {
				throw new InvalidInputException("Sender email Id is not valid for sending mail");
			}

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
