package com.realtech.socialsurvey.api.models;

public class ReviewVO {

	
	private String source;
	private String reviewDate;
	private String rating;
	private String summary;
	private String description;
	private boolean isReportedAbusive;
	private boolean isCRMVerified;
	
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getReviewDate() {
		return reviewDate;
	}
	public void setReviewDate(String reviewDate) {
		this.reviewDate = reviewDate;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isReportedAbusive() {
		return isReportedAbusive;
	}
	public void setIsReportedAbusive(boolean isReportedAbusive) {
		this.isReportedAbusive = isReportedAbusive;
	}
	public boolean isCRMVerified() {
		return isCRMVerified;
	}
	public void setIsCRMVerified(boolean isCRMVerified) {
		this.isCRMVerified = isCRMVerified;
	}
}
