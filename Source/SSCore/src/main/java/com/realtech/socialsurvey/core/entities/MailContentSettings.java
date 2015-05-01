package com.realtech.socialsurvey.core.entities;

/**
 * Settings for the mail content
 */
public class MailContentSettings {

	private MailContent take_survey_mail;
	private MailContent take_survey_mail_customer;
	private MailContent take_survey_reminder_mail;

	public MailContent getTake_survey_mail() {
		return take_survey_mail;
	}

	public void setTake_survey_mail(MailContent take_survey_mail) {
		this.take_survey_mail = take_survey_mail;
	}

	public MailContent getTake_survey_mail_customer() {
		return take_survey_mail_customer;
	}

	public void setTake_survey_mail_customer(MailContent take_survey_mail_customer) {
		this.take_survey_mail_customer = take_survey_mail_customer;
	}

	public MailContent getTake_survey_reminder_mail() {
		return take_survey_reminder_mail;
	}

	public void setTake_survey_reminder_mail(MailContent take_survey_reminder_mail) {
		this.take_survey_reminder_mail = take_survey_reminder_mail;
	}
}
