package com.realtech.socialsurvey.api.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.api.models.SurveyVO;
import com.realtech.socialsurvey.api.models.TransactionInfo;
import com.realtech.socialsurvey.api.transformers.SurveyPreinitiationTransformer;
import com.realtech.socialsurvey.api.transformers.SurveyTransformer;
import com.realtech.socialsurvey.api.utils.RestUtils;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.exception.AuthorizationException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * 
 * @author rohit
 *
 */
@RestController
@RequestMapping ( "/api")
public class SurveyApiController {

	
	private static final Logger LOGGER = LoggerFactory.getLogger( SurveyApiController.class );
	
	
	private SurveyPreinitiationTransformer surveyPreinitiationTransformer;
	
	private SurveyTransformer surveyTransformer;
	
	private SurveyHandler surveyHandler;
	
	private AdminAuthenticationService adminAuthenticationService;
	
	private RestUtils restUtils;
	
	@Autowired
    public SurveyApiController( SurveyPreinitiationTransformer surveyPreinitiationTransformer , SurveyHandler surveyHandler  ,
    		SurveyTransformer surveyTransformer , AdminAuthenticationService adminAuthenticationService ,  RestUtils restUtils)
    {
        this.surveyPreinitiationTransformer = surveyPreinitiationTransformer;
        this.surveyHandler = surveyHandler;
        this.surveyTransformer = surveyTransformer;
        this.adminAuthenticationService = adminAuthenticationService;
        this.restUtils = restUtils;
    }
	
	
	@RequestMapping ( value = "/survey", method = RequestMethod.PUT)
    @ApiOperation ( value = "Post Survey Transaction")
    public ResponseEntity<?> postSurveyTransaction( @Valid @RequestBody TransactionInfo transactionInfo , HttpServletRequest request
        ) throws SSApiException
    {
		
		LOGGER.info( "SurveyApiController.postSurveyTransaction started" );
		String authorizationHeader = request.getHeader( "Authorization" );
		//authorize request
		try {
			adminAuthenticationService.validateAuthHeader( authorizationHeader );
		} catch (AuthorizationException e) {
			return restUtils.getRestResponseEntity(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null);
		}
		
        SurveyPreInitiation surveyPreInitiation;
		try {
			surveyPreInitiation = surveyPreinitiationTransformer.transformApiRequestToDomainObject(transactionInfo);
		} catch (InvalidInputException e) {
			return restUtils.getRestResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage(), null, null);
		}

		
        try {
            surveyPreInitiation = surveyHandler.saveSurveyPreInitiationObject( surveyPreInitiation );
            LOGGER.info( "SurveyApiController.postSurveyTransaction completed successfully" );
            return restUtils.getRestResponseEntity(HttpStatus.CREATED, "inserted", "surveyId", surveyPreInitiation.getSurveyPreIntitiationId());
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
       
    }
	
	
	@RequestMapping ( value = "/survey/{surveyId}", method = RequestMethod.GET)
    @ApiOperation ( value = "Post Survey Transaction")
    public ResponseEntity<?> getSurveyTransaction( @PathVariable ( "surveyId") String surveyId , HttpServletRequest request ) throws SSApiException
    {
        
            LOGGER.info( "SurveyApiController.getSurveyTransaction started" );
            
            String authorizationHeader = request.getHeader( "Authorization" );
    		try {
    			adminAuthenticationService.validateAuthHeader( authorizationHeader );
    		} catch (AuthorizationException e1) {
    			return restUtils.getRestResponseEntity(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null);
    		}
            
            long surveyPreInitiationId;
            try{
            	 surveyPreInitiationId = Long.parseLong(surveyId);
            }catch(NumberFormatException e){
            	return restUtils.getRestResponseEntity(HttpStatus.BAD_REQUEST, "Passed parameter surveyId is invalid", null, null);
            }
            
            SurveyPreInitiation surveyPreInitiation = surveyHandler.getPreInitiatedSurvey(surveyPreInitiationId);
            SurveyDetails review =  surveyHandler.getSurveyBySurveyPreIntitiationId(surveyPreInitiationId);

            SurveyVO surveyVO = surveyTransformer.transformDomainObjectToApiResponse(review, surveyPreInitiation);
            
            LOGGER.info( "SurveyApiController.getSurveyTransaction completed successfully" );
            
            return restUtils.getRestResponseEntity(HttpStatus.OK, "Request Successfully processed", "survey", surveyVO);
        
    }


}
