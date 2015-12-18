package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class CompanyMediaPostErrors
{
    private long companyId;

    private List<SocialMediaSharedError> facebookSharedErrors;
    
    private List<SocialMediaSharedError> twitterSharedErrors;
    
    private List<SocialMediaSharedError> linkedinSharedErrors;

    public long getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
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
