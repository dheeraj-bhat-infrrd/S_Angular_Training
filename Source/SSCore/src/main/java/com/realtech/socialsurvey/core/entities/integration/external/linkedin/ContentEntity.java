package com.realtech.socialsurvey.core.entities.integration.external.linkedin;

import java.io.Serializable;

public class ContentEntity implements Serializable{
	
	private String entityLocation;
	private Thumbnails[] thumbnails;
	
	public String getEntityLocation() {
		return entityLocation;
	}
	public void setEntityLocation(String entityLocation) {
		this.entityLocation = entityLocation;
	}
	public Thumbnails[] getThumbnails() {
		return thumbnails;
	}
	public void setThumbnails(Thumbnails[] thumbnails) {
		this.thumbnails = thumbnails;
	}
	

}
