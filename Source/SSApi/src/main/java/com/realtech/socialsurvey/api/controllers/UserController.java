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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import com.realtech.socialsurvey.api.models.request.LinkedInConnectRequest;
import com.realtech.socialsurvey.api.models.request.LoginRequest;
import com.realtech.socialsurvey.api.models.response.AuthResponse;
import com.realtech.socialsurvey.api.transformers.LinkedInConnectTransformer;
import com.realtech.socialsurvey.api.validators.LinkedInConnectValidator;
import com.realtech.socialsurvey.api.validators.LoginValidator;
import com.realtech.socialsurvey.core.entities.api.LinkedInConnect;
import com.realtech.socialsurvey.core.services.api.UserService;
import com.wordnik.swagger.annotations.ApiOperation;


@RestController
@RequestMapping ( "/users")
public class UserController
{

    @Value ( "http://localhost:8082")
    private String authUrl;

    @Value ( "socialsurvey")
    private String clientId;

    @Value ( "secret")
    private String clientSecret;

    @Autowired
    private RestOperations restTemplate;

    @Autowired
    private LoginValidator loginValidator;

    @Autowired
    private LinkedInConnectValidator linkedInConnectValidator;

    @Autowired
    private LinkedInConnectTransformer linkedInConnectTransformer;

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger( UserController.class );


    @InitBinder ( "loginRequest")
    public void signUpLoginBinder( WebDataBinder binder )
    {
        binder.setValidator( loginValidator );
    }


    @InitBinder ( "linkedInConnectRequest")
    public void signUpLinkedInConnectBinder( WebDataBinder binder )
    {
        binder.setValidator( linkedInConnectValidator );
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


    @RequestMapping ( value = "/profile/social/linkedin/connect", method = RequestMethod.POST)
    @ApiOperation ( value = "Connect linkedIn")
    public ResponseEntity<?> connectLinkedIn( @Valid @RequestBody LinkedInConnectRequest linkedInConnectRequest )
    {
        try {
            //Transform the request into DO.
            LinkedInConnect linkedInConnect = linkedInConnectTransformer
                .transformApiRequestToDomainObject( linkedInConnectRequest );

            //Call service layer and pass DO to this service to save the data in db.
            userService.connectLinkedIn( linkedInConnect );

            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while connecting to linkedIn: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }
}
