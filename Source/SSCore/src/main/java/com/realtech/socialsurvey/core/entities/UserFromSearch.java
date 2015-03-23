package com.realtech.socialsurvey.core.entities;

/**
 * User entity from the search
 */
public class UserFromSearch {

	private long userId;
	private String firstName;
	private String lastName;
	private String emailId;
	private String loginName;
	private int isOwner;
	private String displayName;
	private long companyId;
	private int status;
	private boolean isRegionAdmin;
	private boolean isBranchAdmin;
	private boolean isAgent;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

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

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public int getIsOwner() {
		return isOwner;
	}

	public void setIsOwner(int isOwner) {
		this.isOwner = isOwner;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isRegionAdmin() {
		return isRegionAdmin;
	}

	public void setRegionAdmin(boolean isRegionAdmin) {
		this.isRegionAdmin = isRegionAdmin;
	}

	public boolean isBranchAdmin() {
		return isBranchAdmin;
	}

	public void setBranchAdmin(boolean isBranchAdmin) {
		this.isBranchAdmin = isBranchAdmin;
	}

	public boolean isAgent() {
		return isAgent;
	}

	public void setAgent(boolean isAgent) {
		this.isAgent = isAgent;
	}
	
	@Override
	public String toString(){
		return "user id: "+userId+"\temailId: "+emailId+"\tdisplayName: "+displayName+"\tstatus: "+status+"\tisRegionAdmin: "+isRegionAdmin+"\tisBranchAdmin: "+isBranchAdmin+"\tisAgent: "+isAgent;
	}

}
