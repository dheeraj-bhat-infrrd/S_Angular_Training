package com.realtech.socialsurvey.core.entities;

import java.sql.Timestamp;

public class LinkedInToken {

	private String linkedInId;
	private String linkedInPageLink;
	private String linkedInAccessToken;
	private Timestamp linkedInAccessTokenCreatedOn;

	public Timestamp getLinkedInAccessTokenCreatedOn() {
		return linkedInAccessTokenCreatedOn;
	}

	public void setLinkedInAccessTokenCreatedOn(Timestamp linkedInAccessTokenCreatedOn) {
		this.linkedInAccessTokenCreatedOn = linkedInAccessTokenCreatedOn;
	}

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

	@Override
	public String toString() {
		return "LinkdenInToken [linkedInId=" + linkedInId + ", linkedInPageLink=" + linkedInPageLink + "]";
	}

}
