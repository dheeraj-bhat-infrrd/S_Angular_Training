package com.realtech.socialsurvey.api.models.v2;

public class IncompeteSurveyGetVO 
 {

	private long surveyId;
	private TransactionInfoGetV2VO transactionInfo;
	private ServiceProviderInfoV2 serviceProviderInfo;

	public long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(long surveyId) {
		this.surveyId = surveyId;
	}

	public TransactionInfoGetV2VO getTransactionInfo() {
		return transactionInfo;
	}

	public void setTransactionInfo(TransactionInfoGetV2VO transactionInfo) {
		this.transactionInfo = transactionInfo;
	}

	public ServiceProviderInfoV2 getServiceProviderInfo() {
		return serviceProviderInfo;
	}

	public void setServiceProviderInfo(ServiceProviderInfoV2 serviceProviderInfo) {
		this.serviceProviderInfo = serviceProviderInfo;
	}
}
