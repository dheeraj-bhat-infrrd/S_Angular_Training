package com.realtech.socialsurvey.core.entities;

import java.util.List;

/**
 * 
 * @author rohit
 *
 */
public class EntityMediaPostResponseDetails
{
    private List<SocialMediaPostResponse> facebookPostResponseList;
    
    private List<SocialMediaPostResponse> twitterPostResponseList;
    
    private List<SocialMediaPostResponse> linkedinPostResponseList;

    
    public List<SocialMediaPostResponse> getFacebookPostResponseList()
    {
        return facebookPostResponseList;
    }

    public void setFacebookPostResponseList( List<SocialMediaPostResponse> facebookPostResponseList )
    {
        this.facebookPostResponseList = facebookPostResponseList;
    }

    public List<SocialMediaPostResponse> getTwitterPostResponseList()
    {
        return twitterPostResponseList;
    }

    public void setTwitterPostResponseList( List<SocialMediaPostResponse> twitterPostResponseList )
    {
        this.twitterPostResponseList = twitterPostResponseList;
    }

    public List<SocialMediaPostResponse> getLinkedinPostResponseList()
    {
        return linkedinPostResponseList;
    }

    public void setLinkedinPostResponseList( List<SocialMediaPostResponse> linkedinPostResponseList )
    {
        this.linkedinPostResponseList = linkedinPostResponseList;
    }

}
