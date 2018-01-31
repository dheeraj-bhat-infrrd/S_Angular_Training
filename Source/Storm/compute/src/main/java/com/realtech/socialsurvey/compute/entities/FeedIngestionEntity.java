package com.realtech.socialsurvey.compute.entities;

/**
 * Entity for feed ingestion
 */
public class FeedIngestionEntity {

	private long iden;
	private SocialMediaTokens socialMediaTokens;

	public long getIden() {
		return iden;
	}

	public void setIden(long iden) {
		this.iden = iden;
	}

	public SocialMediaTokens getSocialMediaTokens() {
		return socialMediaTokens;
	}

	public void setSocialMediaTokens(SocialMediaTokens socialMediaTokens) {
		this.socialMediaTokens = socialMediaTokens;
	}

}
