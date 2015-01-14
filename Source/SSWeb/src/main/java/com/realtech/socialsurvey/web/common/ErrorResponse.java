package com.realtech.socialsurvey.web.common;

public class ErrorResponse {

	private String errCode;
	private String errMessage;

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMessage() {
		return errMessage;
	}

	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}

	@Override
	public String toString() {
		return "{errCode=" + errCode + ", errMessage=" + errMessage + "}";
	}

}
