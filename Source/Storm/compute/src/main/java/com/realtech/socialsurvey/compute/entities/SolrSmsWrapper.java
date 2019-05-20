package com.realtech.socialsurvey.compute.entities;

public class SolrSmsWrapper extends SmsInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String smsCreatedDate;
	private String smsSentDate;
    private String smsDeliveredDate;
    private String smsUpdatedDate;
    private String smsCost;
    private long numberOfSegments;
    
    public SolrSmsWrapper( SmsInfo smsInfo ) {
    	
    	this.randomUUID = smsInfo.randomUUID;
    	this.recipientName = smsInfo.recipientName;
    	this.recipientContactNumber = smsInfo.recipientContactNumber;
    	this.smsText = smsInfo.smsText;
    	this.surveyUrl = smsInfo.surveyUrl;
    	this.agentId = smsInfo.agentId;
    	this.companyId = smsInfo.companyId;
    	this.spiId = smsInfo.spiId;
    	this.isContactNumberUnsubscribed = smsInfo.isContactNumberUnsubscribed;
    	this.smsCategory = smsInfo.smsCategory;
    	this.shortenedUrl = smsInfo.shortenedUrl;
    	this.rebrandlyErrorCode = smsInfo.rebrandlyErrorCode;
    	this.rebrandlyErrorMessage = smsInfo.rebrandlyErrorMessage;
    	this.incomingMessageBody = smsInfo.incomingMessageBody;
	}
	public String getSmsCreatedDate() {
		return smsCreatedDate;
	}

	public void setSmsCreatedDate(String smsCreatedDate) {
		this.smsCreatedDate = smsCreatedDate;
	}

	public String getSmsSentDate() {
		return smsSentDate;
	}

	public void setSmsSentDate(String smsSentDate) {
		this.smsSentDate = smsSentDate;
	}

	public String getSmsDeliveredDate() {
		return smsDeliveredDate;
	}

	public void setSmsDeliveredDate(String smsDeliveredDate) {
		this.smsDeliveredDate = smsDeliveredDate;
	}

	public String getSmsUpdatedDate() {
		return smsUpdatedDate;
	}

	public void setSmsUpdatedDate(String smsUpdatedDate) {
		this.smsUpdatedDate = smsUpdatedDate;
	}

	public String getSmsCost() {
		return smsCost;
	}

	public void setSmsCost(String smsCost) {
		this.smsCost = smsCost;
	}

	public long getNumberOfSegments() {
		return numberOfSegments;
	}

	public void setNumberOfSegments(long numberOfSegments) {
		this.numberOfSegments = numberOfSegments;
	}
}
