package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;

/**
 * @author manish
 *
 */
public class FacebookTokenForSM implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String facebookId;
    private String facebookPageLink;
    private String facebookAccessToken;
    private String facebookAccessTokenToPost;
    private boolean tokenExpiryAlertSent;

    public String getFacebookId()
    {
        return facebookId;
    }


    public void setFacebookId( String facebookId )
    {
        this.facebookId = facebookId;
    }


    public String getFacebookPageLink()
    {
        return facebookPageLink;
    }


    public void setFacebookPageLink( String facebookPageLink )
    {
        this.facebookPageLink = facebookPageLink;
    }


    public String getFacebookAccessToken()
    {
        return facebookAccessToken;
    }


    public void setFacebookAccessToken( String facebookAccessToken )
    {
        this.facebookAccessToken = facebookAccessToken;
    }


    public String getFacebookAccessTokenToPost()
    {
        return facebookAccessTokenToPost;
    }


    public void setFacebookAccessTokenToPost( String facebookAccessTokenToPost )
    {
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


    @Override public String toString()
    {
        return "FacebookTokenForSM{" + "facebookId='" + facebookId + '\'' + ", facebookPageLink='" + facebookPageLink + '\''
            + ", facebookAccessToken='" + facebookAccessToken + '\'' + ", facebookAccessTokenToPost='"
            + facebookAccessTokenToPost + '\'' + ", tokenExpiryAlertSent='" + tokenExpiryAlertSent + '\'' + '}';
    }
}