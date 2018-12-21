package com.realtech.socialsurvey.stream.entities;

import java.io.Serializable;

/**
 * 
 * @author rohitpatidar
 *
 */
public class SurveyProcessData implements Serializable{

    private static final long serialVersionUID = 1L;

	private String id; //default mongo id _id
	
	private long agentId;
	private long branchId;
	private long regionId;
	private long companyId;
	
	private String customerFirstName;
    private String customerLastName;
    private String customerEmail;
    
    private double score;
    private int npsScore = -1;
    private String review;
    private String mood;
    
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getAgentId() {
		return agentId;
	}
	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}
	public long getBranchId() {
		return branchId;
	}
	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}
	public long getRegionId() {
		return regionId;
	}
	public void setRegionId(long regionId) {
		this.regionId = regionId;
	}
	public long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
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
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public int getNpsScore() {
		return npsScore;
	}
	public void setNpsScore(int npsScore) {
		this.npsScore = npsScore;
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
}
