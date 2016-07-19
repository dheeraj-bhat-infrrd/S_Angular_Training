package com.realtech.socialsurvey.web.rest;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.BaseRestException;
import com.realtech.socialsurvey.core.exception.InputValidationException;
import com.realtech.socialsurvey.core.exception.InternalServerException;

public class AbstractController
{
    
    private static final Logger LOG = LoggerFactory.getLogger( AbstractController.class );
    
    /**
     * Method to get the error response object from base rest exception
     * 
     * @param ex
     * @return
     */
    protected Response getErrorResponse( BaseRestException ex )
    {
        LOG.debug( "Resolve Error Response" );
        Status httpStatus = resolveHttpStatus( ex );
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put( CommonConstants.STATUS_COLUMN, false );
        resultMap.put( CommonConstants.MESSAGE, ex.getDebugMessage() );
        Response response = Response.status( httpStatus ).entity( new Gson().toJson( resultMap ) ).build();

        return response;
    }


    /**
     * Method to get the http status based on the exception type
     * 
     * @param ex
     * @return
     */
    protected Status resolveHttpStatus( BaseRestException ex )
    {
        LOG.debug( "Resolving http status" );
        Status httpStatus = Status.INTERNAL_SERVER_ERROR;
        if ( ex instanceof InputValidationException ) {
            httpStatus = Status.UNAUTHORIZED;
        } else if ( ex instanceof InternalServerException ) {
            httpStatus = Status.INTERNAL_SERVER_ERROR;
        }
        LOG.debug( "Resolved http status to " + httpStatus.getStatusCode() );
        return httpStatus;
    }

}
