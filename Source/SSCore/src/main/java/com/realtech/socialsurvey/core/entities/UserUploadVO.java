package com.realtech.socialsurvey.core.entities;

/*
 * The view class for User
 */
public class UserUploadVO {

	private String emailId;
	private boolean assignToCompany;
	private String assignedBranchName;
	private String assignedRegionName;
	private boolean isBranchAdmin;
	private boolean isRegionAdmin;

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
