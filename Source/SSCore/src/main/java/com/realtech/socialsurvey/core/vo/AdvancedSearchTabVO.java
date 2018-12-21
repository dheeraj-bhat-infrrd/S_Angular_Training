package com.realtech.socialsurvey.core.vo;

import java.util.List;

import com.realtech.socialsurvey.core.entities.DisplayData;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;

public class AdvancedSearchTabVO {

	private List<DisplayData> sortingOrder;
	private List<DisplayData> distanceCriteria;
	private List<DisplayData> ratingCriteria;
	private List<DisplayData> reviewCriteria;
	private List<DisplayData> profilesCriteria;
	private List<VerticalsMaster> verticals;

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

	public List<VerticalsMaster> getVerticals() {
		return verticals;
	}

	public void setVerticals(List<VerticalsMaster> verticals) {
		this.verticals = verticals;
	}

}
