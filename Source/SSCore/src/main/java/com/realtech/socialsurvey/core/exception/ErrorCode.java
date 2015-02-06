package com.realtech.socialsurvey.core.exception;

/**
 * Holds the error code details
 *
 */
public interface ErrorCode {

	int getErrorCode();
	int getServiceId();
	String getMessage();
}
