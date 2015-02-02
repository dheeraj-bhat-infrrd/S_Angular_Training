package com.realtech.socialsurvey.core.entities;

public class LinkedInToken {

	private String linkedInId;
	private String linkedInPageLink;

	public String getLinkedInId() {
		return linkedInId;
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
