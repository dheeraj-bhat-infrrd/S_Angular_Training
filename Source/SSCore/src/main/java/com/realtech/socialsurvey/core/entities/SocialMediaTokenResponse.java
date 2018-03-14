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
    private SocialMediaTokensForSM socialMediaTokens;
    private ProfileType profileType;
    private String profileImageUrl;

    public long getIden()
    {
        return iden;
    }


    public void setIden( long iden )
    {
        this.iden = iden;
    }

    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }

    
    public ProfileType getProfileType()
    {
        return profileType;
    }


    public void setProfileType( ProfileType profileType )
    {
        this.profileType = profileType;
    }


    public SocialMediaTokensForSM getSocialMediaTokens()
    {
        return socialMediaTokens;
    }


    public void setSocialMediaTokens( SocialMediaTokensForSM socialMediaTokens )
    {
        this.socialMediaTokens = socialMediaTokens;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
