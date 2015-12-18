package com.realtech.socialsurvey.core.entities;

import java.util.List;

/**
 * 
 * @author rohit
 *
 */
public class RegionMediaPostErrors
{

    private long regionId;

    private List<SocialMediaSharedError> facebookSharedErrors;

    private List<SocialMediaSharedError> twitterSharedErrors;

    private List<SocialMediaSharedError> linkedinSharedErrors;

    public long getRegionId()
    {
        return regionId;
    }

    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
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
}
