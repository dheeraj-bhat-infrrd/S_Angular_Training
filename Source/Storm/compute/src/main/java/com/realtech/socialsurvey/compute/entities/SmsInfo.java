package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;

import org.mongodb.morphia.annotations.Embedded;

@Embedded
public class SmsInfo implements Serializable
{

	/**
	 * 
	 */
	protected static final long serialVersionUID = 1L;
	protected String randomUUID;
	protected String recipientName;
	protected String recipientContactNumber;
	protected String smsText;
	protected String surveyUrl;
	protected String shortenedUrl;
	protected long agentId;
	protected long companyId;
	protected long spiId;
	protected boolean isContactNumberUnsubscribed;
	protected boolean isRetried;
	protected Integer rebrandlyErrorCode;
	protected String rebrandlyErrorMessage;
	protected String smsCategory;
	protected String incomingMessageBody;
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
	public Integer getRebrandlyErrorCode() {
		return rebrandlyErrorCode;
	}
	public void setRebrandlyErrorCode(Integer rebrandlyErrorCode) {
		this.rebrandlyErrorCode = rebrandlyErrorCode;
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
	public boolean isContactNumberUnsubscribed() {
		return isContactNumberUnsubscribed;
	}
	public void setContactNumberUnsubscribed(boolean isContactNumberUnsubscribed) {
		this.isContactNumberUnsubscribed = isContactNumberUnsubscribed;
	}
	public boolean isRetried() {
		return isRetried;
	}
	public void setRetried(boolean isRetried) {
		this.isRetried = isRetried;
	}
	public String getShortenedUrl() {
		return shortenedUrl;
	}
	public void setShortenedUrl(String shortenedUrl) {
		this.shortenedUrl = shortenedUrl;
	}
	public String getRebrandlyErrorMessage() {
		return rebrandlyErrorMessage;
	}
	public void setRebrandlyErrorMessage(String rebrandlyErrorMessage) {
		this.rebrandlyErrorMessage = rebrandlyErrorMessage;
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
