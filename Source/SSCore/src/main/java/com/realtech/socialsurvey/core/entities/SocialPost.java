package com.realtech.socialsurvey.core.entities;

public class SocialPost {
	private String source;
	private long companyId = -1;
	private long regionId = -1;
	private long branchId = -1;
	private long agentId = -1;
	private long timeInMillis = 0;
	private long postId = -1;
	private String postText;
	private String postedBy;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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

	public long getTimeInMillis() {
		return timeInMillis;
	}

	public void setTimeInMillis(long timeInMillis) {
		this.timeInMillis = timeInMillis;
	}

	public String getPostText() {
		return postText;
	}

	public void setPostText(String postText) {
		this.postText = postText;
	}

	public String getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(String postedBy) {
		this.postedBy = postedBy;
	}

	public long getPostId() {
		return postId;
	}

	public void setPostId(long postId) {
		this.postId = postId;
	}
}
