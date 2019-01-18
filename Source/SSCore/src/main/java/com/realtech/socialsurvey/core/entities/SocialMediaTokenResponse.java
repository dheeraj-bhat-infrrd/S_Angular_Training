package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.realtech.socialsurvey.core.enums.ProfileType;
import org.springframework.data.mongodb.core.mapping.Field;


/**
 * Entity for social media response
 * @author manish
 *
 */
public class SocialMediaTokenResponse implements Serializable
{
    private static final long serialVersionUID = 1L;
    private long iden;
    private long companyId;
    private SocialMediaTokensForSM socialMediaTokens;
    private ProfileType profileType;
    private String profileImageUrl;
    @Field("contact_details")
    private ContactDetails contactDetails;
    private SocialMediaLastFetched socialMediaLastFetched;

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


    public ContactDetails getContactDetails()
    {
        return contactDetails;
    }


    public void setContactDetails( ContactDetails contactDetails )
    {
        this.contactDetails = contactDetails;
    }


    public SocialMediaLastFetched getSocialMediaLastFetched()
    {
        return socialMediaLastFetched;
    }


    public void setSocialMediaLastFetched( SocialMediaLastFetched socialMediaLastFetched )
    {
        this.socialMediaLastFetched = socialMediaLastFetched;
    }


    @Override public String toString()
    {
        return "SocialMediaTokenResponse{" + "iden=" + iden + ", companyId=" + companyId + ", socialMediaTokens="
            + socialMediaTokens + ", profileType=" + profileType + ", profileImageUrl='" + profileImageUrl + '\''
            + ", contactDetails=" + contactDetails + ", socialMediaLastFetched=" + socialMediaLastFetched + '}';
    }
}
