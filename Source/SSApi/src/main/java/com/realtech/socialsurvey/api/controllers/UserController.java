package com.realtech.socialsurvey.api.controllers;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import com.realtech.socialsurvey.api.models.PersonalProfile;
import com.realtech.socialsurvey.api.models.request.LoginRequest;
import com.realtech.socialsurvey.api.models.response.AuthResponse;
import com.realtech.socialsurvey.api.transformers.PersonalProfileTransformer;
import com.realtech.socialsurvey.api.validators.LoginValidator;
import com.realtech.socialsurvey.api.validators.PersonalProfileValidator;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserCompositeEntity;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.api.UserService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.wordnik.swagger.annotations.ApiOperation;


@RestController
@RequestMapping ( "/users")
public class UserController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( UserController.class );
    private RestOperations restTemplate;
    private LoginValidator loginValidator;
    private PersonalProfileValidator personalProfileValidator;
    private PersonalProfileTransformer personalProfileTransformer;
    private UserService userService;
    private UserManagementService userManagementService;

    @Value ( "http://localhost:8082")
    private String authUrl;

    @Value ( "socialsurvey")
    private String clientId;

    @Value ( "secret")
    private String clientSecret;


    @Autowired
    public UserController( RestOperations restTemplate, LoginValidator loginValidator,
        PersonalProfileValidator personalProfileValidator, PersonalProfileTransformer personalProfileTransformer,
        UserService userService, UserManagementService userManagementService )
    {
        this.restTemplate = restTemplate;
        this.loginValidator = loginValidator;
        this.personalProfileValidator = personalProfileValidator;
        this.personalProfileTransformer = personalProfileTransformer;
        this.userService = userService;
        this.userManagementService = userManagementService;
    }


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
        headers.add( "Authorization", "Basic " + authData );
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


    @RequestMapping ( value = "/profile/stage/update/{userId}/{stage}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update stage")
    public ResponseEntity<?> updateStage( @PathVariable ( "userId") String userId, @PathVariable ( "stage") String stage )
    {
        LOGGER.info( "UserController.updateStage started" );
        userService.updateStage( Long.parseLong( userId ), stage );
        LOGGER.info( "UserController.updateStage completed successfully" );
        return new ResponseEntity<Void>( HttpStatus.OK );
    }


    @RequestMapping ( value = "/profile/update/{userId}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update user profile")
    public ResponseEntity<?> updateUserProfile( @Valid @RequestBody PersonalProfile personalProfile,
        @PathVariable ( "userId") String userId ) throws InvalidInputException, SolrException
    {
        LOGGER.info( "UserController.updateUserProfile started" );
        long userIdLong = Long.parseLong( userId );
        User user = userManagementService.getUserByUserId( userIdLong );
        AgentSettings agentSettings = userManagementService.getAgentSettingsForUserProfiles( userIdLong );
        UserCompositeEntity userProfile = personalProfileTransformer.transformApiRequestToDomainObject( personalProfile, user,
            agentSettings );
        userService.updateUserProfile( userIdLong, userProfile );
        LOGGER.info( "UserController.updateUserProfile completed successfully" );
        return new ResponseEntity<Void>( HttpStatus.OK );
    }


    @RequestMapping ( value = "/profile/details/{userId}", method = RequestMethod.GET)
    @ApiOperation ( value = "Get user profile")
    public ResponseEntity<?> getUserProfile( @PathVariable ( "userId") String userId ) throws InvalidInputException
    {
        LOGGER.info( "UserController.getUserProfile started" );
        UserCompositeEntity userProfile = userService.getUserProfileDetails( Long.parseLong( userId ) );
        PersonalProfile userProfileResponse = personalProfileTransformer.transformDomainObjectToApiResponse( userProfile );
        LOGGER.info( "UserController.getUserProfile completed successfully" );
        return new ResponseEntity<PersonalProfile>( userProfileResponse, HttpStatus.OK );
    }


    @RequestMapping ( value = "/profile/profileimage/remove/{userId}", method = RequestMethod.DELETE)
    @ApiOperation ( value = "Delete user profile image")
    public ResponseEntity<?> deleteUserProfileImage( @PathVariable ( "userId") String userId ) throws InvalidInputException
    {
        LOGGER.info( "UserController.deleteUserProfileImage started" );
        userService.deleteUserProfileImage( Long.parseLong( userId ) );
        LOGGER.info( "UserController.deleteUserProfileImage completed successfully" );
        return new ResponseEntity<Void>( HttpStatus.OK );
    }


    @RequestMapping ( value = "/profile/profileimage/update/{userId}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update user profile image")
    public ResponseEntity<?> updateUserProfileImage( @PathVariable ( "userId") String userId,
        @RequestBody PersonalProfile personalProfile ) throws InvalidInputException
    {
        LOGGER.info( "UserController.updateUserProfileImage started" );
        userService.updateUserProfileImage( Long.parseLong( userId ), personalProfile.getProfilePhotoUrl() );
        LOGGER.info( "UserController.updateUserProfileImage completed successfully" );
        return new ResponseEntity<Void>( HttpStatus.OK );
    }
}
