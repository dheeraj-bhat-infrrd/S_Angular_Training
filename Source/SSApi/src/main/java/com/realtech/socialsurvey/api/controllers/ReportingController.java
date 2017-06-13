package com.realtech.socialsurvey.api.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.api.utils.RestUtils;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping ( "/v1")
public class ReportingController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( SurveyApiController.class );

    @Autowired
    private RestUtils restUtils;
    
    @RequestMapping ( value = "/createdummyapi", method = RequestMethod.GET)
    @ApiOperation ( value = "Dummy api for reporting")
    public ResponseEntity<?> getReportingPage( HttpServletRequest request )
    {
        LOGGER.info( "Reporting page just started" );
        return restUtils.getRestResponseEntity( HttpStatus.OK, "Request Successfully processed", "reporting", null , request, 0 ); 
    }
    

}
