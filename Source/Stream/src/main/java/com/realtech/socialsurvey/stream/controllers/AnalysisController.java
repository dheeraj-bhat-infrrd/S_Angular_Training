package com.realtech.socialsurvey.stream.controllers;

import com.realtech.socialsurvey.stream.entities.FailedEmailMessage;
import com.realtech.socialsurvey.stream.services.FailedMessagesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * Created by nishit on 04/01/18.
 */
@RestController @RequestMapping ("api/v1/analyze") @Api (value = "Analysis APIs", description = "APIs for Analysis on stream data") public class AnalysisController
{
    private static final Logger LOG = LoggerFactory.getLogger( AnalysisController.class );

    private FailedMessagesService failedMessagesService;


    @Autowired public void setFailedMessagesService( FailedMessagesService failedMessagesService )
    {
        this.failedMessagesService = failedMessagesService;
    }


    /**
     * Returns failed messages list
     * @return
     */
    @ApiOperation ( value = "Gets list of failed email messages.", response = FailedEmailMessage.class, responseContainer = "List")
    @ApiResponses ( value = { @ApiResponse ( code = 200, message = "Successfully fetched the list"),
        @ApiResponse ( code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse ( code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse ( code = 503, message = "Service not available") })
    @RequestMapping (value = "/messages/email/failed", method = RequestMethod.GET) public ResponseEntity<?> failedStreamEmailMessages(
        @RequestParam ("filter") String type )
    {
        LOG.info( "Getting failed stream email messages for filter {}", type );
        List<FailedEmailMessage> failedEmailMessages = failedMessagesService.getFailedEmailMessages( type );
        return new ResponseEntity<>( failedEmailMessages, HttpStatus.OK );
    }
}
