package com.realtech.socialsurvey.core.integration.pos.errorhandlers;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;
import com.realtech.socialsurvey.core.exception.FatalException;

/**
 * Http error handler to handle http status codes
 *
 */
public class EncompassHttpErrorHandler implements ErrorHandler {

	public static final Logger LOG = LoggerFactory.getLogger(EncompassHttpErrorHandler.class);
	
	@Override
	public Throwable handleError(RetrofitError cause) {
		LOG.error("Found error "+cause.getMessage());
		Response response = cause.getResponse();
		if(response != null && response.getStatus() != HttpStatus.SC_OK){
			return new FatalException("Error connecting to the encompass server", cause);
		}
		return cause;
	}

}
