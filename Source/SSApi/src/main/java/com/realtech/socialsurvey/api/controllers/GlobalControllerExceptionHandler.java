package com.realtech.socialsurvey.api.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.realtech.socialsurvey.api.exceptions.BadRequestException;
import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.api.exceptions.ValidationException;
import com.realtech.socialsurvey.api.utils.RestUtils;
import com.realtech.socialsurvey.core.exception.AuthorizationException;


@ControllerAdvice
public class GlobalControllerExceptionHandler
{
    private final static String ERRORS = "errors";
    private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);
    
    @Autowired
    private RestUtils restUtils;


    @ExceptionHandler ( ValidationException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleValidationException( ValidationException validationException )
    {
    	Map<String, Object> result = createErrorResponse( validationException.getErrors() );
        return handleAnyKnownException( validationException, result );
    }


    @ExceptionHandler ( BadRequestException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleBadRequestException( BadRequestException badRequestException )
    {
    	Map<String, Object> result = null;
        
        if(badRequestException.getErrors() != null){
            result = createErrorResponse( badRequestException.getErrors() );
        }
        
        return handleAnyKnownException( badRequestException, result );
    }


    @ExceptionHandler ( SSApiException.class)
    @ResponseBody
    public ResponseEntity<?> handleSSApiException( SSApiException ssapiException, HttpServletRequest request )
    {
    	return handleAnyUnknownException( ssapiException, request );
    }
    
    @ExceptionHandler (ServletRequestBindingException.class)
    @ResponseBody
    public ResponseEntity<?> handleMissingParametersException( ServletRequestBindingException bindingException, HttpServletRequest request )
    {
        return handleAnyKnownException( bindingException, null );
    }
    
    @ExceptionHandler ( AuthorizationException.class)
    @ResponseBody
    public ResponseEntity<?> handleAuthorizationException( AuthorizationException authorizationException, HttpServletRequest request )
    {
        LOG.error("Authorization failed for the request");
        long companyId = 0;
        return restUtils.getRestResponseEntity( HttpStatus.UNAUTHORIZED, authorizationException.getMessage(), null, null,
                request, companyId );
    }


    @ExceptionHandler ( NumberFormatException.class)
    @ResponseBody
    public ResponseEntity<?> handleNumberFormatException( NumberFormatException nfe, HttpServletRequest request )
    {
    	return handleAnyKnownException( nfe, null );
    }
    
    private ResponseEntity<Map<String, Object>> handleAnyKnownException( Exception e, Map<String, Object> result)
    {
        LOG.error("Exception occurred", e);
        
        if(result == null){
            result = new HashMap<>();
            result.put( ERRORS, e.getMessage() );
        }
        
        return new ResponseEntity<Map<String, Object>>( result, HttpStatus.BAD_REQUEST );
    }
    
    @ExceptionHandler ( Throwable.class)
    @ResponseBody
    public ResponseEntity<?> handleAnyUnknownException( Throwable e , HttpServletRequest request)
    {
    	LOG.error("Uncaught Exception occurred", e);
    	long companyId = 0;
    	return restUtils.getRestResponseEntity( HttpStatus.INTERNAL_SERVER_ERROR, "Could not process request due to server error", null, null,
                request, companyId );
    }

    
    
    @ExceptionHandler ( HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<?> handleNumberFormatException( HttpMessageNotReadableException hmnre, HttpServletRequest request )
	{

		LOG.error("HttpMessageNotReadableException occurred", hmnre);

		String exceptionMessage = hmnre.getMessage();
		String responseMsg = "";
		if (ExceptionUtils.indexOfType(hmnre, UnrecognizedPropertyException.class) != -1) {
			responseMsg = "Invalid request body. " + StringUtils.substring(exceptionMessage, 0, StringUtils.indexOf(exceptionMessage, "(class com.realtech.socialsurvey"));
		} else if (ExceptionUtils.indexOfType(hmnre, JsonMappingException.class) != -1) {
			responseMsg = "Invalid request body. Not a valid JSON";
		} else {
			responseMsg = exceptionMessage;
		}

		long companyId = 0;
		return restUtils.getRestResponseEntity(HttpStatus.BAD_REQUEST, responseMsg, null, null, request, companyId);
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