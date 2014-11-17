package com.realtech.socialsurvey.core.entities;

// JIRA: SS-7: By RM02: BOC

/**
 * Entity containing the smtp settings for sending mail
 */
public class SmtpSettings {

	private String mailHost;
	private String mailPort;

	public static String MAIL_SMTP_AUTH = "true";
	public static String MAIL_SMTP_STARTTLS_ENABLE = "true";
	public static String MAIL_TRANSPORT = "smtp";

	public String getMailHost() {
		return mailHost;
	}

	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}

	public String getMailPort() {
		return mailPort;
	}

	public void setMailPort(String mailPort) {
		this.mailPort = mailPort;
	}

	@Override
	public String toString() {
		return "SmtpSettings [mailHost=" + mailHost + ", mailPort=" + mailPort + "]";
	}

}
// JIRA: SS-7: By RM02: EOC
