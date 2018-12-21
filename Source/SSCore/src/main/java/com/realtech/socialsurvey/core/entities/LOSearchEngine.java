package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class LOSearchEngine {

	private List<DisplayData> sortingOrder;
	private List<DisplayData> distanceCriteria;
	private List<DisplayData> ratingCriteria;
	private List<DisplayData> reviewCriteria;
	private List<DisplayData> profilesCriteria;
	
	private int defaultOffset;
	private float completionRatio;
	private int spsOffset;
	private float spsRatio;
	
	private int recentSurveyDays;

	public List<DisplayData> getSortingOrder() {
		return sortingOrder;
	}

	public void setSortingOrder(List<DisplayData> sortingOrder) {
		this.sortingOrder = sortingOrder;
	}

	public List<DisplayData> getDistanceCriteria() {
		return distanceCriteria;
	}

	public void setDistanceCriteria(List<DisplayData> distanceCriteria) {
		this.distanceCriteria = distanceCriteria;
	}

	public List<DisplayData> getRatingCriteria() {
		return ratingCriteria;
	}

	public void setRatingCriteria(List<DisplayData> ratingCriteria) {
		this.ratingCriteria = ratingCriteria;
	}

	public List<DisplayData> getReviewCriteria() {
		return reviewCriteria;
	}

	public void setReviewCriteria(List<DisplayData> reviewCriteria) {
		this.reviewCriteria = reviewCriteria;
	}

	public List<DisplayData> getProfilesCriteria() {
		return profilesCriteria;
	}

	public void setProfilesCriteria(List<DisplayData> profilesCriteria) {
		this.profilesCriteria = profilesCriteria;
	}

	public int getDefaultOffset() {
		return defaultOffset;
	}

	public void setDefaultOffset(int defaultOffset) {
		this.defaultOffset = defaultOffset;
	}

	public float getCompletionRatio() {
		return completionRatio;
	}

	public void setCompletionRatio(float completionRatio) {
		this.completionRatio = completionRatio;
	}

	public int getSpsOffset() {
		return spsOffset;
	}

	public void setSpsOffset(int spsOffset) {
		this.spsOffset = spsOffset;
	}

	public float getSpsRatio() {
		return spsRatio;
	}

	public void setSpsRatio(float spsRatio) {
		this.spsRatio = spsRatio;
	}

	public int getRecentSurveyDays() {
		return recentSurveyDays;
	}

	public void setRecentSurveyDays(int recentSurveyDays) {
		this.recentSurveyDays = recentSurveyDays;
	}

}
