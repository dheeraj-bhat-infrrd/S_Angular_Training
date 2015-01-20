package com.realtech.socialsurvey.core.services.search.exception;

import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Exception class for handling solr related exceptions
 */
public class SolrException extends NonFatalException {

	private static final long serialVersionUID = -7430340928328298631L;

	public SolrException() {
		super();
	}

	public SolrException(String message) {
		super(message);
	}

	public SolrException(String message, Throwable thrw) {
		super(message, thrw);
	}

	public SolrException(String message, String errorCode) {
		super(message, errorCode);
	}

	public SolrException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}
}
