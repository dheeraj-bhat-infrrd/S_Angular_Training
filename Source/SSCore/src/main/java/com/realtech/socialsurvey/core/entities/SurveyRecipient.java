package com.realtech.socialsurvey.core.entities;

public class SurveyRecipient {
	private String firstname;
	private String lastname;
	private String emailId;

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	@Override
	public String toString() {
		return "firstname: " + firstname + "\t lastname: " + lastname + "\t emailId: " + emailId;
	}
}