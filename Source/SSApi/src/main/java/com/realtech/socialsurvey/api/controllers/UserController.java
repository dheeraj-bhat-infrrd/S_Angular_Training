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

import com.realtech.socialsurvey.api.models.request.LoginRequest;
import com.realtech.socialsurvey.api.models.request.UserProfileRequest;
import com.realtech.socialsurvey.api.models.response.AuthResponse;
import com.realtech.socialsurvey.api.models.response.UserProfileResponse;
import com.realtech.socialsurvey.api.transformers.UserProfileTransformer;
import com.realtech.socialsurvey.api.validators.LoginValidator;
import com.realtech.socialsurvey.api.validators.UserProfileValidator;
import com.realtech.socialsurvey.core.entities.api.UserProfile;
import com.realtech.socialsurvey.core.services.api.UserService;
import com.wordnik.swagger.annotations.ApiOperation;


@RestController
@RequestMapping ( "/users")
public class UserController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( UserController.class );
    private RestOperations restTemplate;
    private LoginValidator loginValidator;
    private UserProfileValidator userProfileValidator;
    private UserProfileTransformer userProfileTransformer;
    private UserService userService;

    @Value ( "http://localhost:8082")
    private String authUrl;

    @Value ( "socialsurvey")
    private String clientId;

    @Value ( "secret")
    private String clientSecret;


    @Autowired
    public UserController( RestOperations restTemplate, LoginValidator loginValidator,
        UserProfileValidator userProfileValidator, UserProfileTransformer userProfileTransformer, UserService userService )
    {
        this.restTemplate = restTemplate;
        this.loginValidator = loginValidator;
        this.userProfileValidator = userProfileValidator;
        this.userProfileTransformer = userProfileTransformer;
        this.userService = userService;
    }


    @InitBinder ( "loginRequest")
    public void signUpLoginBinder( WebDataBinder binder )
    {
        binder.setValidator( loginValidator );
    }


    @InitBinder ( "userProfileRequest")
    public void signUp2Binder( WebDataBinder binder )
    {
        binder.setValidator( userProfileValidator );
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
        try {
            LOGGER.info( "UserController.updateStage started" );
            userService.updateStage( Integer.parseInt( userId ), stage );
            LOGGER.info( "UserController.updateStage completed successfully" );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while updating stage: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/profile/update/{userId}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update user profile")
    public ResponseEntity<?> updateUserProfile( @PathVariable ( "userId") String userId,
        @Valid @RequestBody UserProfileRequest userProfileRequest )
    {
        try {
            LOGGER.info( "UserController.updateUserProfile started" );
            UserProfile userProfile = userProfileTransformer.transformApiRequestToDomainObject( userProfileRequest );
            userService.updateUserProfile( Integer.parseInt( userId ), userProfile );
            LOGGER.info( "UserController.updateUserProfile completed successfully" );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while updating user profile: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/profile/details/{userId}", method = RequestMethod.GET)
    @ApiOperation ( value = "Get user profile")
    public ResponseEntity<?> getUserProfile( @PathVariable ( "userId") String userId )
    {
        try {
            LOGGER.info( "UserController.getUserProfile started" );
            UserProfile userProfile = userService.getUserProfileDetails( Integer.parseInt( userId ) );
            UserProfileResponse userProfileResponse = userProfileTransformer.transformDomainObjectToApiResponse( userProfile );
            LOGGER.info( "UserController.getUserProfile completed successfully" );
            return new ResponseEntity<UserProfileResponse>( userProfileResponse, HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while getting user profile: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/profile/profileimage/remove/{userId}", method = RequestMethod.DELETE)
    @ApiOperation ( value = "Delete user profile image")
    public ResponseEntity<?> deleteUserProfileImage( @PathVariable ( "userId") String userId )
    {
        try {
            LOGGER.info( "UserController.deleteUserProfileImage started" );
            userService.deleteUserProfileImage( Integer.parseInt( userId ) );
            LOGGER.info( "UserController.deleteUserProfileImage completed successfully" );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while deleting user profile image: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/profile/profileimage/update/{userId}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update user profile image")
    public ResponseEntity<?> updateUserProfileImage( @PathVariable ( "userId") String userId,
        @RequestBody UserProfileRequest userProfileRequest )
    {
        try {
            LOGGER.info( "UserController.updateUserProfileImage started" );
            userService.updateUserProfileImage( Integer.parseInt( userId ), userProfileRequest.getProfilePhotoUrl() );
            LOGGER.info( "UserController.updateUserProfileImage completed successfully" );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while updating user profile image: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }
}
