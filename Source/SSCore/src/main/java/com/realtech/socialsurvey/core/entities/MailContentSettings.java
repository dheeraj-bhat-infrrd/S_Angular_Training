package com.realtech.socialsurvey.core.entities;

/**
 * Settings for the mail content
 */
public class MailContentSettings {

	private MailContent take_survey_mail;
	private MailContent take_survey_mail_customer;
	private MailContent take_survey_reminder_mail;
	private MailContent restart_survey_mail;
	private MailContent survey_completion_mail;
	private MailContent social_post_reminder_mail;
	private MailContent survey_completion_unpleasant_mail;
	private MailContent survey_reviews_reply_mail;

	public MailContent getSocial_post_reminder_mail() {
		return social_post_reminder_mail;
	}

	public void setSocial_post_reminder_mail(MailContent social_post_reminder_mail) {
		this.social_post_reminder_mail = social_post_reminder_mail;
	}

	public MailContent getSurvey_completion_mail() {
		return survey_completion_mail;
	}

	public void setSurvey_completion_mail(MailContent survey_completion_mail) {
		this.survey_completion_mail = survey_completion_mail;
	}

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

	public MailContent getRestart_survey_mail() {
		return restart_survey_mail;
	}

	public void setRestart_survey_mail(MailContent restart_survey_mail) {
		this.restart_survey_mail = restart_survey_mail;
	}

    public MailContent getSurvey_completion_unpleasant_mail()
    {
        return survey_completion_unpleasant_mail;
    }

    public void setSurvey_completion_unpleasant_mail( MailContent survey_completion_unpleasant_mail )
    {
        this.survey_completion_unpleasant_mail = survey_completion_unpleasant_mail;
    }

    public MailContent getSurvey_reviews_reply_mail()
    {
        return survey_reviews_reply_mail;
    }

    public void setSurvey_reviews_reply_mail( MailContent survey_reviews_reply_mail )
    {
        this.survey_reviews_reply_mail = survey_reviews_reply_mail;
    }
	
	
	
}
