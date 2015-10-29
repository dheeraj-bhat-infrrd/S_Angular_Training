package com.realtech.socialsurvey.core.entities;

/**
 * Stores an Entity along with it's profile image url
 *
 */
public class ProfileImageUrlData
{
    private String EntityType;
    private long EntityId;
    private String profileImageUrl;


    public String getEntityType()
    {
        return EntityType;
    }


    public void setEntityType( String entityType )
    {
        EntityType = entityType;
    }


    public long getEntityId()
    {
        return EntityId;
    }


    public void setEntityId( long entityId )
    {
        EntityId = entityId;
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
