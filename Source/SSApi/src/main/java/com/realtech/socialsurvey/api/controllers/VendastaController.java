package com.realtech.socialsurvey.api.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.api.utils.RestUtils;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.wordnik.swagger.annotations.ApiOperation;


/**
 * @author yoganand
 *
 */
@RestController
@RequestMapping ( "/sso/")
public class VendastaController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( VendastaController.class );

    private RestUtils restUtils;

    private AdminAuthenticationService adminAuthenticationService;


    @Autowired
    public VendastaController( RestUtils restUtils, AdminAuthenticationService adminAuthenticationService )
    {
        this.restUtils = restUtils;
        this.adminAuthenticationService = adminAuthenticationService;
    }


    @RequestMapping ( "/authorization/*")
    @ApiOperation ( value = "Authorize Vendasta SSO Request")
    public ResponseEntity<?> Authorizer( HttpServletRequest request )
    {

        LOGGER.info( "VendastaController.Authorizer started" );

        String nextUrl = request.getParameter( "next" );
        String productId = request.getParameter( "product_id" );
        String ssoToken = request.getParameter( "sso_token" );

        if ( productId == "RM" && nextUrl != null && nextUrl.isEmpty() && ssoToken == "RM" ) {
            try {
                String outputUrl = URLDecoder.decode( nextUrl, "UTF-8" );
                outputUrl += "&sso_token=" + ssoToken + "&sso_ticket=" + String.valueOf( System.currentTimeMillis() );
                HttpClient client = HttpClientBuilder.create().build();
                client.execute( new HttpGet( outputUrl ) );
            } catch ( UnsupportedEncodingException exception ) {
                return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Invalid Passed parameter/s ", null, null,
                    request );
            } catch ( IOException exception ) {
                return restUtils.getRestResponseEntity( HttpStatus.INTERNAL_SERVER_ERROR, "Passed parameter count is invalid",
                    null, null, request );
            }
        }
        LOGGER.info( "VendastaController.Authorizer finished" );
        return restUtils.getRestResponseEntity( HttpStatus.CREATED, "SSO ticket generated", null, null, request );
    }


    @RequestMapping ( "/validation/*")
    @ApiOperation ( value = "Validate SSO Ticket")
    public ResponseEntity<?> Validator( HttpServletRequest request )
    {
        LOGGER.info( "VendastaController.Validator started" );

        String productId = request.getParameter( "product_id" );
        String ssoToken = request.getParameter( "sso_token" );
        String ssoTicket = request.getParameter( "sso_ticket" );

        if ( adminAuthenticationService.validateSSOTuple( productId, ssoToken, ssoTicket ) ) {
            LOGGER.info( "VendastaController.Validator finished" );
            return restUtils.getRestResponseEntity( HttpStatus.CREATED, "SSO ticket validated", null, null, request );
        } else {
            LOGGER.debug( "VendastaController.Validator failed" );
            return restUtils.getRestResponseEntity( HttpStatus.CREATED, "Invalid SSO ticket", null, null, request );

        }
    }
}
