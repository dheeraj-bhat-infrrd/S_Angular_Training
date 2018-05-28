package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;


/**
 * @author manish
 *
 */
public class TwitterTokenForSM implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String twitterId;
    private String twitterPageLink;
    private String twitterAccessToken;
    private String twitterAccessTokenSecret;


    public String getTwitterId()
    {
        return twitterId;
    }


    public void setTwitterId( String twitterId )
    {
        this.twitterId = twitterId;
    }


    public String getTwitterPageLink()
    {
        return twitterPageLink;
    }


    public void setTwitterPageLink( String twitterPageLink )
    {
        this.twitterPageLink = twitterPageLink;
    }


    public String getTwitterAccessToken()
    {
        return twitterAccessToken;
    }


    public void setTwitterAccessToken( String twitterAccessToken )
    {
        this.twitterAccessToken = twitterAccessToken;
    }


    public String getTwitterAccessTokenSecret()
    {
        return twitterAccessTokenSecret;
    }


    public void setTwitterAccessTokenSecret( String twitterAccessTokenSecret )
    {
        this.twitterAccessTokenSecret = twitterAccessTokenSecret;
    }


    @Override
    public String toString()
    {
        return "TwitterToken [twitterId=" + twitterId + ", twitterPageLink=" + twitterPageLink + "]";
    }
}