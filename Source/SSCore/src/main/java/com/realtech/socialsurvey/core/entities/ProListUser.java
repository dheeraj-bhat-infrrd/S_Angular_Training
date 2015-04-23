package com.realtech.socialsurvey.core.entities;

public class ProListUser {
	private long userId;
	private String displayName;
	private String profileUrl;
	private String profileImageUrl;
	private String title;
	private String location;
	private String industry;
	private String aboutMe;
	private String profileName;
	private String emailId;
	private long reviewCount;
	private double reviewScore;
	
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getProfileUrl() {
		return profileUrl;
	}
	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getAboutMe() {
		return aboutMe;
	}
	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}
	public long getReviewCount() {
		return reviewCount;
	}
	public void setReviewCount(long reviewCount) {
		this.reviewCount = reviewCount;
	}
	public double getReviewScore() {
		return reviewScore;
	}
	public void setReviewScore(double reviewScore) {
		this.reviewScore = reviewScore;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	
}
