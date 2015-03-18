package com.realtech.socialsurvey.core.entities;

public class LinkedInToken {

	private String linkedInId;
	private String linkedInPageLink;
	private String linkedInAccessToken;
	// private String linkedInAccessTokenSecret;
	private long linkedInAccessTokenCreatedOn;
	private long linkedInAccessTokenExpiresIn;

	public String getLinkedInId() {
		return linkedInId;
	}

	public String getLinkedInAccessToken() {
		return linkedInAccessToken;
	}

	public void setLinkedInAccessToken(String linkedInAccessToken) {
		this.linkedInAccessToken = linkedInAccessToken;
	}

	public void setLinkedInId(String linkedInId) {
		this.linkedInId = linkedInId;
	}

	public String getLinkedInPageLink() {
		return linkedInPageLink;
	}

	public void setLinkedInPageLink(String linkedInPageLink) {
		this.linkedInPageLink = linkedInPageLink;
	}

	/*public String getLinkedInAccessTokenSecret() {
		return linkedInAccessTokenSecret;
	}

	public void setLinkedInAccessTokenSecret(String linkedInAccessTokenSecret) {
		this.linkedInAccessTokenSecret = linkedInAccessTokenSecret;
	}*/

	public long getLinkedInAccessTokenCreatedOn() {
		return linkedInAccessTokenCreatedOn;
	}

	public void setLinkedInAccessTokenCreatedOn(long linkedInAccessTokenCreatedOn) {
		this.linkedInAccessTokenCreatedOn = linkedInAccessTokenCreatedOn;
	}

	public long getLinkedInAccessTokenExpiresIn() {
		return linkedInAccessTokenExpiresIn;
	}

	public void setLinkedInAccessTokenExpiresIn(long linkedInAccessTokenExpiresIn) {
		this.linkedInAccessTokenExpiresIn = linkedInAccessTokenExpiresIn;
	}

	@Override
	public String toString() {
		return "LinkdenInToken [linkedInId=" + linkedInId + ", linkedInPageLink=" + linkedInPageLink + "]";
	}
}