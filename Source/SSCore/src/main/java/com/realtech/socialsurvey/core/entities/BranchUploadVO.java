package com.realtech.socialsurvey.core.entities;
/*
 * The view class for Branch
 */

public class BranchUploadVO {
	
	private String branchName;
	private String branchAddress1;
	private String branchAddress2;
	private boolean assignToCompany;
	private String assignedRegionName;
	
	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getBranchAddress1() {
		return branchAddress1;
	}

	public void setBranchAddress1(String branchAddress1) {
		this.branchAddress1 = branchAddress1;
	}

	public String getBranchAddress2() {
		return branchAddress2;
	}

	public void setBranchAddress2(String branchAddress2) {
		this.branchAddress2 = branchAddress2;
	}

	public boolean isAssignToCompany() {
		return assignToCompany;
	}

	public void setAssignToCompany(boolean assignToCompany) {
		this.assignToCompany = assignToCompany;
	}

	public String getAssignedRegionName() {
		return assignedRegionName;
	}

	public void setAssignedRegionName(String assignedBranchName) {
		this.assignedRegionName = assignedBranchName;
	}
}
