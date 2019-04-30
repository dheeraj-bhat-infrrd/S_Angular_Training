package com.realtech.socialsurvey.api.controllers;

import java.io.IOException;

import javax.validation.Valid;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.AuthorizationException;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestOperations;

import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.core.vo.ManageTeamBulkResponse;
import com.realtech.socialsurvey.core.vo.UserVo;
import com.realtech.socialsurvey.api.models.PersonalProfile;
import com.realtech.socialsurvey.api.models.request.LoginRequest;
import com.realtech.socialsurvey.api.models.response.AuthResponse;
import com.realtech.socialsurvey.core.vo.ManageTeamBulkRequest;
import com.realtech.socialsurvey.api.transformers.PersonalProfileTransformer;
import com.realtech.socialsurvey.api.validators.LoginValidator;
import com.realtech.socialsurvey.api.validators.PersonalProfileValidator;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserCompositeEntity;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.api.UserService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.UrlValidationHelper;
import io.swagger.annotations.ApiOperation;

import java.util.List;


@RestController
@RequestMapping ( "/v1")
public class UserController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( UserController.class );
    private RestOperations restTemplate;
    private LoginValidator loginValidator;
    private PersonalProfileValidator personalProfileValidator;
    private PersonalProfileTransformer personalProfileTransformer;
    private UserService userService;
    private UserManagementService userManagementService;
    private UrlValidationHelper urlValidationHelper;
    private OrganizationManagementService organizationManagementService;
    private AdminAuthenticationService adminAuthenticationService;

    @Value ( "http://localhost:8082")
    private String authUrl;

    @Value ( "socialsurvey")
    private String clientId;

    @Value ( "secret")
    private String clientSecret;


    @Autowired
    public UserController( RestOperations restTemplate, LoginValidator loginValidator,
        PersonalProfileValidator personalProfileValidator, PersonalProfileTransformer personalProfileTransformer,
        UserService userService, UserManagementService userManagementService, UrlValidationHelper urlValidationHelper,
        OrganizationManagementService organizationManagementService, AdminAuthenticationService adminAuthenticationService)
    {
        this.restTemplate = restTemplate;
        this.loginValidator = loginValidator;
        this.personalProfileValidator = personalProfileValidator;
        this.personalProfileTransformer = personalProfileTransformer;
        this.userService = userService;
        this.userManagementService = userManagementService;
        this.urlValidationHelper = urlValidationHelper;
        this.organizationManagementService = organizationManagementService;
        this.adminAuthenticationService = adminAuthenticationService;
    }

    @Autowired
    public Utils utils;


    @InitBinder ( "loginRequest")
    public void signUpLoginBinder( WebDataBinder binder )
    {
        binder.setValidator( loginValidator );
    }


    @InitBinder ( "personalProfile")
    public void signUpPersonalProfileBinder( WebDataBinder binder )
    {
        binder.setValidator( personalProfileValidator );
    }

    private static final String AUTH_FAILED = "AUTHORIZATION FAILED";

    @RequestMapping ( value = "/login", method = RequestMethod.POST)
    @ApiOperation ( value = "User login")
    public ResponseEntity<?> login( @Valid @RequestBody LoginRequest loginRequest )
    {
        String endPoint = authUrl + "/oauth/token";
        String clientCredential = clientId + ":" + clientSecret;
        String authData = Base64Utils.encodeToString( clientCredential.getBytes() );
        String data = String.format( "grant_type=password&username=%s&password=%s", loginRequest.getEmail(),
            loginRequest.getPassword() );
        HttpHeaders headers = new HttpHeaders();
        headers.add( HttpHeaders.AUTHORIZATION, "Basic " + authData );
        headers.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        HttpEntity<String> httpEntity = new HttpEntity<String>( data, headers );
        try {
            ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity( endPoint, httpEntity, AuthResponse.class );

            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Response from Auth Service is: " + authResponse );
            }
            return authResponse;
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while login: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.UNAUTHORIZED );
        }
    }


    @RequestMapping ( value = "/users/{userId}/stage/{stage}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update stage")
    public ResponseEntity<?> updateStage( @PathVariable ( "userId") String userId, @PathVariable ( "stage") String stage )
    {
        LOGGER.info( "UserController.updateStage started" );
        userService.updateStage( Long.parseLong( userId ), stage );
        LOGGER.info( "UserController.updateStage completed successfully" );
        return new ResponseEntity<Void>( HttpStatus.OK );
    }


    @RequestMapping ( value = "/users/{userId}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update user profile")
    public ResponseEntity<?> updateUserProfile( @Valid @RequestBody PersonalProfile personalProfile,
        @PathVariable ( "userId") String userId ) throws SSApiException
    {
        try {
            LOGGER.info( "UserController.updateUserProfile started" );
            long userIdLong = Long.parseLong( userId );
            User user = userManagementService.getUserByUserId( userIdLong );
            AgentSettings agentSettings = userManagementService.getAgentSettingsForUserProfiles( userIdLong );
            UserCompositeEntity userProfile = personalProfileTransformer.transformApiRequestToDomainObject( personalProfile,
                user, agentSettings );
            userService.updateUserProfile( userIdLong, userProfile );
            LOGGER.info( "UserController.updateUserProfile completed successfully" );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    @RequestMapping ( value = "/users/{userId}", method = RequestMethod.GET)
    @ApiOperation ( value = "Get user profile")
    public ResponseEntity<?> getUserProfile( @PathVariable ( "userId") String userId ) throws SSApiException
    {
        try {
            LOGGER.info( "UserController.getUserProfile started" );
            UserCompositeEntity userProfile = userService.getUserProfileDetails( Long.parseLong( userId ) );
            PersonalProfile userProfileResponse = personalProfileTransformer.transformDomainObjectToApiResponse( userProfile );
            LOGGER.info( "UserController.getUserProfile completed successfully" );
            return new ResponseEntity<PersonalProfile>( userProfileResponse, HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    @RequestMapping ( value = "/users/{userId}/profileimage", method = RequestMethod.DELETE)
    @ApiOperation ( value = "Delete user profile image")
    public ResponseEntity<?> deleteUserProfileImage( @PathVariable ( "userId") String userId ) throws SSApiException
    {
        try {
            LOGGER.info( "UserController.deleteUserProfileImage started" );
            userService.deleteUserProfileImage( Long.parseLong( userId ) );
            LOGGER.info( "UserController.deleteUserProfileImage completed successfully" );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    @RequestMapping ( value = "/users/{userId}/profileimage", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update user profile image")
    public ResponseEntity<?> updateUserProfileImage( @PathVariable ( "userId") String userId, @RequestBody String imageUrl )
        throws SSApiException
    {
        try {
            LOGGER.info( "UserController.updateUserProfileImage started" );
            userService.updateUserProfileImage( Long.parseLong( userId ), imageUrl );
            LOGGER.info( "UserController.updateUserProfileImage completed successfully" );
            return new ResponseEntity<String>( imageUrl, HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    @RequestMapping ( value = "/users/{userId}/stage", method = RequestMethod.GET)
    @ApiOperation ( value = "Get user profile stage")
    public ResponseEntity<?> getUserStage( @PathVariable ( "userId") String userId ) throws SSApiException
    {
        try {
            User user = userManagementService.getUserByUserId( Long.parseLong( userId ) );
            return new ResponseEntity<String>( user.getRegistrationStage(), HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    @RequestMapping ( value = "/users/{userId}/password", method = RequestMethod.PUT)
    @ApiOperation ( value = "Save password")
    public ResponseEntity<?> savePassword( @PathVariable ( "userId") String userId, @RequestBody String password )
        throws InvalidInputException
    {
        userService.savePassword( Long.parseLong( userId ), password );
        return new ResponseEntity<Void>( HttpStatus.OK );
    }


    @RequestMapping ( value = "/webaddress", method = RequestMethod.POST)
    @ApiOperation ( value = "Validates the provided web address")
    public ResponseEntity<?> validateWebAddress( @RequestBody String webAddress ) throws SSApiException
    {
        try {
            LOGGER.info( "Validating web address " + webAddress );
            try {
                urlValidationHelper.validateUrl( webAddress );
            } catch ( IOException e ) {
                LOGGER.error( "Error reaching " + webAddress, e );
                throw new InvalidInputException( "Web address passed was invalid", DisplayMessageConstants.GENERAL_ERROR, e );
            }
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    /**
     * Api for re-inviting unverified users in bulk. This method takes care of the already
     * deleted users and the verified users. Calls {@link UserManagementService#reInviteUsers(List)}
     * @param emailIds
     * @param authorizationHeader
     * @return
     */
    @PostMapping ( value = "/users/reinvite" )
    @ApiOperation( "Resends inviatation email for unverified users" )
    public ResponseEntity<?> sendInvitationsForRegistration( @Valid @RequestBody List<String> emailIds,
        @RequestHeader ( "authorizationHeader") String authorizationHeader )
    {
        LOGGER.info( "Sending invitation to users" );

        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );

            final ManageTeamBulkResponse response = userManagementService.reInviteUsers( emailIds );

            return new ResponseEntity<>( response, HttpStatus.ACCEPTED );

        } catch ( AuthorizationException ex ){
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        } catch ( NonFatalException e ) {
            LOGGER.error( "NonFatalException while reinviting user. Reason : " + e.getMessage(), e );
            return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST );
        }
    }


    /**
     * Api for removing existing users in bulk. Handle the scenarios where the user
     * is deleted or the deleted user is an admin. Calls {@link UserManagementService#removeExistingUser(User, long, int)}
     * @param manageTeamBulkRequest
     * @param authorizationHeader
     * @return
     */
    @PostMapping ( value = "/users/delete" )
    @ApiOperation( value = "Deletes the existing users")
    public ResponseEntity<?> removeExistingUser( @Valid @RequestBody ManageTeamBulkRequest manageTeamBulkRequest,
        @RequestHeader ( "authorizationHeader") String authorizationHeader){
        LOGGER.info( "Deactivating existing users" );
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );

            ManageTeamBulkResponse response = userManagementService.removeExistingUsers(manageTeamBulkRequest.getUserIds(),
                manageTeamBulkRequest.getAdminId());

            return new ResponseEntity<>( response, HttpStatus.ACCEPTED );

        } catch ( AuthorizationException ex ){
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        } catch ( InvalidInputException e ) {
            return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST );
        }
    }


    /**
     * Api for removing users in bulk using the emailIds. Handles scenarios where user is
     * deleted or the user to be deleted is admin. Calls {@link UserManagementService#removeExistingUsersByEmail(List, String)}
     * @param manageTeamBulkRequest
     * @param authorizationHeader
     * @return
     */
    @PostMapping ( value = "/removeexistingusersbyemail" )
    @ApiOperation( value = "Deletes the existing users by email")
    public ResponseEntity<?> removeExistingUserByEmail( @Valid @RequestBody ManageTeamBulkRequest manageTeamBulkRequest,
        @RequestHeader ( "authorizationHeader") String authorizationHeader){
        LOGGER.info( "Deactivating existing users by emailIds" );
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );

            ManageTeamBulkResponse response = userManagementService.removeExistingUsersByEmail(manageTeamBulkRequest.getUserEmailIds(),
                manageTeamBulkRequest.getAdminEmailId());

            return new ResponseEntity<>( response, HttpStatus.ACCEPTED );

        } catch ( AuthorizationException ex ){
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST );
        }
    }


    /**
     * Api for assigning multiple users to a branch.
     * Calls {@link OrganizationManagementService#assignBranchToUsers(List, long, long)}
     * @param manageTeamBulkRequest
     * @param authorizationHeader
     * @return
     */
    @PostMapping ( value = "/users/assigntobranch" )
    @ApiOperation( value = "Assigns the given users to the given branch")
    public ResponseEntity<?> assignUsersToBranch( @Valid @RequestBody ManageTeamBulkRequest manageTeamBulkRequest,
        @RequestHeader ( "authorizationHeader") String authorizationHeader){
        LOGGER.info( "Assigning users {} to branch {}", manageTeamBulkRequest.getUserIds(), manageTeamBulkRequest.getBranchId() );
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );

            ManageTeamBulkResponse response = organizationManagementService.assignBranchToUsers(manageTeamBulkRequest.getUserIds(),
                manageTeamBulkRequest.getAdminId(), manageTeamBulkRequest.getBranchId());

            return new ResponseEntity<>( response, HttpStatus.ACCEPTED );

        } catch ( AuthorizationException ex ){
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Api for assigning multiple users to a given region.
     * Calls {@link OrganizationManagementService#assignRegionToUsers(List, long, long)}
     * @param manageTeamBulkRequest
     * @param authorizationHeader
     * @return
     */
    @PostMapping ( value = "/users/assigntoregion" )
    @ApiOperation( value = "Assigns the given users to the given branch")
    public ResponseEntity<?> assignUsersToRegion( @Valid @RequestBody ManageTeamBulkRequest manageTeamBulkRequest,
        @RequestHeader ( "authorizationHeader") String authorizationHeader){
        LOGGER.info( "Assigning users {} to region {}", manageTeamBulkRequest.getUserIds(), manageTeamBulkRequest.getRegionId() );
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );

            ManageTeamBulkResponse response = organizationManagementService.assignRegionToUsers(manageTeamBulkRequest.getUserIds(),
                manageTeamBulkRequest.getAdminId(), manageTeamBulkRequest.getRegionId());

            return new ResponseEntity<>( response, HttpStatus.ACCEPTED );

        } catch ( AuthorizationException ex ){
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST );
        }
    }


    /**
     * Api for bulk assiging given list of userIds as social monitor admin.
     * Calls {@link UserManagementService#assignUsersAsSocialMonitorAdmin(List, long)}
     * @param manageTeamBulkRequest
     * @param authorizationHeader
     * @return
     */
    @PostMapping ( value = "/users/assignassocialmonitoradmin" )
    @ApiOperation( value = "Assigns the given users to the given branch")
    public ResponseEntity<?> assignUsersAsSocialMonitorAdmin( @Valid @RequestBody ManageTeamBulkRequest manageTeamBulkRequest,
        @RequestHeader ( "authorizationHeader") String authorizationHeader){
        LOGGER.info( "Assigning users {} to branch {}", manageTeamBulkRequest.getUserIds(), manageTeamBulkRequest.getBranchId() );
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );

            ManageTeamBulkResponse response = userManagementService.assignUsersAsSocialMonitorAdmin(manageTeamBulkRequest.getUserIds(),
                manageTeamBulkRequest.getAdminId());

            return new ResponseEntity<>( response, HttpStatus.ACCEPTED );

        } catch ( AuthorizationException ex ){
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        } catch ( InvalidInputException  e ) {
            return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST );
        }
    }

    @RequestMapping( value = "/user/{id}", method = RequestMethod.GET)
    @ApiOperation( value = "Gets the user details")
    public ResponseEntity<?> getPrimaryUserProfileByAgentId(@PathVariable long id, @RequestHeader ( "authorizationHeader") String authorizationHeader)
        throws SSApiException
    {
        LOGGER.info( " Method to fetch primary user profile with agent id {} started ", id );
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            return new ResponseEntity<>( userManagementService.getPrimaryUserProfileByAgentId(id), HttpStatus.OK);
        } catch ( AuthorizationException e ) {
            return new ResponseEntity<>( "AUTHORIZATION FAILED", HttpStatus.UNAUTHORIZED );
        } catch ( ProfileNotFoundException | InvalidInputException e ) {
            throw new SSApiException( e.getMessage() );
        }
    }

    @RequestMapping( value = "users/{comanyId}/owner")
    @ApiOperation(value = "get the adminId based on companyId")
    public ResponseEntity<Long> getOwnerForCompany(@PathVariable("comanyId") Long companyId) throws SSApiException {
        try {
            return new ResponseEntity<>(userService.getOwnerByCompanyId(companyId), HttpStatus.OK);
        } catch (NonFatalException e) {
            throw new SSApiException(e.getMessage());
        }
    }

    /**
     * Api for bulk updating the auto-post-score for the given list of agentIds.
     * Calls {@link UserManagementService#bulkUpdateSocialPostScoreForAgents(List, double)}
     * @param manageTeamBulkRequest
     * @param authorizationHeader
     * @return
     */
    @PostMapping(value = "/users/autopostscore")
    @ApiOperation(value = "update auto post score for the given agentIds") 
    public ResponseEntity<Object> bulkUpdateSocialPostScoreForAgents(@RequestBody ManageTeamBulkRequest manageTeamBulkRequest, 
        @RequestHeader ( "authorizationHeader") String authorizationHeader) throws SSApiException
    {
        LOGGER.info( "Method to update minimum social post score started for {} with the minimumSocialPostScore {}",
            manageTeamBulkRequest.getUserIds(), manageTeamBulkRequest.getMinimumSocialPostScore() );
        Object object;
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );

            ManageTeamBulkResponse response = userManagementService.bulkUpdateSocialPostScoreForAgents( manageTeamBulkRequest.getUserIds(), 
                manageTeamBulkRequest.getMinimumSocialPostScore() );
            object = response;
            return new ResponseEntity<>( object, HttpStatus.ACCEPTED);
        }catch  ( AuthorizationException exception  ){
            object = AUTH_FAILED;
            return new ResponseEntity<>( object, HttpStatus.UNAUTHORIZED );
        }catch ( InvalidInputException e ) {
            object = e.getMessage();
            return new ResponseEntity<>( object, HttpStatus.BAD_REQUEST );
        }catch ( DatabaseException exception ) {
            throw new SSApiException( exception.getMessage() );
        }
    }

    /**
     * Api for bulk uploading a profile picture to the given agents
     * Calls {@link UserManagementService#uploadProfilePicToAgents(ManageTeamBulkRequest)}
     * @param authorizationHeader
     * @return
     */
    @PostMapping (value = "/users/uploadprofilepic")
    @ApiOperation (value = "Assigns the given users to the given branch")
    public ResponseEntity<Object> bulkUploadProfilePicToAgents(@RequestBody ManageTeamBulkRequest manageTeamBulkRequest, 
        @RequestHeader ("authorizationHeader") String authorizationHeader )
            throws SSApiException
    {
        LOGGER.info( "Assigning profile picture {} to users {} ", manageTeamBulkRequest.getImageFileName(),
            manageTeamBulkRequest.getUserIds() );
        Object object;
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            ManageTeamBulkResponse response = userManagementService.uploadProfilePicToAgents( manageTeamBulkRequest);
            object = response;
            return new ResponseEntity<>( object, HttpStatus.ACCEPTED);
        } catch ( AuthorizationException e ) {
            object = AUTH_FAILED;
            return new ResponseEntity<>( object, HttpStatus.UNAUTHORIZED );
        } catch ( InvalidInputException e ) {
            object = e.getMessage();
            return new ResponseEntity<>( object, HttpStatus.NOT_ACCEPTABLE );
        } catch ( NonFatalException | DatabaseException exception) {
            throw new SSApiException( "Uploding profile picture to the agents failed {}",exception );
        }
    }
    
    /**
     * Api for getting all the active users for the given hierarchy
     * Calls {@link UserManagementService#getActiveUsersInHierarchy(String, long, long)}
     * @param companyId
     * @param entityType
     * @param adminId
     * @param authorizationHeader
     * @return
     */
    @GetMapping (value = "/users/active")
    @ApiOperation (value = "Get all the active users for the given hierarchy")
    public ResponseEntity<?> getAllActiveUsersInHierarchy(@RequestParam("companyId") long companyId,
        @RequestParam("entityType") String entityType, @RequestParam("adminId") long adminId,
        @RequestHeader ("authorizationHeader") String authorizationHeader) throws SSApiException
    {
        LOGGER.info( "Fetch all the active users for entityTpe {} ", entityType);
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            List<UserVo> userVo= userManagementService.getActiveUsersInHierarchy( entityType, adminId, companyId );

            return new ResponseEntity<>( userVo,HttpStatus.ACCEPTED);
        }catch ( AuthorizationException e ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        } catch ( InvalidInputException e ) {
            throw new SSApiException( "Exception while fetching active users for given hierarchy ", e );
        }
    }

    /**
     * Api for getting all the active users for the given hierarchy
     * Calls {@link UserManagementService#getUnverifiedUsersInHierarchy(long, String, long)}
     * @param companyId
     * @param entityType
     * @param adminId
     * @param authorizationHeader
     * @return
     */
    @GetMapping (value = "/users/unverified")
    @ApiOperation (value = "Get all the unverified users for the given hierarchy")
    public ResponseEntity<?> getAllUnverifiedUsersInHierarchy(@RequestParam("companyId") long companyId,
        @RequestParam("entityType") String entityType, @RequestParam("adminId") long adminId,
        @RequestHeader ("authorizationHeader") String authorizationHeader) throws SSApiException
    {
        LOGGER.info( "Fetch all the active users for the company {} ", companyId);
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            List<UserVo> userVo= userManagementService.getUnverifiedUsersInHierarchy(companyId, entityType, adminId );
            return new ResponseEntity<>( userVo,HttpStatus.ACCEPTED);
        }catch ( AuthorizationException e ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        } catch ( InvalidInputException e ) {
            throw new SSApiException( "Exception while fetching unverified users for given hierarchy ", e );
        }
    }
    
    /**
     * Api for getting all the verified users for the given hierarchy
     * Calls {@link UserManagementService#getVerifiedUsersInHierarchy(long, String, long)}
     * @param companyId
     * @param entityType
     * @param adminId
     * @param authorizationHeader
     * @return
     */
    @GetMapping ( value = "/users/verified" )
    @ApiOperation ( value = "Get All the verified users for the given unverified")
    public ResponseEntity<?> getAllVerifiedUsersInHierarchy(@RequestParam("companyId") long companyId,
        @RequestParam("entityType") String entityType, @RequestParam("adminId") long adminId,
        @RequestHeader ("authorizationHeader") String authorizationHeader ) throws SSApiException
    {
        LOGGER.info( "Fetch all verified users for compnay {} ", companyId );
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            List<UserVo> userVo = userManagementService.getVerifiedUsersInHierarchy(companyId, entityType, adminId );
            return new ResponseEntity<>( userVo, HttpStatus.ACCEPTED );
        } catch (AuthorizationException e) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        } catch ( InvalidInputException e ) {
            throw new SSApiException( "Exception while fetching verified users for given hierarchy ", e );
        }
    }

    /**
     * Api for getting all the active users in a company for the given admin of hierarchy matching the given name pattern
     * Calls {@link UserManagementService#getActiveUsersInHierarchy(String, long, long)}
     * @param companyId
     * @param pattern
     * @param adminId
     * @param authorizationHeader
     * @return
     */
    @GetMapping (value = "/users/active/{namePattern}")
    @ApiOperation (value = "Get all the active users for the given hierarchy")
    public ResponseEntity<?> getAllActiveUsersInHierarchy(@PathVariable("namePattern") String pattern,
        @RequestParam("companyId") long companyId, @RequestParam("adminId") long adminId,
        @RequestParam("sort") String sortOrder, @RequestParam("entityType") String entityType,
        @RequestHeader ("authorizationHeader") String authorizationHeader)
        throws SSApiException
    {
        LOGGER.info( "Fetch all the active users with name pattern like {} ", pattern);
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            List<UserVo> userVo= userManagementService.getActiveUsersInHierarchy( pattern, adminId, companyId,
                CommonConstants.ACTIVE, sortOrder, entityType );

            return new ResponseEntity<>( userVo,HttpStatus.ACCEPTED);
        }catch ( AuthorizationException e ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        } catch ( InvalidInputException e ) {
           return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST );
        } catch ( SolrException e ) {
            throw new SSApiException( "Exception while fetching active users for given pattern {}. Reason : {}", pattern ,e );
        }
    }

    /**
     * Api for getting all the verified users in a company for the given hierarchy matching the given name pattern
     * Calls {@link UserManagementService#getActiveUsersInHierarchy(String, long, long)}
     * @param companyId
     * @param pattern
     * @param adminId
     * @param authorizationHeader
     * @return
     */
    @GetMapping (value = "/users/verified/{namePattern}")
    @ApiOperation (value = "Get all the active users for the given hierarchy")
    public ResponseEntity<?> getAllVerifiedUsersInHierarchy(@PathVariable("namePattern") String pattern,
        @RequestParam("companyId") long companyId, @RequestParam("adminId") long adminId,
        @RequestParam( "sort" ) String sortOrder, @RequestParam("entityType") String entityType,
        @RequestHeader ("authorizationHeader") String authorizationHeader)
        throws SSApiException
    {
        LOGGER.info( "Fetch all the active users with name pattern like {} ", pattern);
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            List<UserVo> activeUsersInHierarchy = userManagementService
                .getActiveUsersInHierarchy( pattern, adminId, companyId, CommonConstants.VERIFIED, sortOrder, entityType );
            List<UserVo> userVo= activeUsersInHierarchy;

            return new ResponseEntity<>( userVo,HttpStatus.ACCEPTED);
        }catch ( AuthorizationException e ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        } catch ( InvalidInputException e ) {
            return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST );
        } catch ( SolrException e ) {
            throw new SSApiException( "Exception while fetching active users for given pattern {}. Reason : {}", pattern ,e );
        }
    }

    /**
     * Api for getting all the unverified users in a company for the given hierarchy matching the given name pattern
     * Calls {@link UserManagementService#getActiveUsersInHierarchy(String, long, long)}
     * @param companyId
     * @param pattern
     * @param adminId
     * @param authorizationHeader
     * @return
     */
    @GetMapping (value = "/users/unverified/{namePattern}")
    @ApiOperation (value = "Get all the active users for the given hierarchy")
    public ResponseEntity<?> getAllUnverifiedUsersInHierarchy(@PathVariable("namePattern") String pattern,
        @RequestParam("companyId") long companyId, @RequestParam("adminId") long adminId,
        @RequestParam("sort") String sortOrder, @RequestParam("entityType") String entityType,
        @RequestHeader ("authorizationHeader") String authorizationHeader)
        throws SSApiException
    {
        LOGGER.info( "Fetch all the active users with name pattern like {} ", pattern);
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
            List<UserVo> activeUsersInHierarchy = userManagementService
                .getActiveUsersInHierarchy( pattern, adminId, companyId, CommonConstants.UN_VERIFIED, sortOrder, entityType );
            List<UserVo> userVo= activeUsersInHierarchy;

            return new ResponseEntity<>( userVo,HttpStatus.ACCEPTED);
        }catch ( AuthorizationException e ) {
            return new ResponseEntity<>( AUTH_FAILED, HttpStatus.UNAUTHORIZED );
        } catch ( InvalidInputException e ) {
            return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST );
        } catch ( SolrException e ) {
            throw new SSApiException( "Exception while fetching active users for given pattern {}. Reason : {}", pattern ,e );
        }
    }
}

