package com.realtech.socialsurvey.api.models;

import java.util.List;

public class ReviewVO {

	
	private String source;
	private String reviewDate;
	private String review;
	private String rating;
	private String summary;
	private String description;
	private boolean isReportedAbusive;
	private boolean isCRMVerified;
	private List<SurveyResponseVO> surveyResponses;
	
	
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
	public List<SurveyResponseVO> getSurveyResponses()
    {
        return surveyResponses;
    }
    public void setSurveyResponses( List<SurveyResponseVO> surveyResponses )
    {
        this.surveyResponses = surveyResponses;
    }
    public String getReview()
    {
        return review;
    }
    public void setReview( String review )
    {
        this.review = review;
    }
}
