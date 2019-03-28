package com.realtech.socialsurvey.core.entities;

public class SocialMediaTokens
{

    private FacebookToken facebookToken;
    private GoogleToken googleToken;
    private LinkedInToken linkedInToken;
    private LinkedInToken linkedInV2Token;
    private SocialProfileToken rssToken;
    private TwitterToken twitterToken;
    private YelpToken yelpToken;
    private ZillowToken zillowToken;
    private LendingTreeToken lendingTreeToken;
    private RealtorToken realtorToken;
    private GoogleBusinessToken googleBusinessToken;
    private FacebookPixelToken facebookPixelToken;
    private InstagramToken instagramToken;
    private String linkedInProfileUrl;

    public GoogleBusinessToken getGoogleBusinessToken()
    {
        return googleBusinessToken;
    }


    public void setGoogleBusinessToken( GoogleBusinessToken googleBusinessToken )
    {
        this.googleBusinessToken = googleBusinessToken;
    }


    public FacebookToken getFacebookToken()
    {
        return facebookToken;
    }


    public void setFacebookToken( FacebookToken facebookToken )
    {
        this.facebookToken = facebookToken;
    }


    public TwitterToken getTwitterToken()
    {
        return twitterToken;
    }


    public void setTwitterToken( TwitterToken twitterToken )
    {
        this.twitterToken = twitterToken;
    }


    public LinkedInToken getLinkedInToken()
    {
        return linkedInToken;
    }


    public void setLinkedInToken( LinkedInToken linkedInToken )
    {
        this.linkedInToken = linkedInToken;
    }


    public YelpToken getYelpToken()
    {
        return yelpToken;
    }


    public void setYelpToken( YelpToken yelpToken )
    {
        this.yelpToken = yelpToken;
    }


    public GoogleToken getGoogleToken()
    {
        return googleToken;
    }


    public void setGoogleToken( GoogleToken googleToken )
    {
        this.googleToken = googleToken;
    }


    public SocialProfileToken getRssToken()
    {
        return rssToken;
    }


    public void setRssToken( SocialProfileToken rssToken )
    {
        this.rssToken = rssToken;
    }


    public ZillowToken getZillowToken()
    {
        return zillowToken;
    }


    public void setZillowToken( ZillowToken zillowToken )
    {
        this.zillowToken = zillowToken;
    }


    public LendingTreeToken getLendingTreeToken()
    {
        return lendingTreeToken;
    }


    public void setLendingTreeToken( LendingTreeToken lendingTreeToken )
    {
        this.lendingTreeToken = lendingTreeToken;
    }


    public RealtorToken getRealtorToken()
    {
        return realtorToken;
    }


    public void setRealtorToken( RealtorToken realtorToken )
    {
        this.realtorToken = realtorToken;
    }


    public FacebookPixelToken getFacebookPixelToken()
    {
        return facebookPixelToken;
    }


    public void setFacebookPixelToken( FacebookPixelToken facebookPixelToken )
    {
        this.facebookPixelToken = facebookPixelToken;
    }


    public InstagramToken getInstagramToken()
    {
        return instagramToken;
    }


    public void setInstagramToken( InstagramToken instagramToken )
    {
        this.instagramToken = instagramToken;
    }
    
    


    public LinkedInToken getLinkedInV2Token()
    {
        return linkedInV2Token;
    }


    public void setLinkedInV2Token( LinkedInToken linkedInV2Token )
    {
        this.linkedInV2Token = linkedInV2Token;
    }



    public String getLinkedInProfileUrl()
    {
        return linkedInProfileUrl;
    }


    public void setLinkedInProfileUrl( String linkedInProfileUrl )
    {
        this.linkedInProfileUrl = linkedInProfileUrl;
    }


    @Override
    public String toString()
    {
        return "SocialMediaTokens [facebookToken=" + facebookToken + ", googleToken=" + googleToken + ", linkedInToken="
            + linkedInToken + ", linkedInV2Token=" + linkedInV2Token + ", rssToken=" + rssToken + ", twitterToken="
            + twitterToken + ", yelpToken=" + yelpToken + ", zillowToken=" + zillowToken + ", lendingTreeToken="
            + lendingTreeToken + ", realtorToken=" + realtorToken + ", googleBusinessToken=" + googleBusinessToken
            + ", facebookPixelToken=" + facebookPixelToken + ", instagramToken=" + instagramToken + "]";
    }
}