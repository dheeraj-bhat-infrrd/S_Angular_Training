package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;

/**
 * @author manish
 *Entity for social media response
 */
public class SocialMediaTokenResponse implements Serializable
{
    private static final long serialVersionUID = 1L;
    private long iden;
    private long companyId;
    private SocialMediaTokens socialMediaTokens;
    private String profileType;


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


    public String getProfileType()
    {
        return profileType;
    }


    public void setProfileType( String profileType )
    {
        this.profileType = profileType;
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

}
