package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;

public class RebrandlyVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String shortUrl;
	private Integer rebrandlyErrorCode;
	private String rebrandlyErrorMessage;
	public String getShortUrl() {
		return shortUrl;
	}
	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}
	public Integer getRebrandlyErrorCode() {
		return rebrandlyErrorCode;
	}
	public void setRebrandlyErrorCode(Integer rebrandlyErrorCode) {
		this.rebrandlyErrorCode = rebrandlyErrorCode;
	}
	public String getRebrandlyErrorMessage() {
		return rebrandlyErrorMessage;
	}
	public void setRebrandlyErrorMessage(String rebrandlyErrorMessage) {
		this.rebrandlyErrorMessage = rebrandlyErrorMessage;
	}
}
