package com.realtech.socialsurvey.compute.entities;

public class SocialProfileToken {

	private String profileId;
	private String profileLink;
	private String accessToken;
	private String accessTokenSecret;
	private long accessTokenCreatedOn;
	private long accessTokenExpiresOn;

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getProfileLink() {
		return profileLink;
	}

	public void setProfileLink(String profileLink) {
		this.profileLink = profileLink;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

	public void setAccessTokenSecret(String accessTokenSecret) {
		this.accessTokenSecret = accessTokenSecret;
	}

	public long getAccessTokenCreatedOn() {
		return accessTokenCreatedOn;
	}

	public void setAccessTokenCreatedOn(long accessTokenCreatedOn) {
		this.accessTokenCreatedOn = accessTokenCreatedOn;
	}

	public long getAccessTokenExpiresOn() {
		return accessTokenExpiresOn;
	}

	public void setAccessTokenExpiresOn(long accessTokenExpiresOn) {
		this.accessTokenExpiresOn = accessTokenExpiresOn;
	}

	@Override
	public String toString() {
		return "SocialProfileToken [profileId=" + profileId + ", profileLink=" + profileLink + "]";
	}
}