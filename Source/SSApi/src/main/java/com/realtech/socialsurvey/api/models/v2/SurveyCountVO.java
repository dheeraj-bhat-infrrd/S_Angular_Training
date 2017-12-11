package com.realtech.socialsurvey.api.models.v2;

public class SurveyCountVO {

	private int noOfReviews;
	
	private float avgScore;

	public int getNoOfReviews() {
		return noOfReviews;
	}

	public void setNoOfReviews(int noOfReviews) {
		this.noOfReviews = noOfReviews;
	}

	public float getAvgScore() {
		return avgScore;
	}

	public void setAvgScore(float avgScore) {
		this.avgScore = avgScore;
	}
}
