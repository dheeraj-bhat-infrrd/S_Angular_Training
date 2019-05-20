package com.realtech.socialsurvey.compute.topology.bolts.smssender.exception;

import com.realtech.socialsurvey.compute.exception.FatalException;

public class TemporarySmsProcessingException extends FatalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TemporarySmsProcessingException() {
		super();
	}
	
	public TemporarySmsProcessingException( String message, Throwable thrw ) {
		super( message, thrw );
	}
	
	public TemporarySmsProcessingException( String message ) {
		super( message );
	}
}
