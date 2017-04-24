package com.realtech.socialsurvey.core.integration.zillow.errorhandlers;

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
public class ZillowHttpErrorHandler implements ErrorHandler {

	public static final Logger LOG = LoggerFactory.getLogger(ZillowHttpErrorHandler.class);
	
	@Override
	public Throwable handleError(RetrofitError cause) {
		LOG.error("Found error "+cause.getMessage());
		Response response = cause.getResponse();
		if(response != null && response.getStatus() != HttpStatus.SC_OK){
			return new FatalException("Error connecting to the zillow : msg " + cause.getMessage() + " localize msg: " + cause.getLocalizedMessage(), cause);
		}
		return cause;
	}

}
