package com.realtech.socialsurvey.core.entities;

import java.util.List;

/**
 * Holds the mail content
 */
public class MailContent {
	private String mail_subject;
	private String mail_body;
	private List<String> param_order;

	public String getMail_subject() {
		return mail_subject;
	}

	public void setMail_subject(String mail_subject) {
		this.mail_subject = mail_subject;
	}

	public String getMail_body() {
		return mail_body;
	}

	public void setMail_body(String mail_body) {
		this.mail_body = mail_body;
	}

	public List<String> getParam_order() {
		return param_order;
	}

	public void setParam_order(List<String> param_order) {
		this.param_order = param_order;
	}
}