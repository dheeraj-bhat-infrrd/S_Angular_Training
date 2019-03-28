package com.realtech.socialsurvey.core.entities.integration.external.linkedin;

import java.io.Serializable;

public class LinkedInV2Content implements Serializable {
	
	private String title;
	private String description;
	private ContentEntity[] contentEntities;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ContentEntity[] getContentEntities() {
		return contentEntities;
	}
	public void setContentEntities(ContentEntity[] contentEntities) {
		this.contentEntities = contentEntities;
	}
	

}
