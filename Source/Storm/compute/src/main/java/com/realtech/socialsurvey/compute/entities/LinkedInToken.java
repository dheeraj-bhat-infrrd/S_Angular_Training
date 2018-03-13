package com.realtech.socialsurvey.compute.entities;

import java.util.Date;

public class LinkedInToken {

	private String linkedInId;
	private String linkedInPageLink;
	private String linkedInAccessToken;
	private long linkedInAccessTokenCreatedOn;
	private long linkedInAccessTokenExpiresIn;
	
	private boolean tokenExpiryAlertSent;
    private String tokenExpiryAlertEmail;

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

	public boolean isTokenExpiryAlertSent()
    {
        return tokenExpiryAlertSent;
    }

    public void setTokenExpiryAlertSent( boolean tokenExpiryAlertSent )
    {
        this.tokenExpiryAlertSent = tokenExpiryAlertSent;
    }

    public String getTokenExpiryAlertEmail()
    {
        return tokenExpiryAlertEmail;
    }

    public void setTokenExpiryAlertEmail( String tokenExpiryAlertEmail )
    {
        this.tokenExpiryAlertEmail = tokenExpiryAlertEmail;
    }

    @Override
	public String toString() {
		return "LinkdenInToken [linkedInId=" + linkedInId + ", linkedInPageLink=" + linkedInPageLink + "]";
	}
}