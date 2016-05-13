package com.realtech.socialsurvey.web.api.errorhandler;

import com.realtech.socialsurvey.web.api.exception.SSAPIBadRequestException;
import com.realtech.socialsurvey.web.api.exception.SSAPIException;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


/**
 * API error handler
 */
public class SSApiErrorHandler implements ErrorHandler
{

    public static final Logger LOG = LoggerFactory.getLogger(SSApiErrorHandler.class);

    @Override
    public Throwable handleError( RetrofitError retrofitError )
    {
        LOG.error( "Found error from an SSAPI call" );
        Response response = retrofitError.getResponse();
        if(response != null){
            String responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
            if(response.getStatus() == HttpStatus.SC_INTERNAL_SERVER_ERROR){
                // Should have an error message
                return new SSAPIException( responseString );
            }else if(response.getStatus() == HttpStatus.SC_BAD_REQUEST){
                // Should have an error message
                return new SSAPIBadRequestException( responseString );
            }
        }
        return retrofitError;
    }
}
