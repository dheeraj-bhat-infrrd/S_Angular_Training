package com.realtech.socialsurvey.compute.entities;

public class TwitterToken {

	private String twitterId;
	private String twitterPageLink;
	private String twitterAccessToken;
	private String twitterAccessTokenSecret;
	private long twitterAccessTokenCreatedOn;

	public String getTwitterId() {
		return twitterId;
	}

	public void setTwitterId(String twitterId) {
		this.twitterId = twitterId;
	}

	public String getTwitterPageLink() {
		return twitterPageLink;
	}

	public void setTwitterPageLink(String twitterPageLink) {
		this.twitterPageLink = twitterPageLink;
	}

	public String getTwitterAccessToken() {
		return twitterAccessToken;
	}

	public void setTwitterAccessToken(String twitterAccessToken) {
		this.twitterAccessToken = twitterAccessToken;
	}

	public String getTwitterAccessTokenSecret() {
		return twitterAccessTokenSecret;
	}

	public void setTwitterAccessTokenSecret(String twitterAccessTokenSecret) {
		this.twitterAccessTokenSecret = twitterAccessTokenSecret;
	}

	public long getTwitterAccessTokenCreatedOn() {
		return twitterAccessTokenCreatedOn;
	}

	public void setTwitterAccessTokenCreatedOn(long twitterAccessTokenCreatedOn) {
		this.twitterAccessTokenCreatedOn = twitterAccessTokenCreatedOn;
	}

	@Override
	public String toString() {
		return "TwitterToken [twitterId=" + twitterId + ", twitterPageLink=" + twitterPageLink + ", twitterAccessToken=" + twitterAccessToken
				+ ", twitterAccessTokenSecret=" + twitterAccessTokenSecret + ", twitterAccessTokenCreatedOn=" + twitterAccessTokenCreatedOn + "]";
	}
}