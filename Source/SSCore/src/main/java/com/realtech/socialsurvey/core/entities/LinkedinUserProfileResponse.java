package com.realtech.socialsurvey.core.entities;

public class LinkedinUserProfileResponse {
	
	String firstName;
	String headLine;
	String id;
	LinkedinUserProfileUrlResponse siteStandardProfileRequest;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getHeadLine() {
		return headLine;
	}
	public void setHeadLine(String headLine) {
		this.headLine = headLine;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public LinkedinUserProfileUrlResponse getSiteStandardProfileRequest() {
		return siteStandardProfileRequest;
	}
	public void setSiteStandardProfileRequest(LinkedinUserProfileUrlResponse siteStandardProfileRequest) {
		this.siteStandardProfileRequest = siteStandardProfileRequest;
	}
}