package com.realtech.socialsurvey.api.models;

public class SurveyPutVO {

	private TransactionInfo transactionInfo;
	private ServiceProviderInfo serviceProviderInfo;
	
	
	public TransactionInfo getTransactionInfo() {
		return transactionInfo;
	}
	public void setTransactionInfo(TransactionInfo transactionInfo) {
		this.transactionInfo = transactionInfo;
	}
	public ServiceProviderInfo getServiceProviderInfo() {
		return serviceProviderInfo;
	}
	public void setServiceProviderInfo(ServiceProviderInfo serviceProviderInfo) {
		this.serviceProviderInfo = serviceProviderInfo;
	}
}
