package com.realtech.socialsurvey.core.entities;

import java.util.Date;

public class LinkedInToken {

	private String linkedInId;
	private String linkedInPageLink;
	private String linkedInAccessToken;
	// private String linkedInAccessTokenSecret;
	private long linkedInAccessTokenCreatedOn;
	private long linkedInAccessTokenExpiresIn;
	
	private boolean tokenExpiryAlertSent;
    private Date tokenExpiryAlertTime;
    private String tokenExpiryAlertEmail;
    private long lastTokenExpiryValidationTime;

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

	public boolean isTokenExpiryAlertSent()
    {
        return tokenExpiryAlertSent;
    }

    public void setTokenExpiryAlertSent( boolean tokenExpiryAlertSent )
    {
        this.tokenExpiryAlertSent = tokenExpiryAlertSent;
    }

    public Date getTokenExpiryAlertTime()
    {
        return tokenExpiryAlertTime;
    }

    public void setTokenExpiryAlertTime( Date tokenExpiryAlertTime )
    {
        this.tokenExpiryAlertTime = tokenExpiryAlertTime;
    }

    public String getTokenExpiryAlertEmail()
    {
        return tokenExpiryAlertEmail;
    }

    public void setTokenExpiryAlertEmail( String tokenExpiryAlertEmail )
    {
        this.tokenExpiryAlertEmail = tokenExpiryAlertEmail;
    }

    public long getLastTokenExpiryValidationTime()
    {
        return lastTokenExpiryValidationTime;
    }

    public void setLastTokenExpiryValidationTime( long lastTokenExpiryValidationTime )
    {
        this.lastTokenExpiryValidationTime = lastTokenExpiryValidationTime;
    }


	@Override public String toString()
	{
		return "LinkedInToken{" + "linkedInId='" + linkedInId + '\'' + ", linkedInPageLink='" + linkedInPageLink + '\''
			+ ", linkedInAccessToken='" + linkedInAccessToken + '\'' + ", linkedInAccessTokenCreatedOn="
			+ linkedInAccessTokenCreatedOn + ", linkedInAccessTokenExpiresIn=" + linkedInAccessTokenExpiresIn
			+ ", tokenExpiryAlertSent=" + tokenExpiryAlertSent + ", tokenExpiryAlertTime=" + tokenExpiryAlertTime
			+ ", tokenExpiryAlertEmail='" + tokenExpiryAlertEmail + '\'' + ", lastTokenExpiryValidationTime="
			+ lastTokenExpiryValidationTime + '}';
	}
}