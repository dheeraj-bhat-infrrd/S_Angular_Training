package com.realtech.socialsurvey.core.entities;

import com.realtech.socialsurvey.core.enums.ProfileType;

/**
 * Entity for social media response
 * @author manish
 *
 */
public class SocialMediaTokenResponse
{
    private long iden;
    private long companyId;
    private SocialMediaTokens socialMediaTokens;
    private ProfileType profileType;


    public long getIden()
    {
        return iden;
    }


    public void setIden( long iden )
    {
        this.iden = iden;
    }


    public SocialMediaTokens getSocialMediaTokens()
    {
        return socialMediaTokens;
    }

    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public void setSocialMediaTokens( SocialMediaTokens socialMediaTokens )
    {
        this.socialMediaTokens = socialMediaTokens;
    }


    public ProfileType getProfileType()
    {
        return profileType;
    }


    public void setProfileType( ProfileType profileType )
    {
        this.profileType = profileType;
    }
}
