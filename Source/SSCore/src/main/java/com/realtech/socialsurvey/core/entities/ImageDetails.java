package com.realtech.socialsurvey.core.entities;

import java.sql.Timestamp;

public class ImageDetails {

	private String imageName;
	private Timestamp createdOn;
	private Timestamp modifiedOn;
	private Timestamp processedOn;

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public Timestamp getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public Timestamp getProcessedOn() {
		return processedOn;
	}

	public void setProcessedOn(Timestamp processedOn) {
		this.processedOn = processedOn;
	}

}
