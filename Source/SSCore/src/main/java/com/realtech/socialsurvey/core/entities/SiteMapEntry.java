package com.realtech.socialsurvey.core.entities;

/**
 * Holds fiels for the sitemap entries
 */
public class SiteMapEntry {

	private String location;
	private String lastModifiedDate; // should be YYYY-mm-dd
	private String changeFrequency;
	private float priority;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getChangeFrequency() {
		return changeFrequency;
	}

	public void setChangeFrequency(String changeFrequency) {
		this.changeFrequency = changeFrequency;
	}

	public float getPriority() {
		return priority;
	}

	public void setPriority(float priority) {
		this.priority = priority;
	}
	
	@Override
	public String toString(){
		return "location: "+location+"\t lastModifiedDate: "+lastModifiedDate+"\t changeFrequency: "+changeFrequency+"\t priority: "+priority;
	}

}
