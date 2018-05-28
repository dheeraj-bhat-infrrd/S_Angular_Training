package com.realtech.socialsurvey.core.entities;

public class GoogleToken {

	private String googleId;
	private String profileLink;
	private String googleAccessToken;
	private String googleRefreshToken;
	private long googleAccessTokenCreatedOn;
	private long googleAccessTokenExpiresIn;

	public String getGoogleId() {
		return googleId;
	}

	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}

	public String getProfileLink() {
		return profileLink;
	}

	public void setProfileLink(String profileLink) {
		this.profileLink = profileLink;
	}

	public String getGoogleAccessToken() {
		return googleAccessToken;
	}

	public void setGoogleAccessToken(String googleAccessToken) {
		this.googleAccessToken = googleAccessToken;
	}

	public String getGoogleRefreshToken() {
		return googleRefreshToken;
	}

	public void setGoogleRefreshToken(String googleRefreshToken) {
		this.googleRefreshToken = googleRefreshToken;
	}

	public long getGoogleAccessTokenCreatedOn() {
		return googleAccessTokenCreatedOn;
	}

	public void setGoogleAccessTokenCreatedOn(long googleAccessTokenCreatedOn) {
		this.googleAccessTokenCreatedOn = googleAccessTokenCreatedOn;
	}

	public long getGoogleAccessTokenExpiresIn() {
		return googleAccessTokenExpiresIn;
	}

	public void setGoogleAccessTokenExpiresIn(long googleAccessTokenExpiresIn) {
		this.googleAccessTokenExpiresIn = googleAccessTokenExpiresIn;
	}


	@Override public String toString()
	{
		return "GoogleToken{" + "googleId='" + googleId + '\'' + ", profileLink='" + profileLink + '\''
			+ ", googleAccessToken='" + googleAccessToken + '\'' + ", googleRefreshToken='" + googleRefreshToken + '\''
			+ ", googleAccessTokenCreatedOn=" + googleAccessTokenCreatedOn + ", googleAccessTokenExpiresIn="
			+ googleAccessTokenExpiresIn + '}';
	}
}