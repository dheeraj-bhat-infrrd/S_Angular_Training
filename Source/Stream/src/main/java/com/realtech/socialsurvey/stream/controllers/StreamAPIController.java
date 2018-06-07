package com.realtech.socialsurvey.stream.controllers;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.stream.entities.TransactionIngestionMessage;
import com.realtech.socialsurvey.stream.entities.ReportRequest;
import com.realtech.socialsurvey.stream.messages.EmailMessage;
import com.realtech.socialsurvey.stream.messages.SendgridEvent;
import com.realtech.socialsurvey.stream.messages.UserEvent;
import com.realtech.socialsurvey.stream.services.AuthenticationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@RestController
@RequestMapping ( "api/v1/stream")
@Api ( value = "Stream APIs", description = "APIs for Social Survey streams")
public class StreamAPIController
{
    private static final Logger LOG = LoggerFactory.getLogger( StreamAPIController.class );

    private KafkaTemplate<String, String> kafkaEmailTemplate;

    private KafkaTemplate<String, String> kafkaEmailEventsTemplate;

    private KafkaTemplate<String, String> kafkaStringTemplate;

    private KafkaTemplate<String, String> kafkaReportGenerationTemplate;

    private KafkaTemplate<String, String> kafkaBatchProcessingTemplate;

    private KafkaTemplate<String, String> kafkaUserEventTemplate;

    private AuthenticationService authenticationService;
    
    private KafkaTemplate<String, String> kafkaTransactionIngestionTemplate;
    
    @Value ( "${kafka.topic.solrTopic}")
    private String solrTopic;


    @Autowired
    @Qualifier ( "genericMessageTemplate")
    public void setKafkaTemplate( KafkaTemplate<String, String> kafkaStringTemplate )
    {
        this.kafkaStringTemplate = kafkaStringTemplate;
    }


    @Autowired
    @Qualifier ( "emailMessageTemplate")
    public void setKafkaEmailTemplate( KafkaTemplate<String, String> kafkaEmailTemplate )
    {
        this.kafkaEmailTemplate = kafkaEmailTemplate;
    }

    
    @Autowired
    @Qualifier ( "emailEventsTemplate")
    public void setKafkaEmailEventsTemplate( KafkaTemplate<String, String> kafkaEmailEventsTemplate )
    {
        this.kafkaEmailEventsTemplate = kafkaEmailEventsTemplate;
    }


    @Autowired
    @Qualifier ( "reportTemplate")
    public void setKafkaReportGenerationTemplate( KafkaTemplate<String, String> kafkaReportGenerationTemplate )
    {
        this.kafkaReportGenerationTemplate = kafkaReportGenerationTemplate;
    }


    @Autowired
    @Qualifier ( "userEventTemplate")
    public void setKafkaUserEventTemplate( KafkaTemplate<String, String> kafkaUserEventTemplate )
    {
        this.kafkaUserEventTemplate = kafkaUserEventTemplate;
    }


    @Autowired
    @Qualifier ( "batchTemplate")
    public void setKafkaBatchProcessingTemplate( KafkaTemplate<String, String> kafkaBatchProcessingTemplate )
    {
        this.kafkaBatchProcessingTemplate = kafkaBatchProcessingTemplate;
    }
    
    
    @Autowired
    public void setAuthenticationService( AuthenticationService authenticationService )
    {
        this.authenticationService = authenticationService;
    }

    
    @Autowired
    @Qualifier ( "transactionIngestionTemplate")
    public void setKafkaTransactionIngestionTemplate(KafkaTemplate<String, String> kafkaTransactionIngestionTemplate)
    {
        this.kafkaTransactionIngestionTemplate = kafkaTransactionIngestionTemplate;
    }


    @ApiOperation ( value = "Queues an email message.", response = Void.class)
    @ApiResponses ( value = { @ApiResponse ( code = 201, message = "Successfully queued the message"),
        @ApiResponse ( code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse ( code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse ( code = 503, message = "Service not available") })
    @RequestMapping ( value = "/mail", method = RequestMethod.POST)
    public ResponseEntity<?> queueEmailMessage( @RequestBody final EmailMessage emailMessage )
        throws InterruptedException, ExecutionException, TimeoutException
    {
        LOG.trace( "Queueing email message: {}", emailMessage );
        kafkaEmailTemplate.send( new GenericMessage<>( emailMessage ) ).get( 60, TimeUnit.SECONDS );
        return new ResponseEntity<>( HttpStatus.CREATED );
    }


    @ApiOperation ( value = "Indicates to refresh SOLR for Social Survey.", response = Void.class)
    @ApiResponses ( value = { @ApiResponse ( code = 201, message = "Acknowledged the message"),
        @ApiResponse ( code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse ( code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse ( code = 503, message = "Service not available") })
    @RequestMapping ( value = "/solr/refresh", method = RequestMethod.GET)
    public ResponseEntity<?> queueSOLRRefresh() throws InterruptedException, ExecutionException, TimeoutException
    {
        LOG.debug( "Queueing for SOLR refresh" );
        kafkaStringTemplate.send( solrTopic, "refresh" ).get( 60, TimeUnit.SECONDS );
        return new ResponseEntity<>( HttpStatus.CREATED );
    }


    @ApiOperation ( value = "Recieves and queues sendgrid events.", response = Void.class)
    @ApiResponses ( value = { @ApiResponse ( code = 201, message = "Acknowledged the message"),
        @ApiResponse ( code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse ( code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse ( code = 503, message = "Service not available") })
    @RequestMapping ( value = "/sendgrid/events", method = RequestMethod.POST)
    public ResponseEntity<?> receiveSendGridEvents( @RequestBody List<SendgridEvent> events )
        throws InterruptedException, ExecutionException, TimeoutException
    {
        LOG.info( "Received events" );
        LOG.trace( "Events {}", events );
        for ( SendgridEvent event : events ) {
            kafkaEmailEventsTemplate.send( new GenericMessage<>( event ) ).get( 60, TimeUnit.SECONDS );
        }
        return new ResponseEntity<>( HttpStatus.OK );
    }


    @ApiOperation ( value = "Generates email reports request.", response = Void.class)
    @ApiResponses ( value = { @ApiResponse ( code = 201, message = "Report generation requested."),
        @ApiResponse ( code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse ( code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse ( code = 503, message = "Service not available") })
    @RequestMapping ( value = "/report", method = RequestMethod.POST)
    public ResponseEntity<?> queueReportGenerationRequest( @RequestBody ReportRequest reportRequest )
        throws InterruptedException, ExecutionException, TimeoutException
    {
        LOG.info( "Received request to generate reports in stream" );
        LOG.debug( "Report request {}", reportRequest );
        kafkaReportGenerationTemplate.send( new GenericMessage<>( reportRequest ) ).get( 60, TimeUnit.SECONDS );
        return new ResponseEntity<>( HttpStatus.CREATED );
    }


    @ApiOperation ( value = "Processes batch for survey invitation email report.", response = Void.class)
    @ApiResponses ( value = { @ApiResponse ( code = 201, message = "Report generation requested."),
        @ApiResponse ( code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse ( code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse ( code = 503, message = "Service not available") })
    @RequestMapping ( value = "/batch", method = RequestMethod.POST)
    public ResponseEntity<?> queueBatchProcessingRequest( @RequestBody ReportRequest reportRequest )
        throws InterruptedException, ExecutionException, TimeoutException
    {
        LOG.info( "Received request to process batch." );
        LOG.debug( "Report request {}", reportRequest );
        kafkaBatchProcessingTemplate.send( new GenericMessage<>( reportRequest ) ).get( 60, TimeUnit.SECONDS );
        return new ResponseEntity<>( HttpStatus.CREATED );
    }
    
    
    @ApiOperation ( value = "Recieves and queues user events originated from the browser.", response = Void.class)
    @ApiResponses ( value = { @ApiResponse ( code = 201, message = "Acknowledged the message"),
        @ApiResponse ( code = 401, message = "You are not authorized to access the resource"),
        @ApiResponse ( code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse ( code = 503, message = "Service not available") })
    @RequestMapping ( value = "/userevent", method = RequestMethod.POST)
    public ResponseEntity<?> receiveUserEvent( @RequestBody UserEvent userEvent, @RequestHeader( value = "Authorization" ) String apiAccessKey  )
        throws InterruptedException, ExecutionException, TimeoutException
    {
        LOG.info( "Received user event" );
        if( !authenticationService.isApiAccessKeyValid( apiAccessKey ) ) {
            LOG.warn( "Invalid api access key : {}", apiAccessKey );
            return new ResponseEntity<>( HttpStatus.UNAUTHORIZED );
        }
        LOG.trace( "Event : {}", userEvent );
        kafkaUserEventTemplate.send( new GenericMessage<>( userEvent ) ).get( 60, TimeUnit.SECONDS );
        return new ResponseEntity<>( HttpStatus.CREATED );
    }
    
    
    @ApiOperation ( value = "Upload surveys transaction request.", response = Void.class)
    @ApiResponses ( value = { @ApiResponse ( code = 201, message = "Upload surveys transaction requested."),
        @ApiResponse ( code = 401, message = "You are not authorized to access the resource"),
        @ApiResponse ( code = 403, message = "Accessing the resource, you were trying to reach is forbidden"),
        @ApiResponse ( code = 503, message = "Service not available") })
    @RequestMapping ( value = "/transaction/ingestion", method = RequestMethod.POST)
    public ResponseEntity<?> queueTransactionIngestionRequest( @RequestBody TransactionIngestionMessage transactionIngestionMessage )
        throws InterruptedException, ExecutionException, TimeoutException
    {
        LOG.info( "Received request to upload surveys transaction request in stream" );
        LOG.debug( "Transaction request {}", transactionIngestionMessage );
        kafkaTransactionIngestionTemplate.send( new GenericMessage<>( transactionIngestionMessage ) ).get( 60, TimeUnit.SECONDS );
        return new ResponseEntity<>( HttpStatus.OK );
    }

}
