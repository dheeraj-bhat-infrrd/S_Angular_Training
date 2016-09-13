package com.realtech.socialsurvey.api.controllers;

import java.util.List;
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
import com.realtech.socialsurvey.api.transformers.SurveysAndReviewsVOTransformer;
import com.realtech.socialsurvey.api.utils.RestUtils;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.exception.AuthorizationException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.vo.SurveysAndReviewsVO;
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
	
	private SurveysAndReviewsVOTransformer surveysAndReviewsVOTransformer;
	
	private SurveyHandler surveyHandler;
	
	private AdminAuthenticationService adminAuthenticationService;
	
	private RestUtils restUtils;
	
	@Autowired
    public SurveyApiController( SurveyPreinitiationTransformer surveyPreinitiationTransformer , SurveyHandler surveyHandler  ,
    		SurveyTransformer surveyTransformer , SurveysAndReviewsVOTransformer surveysAndReviewsVOTransformer, AdminAuthenticationService adminAuthenticationService ,  RestUtils restUtils)
    {
        this.surveyPreinitiationTransformer = surveyPreinitiationTransformer;
        this.surveyHandler = surveyHandler;
        this.surveyTransformer = surveyTransformer;
        this.surveysAndReviewsVOTransformer = surveysAndReviewsVOTransformer;
        this.adminAuthenticationService = adminAuthenticationService;
        this.restUtils = restUtils;
    }
	
	
	@RequestMapping ( value = "/survey", method = RequestMethod.PUT)
    @ApiOperation ( value = "Post Survey Transaction")
    public ResponseEntity<?> postSurveyTransaction( @Valid @RequestBody TransactionInfo transactionInfo , HttpServletRequest request
        ) throws SSApiException
    {
		
		LOGGER.info( "SurveyApiController.postSurveyTransaction started" );
        request.setAttribute( "input" , transactionInfo );

		String authorizationHeader = request.getHeader( "Authorization" );
		//authorize request
		try {
			adminAuthenticationService.validateAuthHeader( authorizationHeader );
		} catch (AuthorizationException e) {
			return restUtils.getRestResponseEntity(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null, request);
		}
		
		//parse input object
        SurveyPreInitiation surveyPreInitiation;
		try {
			surveyPreInitiation = surveyPreinitiationTransformer.transformApiRequestToDomainObject(transactionInfo);
		} catch (InvalidInputException e) {
			return restUtils.getRestResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage(), null, null, request);
		}

		
		//save the object to database
        try {
            surveyPreInitiation = surveyHandler.saveSurveyPreInitiationObject( surveyPreInitiation );
            LOGGER.info( "SurveyApiController.postSurveyTransaction completed successfully" );
            
            return restUtils.getRestResponseEntity(HttpStatus.CREATED, "inserted", "surveyId", surveyPreInitiation.getSurveyPreIntitiationId(), request);
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
       
    }
	
	
	@RequestMapping ( value = "/survey/{surveyId}", method = RequestMethod.GET)
    @ApiOperation ( value = "Get Survey Transaction")
    public ResponseEntity<?> getSurveyTransaction( @PathVariable ( "surveyId") String surveyId , HttpServletRequest request ) throws SSApiException
    {
        
            LOGGER.info( "SurveyApiController.getSurveyTransaction started" );
            
        	//authorize request
            String authorizationHeader = request.getHeader( "Authorization" );
    		try {
    			adminAuthenticationService.validateAuthHeader( authorizationHeader );
    		} catch (AuthorizationException e1) {
    			return restUtils.getRestResponseEntity(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null, request);
    		}
            
    		
    		//parse surveyPreInitiationId from request
            long surveyPreInitiationId;
            try{
            	 surveyPreInitiationId = Long.parseLong(surveyId);
            }catch(NumberFormatException e){
            	return restUtils.getRestResponseEntity(HttpStatus.BAD_REQUEST, "Passed parameter surveyId is invalid", null, null, request);
            }
            
            
            //get data from database
            SurveyPreInitiation surveyPreInitiation = surveyHandler.getPreInitiatedSurvey(surveyPreInitiationId);
            SurveyDetails review =  surveyHandler.getSurveyBySurveyPreIntitiationId(surveyPreInitiationId);
            if(surveyPreInitiation == null){
            	return restUtils.getRestResponseEntity(HttpStatus.NOT_FOUND, "No record found for id", null, null, request);
            }
            
            
            //create vo object
            SurveyVO surveyVO = surveyTransformer.transformDomainObjectToApiResponse(review, surveyPreInitiation);
            LOGGER.info( "SurveyApiController.getSurveyTransaction completed successfully" );
            
            return restUtils.getRestResponseEntity(HttpStatus.OK, "Request Successfully processed", "survey", surveyVO, request);        
    }
	
	
	
	@RequestMapping ( value = "/survey", method = RequestMethod.GET)
    @ApiOperation ( value = "Get Survey Transactions")
    public ResponseEntity<?> getSurveyTransactions( HttpServletRequest request ) throws SSApiException
    {
        
            LOGGER.info( "SurveyApiController.getSurveyTransactions started" );
            
        	//authorize request
            String authorizationHeader = request.getHeader( "Authorization" );
            long companyId;
    		try {
    			companyId = adminAuthenticationService.validateAuthHeader( authorizationHeader );
    		} catch (AuthorizationException e1) {
    			return restUtils.getRestResponseEntity(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null, request);
    		}
            
    		
    		//parse request parameters from request
    		String countStr = request.getParameter("count");
    		String startStr = request.getParameter("start");
    		String status = request.getParameter("status"); 
            int count = 1000;
            int start = 0;
            if(countStr != null){
            	try{
            		count = Integer.parseInt(countStr);
               }catch(NumberFormatException e){
               	return restUtils.getRestResponseEntity(HttpStatus.BAD_REQUEST, "Passed parameter count is invalid", null, null, request);
               }
            }
            
            if(startStr != null){
            	try{
            		start = Integer.parseInt(startStr);
               }catch(NumberFormatException e){
               	return restUtils.getRestResponseEntity(HttpStatus.BAD_REQUEST, "Passed parameter start is invalid", null, null, request);
               }
            }
            
            if(status == null){
            	status = "all";
            }else if( !status.equalsIgnoreCase("complete") && !status.equalsIgnoreCase("incomplete")){
               	return restUtils.getRestResponseEntity(HttpStatus.BAD_REQUEST, "Passed parameter status is invalid", null, null, request);
            }
            	
            //get data from database
            SurveysAndReviewsVO surveysAndReviewsVO = surveyHandler.getSurveysByStatus(status, start, count, companyId);          
            
            
            //create vo object
            List<SurveyVO> surveyVOs = surveysAndReviewsVOTransformer.transformDomainObjectToApiResponse(surveysAndReviewsVO);
            LOGGER.info( "SurveyApiController.getSurveyTransaction completed successfully" );
            
            return restUtils.getRestResponseEntity(HttpStatus.OK, "Request Successfully processed", "surveys", surveyVOs , request);

    }


}
