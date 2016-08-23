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
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.exception.AuthorizationException;
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
	
	@Autowired
    public SurveyApiController( SurveyPreinitiationTransformer surveyPreinitiationTransformer , SurveyHandler surveyHandler  ,
    		SurveyTransformer surveyTransformer , AdminAuthenticationService adminAuthenticationService)
    {
        this.surveyPreinitiationTransformer = surveyPreinitiationTransformer;
        this.surveyHandler = surveyHandler;
        this.surveyTransformer = surveyTransformer;
        this.adminAuthenticationService = adminAuthenticationService;
    }
	
	
	@RequestMapping ( value = "/survey", method = RequestMethod.PUT)
    @ApiOperation ( value = "Post Survey Transaction")
    public ResponseEntity<?> postSurveyTransaction( @Valid @RequestBody TransactionInfo transactionInfo , HttpServletRequest request
        ) throws SSApiException
    {
		
		String authorizationHeader = request.getHeader( "Authorization" );
		try {
			adminAuthenticationService.validateAuthHeader( authorizationHeader );
		} catch (AuthorizationException e1) {
			return new ResponseEntity<String>( "Unauthorized" , HttpStatus.UNAUTHORIZED );
		}
		
        try {
            LOGGER.info( "SurveyApiController.postSurveyTransaction started" );
            
            SurveyPreInitiation surveyPreInitiation = surveyPreinitiationTransformer.transformApiRequestToDomainObject(transactionInfo);
            surveyPreInitiation = surveyHandler.saveSurveyPreInitiationObject( surveyPreInitiation );
            
            LOGGER.info( "SurveyApiController.postSurveyTransaction completed successfully" );
            
            Map<String,Object> responseMap = new java.util.HashMap<String,Object>();
            responseMap.put("surveyId", surveyPreInitiation.getSurveyPreIntitiationId());
            return new ResponseEntity<Map<String,Object>>( responseMap , HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }
	
	
	@RequestMapping ( value = "/survey/{surveyId}", method = RequestMethod.GET)
    @ApiOperation ( value = "Post Survey Transaction")
    public ResponseEntity<?> getSurveyTransaction( @PathVariable ( "surveyId") String surveyId ) throws SSApiException
    {
        try {
            LOGGER.info( "SurveyApiController.getSurveyTransaction started" );
            
            long surveyPreInitiationId;
            try{
            	 surveyPreInitiationId = Long.parseLong(surveyId);
            }catch(NumberFormatException e){
            	throw new NonFatalException("Passed parameter surveyId is invalid");
            }
            SurveyPreInitiation surveyPreInitiation = surveyHandler.getPreInitiatedSurvey(surveyPreInitiationId);
            SurveyDetails review =  surveyHandler.getSurveyBySurveyPreIntitiationId(surveyPreInitiationId);

            SurveyVO surveyVO = surveyTransformer.transformDomainObjectToApiResponse(review, surveyPreInitiation);
            
            LOGGER.info( "SurveyApiController.getSurveyTransaction completed successfully" );
            
            return new ResponseEntity<SurveyVO>( surveyVO , HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


}
