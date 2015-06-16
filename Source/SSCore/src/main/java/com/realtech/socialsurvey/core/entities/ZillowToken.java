package com.realtech.socialsurvey.core.entities;

public class ZillowToken {

	private String zillowId;
	private String zillowProfileLink;

	public String getZillowId() {
		return zillowId;
	}

	public void setZillowId(String zillowId) {
		this.zillowId = zillowId;
	}

	public String getZillowProfileLink() {
		return zillowProfileLink;
	}

	public void setZillowProfileLink(String zillowProfileLink) {
		this.zillowProfileLink = zillowProfileLink;
	}

	@Override
	public String toString() {
		return "YelpToken [zillowId=" + zillowId + ", zillowProfileLink=" + zillowProfileLink + "]";
	}
}