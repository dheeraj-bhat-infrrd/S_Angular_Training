package com.realtech.socialsurvey.core.entities;

import java.util.Date;
import java.util.List;

public class FacebookToken {

	private String facebookId;
	private String facebookPageLink;
	private String facebookAccessToken;
	private long facebookAccessTokenCreatedOn;
	private long facebookAccessTokenExpiresOn;
	private List<FacebookPage> facebookPages;
	private String facebookAccessTokenToPost;
	
	private boolean tokenExpiryAlertSent;
	private Date tokenExpiryAlertTime;
	private String tokenExpiryAlertEmail;
	private long lastTokenExpiryValidationTime;
	private long facebookAccessTokenExpiresOnTemp;
	
	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getFacebookPageLink() {
		return facebookPageLink;
	}

	public void setFacebookPageLink(String facebookPageLink) {
		this.facebookPageLink = facebookPageLink;
	}

	public String getFacebookAccessToken() {
		return facebookAccessToken;
	}

	public void setFacebookAccessToken(String facebookAccessToken) {
		this.facebookAccessToken = facebookAccessToken;
	}

	public long getFacebookAccessTokenCreatedOn() {
		return facebookAccessTokenCreatedOn;
	}

	public void setFacebookAccessTokenCreatedOn(long facebookAccessTokenCreatedOn) {
		this.facebookAccessTokenCreatedOn = facebookAccessTokenCreatedOn;
	}

	public long getFacebookAccessTokenExpiresOn() {
		return facebookAccessTokenExpiresOn;
	}

	public void setFacebookAccessTokenExpiresOn(long facebookAccessTokenExpiresOn) {
		this.facebookAccessTokenExpiresOn = facebookAccessTokenExpiresOn;
	}

	public List<FacebookPage> getFacebookPages() {
		return facebookPages;
	}

	public void setFacebookPages(List<FacebookPage> facebookPages) {
		this.facebookPages = facebookPages;
	}

	public String getFacebookAccessTokenToPost() {
		return facebookAccessTokenToPost;
	}

	public void setFacebookAccessTokenToPost(String facebookAccessTokenToPost) {
		this.facebookAccessTokenToPost = facebookAccessTokenToPost;
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

	public long getFacebookAccessTokenExpiresOnTemp() {
		return facebookAccessTokenExpiresOnTemp;
	}

	public void setFacebookAccessTokenExpiresOnTemp(long facebookAccessTokenExpiresOnTemp) {
		this.facebookAccessTokenExpiresOnTemp = facebookAccessTokenExpiresOnTemp;
	}

	@Override
	public String toString() {
		return "FacebookToken [facebookId=" + facebookId + ", facebookPageLink=" + facebookPageLink
				+ ", facebookAccessToken=" + facebookAccessToken + ", facebookAccessTokenCreatedOn="
				+ facebookAccessTokenCreatedOn + ", facebookAccessTokenExpiresOn=" + facebookAccessTokenExpiresOn
				+ ", facebookPages=" + facebookPages + ", facebookAccessTokenToPost=" + facebookAccessTokenToPost
				+ ", tokenExpiryAlertSent=" + tokenExpiryAlertSent + ", tokenExpiryAlertTime=" + tokenExpiryAlertTime
				+ ", tokenExpiryAlertEmail=" + tokenExpiryAlertEmail + ", lastTokenExpiryValidationTime="
				+ lastTokenExpiryValidationTime + ", facebookAccessTokenExpiresOnTemp="
				+ facebookAccessTokenExpiresOnTemp + "]";
	}
}