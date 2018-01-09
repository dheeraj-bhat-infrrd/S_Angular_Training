package com.realtech.socialsurvey.core.integration.stream;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Error handler for stream apis
 * @author nishit
 *
 */
public class StreamApiErrorHandler implements ErrorHandler
{

    public static final Logger LOG = LoggerFactory.getLogger( StreamApiErrorHandler.class );


    @Override
    public Throwable handleError( RetrofitError cause )
    {
        LOG.error( "Found error in Stream API: {}", cause.getMessage() );
        Response response = cause.getResponse();
        if ( response == null ) {
            // TODO: Implement circuit-breaker
            return new StreamApiConnectException( "Could not connect to stream api.", cause );
        } else if ( response != null && response.getStatus() != HttpStatus.SC_OK
            && response.getStatus() != HttpStatus.SC_CREATED ) {
            return new StreamApiException( "Error accesing stream api.", cause );
        }
        return cause;
    }

}
