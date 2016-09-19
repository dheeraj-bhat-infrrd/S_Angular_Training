package com.realtech.socialsurvey.api.models;

public class SurveyGetVO {

	private long surveyId;
	private String reviewId;
	private TransactionInfoGetVO transactionInfo;
	private ServiceProviderInfo serviceProviderInfo;
	private ReviewVO review;

	public String getReviewId() {
		return reviewId;
	}

	public void setReviewId(String reviewId) {
		this.reviewId = reviewId;
	}

	public long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(long surveyId) {
		this.surveyId = surveyId;
	}

	public TransactionInfoGetVO getTransactionInfo() {
		return transactionInfo;
	}

	public void setTransactionInfo(TransactionInfoGetVO transactionInfo) {
		this.transactionInfo = transactionInfo;
	}

	public ServiceProviderInfo getServiceProviderInfo() {
		return serviceProviderInfo;
	}

	public void setServiceProviderInfo(ServiceProviderInfo serviceProviderInfo) {
		this.serviceProviderInfo = serviceProviderInfo;
	}

	public ReviewVO getReview() {
		return review;
	}

	public void setReview(ReviewVO review) {
		this.review = review;
	}

}
