package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;


/**
 * @author Lavanya
 */
public class InstagramTokenForSM implements Serializable
{

    private static final long serialVersionUID = 1L;
    private String id;
    private String pageLink;
    private String accessToken;
    private String accessTokenToPost;
    private boolean tokenExpiryAlertSent;

    public String getId()
    {
        return id;
    }


    public void setId( String id )
    {
        this.id = id;
    }


    public String getPageLink()
    {
        return pageLink;
    }


    public void setPageLink( String pageLink )
    {
        this.pageLink = pageLink;
    }


    public String getAccessToken()
    {
        return accessToken;
    }


    public void setAccessToken( String accessToken )
    {
        this.accessToken = accessToken;
    }


    public String getAccessTokenToPost()
    {
        return accessTokenToPost;
    }


    public void setAccessTokenToPost( String accessTokenToPost )
    {
        this.accessTokenToPost = accessTokenToPost;
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
        return "InstagramTokenForSM{" + "id='" + id + '\'' + ", pageLink='" + pageLink + '\'' + ", accessToken='" + accessToken
            + '\'' + ", accessTokenToPost='" + accessTokenToPost + '\'' + ", tokenExpiryAlertSent='" + tokenExpiryAlertSent
            + '\'' + '}';
    }
}
