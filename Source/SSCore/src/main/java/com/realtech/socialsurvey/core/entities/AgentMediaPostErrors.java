package com.realtech.socialsurvey.core.entities;

import java.util.List;
/**
 * 
 * @author rohit
 *
 */
public class AgentMediaPostErrors
{
    private long agentId;

    private List<SocialMediaSharedError> facebookSharedErrors;
    
    private List<SocialMediaSharedError> twitterSharedErrors;
    
    private List<SocialMediaSharedError> linkedinSharedErrors;
    
    private List<SocialMediaSharedError> googlePlusSharedErrors;
    
    private List<SocialMediaSharedError> yelpSharedErrors;

    
    public long getAgentId()
    {
        return agentId;
    }

    public void setAgentId( long agentId )
    {
        this.agentId = agentId;
    }

    public List<SocialMediaSharedError> getFacebookSharedErrors()
    {
        return facebookSharedErrors;
    }

    public void setFacebookSharedErrors( List<SocialMediaSharedError> facebookSharedErrors )
    {
        this.facebookSharedErrors = facebookSharedErrors;
    }

    public List<SocialMediaSharedError> getTwitterSharedErrors()
    {
        return twitterSharedErrors;
    }

    public void setTwitterSharedErrors( List<SocialMediaSharedError> twitterSharedErrors )
    {
        this.twitterSharedErrors = twitterSharedErrors;
    }

    public List<SocialMediaSharedError> getLinkedinSharedErrors()
    {
        return linkedinSharedErrors;
    }

    public void setLinkedinSharedErrors( List<SocialMediaSharedError> linkedinSharedErrors )
    {
        this.linkedinSharedErrors = linkedinSharedErrors;
    }

    public List<SocialMediaSharedError> getGooglePlusSharedErrors()
    {
        return googlePlusSharedErrors;
    }

    public void setGooglePlusSharedErrors( List<SocialMediaSharedError> googlePlusSharedErrors )
    {
        this.googlePlusSharedErrors = googlePlusSharedErrors;
    }

    public List<SocialMediaSharedError> getYelpSharedErrors()
    {
        return yelpSharedErrors;
    }

    public void setYelpSharedErrors( List<SocialMediaSharedError> yelpSharedErrors )
    {
        this.yelpSharedErrors = yelpSharedErrors;
    }

}
