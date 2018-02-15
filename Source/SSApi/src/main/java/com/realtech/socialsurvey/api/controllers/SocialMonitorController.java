package com.realtech.socialsurvey.api.controllers;

import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.FeedIngestionEntity;
import com.realtech.socialsurvey.core.entities.FilterKeywordsResponse;
import com.realtech.socialsurvey.core.entities.Keyword;
import com.realtech.socialsurvey.core.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.core.entities.SocialResponseObject;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.socialmonitor.feed.SocialFeedService;
import com.wordnik.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


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
    public ResponseEntity<?> getCompanyKeywords( @PathVariable ( "companyId") long companyId, HttpServletRequest request, int startIndex, int limit, @RequestParam(value = "monitorType", required = false) String monitorType)
            throws SSApiException
    {
        try {
            LOGGER.info( "SocialMonitorController.getCompanyKeywords started" );
            // get company setting for login user
            FilterKeywordsResponse filterKeywordsResponse = organizationManagementService.getCompanyKeywordsByCompanyId( companyId, startIndex, limit, monitorType);
            LOGGER.info( "SocialMonitorController.getCompanyKeywords completed successfully" );
            return new ResponseEntity<>( filterKeywordsResponse, HttpStatus.OK );
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

    @RequestMapping ( value = "/feeds/hash/{hash}/companyId/{companyId}", method = RequestMethod.GET)
    @ApiOperation ( value = "Get duplicate social post count")
    public ResponseEntity<?> getDuplicatePostsCount(@PathVariable ( "hash" ) int hash,
                                                    @PathVariable ( "companyId") long companyId,
                                                    HttpServletRequest request ) throws SSApiException {
        try{
            LOGGER.info( "SocialMonitorController.getDuplicatePostsCount started" );
            // get duplicate social posts count
            long duplicates = socialFeedService.getDuplicatePostsCount(hash, companyId );
            LOGGER.info( "SocialMonitorController.getDuplicatePostsCount completed successfully" );
            return new ResponseEntity<>( duplicates, HttpStatus.OK );
        } catch (InvalidInputException e) {
            throw  new SSApiException(e.getMessage(), e);
        }
    }

    @RequestMapping ( value = "/feeds/hash/{hash}/companyId/{companyId}/duplicateCount/{duplicateCount}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Updates duplicateCount field matching the given hash of social feed collection")
    public ResponseEntity<?> updateDuplicateCount(@PathVariable ( "hash" ) int hash,
                                                    @PathVariable ("companyId") long companyId,
                                                    @PathVariable ( "duplicateCount") long duplicateCount,
                                                    HttpServletRequest request ) throws SSApiException {
        try{
            LOGGER.info( "SocialMonitorController.updateDuplicateCount started" );
            // updates duplicateCount of social post collection
            long updatedDocs = socialFeedService.updateDuplicateCount(hash, companyId, duplicateCount );
            LOGGER.info( "SocialMonitorController.updateDuplicateCount completed successfully" );
            return new ResponseEntity<>( updatedDocs, HttpStatus.OK );
        } catch (InvalidInputException e) {
            throw  new SSApiException(e.getMessage(), e);
        }

    }


}
