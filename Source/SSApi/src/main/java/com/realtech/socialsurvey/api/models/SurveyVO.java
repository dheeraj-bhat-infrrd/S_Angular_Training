package com.realtech.socialsurvey.api.models;

public class SurveyVO {
	
	private long surveyId;
	private TransactionInfo transactionInfo;
	private ReviewVO review;
	
	
	public long getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(long surveyId) {
		this.surveyId = surveyId;
	}
	public TransactionInfo getTransactionInfo() {
		return transactionInfo;
	}
	public void setTransactionInfo(TransactionInfo transactionInfo) {
		this.transactionInfo = transactionInfo;
	}
	public ReviewVO getReview() {
		return review;
	}
	public void setReview(ReviewVO review) {
		this.review = review;
	}

}
