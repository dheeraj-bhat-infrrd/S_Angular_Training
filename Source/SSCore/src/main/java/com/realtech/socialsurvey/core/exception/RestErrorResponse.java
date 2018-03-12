package com.realtech.socialsurvey.core.exception;

import java.util.Map;

/**
 * JIRA:SS-117 by RM02 
 * Class for handling error messages in rest response
 */
public class RestErrorResponse {

	private int httpStatus;
	public final String result = "error"; // should always return error
	private int serviceId;
	private int errorCode;
	private String message;
	private String debugMessage;
	private Throwable thrw;
	private Map<String, String> messageArgs;

	public int getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDebugMessage() {
		return debugMessage;
	}

	public void setDebugMessage(String debugMessage) {
		this.debugMessage = debugMessage;
	}

	public Map<String, String> getMessageArgs() {
		return messageArgs;
	}

	public void setMessageArgs(Map<String, String> messageArgs) {
		this.messageArgs = messageArgs;
	}

    public Throwable getThrw()
    {
        return thrw;
    }

    public void setThrw( Throwable thrw )
    {
        this.thrw = thrw;
    }

}
