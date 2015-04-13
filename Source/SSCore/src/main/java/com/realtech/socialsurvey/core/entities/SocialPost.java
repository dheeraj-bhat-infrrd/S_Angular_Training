package com.realtech.socialsurvey.core.entities;

public class SocialPost {
	
	private String source;
	private long userId;
	private long timeInMillis;
	private String postText;
	private String postedBy;

	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
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
}
