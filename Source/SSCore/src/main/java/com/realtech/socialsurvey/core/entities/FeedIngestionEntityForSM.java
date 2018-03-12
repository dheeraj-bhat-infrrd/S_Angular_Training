package com.realtech.socialsurvey.core.entities;

/**
 * Entity for feed ingestion
 */
public class FeedIngestionEntityForSM {

	private long iden;
	private SocialMediaTokensForSM socialMediaTokens;

	public long getIden() {
		return iden;
	}

	public void setIden(long iden) {
		this.iden = iden;
	}

    public SocialMediaTokensForSM getSocialMediaTokens()
    {
        return socialMediaTokens;
    }

    public void setSocialMediaTokens( SocialMediaTokensForSM socialMediaTokens )
    {
        this.socialMediaTokens = socialMediaTokens;
    }
	
}
