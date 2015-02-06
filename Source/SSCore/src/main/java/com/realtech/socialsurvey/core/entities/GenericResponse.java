package com.realtech.socialsurvey.core.entities;

import com.realtech.socialsurvey.core.exception.RestErrorResponse;

/**
 * JIRA:SS-117 by RM02 Wrapper class for rest response
 */
public class GenericResponse {

	private Object result;
	private RestErrorResponse error;

	public GenericResponse() {}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public RestErrorResponse getError() {
		return error;
	}

	public void setError(RestErrorResponse error) {
		this.error = error;
	}

}
