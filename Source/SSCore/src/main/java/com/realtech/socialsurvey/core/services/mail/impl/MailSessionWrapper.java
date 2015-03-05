package com.realtech.socialsurvey.core.services.mail.impl;

import java.util.Properties;
import javax.mail.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// JIRA SS-7: By RM04
/**
 * Wrapper to fetch mail session
 * 
 */
public class MailSessionWrapper {

	private static final Logger LOG = LoggerFactory
			.getLogger(MailSessionWrapper.class);

	private String mailSmtpAuth;
	private String mailSmtpStartTlsEnable;

	public String getMailSmtpAuth() {
		return mailSmtpAuth;
	}

	public void setMailSmtpAuth(String mailSmtpAuth) {
		this.mailSmtpAuth = mailSmtpAuth;
	}

	public String getMailSmtpStartTlsEnable() {
		return mailSmtpStartTlsEnable;
	}

	public void setMailSmtpStartTlsEnable(String mailSmtpStartTlsEnable) {
		this.mailSmtpStartTlsEnable = mailSmtpStartTlsEnable;
	}

	/**
	 * Returns the session object
	 * 
	 * @return
	 */
	public Session getMailSession() {
		LOG.debug("Preparing session object for sending mail");
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", mailSmtpAuth);
		properties.put("mail.smtp.starttls.enable", mailSmtpStartTlsEnable);
		Session mailSession = Session.getInstance(properties);
		LOG.debug("Returning the session object");
		return mailSession;
	}
}
