package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.realtech.socialsurvey.core.enums.ProfileType;
import com.realtech.socialsurvey.core.enums.SocialFeedStatus;
import com.realtech.socialsurvey.core.enums.SocialFeedType;


/**
 * @author manish
 *
 * @param <T>
 */
@Document
public class SocialResponseObject<T> implements Serializable
{
    private static final long serialVersionUID = 1L;


    public SocialResponseObject()
    {}


    public SocialResponseObject( long companyId, SocialFeedType type, String text, T response, long duplicateCount )
    {
        this.companyId = companyId;
        this.type = type;
        this.text = text;
        this.response = response;
        this.duplicateCount = duplicateCount;
    }


    private String id;
    private String postId;
    private String text;
    private String textHighlighted;
    private String pageLink;
    private List<String> pictures;
    private long updatedTime;
    private long createdTime;
    private String ownerName;
    private String ownerEmail;

    private T response;
    private SocialFeedType type;
    private boolean flagged;
    private SocialFeedStatus status;
    private long companyId;
    private long regionId;
    private long branchId;
    private long agentId;
    private ProfileType profileType;

    private int hash;
    private long duplicateCount;
    private List<String> foundKeywords;
    private List<ActionHistory> actionHistory;
    private boolean isRetried;

    public T getResponse()
    {
        return response;
    }


    public void setResponse( T response )
    {
        this.response = response;
    }


    public String getId()
    {
        return id;
    }


    public void setId( String id )
    {
        this.id = id;
    }


    public String getPostId()
    {
        return postId;
    }


    public void setPostId( String postId )
    {
        this.postId = postId;
    }


    public String getText()
    {
        return text;
    }


    public void setText( String text )
    {
        this.text = text;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public String getOwnerName()
    {
        return ownerName;
    }


    public void setOwnerName( String ownerName )
    {
        this.ownerName = ownerName;
    }


    public SocialFeedType getType()
    {
        return type;
    }


    public void setType( SocialFeedType type )
    {
        this.type = type;
    }


    public boolean isFlagged()
    {
        return flagged;
    }


    public void setFlagged( boolean flagged )
    {
        this.flagged = flagged;
    }


    public SocialFeedStatus getStatus()
    {
        return status;
    }


    public void setStatus( SocialFeedStatus status )
    {
        this.status = status;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public long getRegionId()
    {
        return regionId;
    }


    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }


    public long getBranchId()
    {
        return branchId;
    }


    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }


    public long getAgentId()
    {
        return agentId;
    }


    public void setAgentId( long agentId )
    {
        this.agentId = agentId;
    }


    public int getHash()
    {
        return hash;
    }


    public void setHash( int hash )
    {
        this.hash = hash;
    }


    public long getDuplicateCount()
    {
        return duplicateCount;
    }


    public void setDuplicateCount( long duplicateCount )
    {
        this.duplicateCount = duplicateCount;
    }


    public List<String> getFoundKeywords()
    {
        return foundKeywords;
    }


    public void setFoundKeywords( List<String> foundKeywords )
    {
        this.foundKeywords = foundKeywords;
    }


    public List<ActionHistory> getActionHistory()
    {
        return actionHistory;
    }


    public void setActionHistory( List<ActionHistory> actionHistory )
    {
        this.actionHistory = actionHistory;
    }

    public long getUpdatedTime()
    {
        return updatedTime;
    }


    public void setUpdatedTime( long updatedTime )
    {
        this.updatedTime = updatedTime;
    }


    public long getCreatedTime()
    {
        return createdTime;
    }


    public void setCreatedTime( long createdTime )
    {
        this.createdTime = createdTime;
    }

    public boolean isRetried() {
        return isRetried;
    }

    public void setIsRetried(boolean retried) {
        isRetried = retried;
    }

    public ProfileType getProfileType()
    {
        return profileType;
    }


    public void setProfileType( ProfileType profileType )
    {
        this.profileType = profileType;
    }


    public String getOwnerEmail()
    {
        return ownerEmail;
    }


    public void setOwnerEmail( String ownerEmail )
    {
        this.ownerEmail = ownerEmail;
    }
    
    public String getTextHighlighted()
    {
        return textHighlighted;
    }


    public void setTextHighlighted( String textHighlighted )
    {
        this.textHighlighted = textHighlighted;
    }


    public String getPageLink()
    {
        return pageLink;
    }


    public void setPageLink( String pageLink )
    {
        this.pageLink = pageLink;
    }


    @Override
    public String toString()
    {
        return "SocialResponseObject [id=" + id + ", postId=" + postId + ", text=" + text + ", textHighlighted="
            + textHighlighted + ", pageLink=" + pageLink + ", pictures=" + pictures + ", updatedTime=" + updatedTime
            + ", createdTime=" + createdTime + ", ownerName=" + ownerName + ", ownerEmail=" + ownerEmail + ", response="
            + response + ", type=" + type + ", flagged=" + flagged + ", status=" + status + ", companyId=" + companyId
            + ", regionId=" + regionId + ", branchId=" + branchId + ", agentId=" + agentId + ", profileType=" + profileType
            + ", hash=" + hash + ", duplicateCount=" + duplicateCount + ", foundKeywords=" + foundKeywords + ", actionHistory="
            + actionHistory + ", isRetried=" + isRetried + "]";
    }
    
}