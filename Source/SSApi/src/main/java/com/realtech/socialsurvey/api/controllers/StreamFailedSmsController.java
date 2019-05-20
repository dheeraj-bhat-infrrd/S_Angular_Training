package com.realtech.socialsurvey.api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.core.services.stream.StreamMessagesService;
import com.realtech.socialsurvey.core.vo.SmsVO;

@RestController
@RequestMapping("/v1")
public class StreamFailedSmsController {

	private static final Logger LOG = LoggerFactory.getLogger( StreamFailedSmsController.class );
	
	@Autowired
	private StreamMessagesService streamMessagesServiceImpl;
	
	@PostMapping( value = "/failed/stream/sms" )
	public ResponseEntity<Boolean> addFailedStreamSms( @RequestBody SmsVO smsVO ) {
		
		LOG.debug( "StreamFailedSmsController : addFailedStreamSms() started" );
		return new ResponseEntity<>( streamMessagesServiceImpl.saveFailedSmsInTopology( smsVO ), HttpStatus.OK );
	}
}
