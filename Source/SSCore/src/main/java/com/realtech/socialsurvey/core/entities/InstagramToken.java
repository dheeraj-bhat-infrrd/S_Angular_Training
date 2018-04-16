package com.realtech.socialsurvey.core.entities;

import java.util.Date;

/**
 * @author Lavanya
 */

public class InstagramToken {
    private String id;
    private String pageLink;
    private String accessToken;
    private long accessTokenCreatedOn;
    private long accessTokenExpiresOn;
    private String accessTokenToPost;
    private boolean tokenExpiryAlertSent;
    private Date tokenExpiryAlertTime;
    private String tokenExpiryAlertEmail;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPageLink() {
        return pageLink;
    }

    public void setPageLink(String pageLink) {
        this.pageLink = pageLink;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getAccessTokenCreatedOn() {
        return accessTokenCreatedOn;
    }

    public void setAccessTokenCreatedOn(long accessTokenCreatedOn) {
        this.accessTokenCreatedOn = accessTokenCreatedOn;
    }

    public long getAccessTokenExpiresOn() {
        return accessTokenExpiresOn;
    }

    public void setAccessTokenExpiresOn(long accessTokenExpiresOn) {
        this.accessTokenExpiresOn = accessTokenExpiresOn;
    }

    public String getAccessTokenToPost() {
        return accessTokenToPost;
    }

    public void setAccessTokenToPost(String accessTokenToPost) {
        this.accessTokenToPost = accessTokenToPost;
    }

    public boolean isTokenExpiryAlertSent() {
        return tokenExpiryAlertSent;
    }

    public void setTokenExpiryAlertSent(boolean tokenExpiryAlertSent) {
        this.tokenExpiryAlertSent = tokenExpiryAlertSent;
    }

    public Date getTokenExpiryAlertTime() {
        return tokenExpiryAlertTime;
    }

    public void setTokenExpiryAlertTime(Date tokenExpiryAlertTime) {
        this.tokenExpiryAlertTime = tokenExpiryAlertTime;
    }

    public String getTokenExpiryAlertEmail() {
        return tokenExpiryAlertEmail;
    }

    public void setTokenExpiryAlertEmail(String tokenExpiryAlertEmail) {
        this.tokenExpiryAlertEmail = tokenExpiryAlertEmail;
    }

    @Override
    public String toString() {
        return "InstagramToken{" +
                "id='" + id + '\'' +
                ", pageLink='" + pageLink + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", accessTokenCreatedOn=" + accessTokenCreatedOn +
                ", accessTokenExpiresOn=" + accessTokenExpiresOn +
                ", accessTokenToPost='" + accessTokenToPost + '\'' +
                ", tokenExpiryAlertSent=" + tokenExpiryAlertSent +
                ", tokenExpiryAlertTime=" + tokenExpiryAlertTime +
                ", tokenExpiryAlertEmail='" + tokenExpiryAlertEmail + '\'' +
                '}';
    }
}
