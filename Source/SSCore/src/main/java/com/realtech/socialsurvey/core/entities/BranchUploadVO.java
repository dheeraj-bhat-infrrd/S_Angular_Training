package com.realtech.socialsurvey.core.entities;
/*
 * The view class for Branch
 */

public class BranchUploadVO {
	
	private String branchName;
	private String branchAddress1;
	private String branchAddress2;
	private String branchCountry;
	private String branchCountryCode;
	private String branchState;
	private String branchCity;
	private String branchZipcode;
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
	
	public String getBranchCountry() {
		return branchCountry;
	}

	public void setBranchCountry(String branchCountry) {
		this.branchCountry = branchCountry;
	}

	public String getBranchCountryCode() {
		return branchCountryCode;
	}

	public void setBranchCountryCode(String branchCountryCode) {
		this.branchCountryCode = branchCountryCode;
	}

	public String getBranchState() {
		return branchState;
	}

	public void setBranchState(String branchState) {
		this.branchState = branchState;
	}

	public String getBranchCity() {
		return branchCity;
	}

	public void setBranchCity(String branchCity) {
		this.branchCity = branchCity;
	}

	public String getBranchZipcode() {
		return branchZipcode;
	}

	public void setBranchZipcode(String branchZipcode) {
		this.branchZipcode = branchZipcode;
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
