package com.realtech.socialsurvey.api.controllers;

import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.core.entities.*;
import com.realtech.socialsurvey.core.exception.AuthorizationException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.socialmonitor.feed.SocialFeedService;
import com.realtech.socialsurvey.core.vo.BulkWriteErrorVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;


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
    public ResponseEntity<?> saveFeed( @Valid @RequestBody SocialResponseObject<?> socialFeed,
        @RequestHeader ( "authorizationHeader") String authorizationHeader ) throws SSApiException
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            try {
                LOGGER.info( "SocialMonitorController.saveFeed started" );
                SocialResponseObject<?> socialFeedResponse = socialFeedService.saveFeed( socialFeed );
                LOGGER.info( "SocialMonitorController.saveFeed completed successfully" );
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


    @RequestMapping ( value = "/feeds/id/{id}/hash/{hash}/companyId/{companyId}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Updates duplicateCount field matching the given hash of social feed collection")
    public ResponseEntity<?> updateDuplicateCount( @PathVariable ( "hash") int hash,
        @PathVariable ( "companyId") long companyId, @PathVariable("id") String id, HttpServletRequest request,
        @RequestHeader ( "authorizationHeader") String authorizationHeader ) throws SSApiException
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            try {
                LOGGER.info( "SocialMonitorController.updateDuplicateCount started" );
                // updates duplicateCount of social post collection
                long updatedDocs = socialFeedService.updateDuplicateCount( hash, companyId, id );
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
                LOGGER.info( "SocialMonitorController.getLockedTokens completed successfully" );
                return new ResponseEntity<>( lockedTokens, HttpStatus.OK );
            } catch ( InvalidInputException e ) {
                throw new SSApiException( e.getMessage(), e );
            }
        }catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }
        
    }
    

    @RequestMapping ( value = "/company/{companyId}/keywords", method = RequestMethod.POST)
    @ApiOperation ( value = "Delete keywords from the company", response = Keyword.class, responseContainer = "List")
    @ApiResponses ( value = { @ApiResponse ( code = 200, message = "Successfully deleted the keywords") })
    public ResponseEntity<?> deleteKeywordsFromCompany( @PathVariable ( "companyId") long companyId,
        @RequestBody List<String> keywordIds, @RequestHeader ( "authorizationHeader") String authorizationHeader )
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

    
    @RequestMapping ( value = "/companies/{companyId}/trustedSource", method = RequestMethod.POST)
    @ApiOperation ( value = "Add truested source to the company", response = SocialMonitorTrustedSource.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse ( code = 200, message = "Successfully updated trusted source")})
    public ResponseEntity<?> addTrustedSourceToCompany( @PathVariable ( "companyId") long companyId,
                                                   @Valid @RequestParam String trustedSource, @RequestHeader ( "authorizationHeader") String authorizationHeader ) throws SSApiException
	{
		try {
			adminAuthenticationService.validateAuthHeader(authorizationHeader);
			try {
				LOGGER.info("SocialMonitorController.addTrustedSourceToCompany started");
				List<SocialMonitorTrustedSource> trustedSources = organizationManagementService
						.addTrustedSourceToCompany(companyId, trustedSource);
				LOGGER.info("SocialMonitorController.addTrustedSourceToCompany completed successfully");
				return new ResponseEntity<>(trustedSources, HttpStatus.OK);
			} catch (NonFatalException e) {
				throw new SSApiException(e.getMessage(), e.getErrorCode());
			}
		} catch (AuthorizationException authoriztionFailure) {
			return new ResponseEntity<>(AUTH_FAILED, HttpStatus.UNAUTHORIZED);
		}
	}

    @RequestMapping ( value = "/updateSocialMediaToken/collection/{collection}/iden/{iden}/fieldtoupdate/{fieldtoupdate}/value/{value}",
        method = RequestMethod.PUT)
    @ApiOperation ( value = "Updates the given field of socialMediaToken with the value")
    public ResponseEntity<?> updateSocialMediaToken( HttpServletRequest request,
        @RequestHeader ( "authorizationHeader") String authorizationHeader, @PathVariable("collection") String collection,
        @PathVariable("iden") long iden, @PathVariable("fieldtoupdate") String fieldToUpdate, @PathVariable("value") boolean value)
        throws SSApiException
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            LOGGER.info( "SocialMonitorController.updateSocialMediaToken started" );
            boolean updateStatus = organizationManagementService.updateSocialMediaToken(collection, iden, fieldToUpdate, value  );
            LOGGER.info( "SocialMonitorController.updateSocialMediaToken completed successfully" );
            return new ResponseEntity<>( updateStatus, HttpStatus.OK );
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }
        catch ( InvalidInputException e ) {
            throw new SSApiException( e.getMessage(), e );
        }

    }

    @RequestMapping ( value = "/socialFeed/companyId/{companyId}", method = RequestMethod.GET)
    @ApiOperation( "Method to fetch socialfeed data based on given keyword from mongo" )
    public ResponseEntity<?> getSocialFeedData( HttpServletRequest request, @RequestHeader("authorizationHeader") String authorizationHeader,
        @PathVariable("companyId") long companyId, @RequestParam( "keyword" ) String keyword,
        @RequestParam( "startTime" ) long startTime, @RequestParam( "endTime" ) long endTime, @RequestParam( "pageSize" ) int pageSize,
        @RequestParam( "skips" ) int skips) throws SSApiException
    {
        try{
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            LOGGER.info( "SocialMonitor controller.getSocialFeedData for keyword started" );
            List<SocialResponseObject> response = socialFeedService.getSocialFeed(keyword, companyId, startTime, endTime, pageSize, skips);
            return new ResponseEntity<>( response, HttpStatus.OK );
        } catch ( InvalidInputException e ){
            throw new SSApiException( e.getMessage(), e );
        } catch ( AuthorizationException e ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }
    }

    @RequestMapping ( value = "/socialFeedData/companyId/{companyId}", method = RequestMethod.GET)
    @ApiOperation( "Method to fetch socialfeed data within a particular date range from mongo" )
    public ResponseEntity<?> getSocialFeedData( HttpServletRequest request, @RequestHeader("authorizationHeader") String authorizationHeader,
        @PathVariable("companyId") long companyId, @RequestParam( "startTime" ) long startTime, @RequestParam( "endTime" ) long endTime, @RequestParam( "pageSize" ) int pageSize,
        @RequestParam( "skips" ) int skips) throws SSApiException
    {
        try{
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            LOGGER.info( "SocialMonitor controller.getSocialFeedData started" );
            List<SocialResponseObject> response = socialFeedService.getSocialFeed( companyId, startTime, endTime, pageSize, skips);
            return new ResponseEntity<>( response, HttpStatus.OK );
        } catch ( InvalidInputException e ){
            throw new SSApiException( e.getMessage(), e );
        } catch ( AuthorizationException e ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }
    }

    @RequestMapping ( value = "/companies/{companyId}/trustedSource/remove", method = RequestMethod.POST)
    @ApiOperation ( value = "Remove trusted source to the company", response = SocialMonitorTrustedSource.class, responseContainer = "List")
    @ApiResponses(value = { @ApiResponse ( code = 200, message = "Successfully updated trusted source")})
    public ResponseEntity<?> removeTrustedSourceToCompany( @PathVariable ( "companyId") long companyId,
                                                   @Valid @RequestParam String trustedSource, @RequestHeader ( "authorizationHeader") String authorizationHeader ) throws SSApiException
    {
        try {
            adminAuthenticationService.validateAuthHeader(authorizationHeader);
            try {
                LOGGER.info("SocialMonitorController.removeTrustedSourceToCompany started");
                List<SocialMonitorTrustedSource> trustedSources = organizationManagementService
                        .removeTrustedSourceToCompany(companyId, trustedSource);
                LOGGER.info("SocialMonitorController.removeTrustedSourceToCompany completed successfully");
                return new ResponseEntity<>(trustedSources, HttpStatus.OK);
            } catch (NonFatalException e) {
                throw new SSApiException(e.getMessage(), e.getErrorCode());
            }
        } catch (AuthorizationException authoriztionFailure) {
            return new ResponseEntity<>(AUTH_FAILED, HttpStatus.UNAUTHORIZED);
        }
    }

    //@RequestMapping ( value = "/feeds/id/{id}/hash/{hash}/companyId/{companyId}", method = RequestMethod.PUT)
    @RequestMapping ( value = "/feeds/saveAndUpdate", method = RequestMethod.POST)
    @ApiOperation ( value = "saves post and updates duplicateCount field matching the given hash of social feed collection")
    public ResponseEntity<?> savePostAndUpdateDuplicateCount(  @Valid @RequestBody SocialResponseObject<?> socialFeed,
        @RequestHeader ( "authorizationHeader") String authorizationHeader ) throws SSApiException
    {
        long updatedDocs= 0 ;
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            try {
                LOGGER.info( "SocialMonitorController.savePostAndUpdateDuplicateCount started" );
                socialFeedService.saveFeed( socialFeed );

                // updates duplicateCount of social post collection
                /*if(socialFeed.getHash() != 0 ){
                    updatedDocs  = socialFeedService.updateDuplicateCount( socialFeed.getHash(), socialFeed.getCompanyId(), socialFeed.getId() );
                }*/
                LOGGER.info( "SocialMonitorController.savePostAndUpdateDuplicateCount completed successfully" );
                return new ResponseEntity<>( updatedDocs, HttpStatus.OK );
            } catch ( DuplicateKeyException duplicateKeyException ) {
                throw new SSApiException( duplicateKeyException.getMessage(), "11000" );
            } catch ( InvalidInputException e ) {
                throw new SSApiException( e.getMessage(), e );
            }
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }

    }


    @RequestMapping ( value = "/feeds/bulk", method = RequestMethod.POST)
    @ApiOperation ( value = "saves post in bulk and updates duplicateCount field matching the given hash of social feed collection")
    public ResponseEntity<?> saveFeeds(  @Valid @RequestBody List<SocialResponseObject<?>> socialFeeds,
        @RequestHeader ( "authorizationHeader") String authorizationHeader ) throws SSApiException
    {
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            try {
                LOGGER.info( "SocialMonitorController.saveFeeds started" );
                //logic to bulk insert into mongo
                List<BulkWriteErrorVO> bulkWriteErrors = socialFeedService.saveFeeds( socialFeeds );
                // updates duplicateCount of social post collection
                socialFeedService.updateDuplicateCount( socialFeeds );

                LOGGER.info( "SocialMonitorController.savePostAndUpdateDuplicateCount completed successfully" );
                return new ResponseEntity<>( bulkWriteErrors, HttpStatus.OK );
            }  catch ( InvalidInputException e ) {
                throw new SSApiException( e.getMessage(), e );
            }
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        }

    }


}
