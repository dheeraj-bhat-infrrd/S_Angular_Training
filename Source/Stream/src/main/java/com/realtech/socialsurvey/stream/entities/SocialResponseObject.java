package com.realtech.socialsurvey.stream.entities;

import java.io.Serializable;
import java.util.List;

import com.realtech.socialsurvey.stream.enums.ProfileType;
import com.realtech.socialsurvey.stream.enums.SocialFeedStatus;
import com.realtech.socialsurvey.stream.enums.SocialFeedType;


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


    public SocialResponseObject( long companyId, SocialFeedType type, String text, T response, long duplicateCount, SocialFeedStatus status )
    {
        this.companyId = companyId;
        this.type = type;
        this.text = text;
        this.response = response;
        this.duplicateCount = duplicateCount;
        this.status = status;
    }


    private String id;
    private String postId;
    private String text;
    private String textHighlighted;
    private String pageLink;
    private String postLink;
    private List<SocialFeedMediaEntity> mediaEntities;
    private long updatedTime;
    private long createdTime;
    private String ownerName;
    private String ownerEmail;

    private T response;
    private SocialFeedType type;
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
    private boolean fromTrustedSource;
    private String postSource;
    private boolean isDuplicate;
    private int totalLikesCount;
    private int totalCommentsCount;
    private long retweetCount;
    
    private boolean videoFeed;

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


    public void setActionHistory( List<ActionHistory> actionHistory )
    {
        this.actionHistory = actionHistory;
    }


    public ProfileType getProfileType()
    {
        return profileType;
    }


    public boolean getIsRetried()
    {
        return isRetried;
    }


    public void setIsRetried( boolean retried )
    {
        isRetried = retried;
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

    public String getPostLink()
    {
        return postLink;
    }


    public void setPostLink( String postLink )
    {
        this.postLink = postLink;
    }
    
    public boolean isFromTrustedSource() {
        return fromTrustedSource;
    }


    public void setFromTrustedSource(boolean fromTrustedSource) {
        this.fromTrustedSource = fromTrustedSource;
    } 
    
    public String getPostSource() {
        return postSource;
    }


    public void setPostSource(String postSource) {
        this.postSource = postSource;
    }


    public boolean isDuplicate()
    {
        return isDuplicate;
    }


    public void setDuplicate( boolean duplicate )
    {
        isDuplicate = duplicate;
    }


    public int getTotalLikesCount()
    {
        return totalLikesCount;
    }


    public void setTotalLikesCount( int totalLikesCount )
    {
        this.totalLikesCount = totalLikesCount;
    }


    public int getTotalCommentsCount()
    {
        return totalCommentsCount;
    }


    public void setTotalCommentsCount( int totalCommentsCount )
    {
        this.totalCommentsCount = totalCommentsCount;
    }


    public long getRetweetCount()
    {
        return retweetCount;
    }


    public void setRetweetCount( long retweetCount )
    {
        this.retweetCount = retweetCount;
    }
    

    public List<SocialFeedMediaEntity> getMediaEntities()
    {
        return mediaEntities;
    }


    public void setMediaEntities( List<SocialFeedMediaEntity> mediaEntities )
    {
        this.mediaEntities = mediaEntities;
    }


    public boolean isVideoFeed() {
		return videoFeed;
	}


	public void setVideoFeed(boolean videoFeed) {
		this.videoFeed = videoFeed;
	}


	@Override
    public String toString()
    {
        return "SocialResponseObject [id=" + id + ", postId=" + postId + ", text=" + text + ", textHighlighted="
            + textHighlighted + ", pageLink=" + pageLink + ", postLink=" + postLink + ", mediaEntities=" + mediaEntities
            + ", updatedTime=" + updatedTime + ", createdTime=" + createdTime + ", ownerName=" + ownerName + ", ownerEmail="
            + ownerEmail + ", response=" + response + ", type=" + type + ", status=" + status + ", companyId=" + companyId
            + ", regionId=" + regionId + ", branchId=" + branchId + ", agentId=" + agentId + ", profileType=" + profileType
            + ", hash=" + hash + ", duplicateCount=" + duplicateCount + ", foundKeywords=" + foundKeywords + ", actionHistory="
            + actionHistory + ", isRetried=" + isRetried + ", fromTrustedSource=" + fromTrustedSource + ", postSource="
            + postSource + ", isDuplicate=" + isDuplicate + ", totalLikesCount=" + totalLikesCount + ", totalCommentsCount="
            + totalCommentsCount + ", retweetCount=" + retweetCount + ", videoFeed=" + videoFeed + "]";
    }
}