package com.realtech.socialsurvey.compute.entities;

public class SocialMediaTokens
{

    private FacebookToken facebookToken;
    private LinkedInToken linkedInToken;
    private SocialProfileToken rssToken;
    private TwitterToken twitterToken;
    private FacebookPixelToken facebookPixelToken;


    public FacebookToken getFacebookToken()
    {
        return facebookToken;
    }


    public LinkedInToken getLinkedInToken()
    {
        return linkedInToken;
    }


    public SocialProfileToken getRssToken()
    {
        return rssToken;
    }


    public TwitterToken getTwitterToken()
    {
        return twitterToken;
    }


    public FacebookPixelToken getFacebookPixelToken()
    {
        return facebookPixelToken;
    }


    public void setFacebookToken( FacebookToken facebookToken )
    {
        this.facebookToken = facebookToken;
    }


    public void setLinkedInToken( LinkedInToken linkedInToken )
    {
        this.linkedInToken = linkedInToken;
    }


    public void setRssToken( SocialProfileToken rssToken )
    {
        this.rssToken = rssToken;
    }


    public void setTwitterToken( TwitterToken twitterToken )
    {
        this.twitterToken = twitterToken;
    }


    public void setFacebookPixelToken( FacebookPixelToken facebookPixelToken )
    {
        this.facebookPixelToken = facebookPixelToken;
    }


    @Override
    public String toString()
    {
        return "SocialMediaTokens [facebookToken=" + facebookToken + ", linkedInToken=" + linkedInToken + ", rssToken="
            + rssToken + ", twitterToken=" + twitterToken + ", facebookPixelToken=" + facebookPixelToken + "]";
    }


}