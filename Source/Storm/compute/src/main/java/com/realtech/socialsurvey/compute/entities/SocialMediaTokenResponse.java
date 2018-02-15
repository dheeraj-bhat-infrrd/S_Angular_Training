package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;

import com.realtech.socialsurvey.compute.enums.ProfileType;


/**
 *Entity for social media response
 *@author manish
 */
public class SocialMediaTokenResponse implements Serializable
{
    private static final long serialVersionUID = 1L;
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


    public void setSocialMediaTokens( SocialMediaTokens socialMediaTokens )
    {
        this.socialMediaTokens = socialMediaTokens;
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
}
