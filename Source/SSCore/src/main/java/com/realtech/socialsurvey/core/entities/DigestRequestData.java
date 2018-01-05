package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.Set;


public class DigestRequestData implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String profileLevel;
    private long entityId;
    private String entityName;
    private Set<String> recipientMailIds;


    public String getProfileLevel()
    {
        return profileLevel;
    }


    public void setProfileLevel( String profileLevel )
    {
        this.profileLevel = profileLevel;
    }


    public long getEntityId()
    {
        return entityId;
    }


    public void setEntityId( long entityId )
    {
        this.entityId = entityId;
    }


    public String getEntityName()
    {
        return entityName;
    }


    public void setEntityName( String entityName )
    {
        this.entityName = entityName;
    }


    public Set<String> getRecipientMailIds()
    {
        return recipientMailIds;
    }


    public void setRecipientMailIds( Set<String> recipientMailIds )
    {
        this.recipientMailIds = recipientMailIds;
    }


    @Override
    public String toString()
    {
        return "DigestRequestData [profileLevel=" + profileLevel + ", entityId=" + entityId + ", entityName=" + entityName
            + ", recipientMailIds=" + recipientMailIds + "]";
    }
}
