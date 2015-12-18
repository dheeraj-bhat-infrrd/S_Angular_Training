package com.realtech.socialsurvey.core.entities;

import java.util.List;


public class BranchMediaPostErrors
{
    private long branchId;

    private long regionId;

    private List<SocialMediaSharedError> facebookSharedErrors;

    private List<SocialMediaSharedError> twitterSharedErrors;

    private List<SocialMediaSharedError> linkedinSharedErrors;


    public long getBranchId()
    {
        return branchId;
    }


    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }


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
