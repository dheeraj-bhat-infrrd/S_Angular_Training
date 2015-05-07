package com.realtech.socialsurvey.core.services.integeration.pos;

import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Exception when agent is not available in the system
 *
 */
public class AgentNotAvailableException extends NonFatalException {

	private static final long serialVersionUID = 489956476127732587L;
	
	public AgentNotAvailableException() {
		super();
	}

	public AgentNotAvailableException(String message) {
		super(message);
	}

	public AgentNotAvailableException(String message, Throwable thrw) {
		super(message, thrw);
	}

	public AgentNotAvailableException(String message, String errorCode, Throwable thrw) {
		super(message, errorCode, thrw);
	}

	public AgentNotAvailableException(String message, String errorCode) {
		super(message, errorCode);
	}

}
