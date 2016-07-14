package com.realtech.socialsurvey.core.integration.pos.errorhandlers;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoneWolfHttpErrorHandler implements ErrorHandler
{

    public static final Logger LOG = LoggerFactory.getLogger( LoneWolfHttpErrorHandler.class );


    @Override
    public Throwable handleError( RetrofitError cause )
    {
        LOG.error( "Found error " + cause.getMessage() );
        Response response = cause.getResponse();
        if ( response != null && response.getStatus() != HttpStatus.SC_OK && response.getStatus() != HttpStatus.SC_CREATED ) {
            return new LoneWolfAccessException( "Access Failure.", cause );
        }
        return cause;
    }

}
