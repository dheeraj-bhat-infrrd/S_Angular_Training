package com.realtech.socialsurvey.api.controllers;

import com.realtech.socialsurvey.api.exceptions.BadRequestException;
import com.realtech.socialsurvey.api.models.request.LoginRequest;
import com.realtech.socialsurvey.api.models.request.UserProfileRequest;
import com.realtech.socialsurvey.api.models.response.AuthResponse;
import com.realtech.socialsurvey.api.models.response.UserProfileResponse;
import com.realtech.socialsurvey.api.transformers.UserProfileTransformer;
import com.realtech.socialsurvey.api.validators.*;
import com.realtech.socialsurvey.core.entities.api.UserProfile;
import com.realtech.socialsurvey.core.services.api.UserService;
import com.wordnik.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.Base64Utils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestOperations;

import javax.validation.Valid;


@RestController
@RequestMapping ( "/users")
public class UserController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( UserController.class );
    private RestOperations restTemplate;
    private LoginValidator loginValidator;
    private LinkedInConnectValidator linkedInConnectValidator;
    private UserProfilePhase1Validator userProfilePhase1Validator;
    private UserProfilePhase2Validator userProfilePhase2Validator;
    private UserProfileTransformer userProfileTransformer;
    private UserService userService;
    private GlobalControllerExceptionHandler exceptionHandler;
    private UserProfileImageValidator userProfileImageValidator;

    @Value ( "http://localhost:8082")
    private String authUrl;

    @Value ( "socialsurvey")
    private String clientId;

    @Value ( "secret")
    private String clientSecret;


    @Autowired
    public UserController( RestOperations restTemplate, LoginValidator loginValidator,
        UserProfilePhase1Validator userProfilePhase1Validator, UserProfileTransformer userProfileTransformer,
        UserService userService, LinkedInConnectValidator linkedInConnectValidator,
        UserProfilePhase2Validator userProfilePhase2Validator, GlobalControllerExceptionHandler exceptionHandler,
        UserProfileImageValidator userProfileImageValidator )
    {
        this.restTemplate = restTemplate;
        this.loginValidator = loginValidator;
        this.userProfilePhase1Validator = userProfilePhase1Validator;
        this.userProfileTransformer = userProfileTransformer;
        this.userService = userService;
        this.linkedInConnectValidator = linkedInConnectValidator;
        this.userProfilePhase2Validator = userProfilePhase2Validator;
        this.exceptionHandler = exceptionHandler;
        this.userProfileImageValidator = userProfileImageValidator;
    }


    @InitBinder ( "loginRequest")
    public void signUpLoginBinder( WebDataBinder binder )
    {
        binder.setValidator( loginValidator );
    }


    @InitBinder ( "userProfileRequestPhase1")
    public void signUp1Binder( WebDataBinder binder )
    {
        binder.setValidator( userProfilePhase1Validator );
    }


    @InitBinder ( "userProfileRequestPhase2")
    public void signUp2Binder( WebDataBinder binder )
    {
        binder.setValidator( userProfilePhase2Validator );
    }


    @InitBinder ( "userProfileImageRequest")
    public void signUp3Binder( WebDataBinder binder )
    {
        binder.setValidator( userProfileImageValidator );
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


    @RequestMapping ( value = "/profile/social/linkedin/connect/{userId}", method = RequestMethod.POST)
    @ApiOperation ( value = "Connect linkedIn")
    public ResponseEntity<?> connectLinkedIn( @PathVariable ( "userId") String userId,
        @RequestBody UserProfileRequest userProfileRequest, BindingResult errors )
    {
        try {
            linkedInConnectValidator.validate( userProfileRequest, errors );
            UserProfile userProfile = userProfileTransformer.transformApiRequestToDomainObject( userProfileRequest );
            userService.connectLinkedIn( Integer.parseInt( userId ), userProfile );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( BadRequestException ex ) {
            return exceptionHandler.handleBadRequestException( ex );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while connecting to linkedIn: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/profile/phase1/update/{userId}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update user profile phase 1")
    public ResponseEntity<?> updateUserProfilePhase1( @PathVariable ( "userId") String userId,
        @Validated ( value = UserProfilePhase1Validator.class) @RequestBody UserProfileRequest userProfileRequestPhase1,
        BindingResult errors )
    {
        try {
            //userProfilePhase1Validator.validate( userProfileRequest, errors );
            UserProfile userProfile = userProfileTransformer.transformApiRequestToDomainObject( userProfileRequestPhase1 );
            userService.updateUserProfile( Integer.parseInt( userId ), userProfile );
            return new ResponseEntity<Void>( HttpStatus.OK );
        }
        //        catch ( BadRequestException ex ) {
        //            return exceptionHandler.handleBadRequestException( ex );
        //        } 
        catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while updating user profile phase 1: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/profile/phase2/update/{userId}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update user profile phase 2")
    public ResponseEntity<?> updateUserProfilePhase2( @PathVariable ( "userId") String userId,
        @Validated ( value = UserProfilePhase2Validator.class) @RequestBody UserProfileRequest userProfileRequestPhase2,
        BindingResult errors )
    {
        try {
            //userProfilePhase2Validator.validate( userProfileRequest, errors );
            UserProfile userProfile = userProfileTransformer.transformApiRequestToDomainObject( userProfileRequestPhase2 );
            userService.updateUserProfile( Integer.parseInt( userId ), userProfile );
            return new ResponseEntity<Void>( HttpStatus.OK );
        }
        //        catch ( BadRequestException ex ) {
        //            return exceptionHandler.handleBadRequestException( ex );
        //        } 
        catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while updating user profile phase 2: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/profile/details/{userId}", method = RequestMethod.GET)
    @ApiOperation ( value = "Get user profile")
    public ResponseEntity<?> getUserProfile( @PathVariable ( "userId") String userId )
    {
        try {
            UserProfile userProfile = userService.getUserProfileDetails( Integer.parseInt( userId ) );
            UserProfileResponse userProfileResponse = userProfileTransformer.transformDomainObjectToApiResponse( userProfile );
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
            userService.deleteUserProfileImage( Integer.parseInt( userId ) );
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
        @Validated ( value = UserProfileImageValidator.class) @RequestBody UserProfileRequest userProfileImageRequest,
        BindingResult errors )
    {
        try {
            UserProfile userProfile = userProfileTransformer.transformApiRequestToDomainObject( userProfileImageRequest );
            userService.updateUserProfileImage( Integer.parseInt( userId ), userProfile.getProfilePhotoUrl() );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while updating user profile image: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }
}
