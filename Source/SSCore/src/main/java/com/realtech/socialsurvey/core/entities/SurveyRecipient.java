package com.realtech.socialsurvey.core.entities;

public class SurveyRecipient {
	private long agentId;
	private String agentName;
	private String firstname;
	private String lastname;
	private String emailId;
	private String agentEmailId;

	public String getAgentEmailId()
    {
        return agentEmailId;
    }

    public void setAgentEmailId( String agentEmailId )
    {
        this.agentEmailId = agentEmailId;
    }

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

	public long getAgentId() {
		return agentId;
	}

	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	@Override
	public String toString() {
		return "firstname: " + firstname + "\t lastname: " + lastname + "\t emailId: " + emailId;
	}
}