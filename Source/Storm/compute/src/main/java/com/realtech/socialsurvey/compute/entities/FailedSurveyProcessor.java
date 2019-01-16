package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;


@Entity("failed_messages")
public class FailedSurveyProcessor extends FailedMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	@Embedded
	private SurveyData data;
	
	private boolean isRetried;

	private String postId;
	
	private String Id;

	public SurveyData getData() {
		return data;
	}

	public void setData(SurveyData data) {
		this.data = data;
	}

	public boolean isRetried() {
		return isRetried;
	}

	public void setRetried(boolean isRetried) {
		this.isRetried = isRetried;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String get_id() {
		return Id;
	}

	public void set_id(String _id) {
		this.Id = _id;
	}

}
