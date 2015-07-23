package com.realtech.socialsurvey.core.entities;

/*
 * The view class for User
 */
public class UserUploadVO {

	private String firstName;
	private String lastName;
	private String title;
	private long sourceBranchId;
	private long branchId;
	private long sourceRegionId;
	private long regionId;
	private boolean isAgent;
	private String emailId;
	private boolean assignToCompany;
	private String assignedBranchName;
	private String assignedRegionName;
	private boolean isBranchAdmin;
	private boolean isRegionAdmin;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getSourceBranchId() {
		return sourceBranchId;
	}

	public void setSourceBranchId(long sourceBranchId) {
		this.sourceBranchId = sourceBranchId;
	}

	public long getBranchId() {
		return branchId;
	}

	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}

	public long getSourceRegionId() {
		return sourceRegionId;
	}

	public void setSourceRegionId(long sourceRegionId) {
		this.sourceRegionId = sourceRegionId;
	}

	public long getRegionId() {
		return regionId;
	}

	public void setRegionId(long regionId) {
		this.regionId = regionId;
	}

	public boolean isAgent() {
		return isAgent;
	}

	public void setAgent(boolean isAgent) {
		this.isAgent = isAgent;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public boolean isBelongsToCompany() {
		return assignToCompany;
	}

	public void setBelongsToCompany(boolean belongsToCompany) {
		this.assignToCompany = belongsToCompany;
	}

	public String getAssignedBranchName() {
		return assignedBranchName;
	}

	public void setAssignedBranchName(String assignedBranchName) {
		this.assignedBranchName = assignedBranchName;
	}

	public String getAssignedRegionName() {
		return assignedRegionName;
	}

	public void setAssignedRegionName(String assignedRegionName) {
		this.assignedRegionName = assignedRegionName;
	}

	public boolean isBranchAdmin() {
		return isBranchAdmin;
	}

	public void setBranchAdmin(boolean isBranchAdmin) {
		this.isBranchAdmin = isBranchAdmin;
	}

	public boolean isRegionAdmin() {
		return isRegionAdmin;
	}

	public void setRegionAdmin(boolean isRegionAdmin) {
		this.isRegionAdmin = isRegionAdmin;
	}
}
