package com.realtech.socialsurvey.web.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AbusiveMailSettings;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProListUser;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserListFromSearch;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.BaseRestException;
import com.realtech.socialsurvey.core.exception.CompanyProfilePreconditionFailureErrorCode;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InputValidationException;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.ProfileServiceErrorCode;
import com.realtech.socialsurvey.core.exception.RestErrorResponse;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsLocker;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsManager;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.web.common.ErrorCodes;
import com.realtech.socialsurvey.web.common.ErrorResponse;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Kind;


/**
 * JIRA:SS-117 by RM02 Class with rest services for fetching various profiles
 */

@Controller
@RequestMapping ( value = "/profile")
public class ProfileController
{

    private static final Logger LOG = LoggerFactory.getLogger( ProfileController.class );

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private ProfileManagementService profileManagementService;
    @Autowired
    private UserManagementService userManagementService;
    @Autowired
    private SolrSearchService solrSearchService;
    @Autowired
    private SurveyHandler surveyHandler;
    @Autowired
    private SettingsManager settingsManager;
    @Autowired
    private SettingsLocker settingsLocker;

    @Autowired
    private EmailServices emailServices;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String applicationAdminEmail;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String applicationAdminName;

    private static final String PROFILE_TYPE_COMPANY = "company";
    private static final String PROFILE_TYPE_REGION = "region";
    private static final String PROFILE_TYPE_BRANCH = "branch";
    private static final String PROFILE_TYPE_INDIVIDUAL = "individual";


    /**
     * Service to get company details along with all regions based on profile name
     * 
     * @param profileName
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/{profileName}")
    public Response getCompanyProfile( @PathVariable String profileName ) throws ProfileNotFoundException
    {
        LOG.info( "Service to get company profile called for profileName :" + profileName );
        Response response = null;
        try {
            if ( profileName == null || profileName.isEmpty() ) {
                throw new InputValidationException( new CompanyProfilePreconditionFailureErrorCode(
                    "Company profile name is not specified for getting company profile" ),
                    "Company profile name is not specified for getting company profile" );
            }
            OrganizationUnitSettings companyProfile = null;

            companyProfile = profileManagementService.getCompanyProfileByProfileName( profileName );
            String json = new Gson().toJson( companyProfile );
            LOG.debug( "companyProfile json : " + json );
            response = Response.ok( json ).build();

        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to get company profile executed successfully" );
        return response;
    }


    /**
     * Service to get region profile based on company profile name and region profile name
     * 
     * @param companyProfileName
     * @param regionProfileName
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/{companyProfileName}/region/{regionProfileName}")
    public Response getRegionProfile( @PathVariable String companyProfileName, @PathVariable String regionProfileName )
        throws ProfileNotFoundException
    {
        LOG.info( "Service to get region profile called for regionProfileName:" + regionProfileName );
        Response response = null;
        try {
            if ( companyProfileName == null || companyProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_PROFILE_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_REGION_PROFILE, "Profile name for company is invalid" ),
                    "company profile name is null or empty" );
            }
            if ( regionProfileName == null || regionProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_PROFILE_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_REGION_PROFILE, "Profile name for region is invalid" ),
                    "region profile name is null or empty" );
            }
            OrganizationUnitSettings regionProfile = null;
            try {
                regionProfile = profileManagementService.getRegionByProfileName( companyProfileName, regionProfileName );

                // aggregated social profile urls
                SocialMediaTokens agentTokens = profileManagementService.aggregateSocialProfiles( regionProfile,
                    CommonConstants.REGION_ID );
                regionProfile.setSocialMediaTokens( agentTokens );

                String json = new Gson().toJson( regionProfile );
                LOG.debug( "regionProfile json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_PROFILE_SERVICE_FAILURE, CommonConstants.SERVICE_CODE_REGION_PROFILE,
                    "Error occured while fetching region profile" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to get region profile executed successfully" );
        return response;
    }


    /**
     * Service to get branch based on company profile name and branch profile name
     * 
     * @param companyProfileName
     * @param branchProfileName
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/{companyProfileName}/branch/{branchProfileName}")
    public Response getBranchProfile( @PathVariable String companyProfileName, @PathVariable String branchProfileName )
        throws ProfileNotFoundException
    {
        LOG.info( "Service to get branch profile called for regionProfileName:" + branchProfileName );
        Response response = null;
        try {
            if ( companyProfileName == null || companyProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_BRANCH_PROFILE_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_BRANCH_PROFILE, "Profile name for company is invalid" ),
                    "company profile name is null or empty" );
            }
            if ( branchProfileName == null || branchProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_BRANCH_PROFILE_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_BRANCH_PROFILE, "Profile name for branch is invalid" ),
                    "branch profile name is null or empty" );
            }
            OrganizationUnitSettings branchProfile = null;
            try {
                branchProfile = profileManagementService.getBranchByProfileName( companyProfileName, branchProfileName );

                // aggregated social profile urls
                SocialMediaTokens agentTokens = profileManagementService.aggregateSocialProfiles( branchProfile,
                    CommonConstants.BRANCH_ID );
                branchProfile.setSocialMediaTokens( agentTokens );

                String json = new Gson().toJson( branchProfile );
                LOG.debug( "branchProfile json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_BRANCH_PROFILE_SERVICE_FAILURE, CommonConstants.SERVICE_CODE_BRANCH_PROFILE,
                    "Error occured while fetching branch profile" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to get branch profile executed successfully" );
        return response;
    }


    /**
     * Service to get the profile of an individual
     * 
     * @param companyProfileName
     * @param individualProfileName
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/individual/{individualProfileName}")
    public Response getIndividualProfile( @PathVariable String individualProfileName ) throws ProfileNotFoundException
    {
        LOG.info( "Service to get profile of individual called for individualProfileName : " + individualProfileName );
        Response response = null;
        try {
            if ( individualProfileName == null || individualProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_INDIVIDUAL_PROFILE_SERVICE_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_INDIVIDUAL_PROFILE, "Profile name for individual is invalid" ),
                    "individual profile name is null or empty" );
            }
            OrganizationUnitSettings individualProfile = null;
            try {
                individualProfile = profileManagementService.getIndividualByProfileName( individualProfileName );

                // aggregated social profile urls
                SocialMediaTokens agentTokens = profileManagementService.aggregateSocialProfiles( individualProfile,
                    CommonConstants.AGENT_ID );
                individualProfile.setSocialMediaTokens( agentTokens );

                String json = new Gson().toJson( individualProfile );
                LOG.debug( "individualProfile json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_INDIVIDUAL_PROFILE_SERVICE_FAILURE,
                    CommonConstants.SERVICE_CODE_INDIVIDUAL_PROFILE, "Profile name for individual is invalid" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to get profile of individual finished" );
        return response;

    }


    /**
     * Service to fetch all regions for company whose profile name is specified
     * 
     * @param companyProfileName
     * @return
     * @throws InvalidInputException
     */
    @ResponseBody
    @RequestMapping ( value = "/{companyProfileName}/regions")
    public Response getRegionsForCompany( @PathVariable String companyProfileName ) throws InvalidInputException,
        ProfileNotFoundException
    {
        LOG.info( "Service to get regions for company called for companyProfileName:" + companyProfileName );
        Response response = null;
        try {
            if ( companyProfileName == null || companyProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_ALL_REGIONS, "Profile name for company is invalid" ),
                    "company profile name is null or empty while fetching all regions for a company" );
            }
            List<Region> regions = null;
            try {
                regions = organizationManagementService.getRegionsForCompany( companyProfileName );
                String json = new Gson().toJson( regions );
                LOG.debug( "regions json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_FETCH_SERVICE_FAILURE, CommonConstants.SERVICE_CODE_FETCH_ALL_REGIONS,
                    "Error occured while fetching regions under a company" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to get regions for company excecuted successfully" );
        return response;
    }


    /**
     * Service to fetch all branches linked directly to a company
     * 
     * @param companyProfileName
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/{companyProfileName}/branches")
    public Response getBranchesForCompany( @PathVariable String companyProfileName ) throws ProfileNotFoundException
    {
        LOG.info( "Service to get all branches of company called" );
        Response response = null;
        try {
            if ( companyProfileName == null || companyProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_COMPANY_BRANCHES_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_COMPANY_BRANCHES, "Profile name for company is invalid" ),
                    "company profile name is null or empty while fetching all branches directly under a company" );
            }
            LOG.debug( "Calling services to fetch all branches linked directly to company:" + companyProfileName );
            List<Branch> branches = null;
            try {
                branches = organizationManagementService.getBranchesUnderCompany( companyProfileName );
                String json = new Gson().toJson( branches );
                LOG.debug( "regions json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_COMPANY_BRANCHES_FETCH_SERVICE_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_COMPANY_BRANCHES,
                    "Something went wrong while fetching branches under company" ), e.getMessage(), e );
            }

        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }

        LOG.info( "Service to get all branches of company executed successfully" );
        return response;
    }


    /**
     * Service to fetch all individuals linked directly to a company
     * 
     * @param companyProfileName
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/{companyProfileName}/individuals")
    public Response getIndividualsForCompany( @PathVariable String companyProfileName ) throws ProfileNotFoundException
    {
        LOG.info( "Service to get all individuals of company called" );
        Response response = null;
        try {
            if ( companyProfileName == null || companyProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_COMPANY_INDIVIDUALS_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_COMPANY_INDIVIDUALS, "Profile name for company is invalid" ),
                    "company profile name is null or empty while fetching all individuals for a company" );
            }
            List<AgentSettings> users = null;
            try {
                users = profileManagementService.getIndividualsForCompany( companyProfileName );
                String json = new Gson().toJson( users );
                LOG.debug( "individuals json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_COMPANY_INDIVIDUALS_FETCH_SERVICE_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_COMPANY_INDIVIDUALS,
                    "Error occurred while fetching individuals for company" ), e.getMessage() );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }

        LOG.info( "Service to get all individuals of company excecuted successfully" );
        return response;
    }


    /**
     * Service to fetch all the branches inside a region of company
     * 
     * @param companyProfileName
     * @param regionProfileName
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/{companyProfileName}/region/{regionProfileName}/branches")
    public Response getBranchesForRegion( @PathVariable String companyProfileName, @PathVariable String regionProfileName )
        throws ProfileNotFoundException
    {
        LOG.info( "Service to fetch all the branches inside a region of company called" );
        Response response = null;
        try {
            if ( companyProfileName == null || companyProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_BRANCHES_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_REGION_BRANCHES, "Profile name for company is invalid" ),
                    "company profile name is null or empty while fetching all branches for the region" );
            }
            if ( regionProfileName == null || regionProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_BRANCHES_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_REGION_BRANCHES, "Profile name for region is invalid" ),
                    "region profile name is null or empty while fetching all branches for the region" );
            }
            List<Branch> branches = null;
            try {
                branches = organizationManagementService.getBranchesForRegion( companyProfileName, regionProfileName );
                String json = new Gson().toJson( branches );
                LOG.debug( "branches json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_BRANCHES_FETCH_SERVICE_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_REGION_BRANCHES,
                    "Something went wrong while fetching branches under region" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }

        LOG.info( "Service to fetch all the branches inside a region of company executed successfully" );
        return response;
    }


    /**
     * Service to fetch branches when regionId is provided
     * 
     * @param regionId
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/region/{regionId}/branches")
    public Response getBranchesByRegionId( @PathVariable long regionId ) throws ProfileNotFoundException
    {
        LOG.info( "Service to fetch branches for a region called for regionId :" + regionId );
        Response response = null;
        try {
            if ( regionId <= 0l ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_BRANCHES_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_REGION_BRANCHES, "Region id is invalid" ),
                    "region id is invalid while fetching all branches for the region" );
            }
            try {
                List<Branch> branches = organizationManagementService.getBranchesByRegionId( regionId );
                String json = new Gson().toJson( branches );
                LOG.debug( "branches json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_BRANCHES_FETCH_SERVICE_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_REGION_BRANCHES,
                    "Something went wrong while fetching individuals under region" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        return response;
    }


    @ResponseBody
    @RequestMapping ( value = "/branch/{branchId}/individuals")
    public Response getIndividualsByBranchId( @PathVariable long branchId, HttpServletRequest request )
    {
        LOG.info( "Service to fetch individuals for branch called for branchId:" + branchId );
        Response response = null;
        try {
            if ( branchId <= 0l ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_BRANCH_INDIVIDUALS_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_BRANCH_INDIVIDUALS, "Branch id is invalid" ),
                    "branch id is invalid while fetching all individuals for the branch" );
            }
            try {
                int startIndex = -1;
                int batchSize = -1;
                String startIndexStr = request.getParameter( "start" );
                String batchSizeStr = request.getParameter( "rows" );
                try {
                    startIndex = Integer.parseInt( startIndexStr );
                    batchSize = Integer.parseInt( batchSizeStr );
                } catch ( NumberFormatException e ) {
                    LOG.error( "Invalid startIndex or batch size passed" );
                }
                List<AgentSettings> individuals = profileManagementService.getIndividualsByBranchId( branchId, startIndex,
                    batchSize );
                String json = new Gson().toJson( individuals );
                LOG.debug( "individuals json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_BRANCH_INDIVIDUALS_FETCH_SERVICE_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_BRANCH_INDIVIDUALS,
                    "Something went wrong while fetching individuals under branch" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to fetch individuals for branch executed successfully" );
        return response;
    }


    /**
     * Service to fetch individuals for the provided region id
     * 
     * @param regionId
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/region/{regionId}/individuals")
    public Response getIndividualsByRegionId( @PathVariable long regionId, HttpServletRequest request )
    {
        LOG.info( "Service to fetch individuals for region called for regionId:" + regionId );
        Response response = null;
        try {
            if ( regionId <= 0l ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_INDIVIDUALS_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_REGION_INDIVIDUALS, "Region id is invalid" ),
                    "region id is invalid while fetching all individuals for the region" );
            }

            int startIndex = -1;
            int batchSize = -1;
            String startIndexStr = request.getParameter( "start" );
            String batchSizeStr = request.getParameter( "rows" );
            try {
                startIndex = Integer.parseInt( startIndexStr );
                batchSize = Integer.parseInt( batchSizeStr );
            } catch ( NumberFormatException e ) {
                LOG.error( "Invalid startIndex or batch size passed" );
            }

            try {
                List<AgentSettings> individuals = profileManagementService.getIndividualsByRegionId( regionId, startIndex,
                    batchSize );
                String json = new Gson().toJson( individuals );
                LOG.debug( "individuals json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_INDIVIDUALS_FETCH_SERVICE_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_REGION_INDIVIDUALS,
                    "Something went wrong while fetching individuals under region" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to fetch individuals for region executed successfully" );
        return response;
    }


    /**
     * Service to get all individuals directly linked to the specified region
     * 
     * @param companyProfileName
     * @param regionProfileName
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/{companyProfileName}/region/{regionProfileName}/individuals")
    public Response getIndividualsForRegion( @PathVariable String companyProfileName, @PathVariable String regionProfileName )
        throws ProfileNotFoundException
    {
        LOG.info( "Service to get all individuals directly linked to the specified region called" );
        Response response = null;
        try {
            if ( companyProfileName == null || companyProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_INDIVIDUALS_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_REGION_INDIVIDUALS, "Profile name for company is invalid" ),
                    "company profile name is null or empty while fetching all individuals for the region" );
            }
            if ( regionProfileName == null || regionProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_INDIVIDUALS_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_REGION_INDIVIDUALS, "Profile name for company is invalid" ),
                    "region profile name is null or empty while fetching all individuals for the region" );
            }
            List<AgentSettings> users = null;
            try {
                users = profileManagementService.getIndividualsForRegion( companyProfileName, regionProfileName );
                String json = new Gson().toJson( users );
                LOG.debug( "individuals json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_INDIVIDUALS_FETCH_SERVICE_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_REGION_INDIVIDUALS,
                    "Something went wrong while fetching individuals under region" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to get all individuals directly linked to the specified region executed successfully" );
        return response;
    }


    /**
     * Service to get the list of users under the branch specified
     * 
     * @param companyProfileName
     * @param branchProfileName
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/{companyProfileName}/branch/{branchProfileName}/individuals")
    public Response getIndividualsForBranch( @PathVariable String companyProfileName, @PathVariable String branchProfileName )
        throws ProfileNotFoundException
    {
        LOG.info( "Servie to get all individuals in a branch called" );
        Response response = null;
        try {
            if ( companyProfileName == null || companyProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_BRANCH_INDIVIDUALS_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_BRANCH_INDIVIDUALS, "Profile name for company is invalid" ),
                    "company profile name is null or empty while fetching all individuals for a branch" );
            }
            if ( branchProfileName == null || branchProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_BRANCH_INDIVIDUALS_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_BRANCH_INDIVIDUALS, "Profile name for branch is invalid" ),
                    "branch profile name is null or empty while fetching all individuals for a branch" );
            }
            List<AgentSettings> users = null;
            try {
                users = profileManagementService.getIndividualsForBranch( companyProfileName, branchProfileName );
                String json = new Gson().toJson( users );
                LOG.debug( "individuals under branch json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_BRANCH_INDIVIDUALS_FETCH_SERVICE_FAILURE,
                    CommonConstants.SERVICE_CODE_FETCH_BRANCH_INDIVIDUALS,
                    "Something went wrong while fetching individuals under branch" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }

        LOG.info( "Service to get all individuals in a branch executed successfully" );
        return response;
    }


    /**
     * Service to fetch the reviews within the rating score specified
     * 
     * @param companyId
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/company/{companyId}/reviews")
    public Response getReviewsForCompany( @PathVariable long companyId, @QueryParam ( value = "minScore") Double minScore,
        @QueryParam ( value = "maxScore") Double maxScore, @QueryParam ( value = "start") Integer start,
        @QueryParam ( value = "numRows") Integer numRows, @QueryParam ( value = "sortCriteria") String sortCriteria )
    {
        LOG.info( "Service to fetch reviews of company called for companyId: {} ,minScore: {} and maxscore: {}", companyId, minScore, maxScore );
        Response response = null;
        try {
            if ( companyId <= 0l ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_COMPANY_REVIEWS_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_COMPANY_REVIEWS, "Company id for company is invalid" ),
                    "company id is not valid while fetching all reviews for a company" );
            }
            if ( minScore == null ) {
                minScore = CommonConstants.MIN_RATING_SCORE;
            }
            if ( maxScore == null ) {
                maxScore = CommonConstants.MAX_RATING_SCORE;
            }
            if ( start == null ) {
                start = -1;
            }
            if ( numRows == null ) {
                numRows = -1;
            }
            try {
                List<SurveyDetails> reviews = profileManagementService.getReviews( companyId, minScore, maxScore, start,
                    numRows, CommonConstants.PROFILE_LEVEL_COMPANY, false, null, null,
                    profileManagementService.processSortCriteria( companyId, sortCriteria ) );
                //This is added to get the agent's app ID and profile URL 
                //DO NOT REMOVE!
                profileManagementService.setAgentProfileUrlForReview( reviews );
                String json = new Gson().toJson( reviews );
                LOG.debug( "reviews json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_COMPANY_REVIEWS_FETCH_FAILURE, CommonConstants.SERVICE_CODE_COMPANY_REVIEWS,
                    "Something went wrong while fetching reviews for a company" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }

        LOG.info( "Service to fetch reviews of company completed successfully" );
        return response;
    }


    /**
     * Service to fetch reviews for a region
     * 
     * @param regionId
     * @param minScore
     * @param maxScore
     * @param start
     * @param numRows
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/region/{regionId}/reviews")
    public Response getReviewsForRegion( @PathVariable long regionId, @QueryParam ( value = "minScore") Double minScore,
        @QueryParam ( value = "maxScore") Double maxScore, @QueryParam ( value = "start") Integer start,
        @QueryParam ( value = "numRows") Integer numRows, @QueryParam ( value = "sortCriteria") String sortCriteria )
    {
        LOG.info( "Service to fetch reviews of region called for regionId:" + regionId + " ,minScore:" + minScore
            + " and maxscore:" + maxScore );
        Response response = null;


        OrganizationUnitSettings regionProfile = null;
        try {
            if ( regionId <= 0l ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_REVIEWS_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_REGION_REVIEWS, "Region id for region is invalid" ),
                    "region id is not valid while fetching all reviews for a region" );
            }
            if ( minScore == null ) {
                minScore = CommonConstants.MIN_RATING_SCORE;
            }
            if ( maxScore == null ) {
                maxScore = CommonConstants.MAX_RATING_SCORE;
            }
            if ( start == null ) {
                start = -1;
            }
            if ( numRows == null ) {
                numRows = -1;
            }

            try {


                regionProfile = organizationManagementService.getRegionSettings( regionId );
                if ( regionProfile.getSurvey_settings() == null ) {
                    SurveySettings surveySettings = new SurveySettings();
                    surveySettings.setAutoPostEnabled( true );
                    surveySettings.setShow_survey_above_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
                    surveySettings.setAuto_post_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
                    organizationManagementService.updateScoreForSurvey(
                        MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionProfile, surveySettings );
                    // update survey settings in the profile object
                    regionProfile.setSurvey_settings( surveySettings );

                } else {
                    if ( regionProfile.getSurvey_settings().getShow_survey_above_score() <= 0 ) {
                        regionProfile.getSurvey_settings().setAutoPostEnabled( true );
                        regionProfile.getSurvey_settings().setShow_survey_above_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
                        regionProfile.getSurvey_settings().setAuto_post_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
                        organizationManagementService.updateScoreForSurvey(
                            MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionProfile,
                            regionProfile.getSurvey_settings() );
                    }
                }
                if ( minScore != 0.0 ) {
                    minScore = (double) regionProfile.getSurvey_settings().getShow_survey_above_score();
                }

                List<SurveyDetails> reviews = profileManagementService.getReviews( regionId, minScore, maxScore, start, numRows,
                    CommonConstants.PROFILE_LEVEL_REGION, false, null, null, profileManagementService.processSortCriteria(
                        userManagementService.getRegionById( regionId ).getCompany().getCompanyId(), sortCriteria ) );
                //This is added to get the agent's app ID and profile URL 
                //DO NOT REMOVE!
                profileManagementService.setAgentProfileUrlForReview( reviews );
                String json = new Gson().toJson( reviews );
                LOG.debug( "reviews json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_REVIEWS_FETCH_FAILURE, CommonConstants.SERVICE_CODE_REGION_REVIEWS,
                    "Something went wrong while fetching reviews for a region" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }

        LOG.info( "Service to fetch reviews of region completed successfully" );
        return response;
    }


    /**
     * Service to fetch average ratings for company
     * 
     * @param companyId
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/company/{companyId}/ratings")
    public Response getAverageRatingForCompany( @PathVariable long companyId )
    {
        LOG.info( "Service to get average rating of company called " );
        Response response = null;
        try {
            if ( companyId <= 0l ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_AVERAGE_RATING_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_COMPANY_AVERAGE_RATINGS, "Company id for company is invalid" ),
                    "company id is not valid while fetching average ratings for a company" );
            }
            try {
                double averageRating = profileManagementService.getAverageRatings( companyId,
                    CommonConstants.PROFILE_LEVEL_COMPANY, false );
                String json = new Gson().toJson( averageRating );
                LOG.debug( "averageRating json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_AVERAGE_RATING_FETCH_FAILURE,
                    CommonConstants.SERVICE_CODE_COMPANY_AVERAGE_RATINGS,
                    "Something went wrong while fetching average ratings for company" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to get average rating of company executed successfully " );
        return response;
    }


    /**
     * Service to fetch count of reviews based on ratings specified
     * 
     * @param companyId
     * @param minScore
     * @param maxScore
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/company/{companyId}/reviewcount")
    public Response getReviewCountForCompany( @PathVariable long companyId, @QueryParam ( value = "minScore") Double minScore,
        @QueryParam ( value = "maxScore") Double maxScore, @QueryParam ( value = "notRecommended") Boolean notRecommended )
    {
        LOG.info( "Service to fetch the reviews count called for companyId :" + companyId + " ,minScore:" + minScore
            + " and maxScore:" + maxScore );
        Response response = null;
        try {
            if ( companyId <= 0l ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REVIEWS_COUNT_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_COMPANY_REVIEWS_COUNT, "Company id is invalid" ),
                    "company id is not valid while fetching reviews count for a company" );
            }
            if ( minScore == null ) {
                minScore = CommonConstants.MIN_RATING_SCORE;
            }
            if ( maxScore == null ) {
                maxScore = CommonConstants.MAX_RATING_SCORE;
            }
            if ( notRecommended == null ) {
                notRecommended = false;
            }
            long reviewsCount = 0;
            try {
                reviewsCount = profileManagementService.getReviewsCount( companyId, minScore, maxScore,
                    CommonConstants.PROFILE_LEVEL_COMPANY, false, notRecommended );
                String json = new Gson().toJson( reviewsCount );
                LOG.debug( "reviews count json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REVIEWS_COUNT_FETCH_FAILURE, CommonConstants.SERVICE_CODE_COMPANY_REVIEWS_COUNT,
                    "Error occured while getting reviews count" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to fetch the reviews count executed successfully" );
        return response;

    }


    /**
     * Service to fetch average ratings for region
     * 
     * @param regionId
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/region/{regionId}/ratings")
    public Response getAverageRatingForRegion( @PathVariable long regionId )
    {
        LOG.info( "Service to get average rating of region called " );
        Response response = null;
        try {
            if ( regionId <= 0l ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_AVERAGE_RATING_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_REGION_AVERAGE_RATINGS, "Region id for region is invalid" ),
                    "region id is not valid while fetching average ratings for a region" );
            }
            try {
                double averageRating = profileManagementService.getAverageRatings( regionId,
                    CommonConstants.PROFILE_LEVEL_REGION, false );
                String json = new Gson().toJson( averageRating );
                LOG.debug( "averageRating json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_AVERAGE_RATING_FETCH_FAILURE,
                    CommonConstants.SERVICE_CODE_REGION_AVERAGE_RATINGS,
                    "Something went wrong while fetching average ratings for region" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to get average rating of region executed successfully " );
        return response;
    }


    /**
     * Service to fetch review count for a region
     * 
     * @param regionId
     * @param minScore
     * @param maxScore
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/region/{regionId}/reviewcount")
    public Response getReviewCountForRegion( @PathVariable long regionId, @QueryParam ( value = "minScore") Double minScore,
        @QueryParam ( value = "maxScore") Double maxScore, @QueryParam ( value = "notRecommended") Boolean notRecommended )
    {
        LOG.info( "Service to fetch the reviews count called for regionId :" + regionId + " ,minScore:" + minScore
            + " and maxScore:" + maxScore );
        Response response = null;
        try {
            if ( regionId <= 0l ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REVIEWS_COUNT_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_REGION_REVIEWS_COUNT, "Region id is invalid" ),
                    "region id is not valid while fetching reviews count for a region" );
            }
            if ( minScore == null ) {
                minScore = CommonConstants.MIN_RATING_SCORE;
            }
            if ( maxScore == null ) {
                maxScore = CommonConstants.MAX_RATING_SCORE;
            }
            if ( notRecommended == null ) {
                notRecommended = false;
            }
            long reviewsCount = 0;
            try {
                reviewsCount = profileManagementService.getReviewsCount( regionId, minScore, maxScore,
                    CommonConstants.PROFILE_LEVEL_REGION, false, notRecommended );
                String json = new Gson().toJson( reviewsCount );
                LOG.debug( "reviews count json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REVIEWS_COUNT_FETCH_FAILURE, CommonConstants.SERVICE_CODE_REGION_REVIEWS_COUNT,
                    "Error occured while getting reviews count" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to fetch the reviews count executed successfully" );
        return response;
    }


    /**
     * Service to fetch average ratings for branch
     * 
     * @param branchId
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/branch/{branchId}/ratings")
    public Response getAverageRatingForBranch( @PathVariable long branchId )
    {
        LOG.info( "Service to get average rating of branch called " );
        Response response = null;
        try {
            if ( branchId <= 0l ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_AVERAGE_RATING_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_BRANCH_AVERAGE_RATINGS, "branch id for branch is invalid" ),
                    "branch id is not valid while fetching average ratings for a branch" );
            }
            try {
                double averageRating = profileManagementService.getAverageRatings( branchId,
                    CommonConstants.PROFILE_LEVEL_BRANCH, false );
                String json = new Gson().toJson( averageRating );
                LOG.debug( "averageRating json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_AVERAGE_RATING_FETCH_FAILURE,
                    CommonConstants.SERVICE_CODE_BRANCH_AVERAGE_RATINGS,
                    "Something went wrong while fetching average ratings for branch" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to get average rating of branch executed successfully " );
        return response;
    }


    /**
     * Service to fetch review count for a branch
     * 
     * @param branchId
     * @param minScore
     * @param maxScore
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/branch/{branchId}/reviewcount")
    public Response getReviewCountForBranch( @PathVariable long branchId, @QueryParam ( value = "minScore") Double minScore,
        @QueryParam ( value = "maxScore") Double maxScore, @QueryParam ( value = "notRecommended") Boolean notRecommended )
    {
        LOG.info( "Service to fetch the reviews count called for branchId :" + branchId + " ,minScore:" + minScore
            + " and maxScore:" + maxScore );
        Response response = null;
        try {
            if ( branchId <= 0l ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REVIEWS_COUNT_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_BRANCH_REVIEWS_COUNT, "branch id is invalid" ),
                    "branch id is not valid while fetching reviews count for a branch" );
            }
            if ( minScore == null ) {
                minScore = CommonConstants.MIN_RATING_SCORE;
            }
            if ( maxScore == null ) {
                maxScore = CommonConstants.MAX_RATING_SCORE;
            }
            if ( notRecommended == null ) {
                notRecommended = false;
            }
            long reviewsCount = 0;
            try {
                reviewsCount = profileManagementService.getReviewsCount( branchId, minScore, maxScore,
                    CommonConstants.PROFILE_LEVEL_BRANCH, false, notRecommended );
                String json = new Gson().toJson( reviewsCount );
                LOG.debug( "reviews count json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REVIEWS_COUNT_FETCH_FAILURE, CommonConstants.SERVICE_CODE_BRANCH_REVIEWS_COUNT,
                    "Error occured while getting reviews count" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to fetch the reviews count of a branch executed successfully" );
        return response;
    }


    /**
     * Service to fetch reviews for a branch
     * 
     * @param branchId
     * @param minScore
     * @param maxScore
     * @param start
     * @param numRows
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/branch/{branchId}/reviews")
    public Response getReviewsForBranch( @PathVariable long branchId, @QueryParam ( value = "minScore") Double minScore,
        @QueryParam ( value = "maxScore") Double maxScore, @QueryParam ( value = "start") Integer start,
        @QueryParam ( value = "numRows") Integer numRows, @QueryParam ( value = "sortCriteria") String sortCriteria )
    {
        LOG.info( "Service to fetch reviews of branch called for branchId:" + branchId + " ,minScore:" + minScore
            + " and maxscore:" + maxScore );
        Response response = null;
        long companyId = 0;
        long regionId = 0;

        Map<SettingsForApplication, OrganizationUnit> map = null;
        OrganizationUnitSettings branchProfile = null;
        try {
            if ( branchId <= 0l ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_BRANCH_REVIEWS_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_BRANCH_REVIEWS, "branch id for branch is invalid" ),
                    "branch id is not valid while fetching all reviews for a branch" );
            }
            if ( minScore == null ) {
                minScore = CommonConstants.MIN_RATING_SCORE;
            }
            if ( maxScore == null ) {
                maxScore = CommonConstants.MAX_RATING_SCORE;
            }
            if ( start == null ) {
                start = -1;
            }
            if ( numRows == null ) {
                numRows = -1;
            }
            try {

                branchProfile = organizationManagementService.getBranchSettingsDefault( branchId );
                if ( branchProfile.getSurvey_settings() == null ) {
                    SurveySettings surveySettings = new SurveySettings();
                    surveySettings.setShow_survey_above_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
                    surveySettings.setAuto_post_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
                    surveySettings.setAutoPostEnabled( true );
                    organizationManagementService.updateScoreForSurvey(
                        MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchProfile, surveySettings );

                    branchProfile.setSurvey_settings( surveySettings );
                    // update survey settings in the profile object
                    branchProfile.setSurvey_settings( surveySettings );
                } else {
                    if ( branchProfile.getSurvey_settings().getShow_survey_above_score() <= 0 ) {
                        branchProfile.getSurvey_settings().setAutoPostEnabled( true );
                        branchProfile.getSurvey_settings().setShow_survey_above_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
                        branchProfile.getSurvey_settings().setAuto_post_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
                        organizationManagementService.updateScoreForSurvey(
                            MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchProfile,
                            branchProfile.getSurvey_settings() );
                    }
                }
                if ( minScore != 0.0 ) {
                    minScore = (double) branchProfile.getSurvey_settings().getShow_survey_above_score();
                }
                List<SurveyDetails> reviews = profileManagementService.getReviews( branchId, minScore, maxScore, start, numRows,
                    CommonConstants.PROFILE_LEVEL_BRANCH, false, null, null, profileManagementService.processSortCriteria(
                        userManagementService.getBranchById( branchId ).getCompany().getCompanyId(), sortCriteria ) );
                //This is added to get the agent's app ID and profile URL 
                //DO NOT REMOVE!
                profileManagementService.setAgentProfileUrlForReview( reviews );
                String json = new Gson().toJson( reviews );
                LOG.debug( "reviews json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_BRANCH_REVIEWS_FETCH_FAILURE, CommonConstants.SERVICE_CODE_BRANCH_REVIEWS,
                    "Something went wrong while fetching reviews for a branch" ), e.getMessage(), e );
            } catch ( NoRecordsFetchedException e1 ) {
                LOG.error( "Branch Profile Not Found ", e1 );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }

        LOG.info( "Service to fetch reviews of branch completed successfully" );
        return response;
    }


    /**
     * Service to fetch average ratings for agent
     * 
     * @param agentId
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/individual/{agentId}/ratings")
    public Response getAverageRatingForAgent( @PathVariable long agentId )
    {
        LOG.info( "Service to get average rating of agent called for agentId:" + agentId );
        Response response = null;
        try {
            if ( agentId <= 0l ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_AVERAGE_RATING_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_INDIVIDUAL_AVERAGE_RATINGS, "individual id is invalid" ),
                    "agent id is not valid while fetching average ratings for an agent" );
            }
            try {
                double averageRating = profileManagementService.getAverageRatings( agentId,
                    CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false );
                String json = new Gson().toJson( averageRating );
                LOG.debug( "averageRating json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_AVERAGE_RATING_FETCH_FAILURE,
                    CommonConstants.SERVICE_CODE_BRANCH_AVERAGE_RATINGS,
                    "Something went wrong while fetching average ratings for agent" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to get average rating of agent executed successfully " );
        return response;
    }


    /**
     * Service to fetch review count for an agent
     * 
     * @param agentId
     * @param minScore
     * @param maxScore
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/individual/{agentId}/reviewcount")
    public Response getReviewCountForAgent( @PathVariable long agentId, @QueryParam ( value = "minScore") Double minScore,
        @QueryParam ( value = "maxScore") Double maxScore, @QueryParam ( value = "notRecommended") Boolean notRecommended )
    {
        LOG.info( "Service to fetch the reviews count called for agentId :" + agentId + " ,minScore:" + minScore
            + " and maxScore:" + maxScore );
        Response response = null;
        try {
            if ( agentId <= 0l ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REVIEWS_COUNT_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_INDIVIDUAL_REVIEWS_COUNT, "agent id is invalid" ),
                    "agent id is not valid while fetching reviews count for a agent" );
            }
            if ( minScore == null ) {
                minScore = CommonConstants.MIN_RATING_SCORE;
            }
            if ( maxScore == null ) {
                maxScore = CommonConstants.MAX_RATING_SCORE;
            }
            if ( notRecommended == null ) {
                notRecommended = false;
            }
            long reviewsCount = 0;
            try {
                reviewsCount = profileManagementService.getReviewsCount( agentId, minScore, maxScore,
                    CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false, notRecommended );
                String json = new Gson().toJson( reviewsCount );
                LOG.debug( "reviews count json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REVIEWS_COUNT_FETCH_FAILURE,
                    CommonConstants.SERVICE_CODE_INDIVIDUAL_REVIEWS_COUNT, "Error occured while getting reviews count" ),
                    e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to fetch the reviews count of an agent executed successfully" );
        return response;
    }


    /**
     * Service to fetch reviews for an agent
     * 
     * @param agentId
     * @param minScore
     * @param maxScore
     * @param start
     * @param numRows
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/individual/{agentId}/reviews")
    public Response getReviewsForAgent( @PathVariable long agentId, @QueryParam ( value = "minScore") Double minScore,
        @QueryParam ( value = "maxScore") Double maxScore, @QueryParam ( value = "start") Integer start,
        @QueryParam ( value = "numRows") Integer numRows, @QueryParam ( value = "sortCriteria") String sortCriteria )
    {
        LOG.info( "Service to fetch reviews of an agent called for agentId:" + agentId + " ,minScore:" + minScore
            + " and maxscore:" + maxScore );
        Response response = null;

        OrganizationUnitSettings agentProfile = null;

        try {
            if ( agentId <= 0l ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_REVIEWS_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_REGION_REVIEWS, "Agent id is invalid" ),
                    "agent id is not valid while fetching all reviews for an agent" );
            }
            if ( minScore == null ) {
                minScore = CommonConstants.MIN_RATING_SCORE;
            }
            if ( maxScore == null ) {
                maxScore = CommonConstants.MAX_RATING_SCORE;
            }
            if ( start == null ) {
                start = -1;
            }
            if ( numRows == null ) {
                numRows = -1;
            }
            agentProfile = organizationManagementService.getAgentSettings( agentId );

            if ( agentProfile.getSurvey_settings() != null ) {
                if ( agentProfile.getSurvey_settings().getShow_survey_above_score() <= 0 ) {
                    agentProfile.getSurvey_settings().setShow_survey_above_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
                    agentProfile.getSurvey_settings().setAuto_post_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
                    agentProfile.getSurvey_settings().setAutoPostEnabled( true );
                    organizationManagementService.updateScoreForSurvey(
                        MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentProfile,
                        agentProfile.getSurvey_settings() );
                }
            } else {
                SurveySettings surveySettings = new SurveySettings();
                surveySettings.setShow_survey_above_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
                surveySettings.setAuto_post_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
                surveySettings.setAutoPostEnabled( true );
                organizationManagementService.updateScoreForSurvey(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentProfile, surveySettings );

                // update survey settings in the profile object
                agentProfile.setSurvey_settings( surveySettings );
            }
            if ( minScore != 0.0 ) {
                minScore = (double) agentProfile.getSurvey_settings().getShow_survey_above_score();
            }
            List<SurveyDetails> reviews = profileManagementService.getReviews( agentId, minScore, maxScore, start, numRows,
                CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false, null, null, profileManagementService.processSortCriteria(
                    userManagementService.getUserByUserId( agentId ).getCompany().getCompanyId(), sortCriteria ) );
            profileManagementService.setAgentProfileUrlForReview( reviews );
            String json = new Gson().toJson( reviews );
            LOG.debug( "reviews json : " + json );
            response = Response.ok( json ).build();

        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        } catch ( InvalidInputException e ) {
            throw new FatalException( "Exception caught ", e );
        } catch ( NoRecordsFetchedException e ) {
            throw new FatalException( "Exception caught ", e );
        }

        LOG.info( "Service to fetch reviews of agent completed successfully" );
        return response;
    }


    /**
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/individuals/{iden}")
    public Response getProListByProfile( @PathVariable long iden, @QueryParam ( value = "profileLevel") String profileLevel,
        @QueryParam ( value = "start") Integer start, @QueryParam ( value = "numOfRows") Integer numRows )
    {
        LOG.info( "Method getProListByProfile called for iden:" + iden + " and profileLevel:" + profileLevel );
        Response response = null;
        try {
            if ( iden <= 0l ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_PRO_LIST_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_PRO_LIST_FETCH, "Could not fetch users list. Iden is invalid" ),
                    "iden is invalid while getting users list for profile" );
            }
            if ( profileLevel == null ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_PRO_LIST_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_PRO_LIST_FETCH, "Could not fetch users list. Iden is invalid" ),
                    "iden is invalid while getting users list for profile" );
            }
            if ( start == null ) {
                start = -1;
            }
            if ( numRows == null ) {
                numRows = -1;
            }
            try {
                List<Long> userIds = new ArrayList<Long>();
                String idenFieldName = "";
                List<ProListUser> users = new ArrayList<ProListUser>();
                UserListFromSearch userList = new UserListFromSearch();

                switch ( profileLevel ) {
                    case CommonConstants.PROFILE_LEVEL_COMPANY:
                        idenFieldName = CommonConstants.COMPANY_ID_SOLR;
                        break;
                    case CommonConstants.PROFILE_LEVEL_REGION:
                        idenFieldName = CommonConstants.REGIONS_SOLR;
                        break;
                    case CommonConstants.PROFILE_LEVEL_BRANCH:
                        idenFieldName = CommonConstants.BRANCHES_SOLR;
                        break;
                    default:
                        throw new InvalidInputException( "profile level is invalid in getProListByProfileLevel" );
                }

                SolrDocumentList results = solrSearchService.getUserIdsByIden( iden, idenFieldName, true, start, numRows );

                for ( SolrDocument solrDocument : results ) {
                    userIds.add( (Long) solrDocument.getFieldValue( "userId" ) );
                }
                users = userManagementService.getMultipleUsersByUserId( userIds );
                userList.setUsers( users );
                userList.setUserFound( results.getNumFound() );
                String json = new Gson().toJson( userList );
                LOG.debug( "userList json : " + json );
                response = Response.ok( json ).build();
            } catch ( SolrException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_PRO_LIST_FETCH_FAILURE, CommonConstants.SERVICE_CODE_PRO_LIST_FETCH,
                    "Could not fetch users list." ), e.getMessage(), e );
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_PRO_LIST_FETCH_FAILURE, CommonConstants.SERVICE_CODE_PRO_LIST_FETCH,
                    "Could not fetch users list." ), e.getMessage(), e  );
            }
        } catch ( BaseRestException e ) {
            LOG.error( "BaseRestException while searching in getProListByProfile(). Reason : " + e.getMessage(), e );
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrCode( ErrorCodes.REQUEST_FAILED );
            errorResponse.setErrMessage( e.getMessage() );
            String json = new Gson().toJson( errorResponse );
            response = Response.ok( json ).build();
        }

        return response;

    }


    /**
     * Downloads the vcard
     * 
     * @param id
     * @return
     */
    @RequestMapping ( value = "/downloadvcard/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Response downloadVCard( @PathVariable String id, HttpServletResponse response ) throws ProfileNotFoundException
    {
        LOG.info( "Downloading vcard for profile id: " + id );
        try {
            if ( id == null || id.isEmpty() ) {
                LOG.error( "Profile id missing to download vcard" );
                throw new InputValidationException( new ProfileServiceErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "Profile id missing to download vcard" ),
                    "Expected profile id, but is null or empty" );
            }
            OrganizationUnitSettings individualProfile = null;
            try {
                individualProfile = profileManagementService.getIndividualByProfileName( id );
                if ( individualProfile != null ) {
                    LOG.debug( "Creating Vcard for the profile" );
                    VCard vCard = new VCard();
                    vCard.setKind( Kind.individual() );
                    // Set the name
                    if ( individualProfile.getProfileName() != null ) {
                        LOG.debug( "Setting profile name as formatted name in the vcard" );
                        vCard.setFormattedName( individualProfile.getProfileName() );
                    }
                    // set the contact number
                    if ( individualProfile.getContact_details() != null ) {
                        ContactDetailsSettings contactDetails = individualProfile.getContact_details();
                        // set the contact number
                        if ( contactDetails.getContact_numbers() != null ) {
                            if ( contactDetails.getContact_numbers().getWork() != null ) {
                                LOG.debug( "Setting work contact number" );
                                vCard.addTelephoneNumber( contactDetails.getContact_numbers().getWork(), TelephoneType.WORK );
                            }
                            if ( contactDetails.getContact_numbers().getPersonal() != null ) {
                                LOG.debug( "Setting cell contact number" );
                                vCard
                                    .addTelephoneNumber( contactDetails.getContact_numbers().getPersonal(), TelephoneType.CELL );
                            }
                            if ( contactDetails.getContact_numbers().getFax() != null ) {
                                LOG.debug( "Setting fax number" );
                                vCard.addTelephoneNumber( contactDetails.getContact_numbers().getFax(), TelephoneType.FAX );
                            }
                        }
                        // setting email addresses
                        if ( contactDetails.getMail_ids() != null ) {
                            if ( contactDetails.getMail_ids().getWork() != null
                                && contactDetails.getMail_ids().getIsWorkEmailVerified() ) {
                                LOG.debug( "Adding work email address" );
                                vCard.addEmail( contactDetails.getMail_ids().getWork(), EmailType.WORK );
                            }
                            if ( contactDetails.getMail_ids().getPersonal() != null
                                && contactDetails.getMail_ids().getIsPersonalEmailVerified() ) {
                                LOG.debug( "Adding personla email address" );
                                vCard.addEmail( contactDetails.getMail_ids().getPersonal(), EmailType.HOME );
                            }
                        }
                        // setting the title
                        if ( contactDetails.getTitle() != null ) {
                            LOG.debug( "Setting title: " + contactDetails.getTitle() );
                            vCard.addTitle( contactDetails.getTitle() );
                        }

                        // validate to version 4
                        LOG.warn( vCard.validate( VCardVersion.V4_0 ).toString() );

                        // send it to the response
                        response.setContentType( "text/vcf" );
                        response.setHeader( "Content-Disposition",
                            String.format( "attachment; filename=\"%s\"", individualProfile.getProfileName() + ".vcf" ) );
                        OutputStream responseStream = null;
                        try {
                            responseStream = response.getOutputStream();
                            Ezvcard.write( vCard ).go( responseStream );
                        } catch ( IOException e ) {
                            e.printStackTrace();
                        } finally {
                            try {
                                responseStream.close();
                            } catch ( IOException e ) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                    CommonConstants.SERVICE_CODE_GENERAL, "Profile name for individual is invalid" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            return getErrorResponse( e );
        }
        return null;
    }


    /**
     * Service to get the posts of an individual
     * 
     * @param individualProfileName
     * @param start
     * @param numberOfRows
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/{individualProfileName}/posts")
    public Response getPostsForIndividual( @PathVariable String individualProfileName,
        @QueryParam ( value = "start") Integer start, @QueryParam ( value = "numRows") Integer numRows )
        throws ProfileNotFoundException
    {
        LOG.info( "Service to get posts of an individual called for individualProfileName : " + individualProfileName );
        Response response = null;
        try {
            if ( individualProfileName == null || individualProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_INDIVIDUAL_POSTS_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_INDIVIDUAL_POSTS, "Profile name for individual is invalid" ),
                    "individual profile name is null or empty" );
            }
            if ( start == null ) {
                start = -1;
            }
            if ( numRows == null ) {
                numRows = -1;
            }
            OrganizationUnitSettings individualProfile = null;
            try {
                individualProfile = profileManagementService.getIndividualByProfileName( individualProfileName );
                List<SocialPost> posts = profileManagementService.getSocialPosts( individualProfile.getIden(),
                    CommonConstants.AGENT_ID_COLUMN, start, numRows );
                String json = new Gson().toJson( posts );
                LOG.debug( "individual posts json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_INDIVIDUAL_POSTS_FETCH_FAILURE, CommonConstants.SERVICE_CODE_INDIVIDUAL_POSTS,
                    "Profile name for individual is invalid" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to get posts of individual finished" );
        return response;
    }


    /**
     * Service to get the posts of company
     * 
     * @param companyName
     * @param start
     * @param numberOfRows
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/company/{companyProfileName}/posts")
    public Response getPostsForCompany( @PathVariable String companyProfileName, @QueryParam ( value = "start") Integer start,
        @QueryParam ( value = "numRows") Integer numRows ) throws ProfileNotFoundException
    {
        LOG.info( "Service to get posts of a company called for companyProfileName : " + companyProfileName );
        Response response = null;
        try {
            if ( companyProfileName == null || companyProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_COMPANY_POSTS_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_COMPANY_POSTS, "Profile name for company is invalid" ),
                    "company profile name is null or empty" );
            }
            if ( start == null ) {
                start = -1;
            }
            if ( numRows == null ) {
                numRows = -1;
            }

            OrganizationUnitSettings companyProfile = null;
            try {
                companyProfile = profileManagementService.getCompanyProfileByProfileName( companyProfileName );
                List<SocialPost> posts = profileManagementService.getSocialPosts( companyProfile.getIden(),
                    CommonConstants.COMPANY_ID_COLUMN, start, numRows );
                String json = new Gson().toJson( posts );
                LOG.debug( "individual posts json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_COMPANY_POSTS_FETCH_FAILURE, CommonConstants.SERVICE_CODE_COMPANY_POSTS,
                    "Profile name for company is invalid" ), e.getMessage(), e  );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to get posts of company finished" );
        return response;
    }


    /**
     * Service to get the posts of region
     * 
     * @param regionProfileName
     * @param start
     * @param numberOfRows
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/region/{companyProfileName}/{regionProfileName}/posts")
    public Response getPostsForRegion( @PathVariable String regionProfileName, @PathVariable String companyProfileName,
        @QueryParam ( value = "start") Integer start, @QueryParam ( value = "numRows") Integer numRows )
        throws ProfileNotFoundException
    {
        LOG.info( "Service to get posts of a region called for regionProfileName : " + regionProfileName );
        Response response = null;
        try {
            if ( regionProfileName == null || regionProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_POSTS_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_REGION_POSTS, "Profile name for region is invalid" ),
                    "region profile name is null or empty" );
            }
            if ( start == null ) {
                start = -1;
            }
            if ( numRows == null ) {
                numRows = -1;
            }

            OrganizationUnitSettings regionProfile = null;
            try {
                regionProfile = profileManagementService.getRegionByProfileName( companyProfileName, regionProfileName );
                List<SocialPost> posts = profileManagementService.getSocialPosts( regionProfile.getIden(),
                    CommonConstants.REGION_ID_COLUMN, start, numRows );
                String json = new Gson().toJson( posts );
                LOG.debug( "individual posts json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_POSTS_FETCH_FAILURE, CommonConstants.SERVICE_CODE_REGION_POSTS,
                    "Profile name for region is invalid" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to get posts of region finished" );
        return response;
    }


    /**
     * Service to get the posts of branch
     * 
     * @param branchProfileName
     * @param start
     * @param numberOfRows
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/branch/{companyProfileName}/{branchProfileName}/posts")
    public Response getPostsForBranch( @PathVariable String branchProfileName, @PathVariable String companyProfileName,
        @QueryParam ( value = "start") Integer start, @QueryParam ( value = "numRows") Integer numRows )
        throws ProfileNotFoundException
    {
        LOG.info( "Service to get posts of a branch called for branchProfileName : " + branchProfileName );
        Response response = null;
        try {
            if ( branchProfileName == null || branchProfileName.isEmpty() ) {
                throw new InputValidationException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_BRANCH_POSTS_FETCH_PRECONDITION_FAILURE,
                    CommonConstants.SERVICE_CODE_BRANCH_POSTS, "Profile name for branch is invalid" ),
                    "branch profile name is null or empty" );
            }
            if ( start == null ) {
                start = -1;
            }
            if ( numRows == null ) {
                numRows = -1;
            }

            OrganizationUnitSettings branchProfile = null;
            try {
                branchProfile = profileManagementService.getBranchByProfileName( companyProfileName, branchProfileName );
                List<SocialPost> posts = profileManagementService.getSocialPosts( branchProfile.getIden(),
                    CommonConstants.BRANCH_ID_COLUMN, start, numRows );
                String json = new Gson().toJson( posts );
                LOG.debug( "individual posts json : " + json );
                response = Response.ok( json ).build();
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_BRANCH_POSTS_FETCH_FAILURE, CommonConstants.SERVICE_CODE_BRANCH_POSTS,
                    "Profile name for branch is invalid" ), e.getMessage(), e );
            }
        } catch ( BaseRestException e ) {
            response = getErrorResponse( e );
        }
        LOG.info( "Service to get posts of branch finished" );
        return response;
    }


    /**
     * Method to fetch zillow reviews for a  profile type
     * */
    @ResponseBody
    @RequestMapping ( value = "/{profileType}/{iden}/zillowreviews")
    public Response getZillowReviews( @PathVariable String profileType, @PathVariable long iden )
        throws ProfileNotFoundException
    {
        LOG.info( "Getting zillow reviews for profile type: " + profileType + " and id: " + iden );
        Response response = null;
        if ( profileType.equals( PROFILE_TYPE_COMPANY ) ) {
            try {
                // get the company details
                OrganizationUnitSettings companyProfile = organizationManagementService.getCompanySettings( iden );
                if ( companyProfile.getSocialMediaTokens() != null
                    && companyProfile.getSocialMediaTokens().getZillowToken() != null ) {
                    LOG.info( "Fetcing zillow reviews for company id: " + iden );
                    // Commented as Zillow reviews will be fetched only for Agents, SS-307
                    // List<SurveyDetails> surveyDetailsList = profileManagementService.fetchZillowData( companyProfile,
                    //    CommonConstants.COMPANY_SETTINGS_COLLECTION );
                    LOG.info( "Done fetching zillow reviews for company id: " + iden );
                    //String json = new Gson().toJson( surveyDetailsList );
                    response = Response.ok().build();
                }
            } catch ( InvalidInputException e ) {
                LOG.error( "Could not fetch zillow reviews for company: " + iden, e );
            //} catch ( UnavailableException e ) {
            //    LOG.error( "Could not fetch zillow reviews for company: " + iden, e );
            //    response = Response.ok( CommonConstants.ZILLOW_FETCH_FAIL_RESPONSE ).build();
            }
        } else if ( profileType.equals( PROFILE_TYPE_REGION ) ) {
            try {
                // get the region details
                OrganizationUnitSettings regionProfile = organizationManagementService.getRegionSettings( iden );
                if ( regionProfile.getSocialMediaTokens() != null
                    && regionProfile.getSocialMediaTokens().getZillowToken() != null ) {
                    LOG.info( "Fetcing zillow reviews for region id: " + iden );
                    // Commented as Zillow reviews will be fetched only for Agents, SS-307
                    // List<SurveyDetails> surveyDetailsList = profileManagementService.fetchZillowData( regionProfile,
                    //    CommonConstants.REGION_SETTINGS_COLLECTION );
                    LOG.info( "Done fetching zillow reviews for region id: " + iden );
                    // String json = new Gson().toJson( surveyDetailsList );
                    response = Response.ok().build();
                }
            } catch ( InvalidInputException e ) {
                LOG.error( "Could not fetch zillow reviews for region: " + iden, e );
            //} catch ( UnavailableException e ) {
            //    LOG.error( "Could not fetch zillow reviews for region: " + iden, e );
            //    response = Response.ok( CommonConstants.ZILLOW_FETCH_FAIL_RESPONSE ).build();
            }
        } else if ( profileType.equals( PROFILE_TYPE_BRANCH ) ) {
            try {
                // get the branch details
                OrganizationUnitSettings branchProfile = organizationManagementService.getBranchSettingsDefault( iden );
                if ( branchProfile.getSocialMediaTokens() != null
                    && branchProfile.getSocialMediaTokens().getZillowToken() != null ) {
                    LOG.info( "Fetcing zillow reviews for branch id: " + iden );
                    // Commented as Zillow reviews will be fetched only for Agents, SS-307
                    // List<SurveyDetails> surveyDetailsList = profileManagementService.fetchZillowData( branchProfile,
                    //    CommonConstants.BRANCH_SETTINGS_COLLECTION );
                    LOG.info( "Done fetching zillow reviews for branch id: " + iden );
//                    String json = new Gson().toJson( surveyDetailsList );
                    response = Response.ok().build();
                }
            } catch ( InvalidInputException e ) {
                LOG.error( "Could not fetch zillow reviews for branch: " + iden, e );
            } catch ( NoRecordsFetchedException e ) {
                LOG.error( "Could not fetch zillow reviews for branch: " + iden, e );
            //} catch ( UnavailableException e ) {
            //   LOG.error( "Could not fetch zillow reviews for branch: " + iden, e );
            //   response = Response.ok( CommonConstants.ZILLOW_FETCH_FAIL_RESPONSE ).build();
            }
        } else if ( profileType.equals( PROFILE_TYPE_INDIVIDUAL ) ) {
            try {
                // get company id for the user id
                User user = userManagementService.getUserByUserId( iden );
                // get the individual details
                AgentSettings individualProfile = organizationManagementService.getAgentSettings( iden );
                if ( individualProfile.getSocialMediaTokens() != null
                    && individualProfile.getSocialMediaTokens().getZillowToken() != null ) {
                    LOG.info( "Fetcing zillow reviews for agent id: " + iden );
                    List<SurveyDetails> surveyDetailsList = profileManagementService.fetchAndSaveZillowData( individualProfile,
                        CommonConstants.AGENT_SETTINGS_COLLECTION, user.getCompany().getCompanyId(), false, true );
                    LOG.info( "Done fetching zillow reviews for agent id: " + iden );
                    String json = new Gson().toJson( surveyDetailsList );
                    response = Response.ok( json ).build();
                }
            } catch ( InvalidInputException e ) {
                LOG.error( "Could not fetch zillow reviews for agent: " + iden, e );
            } catch ( NoRecordsFetchedException e ) {
                LOG.error( "Could not fetch zillow reviews for agent: " + iden, e );
            } catch ( UnavailableException e ) {
                LOG.error( "Could not fetch zillow reviews for agent: " + iden, e );
                response = Response.ok( CommonConstants.ZILLOW_FETCH_FAIL_RESPONSE ).build();
            }
        }
        LOG.info( "Fetched zillow reviews for profile type: " + profileType + " and id: " + iden );
        return response;
    }


    @ResponseBody
    @RequestMapping ( value = "/surveyreportabuse")
    public String reportAbuse( HttpServletRequest request )
    {
        String reason = request.getParameter( "reportText" );
        String reporterName = request.getParameter( "reporterName" );
        String reporterEmail = request.getParameter( "reporterEmail" );
        String surveyMongoId = request.getParameter( "surveyMongoId" );

        try {
             if ( surveyMongoId == null || surveyMongoId.isEmpty() ) {
                throw new InvalidInputException( "Invalid value (Null/Empty) found for surveyMongoId." );
            }
            
            SurveyDetails surveyDetails = surveyHandler.getSurveyDetails( surveyMongoId );
            if ( surveyDetails == null ) {
                throw new InvalidInputException( "Invalid value. No survey found for surveyMongoId." );
            }
            
            //make survey as abusive
            surveyHandler.updateSurveyAsAbusive( surveyMongoId, reporterEmail, reporterName, reason );

            String customerName = surveyDetails.getCustomerFirstName() + surveyDetails.getCustomerLastName();
            // Calling email services method to send mail to the Application level admin.
            emailServices.sendReportAbuseMail( applicationAdminEmail, applicationAdminName, surveyDetails.getAgentName(),
                customerName.replaceAll( "null", "" ), surveyDetails.getCustomerEmail(), surveyDetails.getReview(), reason, reporterName, reporterEmail );

            // Calling email services method to send mail to the reporter
            emailServices.sendSurveyReportMail( reporterEmail, reporterName, reason );
            //send abusive mail for registered email
            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings(surveyDetails.getCompanyId());
            if (companySettings.getSurvey_settings() != null && companySettings.getSurvey_settings().getAbusive_mail_settings() != null) {
				AbusiveMailSettings abusiveMailSettings = companySettings.getSurvey_settings().getAbusive_mail_settings();
				surveyDetails.setAbusiveNotify(true);
				surveyHandler.updateSurveyAsAbusiveNotify(surveyDetails.get_id());
				long agentId = surveyDetails.getAgentId();
	            User userObj = userManagementService.getUserByUserId( agentId );


				// SS-1435: Send survey details too.
				// SS-715: Full customer name
				String displayName = surveyDetails.getCustomerFirstName();
				if (surveyDetails.getCustomerLastName() != null)
					displayName = displayName + " " + surveyDetails.getCustomerLastName();
				Date currentDate = new Date( System.currentTimeMillis() );
				emailServices.sendAbusiveNotifyMail(reporterName, abusiveMailSettings.getMailId(), displayName,surveyDetails.getCustomerEmail(), surveyDetails.getAgentName(), 
						userObj.getEmailId(),surveyDetails.getMood(), String.valueOf(surveyDetails.getScore()), surveyDetails.getSourceId(), surveyDetails.getReview(),currentDate.toString());
			}
        } catch ( NonFatalException e ) {
            LOG.error( "NonfatalException caught in reportAbuse(). Nested exception is ", e );
            return CommonConstants.ERROR;
        }

        return CommonConstants.SUCCESS_ATTRIBUTE;
    }


    /**
     * Method to get the error response object from base rest exception
     * 
     * @param ex
     * @return
     */
    private Response getErrorResponse( BaseRestException ex )
    {
        LOG.debug( "Resolve Error Response" );
        Status httpStatus = resolveHttpStatus( ex );
        RestErrorResponse errorResponse = ex.transformException( httpStatus.getStatusCode() );
        Response response = Response.status( httpStatus ).entity( new Gson().toJson( errorResponse ) ).build();
        return response;
    }


    /**
     * Method to get the http status based on the exception type
     * 
     * @param ex
     * @return
     */
    private Status resolveHttpStatus( BaseRestException ex )
    {
        LOG.debug( "Resolving http status" );
        Status httpStatus = Status.INTERNAL_SERVER_ERROR;
        if ( ex instanceof InputValidationException ) {
            httpStatus = Status.UNAUTHORIZED;
        } else if ( ex instanceof InternalServerException ) {
            httpStatus = Status.INTERNAL_SERVER_ERROR;
        }
        LOG.debug( "Resolved http status to " + httpStatus.getStatusCode() );
        return httpStatus;
    }


    /**
     * Method to fetch all ids under a profile level connected to zillow
     * */
    @ResponseBody
    @RequestMapping ( value = "/{profileType}/{iden}/fetchhierarchyconnectedtozillow")
    public String getIdsOfHeirarchyConnectedWithZillow( @PathVariable String profileType, @PathVariable long iden,
        @QueryParam ( value = "start") Integer start, @QueryParam ( value = "numRows") Integer numRows,
        @QueryParam ( value = "currentHierarchyLevel") String currentHierarchyLevel )
    {
        String json = null;
        LOG.info( "Method getIdsOfHeirarchyConnectedWithZillow started to get ids of hierarchy under " + profileType
            + " and id: " + iden + " for currentHierarchyLevel:" + currentHierarchyLevel + " connected with zillow" );
        Set<Long> ids = null;
        try {
            if ( profileType.equals( PROFILE_TYPE_COMPANY ) ) {
                if ( currentHierarchyLevel.equals( CommonConstants.PROFILE_LEVEL_REGION ) ) {
                    ids = organizationManagementService.getAllRegionsUnderCompanyConnectedToZillow( iden, start, numRows );
                } else if ( currentHierarchyLevel.equals( CommonConstants.PROFILE_LEVEL_BRANCH ) ) {
                    ids = organizationManagementService.getAllBranchesUnderProfileTypeConnectedToZillow( profileType, iden, start
                        , numRows );
                } else if ( currentHierarchyLevel.equals( CommonConstants.PROFILE_LEVEL_INDIVIDUAL ) ) {
                    ids = organizationManagementService.getAllUsersUnderProfileTypeConnectedToZillow( profileType, iden, start,
                        numRows );
                }
            } else if ( profileType.equals( PROFILE_TYPE_REGION ) ) {
                if ( currentHierarchyLevel.equals( CommonConstants.PROFILE_LEVEL_BRANCH ) ) {
                    ids = organizationManagementService.getAllBranchesUnderProfileTypeConnectedToZillow( profileType, iden, start,
                        numRows );
                } else if ( currentHierarchyLevel.equals( CommonConstants.PROFILE_LEVEL_INDIVIDUAL ) ) {
                    ids = organizationManagementService.getAllUsersUnderProfileTypeConnectedToZillow( profileType, iden, start,
                        numRows );
                }
            } else if ( profileType.equals( PROFILE_TYPE_BRANCH ) ) {
                if ( currentHierarchyLevel.equals( CommonConstants.PROFILE_LEVEL_INDIVIDUAL ) ) {
                    ids = organizationManagementService.getAllUsersUnderProfileTypeConnectedToZillow( profileType, iden, start,
                        numRows );
                }
            }
            if ( ids != null && !ids.isEmpty() ) {
                json = new Gson().toJson( ids );
            }
            LOG.info( "Getting ids of hierarchy under " + profileType + " and id: " + iden + " for currentHierarchyLevel:"
                + currentHierarchyLevel + " connected with zillow" );
            LOG.info( "Method getIdsOfHeirarchyConnectedWithZillow ended" );
        } catch ( InvalidInputException iie ) {
            LOG.error(
                "InvalidInputException occurred while fetching ids for profile type and current hierarchy type. Reason : ", iie );
        } catch ( Exception e ) {
            LOG.error(
                "Exception occurred while fetching ids for profile type and current hierarchy type. Reason : ", e );
        }
        return json;

    }
}