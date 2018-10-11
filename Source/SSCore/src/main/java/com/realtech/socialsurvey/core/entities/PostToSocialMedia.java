package com.realtech.socialsurvey.core.entities;

public class PostToSocialMedia {

	private String agentName;
	private String agentProfileLink;
	private String custFirstName;
	private String custLastName;
	private long agentId;
	private double rating;
	private String feedback;
	private boolean abusive;
	private String serverBaseUrl;
	private boolean onlyPostToSocialSurvey;
	private boolean zillow;
	
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getAgentProfileLink() {
		return agentProfileLink;
	}
	public void setAgentProfileLink(String agentProfileLink) {
		this.agentProfileLink = agentProfileLink;
	}
	public String getCustFirstName() {
		return custFirstName;
	}
	public void setCustFirstName(String custFirstName) {
		this.custFirstName = custFirstName;
	}
	public String getCustLastName() {
		return custLastName;
	}
	public void setCustLastName(String custLastName) {
		this.custLastName = custLastName;
	}
	public long getAgentId() {
		return agentId;
	}
	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	public boolean isAbusive() {
		return abusive;
	}
	public void setAbusive(boolean isAbusive) {
		this.abusive = isAbusive;
	}
	public String getServerBaseUrl() {
		return serverBaseUrl;
	}
	public void setServerBaseUrl(String serverBaseUrl) {
		this.serverBaseUrl = serverBaseUrl;
	}
	public boolean isOnlyPostToSocialSurvey() {
		return onlyPostToSocialSurvey;
	}
	public void setOnlyPostToSocialSurvey(boolean onlyPostToSocialSurvey) {
		this.onlyPostToSocialSurvey = onlyPostToSocialSurvey;
	}
	public boolean isZillow() {
		return zillow;
	}
	public void setZillow(boolean isZillow) {
		this.zillow = isZillow;
	}
	
	
}
