package com.realtech.socialsurvey.stream.controllers;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.stream.entities.FailedEmailMessage;
import com.realtech.socialsurvey.stream.services.FailedMessagesService;
import com.realtech.socialsurvey.stream.services.FailedSocialPostService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Created by nishit on 04/01/18.
 */
@RestController @RequestMapping ("api/v1/analyze") @Api (value = "Analysis APIs", description = "APIs for Analysis on stream data") public class AnalysisController
{
    private static final Logger LOG = LoggerFactory.getLogger( AnalysisController.class );
    
    private static final int NUMBER_OF_RECORDS = 10;
    private FailedMessagesService failedMessagesService;
    private FailedSocialPostService failedSocialPostService;


    @Autowired public void setFailedMessagesService( FailedMessagesService failedMessagesService )
    {
        this.failedMessagesService = failedMessagesService;
    }
    
    @Autowired public void setFailedSocialPostService( FailedSocialPostService failedSocialPostService )
    {
        this.failedSocialPostService = failedSocialPostService;
    }
    

    
    /**
     * Returns failed messages list of 10 records
     * @return
     */
    @ApiOperation ( value = "Gets list of 10 failed email messages.", response = FailedEmailMessage.class, responseContainer = "List")
    @ApiResponses ( value = { @ApiResponse ( code = 200, message = "Successfully fetched the list"),
        @ApiResponse ( code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse ( code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse ( code = 503, message = "Service not available") })
    @RequestMapping (value = "/messages/email/failed", method = RequestMethod.GET) public ResponseEntity<?> failedStreamPaginatedEmailMessages(
        @RequestParam ("filter") String type, @RequestParam ("offset") int offset )
    {
        LOG.info( "Getting 10 failed stream email messages for filter {} from record {}", type, offset );
        Pageable topTen = new PageRequest(offset, NUMBER_OF_RECORDS);
        List<FailedEmailMessage> failedEmailMessages = failedMessagesService.getPaginatedFailedEmailMessages(type, topTen);
        return new ResponseEntity<>( failedEmailMessages, HttpStatus.OK );    	
    }
    
	/**
	 * Returns failed message based on id
	 * 	
	 * @return
	 */
	@ApiOperation(value = "Gets failed email message based on id.", response = FailedEmailMessage.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully fetched the email message"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 503, message = "Service not available") })
	@RequestMapping(value = "/messages/email/failed/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> failedStreamEmailMessageById(@PathVariable ObjectId id) {
		LOG.info("Getting failed stream email message for object id {}", id);
		FailedEmailMessage failedEmailMessage = failedMessagesService.getFailedEmailMessageById(id);
		return new ResponseEntity<>(failedEmailMessage, HttpStatus.OK);
	}
	
	
	/**
	 * Returns failed messages based on companyId
	 * 
	 * @return
	 */
	@ApiOperation(value = "Gets failed email messages based on companyId.", response = FailedEmailMessage.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully fetched the list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 503, message = "Service not available") })
	@RequestMapping(value = "/messages/email/failed/company/{companyid}", method = RequestMethod.GET)
	public ResponseEntity<?> failedEmailMessagesByCompanyId(@PathVariable long companyid,
			@RequestParam("offset") int offset) {
		LOG.info("Fetching failed email messaged for company id {}", companyid);
		Pageable topTen = new PageRequest(offset, NUMBER_OF_RECORDS);
		List<FailedEmailMessage> failedEmailMessages = failedMessagesService
				.getFailedEmailMessagesByCompanyId(companyid, topTen);
		return new ResponseEntity<>(failedEmailMessages, HttpStatus.OK);

	}
	
	/**
	 * Returns failed messages based on recipients
	 * 
	 * @return
	 */
	@ApiOperation(value = "Gets failed email messages based on recipients.", response = FailedEmailMessage.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully fetched the list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 503, message = "Service not available") })
	@RequestMapping(value = "/messages/email/failed/recipients", method = RequestMethod.GET)
	public ResponseEntity<?> failedEmailMessagesByRecipients(@RequestParam("recipients") List<String> recipients,
			@RequestParam("offset") int offset) {
		LOG.info("Fetching failed email messaged for company id {}", recipients);
		Pageable topTen = new PageRequest(offset, NUMBER_OF_RECORDS);
		List<FailedEmailMessage> failedEmailMessages = failedMessagesService
				.getFailedEmailMessagesByRecipients(recipients, topTen);
		return new ResponseEntity<>(failedEmailMessages, HttpStatus.OK);

	}
	
	
	 @ApiOperation ( value = "Queues failed social posts to social monitor.", response = Void.class)
	    @ApiResponses ( value = { @ApiResponse ( code = 201, message = "Successfully queued failed social posts."),
	        @ApiResponse ( code = 401, message = "You are not authorized to view the resource"),
	        @ApiResponse ( code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
	        @ApiResponse ( code = 503, message = "Service not available") })
	    @RequestMapping ( value = "/failed/socialposts", method = RequestMethod.GET)
	    public ResponseEntity<?> queueReportGenerationRequest(  )
	        throws InterruptedException, ExecutionException, TimeoutException
	    {
	        LOG.info( "Received request to generate reports in stream" );
	        failedSocialPostService.queueFailedSocialPosts();
	        return new ResponseEntity<>( HttpStatus.CREATED );
	    }
		    
 
}
