package com.realtech.socialsurvey.api.controllers;


import com.google.gson.Gson;
import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.api.utils.RestUtils;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.AgentDisableApiEntity;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.AuthorizationException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping ( "/v1")
public class OrganizationManagementApiController
{

    private static final Logger LOGGER = LoggerFactory.getLogger( OrganizationManagementApiController.class );

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private UserManagementService userManagementService;
    
    @Autowired
    private AdminAuthenticationService adminAuthenticationService;
    
    @Autowired
    private RestUtils restUtils;

    private static final String AUTH_FAILED = "AUTHORIZATION FAILED";

    @RequestMapping ( value = "/updateabusivemail", method = RequestMethod.POST)
    @ApiOperation ( value = "Updating abusive mail settings")
    public String updateAbusiveMail( long entityId, String mailId ) throws InvalidInputException, NonFatalException
    {
        LOGGER.info( "Updating abusive mail settings for companyId : {}", entityId );
        organizationManagementService.updateAbusiveMailService( entityId, mailId );
        return mailId;

    }


    @RequestMapping ( value = "/unsetabusivemail", method = RequestMethod.POST)
    @ApiOperation ( value = "Unset abusive mail settings")
    public void unsetAbusiveMail( long entityId ) throws NonFatalException
    {
        LOGGER.info( "Unset abusive mail settings for companyId : {}", entityId );
        organizationManagementService.unsetAbusiveMailService( entityId );
    }


    @RequestMapping ( value = "/unsetcompres", method = RequestMethod.POST)
    @ApiOperation ( value = "Unset Complaint Resolution settings")
    public void unsetCompRes( long entityId ) throws NonFatalException
    {
        LOGGER.info( "Unset Complaint Resolution settings for companyId : {}", entityId );
        organizationManagementService.unsetComplaintResService( entityId );
    }


    @RequestMapping ( value = "/encompass/{companyId}/version/update", method = RequestMethod.POST)
    @ApiOperation ( value = "update the version of encompass for a company")
    public ResponseEntity<?> updateEncompassVersion( @PathVariable long companyId, String version ) throws NonFatalException
    {
        LOGGER.info( "Updating the encompass version for companyId : {}", companyId );
        try {
            organizationManagementService.updateEncompassVersion( companyId, version );
            return new ResponseEntity<>( HttpStatus.OK );
        } catch ( NonFatalException error ) {
            throw new SSApiException( "Unable to update encompass version, " + error.getMessage(), error );
        }
    }
    
    @RequestMapping ( value = "/unsetwebadd", method = RequestMethod.POST)
    @ApiOperation ( value = "Unset web address in profile")
    public void unsetWebAdd( long entityId ,String entityType) throws  NonFatalException
    {
        LOGGER.info( "Unset web address in profile for entityId : {} , entityType : {}",entityId,entityType );
        organizationManagementService.unsetWebAddressInProfile( entityId, entityType );
    }


    /**
     * 
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/userprofileflags", method = RequestMethod.GET)
    public ResponseEntity<?> getProfileFlags( long userId )
    {
        LOGGER.info( "Method getProfileFlags() started." );
        Map<String, String> response = new HashMap<>();

        response.put( "success", "false" );
        if ( userId > 0 ) {
            try {
                List<UserProfile> profiles = userManagementService.getUserProfiles( userId );
                if ( profiles != null ) {
                    response.put( "success", "true" );
                    response.put( "isBranchAdmin", "false" );
                    response.put( "isRegionAdmin", "false" );
                    response.put( "isAgent", "false" );
                    for ( UserProfile profile : profiles ) {
                        if ( profile.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                            if ( profile.getProfilesMaster()
                                .getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                                response.put( "isAgent", "true" );
                            } else if ( profile.getProfilesMaster()
                                .getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                                response.put( "isBranchAdmin", "true" );
                            } else if ( profile.getProfilesMaster()
                                .getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                                response.put( "isRegionAdmin", "true" );
                            }
                        }
                    }
                } else {
                    response.put( "reason", "No user profiles found" );
                }

            } catch ( InvalidInputException error ) {
                LOGGER.warn( "Unable to parse user ID, ", error );
                response.put( "reason", error.getMessage() );
            }
        } else {
            response.put( "reason", "Invalid user ID" );
        }

        LOGGER.info( "Method getProfileFlags() finished." );
        return new ResponseEntity<>( response, HttpStatus.OK );
    }
    
    @RequestMapping ( value = "/flag/agentprofiles", method = RequestMethod.POST)
    @ApiOperation ( value = "Api to disable or enable agent edit in profile")
    public  ResponseEntity<?> flagAgentProfile( long companyId , boolean isAgentProfileDisabled ,  HttpServletRequest request ) throws  NonFatalException
    {
        LOGGER.info( "Api to disable or enable agent edit in profile started for companyId:{}",companyId );
        String authorizationHeader = request.getHeader( CommonConstants.SURVEY_API_REQUEST_PARAMETER_AUTHORIZATION );
        //authorize request
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            LOGGER.info( "Api to disable or enable agent edit in profile for companyId : {} , isAgentProfileDisabled : {}",companyId,isAgentProfileDisabled );

        } catch ( AuthorizationException e ) {
            return restUtils.getRestResponseEntity( HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null, request, companyId );
        }
        organizationManagementService.updateAgentProfileDisable( companyId, isAgentProfileDisabled );
        return restUtils.getRestResponseEntity( HttpStatus.OK, "Api to disable or enable agent edit in profile Successfully processed", null, null, request,
            companyId );
    }
    
    @RequestMapping ( value = "/flag/agents/profile", method = RequestMethod.POST)
    @ApiOperation ( value = "Api to disable or enable agent edit in profile")
    public  ResponseEntity<?> flagAgentProfiles( @Valid @RequestBody AgentDisableApiEntity agentDisableApiEntity,  HttpServletRequest request ) throws  NonFatalException
    {
        LOGGER.info( "Api to disable or enable agents edit in profile started" );
        long companyId = 0;
        String authorizationHeader = request.getHeader( CommonConstants.SURVEY_API_REQUEST_PARAMETER_AUTHORIZATION );
        //authorize request
        try {
            companyId = adminAuthenticationService.validateAuthHeader( authorizationHeader );
            LOGGER.info( "Api to disable or enable agent edit in profile for agents, isAgentProfileDisabled : {}",agentDisableApiEntity.isAgentProfileDisabled() );

        } catch ( AuthorizationException e ) {
            return restUtils.getRestResponseEntity( HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null, null, request, companyId );
        }
        organizationManagementService.updateAgentsProfileDisable( agentDisableApiEntity.getAgentIds(), agentDisableApiEntity.isAgentProfileDisabled() );
        return restUtils.getRestResponseEntity( HttpStatus.OK, "Api to disable or enable agent edit in profile Successfully processed", null, null, request,
            companyId );
    }
    
    @RequestMapping ( value = "/sethasregisteredforsummit", method = RequestMethod.POST)
    @ApiOperation ( value = "Set hasRegisteredForSummit")
    public ResponseEntity<?> setHasRegisteredForSummit(long companyId, boolean hasRegisteredForSummit) throws SSApiException
    {
        try {
            organizationManagementService.setHasRegisteredForSummit(companyId, hasRegisteredForSummit);
            LOGGER.info( "setHasregisteredforsummit completed successfully" );
            return new ResponseEntity<>( "Successfully updated", HttpStatus.OK );
                
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }
    
    @RequestMapping ( value = "/setshowsummitpopup", method = RequestMethod.POST)
    @ApiOperation ( value = "Set IsShowSummitPopup")
    public ResponseEntity<?> setShowSummitPopup(long companyId, boolean isShowSummitPopup) throws SSApiException
    {
        try {
            organizationManagementService.setShowSummitPopup(companyId, isShowSummitPopup);
            LOGGER.info( "setShowSummitPopup completed successfully" );
            return new ResponseEntity<>( "Successfully updated", HttpStatus.OK );
                
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }
}
