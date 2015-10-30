package com.realtech.socialsurvey.core.entities;

/**
 * Stores an Entity along with it's profile image url
 *
 */
public class ProfileImageUrlData
{
    private String entityType;
    private long entityId;
    private String profileImageUrl;


    public String getEntityType()
    {
        return entityType;
    }


    public void setEntityType( String entityType )
    {
        this.entityType = entityType;
    }


    public long getEntityId()
    {
        return entityId;
    }


    public void setEntityId( long entityId )
    {
        this.entityId = entityId;
    }


    public String getProfileImageUrl()
    {
        return profileImageUrl;
    }


    public void setProfileImageUrl( String profileImageUrl )
    {
        this.profileImageUrl = profileImageUrl;
    }
}
