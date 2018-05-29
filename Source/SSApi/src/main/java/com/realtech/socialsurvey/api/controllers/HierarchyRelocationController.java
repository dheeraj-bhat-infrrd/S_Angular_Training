package com.realtech.socialsurvey.api.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.api.utils.RestUtils;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.AuthorizationException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.services.hierarchylocationmanagement.HierarchyLocationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping ( "/v2/relocate")
public class HierarchyRelocationController
{
    private static final Logger LOG = LoggerFactory.getLogger( HierarchyRelocationController.class );

    @Autowired
    private RestUtils restUtils;

    @Autowired
    AdminAuthenticationService adminAuthenticationService;

    @Autowired
    HierarchyLocationManagementService hierarchyLocationManagementService;

    @Autowired
    UserManagementService userManagementService;


    @RequestMapping ( value = "/region", method = RequestMethod.GET)
    @ApiOperation ( value = "Relocation of a Region")
    public ResponseEntity<?> relocateRegion( HttpServletRequest request )
    {
        LOG.info( "HierarchyRelocationController.relocateRegion started" );


        //authenticate relocation
        String authorizationHeader = request.getHeader( CommonConstants.SURVEY_API_REQUEST_PARAMETER_AUTHORIZATION );
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<Void>( HttpStatus.UNAUTHORIZED );
        }

        long regionId = 0;
        long targetCompanyId = 0;

        try {
            regionId = Long.parseLong( request.getParameter( "region-id" ) );
        } catch ( NumberFormatException numberFormatException ) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Unable to parse the region ID", null, null,
                request, targetCompanyId );
        }

        try {
            targetCompanyId = Long.parseLong( request.getParameter( "target-company-id" ) );
        } catch ( NumberFormatException numberFormatException ) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Unable to parse the target company ID", null, null,
                request, targetCompanyId );
        }

        // start the relocation process
        try {
            hierarchyLocationManagementService.generateEntitiesAndStartRelocationForRegion( regionId, targetCompanyId );
        } catch ( InvalidInputException | SolrException exception ) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, exception.getMessage(), null, null, request,
                targetCompanyId );
        }

        LOG.info( "HierarchyRelocationController.relocateRegion finished" );
        return restUtils.getRestResponseEntity( HttpStatus.CREATED, "Region successfully Relocated", null, null, request,
            targetCompanyId );
    }


    @RequestMapping ( value = "/branch", method = RequestMethod.GET)
    @ApiOperation ( value = "Relocation of a Branch")
    public ResponseEntity<?> relocateBranch( HttpServletRequest request )
    {
        LOG.info( "HierarchyRelocationController.relocateBranch started" );

        //authenticate relocation
        String authorizationHeader = request.getHeader( CommonConstants.SURVEY_API_REQUEST_PARAMETER_AUTHORIZATION );
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<Void>( HttpStatus.UNAUTHORIZED );
        }

        long branchId = 0;
        long targetCompanyId = 0;
        long targetRegionId = 0;

        try {
            branchId = Long.parseLong( request.getParameter( "branch-id" ) );
        } catch ( NumberFormatException numberFormatException ) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Unable to parse the branch ID", null, null,
                request, targetCompanyId );
        }

        try {
            targetCompanyId = Long.parseLong( request.getParameter( "target-company-id" ) );
        } catch ( NumberFormatException numberFormatException ) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Unable to parse the target company ID", null, null,
                request, targetCompanyId );
        }

        try {
            targetRegionId = Long.parseLong( request.getParameter( "target-region-id" ) );
        } catch ( NumberFormatException numberFormatException ) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Unable to parse the target region ID", null, null,
                request, targetCompanyId );
        }

        // start the relocation process
        try {
            hierarchyLocationManagementService.generateEntitiesAndStartRelocationForBranch( branchId, targetCompanyId,
                targetRegionId );
        } catch ( InvalidInputException | SolrException exception ) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, exception.getMessage(), null, null, request,
                targetCompanyId );
        }
        LOG.info( "HierarchyRelocationController.relocateBranch finished" );
        return restUtils.getRestResponseEntity( HttpStatus.CREATED, "Branch successfully Relocated", null, null, request,
            targetCompanyId );
    }


    @RequestMapping ( value = "/user", method = RequestMethod.GET)
    @ApiOperation ( value = "Relocation of a User")
    public ResponseEntity<?> relocateUser( HttpServletRequest request )
    {
        LOG.info( "HierarchyRelocationController.relocateUser started" );

        //authenticate relocation
        String authorizationHeader = request.getHeader( CommonConstants.SURVEY_API_REQUEST_PARAMETER_AUTHORIZATION );
        try {
            adminAuthenticationService.validateAuthHeader( authorizationHeader );
        } catch ( AuthorizationException authoriztionFailure ) {
            return new ResponseEntity<Void>( HttpStatus.UNAUTHORIZED );
        }

        int surveyRelocation = 0;
        long userId = 0;
        long targetBranchId = 0;
        long targetCompanyId = 0;


        try {
            userId = Long.parseLong( request.getParameter( "user_id" ) );
        } catch ( NumberFormatException numberFormatException ) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Unable to parse the user_id", null, null, request,
                targetCompanyId );
        }


        try {
            targetBranchId = Long.parseLong( request.getParameter( "target_branch_id" ) );
        } catch ( NumberFormatException numberFormatException ) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Unable to parse the target target_branch_id", null, null,
                request, targetCompanyId );
        }
        
        try {
            surveyRelocation = Integer.parseInt( request.getParameter( "survey_realocation" ) );
        } catch ( NumberFormatException numberFormatException ) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, "Unable to parse the survey_realocation", null, null, request,
                targetCompanyId );
        }

        // start the relocation process
        try {
            hierarchyLocationManagementService.generateEntitiesAndStartRelocationForUser( userId,  targetBranchId, surveyRelocation );
        } catch ( InvalidInputException | SolrException exception ) {
            return restUtils.getRestResponseEntity( HttpStatus.BAD_REQUEST, exception.getMessage(), null, null, request,
                targetCompanyId );
        }

        LOG.info( "HierarchyRelocationController.relocateUser finished" );
        return restUtils.getRestResponseEntity( HttpStatus.CREATED, "User successfully Relocated", null, null, request,
            targetCompanyId );
    }
}
