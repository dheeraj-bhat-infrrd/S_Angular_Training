package com.realtech.socialsurvey.api.controllers;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
    public void authorizer( HttpServletRequest request, HttpServletResponse response )
    {

        LOGGER.info( "VendastaController.Authorizer started" );

        String nextUrl = request.getParameter( "next" );
        String productId = request.getParameter( "product_id" );
        String ssoToken = request.getParameter( "sso_token" );

        if ( !StringUtils.isEmpty( productId ) && productId.equalsIgnoreCase( "RM" ) && !StringUtils.isEmpty( nextUrl )
            && !StringUtils.isEmpty( ssoToken ) ) {
            try {
                String outputUrl = URLDecoder.decode( nextUrl, "UTF-8" );
                outputUrl += "&sso_token=" + ssoToken + "&sso_ticket=" + String.valueOf( System.currentTimeMillis() );
                response.sendRedirect( outputUrl );

            } catch ( Exception exception ) {
                LOGGER.error( "VendastaController.Authorizer exception: " + exception );
            }
        }
        LOGGER.info( "VendastaController.Authorizer finished" );
    }


    @RequestMapping ( "/validation/*")
    @ApiOperation ( value = "Validate SSO Ticket")
    public ResponseEntity<?> validator( HttpServletRequest request )
    {
        LOGGER.info( "VendastaController.Validator started" );

        String productId = request.getParameter( "product_id" );
        String ssoToken = request.getParameter( "sso_token" );
        String ssoTicket = request.getParameter( "sso_ticket" );

        if ( adminAuthenticationService.validateSSOTuple( productId, ssoToken, ssoTicket ) ) {
            LOGGER.info( "VendastaController.Validator finished" );
            return restUtils.getRestResponseEntity( HttpStatus.OK, "SSO ticket validated", null, null, request );
        } else {
            LOGGER.debug( "VendastaController.Validator failed" );
            return restUtils.getRestResponseEntity( HttpStatus.OK, "Invalid SSO ticket", null, null, request );

        }
    }
}
