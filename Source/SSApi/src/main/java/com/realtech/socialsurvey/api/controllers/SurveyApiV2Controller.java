package com.realtech.socialsurvey.api.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.api.models.BulkSurveyPutVO;
import com.realtech.socialsurvey.api.models.SurveyPutVO;
import com.realtech.socialsurvey.api.models.TransactionInfoPutVO;
import com.realtech.socialsurvey.api.models.v2.BulkSurveyProcessResponseVO;
import com.realtech.socialsurvey.api.models.v2.IncompeteSurveyGetVO;
import com.realtech.socialsurvey.api.models.v2.SurveyCountVO;
import com.realtech.socialsurvey.api.models.v2.SurveyGetV2VO;
import com.realtech.socialsurvey.api.transformers.IncompleteSurveyVOTransformer;
import com.realtech.socialsurvey.api.transformers.SurveyPreinitiationTransformer;
import com.realtech.socialsurvey.api.transformers.SurveyV2Transformer;
import com.realtech.socialsurvey.api.transformers.SurveysAndReviewsV2VOTransformer;
import com.realtech.socialsurvey.api.utils.RestUtils;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.PostToSocialMedia;
import com.realtech.socialsurvey.core.entities.ReviewReplyVO;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.AuthorizationException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.vo.BulkWriteErrorVO;
import com.realtech.socialsurvey.core.vo.SurveyDetailsVO;
import com.realtech.socialsurvey.core.vo.SurveysAndReviewsVO;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping ( "/v2")
public class SurveyApiV2Controller
{
    private static final Logger LOGGER = LoggerFactory.getLogger( SurveyApiV2Controller.class );
    Logger AUDIT_LOG = LoggerFactory.getLogger("Audit");
    private static final Logger AUDIT_app = LoggerFactory.getLogger("auditlog");


    @Autowired
    private SurveysAndReviewsV2VOTransformer surveysAndReviewsV2VOTransformer;

    @Autowired
    private IncompleteSurveyVOTransformer incompleteSurveyVOTransformer;

    private SurveyHandler surveyHandler;

    @Autowired
    public void setSurveyHandler(SurveyHandler surveyHandler) {
        this.surveyHandler = surveyHandler;
    }


    @Autowired
    private AdminAuthenticationService adminAuthenticationService;

    @Autowired
    private RestUtils restUtils;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private SurveyPreinitiationTransformer surveyPreinitiationTransformer;

    @Autowired
    private SurveyV2Transformer surveyV2Transformer;
    
    @Autowired
    private SocialManagementService socialManagementService;
    
    private SurveyBuilder surveyBuilder;
    
    @Autowired
	public void setSurveyBuilder(SurveyBuilder surveyBuilder) {
		this.surveyBuilder = surveyBuilder;
	}
    
    @Autowired
    private OrganizationManagementService organizationManagementService;

    private static final String AUTH_FAILED = "AUTHORIZATION FAILED";

	@RequestMapping ( value = "/surveys", method = RequestMethod.PUT)
    @ApiOperation ( value = "Post Survey Transaction")
    public ResponseEntity<?> postSurveyTransaction( @Valid @RequestBody SurveyPutVO surveyModel, HttpServletRequest request )
        throws SSApiException
    {
        LOGGER.info( "SurveyApiController.postSurveyTransaction started" );

        request.setAttribute( "input", surveyModel );
        String message = "Survey successfully created.";
        boolean isDuplicate = false;
        boolean isUnsubscribed = false;
        boolean isPartnerSurveyAllowed = true;

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
            surveyPreInitiations = surveyPreinitiationTransformer.transformApiRequestToDomainObject( surveyModel, companyId , CommonConstants.CRM_INFO_SOURCE_API);
        } catch ( InvalidInputException e ) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, e.getMessage(), null, null, request, companyId );
        }

        //validate survey
        try {
        		surveyPreInitiations = surveyHandler.validatePreinitiatedRecord( surveyPreInitiations , companyId );
		} catch (InvalidInputException e) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, e.getMessage(), null, null, request, companyId );
        }


        //save the object to database
        Map<String, Long> surveyIds = new LinkedHashMap<String, Long>();
        try {
            for ( SurveyPreInitiation surveyPreInitiation : surveyPreInitiations ) {
                surveyPreInitiation = surveyHandler.saveSurveyPreInitiationObject( surveyPreInitiation );

                surveyIds.put( surveyPreinitiationTransformer.getParticipantForResponse(surveyPreInitiation.getParticipantType()),
                    surveyPreInitiation.getSurveyPreIntitiationId());

                if(surveyPreInitiation.getStatus() == CommonConstants.STATUS_SURVEYPREINITIATION_DUPLICATE_RECORD) {
                	isDuplicate = true;
                }
                else if(surveyPreInitiation.getStatus() == CommonConstants.STATUS_SURVEYPREINITIATION_MISMATCH_RECORD) {
                	message = "Survey requests accepted sucessfully";
                }
                else if(surveyPreInitiation.getStatus() == CommonConstants.STATUS_SURVEYPREINITIATION_UNSUBSCRIBED ) {
                	isUnsubscribed = true;
                }
                else if( surveyPreInitiation.getStatus() == CommonConstants.STATUS_SURVEYPREINITIATION_SURVEY_NOT_ALLOWED ) {
                    isPartnerSurveyAllowed = false;
                }
            }
            
            // Extra space in the message is intentionally given
            if(isDuplicate)
            	message += " One or more survey requests were duplicate.";
            if(isUnsubscribed)
            	message += " One or more survey requests were unsubscribed.";
            if(!isPartnerSurveyAllowed)
                message += " Partner survey not allowd for one or more requests.";
            LOGGER.info( "SurveyApiController.postSurveyTransaction completed successfully" );

            return restUtils.getRestResponseEntity( HttpStatus.CREATED, message, "surveyId", surveyIds,
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
        SurveyGetV2VO surveyVO = surveyV2Transformer.transformDomainObjectToApiResponse( review, surveyPreInitiation );
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
        SimpleDateFormat sdf = new SimpleDateFormat( CommonConstants.SURVEY_API_DATE_FORMAT );
        sdf.setLenient(false);
        try {
            companyId = adminAuthenticationService.validateAuthHeader( authorizationHeader );
        } catch ( AuthorizationException e1 ) {
            return restUtils.getRestResponseEntity( HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null, request, companyId );
        }

        //parse request parameters from request
        String countStr = request.getParameter( "count" );
        String startStr = request.getParameter( "start" );
        String status = request.getParameter( "status" );
        String startSurveyIDStr = request.getParameter( "startSurveyId" );
        String startReviewDateStr = request.getParameter( "startReviewDateTime" );
        String startTransactionDateStr = request.getParameter( "startTransactionDateTime" );
        String state = request.getParameter( "state" );
        String userEmailAddress = request.getParameter( "user" );
        String includeManagedTeamStr = request.getParameter( "includeManagedTeam" );
        String isAlteredStr = request.getParameter( "isAltered" );

        Set<String> inputRequestParameters = request.getParameterMap().keySet();
        List<String> fixReqParameters = Arrays.asList( "count", "start", "status", "startSurveyId", "startReviewDateTime",
            "startTransactionDateTime", "state", "user", "includeManagedTeam", "isAltered" );
        for ( String currParameter : inputRequestParameters ) {
            if ( !fixReqParameters.contains( currParameter ) ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST,
                    "Unsupported filter parameter : " + currParameter, null, null, request, companyId );
            }
        }

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
            status = CommonConstants.SURVEY_API_SURVEY_STATUS_ALL;
        } else if ( !status.equalsIgnoreCase( CommonConstants.SURVEY_API_SURVEY_STATUS_COMPLETE ) && !status.equalsIgnoreCase( CommonConstants.SURVEY_API_SURVEY_STATUS_INCOMPLETE ) ) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Passed parameter status is invalid", null, null,
                request, companyId );
        }

        boolean isAltered = false;
        if ( isAlteredStr != null ) {
        		if(isAlteredStr != "true" || isAlteredStr != "false") {
        				return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Passed isAltered start is invalid. Valid value is true or false", null, null,
        						request, companyId );
        		}
            try {
            	isAltered = Boolean.parseBoolean(isAlteredStr);
            } catch ( NumberFormatException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Passed isAltered start is invalid. Valid value is true", null, null,
                    request, companyId );
            }
        }



        if ( state != null ) {
            if ( CommonConstants.SURVEY_MOOD_GREAT.equalsIgnoreCase( state ) ) {
                state = CommonConstants.SURVEY_MOOD_GREAT;
            } else if ( CommonConstants.SURVEY_MOOD_OK.equalsIgnoreCase( state ) ) {
                state = CommonConstants.SURVEY_MOOD_OK;
            } else if ( CommonConstants.SURVEY_MOOD_UNPLEASANT.equalsIgnoreCase( state ) ) {
                state = CommonConstants.SURVEY_MOOD_UNPLEASANT;
            } else {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Passed parameter state is invalid", null, null,
                    request, companyId );
            }
        }

        Long startSurveyID = null;
        if ( !StringUtils.isEmpty( startSurveyIDStr ) ) {
            try {
                startSurveyID = Long.parseLong( startSurveyIDStr );
            } catch ( NumberFormatException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Passed parameter startSurveyID is invalid",
                    null, null, request, companyId );
            }
        }

        Date startReviewDate = null;
        if ( startReviewDateStr != null && !startReviewDateStr.isEmpty() ) {
            try {
                startReviewDate = sdf.parse( startReviewDateStr );
            } catch ( ParseException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST,
                    "Passed parameter startReviewDateTime is invalid. Correct date format is " + CommonConstants.SURVEY_API_DATE_FORMAT, null, null, request, companyId );
            }
        }

        //startTransactionDate
        Date startTransactionDate = null;
        if ( startTransactionDateStr != null && !startTransactionDateStr.isEmpty() ) {
            try {
                startTransactionDate = sdf.parse( startTransactionDateStr );
            } catch ( ParseException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST,
                    "Passed parameter startTransactionDateTime is invalid. Correct date format is" + CommonConstants.SURVEY_API_DATE_FORMAT, null, null, request, companyId );
            }
        }


        //includeManagedTeam
        Boolean includeManagedTeam = null;
        if ( !StringUtils.isEmpty( includeManagedTeamStr ) ) {
            try {
                includeManagedTeam = Boolean.parseBoolean( includeManagedTeamStr );
            } catch ( NumberFormatException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST,
                    "Passed parameter includeManagedTeam is invalid", null, null, request, companyId );
            }

            if ( StringUtils.isEmpty( userEmailAddress ) ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST,
                    "Passed parameter userEmailAddress can not be empty if includeManagedTeam is used ", null, null, request,
                    companyId );
            }
        }

        //get user
        List<Long> userIds = null;
        if ( !StringUtils.isEmpty( userEmailAddress ) ) {
            try {
                User user = userManagementService.getUserByEmailAndCompany(companyId, userEmailAddress);
                userIds = new ArrayList<Long>();
                userIds.add( user.getUserId() );

                //if includeManagedTeam is present than get all the users under the given user 
                if ( includeManagedTeam != null && includeManagedTeam == true ) {
                    Set<Long> agentIds = userManagementService.getUserIdsUnderAdmin( user );
                    userIds.addAll( agentIds );
                }

            } catch ( InvalidInputException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST,
                    "Passed parameter user " + userEmailAddress + " is invalid", null, null, request, companyId );
            } catch (NoRecordsFetchedException e) {
            	return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "User not registered or you do not have access to this account.",
                        null, null, request , companyId  );
			}
        }

        //get data from database
        SurveysAndReviewsVO surveysAndReviewsVO = surveyHandler.getSurveysByFilterCriteria( state, startSurveyID,
            startReviewDate, startTransactionDate, userIds, isAltered , start, count, companyId );

        //create vo object
        List<SurveyGetV2VO> surveyVOs = surveysAndReviewsV2VOTransformer
            .transformDomainObjectToApiResponse( surveysAndReviewsVO );
        LOGGER.info( "SurveyApiController.getSurveyTransaction completed successfully" );

        return restUtils.getRestResponseEntity( HttpStatus.OK, "Request Successfully processed", "surveys", surveyVOs, request,
            companyId );
    }

    @RequestMapping ( value = "/addquestiontosurvey", method = RequestMethod.POST)
    @ApiOperation ( value = "Add question to existing survey.")
	public String addQuestionToExistingSurvey(@RequestBody SurveyQuestionDetails questionDetails) throws SSApiException {
    	LOGGER.info("API call for add question to existing survey.");
    	long questionId = 0;
		try {
			questionId = surveyBuilder.createSurveyQuestionForExistingSurvey(questionDetails);
		} catch (InvalidInputException ie) {
			LOGGER.error("Invalid input exception caught while adding question to existing survey.",ie);
			throw new SSApiException("Invalid input exception caught while adding question to existing survey.",ie);
		}catch(NoRecordsFetchedException e){
			LOGGER.error("NoRecordsFetchedException caught while adding new question to existing survey.",e);
			throw new SSApiException("NoRecordsFetchedException caught while adding new question to existing survey.",e);
		}
		return Long.toString(questionId);
	}

    @RequestMapping ( value = "/updatesurveyquestion", method = RequestMethod.PUT)
    @ApiOperation ( value = "update existing survey question.")
	public String updateQuestionInExistingSurvey(@RequestBody SurveyQuestionDetails questionDetails) throws SSApiException {
    	LOGGER.info("API call for update question of existing survey.");
		try {
			surveyBuilder.updateSurveyQuestionAndAnswer(questionDetails);
		} catch (InvalidInputException ie) {
			LOGGER.error("Invalid input exception caught while updating question to existing survey.",ie);
			throw new SSApiException("Invalid input exception caught while adding question to existing survey.",ie);
		}catch(NoRecordsFetchedException e){
			LOGGER.error("NoRecordsFetchedException caught while updating question in existing survey.",e);
			throw new SSApiException("NoRecordsFetchedException caught while updating question in existing survey.",e);
		}
    	return "SUCCESS";
    }

    @RequestMapping ( value = "/removesurveyquestion", method = RequestMethod.DELETE)
    @ApiOperation ( value = "remove question from survey question.")
	public String removeQuestionFromExistingSurvey(long userId, long surveyQuestionId) throws SSApiException {
    	LOGGER.info("API call to remove question from existing survey.");
		try {
			surveyBuilder.removeQuestionFromSurvey(userId,surveyQuestionId);
		} catch (InvalidInputException ie) {
			LOGGER.error("Invalid input exception caught while removing question from existing survey.",ie);
			throw new SSApiException("Invalid input exception caught while removing question from existing survey.",ie);
		}catch(NoRecordsFetchedException e){
			LOGGER.error("NoRecordsFetchedException caught while removing question from existing survey.",e);
			throw new SSApiException("NoRecordsFetchedException caught while removing question from existing survey.",e);
		}
    	return "SUCCESS";
    }

    @RequestMapping ( value = "/surveys/{surveyId}/response", method = RequestMethod.POST)
    @ApiOperation ( value = "Update Survey Response And Get Swear words")
    public String updateSurveyResponse( @PathVariable ( "surveyId") String surveyId, String question, String questionType, String answer, int stage,
        boolean isUserRankingQuestion, boolean isNpsQuestion , int questionId , boolean considerForScore ){
        LOGGER.info("Method updateSurveyResponse() started to store response of customer.");
        surveyHandler.updateCustomerAnswersInSurvey(surveyId, question, questionType, answer, stage, isUserRankingQuestion, isNpsQuestion, questionId, considerForScore);
        LOGGER.info("Method updateSurveyResponse() to store response of customer finished successfully");
        return "Survey response updated successfully";
    }

    @RequestMapping ( value = "/surveys/{surveyId}/score", method = RequestMethod.POST)
    @ApiOperation ( value = "Get Survey Transaction")
    public double updateScore(@PathVariable ( "surveyId") String surveyId,String mood,String feedback,boolean isAbusive,String agreedToShare){
        LOGGER.info("Method storeScore() started to store score of survey");
        double score = surveyHandler.updateGatewayQuestionResponseAndScore(surveyId, mood, feedback, isAbusive, agreedToShare);
        LOGGER.info("Method storeScore() to store score of survey finished successfully");
        return score;
    }

    @RequestMapping ( value = "/swearwords",method = RequestMethod.GET)
    @ApiOperation ( value = "Get Swear Words" )
    public String getSwearWordsList(long companyId) throws InvalidInputException{
        LOGGER.info("Method getSwearWordsList() started to send swear word list");
        return new Gson().toJson(surveyHandler.fetchSwearWords( "companyId", companyId ));
    }


    @RequestMapping ( value = "/surveycount", method = RequestMethod.GET)
    @ApiOperation ( value = "Get Survey Transactions Count")
    public ResponseEntity<?> getSurveyTransactionsCouunt( HttpServletRequest request ) throws SSApiException
    {
        LOGGER.info( "SurveyApiController.getSurveyTransactionsCouunt started" );

        //authorize request
        String authorizationHeader = request.getHeader( CommonConstants.SURVEY_API_REQUEST_PARAMETER_AUTHORIZATION );
        long companyId = 0;
        SimpleDateFormat sdf = new SimpleDateFormat( CommonConstants.SURVEY_API_DATE_FORMAT );
        sdf.setLenient(false);
        try {
            companyId = adminAuthenticationService.validateAuthHeader( authorizationHeader );
        } catch ( AuthorizationException e1 ) {
            return restUtils.getRestResponseEntity( HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null, request, companyId );
        }

        //parse request parameters from request
        String status = request.getParameter( "status" );
        String startSurveyIDStr = request.getParameter( "startSurveyId" );
        String startReviewDateStr = request.getParameter( "startReviewDateTime" );
        String startTransactionDateStr = request.getParameter( "startTransactionDateTime" );
        String state = request.getParameter( "state" );
        String userEmailAddress = request.getParameter( "user" );
        String includeManagedTeamStr = request.getParameter( "includeManagedTeam" );
        String isAlteredStr = request.getParameter( "isAltered" );

        Set<String> inputRequestParameters = request.getParameterMap().keySet();
        List<String> fixReqParameters = Arrays.asList( "status", "startSurveyId", "startReviewDateTime",
            "startTransactionDateTime", "state", "user", "includeManagedTeam", "isAltered" );
        for ( String currParameter : inputRequestParameters ) {
            if ( !fixReqParameters.contains( currParameter ) ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST,
                    "Unsupported filter parameter : " + currParameter, null, null, request, companyId );
            }
        }

        if ( status == null ) {
            status = CommonConstants.SURVEY_API_SURVEY_STATUS_ALL;
        } else if ( !status.equalsIgnoreCase( CommonConstants.SURVEY_API_SURVEY_STATUS_COMPLETE ) && !status.equalsIgnoreCase( CommonConstants.SURVEY_API_SURVEY_STATUS_INCOMPLETE ) ) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Passed parameter status is invalid", null, null,
                request, companyId );
        }

        boolean isAltered = false;
        if ( isAlteredStr != null ) {
        	    if(isAlteredStr != "true" || isAlteredStr != "false") {
        	    	return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Passed isAltered start is invalid. Valid value is true or false", null, null,
                            request, companyId );
        	    }
            try {
            	isAltered = Boolean.parseBoolean(isAlteredStr);
            } catch ( NumberFormatException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Passed isAltered start is invalid. Valid value is true or false", null, null,
                    request, companyId );
            }
        }



        if ( state != null ) {
            if ( CommonConstants.SURVEY_MOOD_GREAT.equalsIgnoreCase( state ) ) {
                state = CommonConstants.SURVEY_MOOD_GREAT;
            } else if ( CommonConstants.SURVEY_MOOD_OK.equalsIgnoreCase( state ) ) {
                state = CommonConstants.SURVEY_MOOD_OK;
            } else if ( CommonConstants.SURVEY_MOOD_UNPLEASANT.equalsIgnoreCase( state ) ) {
                state = CommonConstants.SURVEY_MOOD_UNPLEASANT;
            } else {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Passed parameter state is invalid", null, null,
                    request, companyId );
            }
        }

        Long startSurveyID = null;
        if ( !StringUtils.isEmpty( startSurveyIDStr ) ) {
            try {
                startSurveyID = Long.parseLong( startSurveyIDStr );
            } catch ( NumberFormatException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Passed parameter startSurveyID is invalid",
                    null, null, request, companyId );
            }
        }

        Date startReviewDate = null;
        if ( startReviewDateStr != null && !startReviewDateStr.isEmpty() ) {
            try {
                startReviewDate = sdf.parse( startReviewDateStr );
            } catch ( ParseException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST,
                    "Passed parameter startReviewDateTime is invalid. Correct date format is " + CommonConstants.SURVEY_API_DATE_FORMAT, null, null, request, companyId );
            }
        }

        //startTransactionDate
        Date startTransactionDate = null;
        if ( startTransactionDateStr != null && !startTransactionDateStr.isEmpty() ) {
            try {
                startTransactionDate = sdf.parse( startTransactionDateStr );
            } catch ( ParseException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST,
                    "Passed parameter startTransactionDateTime is invalid. Correct date format is " + CommonConstants.SURVEY_API_DATE_FORMAT, null, null, request, companyId );
            }
        }


        //includeManagedTeam
        Boolean includeManagedTeam = null;
        if ( !StringUtils.isEmpty( includeManagedTeamStr ) ) {
            try {
                includeManagedTeam = Boolean.parseBoolean( includeManagedTeamStr );
            } catch ( NumberFormatException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST,
                    "Passed parameter includeManagedTeam is invalid", null, null, request, companyId );
            }

            if ( StringUtils.isEmpty( userEmailAddress ) ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST,
                    "Passed parameter userEmailAddress can not be empty if includeManagedTeam is used ", null, null, request,
                    companyId );
            }
        }

        //get user
        List<Long> userIds = null;
        if ( !StringUtils.isEmpty( userEmailAddress ) ) {
            try {
                User user = userManagementService.getUserByEmailAndCompany(companyId, userEmailAddress);
                userIds = new ArrayList<Long>();
                userIds.add( user.getUserId() );

                //if includeManagedTeam is present than get all the users under the given user 
                if ( includeManagedTeam != null && includeManagedTeam == true ) {
                    Set<Long> agentIds = userManagementService.getUserIdsUnderAdmin( user );
                    userIds.addAll( agentIds );
                }

            } catch ( InvalidInputException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST,
                    "Passed parameter user " + userEmailAddress + " is invalid", null, null, request, companyId );
            } catch (NoRecordsFetchedException e) {
            	return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "User not registered or you do not have access to this account.",
                        null, null, request , companyId  );
			}
        }

        //get data from database
       Integer surveyCount = surveyHandler.getSurveysCountByFilterCriteria( state, startSurveyID,
            startReviewDate, startTransactionDate, userIds, isAltered, companyId );

       Float surveyAvgScore = surveyHandler.getSurveysAvgScoreByFilterCriteria( state, startSurveyID,
               startReviewDate, startTransactionDate, userIds, isAltered, companyId );

        //create vo object
       	SurveyCountVO surveyCountVO = new SurveyCountVO();
       	surveyCountVO.setNoOfReviews(surveyCount);
       	surveyCountVO.setAvgScore( Float.valueOf(String.format("%.2f",  surveyAvgScore) ) );
        LOGGER.info( "SurveyApiController.getSurveyTransactionsCouunt completed successfully" );

        return restUtils.getRestResponseEntity( HttpStatus.OK, "Request Successfully processed", "surveyStats", surveyCountVO, request,
            companyId );
    }


    @RequestMapping ( value = "/incompletesurveys", method = RequestMethod.GET)
    @ApiOperation ( value = "Get Survey Transactions")
    public ResponseEntity<?> getIncompleteSurveyTransactions( HttpServletRequest request ) throws SSApiException
    {
        LOGGER.info( "SurveyApiController.getIncompleteSurveyTransactions started" );

        //authorize request
        String authorizationHeader = request.getHeader( CommonConstants.SURVEY_API_REQUEST_PARAMETER_AUTHORIZATION );
        long companyId = 0;
        SimpleDateFormat sdf = new SimpleDateFormat( CommonConstants.SURVEY_API_DATE_FORMAT );
        sdf.setLenient(false);
        try {
            companyId = adminAuthenticationService.validateAuthHeader( authorizationHeader );
        } catch ( AuthorizationException e1 ) {
            return restUtils.getRestResponseEntity( HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null, request, companyId );
        }

        //parse request parameters from request
        String countStr = request.getParameter( "count" );
        String startStr = request.getParameter( "start" );
        String startSurveyIDStr = request.getParameter( "startSurveyId" );
        String startTransactionDateStr = request.getParameter( "startTransactionDateTime" );
        String userEmailAddress = request.getParameter( "user" );
        String includeManagedTeamStr = request.getParameter( "includeManagedTeam" );

        Set<String> inputRequestParameters = request.getParameterMap().keySet();
        List<String> fixReqParameters = Arrays.asList( "count", "start", "status", "startSurveyId",
            "startTransactionDateTime", "user", "includeManagedTeam" );
        for ( String currParameter : inputRequestParameters ) {
            if ( !fixReqParameters.contains( currParameter ) ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST,
                    "Unsupported filter parameter : " + currParameter, null, null, request, companyId );
            }
        }

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

        Long startSurveyID = null;
        if ( !StringUtils.isEmpty( startSurveyIDStr ) ) {
            try {
                startSurveyID = Long.parseLong( startSurveyIDStr );
            } catch ( NumberFormatException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Passed parameter startSurveyID is invalid",
                    null, null, request, companyId );
            }
        }

        //startTransactionDate
        Date startTransactionDate = null;
        if ( startTransactionDateStr != null && !startTransactionDateStr.isEmpty() ) {
            try {
                startTransactionDate = sdf.parse( startTransactionDateStr );
            } catch ( ParseException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST,
                    "Passed parameter startTransactionDateTime is invalid", null, null, request, companyId );
            }
        }


        //includeManagedTeam
        Boolean includeManagedTeam = null;
        if ( !StringUtils.isEmpty( includeManagedTeamStr ) ) {
            try {
                includeManagedTeam = Boolean.parseBoolean( includeManagedTeamStr );
            } catch ( NumberFormatException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST,
                    "Passed parameter includeManagedTeam is invalid", null, null, request, companyId );
            }

            if ( StringUtils.isEmpty( userEmailAddress ) ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST,
                    "Passed parameter userEmailAddress can not be empty if includeManagedTeam is used ", null, null, request,
                    companyId );
            }
        }

        //get user
        List<Long> userIds = null;
        if ( !StringUtils.isEmpty( userEmailAddress ) ) {
            try {
                User user = userManagementService.getUserByEmail(userEmailAddress);

                //check if user is in same comapny
                if(user.getCompany().getCompanyId() != companyId) {
                	return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "You do not have access to the user's data.",
                            null, null, request , companyId  );
                }

                userIds = new ArrayList<Long>();
                userIds.add( user.getUserId() );

                //if includeManagedTeam is present than get all the users under the given user 
                if ( includeManagedTeam != null && includeManagedTeam == true ) {
                    Set<Long> agentIds = userManagementService.getUserIdsUnderAdmin( user );
                    userIds.addAll( agentIds );
                }

            } catch ( InvalidInputException e ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST,
                    "Passed parameter user " + userEmailAddress + " is invalid", null, null, request, companyId );
            } catch (NoRecordsFetchedException e) {
            	return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "The user specified could not be found in the system.",
                        null, null, request , companyId  );
			}
        }

        //get data from database
        SurveysAndReviewsVO surveysAndReviewsVO = surveyHandler.getIncompelteSurveysByFilterCriteria( startSurveyID, startTransactionDate, userIds , start, count, companyId );

        //create vo object
        List<IncompeteSurveyGetVO> incompleteSurveyVOs = incompleteSurveyVOTransformer.transformDomainObjectToApiResponse(surveysAndReviewsVO);
        LOGGER.info( "SurveyApiController.getIncompleteSurveyTransactions completed successfully" );

        return restUtils.getRestResponseEntity( HttpStatus.OK, "Request Successfully processed", "surveys", incompleteSurveyVOs, request,
            companyId );
    }


    @RequestMapping ( value = "/swearwords/{companyId}", method = RequestMethod.GET)
    @ApiOperation ( value = "Get Swear Words")
    public ResponseEntity<?> getSwearWords( @PathVariable ( "companyId") long companyId, HttpServletRequest request )
        throws SSApiException, InvalidInputException
    {
        LOGGER.info( "SurveyApiController.getSwearWords started for companyId:{}",companyId );

        //authorize request
        long companyIdAuth = 0;
        String[] swear_words = null;
        String authorizationHeader = request.getHeader( CommonConstants.SURVEY_API_REQUEST_PARAMETER_AUTHORIZATION );
        try {
            companyIdAuth = adminAuthenticationService.validateAuthHeader( authorizationHeader );
            if(companyId != companyIdAuth)
                return restUtils.getRestResponseEntity( HttpStatus.UNAUTHORIZED, "YOU NEED TO USE AUTH OF THE SAME COMPANY", null, null, request, companyId );
        } catch ( AuthorizationException e1 ) {
            return restUtils.getRestResponseEntity( HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null, request, companyId );
        }
        swear_words = surveyHandler.fetchSwearWords( "companyId", companyId );
        return restUtils.getRestResponseEntity( HttpStatus.OK, "Request Successfully processed", "swearWords", swear_words, request,
            companyId );
    }

    @RequestMapping ( value = "/swearwords/{companyId}", method = RequestMethod.POST)
    @ApiOperation ( value = "post swear words Transaction")
    public ResponseEntity<?> postSwearWords( @PathVariable ( "companyId") long companyId, HttpServletRequest request , @RequestBody String[] swearWords)
        throws SSApiException, InvalidInputException
    {
        LOGGER.info( "SurveyApiController.getSwearWords started for companyId:{}",companyId );
        AUDIT_LOG.info("Swear words list was changed by companyId : {} \n the swear word list is : {}",companyId,swearWords );
        AUDIT_app.info("test reference");


        //authorize request
        long companyIdAuth = 0;
        String authorizationHeader = request.getHeader( CommonConstants.SURVEY_API_REQUEST_PARAMETER_AUTHORIZATION );
        try {
            companyIdAuth = adminAuthenticationService.validateAuthHeader( authorizationHeader );
            if(companyId != companyIdAuth)
                return restUtils.getRestResponseEntity( HttpStatus.UNAUTHORIZED, "YOU NEED TO USE AUTH OF THE SAME COMPANY", null, null, request, companyId );
        } catch ( AuthorizationException e1 ) {
            return restUtils.getRestResponseEntity( HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null, request, companyId );
        }
        surveyHandler.updateSwearWords( "companyId", companyId, swearWords );
        return restUtils.getRestResponseEntity( HttpStatus.OK, "Request Successfully processed", null, null, request,
            companyId );
    }

    /**
     *
     * @param bulkSurveyPutVO
     * @param request
     * @return
     * @throws SSApiException
     */
    @RequestMapping ( value = "/bulksurveys", method = RequestMethod.PUT)
    @ApiOperation ( value = "Post Multiple Survey Transactions")
    public ResponseEntity<?> postBulkSurveyTransactions( @Valid @RequestBody BulkSurveyPutVO bulkSurveyPutVO, HttpServletRequest request )
        throws SSApiException
    {
        LOGGER.info( "SurveyApiController.postMultipleSurveyTransactions started" );
        request.setAttribute( "input", bulkSurveyPutVO );
        List<BulkSurveyProcessResponseVO> bulkSurveyProcessResponseVOs = new ArrayList<BulkSurveyProcessResponseVO>();
        String message = "Surveys successfully processed.";

        String authorizationHeader = request.getHeader( CommonConstants.SURVEY_API_REQUEST_PARAMETER_AUTHORIZATION );
        //authorize request
        try {
            long authCompanyId = adminAuthenticationService.validateAuthHeader( authorizationHeader );
            if(authCompanyId != CommonConstants.DEFAULT_COMPANY_ID)
                return restUtils.getRestResponseEntity( HttpStatus.UNAUTHORIZED, "Passed auth token is not for default company 1", null, null, request, 0l );
        } catch ( AuthorizationException e ) {
            return restUtils.getRestResponseEntity( HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null, request, 0l );
        }

        if(bulkSurveyPutVO == null)
                return restUtils.getRestResponseEntity( HttpStatus.NOT_ACCEPTABLE, "Passed Body is null or empty", null, null, request, 0l );

        long companyId = bulkSurveyPutVO.getCompanyId();
        if(companyId <= 0l)
            return restUtils.getRestResponseEntity( HttpStatus.NOT_ACCEPTABLE, "Passed companyId is  invalid", null, null, request, 0l );

        //check if survey list is present
        List<SurveyPutVO> surveyPutVOs = bulkSurveyPutVO.getSurveys();
        if(surveyPutVOs == null || surveyPutVOs.size() == 0)
                return restUtils.getRestResponseEntity( HttpStatus.NOT_ACCEPTABLE, "Survey list can not be null or empty", null, null, request, companyId );


        //parse input object
        List<SurveyPreInitiation> transactionSurveyPreInitiations;
        for (SurveyPutVO surveyPutVO : surveyPutVOs) {
            // create response VO
            BulkSurveyProcessResponseVO bulkSurveyProcessResponseVO = new BulkSurveyProcessResponseVO();
            Map<Integer, Long> surveyIds = new HashMap<>();
            try {
                if(surveyPutVO.getServiceProviderInfo() == null && surveyPutVO.getTransactionInfo() == null) {
                    throw new InvalidInputException( "The row is empty" );
                }
                //transform api object to domain object
                transactionSurveyPreInitiations = surveyPreinitiationTransformer.transformApiRequestToDomainObject(surveyPutVO, companyId, bulkSurveyPutVO.getSource());
                // validate survey
                transactionSurveyPreInitiations = surveyHandler.validatePreinitiatedRecord(transactionSurveyPreInitiations, companyId);
                //save validated object
                for (SurveyPreInitiation surveyPreInitiation : transactionSurveyPreInitiations) {
                    surveyPreInitiation = surveyHandler.saveSurveyPreInitiationTempObject(surveyPreInitiation);
                    if(surveyPreInitiation.getStatus() == CommonConstants.SURVEY_STATUS_PRE_INITIATED)
                        surveyIds.put(surveyPreInitiation.getParticipantType(), surveyPreInitiation.getSurveyPreIntitiationId());
                }
                //fill response vo
                bulkSurveyProcessResponseVO.setProcessed(true);
                bulkSurveyProcessResponseVO.setSurveyIds(surveyIds);
                bulkSurveyProcessResponseVO.setLineNumber( surveyPutVO.getLineNumber() );
                bulkSurveyProcessResponseVOs.add(bulkSurveyProcessResponseVO);
            } catch (InvalidInputException e) {
                //fill response vo
                bulkSurveyProcessResponseVO.setProcessed(false);
                bulkSurveyProcessResponseVO.setErrorMessage(e.getMessage());
                bulkSurveyProcessResponseVO.setLineNumber( surveyPutVO.getLineNumber() );
                bulkSurveyProcessResponseVOs.add(bulkSurveyProcessResponseVO);
            }
        }
        LOGGER.info( "SurveyApiController.postSurveyTransaction completed successfully" );

        return restUtils.getRestResponseEntity( HttpStatus.CREATED, message, "response", bulkSurveyProcessResponseVOs,
                request, companyId );
    }
    
    //post to social media api
    @RequestMapping ( value = "/surveys/{surveyId}/socialMedia", method = RequestMethod.POST)
    @ApiOperation ( value = "Post to Social media")
    public ResponseEntity<?> postToSocialMedia(@PathVariable ( "surveyId") String surveyId,@Valid @RequestBody PostToSocialMedia postsToSocialMedia , HttpServletRequest request){
        LOGGER.info("Method postToSocialMedia() started to post review");
        try {
        socialManagementService.postToSocialMedia(postsToSocialMedia.getAgentName(),postsToSocialMedia.getAgentProfileLink(),postsToSocialMedia.getCustFirstName(),postsToSocialMedia.getCustLastName(),
        		postsToSocialMedia.getAgentId(),postsToSocialMedia.getRating(),surveyId,postsToSocialMedia.getFeedback(),postsToSocialMedia.isAbusive(),postsToSocialMedia.getServerBaseUrl(),
        		postsToSocialMedia.isOnlyPostToSocialSurvey(),postsToSocialMedia.isZillow());
        } catch ( NonFatalException e1 ) {
            return restUtils.getRestResponseEntity( HttpStatus.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", null, null, request, 0 );
        }
        LOGGER.info("Method postToSocialMedia() to post review finished successfully");
       return restUtils.getRestResponseEntity( HttpStatus.OK, "Posted successfully", null, null, request, 0 );
    }

    @RequestMapping ( value = "/reviews/bulk", method = RequestMethod.POST)
    @ApiOperation ( value = "Find existing reviees from given surveys")
    public ResponseEntity<?> saveOrUpdateReviews( @Valid @RequestBody List<SurveyDetailsVO> surveyDetails,
        @RequestHeader ( "authorizationHeader") String authorizationHeader, HttpServletRequest request )
        throws SSApiException{
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            List<BulkWriteErrorVO> bulkWriteErrors = surveyHandler.saveOrUpdateReviews(surveyDetails);
            return new ResponseEntity<>(bulkWriteErrors, HttpStatus.OK);
        } catch ( InvalidInputException | ParseException e ) {
            throw new SSApiException( e.getMessage(), e );
        } catch ( AuthorizationException e ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }
    }
    
    /**
     * Add a reply to a review
     * 
     * @param surveyId
     * @param replyText
     * @param replyByName
     * @return
     * @throws InvalidInputException
     * @throws ProfileNotFoundException
     */
    @RequestMapping ( value = "/surveys/{surveyId}/replies", method = RequestMethod.POST)
    @ApiOperation ( value = "Add a reply to the review")
    public ResponseEntity<?> replyToReview( @PathVariable ( "surveyId") String surveyId, String replyText, String replyByName,
        String replyById, String entityType ) throws InvalidInputException, ProfileNotFoundException {
        String threadName = Thread.currentThread().getName(); 
        Thread.currentThread().setName("CreateReply surveyId:" + surveyId);
        LOGGER.info( "Method to create a reply to a review started" );
        try {
            ReviewReplyVO reviewReplyVO = surveyHandler.createOrUpdateReplyToReview( surveyId, replyText, replyByName, 
                replyById, null , entityType);
            LOGGER.info( "Method to create a reply to a review finished successfully" );
            return new ResponseEntity<>(reviewReplyVO ,HttpStatus.OK);
        } catch ( Exception e ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }
        finally {
            Thread.currentThread().setName( threadName );
        }
    }

    /**
     * Edit a specific reply to a review
     * 
     * @param surveyId
     * @param replyId
     * @param replyText
     * @param replyByName
     * @return
     * @throws InvalidInputException
     * @throws ProfileNotFoundException
     */
    @RequestMapping ( value = "/surveys/{surveyId}/replies/{replyId}", method = RequestMethod.PUT) 
    @ApiOperation ( value = "Update the reply of a review")
    public ResponseEntity<?> editReplyToReview(@PathVariable ( "surveyId") String surveyId, @PathVariable ( "replyId") String replyId,
        String replyText, String replyByName, String replyById){
        
        String threadName = Thread.currentThread().getName(); 
        Thread.currentThread().setName( "UpdateReply replyId:" + replyId );
        LOGGER.info("Method to edit reply to a review started");
        try {
            ReviewReplyVO reviewReplyVO =surveyHandler.createOrUpdateReplyToReview(surveyId, replyText, replyByName, 
                replyById, replyId , null);
            LOGGER.info("Method to edit reply to a review finished successfully");
            return new ResponseEntity<>(reviewReplyVO, HttpStatus.OK);
        }
        catch(Exception e) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }
        finally {
            Thread.currentThread().setName( threadName );
        }
    }
    
    
    @RequestMapping ( value = "/surveys/{surveyId}/replies/{replyId}", method = RequestMethod.DELETE) 
    @ApiOperation ( value = "Delete the reply of a review")
    public ResponseEntity<?> deleteReplyToReview(@PathVariable ( "surveyId") String surveyId, @PathVariable ( "replyId") String replyId){
        
        String threadName = Thread.currentThread().getName(); 
        Thread.currentThread().setName( "DeleteReply replyId:" + replyId );
        LOGGER.info("Method to delete a reply to a review started");
        try {
            surveyHandler.deleteReviewReply( replyId, surveyId );
            LOGGER.info( "Method to delete a reply to a review completed successfully" );
            return new ResponseEntity<>( HttpStatus.OK );
        } catch ( Exception e ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }
        finally {
            Thread.currentThread().setName( threadName );
        }
    }
}
