package com.realtech.socialsurvey.core.entities;

/**
 * 
 * @author rohit
 *
 */
public class EntityMediaPostResponseDetails
{
    private SocialMediaPostResponse facebookPostResponse;
    
    private SocialMediaPostResponse twitterPostResponse;
    
    private SocialMediaPostResponse linkedinPostResponse;
    

    public SocialMediaPostResponse getFacebookPostResponse()
    {
        return facebookPostResponse;
    }

    public void setFacebookPostResponse( SocialMediaPostResponse facebookPostResponse )
    {
        this.facebookPostResponse = facebookPostResponse;
    }

    public SocialMediaPostResponse getTwitterPostResponse()
    {
        return twitterPostResponse;
    }

    public void setTwitterPostResponse( SocialMediaPostResponse twitterPostResponse )
    {
        this.twitterPostResponse = twitterPostResponse;
    }

    public SocialMediaPostResponse getLinkedinPostResponse()
    {
        return linkedinPostResponse;
    }

    public void setLinkedinPostResponse( SocialMediaPostResponse linkedinPostResponse )
    {
        this.linkedinPostResponse = linkedinPostResponse;
    }

}
