package com.realtech.socialsurvey.api.controllers;

import java.util.List;

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
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.FeedIngestionEntity;
import com.realtech.socialsurvey.core.entities.Keyword;
import com.realtech.socialsurvey.core.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.core.entities.SocialResponseObject;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.socialmonitor.feed.SocialFeedService;
import com.wordnik.swagger.annotations.ApiOperation;


/**
 * @author manish
 *
 */
@RestController
@RequestMapping ( "/v1")
public class SocialMonitorController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( SocialMonitorController.class );

    private OrganizationManagementService organizationManagementService;

    private SocialFeedService socialFeedService;


    @Autowired
    public void setOrganizationManagementService( OrganizationManagementService organizationManagementService,
        SocialFeedService socialFeedService )
    {
        this.organizationManagementService = organizationManagementService;
        this.socialFeedService = socialFeedService;
    }


    @RequestMapping ( value = "/companies/{companyId}/keywords", method = RequestMethod.POST)
    @ApiOperation ( value = "Initiate account registration")
    public ResponseEntity<?> addKeywordsToCompany( @PathVariable ( "companyId") long companyId,
        @Valid @RequestBody List<Keyword> keywordsRequest ) throws SSApiException
    {
        try {
            LOGGER.info( "SocialMonitorController.addKeywordsToCompany started" );
            List<Keyword> filterKeywords = organizationManagementService.addKeyworodsToCompanySettings( companyId,
                keywordsRequest );
            LOGGER.info( "SocialMonitorController.addKeywordsToCompany completed successfully" );
            return new ResponseEntity<>( filterKeywords, HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    @RequestMapping ( value = "/companies/{companyId}/keywords/{keywordId}/enable", method = RequestMethod.POST)
    @ApiOperation ( value = "Initiate account registration")
    public ResponseEntity<?> enableKeywordsByIdForCompany( @PathVariable ( "companyId") long companyId,
        @PathVariable ( "keywordId") long keywordId, @Valid @RequestBody List<Keyword> keywordsRequest ) throws SSApiException
    {
        try {
            LOGGER.info( "SocialMonitorController.enableKeywordsByIdForCompany started" );
            // get company setting for login user
            List<Keyword> filterKeywords = organizationManagementService.addKeyworodsToCompanySettings( companyId,
                keywordsRequest );
            LOGGER.info( "SocialMonitorController.enableKeywordsByIdForCompany completed successfully" );
            return new ResponseEntity<>( filterKeywords, HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    @RequestMapping ( value = "/companies/{companyId}/keywords/{keywordId}/disable", method = RequestMethod.POST)
    @ApiOperation ( value = "Initiate account registration")
    public ResponseEntity<?> disabledKeywordsByIdForCompany( @PathVariable ( "companyId") long companyId,
        @PathVariable ( "keywordId") long keywordId, @Valid @RequestBody List<Keyword> keywordsRequest ) throws SSApiException
    {
        try {
            LOGGER.info( "SocialMonitorController.disabledKeywordsByIdForCompany started" );
            // get company setting for login user
            List<Keyword> filterKeywords = organizationManagementService.addKeyworodsToCompanySettings( companyId,
                keywordsRequest );
            LOGGER.info( "SocialMonitorController.disabledKeywordsByIdForCompany completed successfully" );
            return new ResponseEntity<>( filterKeywords, HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    @RequestMapping ( value = "/companies/{companyId}/keywords", method = RequestMethod.GET)
    @ApiOperation ( value = "Initiate account registration")
    public ResponseEntity<?> getCompanyKeywords( @PathVariable ( "companyId") long companyId, HttpServletRequest request )
        throws SSApiException
    {
        try {
            LOGGER.info( "SocialMonitorController.getCompanyKeywords started" );
            // get company setting for login user
            List<Keyword> filterKeywords = organizationManagementService.getCompanyKeywordsByCompanyId( companyId );
            LOGGER.info( "SocialMonitorController.getCompanyKeywords completed successfully" );
            return new ResponseEntity<>( filterKeywords, HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    @RequestMapping ( value = "/feeds", method = RequestMethod.POST)
    @ApiOperation ( value = "Initiate account registration")
    public ResponseEntity<?> saveFeeds( @Valid @RequestBody SocialResponseObject<?> socialFeed ) throws SSApiException
    {
        try {
            LOGGER.info( "SocialMonitorController.disabledKeywordsByIdForCompany started" );
            SocialResponseObject<?> socialFeedResponse = socialFeedService.saveFeed( socialFeed );
            LOGGER.info( "SocialMonitorController.disabledKeywordsByIdForCompany completed successfully" );
            return new ResponseEntity<>( socialFeedResponse, HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e );
        }
    }

    @RequestMapping ( value = "/companies/mediatokens", method = RequestMethod.GET)
    @ApiOperation ( value = "Initiate account registration")
    public ResponseEntity<?> fetchSocialMediaTokens(HttpServletRequest request ) throws SSApiException, InvalidInputException
    {
        LOGGER.info( "SocialMonitorController.getCompanyKeywords started" );
        // get company setting for login user
        List<SocialMediaTokenResponse> mediaTokens = organizationManagementService.fetchSocialMediaTokensResponse( 0, 0 );
        LOGGER.info( "SocialMonitorController.getCompanyKeywords completed successfully" );
        return new ResponseEntity<>( mediaTokens, HttpStatus.OK );

    }
}
