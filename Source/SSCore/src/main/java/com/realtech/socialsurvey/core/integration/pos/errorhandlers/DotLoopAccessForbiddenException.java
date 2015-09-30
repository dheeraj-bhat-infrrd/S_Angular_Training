package com.realtech.socialsurvey.core.integration.pos.errorhandlers;

import com.realtech.socialsurvey.core.exception.FatalException;

public class DotLoopAccessForbiddenException extends FatalException {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 2015020702364453334L;

	public DotLoopAccessForbiddenException() {
		super();
	}

	public DotLoopAccessForbiddenException(String message) {
		super(message);
	}

	public DotLoopAccessForbiddenException(String message, Throwable thrw) {
		super(message, thrw);
	}
}
