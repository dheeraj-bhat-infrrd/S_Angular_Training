package com.realtech.socialsurvey.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.VendastaManagementService;


/**
 * URL handlers for SingleSignOn process into Vendasta Accounts
 *
 */

@Controller
@RequestMapping ( value = "/sso")
public class VendastaLoginController
{
    private static final Logger LOG = LoggerFactory.getLogger( VendastaLoginController.class );

    @Autowired
    private VendastaManagementService vendastaManagementSerivce;

    @Autowired
    private SessionHelper sessionHelper;


    // method to authorize the user to access vendasta by creating sso ticket with an accountId and ssotoken
    @RequestMapping ( value = "/authorization")
    public void authorizer( HttpServletRequest request, HttpServletResponse response )
    {

        LOG.info( "VendastaLoginController.Authorizer started" );
        try {
            if ( request.getParameterMap().size() == 0 ) {
                LOG.error( "VendastaLoginController.Authorizer sso process failure" );
                response.sendRedirect( "/vendastaError.do" );
                return;
            }
            HttpSession session = request.getSession( false );
            User user = sessionHelper.getCurrentUser();
            if ( session != null ) {
                boolean vendastaAccessible = (boolean) session.getAttribute( CommonConstants.VENDASTA_ACCESS );
                if ( vendastaAccessible ) {
                    String nextUrl = request.getParameter( "next" );
                    String productId = request.getParameter( "product_id" );
                    if ( nextUrl != null && productId != null && !nextUrl.isEmpty() && !productId.isEmpty() ) {

                        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
                        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );

                        try {
                            String ssoToken = vendastaManagementSerivce.fetchSSOTokenForReputationManagementAccount( entityType,
                                entityId, productId );
                            String validateUrl = vendastaManagementSerivce.validateUrlGenerator( user, nextUrl, productId,
                                ssoToken );
                            response.sendRedirect( validateUrl );
                            LOG.info( "VendastaLoginController.Authorizer finished" );

                        } catch ( InvalidInputException | NoRecordsFetchedException error ) {
                            LOG.error( "Insufficeint data to parse a response" );
                            response.sendRedirect( "/vendastaError.do" );
                        } catch ( IOException error ) {
                            LOG.error( "Failed to redirect with validate Url to vendasta" );
                            response.sendRedirect( "/vendastaError.do" );
                        }
                    } else {
                        LOG.error( "Required one or more valid parameters" );
                        response.sendRedirect( "/vendastaError.do" );
                    }
                } else {
                    LOG.info( "Insufficient permission to access Vendasta" );
                    response.sendRedirect( "/vendastaError.do" );
                }

            } else {
                LOG.error( "Unauthorized request to access Vendasta accounts" );
                response.sendRedirect( "/vendastaError.do" );
            }
        } catch ( IOException e ) {
            LOG.error( "VendastaLoginController.Authorizer not able to redirect to vedensta error page" );
        }
    }


    //method to validate a ssoticket by checking if the ticket is stored in vendasta_sso_ticket table
    @RequestMapping ( value = "/validation")
    public void validator( HttpServletRequest request, HttpServletResponse response )
    {
        LOG.info( "VendastaLoginController.Validator started" );

        String productId = request.getParameter( "product_id" );
        String ssoToken = request.getParameter( "sso_token" );
        String ssoTicket = request.getParameter( "sso_ticket" );

        if ( vendastaManagementSerivce.validateSSOTuple( productId, ssoToken, ssoTicket ) ) {
            response.setStatus( HttpStatus.OK.value() );
            LOG.info( "VendastaController.Validator has successfully validated" );
        } else {
            response.setStatus( HttpStatus.UNAUTHORIZED.value() );
            LOG.error( "VendastaLoginController.Validator failed to validate" );

        }
    }


}
