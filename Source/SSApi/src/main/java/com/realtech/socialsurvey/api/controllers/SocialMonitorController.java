package com.realtech.socialsurvey.api.controllers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.core.entities.FilterKeywordsResponse;
import com.realtech.socialsurvey.core.entities.Keyword;
import com.realtech.socialsurvey.core.entities.MultiplePhrasesVO;
import com.realtech.socialsurvey.core.entities.SocialMediaTokenResponse;
import com.realtech.socialsurvey.core.entities.SocialMediaTokensPaginated;
import com.realtech.socialsurvey.core.entities.SocialResponseObject;
import com.realtech.socialsurvey.core.exception.AuthorizationException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.socialmonitor.feed.SocialFeedService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * @author manish
 *
 */
@RestController
@RequestMapping ( "/v1")
@Api ( value = "Social monitor APIs", description = "APIs for Social Monitor Feeds")
public class SocialMonitorController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( SocialMonitorController.class );
    private static final String AUTH_FAILED = "AUTHORIZATION FAILED";

    private OrganizationManagementService organizationManagementService;

    private SocialFeedService socialFeedService;
    
    @Autowired
    AdminAuthenticationService adminAuthenticationService;

    @Autowired
    public void setOrganizationManagementService( OrganizationManagementService organizationManagementService,
        SocialFeedService socialFeedService )
    {
        this.organizationManagementService = organizationManagementService;
        this.socialFeedService = socialFeedService;
    }


    @RequestMapping ( value = "/companies/{companyId}/keywords", method = RequestMethod.POST)
    @ApiOperation ( value = "Add keywords to the company", response = Keyword.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse ( code = 200, message = "Successfully updated the keywords")})
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
        @PathVariable ( "keywordId") long keywordId, @Valid @RequestBody List<Keyword> keywordsRequest,
        @RequestHeader ( "authorizationHeader") String authorizationHeader ) throws SSApiException
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
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
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }

    }


    @RequestMapping ( value = "/companies/{companyId}/keywords/{keywordId}/disable", method = RequestMethod.POST)
    @ApiOperation ( value = "Initiate account registration")
    public ResponseEntity<?> disabledKeywordsByIdForCompany( @PathVariable ( "companyId") long companyId,
        @PathVariable ( "keywordId") long keywordId, @Valid @RequestBody List<Keyword> keywordsRequest,
        @RequestHeader ( "authorizationHeader") String authorizationHeader ) throws SSApiException
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
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
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }

    }


    @RequestMapping ( value = "/companies/{companyId}/keywords", method = RequestMethod.GET)
    @ApiOperation ( value = "Get keywords of a company", response = FilterKeywordsResponse.class)
    @ApiResponses ( value = { @ApiResponse ( code = 200, message = "Successfully fetched the keywords of a company") })
    public ResponseEntity<?> getCompanyKeywords( @PathVariable ( "companyId") long companyId, int startIndex, int limit,
        @RequestParam ( value = "monitorType", required = false) String monitorType,
        @RequestParam ( value = "searchPhrase", required = false) String searchPhrase,
        @RequestHeader ( "authorizationHeader") String authorizationHeader ) throws SSApiException
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            try {
                LOGGER.info( "SocialMonitorController.getCompanyKeywords started" );
                // get company setting for login user
                FilterKeywordsResponse filterKeywordsResponse = organizationManagementService
                    .getCompanyKeywordsByCompanyId( companyId, startIndex, limit, monitorType, searchPhrase );
                LOGGER.info( "SocialMonitorController.getCompanyKeywords completed successfully" );
                return new ResponseEntity<>( filterKeywordsResponse, HttpStatus.OK );
            } catch ( NonFatalException e ) {
                throw new SSApiException( e.getMessage(), e.getErrorCode() );
            }
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }
    }


    @RequestMapping ( value = "/feeds", method = RequestMethod.POST)
    @ApiOperation ( value = "Save socialpost into mongo")
    public ResponseEntity<?> saveFeeds( @Valid @RequestBody SocialResponseObject<?> socialFeed,
        @RequestHeader ( "authorizationHeader") String authorizationHeader ) throws SSApiException
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            try {
                LOGGER.info( "SocialMonitorController.saveFeeds started" );
                SocialResponseObject<?> socialFeedResponse = socialFeedService.saveFeed( socialFeed );
                LOGGER.info( "SocialMonitorController.saveFeeds completed successfully" );
                return new ResponseEntity<>( socialFeedResponse, HttpStatus.OK );
            } catch ( DuplicateKeyException duplicateKeyException ) {
                throw new SSApiException( duplicateKeyException.getMessage(), "11000" );
            } catch ( NonFatalException e ) {
                throw new SSApiException( e.getMessage(), e );
            }
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }

    }


    @RequestMapping ( value = "/companies/mediatokens", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch media tokens")
    public ResponseEntity<?> fetchSocialMediaTokens( HttpServletRequest request,
        @RequestHeader ( "authorizationHeader") String authorizationHeader ) throws SSApiException, InvalidInputException
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            LOGGER.info( "SocialMonitorController.fetchSocialMediaTokens started" );
            // get company setting for login user
            List<SocialMediaTokenResponse> mediaTokens = organizationManagementService.fetchSocialMediaTokensResponse( 0, 0 );
            LOGGER.info( "SocialMonitorController.fetchSocialMediaTokens completed successfully" );
            return new ResponseEntity<>( mediaTokens, HttpStatus.OK );
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }


    }
    
    
    @RequestMapping ( value = "/companies/mediaTokensPaginated", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch media tokens paginated")
    public ResponseEntity<?> fetchSocialMediaTokensPaginated( HttpServletRequest request,
        @RequestParam ( value = "skipCount", required = false) int skipCount,
        @RequestParam ( value = "batchSize", required = false) int batchSize,
        @RequestHeader ( "authorizationHeader") String authorizationHeader ) throws SSApiException, InvalidInputException
    {
        // get company setting for login user
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            SocialMediaTokensPaginated mediaTokens = organizationManagementService.fetchSocialMediaTokensPaginated( skipCount,
                batchSize );
            return new ResponseEntity<>( mediaTokens, HttpStatus.OK );
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }

    }


    @RequestMapping ( value = "/feeds/hash/{hash}/companyId/{companyId}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Updates duplicateCount field matching the given hash of social feed collection")
    public ResponseEntity<?> updateDuplicateCount( @PathVariable ( "hash") int hash,
        @PathVariable ( "companyId") long companyId, HttpServletRequest request,
        @RequestHeader ( "authorizationHeader") String authorizationHeader ) throws SSApiException
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            try {
                LOGGER.info( "SocialMonitorController.updateDuplicateCount started" );
                // updates duplicateCount of social post collection
                long updatedDocs = socialFeedService.updateDuplicateCount( hash, companyId );
                LOGGER.info( "SocialMonitorController.updateDuplicateCount completed successfully" );
                return new ResponseEntity<>( updatedDocs, HttpStatus.OK );
            } catch ( InvalidInputException e ) {
                throw new SSApiException( e.getMessage(), e );
            }
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }

    }
        
    @RequestMapping ( value = "/token/locks", method = RequestMethod.GET)
    @ApiOperation ( value = "Updates duplicateCount field matching the given hash of social feed collection")
    public ResponseEntity<?> getLockedTokens( @RequestParam ( value = "lockType", required = false) String lockType,
        HttpServletRequest request, @RequestHeader ( "authorizationHeader") String authorizationHeader ) throws SSApiException
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            try {
                LOGGER.info( "SocialMonitorController.getLockedTokens started" );

                Map<String, Long> lockedTokens = organizationManagementService.getFacebookAndTwitterLocks( lockType );
                LOGGER.info( "SocialMonitorController.updateDuplicateCount completed successfully" );
                return new ResponseEntity<>( lockedTokens, HttpStatus.OK );
            } catch ( InvalidInputException e ) {
                throw new SSApiException( e.getMessage(), e );
            }
        }catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }
        
    }
    

    @RequestMapping ( value = "/company/{companyId}keywords", method = RequestMethod.DELETE)
    @ApiOperation ( value = "Delete keywords from the company", response = Keyword.class, responseContainer = "List")
    @ApiResponses ( value = { @ApiResponse ( code = 200, message = "Successfully deleted the keywords") })
    public ResponseEntity<?> deleteKeywordsFromCompany( @PathVariable ( "companyId") long companyId,
        @RequestParam List<String> keywordIds, @RequestHeader ( "authorizationHeader") String authorizationHeader )
        throws SSApiException
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            try {
                LOGGER.info( "SocialMonitorController.deleteKeywordsFromCompany started" );
                List<Keyword> filterKeywords = organizationManagementService.deleteKeywordsFromCompany( companyId, keywordIds );
                LOGGER.info( "SocialMonitorController.deleteKeywordsFromCompany completed successfully" );
                return new ResponseEntity<>( filterKeywords, HttpStatus.OK );
            } catch ( NonFatalException e ) {
                throw new SSApiException( e.getMessage(), e.getErrorCode() );
            }
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }
    }
    

    @RequestMapping ( value = "/company/{companyId}/keyword", method = RequestMethod.POST)
    @ApiOperation ( value = "Add keyword to the company", response = Keyword.class, responseContainer = "List")
    @ApiResponses ( value = { @ApiResponse ( code = 200, message = "Successfully updated the keywords") })
    public ResponseEntity<?> addKeywordToCompany( @PathVariable ( "companyId") long companyId,
        @Valid @RequestBody Keyword keywordsRequest, @RequestHeader ( "authorizationHeader") String authorizationHeader )
        throws SSApiException
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            try {
                LOGGER.info( "SocialMonitorController.addKeywordToCompany started" );
                List<Keyword> filterKeywords = organizationManagementService.addKeywordToCompanySettings( companyId,
                    keywordsRequest );
                LOGGER.info( "SocialMonitorController.addKeywordToCompany completed successfully" );
                return new ResponseEntity<>( filterKeywords, HttpStatus.OK );
            } catch ( NonFatalException e ) {
                throw new SSApiException( e.getMessage(), e.getErrorCode() );
            }
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }

    }
    
    @RequestMapping ( value = "/company/{companyId}/keyword/phrases", method = RequestMethod.POST)
    @ApiOperation ( value = "Add keyword to the company with multiple phrases", response = Keyword.class, responseContainer = "List")
    @ApiResponses ( value = { @ApiResponse ( code = 200, message = "Successfully updated the keywords") })
    public ResponseEntity<?> addMultiplePhrasesToCompany( @PathVariable ( "companyId") long companyId,
        @Valid @RequestBody MultiplePhrasesVO multiplePhrasesVO, @RequestHeader ( "authorizationHeader") String authorizationHeader )
        throws SSApiException
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            try {
                LOGGER.info( "SocialMonitorController.addMultiplePhrasesToCompany started" );
                List<Keyword> filterKeywords = organizationManagementService.addMultiplePhrasesToCompany( companyId, multiplePhrasesVO );
                LOGGER.info( "SocialMonitorController.addMultiplePhrasesToCompany completed successfully" );
                return new ResponseEntity<>( filterKeywords, HttpStatus.OK );
            } catch ( NonFatalException e ) {
                throw new SSApiException( e.getMessage(), e.getErrorCode() );
            }
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }

    }

}
