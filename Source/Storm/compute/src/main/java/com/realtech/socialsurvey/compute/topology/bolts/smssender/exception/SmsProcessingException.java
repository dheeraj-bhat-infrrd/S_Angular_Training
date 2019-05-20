package com.realtech.socialsurvey.compute.topology.bolts.smssender.exception;

import com.realtech.socialsurvey.compute.exception.FatalException;

public class SmsProcessingException extends FatalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SmsProcessingException() {
		super();
	}
	
	public SmsProcessingException( String message, Throwable thrw ) {
		super( message, thrw );
	}
	
	public SmsProcessingException( String message ) {
		super( message );
	}
}
