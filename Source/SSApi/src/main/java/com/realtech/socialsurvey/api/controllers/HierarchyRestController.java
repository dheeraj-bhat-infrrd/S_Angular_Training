package com.realtech.socialsurvey.api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.core.exception.AuthorizationException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.facade.HierarchyFacade;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.vo.HierarchyViewVO;


@RestController
@RequestMapping ( "/v1")
public class HierarchyRestController
{

    private static final Logger LOG = LoggerFactory.getLogger( HierarchyRestController.class );

    @Autowired
    private HierarchyFacade hierarchyFacade;

    @Autowired
    private AdminAuthenticationService adminAuthenticationService;


    @RequestMapping ( value = "/companyhierarchy", method = RequestMethod.GET)
    public HierarchyViewVO companyHierarchyView( @RequestParam ( required = true) long companyId,
        @RequestHeader (required = true, value= "authorizationHeader") String authorizationHeader ) throws SSApiException, AuthorizationException
    {
        LOG.info( "Inside companyHierarchyView() method in admin controller" );
        adminAuthenticationService.validateAuthHeader( authorizationHeader );
        try {
            return hierarchyFacade.getCompanyHierarchyView( companyId );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while fetching hierarchy view list main page", e );
            throw new SSApiException( "Error while getting company hierarchy", e );
        }

    }
}
