package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class SurveyDetails {

	private long agentId;
	private String agentName;
	private int reminderCount;
	private String customerName;
	private String customerEmail;
	private long companyId;
	private long regionID;
	private long branchId;
	private int stage;
	private double score;
	private String review;
	private String mood;
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
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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
	public long getRegionID() {
		return regionID;
	}
	public void setRegionID(long regionID) {
		this.regionID = regionID;
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
	public List<SurveyResponse> getSurveyResponse() {
		return surveyResponse;
	}
	public void setSurveyResponse(List<SurveyResponse> surveyResponse) {
		this.surveyResponse = surveyResponse;
	}
}