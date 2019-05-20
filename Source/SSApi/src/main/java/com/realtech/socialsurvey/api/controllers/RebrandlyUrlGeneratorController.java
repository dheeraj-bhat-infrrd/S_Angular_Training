package com.realtech.socialsurvey.api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.core.services.generator.RebrandlyUrlGenerator;
import com.realtech.socialsurvey.core.vo.RebrandlyVO;

@RestController
@RequestMapping("/v1")
public class RebrandlyUrlGeneratorController {

	private static final Logger LOG = LoggerFactory.getLogger( RebrandlyUrlGeneratorController.class );
	
	@Autowired
	private RebrandlyUrlGenerator rebrandlyUrlGeneratorImpl;
	
	@GetMapping( value = "/rebrandly/url" )
	public ResponseEntity<RebrandlyVO> getShortenedUrl( @RequestParam( value = "surveyUrl" ) String surveyUrl ) {
		
		LOG.debug( "RebrandlyUrlGeneratorController : getShortenedUrl() started" );
		RebrandlyVO rebrandlyVO = rebrandlyUrlGeneratorImpl.getShortenedUrl( surveyUrl );
		return new ResponseEntity<>( rebrandlyVO, HttpStatus.OK );
	}
}
