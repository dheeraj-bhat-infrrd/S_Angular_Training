package com.realtech.socialsurvey.core.feed.impl;

import java.sql.Timestamp;
import java.util.Date;

public class GooglePlusPost {
	
	private String id;
	private String post;
	private Date createdOn;
	private Timestamp lastUpdatedOn;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public Timestamp getLastUpdatedOn() {
		return lastUpdatedOn;
	}
	public void setLastUpdatedOn(Timestamp lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}
}
