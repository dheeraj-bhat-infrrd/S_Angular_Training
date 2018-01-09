package com.realtech.socialsurvey.stream.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.stream.entities.DummyFeed;
import com.realtech.socialsurvey.stream.services.DummyFeedService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@RestController
@RequestMapping ( "api/v1/dummy")
@Api ( value = "Stream APIs", description = "Dummy APIs for testing")
public class DummyApiController
{

    private static final Logger LOG = LoggerFactory.getLogger( DummyApiController.class );

    private DummyFeedService dummyFeedService;


    @Autowired
    public void setDummyFeedService( DummyFeedService dummyFeedService )
    {
        this.dummyFeedService = dummyFeedService;
    }


    @ApiOperation ( value = "Adds a post/tweet etc.", response = Void.class)
    @ApiResponses ( value = { @ApiResponse ( code = 201, message = "Successfully saved the feed"),
        @ApiResponse ( code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse ( code = 403, message = "Accessing the resource you were trying to reach is forbidden") })
    @RequestMapping ( value = "/feed", method = RequestMethod.POST)
    public ResponseEntity<?> postDummyFeed( @RequestBody final String feed )
    {
        LOG.info( "Inserting a feed" );
        DummyFeed dummyFeed = new DummyFeed();
        dummyFeed.setFeed( feed );
        return new ResponseEntity<DummyFeed>( dummyFeedService.insertDummyFeed( dummyFeed ), HttpStatus.CREATED );
    }
}
