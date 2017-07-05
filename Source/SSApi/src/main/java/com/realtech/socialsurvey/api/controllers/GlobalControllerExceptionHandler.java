package com.realtech.socialsurvey.api.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.realtech.socialsurvey.api.exceptions.BadRequestException;
import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.api.exceptions.ValidationException;
import com.realtech.socialsurvey.api.utils.RestUtils;


@ControllerAdvice
public class GlobalControllerExceptionHandler
{
    private final static String ERRORS = "errors";
    
    @Autowired
    private RestUtils restUtils;


    @ExceptionHandler ( ValidationException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleValidationException( ValidationException validationException )
    {
        Map<String, Object> result = createErrorResponse( validationException.getErrors() );
        return new ResponseEntity<Map<String, Object>>( result, HttpStatus.OK );
    }


    @ExceptionHandler ( BadRequestException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleBadRequestException( BadRequestException badRequestException )
    {
        Map<String, Object> result = createErrorResponse( badRequestException.getErrors() );
        return new ResponseEntity<Map<String, Object>>( result, HttpStatus.BAD_REQUEST );
    }


    @ExceptionHandler ( SSApiException.class)
    @ResponseBody
    public ResponseEntity<?> handleSSApiException( SSApiException ssapiException, HttpServletRequest request )
    {
        long companyId = 0;
		return restUtils.getRestResponseEntity( HttpStatus.INTERNAL_SERVER_ERROR, ssapiException.getMessage(), null, null,
                request, companyId );
    }


    @ExceptionHandler ( NumberFormatException.class)
    @ResponseBody
    public ResponseEntity<?> handleNumberFormatException( NumberFormatException ie, HttpServletRequest request )
    {
        long companyId = 0;
		return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, ie.getMessage(), null, null,
                request, companyId );
    }
    
    @ExceptionHandler ( Throwable.class)
    @ResponseBody
    public ResponseEntity<?> handleAnyException( Throwable e , HttpServletRequest request)
    {
    	long companyId = 0;
		return restUtils.getRestResponseEntity( HttpStatus.INTERNAL_SERVER_ERROR, "Could not process request due to server error", null, null,
                request, companyId );
    }


    private Map<String, Object> createErrorResponse( Errors errors )
    {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, String> errorResults = new HashMap<String, String>();
        for ( ObjectError error : errors.getAllErrors() ) {
            errorResults.put( error.getCode(), error.getDefaultMessage() );
        }
        result.put( ERRORS, errorResults );
        return result;
    }
}