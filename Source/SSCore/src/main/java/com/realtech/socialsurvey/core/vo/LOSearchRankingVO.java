package com.realtech.socialsurvey.core.vo;

import java.util.List;

public class LOSearchRankingVO {

	private long agentId;
	private String profileImageUrl;
	private String profileImageUrlThumbnail;
    private String profileImageUrlRectangularThumbnail;
    private boolean isProfileImageProcessed;
	private String name;
	private String title;
	private String vertical;
	private List<String> NMLS;
	private String companyName;
	private double rating;
	private long numberOfReviews;
	private long numberOfRecentReviews;
	private String latestReview;
	private long searchRank;
	private String profileUrl;
    private LOSearchContactAndDistanceVO loSearchContactAndDistanceVO;

	public long getAgentId() {
		return agentId;
	}

	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LOSearchContactAndDistanceVO getLoSearchContactAndDistanceVO() {
		return loSearchContactAndDistanceVO;
	}

	public void setLoSearchContactAndDistanceVO(LOSearchContactAndDistanceVO loSearchContactAndDistanceVO) {
		this.loSearchContactAndDistanceVO = loSearchContactAndDistanceVO;
	}

	public String getVertical() {
		return vertical;
	}

	public void setVertical(String vertical) {
		this.vertical = vertical;
	}

	public List<String> getNMLS() {
		return NMLS;
	}

	public void setNMLS(List<String> nMLS) {
		NMLS = nMLS;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public long getNumberOfReviews() {
		return numberOfReviews;
	}

	public void setNumberOfReviews(long numberOfReviews) {
		this.numberOfReviews = numberOfReviews;
	}

	public long getNumberOfRecentReviews() {
		return numberOfRecentReviews;
	}

	public void setNumberOfRecentReviews(long numberOfRecentReviews) {
		this.numberOfRecentReviews = numberOfRecentReviews;
	}

	public String getLatestReview() {
		return latestReview;
	}

	public void setLatestReview(String latestReview) {
		this.latestReview = latestReview;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public String getProfileImageUrlThumbnail() {
		return profileImageUrlThumbnail;
	}

	public void setProfileImageUrlThumbnail(String profileImageUrlThumbnail) {
		this.profileImageUrlThumbnail = profileImageUrlThumbnail;
	}

	public String getProfileImageUrlRectangularThumbnail() {
		return profileImageUrlRectangularThumbnail;
	}

	public void setProfileImageUrlRectangularThumbnail(String profileImageUrlRectangularThumbnail) {
		this.profileImageUrlRectangularThumbnail = profileImageUrlRectangularThumbnail;
	}

	public boolean isProfileImageProcessed() {
		return isProfileImageProcessed;
	}

	public void setProfileImageProcessed(boolean isProfileImageProcessed) {
		this.isProfileImageProcessed = isProfileImageProcessed;
	}

	public long getSearchRank() {
		return searchRank;
	}

	public void setSearchRank(long searchRank) {
		this.searchRank = searchRank;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	
	
}
