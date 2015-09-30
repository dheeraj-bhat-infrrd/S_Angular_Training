package com.realtech.socialsurvey.core.integration.pos.errorhandlers;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Handles http error from dotloop API
 * @author nishit
 *
 */
public class DotLoopHttpErrorHandler implements ErrorHandler {

	public static final Logger LOG = LoggerFactory.getLogger(DotLoopHttpErrorHandler.class);
	
	@Override
	public Throwable handleError(RetrofitError cause) {
		LOG.error("Found error "+cause.getMessage());
		Response response = cause.getResponse();
		if(response != null && response.getStatus() == HttpStatus.SC_FORBIDDEN){
			return new DotLoopAccessForbiddenException("Access forbidden.", cause);
		}
		return cause;
	}

}
