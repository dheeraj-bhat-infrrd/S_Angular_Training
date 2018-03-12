package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;


/**
 * @author manish
 *
 */
public class GoogleTokenForSM implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String googleId;
    private String profileLink;
    private String googleAccessToken;
    private String googleRefreshToken;


    public String getGoogleId()
    {
        return googleId;
    }


    public void setGoogleId( String googleId )
    {
        this.googleId = googleId;
    }


    public String getProfileLink()
    {
        return profileLink;
    }


    public void setProfileLink( String profileLink )
    {
        this.profileLink = profileLink;
    }


    public String getGoogleAccessToken()
    {
        return googleAccessToken;
    }


    public void setGoogleAccessToken( String googleAccessToken )
    {
        this.googleAccessToken = googleAccessToken;
    }


    public String getGoogleRefreshToken()
    {
        return googleRefreshToken;
    }


    public void setGoogleRefreshToken( String googleRefreshToken )
    {
        this.googleRefreshToken = googleRefreshToken;
    }


    @Override
    public String toString()
    {
        return "FacebookToken [googleId=" + googleId + ", googlePageLink=" + profileLink + "]";
    }
}