package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.List;

import com.realtech.socialsurvey.core.enums.SocialFeedStatus;
import com.realtech.socialsurvey.core.enums.SocialFeedType;

public class SocialMonitorFeedData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String postId = "";
	private long companyId = 0;
	private long regionId = 0;
	private long branchId = 0;
	private long agentId = 0;
	private String text = "";
	private List<String> pictures;
	private long updatedOn;
	private String ownerName = "";
	private String ownerProfileImage="";
	private SocialFeedType type;
	private SocialFeedStatus status;
	private List<ActionHistory> actionHistory;
	private List<String> foundKeywords;
	private long duplicateCount;
    private String pageLink;
    private String postLink;
    private String textHighlighted;
    private boolean fromTrustedSource;
    private String postSource;

	public String getPageLink()
    {
        return pageLink;
    }

    public void setPageLink( String pageLink )
    {
        this.pageLink = pageLink;
    }

	public SocialFeedType getType() {
		return type;
	}

	public void setType(SocialFeedType type) {
		this.type = type;
	}

	public SocialFeedStatus getStatus() {
		return status;
	}

	public void setStatus(SocialFeedStatus status) {
		this.status = status;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public long getRegionId() {
		return regionId;
	}

	public void setRegionId(long regionId) {
		this.regionId = regionId;
	}

	public long getBranchId() {
		return branchId;
	}

	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}

	public long getAgentId() {
		return agentId;
	}

	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getPictures() {
		return pictures;
	}

	public void setPictures(List<String> pictures) {
		this.pictures = pictures;
	}

	public long getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(long updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public List<ActionHistory> getActionHistory() {
		return actionHistory;
	}

	public void setActionHistory(List<ActionHistory> actionHistory) {
		this.actionHistory = actionHistory;
	}

	public long getDuplicateCount() {
		return duplicateCount;
	}

	public void setDuplicateCount(long duplicateCount) {
		this.duplicateCount = duplicateCount;
	}

	public List<String> getFoundKeywords() {
		return foundKeywords;
	}

	public void setFoundKeywords(List<String> foundKeywords) {
		this.foundKeywords = foundKeywords;
	}

	public String getOwnerProfileImage() {
		return ownerProfileImage;
	}

	public void setOwnerProfileImage(String ownerProfileImage) {
		this.ownerProfileImage = ownerProfileImage;
	}

	public String getTextHighlighted()
    {
        return textHighlighted;
    }

    public void setTextHighlighted( String textHighlighted )
    {
        this.textHighlighted = textHighlighted;
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

	@Override
    public String toString()
    {
        return "SocialMonitorFeedData [postId=" + postId + ", companyId=" + companyId + ", regionId=" + regionId + ", branchId="
            + branchId + ", agentId=" + agentId + ", text=" + text + ", pictures=" + pictures + ", updatedOn=" + updatedOn
            + ", ownerName=" + ownerName + ", ownerProfileImage=" + ownerProfileImage + ", type=" + type + ", status=" + status
            + ", actionHistory=" + actionHistory + ", foundKeywords=" + foundKeywords + ", duplicateCount=" + duplicateCount
            + ", pageLink=" + pageLink + ", postLink=" + postLink + ", textHighlighted=" + textHighlighted
            + ", fromTrustedSource=" + fromTrustedSource + ", postSource=" + postSource + "]";
    }
}
