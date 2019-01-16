package com.realtech.socialsurvey.stream.entities;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "failed_messages")
public class FailedSurveyProcessor extends FailedMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private SurveyData data;

	private boolean isRetried;

	private String postId;
	
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

	
}
