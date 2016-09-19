package com.realtech.socialsurvey.api.models;

public class SurveyPutVO {

	private TransactionInfoPutVO transactionInfo;
	private ServiceProviderInfo serviceProviderInfo;
	
	
	public TransactionInfoPutVO getTransactionInfo() {
		return transactionInfo;
	}
	public void setTransactionInfo(TransactionInfoPutVO transactionInfo) {
		this.transactionInfo = transactionInfo;
	}
	public ServiceProviderInfo getServiceProviderInfo() {
		return serviceProviderInfo;
	}
	public void setServiceProviderInfo(ServiceProviderInfo serviceProviderInfo) {
		this.serviceProviderInfo = serviceProviderInfo;
	}
}
