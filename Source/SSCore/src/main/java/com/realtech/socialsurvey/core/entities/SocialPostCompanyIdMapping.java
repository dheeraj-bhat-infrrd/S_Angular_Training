package com.realtech.socialsurvey.core.entities;


/**
 * Holds entityId:CompanyId map and the entity type for SetCompanyIdInSocialPosts
 *
 */
public class SocialPostCompanyIdMapping
{
    private String entityType;
    private Long companyId;
    private Long entityId;


    public Long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( Long companyId )
    {
        this.companyId = companyId;
    }


    public Long getEntityId()
    {
        return entityId;
    }


    public void setEntityId( Long entityId )
    {
        this.entityId = entityId;
    }


    public String getEntityType()
    {
        return entityType;
    }


    public void setEntityType( String entityType )
    {
        this.entityType = entityType;
    }
}
