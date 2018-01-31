package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.realtech.socialsurvey.core.enums.SocialFeedStatus;
import com.realtech.socialsurvey.core.enums.SocialFeedType;


/**
 * @author manish
 *
 * @param <T>
 */
public class SocialResponseObject<T> implements Serializable
{
    private static final long serialVersionUID = 1L;


    public SocialResponseObject()
    {}


    public SocialResponseObject( long companyId, SocialFeedType type, String text, T response )
    {
        this.type = type;
        this.text = text;
        this.companyId = companyId;
        this.response = response;
    }


    // Common fields
    private String id;
    private String postId;
    private String text;
    private String picture;
    private Timestamp updatedTime;
    private String ownerName;

    private T response;
    private SocialFeedType type;
    private boolean flagged;
    private SocialFeedStatus status;
    private long companyId;
    private long regionId;
    private long branchId;
    private long agentId;
    private List<String> foundKeywords;
    private List<ActionHistory> actionHistory;


    public T getResponse()
    {
        return response;
    }


    public void setResponse( T response )
    {
        this.response = response;
    }
    
    

    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public boolean isFlagged()
    {
        return flagged;
    }


    public void setFlagged( boolean flagged )
    {
        this.flagged = flagged;
    }


    public long getRegionId()
    {
        return regionId;
    }


    public long getBranchId()
    {
        return branchId;
    }


    public long getAgentId()
    {
        return agentId;
    }


    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }


    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }


    public void setAgentId( long agentId )
    {
        this.agentId = agentId;
    }


    public String getText()
    {
        return text;
    }


    public List<String> getFoundKeywords()
    {
        return foundKeywords;
    }


    public void setText( String text )
    {
        this.text = text;
    }


    public void setFoundKeywords( List<String> foundKeywords )
    {
        this.foundKeywords = foundKeywords;
    }


    public String getId()
    {
        return id;
    }


    public void setId( String id )
    {
        this.id = id;
    }


    public String getPicture()
    {
        return picture;
    }


    public Timestamp getUpdatedTime()
    {
        return updatedTime;
    }


    public String getOwnerName()
    {
        return ownerName;
    }


    public List<ActionHistory> getActionHistory()
    {
        return actionHistory;
    }


    public void setPicture( String picture )
    {
        this.picture = picture;
    }


    public void setUpdatedTime( Timestamp updatedTime )
    {
        this.updatedTime = updatedTime;
    }


    public void setOwnerName( String ownerName )
    {
        this.ownerName = ownerName;
    }


    public void setActionHistory( List<ActionHistory> actionHistory )
    {
        this.actionHistory = actionHistory;
    }


    public String getPostId()
    {
        return postId;
    }


    public void setPostId( String postId )
    {
        this.postId = postId;
    }
    
    public SocialFeedStatus getStatus()
    {
        return status;
    }


    public void setStatus( SocialFeedStatus status )
    {
        this.status = status;
    }
    
    public SocialFeedType getType()
    {
        return type;
    }


    public void setType( SocialFeedType type )
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "SocialResponseObject [id=" + id + ", postId=" + postId + ", text=" + text + ", picture=" + picture
            + ", updatedTime=" + updatedTime + ", ownerName=" + ownerName + ", response=" + response + ", type=" + type
            + ", flagged=" + flagged + ", status=" + status + ", companyId=" + companyId + ", regionId=" + regionId
            + ", branchId=" + branchId + ", agentId=" + agentId + ", foundKeywords=" + foundKeywords + ", actionHistory="
            + actionHistory + "]";
    }
}
