package com.realtech.socialsurvey.stream.messages;

import java.io.Serializable;

public class SmsInfo implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String randomUUID;
	private String recipientName;
	private String recipientContactNumber;
	private String smsText;
	private String surveyUrl;
	private long agentId;
	private long companyId;
	private long spiId;
	private String smsCategory;
	private String incomingMessageBody;
	private String twilioSmsId;
	private String smsStatus;
	private String errorCode;
	private String errorMessage;
	public String getRandomUUID() {
		return randomUUID;
	}
	public void setRandomUUID(String randomUUID) {
		this.randomUUID = randomUUID;
	}
	public String getRecipientName() {
		return recipientName;
	}
	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}
	public String getRecipientContactNumber() {
		return recipientContactNumber;
	}
	public void setRecipientContactNumber(String recipientContactNumber) {
		this.recipientContactNumber = recipientContactNumber;
	}
	public String getSmsText() {
		return smsText;
	}
	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}
	public String getSurveyUrl() {
		return surveyUrl;
	}
	public void setSurveyUrl(String surveyUrl) {
		this.surveyUrl = surveyUrl;
	}
	public long getAgentId() {
		return agentId;
	}
	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}
	public long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}
	public long getSpiId() {
		return spiId;
	}
	public void setSpiId(long spiId) {
		this.spiId = spiId;
	}
	public String getSmsCategory() {
		return smsCategory;
	}
	public void setSmsCategory(String smsCategory) {
		this.smsCategory = smsCategory;
	}
	public String getIncomingMessageBody() {
		return incomingMessageBody;
	}
	public void setIncomingMessageBody(String incomingMessageBody) {
		this.incomingMessageBody = incomingMessageBody;
	}
	public String getTwilioSmsId() {
		return twilioSmsId;
	}
	public void setTwilioSmsId(String twilioSmsId) {
		this.twilioSmsId = twilioSmsId;
	}
	public String getSmsStatus() {
		return smsStatus;
	}
	public void setSmsStatus(String smsStatus) {
		this.smsStatus = smsStatus;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
