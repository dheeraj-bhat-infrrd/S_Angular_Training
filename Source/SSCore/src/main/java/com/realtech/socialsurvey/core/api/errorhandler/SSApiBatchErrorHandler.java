package com.realtech.socialsurvey.core.api.errorhandler;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.exception.SSAPIBatchBadRequestException;
import com.realtech.socialsurvey.core.exception.SSAPIBatchException;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class SSApiBatchErrorHandler implements ErrorHandler
{
    public static final Logger LOG = LoggerFactory.getLogger(SSApiBatchErrorHandler.class);

    @Override
    public Throwable handleError( RetrofitError retrofitError )
    {
        LOG.error( "Found error from an SSAPI call" );
        Response response = retrofitError.getResponse();
        if(response != null && response.getBody() != null ){
            String responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
            if(response.getStatus() == HttpStatus.SC_INTERNAL_SERVER_ERROR){
                // Should have an error message
                return new SSAPIBatchException( responseString );
            }else if(response.getStatus() == HttpStatus.SC_BAD_REQUEST){
                // Should have an error message
                return new SSAPIBatchBadRequestException( responseString );
            }
        }
        return retrofitError;
    }

}
