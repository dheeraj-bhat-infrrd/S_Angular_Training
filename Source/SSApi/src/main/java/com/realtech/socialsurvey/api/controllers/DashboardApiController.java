package com.realtech.socialsurvey.api.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.proto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.core.exception.AuthorizationException;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.vo.SmsSurveyReminderResponseVO;

@RestController
@RequestMapping ("/v1")
public class DashboardApiController {

	private static final Logger LOG = LoggerFactory.getLogger( DashboardApiController.class );

	@Autowired
    private AdminAuthenticationService adminAuthenticationService;
	
	@Autowired
	private SurveyHandler surveyHandler;
	
	/**
	 * Method to resend multiple pre intiated survey reminders via sms
	 * 
	 * @param authorizationHeader
	 * @param surveysSelectedStr
	 * @return
	 */
	@ResponseBody
	@PostMapping ( value = "/surveys/sendsmsreminder/manual")
	public  ResponseEntity<?> sendMultipleSurveyRemindersViaSms( 
			@RequestHeader ( "authorizationHeader") String authorizationHeader,
			@RequestParam("companyId") long companyId, 
			@RequestParam("selectedSurveys") String selectedSurveys)
	{
		LOG.info( "Method sendMultipleSurveyRemindersViaSms() called" );

		try {
			
			String[] surveysSelectedArray = selectedSurveys.split( "," );
			
			adminAuthenticationService.validateAuthHeader( authorizationHeader );
			
			List<SmsSurveyReminderResponseVO> response = surveyHandler.sendMultipleIncompleteSurveyReminder(companyId, surveysSelectedArray);
			
			return new ResponseEntity<>( response, HttpStatus.OK );
		}
        catch ( AuthorizationException authException ) {
            
        	LOG.error("Exception while authenticating sendMultipleSurveyRemindersViaSms() ", authException);
        
        	Map<String, String> response = new HashMap<>();
        	response.put("errMsg", "Something went wrong, Please try again after sometime.");
            return new ResponseEntity<>( response, HttpStatus.UNAUTHORIZED );
        }
        catch ( Exception ex ) {
        	LOG.error("Exception while sending multiple survey reminders over SMS", ex);
        	Map<String, String> response = new HashMap<>();
        	response.put("errMsg", "Something went wrong, Please try again after sometime.");
            return new ResponseEntity<>( response, HttpStatus.INTERNAL_SERVER_ERROR );
        }
	}
}
