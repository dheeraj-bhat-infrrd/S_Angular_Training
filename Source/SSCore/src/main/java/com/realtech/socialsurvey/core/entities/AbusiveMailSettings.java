package com.realtech.socialsurvey.core.entities;

public class AbusiveMailSettings {

	
	private String mailId;
	private boolean enabled;
	
	public AbusiveMailSettings() {
		mailId = "";
		enabled = false;
	}

	public String getMailId() {
		return mailId;
	}

	public void setMailId(String mailId) {
		this.mailId = mailId;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "AbusiveMailSettings [mailId=" + mailId + ", enabled=" + enabled + "]";
	}

	
}
