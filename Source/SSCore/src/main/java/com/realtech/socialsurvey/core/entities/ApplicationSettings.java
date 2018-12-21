package com.realtech.socialsurvey.core.entities;

import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class ApplicationSettings {

	
	
	private LOSearchEngine loSearchEngine;
	private long createdOn;
	private long modifiedOn;

	
	// private constructor so no one can create instance 
	private ApplicationSettings() {}
	
	public LOSearchEngine getLoSearchEngine() {
		return loSearchEngine;
	}

	public void setLoSearchEngine(LOSearchEngine loSearchEngine) {
		this.loSearchEngine = loSearchEngine;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public long getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(long modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	
	
}
