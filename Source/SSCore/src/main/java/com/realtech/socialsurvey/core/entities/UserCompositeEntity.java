package com.realtech.socialsurvey.core.entities;

/**
 * Holds the agent settings from mongo and the user object from mysql
 */
public class UserCompositeEntity {

	private User user;
	private AgentSettings agentSettings;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public AgentSettings getAgentSettings() {
		return agentSettings;
	}

	public void setAgentSettings(AgentSettings agentSettings) {
		this.agentSettings = agentSettings;
	}

}
