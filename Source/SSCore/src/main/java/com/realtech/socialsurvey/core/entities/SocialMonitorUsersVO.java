package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;

public class SocialMonitorUsersVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long regionId;
	private long branchId;
	private long userId;
	private String name;
	private String profileImageUrl;

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

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	@Override
	public String toString() {
		return "SocialMonitorUsersVO [regionId=" + regionId + ", branchId=" + branchId + ", userId=" + userId
				+ ", name=" + name + ", profileImageUrl=" + profileImageUrl + "]";
	}

}
