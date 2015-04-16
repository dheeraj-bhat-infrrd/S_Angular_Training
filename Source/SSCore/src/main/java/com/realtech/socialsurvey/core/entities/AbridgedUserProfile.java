package com.realtech.socialsurvey.core.entities;

public class AbridgedUserProfile {

	private long userProfileId;
	private String userProfileName;
	private String profileName;
	private long profileValue;
	private int profilesMasterId;

	public long getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(long userProfileId) {
		this.userProfileId = userProfileId;
	}

	public String getUserProfileName() {
		return userProfileName;
	}

	public void setUserProfileName(String userProfileName) {
		this.userProfileName = userProfileName;
	}

	public long getProfileValue() {
		return profileValue;
	}

	public void setProfileValue(long profileValue) {
		this.profileValue = profileValue;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public int getProfilesMasterId() {
		return profilesMasterId;
	}

	public void setProfilesMasterId(int profilesMasterId) {
		this.profilesMasterId = profilesMasterId;
	}

	@Override
	public String toString() {
		return "userProfileId: " + userProfileId + "\tuserProfileName: " + userProfileName + "\tprofileId: " + profileValue + "\tprofileType: "
				+ profileName + "\tprofilesMasterId: " + profilesMasterId;
	}
}