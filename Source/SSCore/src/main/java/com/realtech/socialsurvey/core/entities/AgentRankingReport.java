package com.realtech.socialsurvey.core.entities;


public class AgentRankingReport {
	private long agentId;
	private String agentName;
	private String agentFirstName;
	private String agentLastName;
	private double averageScore;
	private long completedSurveys;
	private long incompleteSurveys;
	private long registrationDate;

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getAgentFirstName() {
		return agentFirstName;
	}

	public void setAgentFirstName(String agentFirstName) {
		this.agentFirstName = agentFirstName;
	}

	public String getAgentLastName() {
		return agentLastName;
	}

	public void setAgentLastName(String agentLastName) {
		this.agentLastName = agentLastName;
	}

	public long getAgentId() {
		return agentId;
	}

	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}

	public double getAverageScore() {
		return averageScore;
	}

	public void setAverageScore(double averageScore) {
		this.averageScore = averageScore;
	}

	public long getCompletedSurveys() {
		return completedSurveys;
	}

	public void setCompletedSurveys(long completedSurveys) {
		this.completedSurveys = completedSurveys;
	}

	public long getIncompleteSurveys() {
		return incompleteSurveys;
	}

	public void setIncompleteSurveys(long incompleteSurveys) {
		this.incompleteSurveys = incompleteSurveys;
	}

	public long getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(long registrationDate) {
		this.registrationDate = registrationDate;
	}
}