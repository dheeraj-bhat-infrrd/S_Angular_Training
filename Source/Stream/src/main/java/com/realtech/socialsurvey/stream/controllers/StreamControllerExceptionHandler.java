package com.realtech.socialsurvey.stream.controllers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Exception handler for stream api controllers
 * @author nishit
 *
 */
@ControllerAdvice
public class StreamControllerExceptionHandler
{
    private static final Logger LOG = LoggerFactory.getLogger( StreamControllerExceptionHandler.class );


    @ExceptionHandler ( { ExecutionException.class, TimeoutException.class, InterruptedException.class })
    @ResponseBody
    public ResponseEntity<?> handleWaitException( Exception e, HttpServletRequest request )
    {
        LOG.error( "TimeoutException/  InterruptedException", e );
        return getRestResponseEntity( HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), null, null, request );
    }


    private ResponseEntity<Map<String, Object>> getRestResponseEntity( HttpStatus httpStatus, String responseMsg,
        String dataKey, Object dataObject, HttpServletRequest request )
    {

        Map<String, Object> responseMap = new LinkedHashMap<>();

        Map<String, Object> msgEntity = new HashMap<>();
        msgEntity.put( "code", httpStatus.value() );
        msgEntity.put( "message", responseMsg );
        responseMap.put( "msg", msgEntity );

        if ( !StringUtils.isEmpty( dataKey ) ) {
            Map<String, Object> dateEntity = new HashMap<>();
            dateEntity.put( dataKey, dataObject );
            responseMap.put( "data", dateEntity );
        }

        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>( responseMap, httpStatus );
        request.setAttribute( "output", responseEntity );
        return responseEntity;
    }

}
