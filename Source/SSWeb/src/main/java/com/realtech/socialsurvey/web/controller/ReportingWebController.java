package com.realtech.socialsurvey.web.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SettingsDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.ProfileServiceErrorCode;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.reportingmanagement.ReportingDashboardManagement;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsManager;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.common.JspResolver;

import retrofit.client.Response;
import scala.Console;
import scala.collection.generic.BitOperations.Int;

@Controller
public class ReportingWebController
{
    private static final Logger LOG = LoggerFactory.getLogger( AccountWebController.class );

    @Autowired
    private SessionHelper sessionHelper;
    
    @Autowired
    private OrganizationManagementService organizationManagementService;
    
    @Autowired
    private UserManagementService userManagementService;
    
    @Autowired
    private SolrSearchService solrSearchService;
    
    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private ProfileManagementService profileManagementService;
    
    @Autowired
    private SettingsManager settingsManager;
    
    @Autowired
    private SSApiIntergrationBuilder ssApiIntergrationBuilder;
    
    @Autowired
    private ReportingDashboardManagement reportingDashboardManagement;
    

    @RequestMapping ( value = "/showreportingpage", method = RequestMethod.GET)
    public String openReportingPage(Model model, HttpServletRequest request) throws NonFatalException
    {
        
        LOG.info( "Reporting Dashboard Page started" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();
        if ( user == null ) {
            throw new NonFatalException( "NonFatalException while logging in. " );
        }
        
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
        boolean modelSet = false;
        if ( user.getCompany() != null && user.getCompany().getLicenseDetails() != null
            && !user.getCompany().getLicenseDetails().isEmpty()
            && user.getCompany().getLicenseDetails().get( 0 ).getAccountsMaster() != null ) {
            if ( user.getCompany().getLicenseDetails().get( 0 ).getAccountsMaster()
                .getAccountsMasterId() == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                model.addAttribute( "columnName", CommonConstants.AGENT_ID_COLUMN );
                model.addAttribute( "columnValue", entityId );
                modelSet = true;
            }
        }

        String profileName = "";
        boolean isPasswordSet = true;
        if ( user.getIsForcePassword() == 1 && user.getLoginPassword() == null ) {
            isPasswordSet = false;
        }
        if ( !modelSet ) {
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                model.addAttribute( "columnName", entityType );
                model.addAttribute( "columnValue", entityId );
                model.addAttribute( "showSendSurveyPopupAdmin", String.valueOf( isPasswordSet ) );
                profileName = user.getCompany().getCompany();
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                model.addAttribute( "columnName", entityType );
                model.addAttribute( "columnValue", entityId );
                model.addAttribute( "showSendSurveyPopupAdmin", String.valueOf( isPasswordSet ) );
                profileName = solrSearchService.searchRegionById( entityId );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                model.addAttribute( "columnName", entityType );
                model.addAttribute( "columnValue", entityId );
                model.addAttribute( "showSendSurveyPopupAdmin", String.valueOf( isPasswordSet ) );
                profileName = solrSearchService.searchBranchNameById( entityId );
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                model.addAttribute( "columnName", CommonConstants.AGENT_ID_COLUMN );
                model.addAttribute( "columnValue", entityId );
                profileName = user.getFirstName() + " " + user.getLastName();
            }
        }

        model.addAttribute( "profileName", profileName );
        model.addAttribute( "userId", user.getUserId() );
        boolean allowOverrideForSocialMedia = false;
        long branchId = 0;
        long regionId = 0;
        long companyId = 0;
        long agentId = 0;
        int profilesMaster = 0;
        List<SettingsDetails> settingsDetailsList = null;
        OrganizationUnitSettings profileSettings = null;
        Map<SettingsForApplication, OrganizationUnit> map = null;
        //Get the hierarchy details associated with the current profile get all the id's like companyId, regionId , branchId
        try {
            Map<String, Long> hierarchyDetails = profileManagementService.getHierarchyDetailsByEntity( entityType, entityId );
            if ( hierarchyDetails == null ) {
                LOG.error( "Unable to fetch primary profile for this user " );
                throw new FatalException(
                    "Unable to fetch primary profile for type : " + entityType + " and ID : " + entityId );
            }
            branchId = hierarchyDetails.get( CommonConstants.BRANCH_ID_COLUMN );
            regionId = hierarchyDetails.get( CommonConstants.REGION_ID_COLUMN );
            companyId = hierarchyDetails.get( CommonConstants.COMPANY_ID_COLUMN );
            agentId = hierarchyDetails.get( CommonConstants.AGENT_ID_COLUMN );
            //Sorting out the default region's and branche's in the list 
            settingsDetailsList = settingsManager.getScoreForCompleteHeirarchy( companyId, branchId, regionId );
            LOG.debug( "Company ID : " + companyId + " Region ID : " + regionId + " Branch ID : " + branchId + " Agent ID : "
                + agentId );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException while showing profile page. Reason :" + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        } catch ( ProfileNotFoundException e ) {
            LOG.error( "No profile found for the user ", e );
            return JspResolver.NO_PROFILES_FOUND;
        }

        //get unitSetting's and set session attribute column's 
        sessionHelper.updateSelectedProfile( session, entityId, entityType );
        // fetching details from profile based on type 
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            //If the profile is a company profile
            model.addAttribute( "columnName", entityType );
            profilesMaster = CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID;
            OrganizationUnitSettings companyProfile = null;
            try {
                companyProfile = organizationManagementService.getCompanySettings( companyId );
                //set setting detail by company Setting
              //fetch nmls-id
                try {
                    Integer nmlsId = null;
                    nmlsId = profileManagementService.fetchAndSaveNmlsId( companyProfile,
                        CommonConstants.COMPANY_SETTINGS_COLLECTION, user.getCompany().getCompanyId(), false, true );
                  model.addAttribute( "NMLS", nmlsId );
                } catch ( UnavailableException e ) {
                    LOG.error( "UnavailableException: message : " + e.getMessage(), e );
                }
                setSettingSetByEntityInModel( model, companyProfile );
                String json = new Gson().toJson( companyProfile );
                model.addAttribute( "profileJson", json );
                double averageRating = profileManagementService.getAverageRatings( companyId,
                    CommonConstants.PROFILE_LEVEL_COMPANY, false );
                model.addAttribute( "averageRating", averageRating );

                long reviewsCount = profileManagementService.getReviewsCount( companyId, CommonConstants.MIN_RATING_SCORE,
                    CommonConstants.MAX_RATING_SCORE, CommonConstants.PROFILE_LEVEL_COMPANY, false, false );
                //Check if social media override is allowed
                allowOverrideForSocialMedia = companyProfile.isAllowOverrideForSocialMedia();
                model.addAttribute( "reviewsCount", reviewsCount );
            } catch ( InvalidInputException e ) {
                throw new InternalServerException(
                    new ProfileServiceErrorCode( CommonConstants.ERROR_CODE_COMPANY_PROFILE_SERVICE_FAILURE,
                        CommonConstants.SERVICE_CODE_COMPANY_PROFILE, "Error occured while fetching company profile" ),
                    e.getMessage() );
            }
            profileSettings = companyProfile;
            model.addAttribute( "companyProfileName", companyProfile.getProfileName() );
            model.addAttribute( "profileLevel", CommonConstants.PROFILE_LEVEL_COMPANY );
        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            //If the profile is a region profile
            model.addAttribute( "columnName", entityType );
            profilesMaster = CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID;
            OrganizationUnitSettings regionProfile = null;
            OrganizationUnitSettings companyProfile = null;
            try {
                companyProfile = organizationManagementService.getCompanySettings( companyId );
                regionProfile = organizationManagementService.getRegionSettings( regionId );
                //fetch nmls-id
                try {
                    Integer nmlsId = null;
                    nmlsId = profileManagementService.fetchAndSaveNmlsId( regionProfile,
                        CommonConstants.REGION_SETTINGS_COLLECTION, user.getCompany().getCompanyId(), false, true );
                  model.addAttribute( "NMLS", nmlsId );
                } catch ( UnavailableException e ) {
                    LOG.error( "UnavailableException: message : " + e.getMessage(), e );
                }
                //set setting detail by region Setting
                setSettingSetByEntityInModel( model, regionProfile );
                //Check if social media override is allowed
                allowOverrideForSocialMedia = companyProfile.isAllowOverrideForSocialMedia();

                try {
                    map = profileManagementService.getPrimaryHierarchyByEntity( CommonConstants.REGION_ID,
                        regionProfile.getIden() );
                    if ( map == null ) {
                        LOG.error( "Unable to fetch primary profile for this user " );
                        throw new FatalException( "Unable to fetch primary profile this user " + regionProfile.getIden() );
                    }
                } catch ( InvalidSettingsStateException e ) {
                    throw new InternalServerException(
                        new ProfileServiceErrorCode( CommonConstants.ERROR_CODE_REGION_PROFILE_SERVICE_FAILURE,
                            CommonConstants.SERVICE_CODE_REGION_PROFILE, "Error occured while fetching region profile" ),
                        e.getMessage() );
                } catch ( ProfileNotFoundException e ) {
                    LOG.error( "No profile found for the user ", e );
                    return JspResolver.NO_PROFILES_FOUND;
                }

                regionProfile = profileManagementService.fillUnitSettings( regionProfile,
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, companyProfile, regionProfile, null, null,
                    map, false );

                // aggregated disclaimer
                String disclaimer = profileManagementService.aggregateDisclaimer( regionProfile, CommonConstants.REGION_ID );
                regionProfile.setDisclaimer( disclaimer );
                regionProfile.setHiddenSection( companyProfile.isHiddenSection() );

                String json = new Gson().toJson( regionProfile );
                model.addAttribute( "profileJson", json );

                double averageRating = profileManagementService.getAverageRatings( regionId,
                    CommonConstants.PROFILE_LEVEL_REGION, false );
                model.addAttribute( "averageRating", averageRating );

                long reviewsCount = profileManagementService.getReviewsCount( regionId, CommonConstants.MIN_RATING_SCORE,
                    CommonConstants.MAX_RATING_SCORE, CommonConstants.PROFILE_LEVEL_REGION, false, false );
                model.addAttribute( "reviewsCount", reviewsCount );
            } catch ( InvalidInputException e ) {
                throw new InternalServerException(
                    new ProfileServiceErrorCode( CommonConstants.ERROR_CODE_REGION_PROFILE_SERVICE_FAILURE,
                        CommonConstants.SERVICE_CODE_REGION_PROFILE, "Error occured while fetching region profile" ),
                    e.getMessage() );
            }
            profileSettings = regionProfile;
            model.addAttribute( "companyProfileName", companyProfile.getProfileName() );
            model.addAttribute( "regionProfileName", regionProfile.getProfileName() );
            model.addAttribute( "profileLevel", CommonConstants.PROFILE_LEVEL_REGION );

        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            //If the profile is a branch profile
            model.addAttribute( "columnName", entityType );
            profilesMaster = CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID;
            OrganizationUnitSettings companyProfile = null;
            OrganizationUnitSettings branchProfile = null;
            OrganizationUnitSettings regionProfile = null;

            try {
                companyProfile = organizationManagementService.getCompanySettings( companyId );
                regionProfile = organizationManagementService.getRegionSettings( regionId );
                branchProfile = organizationManagementService.getBranchSettingsDefault( branchId );
                //fetch nmls-id
                try {
                    Integer nmlsId = null;
                    nmlsId = profileManagementService.fetchAndSaveNmlsId( branchProfile,
                        CommonConstants.BRANCH_SETTINGS_COLLECTION, user.getCompany().getCompanyId(), false, true );
                  model.addAttribute( "NMLS", nmlsId );
                } catch ( UnavailableException e ) {
                    LOG.error( "UnavailableException: message : " + e.getMessage(), e );
                }

                //set setting detail by branch Setting
                setSettingSetByEntityInModel( model, branchProfile );

                //Check if social media override is allowed
                allowOverrideForSocialMedia = companyProfile.isAllowOverrideForSocialMedia();
                try {
                    map = profileManagementService.getPrimaryHierarchyByEntity( CommonConstants.BRANCH_ID_COLUMN,
                        branchProfile.getIden() );
                    if ( map == null ) {
                        LOG.error( "Unable to fetch primary profile for this user " );
                        throw new FatalException( "Unable to fetch primary profile this user " + branchProfile.getIden() );
                    }

                } catch ( InvalidSettingsStateException e ) {
                    throw new InternalServerException(
                        new ProfileServiceErrorCode( CommonConstants.ERROR_CODE_BRANCH_PROFILE_SERVICE_FAILURE,
                            CommonConstants.SERVICE_CODE_BRANCH_PROFILE, "Error occured while fetching branch profile" ),
                        e.getMessage() );
                } catch ( ProfileNotFoundException e ) {
                    LOG.error( "No profile found for the user ", e );
                    return JspResolver.NO_PROFILES_FOUND;
                }
                branchProfile = profileManagementService.fillUnitSettings( branchProfile,
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, companyProfile, regionProfile,
                    branchProfile, null, map, false );
                // aggregated disclaimer
                String disclaimer = profileManagementService.aggregateDisclaimer( branchProfile, CommonConstants.BRANCH_ID );
                branchProfile.setDisclaimer( disclaimer );
                branchProfile.setHiddenSection( companyProfile.isHiddenSection() );

                String json = new Gson().toJson( branchProfile );
                model.addAttribute( "profileJson", json );

                double averageRating = profileManagementService.getAverageRatings( branchId,
                    CommonConstants.PROFILE_LEVEL_BRANCH, false );
                model.addAttribute( "averageRating", averageRating );

                long reviewsCount = profileManagementService.getReviewsCount( branchId, CommonConstants.MIN_RATING_SCORE,
                    CommonConstants.MAX_RATING_SCORE, CommonConstants.PROFILE_LEVEL_BRANCH, false, false );
                model.addAttribute( "reviewsCount", reviewsCount );
              
            } catch ( InvalidInputException e ) {
                throw new InternalServerException(
                    new ProfileServiceErrorCode( CommonConstants.ERROR_CODE_BRANCH_PROFILE_SERVICE_FAILURE,
                        CommonConstants.SERVICE_CODE_BRANCH_PROFILE, "Error occured while fetching branch profile" ),
                    e.getMessage() );
            } catch ( NoRecordsFetchedException e ) {
                LOG.error( "NoRecordsFetchedException: message : " + e.getMessage(), e );
            }
            profileSettings = branchProfile;
            model.addAttribute( "companyProfileName", companyProfile.getProfileName() );
            model.addAttribute( "branchProfileName", branchProfile.getProfileName() );
            model.addAttribute( "profileLevel", CommonConstants.PROFILE_LEVEL_BRANCH );

        } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
            //If the profile is a individual profile
            model.addAttribute( "columnName", entityType );
            profilesMaster = CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID;
            OrganizationUnitSettings companyProfile = null;
            OrganizationUnitSettings regionProfile = null;
            OrganizationUnitSettings branchProfile = null;
            AgentSettings individualProfile = null;

            try {
                companyProfile = organizationManagementService.getCompanySettings( companyId );
                regionProfile = organizationManagementService.getRegionSettings( regionId );
                branchProfile = organizationManagementService.getBranchSettingsDefault( branchId );
                individualProfile = userManagementService.getAgentSettingsForUserProfiles( agentId );

                //set setting detail by agent Setting
                setSettingSetByEntityInModel( model, individualProfile );

                //Check if social media override is allowed
                allowOverrideForSocialMedia = companyProfile.isAllowOverrideForSocialMedia();

                try {
                    map = profileManagementService.getPrimaryHierarchyByEntity( CommonConstants.AGENT_ID_COLUMN,
                        individualProfile.getIden() );
                    if ( map == null ) {
                        LOG.error( "Unable to fetch primary profile for this user " );
                        throw new FatalException( "Unable to fetch primary profile this user " + branchProfile.getIden() );
                    }

                } catch ( InvalidSettingsStateException e ) {
                    LOG.error( "Error occured while fetching branch profile" + e.getMessage() );
                } catch ( ProfileNotFoundException e ) {
                    LOG.error( "No profile found for the user " );
                    return JspResolver.NO_PROFILES_FOUND;
                }

                if ( map == null ) {
                    LOG.error( "Unable to fetch primary profile for this user " );
                    throw new FatalException( "Unable to fetch primary profile this user " + individualProfile.getIden() );
                }

                individualProfile = (AgentSettings) profileManagementService.fillUnitSettings( individualProfile,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, companyProfile, regionProfile, branchProfile,
                    individualProfile, map, false );
                individualProfile.setVertical( user.getCompany().getVerticalsMaster().getVerticalName() );
                String disclaimer = profileManagementService.aggregateDisclaimer( individualProfile, CommonConstants.AGENT_ID );
                individualProfile.setDisclaimer( disclaimer );
                individualProfile.setHiddenSection( companyProfile.isHiddenSection() );

                String json = new Gson().toJson( individualProfile );
                model.addAttribute( "profileJson", json );

                double averageRating = profileManagementService.getAverageRatings( agentId,
                    CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false );
                model.addAttribute( "averageRating", averageRating );
                long reviewsCount = profileManagementService.getReviewsCount( agentId, CommonConstants.MIN_RATING_SCORE,
                    CommonConstants.MAX_RATING_SCORE, CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false, false );
                model.addAttribute( "reviewsCount", reviewsCount );

                profileSettings = individualProfile;
                
              //fetch nmls-id
                try {
                    Integer nmlsId = null;
                    nmlsId = profileManagementService.fetchAndSaveNmlsId( individualProfile,
                        CommonConstants.AGENT_SETTINGS_COLLECTION, user.getCompany().getCompanyId(), false, true );
                  model.addAttribute( "NMLS", nmlsId );
                } catch ( UnavailableException e ) {
                    LOG.error( "UnavailableException: message : " + e.getMessage(), e );
                }
            } catch ( InvalidInputException e ) {
                LOG.error( "InvalidInputException: message : " + e.getMessage(), e );
                model.addAttribute( "message",
                    messageUtils.getDisplayMessage( DisplayMessageConstants.INVALID_INDIVIDUAL_PROFILENAME,
                        DisplayMessageType.ERROR_MESSAGE ).getMessage() );
                return JspResolver.NOT_FOUND_PAGE;
            } catch ( NoRecordsFetchedException e ) {
                LOG.error( "NoRecordsFetchedException: message : " + e.getMessage(), e );
            }

        }

        
        model.addAttribute( "allowOverrideForSocialMedia", allowOverrideForSocialMedia );
        model.addAttribute( "profileSettings", profileSettings );
        session.setAttribute( CommonConstants.USER_PROFILE_SETTINGS, profileSettings );
        return JspResolver.REPORTING_DASHBOARD; 
    }
    
    @ResponseBody
    @RequestMapping ( value = "/fetchspsfromreportingoverview", method = RequestMethod.GET)
    public Response reportingOverviewSpsStats(Model model, HttpServletRequest request) throws NonFatalException
    {
        LOG.info( "Reporting Dashboard Page started" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();

        Response response = null;
        if ( user == null ) {
            throw new NonFatalException( "NonFatalException while logging in. " );
        }    
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        response = ssApiIntergrationBuilder.getIntegrationApi().getSpsStatsFromOverview( entityId, entityType );
        return response;
        
    }   
    
    @ResponseBody
    @RequestMapping ( value = "/fetchalltimefromreportingoverview", method = RequestMethod.GET)
    public Response reportingOverviewAllTimeStats(Model model, HttpServletRequest request) throws NonFatalException
    {
        LOG.info( "Reporting Dashboard Page started" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();

        Response response = null;
        if ( user == null ) {
            throw new NonFatalException( "NonFatalException while logging in. " );
        }    
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        response = ssApiIntergrationBuilder.getIntegrationApi().getAllTimeDataOverview( entityId, entityType );
        return response;
        
    }   
  
    /*
     * Method to get profile details for displaying
     */
    @RequestMapping ( value = "/showreportingprofiledetails")
    public String getProfileDetails( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to get profile of company/region/branch/agent getProfileDetails() started" );
        User user = sessionHelper.getCurrentUser();

        // settings profile details
        String columnName = request.getParameter( "columnName" );
        String realtechAdminStr = request.getParameter( "realtechAdmin" );
        boolean realtechAdmin = false;

        OrganizationUnitSettings unitSettings = null;
        long columnValue = 0;
        try {
            if ( columnName.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
                columnValue = user.getCompany().getCompanyId();

                unitSettings = organizationManagementService.getCompanySettings( user );
                if ( unitSettings.getContact_details() != null && unitSettings.getContact_details().getName() != null ) {
                    model.addAttribute( "name", unitSettings.getContact_details().getName() );
                }
                model.addAttribute( "title", unitSettings.getContact_details().getTitle() );
                model.addAttribute( "location", unitSettings.getContact_details().getLocation() );
                model.addAttribute( "vertical", unitSettings.getVertical() );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) ) {

                try {
                    columnValue = Long.parseLong( request.getParameter( "columnValue" ) );
                } catch ( NumberFormatException e ) {
                    LOG.error(
                        "NumberFormatException caught in getProfileDetails() while converting columnValue for regionId/branchId/agentId." );
                    throw e;
                }

                unitSettings = organizationManagementService.getRegionSettings( columnValue );
                if ( unitSettings.getContact_details() != null && unitSettings.getContact_details().getName() != null ) {
                    model.addAttribute( "name", unitSettings.getContact_details().getName() );
                }
                model.addAttribute( "title", unitSettings.getContact_details().getTitle() );
                model.addAttribute( "company", user.getCompany().getCompany() );
                model.addAttribute( "location", unitSettings.getContact_details().getLocation() );
                model.addAttribute( "vertical", unitSettings.getVertical() );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) ) {

                try {
                    columnValue = Long.parseLong( request.getParameter( "columnValue" ) );
                } catch ( NumberFormatException e ) {
                    LOG.error(
                        "NumberFormatException caught in getProfileDetails() while converting columnValue for regionId/branchId/agentId." );
                    throw e;
                }

                unitSettings = organizationManagementService.getBranchSettingsDefault( columnValue );
                if ( unitSettings.getContact_details() != null && unitSettings.getContact_details().getName() != null ) {
                    model.addAttribute( "name", unitSettings.getContact_details().getName() );
                }
                model.addAttribute( "title", unitSettings.getContact_details().getTitle() );
                model.addAttribute( "company", user.getCompany().getCompany() );
                model.addAttribute( "location", unitSettings.getContact_details().getLocation() );
                model.addAttribute( "vertical", unitSettings.getVertical() );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) ) {
                columnValue = user.getUserId();

                unitSettings = userManagementService.getUserSettings( columnValue );
                model.addAttribute( "name",
                    user.getFirstName() + " " + ( user.getLastName() != null ? user.getLastName() : "" ) );
                model.addAttribute( "title", unitSettings.getContact_details().getTitle() );
                model.addAttribute( "company", user.getCompany().getCompany() );
                model.addAttribute( "location", unitSettings.getContact_details().getLocation() );
                model.addAttribute( "vertical", unitSettings.getVertical() );
            } else if ( realtechAdminStr != null && !realtechAdminStr.isEmpty() ) {
                realtechAdmin = Boolean.parseBoolean( realtechAdminStr );
            }

            boolean allowOverrideForSocialMedia = false;
            boolean hiddenSection = false;
            //Code to determine if social media can be overridden during autologin and the value for hiddenSection
            if ( columnName.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
                allowOverrideForSocialMedia = unitSettings.isAllowOverrideForSocialMedia();
                hiddenSection = unitSettings.isHiddenSection();
            } else {
                OrganizationUnitSettings companySettings = organizationManagementService
                    .getCompanySettings( user.getCompany().getCompanyId() );
                allowOverrideForSocialMedia = companySettings.isAllowOverrideForSocialMedia();
                hiddenSection = companySettings.isHiddenSection();
            }
            model.addAttribute( "allowOverrideForSocialMedia", allowOverrideForSocialMedia );
            model.addAttribute( "hiddenSection", hiddenSection );

            model.addAttribute( "columnName", columnName );
            model.addAttribute( "columnValue", columnValue );

            LOG.info( "Method to get profile of company/region/branch/agent getProfileDetails() finished" );
            return JspResolver.REPORTING_PROFILE;
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "NonFatalException while fetching profile details. Reason :" + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( DisplayMessageConstants.GENERAL_ERROR, DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.REPORTING_PROFILE;
        }
    }

   
   @ResponseBody
   @RequestMapping( value = "/fetchreportingspsstats", method = RequestMethod.GET)
   public Response fetchSpsStats( Model model, HttpServletRequest request ) throws NonFatalException 
   {
        LOG.info( "Fetching Sps Stats Graph" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();
        Response response = null;

        if ( user == null ) {
            throw new NonFatalException( "NonFatalException while logging in. " );
        }    
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        response = ssApiIntergrationBuilder.getIntegrationApi().getReportingSpsStats( entityId, entityType );
        return response;
        
   }
   
   @ResponseBody
   @RequestMapping( value = "/fetchreportingcompletionrate", method = RequestMethod.GET)
   public Response fetchCompletionRate( Model model, HttpServletRequest request ) throws NonFatalException 
   {
        LOG.info( "Fetching Completion Rate Graph" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();
        Response response = null;

        if ( user == null ) {
            throw new NonFatalException( "NonFatalException while logging in. " );
        }    
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        response = ssApiIntergrationBuilder.getIntegrationApi().getReportingCompletionRateApi(entityId,entityType);
        return response;
        
   }
   
   @ResponseBody
   @RequestMapping( value = "/fetchmonthdataforoverview", method = RequestMethod.GET)
   public Response fetchMonthDataForOverview( Model model, HttpServletRequest request ) throws NonFatalException 
   {
        LOG.info( "Fetching Overview Based On Month" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();
        Response response = null;
        int month = 0;
        int year = 0;

        if ( user == null ) {
            throw new NonFatalException( "NonFatalException while logging in. " );
        }    
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        String month_string = request.getParameter( "month" );
        if(month_string != null && !month_string.isEmpty()){
           month = Integer.valueOf(month_string);
        }
        String year_string = request.getParameter( "year");
        if(year_string != null && !year_string.isEmpty()){
            year = Integer.valueOf(year_string);
        }
        if ( month == 0 || year == 0 ) {
            throw new NonFatalException( "NonFatalException while logging in. " );
        }else {
            response = ssApiIntergrationBuilder.getIntegrationApi().getMonthDataOverviewForDashboard(entityId,entityType, month, year);
        }
        return response;
        
   }
   
   @ResponseBody
   @RequestMapping( value = "/fetchyeardataforoverview", method = RequestMethod.GET)
   public Response fetchYearDataForOverview( Model model, HttpServletRequest request ) throws NonFatalException 
   {
        LOG.info( "Fetching Overview Based On Year" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();
        Response response = null;
        int year = 0;

        if ( user == null ) {
            throw new NonFatalException( "NonFatalException while logging in. " );
        }    
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        String year_string = request.getParameter( "year");
        if(year_string != null && !year_string.isEmpty()){
            year = Integer.valueOf(year_string);
        }
        if ( year == 0 ) {
            throw new NonFatalException( "NonFatalException while logging in. " );
        }else {
            response = ssApiIntergrationBuilder.getIntegrationApi().getYearDataOverviewForDashboard(entityId,entityType,year);
        }
        return response;
        
   }
   
   /*
    * Generate Reports For the reporting UI
    */
   @SuppressWarnings ( "unused")
   @ResponseBody
   @RequestMapping( value = "/savereportingdata", method = RequestMethod.POST)
   public String saveReportingData( Model model, HttpServletRequest request, HttpServletResponse response ) throws NonFatalException, ParseException,FileNotFoundException, IOException{
       LOG.info( "the step to generate reporting reports :generateReportingReports started " );
       HttpSession session = request.getSession( false );
       User user = sessionHelper.getCurrentUser();
       Long adminUserid = user.getUserId();
       String message = "";
       //since we need to store the current time stamp
       
       if ( user == null ) {
           throw new NonFatalException( "NonFatalException while logging in. " );
       } 
       Date startDate = null;
       String startDateStr = request.getParameter( "startDate" );
       if ( startDateStr != null && !startDateStr.isEmpty() ) {
           startDate = new SimpleDateFormat("MM/dd/yyyy").parse( startDateStr );
       }
       Date endDate = null;
       String endDateStr = request.getParameter( "endDate" );
       if( endDateStr != null && !endDateStr.isEmpty()){
           endDate =  new SimpleDateFormat("MM/dd/yyyy").parse( endDateStr ) ;
       }
       String reportIdString = request.getParameter( "reportId" );
       int reportId = Integer.parseInt( reportIdString );
       long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
       String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
       Company company = user.getCompany();
       reportingDashboardManagement.createEntryInFileUploadForReporting( reportId, startDate, endDate,entityId, entityType ,company , adminUserid );
       message = "The report is being generated";
       return message;
       
   }
   
   /*
    * Method to get count of all the recent activities
    */
   @ResponseBody
   @RequestMapping ( value = "/fetchrecentactivitiescount")
   public String getIncompleteSurveyCount( Model model, HttpServletRequest request )
   {
       LOG.info( "Method to get recent activities of reports" );
       long count = 0l;
       HttpSession session = request.getSession( false );
       long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
       String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
       count = reportingDashboardManagement.getRecentActivityCount( entityId, entityType );

       LOG.info( "Method to get reviews of company, region, branch, agent getReviews() finished." );
       return String.valueOf( count );
   }
   
   @ResponseBody
   @RequestMapping( value = "/fetchrecentactivities", method = RequestMethod.GET)
   public Response fetchRecentActivity( Model model, HttpServletRequest request ) throws NonFatalException 
   {
        LOG.info( "Fetching Recent Activity Graph" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();
        Response response = null;

        if ( user == null ) {
            throw new NonFatalException( "NonFatalException while logging in. " );
        }   
        int startIndex = 0;
        int batchSize = 0;
        String startIndexStr = request.getParameter( "startIndex" );
        String batchSizeStr = request.getParameter( "batchSize" );
        if(startIndexStr!=null && !startIndexStr.isEmpty()){
            startIndex = Integer.parseInt( startIndexStr );
        }
        if(batchSizeStr != null && !batchSizeStr.isEmpty()){
            batchSize = Integer.parseInt( batchSizeStr );
        }
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        response = ssApiIntergrationBuilder.getIntegrationApi().getRecentActivity(entityId,entityType, startIndex, batchSize);
        return response;
        
   }
   
   @ResponseBody
   @RequestMapping( value = "/deletefromrecentactivities", method = RequestMethod.POST)
   public String deleteFromRecentActivity( HttpServletRequest request ) 
   {
       String message = "The row is deleted from the recentActivity and will not be displayed again";
        try{
            LOG.info( "Fetching Recent Activity Graph" );
            long fileUploadId = 0;
            String fileUploadIdStr = request.getParameter( "fileUploadId" );
            if(fileUploadIdStr != null && !fileUploadIdStr.isEmpty()){
                fileUploadId = Integer.parseInt( fileUploadIdStr );
            }else{
                message = "The row Id was null or an empty string"; 
            }
            reportingDashboardManagement.deleteRecentActivity( fileUploadId );
            return message;
        }catch(Exception e){
            message = "There was an exception :"+e ;
            return message;
        }
        
   }
   
   //TO SHOW REPORTING UI
   @RequestMapping ( value = "/showreportspage", method = RequestMethod.GET)
   public String showReportsPage( Model model, HttpServletRequest request ) throws NonFatalException
   {
       LOG.info( "Showing reports page" );
       HttpSession session = request.getSession( false );
       User user = sessionHelper.getCurrentUser();
       if ( user == null ) {
           throw new NonFatalException( "NonFatalException while logging in. " );
       }
       long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
       String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
       Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
       boolean modelSet = false;
       if ( user.getCompany() != null && user.getCompany().getLicenseDetails() != null
           && !user.getCompany().getLicenseDetails().isEmpty()
           && user.getCompany().getLicenseDetails().get( 0 ).getAccountsMaster() != null ) {
           if ( user.getCompany().getLicenseDetails().get( 0 ).getAccountsMaster()
               .getAccountsMasterId() == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
               model.addAttribute( "columnName", CommonConstants.AGENT_ID_COLUMN );
               model.addAttribute( "columnValue", entityId );
               modelSet = true;
           }
       }
       String profileName = "";
       boolean isPasswordSet = true;
       if ( user.getIsForcePassword() == 1 && user.getLoginPassword() == null ) {
           isPasswordSet = false;
       }
       if ( !modelSet ) {
           if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
               model.addAttribute( "columnName", entityType );
               model.addAttribute( "columnValue", entityId );
           } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
               model.addAttribute( "columnName", entityType );
               model.addAttribute( "columnValue", entityId );
           } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
               model.addAttribute( "columnName", entityType );
               model.addAttribute( "columnValue", entityId );
           } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
               model.addAttribute( "columnName", CommonConstants.AGENT_ID_COLUMN );
               model.addAttribute( "columnValue", entityId );
           }
       }
       return JspResolver.REPORTS;
   }
   
   //Ranking Settings Page
   @RequestMapping ( value = "/showrankingsettings", method = RequestMethod.GET)
   public String showRankingPage( Model model, HttpServletRequest request ) throws NonFatalException
   {
       return JspResolver.RANKING_SETTINGS;
   }
   
   @ResponseBody
   @RequestMapping ( value = "/getuserrankingrankandcount", method = RequestMethod.GET)
   public Response getUserRankingRankAndCount(Model model, HttpServletRequest request) throws NonFatalException
   {     
       LOG.info( "Get User Ranking Rank And Count" );
       
       LOG.info( "Method to get reviews of company, region, branch, agent getReviews() started." );
       List<List<Object>> userRankingList = new ArrayList<>();
       User user = sessionHelper.getCurrentUser();
       Long userId = user.getUserId();
       Integer batchSize = 0;
       int timeFrame = 1;
       Long entityId = (long) 0;
       int year = 0;
       int month = 0;
       String batchSizeStr = request.getParameter( "batchSize" );
       String entityIdStr = request.getParameter("entityId");
       String entityType = request.getParameter("entityType");
       String timeFrameStr = request.getParameter("timeFrame");
       String yearStr = request.getParameter("year");
       String monthStr = request.getParameter("month");
       Response response = null;
       if(batchSizeStr != null && !batchSizeStr.isEmpty()){
           batchSize = Integer.parseInt( batchSizeStr );
       }
       if ( ( entityType == null || entityType.isEmpty() ) ) {
           LOG.error( "Invalid value (null/empty) passed for profile level." );
           throw new InvalidInputException( "Invalid value (null/empty) passed for profile level." );
       }
       if(timeFrameStr!=null && !timeFrameStr.isEmpty()){
           timeFrame = Integer.parseInt(timeFrameStr);
       }
       if ( entityIdStr != null && !entityIdStr.isEmpty() ) {
           try {
               entityId = Long.parseLong( entityIdStr );
           } catch ( NumberFormatException e ) {
               LOG.error( "NumberFormatException caught while parsing columnValue in getReviews(). Nested exception is ",
                   e );
               throw e;
           }
       }
       if(yearStr != null && !yearStr.isEmpty()){
           year = Integer.parseInt( yearStr );
       }
       if(monthStr != null && !monthStr.isEmpty()){
           month = Integer.parseInt( monthStr );
       }
       switch(timeFrame){
           case 1: response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingRankCountForThisYear(userId , entityId, entityType, year,batchSize);
               break;
           case 2: response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingRankCountForThisMonth(userId , entityId, entityType, month,year,batchSize);
               break;
           case 3:  response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingRankCountForPastYear(userId , entityId, entityType, year,batchSize);
               break;
           case 4: response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingRankCountForPastMonth(userId ,entityId, entityType, month,year,batchSize);
                   break;
           default: throw new NonFatalException( "NonFatalException while getting User Ranking Count" );
      }
      
    return response;
   }
   
   @ResponseBody
   @RequestMapping ( value = "/getuserrankingcount", method = RequestMethod.GET)
   public Response getUserRankingCount(Model model, HttpServletRequest request) throws NonFatalException
   {     
       LOG.info( "Get User Ranking Rank " );
       
       LOG.info( "Method to get reviews of company, region, branch, agent getReviews() started." );
       Integer batchSize = 0;
       int timeFrame = 1;
       Long entityId = (long) 0;
       int year = 0;
       int month = 0;
       String batchSizeStr = request.getParameter( "batchSize" );
       String entityIdStr = request.getParameter("entityId");
       String entityType = request.getParameter("entityType");
       String timeFrameStr = request.getParameter("timeFrame");
       String yearStr = request.getParameter("year");
       String monthStr = request.getParameter("month");
       Response response = null;
       if(batchSizeStr != null && !batchSizeStr.isEmpty()){
           batchSize = Integer.parseInt( batchSizeStr );
       }
       if ( ( entityType == null || entityType.isEmpty() ) ) {
           LOG.error( "Invalid value (null/empty) passed for profile level." );
           throw new InvalidInputException( "Invalid value (null/empty) passed for profile level." );
       }
       if(timeFrameStr!=null && !timeFrameStr.isEmpty()){
           timeFrame = Integer.parseInt(timeFrameStr);
       }
       if ( entityIdStr != null && !entityIdStr.isEmpty() ) {
           try {
               entityId = Long.parseLong( entityIdStr );
           } catch ( NumberFormatException e ) {
               LOG.error( "NumberFormatException caught while parsing columnValue in getReviews(). Nested exception is ",
                   e );
               throw e;
           }
       }
       if(yearStr != null && !yearStr.isEmpty()){
           year = Integer.parseInt( yearStr );
       }
       if(monthStr != null && !monthStr.isEmpty()){
           month = Integer.parseInt( monthStr );
       }
       switch(timeFrame){
           case 1: response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingCountForThisYear(entityId, entityType, year,batchSize);
               break;
           case 2: response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingCountForThisMonth(entityId, entityType, month,year,batchSize);
               break;
           case 3:  response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingCountForPastYear(entityId, entityType, year,batchSize);
               break;
           case 4: response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingCountForPastMonth(entityId, entityType, month,year,batchSize);
                   break;
           default: throw new NonFatalException( "NonFatalException while getting User Ranking Count" );
      }
      
    return response;
   }
   
   @ResponseBody
   @RequestMapping ( value = "/getuserranking", method = RequestMethod.GET)
   public Response getUserRanking(Model model, HttpServletRequest request) throws NonFatalException
   {     
       LOG.info( "Get User Ranking for this year" );
       
       LOG.info( "Method to get reviews of company, region, branch, agent getReviews() started." );
       List<List<Object>> userRankingList = new ArrayList<>();
       User user = sessionHelper.getCurrentUser();
       
       String entityType= request.getParameter(CommonConstants.ENTITY_TYPE_COLUMN);
       String entityIdStr= request.getParameter(CommonConstants.ENTITY_ID_COLUMN);
       String timeFrameStr = request.getParameter("timeFrame");
       String startIndexStr = request.getParameter("startIndex");
       String batchSizeStr = request.getParameter("batchSize");
       String yearStr= request.getParameter("year");
       String monthStr="";
       int startIndex = 0;
       int batchSize = 11;
       int timeFrame = 1;
       long entityId = 0;
       
       @SuppressWarnings("deprecation")
       int year= (new Date()).getYear();
       @SuppressWarnings("deprecation")
       int month = (new Date()).getMonth()+1;
       Response response =null;
       
       if(startIndexStr!=null && !startIndexStr.isEmpty()){
           startIndex = Integer.parseInt( startIndexStr );
       }
       if(batchSizeStr != null && !batchSizeStr.isEmpty()){
           batchSize = Integer.parseInt( batchSizeStr );
       }
       if(timeFrameStr!=null && !timeFrameStr.isEmpty()){
           timeFrame = Integer.parseInt(timeFrameStr);
       }
       if(entityIdStr!=null && !entityIdStr.isEmpty()){
           entityId = Long.parseLong(entityIdStr);
       }
       if(yearStr!=null && !yearStr.isEmpty()){
           year = Integer.parseInt(yearStr);
       }
       
       switch(timeFrame){
            case 1: response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingForThisYear(entityId, entityType, year,startIndex,batchSize);
                break;
            case 2: 
                monthStr = request.getParameter("month");
                month = Integer.parseInt(monthStr);
                response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingForThisMonth(entityId, entityType, month,year,startIndex,batchSize);
                break;
            case 3:  response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingForPastYear(entityId, entityType, year,startIndex,batchSize);
                break;
            case 4: monthStr = request.getParameter("month");
                    month = Integer.parseInt(monthStr);
                    response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingForPastMonth(entityId, entityType, month,year,startIndex,batchSize);
                    break;
            default: throw new NonFatalException( "NonFatalException while choosing time frame for leaderboard" );
       }
       
       return response;
   }
   
   @ResponseBody
   @RequestMapping ( value = "/getuserprofimageforleaderboard", method = RequestMethod.GET)
   public String getUserProfileImageForLeaderboard(Model model, HttpServletRequest request) throws NonFatalException
   {
	   String json=null;
	   
	   Map<String,String> profileImageMap = null;
	   String userIdStr = request.getParameter("userId");
	   String profileImageUrl = "";
	   long userId=01;
	   if(userIdStr!=null && !userIdStr.isEmpty()){
		   userId = Long.parseLong(userIdStr);
		   try {
				// get details from mongo
				AgentSettings agentSettings = userManagementService.getUserSettings(userId);
				if (agentSettings != null) {
					profileImageUrl = (agentSettings.getProfileImageUrlThumbnail() != null ? agentSettings.getProfileImageUrlThumbnail()
							: agentSettings.getProfileImageUrl());
				}
			}
			catch (InvalidInputException e) {
				LOG.error("Error occurred while fetching details of agent. Error is : " + e);
				return json;
			}
	   }
	   json= new Gson().toJson(profileImageUrl);
	   return json;
   }
    /**
    *
    * @param model
    * @param entitySetting
    * @throws InvalidInputException
    */
   private void setSettingSetByEntityInModel( Model model, OrganizationUnitSettings entitySetting )
       throws InvalidInputException
   {
       LOG.debug( "method setSettingSetByEntityInModel() started " );
       boolean isLogoSetByEntity;
       boolean isContactNoSetByEntity;
       boolean isWebAddressSetByEntity;
       boolean isWorkEmailSetByEntity;

       if ( entitySetting == null ) {
           throw new InvalidInputException( "Passed entity setting is null" );
       }

       if ( entitySetting.getLogo() == null || entitySetting.getLogo().isEmpty() ) {
           isLogoSetByEntity = false;
       } else {
           isLogoSetByEntity = true;
       }

       if ( entitySetting.getContact_details() != null && entitySetting.getContact_details().getWeb_addresses() != null
           && entitySetting.getContact_details().getWeb_addresses().getWork() != null
           && !entitySetting.getContact_details().getWeb_addresses().getWork().isEmpty() ) {
           isWebAddressSetByEntity = true;
       } else {
           isWebAddressSetByEntity = false;
       }

       if ( entitySetting.getContact_details() != null && entitySetting.getContact_details().getContact_numbers() != null
           && entitySetting.getContact_details().getContact_numbers().getWork() != null
           && !entitySetting.getContact_details().getContact_numbers().getWork().isEmpty() ) {
           isContactNoSetByEntity = true;
       } else {
           isContactNoSetByEntity = false;
       }

       if ( entitySetting.getContact_details() != null && entitySetting.getContact_details().getMail_ids() != null
           && entitySetting.getContact_details().getMail_ids().getWork() != null
           && !entitySetting.getContact_details().getMail_ids().getWork().isEmpty() ) {
           isWorkEmailSetByEntity = true;
       } else {
           isWorkEmailSetByEntity = false;
       }

       model.addAttribute( "isLogoSetByEntity", isLogoSetByEntity );
       model.addAttribute( "isWebAddressSetByEntity", isWebAddressSetByEntity );
       model.addAttribute( "isContactNoSetByEntity", isContactNoSetByEntity );
       model.addAttribute( "isWorkEmailSetByEntity", isWorkEmailSetByEntity );

       LOG.debug( "method setSettingSetByEntityInModel() ended " );
   }
}


