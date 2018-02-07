package com.realtech.socialsurvey.core.exception;

/**
 * JIRA:SS-117 by RM02 
 * Exception class for handling errors occurring on server
 */
public class InternalServerException extends BaseRestException {

	private static final long serialVersionUID = 1L;
	
	public InternalServerException(ErrorCode errorCode, String debugMessage, Throwable thrw) {
	    super(thrw);
	    this.debugMessage=debugMessage;
	    this.errorCode = errorCode;
	}
}
