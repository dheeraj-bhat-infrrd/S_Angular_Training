package com.realtech.socialsurvey.api.controllers;

import java.util.HashMap;
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
import com.realtech.socialsurvey.api.models.SurveyGetVO;
import com.realtech.socialsurvey.api.models.SurveyPutVO;
import com.realtech.socialsurvey.api.transformers.SurveyPreinitiationTransformer;
import com.realtech.socialsurvey.api.transformers.SurveyTransformer;
import com.realtech.socialsurvey.api.transformers.SurveysAndReviewsVOTransformer;
import com.realtech.socialsurvey.api.utils.RestUtils;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.exception.AuthorizationException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.vo.SurveysAndReviewsVO;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping ( "/v1")
public class SurveyApiController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( SurveyApiController.class );

    @Autowired
    private SurveyPreinitiationTransformer surveyPreinitiationTransformer;

    @Autowired
    private SurveyTransformer surveyTransformer;

    @Autowired
    private SurveysAndReviewsVOTransformer surveysAndReviewsVOTransformer;

    @Autowired
    private SurveyHandler surveyHandler;

    @Autowired
    private AdminAuthenticationService adminAuthenticationService;

    @Autowired
    private RestUtils restUtils;


    @RequestMapping ( value = "/surveys", method = RequestMethod.PUT)
    @ApiOperation ( value = "Post Survey Transaction")
    public ResponseEntity<?> postSurveyTransaction( @Valid @RequestBody SurveyPutVO surveyModel, HttpServletRequest request )
        throws SSApiException
    {
        LOGGER.info( "SurveyApiController.postSurveyTransaction started" );
        request.setAttribute( "input", surveyModel );

        String authorizationHeader = request.getHeader( CommonConstants.SURVEY_API_REQUEST_PARAMETER_AUTHORIZATION );
        long companyId = 0;
        //authorize request
        try {
            companyId = adminAuthenticationService.validateAuthHeader( authorizationHeader );
        } catch ( AuthorizationException e ) {
            return restUtils.getRestResponseEntity( HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null, request, companyId );
        }

        //parse input object
        List<SurveyPreInitiation> surveyPreInitiations;
        try {
            surveyPreInitiations = surveyPreinitiationTransformer.transformApiRequestToDomainObject( surveyModel, companyId );
        } catch ( InvalidInputException e ) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, e.getMessage(), null, null, request, companyId );
        }

        //save the object to database
        Map<String, Long> surveyIds = new HashMap<String, Long>();
        try {
            for ( SurveyPreInitiation surveyPreInitiation : surveyPreInitiations ) {
                surveyPreInitiation = surveyHandler.saveSurveyPreInitiationObject( surveyPreInitiation );
                surveyIds.put( surveyPreInitiation.getCustomerEmailId(), surveyPreInitiation.getSurveyPreIntitiationId() );
            }
            LOGGER.info( "SurveyApiController.postSurveyTransaction completed successfully" );

            return restUtils.getRestResponseEntity( HttpStatus.CREATED, "Survey successfully created", "surveyId", surveyIds,
                request, companyId );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    @RequestMapping ( value = "/surveys/{surveyId}", method = RequestMethod.GET)
    @ApiOperation ( value = "Get Survey Transaction")
    public ResponseEntity<?> getSurveyTransaction( @PathVariable ( "surveyId") String surveyId, HttpServletRequest request )
        throws SSApiException
    {
        LOGGER.info( "SurveyApiController.getSurveyTransaction started" );

        //authorize request
        long companyId = 0;
        String authorizationHeader = request.getHeader( CommonConstants.SURVEY_API_REQUEST_PARAMETER_AUTHORIZATION );
        try {
            companyId = adminAuthenticationService.validateAuthHeader( authorizationHeader );
        } catch ( AuthorizationException e1 ) {
            return restUtils.getRestResponseEntity( HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null, request, companyId );
        }

        //parse surveyPreInitiationId from request
        long surveyPreInitiationId;
        try {
            surveyPreInitiationId = Long.parseLong( surveyId );
        } catch ( NumberFormatException e ) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Passed parameter surveyId is invalid", null, null,
                request, companyId );
        }

        //get data from database
        SurveyPreInitiation surveyPreInitiation = surveyHandler.getPreInitiatedSurvey( surveyPreInitiationId );
        SurveyDetails review = surveyHandler.getSurveyBySurveyPreIntitiationId( surveyPreInitiationId );
        if ( surveyPreInitiation == null ) {
            return restUtils.getRestResponseEntity( HttpStatus.NOT_FOUND, "No record found for id", null, null, request,
                companyId );
        }

        //create vo object
        SurveyGetVO surveyVO = surveyTransformer.transformDomainObjectToApiResponse( review, surveyPreInitiation );
        LOGGER.info( "SurveyApiController.getSurveyTransaction completed successfully" );

        return restUtils.getRestResponseEntity( HttpStatus.OK, "Request Successfully processed", "survey", surveyVO, request,
            companyId );
    }


    @RequestMapping ( value = "/surveys", method = RequestMethod.GET)
    @ApiOperation ( value = "Get Survey Transactions")
    public ResponseEntity<?> getSurveyTransactions( HttpServletRequest request ) throws SSApiException
    {
        LOGGER.info( "SurveyApiController.getSurveyTransactions started" );

        //authorize request
        String authorizationHeader = request.getHeader( CommonConstants.SURVEY_API_REQUEST_PARAMETER_AUTHORIZATION );
        long companyId = 0;
        try {
            companyId = adminAuthenticationService.validateAuthHeader( authorizationHeader );
        } catch ( AuthorizationException e1 ) {
            return restUtils.getRestResponseEntity( HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null, request, companyId );
        }

        //parse request parameters from request
        String countStr = request.getParameter( "count" );
        String startStr = request.getParameter( "start" );
        String status = request.getParameter( "status" );

        int count = CommonConstants.SURVEY_API_DEFAUAT_BATCH_SIZE;
        int start = 0;
        if ( countStr != null ) {
            try {
                count = Integer.parseInt( countStr );
                // default count is 1000
                if ( count > CommonConstants.SURVEY_API_DEFAUAT_BATCH_SIZE ) {
                    count = CommonConstants.SURVEY_API_DEFAUAT_BATCH_SIZE;
                }
            } catch ( NumberFormatException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Passed parameter count is invalid", null, null,
                    request, companyId );
            }
        }

        if ( startStr != null ) {
            try {
                start = Integer.parseInt( startStr );
            } catch ( NumberFormatException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Passed parameter start is invalid", null, null,
                    request, companyId );
            }
        }

        if ( status == null ) {
            status = "all";
        } else if ( !status.equalsIgnoreCase( "complete" ) && !status.equalsIgnoreCase( "incomplete" ) ) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Passed parameter status is invalid", null, null,
                request, companyId );
        }

        //get data from database
        SurveysAndReviewsVO surveysAndReviewsVO = surveyHandler.getSurveysByFilterCriteria( null, null, null, null,
            null, false, start, count, companyId );

        //create vo object
        List<SurveyGetVO> surveyVOs = surveysAndReviewsVOTransformer.transformDomainObjectToApiResponse( surveysAndReviewsVO );
        LOGGER.info( "SurveyApiController.getSurveyTransaction completed successfully" );

        return restUtils.getRestResponseEntity( HttpStatus.OK, "Request Successfully processed", "surveys", surveyVOs, request,
            companyId );
    }

}
