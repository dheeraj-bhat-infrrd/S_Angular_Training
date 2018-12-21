package com.realtech.socialsurvey.core.vo;

import java.util.List;

import com.google.maps.model.LatLng;

public class AdvancedSearchVO {

	private String sortBy;
	private long distanceCriteria;
	private long ratingCriteria;
	private long reviewCountCriteria;
	private List<String> categoryFilterList;
	private String profileFilter;
	private LatLng nearLocation;
	private String findBasedOn;
	private long startIndex;
	private long batchSize;
	private String companyProfileName;

	public long getDistanceCriteria() {
		return distanceCriteria;
	}

	public void setDistanceCriteria(long distanceCriteria) {
		this.distanceCriteria = distanceCriteria;
	}

	public long getRatingCriteria() {
		return ratingCriteria;
	}

	public void setRatingCriteria(long ratingCriteria) {
		this.ratingCriteria = ratingCriteria;
	}

	public long getReviewCountCriteria() {
		return reviewCountCriteria;
	}

	public void setReviewCountCriteria(long reviewCountCriteria) {
		this.reviewCountCriteria = reviewCountCriteria;
	}

	public List<String> getCategoryFilterList() {
		return categoryFilterList;
	}

	public void setCategoryFilterList(List<String> categoryFilterList) {
		this.categoryFilterList = categoryFilterList;
	}

	public String getProfileFilter() {
		return profileFilter;
	}

	public void setProfileFilter(String profileFilter) {
		this.profileFilter = profileFilter;
	}

	public LatLng getNearLocation() {
		return nearLocation;
	}

	public void setNearLocation(LatLng nearLocation) {
		this.nearLocation = nearLocation;
	}

	public String getFindBasedOn() {
		return findBasedOn;
	}

	public void setFindBasedOn(String findBasedOn) {
		this.findBasedOn = findBasedOn;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public long getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(long startIndex) {
		this.startIndex = startIndex;
	}

	public long getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(long batchSize) {
		this.batchSize = batchSize;
	}

	public String getCompanyProfileName() {
		return companyProfileName;
	}

	public void setCompanyProfileName(String companyProfileName) {
		this.companyProfileName = companyProfileName;
	}

}
