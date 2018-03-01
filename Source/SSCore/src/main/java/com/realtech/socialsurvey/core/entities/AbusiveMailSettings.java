package com.realtech.socialsurvey.core.entities;

public class AbusiveMailSettings {

	
	private String mailId;
	
	public AbusiveMailSettings() {
		mailId = "";
	}

	public String getMailId() {
		return mailId;
	}

	public void setMailId(String mailId) {
		this.mailId = mailId;
	}


	@Override
	public String toString() {
		return "AbusiveMailSettings [mailId=" + mailId + "]";
	}

	
}
