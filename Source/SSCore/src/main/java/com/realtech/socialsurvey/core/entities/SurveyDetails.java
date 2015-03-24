package com.realtech.socialsurvey.core.entities;

import java.util.Date;
import java.util.List;

public class SurveyDetails {

	private long agentId;
	private String agentName;
	private int reminderCount;
	private String customerFirstName;
	private String customerLastName;
	private String customerEmail;
	private long companyId;
	private long regionId;
	private long branchId;
	private int stage;
	private double score;
	private String review;
	private String mood;
	private Date createdOn;
	private Date modifiedOn;
	private List<String> sharedOn;
	String custRelationWithAgent;
	private String initiatedBy;
	private String url;
	private List<SurveyResponse> surveyResponse;
	
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
	public int getReminderCount() {
		return reminderCount;
	}
	public void setReminderCount(int reminderCount) {
		this.reminderCount = reminderCount;
	}
	public String getCustomerFirstName() {
		return customerFirstName;
	}
	public void setCustomerFirstName(String customerFirstName) {
		this.customerFirstName = customerFirstName;
	}
	public String getCustomerLastName() {
		return customerLastName;
	}
	public void setCustomerLastName(String customerLastName) {
		this.customerLastName = customerLastName;
	}
	public String getCustomerEmail() {
		return customerEmail;
	}
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	public long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}
	public long getRegionId() {
		return regionId;
	}
	public void setRegionId(long regionId) {
		this.regionId = regionId;
	}
	public long getBranchId() {
		return branchId;
	}
	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getReview() {
		return review;
	}
	public void setReview(String review) {
		this.review = review;
	}
	public String getMood() {
		return mood;
	}
	public void setMood(String mood) {
		this.mood = mood;
	}
	public Date getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public List<String> getSharedOn() {
		return sharedOn;
	}
	public void setSharedOn(List<String> sharedOn) {
		this.sharedOn = sharedOn;
	}
	public String getCustRelationWithAgent() {
		return custRelationWithAgent;
	}
	public void setCustRelationWithAgent(String custRelationWithAgent) {
		this.custRelationWithAgent = custRelationWithAgent;
	}
	public String getInitiatedBy() {
		return initiatedBy;
	}
	public void setInitiatedBy(String initiatedBy) {
		this.initiatedBy = initiatedBy;
	}
	public List<SurveyResponse> getSurveyResponse() {
		return surveyResponse;
	}
	public void setSurveyResponse(List<SurveyResponse> surveyResponse) {
		this.surveyResponse = surveyResponse;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}