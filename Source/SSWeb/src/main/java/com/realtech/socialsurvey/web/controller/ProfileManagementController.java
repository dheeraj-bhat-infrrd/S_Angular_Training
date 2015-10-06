package com.realtech.socialsurvey.web.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.QueryParam;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import sun.misc.BASE64Decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.SocialPostDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.Achievement;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Association;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyPositions;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.DisplayMessage;
import com.realtech.socialsurvey.core.entities.FacebookToken;
import com.realtech.socialsurvey.core.entities.GoogleToken;
import com.realtech.socialsurvey.core.entities.LendingTreeToken;
import com.realtech.socialsurvey.core.entities.Licenses;
import com.realtech.socialsurvey.core.entities.LinkedInToken;
import com.realtech.socialsurvey.core.entities.LockSettings;
import com.realtech.socialsurvey.core.entities.MailIdSettings;
import com.realtech.socialsurvey.core.entities.MiscValues;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProListUser;
import com.realtech.socialsurvey.core.entities.ProfileStage;
import com.realtech.socialsurvey.core.entities.RealtorToken;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SettingsDetails;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SocialUpdateAction;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.TwitterToken;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.entities.UserHierarchyAssignments;
import com.realtech.socialsurvey.core.entities.UserListFromSearch;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.entities.WebAddressSettings;
import com.realtech.socialsurvey.core.entities.YelpToken;
import com.realtech.socialsurvey.core.entities.ZillowToken;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.ProfileServiceErrorCode;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsLocker;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsManager;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsSetter;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.services.upload.impl.UploadUtils;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.core.utils.UrlValidationHelper;
import com.realtech.socialsurvey.web.common.ErrorCodes;
import com.realtech.socialsurvey.web.common.ErrorResponse;
import com.realtech.socialsurvey.web.common.JspResolver;
import com.realtech.socialsurvey.web.util.BotRequestUtils;


@Controller
public class ProfileManagementController
{

    private static final Logger LOG = LoggerFactory.getLogger( ProfileManagementController.class );

    // JIRA SS-97 by RM-06 : BOC
    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private UploadUtils uploadUtils;

    @Autowired
    private SessionHelper sessionHelper;

    @Autowired
    private UrlValidationHelper urlValidationHelper;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private SolrSearchService solrSearchService;

    @Autowired
    private SocialManagementService socialManagementService;

    @Autowired
    private Utils utils;

    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;

    @Value ( "${CDN_PATH}")
    private String amazonEndpoint;

    @Value ( "${AMAZON_IMAGE_BUCKET}")
    private String amazonImageBucket;

    @Value ( "${AMAZON_LOGO_BUCKET}")
    private String amazonLogoBucket;

    @Autowired
    private BotRequestUtils botRequestUtils;

    @Autowired
    private SettingsSetter settingsSetter;

    @Autowired
    private SettingsLocker settingsLocker;

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private SocialPostDao socialPostDao;
    @Transactional
    @RequestMapping ( value = "/showprofilepage", method = RequestMethod.GET)
    public String showProfileEditPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Method showProfileEditPage() called from ProfileManagementService" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();
        AccountType accountType = (AccountType) session.getAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION );
        UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
        long entityId = 0;
        String entityIdStr = request.getParameter( "entityId" );
        if ( entityIdStr == null || entityIdStr.isEmpty() ) {
            entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        } else {
            try {
                if ( entityIdStr != null && !entityIdStr.equals( "" ) ) {
                    entityId = Long.parseLong( entityIdStr );
                } else {
                    throw new NumberFormatException();
                }
            } catch ( NumberFormatException e ) {
                LOG.error( "Number format exception occurred while parsing the entity id. Reason :" + e.getMessage(), e );
            }
        }

        String entityType = request.getParameter( "entityType" );
        if ( entityType == null || entityType.isEmpty() ) {
            entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        }

        long branchId = 0;
        long regionId = 0;
        long companyId = 0;
        long agentId = 0;
        int profilesMaster = 0;
        List<SettingsDetails> settingsDetailsList = null;
        OrganizationUnitSettings profileSettings = null;
        Map<SettingsForApplication, OrganizationUnit> map = null;
        //Get the hierarchy details associated with the current profile
        try {
            Map<String, Long> hierarchyDetails = profileManagementService.getHierarchyDetailsByEntity( entityType, entityId );
            if ( hierarchyDetails == null ) {
                LOG.error( "Unable to fetch primary profile for this user " );
                throw new FatalException( "Unable to fetch primary profile for type : " + entityType + " and ID : " + entityId );
            }
            branchId = hierarchyDetails.get( CommonConstants.BRANCH_ID_COLUMN );
            regionId = hierarchyDetails.get( CommonConstants.REGION_ID_COLUMN );
            companyId = hierarchyDetails.get( CommonConstants.COMPANY_ID_COLUMN );
            agentId = hierarchyDetails.get( CommonConstants.AGENT_ID_COLUMN );
            settingsDetailsList = settingsManager.getScoreForCompleteHeirarchy( companyId, branchId, regionId );
            LOG.debug( "Company ID : " + companyId + " Region ID : " + regionId + " Branch ID : " + branchId + " Agent ID : "
                + agentId );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException while showing profile page. Reason :" + e.getMessage(), e );
            model
                .addAttribute( "message", messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }  catch (ProfileNotFoundException e) {
       	 	LOG.error( "No profile found for the user " , e );
			return JspResolver.NO_PROFILES_FOUND;
		}

        sessionHelper.updateSelectedProfile( session, entityId, entityType );

        // fetching details from profile
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            //If the profile is a company profile
            model.addAttribute( "columnName", entityType );
            profilesMaster = CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID;
            OrganizationUnitSettings companyProfile = null;
            try {
                companyProfile = organizationManagementService.getCompanySettings( companyId );
                String json = new Gson().toJson( companyProfile );
                model.addAttribute( "profileJson", json );
                double averageRating = profileManagementService.getAverageRatings( companyId,
                    CommonConstants.PROFILE_LEVEL_COMPANY, false );
                model.addAttribute( "averageRating", averageRating );

                long reviewsCount = profileManagementService.getReviewsCount( companyId, CommonConstants.MIN_RATING_SCORE,
                    CommonConstants.MAX_RATING_SCORE, CommonConstants.PROFILE_LEVEL_COMPANY, false );
                model.addAttribute( "reviewsCount", reviewsCount );
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_COMPANY_PROFILE_SERVICE_FAILURE, CommonConstants.SERVICE_CODE_COMPANY_PROFILE,
                    "Error occured while fetching company profile" ), e.getMessage() );
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

                try {
                    map = profileManagementService.getPrimaryHierarchyByEntity( CommonConstants.REGION_ID,
                        regionProfile.getIden() );
                    if ( map == null ) {
                        LOG.error( "Unable to fetch primary profile for this user " );
                        throw new FatalException( "Unable to fetch primary profile this user " + regionProfile.getIden() );
                    }
                } catch ( InvalidSettingsStateException e ) {
                    throw new InternalServerException( new ProfileServiceErrorCode(
                        CommonConstants.ERROR_CODE_REGION_PROFILE_SERVICE_FAILURE, CommonConstants.SERVICE_CODE_REGION_PROFILE,
                        "Error occured while fetching region profile" ), e.getMessage() );
                }  catch (ProfileNotFoundException e) {
                	LOG.error( "No profile found for the user " , e );
					return JspResolver.NO_PROFILES_FOUND;
				}

                regionProfile = profileManagementService.fillUnitSettings( regionProfile,
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, companyProfile, regionProfile, null, null,
                    map );

                // aggregated disclaimer
                String disclaimer = profileManagementService.aggregateDisclaimer( regionProfile, CommonConstants.REGION_ID );
                regionProfile.setDisclaimer( disclaimer );

                String json = new Gson().toJson( regionProfile );
                model.addAttribute( "profileJson", json );

                double averageRating = profileManagementService.getAverageRatings( regionId,
                    CommonConstants.PROFILE_LEVEL_REGION, false );
                model.addAttribute( "averageRating", averageRating );

                long reviewsCount = profileManagementService.getReviewsCount( regionId, CommonConstants.MIN_RATING_SCORE,
                    CommonConstants.MAX_RATING_SCORE, CommonConstants.PROFILE_LEVEL_REGION, false );
                model.addAttribute( "reviewsCount", reviewsCount );
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_REGION_PROFILE_SERVICE_FAILURE, CommonConstants.SERVICE_CODE_REGION_PROFILE,
                    "Error occured while fetching region profile" ), e.getMessage() );
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

                try {
                    map = profileManagementService.getPrimaryHierarchyByEntity( CommonConstants.BRANCH_ID_COLUMN,
                        branchProfile.getIden() );
                    if ( map == null ) {
                        LOG.error( "Unable to fetch primary profile for this user " );
                        throw new FatalException( "Unable to fetch primary profile this user " + branchProfile.getIden() );
                    }

                } catch ( InvalidSettingsStateException e ) {
                    throw new InternalServerException( new ProfileServiceErrorCode(
                        CommonConstants.ERROR_CODE_BRANCH_PROFILE_SERVICE_FAILURE, CommonConstants.SERVICE_CODE_BRANCH_PROFILE,
                        "Error occured while fetching branch profile" ), e.getMessage() );
                } catch (ProfileNotFoundException e) {
                	 LOG.error( "No profile found for the user " , e );
 					return JspResolver.NO_PROFILES_FOUND;
				}
                branchProfile = profileManagementService.fillUnitSettings( branchProfile,
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, companyProfile, regionProfile,
                    branchProfile, null, map );
                // aggregated disclaimer
                String disclaimer = profileManagementService.aggregateDisclaimer( branchProfile, CommonConstants.BRANCH_ID );
                branchProfile.setDisclaimer( disclaimer );

                String json = new Gson().toJson( branchProfile );
                model.addAttribute( "profileJson", json );

                double averageRating = profileManagementService.getAverageRatings( branchId,
                    CommonConstants.PROFILE_LEVEL_BRANCH, false );
                model.addAttribute( "averageRating", averageRating );

                long reviewsCount = profileManagementService.getReviewsCount( branchId, CommonConstants.MIN_RATING_SCORE,
                    CommonConstants.MAX_RATING_SCORE, CommonConstants.PROFILE_LEVEL_BRANCH, false );
                model.addAttribute( "reviewsCount", reviewsCount );
            } catch ( InvalidInputException e ) {
                throw new InternalServerException( new ProfileServiceErrorCode(
                    CommonConstants.ERROR_CODE_BRANCH_PROFILE_SERVICE_FAILURE, CommonConstants.SERVICE_CODE_BRANCH_PROFILE,
                    "Error occured while fetching branch profile" ), e.getMessage() );
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

                try {
                    map = profileManagementService.getPrimaryHierarchyByEntity( CommonConstants.AGENT_ID_COLUMN,
                        individualProfile.getIden() );
                    if ( map == null ) {
                        LOG.error( "Unable to fetch primary profile for this user " );
                        throw new FatalException( "Unable to fetch primary profile this user " + branchProfile.getIden() );
                    }

                } catch ( InvalidSettingsStateException e ) {
                    LOG.error( "Error occured while fetching branch profile" + e.getMessage() );
                } catch (ProfileNotFoundException e) {
                	 LOG.error( "No profile found for the user " );
					return JspResolver.NO_PROFILES_FOUND;
				}

                if ( map == null ) {
                    LOG.error( "Unable to fetch primary profile for this user " );
                    throw new FatalException( "Unable to fetch primary profile this user " + individualProfile.getIden() );
                }

                individualProfile = (AgentSettings) profileManagementService.fillUnitSettings( individualProfile,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, companyProfile, regionProfile,
                    branchProfile, individualProfile, map );
                individualProfile.setVertical( user.getCompany().getVerticalsMaster().getVerticalName() );
                String disclaimer = profileManagementService.aggregateDisclaimer( individualProfile, CommonConstants.AGENT_ID );
                individualProfile.setDisclaimer( disclaimer );

                String json = new Gson().toJson( individualProfile );
                model.addAttribute( "profileJson", json );

                double averageRating = profileManagementService.getAverageRatings( agentId,
                    CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false );
                model.addAttribute( "averageRating", averageRating );
                long reviewsCount = profileManagementService.getReviewsCount( agentId, CommonConstants.MIN_RATING_SCORE,
                    CommonConstants.MAX_RATING_SCORE, CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false );
                model.addAttribute( "reviewsCount", reviewsCount );

                profileSettings = individualProfile;
            } catch ( InvalidInputException e ) {
                LOG.error( "InvalidInputException: message : " + e.getMessage(), e );
                model.addAttribute(
                    "message",
                    messageUtils.getDisplayMessage( DisplayMessageConstants.INVALID_INDIVIDUAL_PROFILENAME,
                        DisplayMessageType.ERROR_MESSAGE ).getMessage() );
                return JspResolver.NOT_FOUND_PAGE;
            } catch ( NoRecordsFetchedException e ) {
                LOG.error( "NoRecordsFetchedException: message : " + e.getMessage(), e );
            }

        }


        model.addAttribute( "profileSettings", profileSettings );
        session.setAttribute( CommonConstants.USER_PROFILE_SETTINGS, profileSettings );

        //Setting parentLock in session
        /*LockSettings parentLock = fetchParentLockSettings( model, user, accountType, userSettings, branchId, regionId,
            profilesMaster );*/

        LockSettings parentLock = null;
        try {
            parentLock = profileManagementService.fetchHierarchyLockSettings( companyId, branchId, regionId, entityType );
        } catch ( NonFatalException e ) {
            LOG.error( "Unable to fetch lock values", e );
        }
        session.setAttribute( CommonConstants.PARENT_LOCK, parentLock );


        LOG.info( "Method showProfileEditPage() finished from ProfileManagementService" );
        return JspResolver.PROFILE_EDIT;
    }


    @ResponseBody
    @RequestMapping ( value = "/fetchverticalsmaster", method = RequestMethod.GET)
    public String fetchVerticalsMaster( Model model, HttpServletRequest request )
    {
        LOG.info( "Fetching profile aboutme" );
        String response = null;
        try {
            try {
                List<VerticalsMaster> verticalsMasterList = organizationManagementService.getAllVerticalsMaster();

                List<String> verticalsMap = new ArrayList<>();
                for ( VerticalsMaster verticalsMaster : verticalsMasterList ) {
                    verticalsMap.add( verticalsMaster.getVerticalName() );
                }
                response = new Gson().toJson( verticalsMap );
            } catch ( InvalidInputException e ) {
                throw new NonFatalException( "Some error occurred while fetching verticals master", e );
            }
        } catch ( NonFatalException e ) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrCode( ErrorCodes.REQUEST_FAILED );
            errorResponse.setErrMessage( e.getMessage() );
            response = new Gson().toJson( errorResponse );
        }
        return response;
    }


    private OrganizationUnitSettings fetchUserProfile( Model model, User user, AccountType accountType, UserSettings settings,
        long branchId, long regionId, int profilesMaster )
    {
        LOG.debug( "Method fetchUserProfile() called from ProfileManagementService" );
        OrganizationUnitSettings profile = null;
        try {
            profile = profileManagementService.aggregateUserProfile( user, accountType, settings, branchId, regionId,
                profilesMaster );
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "InvalidInputException while fetching profile. Reason :" + e.getMessage(), e );
            model
                .addAttribute( "message", messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.debug( "Method fetchUserProfile() finished from ProfileManagementService" );
        return profile;
    }


    private LockSettings fetchParentLockSettings( Model model, User user, AccountType accountType, UserSettings settings,
        long branchId, long regionId, int profilesMaster )
    {
        LOG.debug( "Method fetchParentLockSettings() called from ProfileManagementService" );
        LockSettings parentLock = null;
        try {
            parentLock = profileManagementService.aggregateParentLockSettings( user, accountType, settings, branchId, regionId,
                profilesMaster );
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "InvalidInputException while fetching profile. Reason :" + e.getMessage(), e );
            model
                .addAttribute( "message", messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.debug( "Method fetchParentLockSettings() finished from ProfileManagementService" );
        return parentLock;
    }


    @RequestMapping ( value = "/fetchaboutme", method = RequestMethod.GET)
    public String fetchProfileAboutMe( Model model, HttpServletRequest request )
    {
        LOG.info( "Fetching profile aboutme" );
        return JspResolver.PROFILE_ABOUT_ME;
    }


    @RequestMapping ( value = "/fetchcontactdetails", method = RequestMethod.GET)
    public String fetchContactDetails( Model model, HttpServletRequest request )
    {
        LOG.info( "Fetching contact details for profile" );
        return JspResolver.PROFILE_CONTACT_DETAILS;
    }


    @RequestMapping ( value = "/fetchbasicdetails", method = RequestMethod.GET)
    public String fetchBasicDetails( Model model, HttpServletRequest request )
    {
        LOG.info( "Fetching Basic details for profile" );
        return JspResolver.PROFILE_BASIC_DETAILS;
    }


    @RequestMapping ( value = "/fetchaddressdetails", method = RequestMethod.GET)
    public String fetchAddressDetails( Model model, HttpServletRequest request )
    {
        LOG.info( "Fetching Address details for profile" );
        return JspResolver.PROFILE_ADDRESS_DETAILS;
    }


    @RequestMapping ( value = "/fetchaddressdetailsedit", method = RequestMethod.GET)
    public String fetchAddressDetailsEdit( Model model, HttpServletRequest request )
    {
        LOG.info( "Fetching Address details for Edit for profile" );
        return JspResolver.PROFILE_ADDRESS_DETAILS_EDIT;
    }


    @RequestMapping ( value = "/fetchprofileimage", method = RequestMethod.GET)
    public String fetchProfileImage( Model model, HttpServletRequest request )
    {
        LOG.info( "Fetching profile image" );
        return JspResolver.PROFILE_IMAGE;
    }


    @RequestMapping ( value = "/fetchprofilelogo", method = RequestMethod.GET)
    public String fetchProfileLogo( Model model, HttpServletRequest request )
    {
        LOG.info( "Fetching profile logo" );
        return JspResolver.PROFILE_LOGO;
    }


    /**
     * Method to update profile lock settings
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/updatelocksettings", method = RequestMethod.POST)
    public String updateLockSettings( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateLockSettings() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        LockSettings lockSettings = null;

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            LockSettings parentLock = (LockSettings) session.getAttribute( CommonConstants.PARENT_LOCK );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            // Get the profile address parameters
            String fieldId = request.getParameter( "id" );
            boolean fieldState = Boolean.parseBoolean( request.getParameter( "state" ) );
            if ( fieldId == null || fieldId.isEmpty() ) {
                throw new InvalidInputException( "Name passed can not be null or empty", DisplayMessageConstants.GENERAL_ERROR );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                lockSettings = companySettings.getLockSettings();
                lockSettings = updateLockSettings( lockSettings, parentLock, fieldId, fieldState );
                lockSettings = profileManagementService.updateLockSettings(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, lockSettings );
                companySettings.setLockSettings( lockSettings );
                userSettings.setCompanySettings( companySettings );
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    if ( fieldId.equalsIgnoreCase( "prof-logo-lock" ) ) {
                        if ( fieldState ) {
                            settingsLocker.lockSettingsValueForCompany( company, SettingsForApplication.LOGO, true );
                        } else {
                            settingsLocker.lockSettingsValueForCompany( company, SettingsForApplication.LOGO, false );
                        }
                    }
                    if ( fieldId.equalsIgnoreCase( "phone-number-work-lock" ) ) {
                        if ( fieldState ) {
                            settingsLocker.lockSettingsValueForCompany( company, SettingsForApplication.PHONE, true );
                        } else {
                            settingsLocker.lockSettingsValueForCompany( company, SettingsForApplication.PHONE, false );
                        }
                    }
                    if ( fieldId.equalsIgnoreCase( "web-address-work-lock" ) ) {
                        if ( fieldState ) {
                            settingsLocker.lockSettingsValueForCompany( company, SettingsForApplication.WEB_ADDRESS_WORK, true );
                        } else {
                            settingsLocker
                                .lockSettingsValueForCompany( company, SettingsForApplication.WEB_ADDRESS_WORK, false );
                        }
                    }
                 
                    if ( fieldId.equalsIgnoreCase( "aboutme-lock" ) ) {
                        if ( fieldState ) {
                            settingsLocker.lockSettingsValueForCompany( company, SettingsForApplication.ABOUT_ME, true );
                        } else {
                            settingsLocker.lockSettingsValueForCompany( company, SettingsForApplication.ABOUT_ME, false );
                        }
                    }
                    userManagementService.updateCompany( company );
                }
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                lockSettings = regionSettings.getLockSettings();
                lockSettings = updateLockSettings( lockSettings, parentLock, fieldId, fieldState );
                lockSettings = profileManagementService.updateLockSettings(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, lockSettings );
                regionSettings.setLockSettings( lockSettings );
                userSettings.getRegionSettings().put( entityId, regionSettings );
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    if ( fieldId.equalsIgnoreCase( "prof-logo-lock" ) ) {
                        if ( fieldState ) {
                            settingsLocker.lockSettingsValueForRegion( region, SettingsForApplication.LOGO, true );
                        } else {
                            settingsLocker.lockSettingsValueForRegion( region, SettingsForApplication.LOGO, false );
                        }
                    }
                    if ( fieldId.equalsIgnoreCase( "phone-number-work-lock" ) ) {
                        if ( fieldState ) {
                            settingsLocker.lockSettingsValueForRegion( region, SettingsForApplication.PHONE, true );
                        } else {
                            settingsLocker.lockSettingsValueForRegion( region, SettingsForApplication.PHONE, false );
                        }
                    }
                    if ( fieldId.equalsIgnoreCase( "web-address-work-lock" ) ) {
                        if ( fieldState ) {
                            settingsLocker.lockSettingsValueForRegion( region, SettingsForApplication.WEB_ADDRESS_WORK, true );
                        } else {
                            settingsLocker.lockSettingsValueForRegion( region, SettingsForApplication.WEB_ADDRESS_WORK, false );
                        }
                    }

                    if ( fieldId.equalsIgnoreCase( "aboutme-lock" ) ) {
                        if ( fieldState ) {
                            settingsLocker.lockSettingsValueForRegion( region, SettingsForApplication.ABOUT_ME, true );
                        } else {
                            settingsLocker.lockSettingsValueForRegion( region, SettingsForApplication.ABOUT_ME, false );
                        }
                    }
                    userManagementService.updateRegion( region );
                }
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                lockSettings = branchSettings.getLockSettings();
                lockSettings = updateLockSettings( lockSettings, parentLock, fieldId, fieldState );
                lockSettings = profileManagementService.updateLockSettings(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, lockSettings );
                branchSettings.setLockSettings( lockSettings );
                userSettings.getBranchSettings().put( entityId, branchSettings );
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    if ( fieldId.equalsIgnoreCase( "prof-logo-lock" ) ) {
                        if ( fieldState ) {
                            settingsLocker.lockSettingsValueForBranch( branch, SettingsForApplication.LOGO, true );
                        } else {
                            settingsLocker.lockSettingsValueForBranch( branch, SettingsForApplication.LOGO, false );
                        }
                    }
                    if ( fieldId.equalsIgnoreCase( "phone-number-work-lock" ) ) {
                        if ( fieldState ) {
                            settingsLocker.lockSettingsValueForBranch( branch, SettingsForApplication.PHONE, true );
                        } else {
                            settingsLocker.lockSettingsValueForBranch( branch, SettingsForApplication.PHONE, false );
                        }
                    }
                    if ( fieldId.equalsIgnoreCase( "web-address-work-lock" ) ) {
                        if ( fieldState ) {
                            settingsLocker.lockSettingsValueForBranch( branch, SettingsForApplication.WEB_ADDRESS_WORK, true );
                        } else {
                            settingsLocker.lockSettingsValueForBranch( branch, SettingsForApplication.WEB_ADDRESS_WORK, false );
                        }
                    }

                    if ( fieldId.equalsIgnoreCase( "aboutme-lock" ) ) {
                        if ( fieldState ) {
                            settingsLocker.lockSettingsValueForBranch( branch, SettingsForApplication.ABOUT_ME, true );
                        } else {
                            settingsLocker.lockSettingsValueForBranch( branch, SettingsForApplication.ABOUT_ME, false );
                        }
                    }
                    userManagementService.updateBranch( branch );
                }
            } else {
                throw new InvalidInputException( "Invalid input exception occurred in editing LockSettings.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setLockSettings( lockSettings );

            LOG.info( "Lock Settings updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.LOCK_UPDATE_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while editing LockSettings. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.LOCK_UPDATE_UNSUCCESSFUL,
                DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateLockSettings() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    // Update lockSettings
    private LockSettings updateLockSettings( LockSettings lockSettings, LockSettings parentLock, String fieldId, boolean status )
    {
        LOG.debug( "Method updateLockSettings() called from ProfileManagementController" );

        // Checking if locked by parent, if not updating lock settings
        switch ( fieldId ) {
            case "prof-logo-lock":
                if ( !parentLock.getIsLogoLocked() ) {
                    lockSettings.setLogoLocked( status );
                }
                break;
            case "web-address-work-lock":
                if ( !parentLock.getIsWebAddressLocked() ) {
                    lockSettings.setWebAddressLocked( status );
                    ;
                }
                break;
            case "web-address-blogs-lock":
                if ( !parentLock.getIsBlogAddressLocked() ) {
                    lockSettings.setBlogAddressLocked( status );
                    ;
                }
                break;
            case "phone-number-work-lock":
                if ( !parentLock.getIsWorkPhoneLocked() ) {
                    lockSettings.setWorkPhoneLocked( status );
                }
                break;
            case "phone-number-personal-lock":
                if ( !parentLock.getIsPersonalPhoneLocked() ) {
                    lockSettings.setPersonalPhoneLocked( status );
                }
                break;
            case "phone-number-fax-lock":
                if ( !parentLock.getIsFaxPhoneLocked() ) {
                    lockSettings.setFaxPhoneLocked( status );
                }
                break;
            case "aboutme-lock":
                if ( !parentLock.getIsAboutMeLocked() ) {
                    lockSettings.setAboutMeLocked( status );
                }
                break;
        }
        LOG.debug( "Method updateLockSettings() finished from ProfileManagementController" );
        return lockSettings;
    }


    /**
     * Method to update about profile details
     * 
     * @param model
     * @param request
     */
    @RequestMapping ( value = "/addorupdateaboutme", method = RequestMethod.POST)
    public String updateAboutMe( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateAboutMe() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        ContactDetailsSettings contactDetailsSettings = null;

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            String aboutMe = request.getParameter( "aboutMe" );
            if ( aboutMe == null || aboutMe.isEmpty() ) {
                throw new InvalidInputException( "About me can not be null or empty", DisplayMessageConstants.GENERAL_ERROR );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                contactDetailsSettings = companySettings.getContact_details();
                contactDetailsSettings.setAbout_me( aboutMe );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, contactDetailsSettings );
                companySettings.setContact_details( contactDetailsSettings );
                userSettings.setCompanySettings( companySettings );
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.ABOUT_ME, true );
                    userManagementService.updateCompany( company );
                }
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                contactDetailsSettings = regionSettings.getContact_details();
                contactDetailsSettings.setAbout_me( aboutMe );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, contactDetailsSettings );
                regionSettings.setContact_details( contactDetailsSettings );
                userSettings.getRegionSettings().put( entityId, regionSettings );
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.ABOUT_ME, true );
                    userManagementService.updateRegion( region );
                }
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                contactDetailsSettings = branchSettings.getContact_details();
                contactDetailsSettings.setAbout_me( aboutMe );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, contactDetailsSettings );
                branchSettings.setContact_details( contactDetailsSettings );
                userSettings.getRegionSettings().put( entityId, branchSettings );
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.ABOUT_ME, true );
                    userManagementService.updateBranch( branch );
                }
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                contactDetailsSettings = agentSettings.getContact_details();
                contactDetailsSettings.setAbout_me( aboutMe );
                contactDetailsSettings = profileManagementService.updateAgentContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, contactDetailsSettings );
                agentSettings.setContact_details( contactDetailsSettings );
                userSettings.setAgentSettings( agentSettings );

                // Modify Agent details in Solr
                solrSearchService.editUserInSolr( agentSettings.getIden(), CommonConstants.ABOUT_ME_SOLR, aboutMe );
            } else {
                throw new InvalidInputException( "Error occurred while updating About me.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setContact_details( contactDetailsSettings );

            LOG.info( "About me details updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.ABOUT_ME_DETAILS_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating about me details. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.ABOUT_ME_DETAILS_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateAboutMe() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    private String replaceQuoteInString( String str )
    {
        if ( str.contains( "\"" ) ) {
            str = str.replace( "\"", "&quot;" );
        }
        return str;
    }


    /**
     * Method to update profile addresses in profile
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/updatebasicprofile", method = RequestMethod.POST)
    public String updateBasicDetail( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateBasicDetail() called from ProfileManagementController" );
        HttpSession session = request.getSession( false );
        ContactDetailsSettings contactDetailsSettings = null;

        try {
            User user = sessionHelper.getCurrentUser();
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            UserHierarchyAssignments assignments = (UserHierarchyAssignments) session
                .getAttribute( CommonConstants.USER_ASSIGNMENTS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            // Get the profile address parameters
            String name = request.getParameter( "profName" );
            if ( name == null || name.isEmpty() ) {
                throw new InvalidInputException( "Name passed can not be null or empty", DisplayMessageConstants.GENERAL_ERROR );
            }

            String title = request.getParameter( "profTitle" );

            String vertical = request.getParameter( "profVertical" );

            String location = request.getParameter( "profLocation" );

            if ( name != null ) {
                name = replaceQuoteInString( name );
            }
            if ( title != null ) {
                title = replaceQuoteInString( title );
            }
            if ( vertical != null ) {
                vertical = replaceQuoteInString( vertical );
            }
            if ( location != null ) {
                location = replaceQuoteInString( location );
            }

            AccountType accountType = (AccountType) session.getAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION );

            if ( accountType.getValue() == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }

                companySettings.setVertical( vertical );
                profileManagementService.updateVertical( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
                    companySettings, vertical );
                userSettings.setCompanySettings( companySettings );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                contactDetailsSettings = companySettings.getContact_details();

                contactDetailsSettings = updateBasicDetail( contactDetailsSettings, name, title, location );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, contactDetailsSettings );
                companySettings.setContact_details( contactDetailsSettings );

                // update company name
                profileManagementService.updateCompanyName( user.getUserId(), companySettings.getIden(), name );
                if ( accountType.getValue() != CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                    assignments.getCompanies().put( entityId, name );
                }

                companySettings.setVertical( vertical );
                profileManagementService.updateVertical( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
                    companySettings, vertical );

                userSettings.setCompanySettings( companySettings );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                contactDetailsSettings = regionSettings.getContact_details();
                contactDetailsSettings = updateBasicDetail( contactDetailsSettings, name, title, location );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, contactDetailsSettings );
                regionSettings.setContact_details( contactDetailsSettings );

                // update region name
                profileManagementService.updateRegionName( user.getUserId(), regionSettings.getIden(), name );
                assignments.getRegions().put( entityId, name );

                userSettings.getRegionSettings().put( entityId, regionSettings );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                contactDetailsSettings = branchSettings.getContact_details();
                contactDetailsSettings = updateBasicDetail( contactDetailsSettings, name, title, location );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, contactDetailsSettings );
                branchSettings.setContact_details( contactDetailsSettings );

                // update branch name
                profileManagementService.updateBranchName( user.getUserId(), branchSettings.getIden(), name );
                assignments.getBranches().put( entityId, name );

                userSettings.getRegionSettings().put( entityId, branchSettings );
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                contactDetailsSettings = agentSettings.getContact_details();
                // for individual set vertical/industry
                // contactDetailsSettings.setIndustry(vertical);
                contactDetailsSettings = updateBasicDetail( contactDetailsSettings, name, title, location );
                contactDetailsSettings = profileManagementService.updateAgentContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, contactDetailsSettings );
                agentSettings.setContact_details( contactDetailsSettings );

                // update user name
                profileManagementService.updateIndividualName( user.getUserId(), agentSettings.getIden(), name );

                /*
                 * agentSettings.setVertical(vertical);
                 * profileManagementService.
                 * updateVertical(MongoOrganizationUnitSettingDaoImpl
                 * .AGENT_SETTINGS_COLLECTION, agentSettings, vertical);
                 */

                userSettings.setAgentSettings( agentSettings );

                // Modify Agent details in Solr
                Map<String, Object> userMap = new HashMap<>();
                userMap.put( CommonConstants.USER_DISPLAY_NAME_SOLR, name );
                userMap.put( CommonConstants.TITLE_SOLR, title );
                if ( name.indexOf( " " ) != -1 ) {
                    userMap.put( CommonConstants.USER_FIRST_NAME_SOLR, name.substring( 0, name.indexOf( ' ' ) ) );
                    userMap.put( CommonConstants.USER_LAST_NAME_SOLR, name.substring( name.indexOf( ' ' ) + 1 ) );
                    user.setFirstName( name.substring( 0, name.indexOf( ' ' ) ) );
                    user.setLastName( name.substring( name.indexOf( ' ' ) + 1 ) );
                } else {
                    userMap.put( CommonConstants.USER_FIRST_NAME_SOLR, name );
                }
                userManagementService.updateUser( user, userMap );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred in upadting Basic details.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            if ( vertical != null && !vertical.isEmpty() ) {
                profileSettings.setVertical( vertical );
            }
            profileSettings.setContact_details( contactDetailsSettings );

            LOG.info( "Basic Detail updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.BASIC_DETAILS_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating profile basic details. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.BASIC_DETAILS_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateBasicDetail() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    // Update address details
    private ContactDetailsSettings updateBasicDetail( ContactDetailsSettings contactDetailsSettings, String name, String title,
        String location )
    {
        LOG.debug( "Method updateBasicDetial() called from ProfileManagementController" );
        contactDetailsSettings.setName( name );
        contactDetailsSettings.setTitle( title );
        contactDetailsSettings.setLocation( location );
        LOG.debug( "Method updateBasicDetial() finished from ProfileManagementController" );
        return contactDetailsSettings;
    }


    /**
     * Method to update profile addresses in profile
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/updateprofileaddress", method = RequestMethod.POST)
    public String updateProfileAddress( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateProfileAddress() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        ContactDetailsSettings contactDetailsSettings = null;

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            // Get the profile address parameters
            String address1 = request.getParameter( CommonConstants.ADDRESS1 );
            String address2 = request.getParameter( CommonConstants.ADDRESS2 );
            String state = request.getParameter( CommonConstants.STATE );
            String city = request.getParameter( CommonConstants.CITY );
            String country = request.getParameter( CommonConstants.COUNTRY );
            String countryCode = request.getParameter( CommonConstants.COUNTRY_CODE );
            String zipcode = request.getParameter( CommonConstants.ZIPCODE );
            if ( address1 == null || address1.isEmpty() ) {
                throw new InvalidInputException( "Address 1 passed can not be null or empty",
                    DisplayMessageConstants.GENERAL_ERROR );
            }
            if ( country == null || country.isEmpty() ) {
                throw new InvalidInputException( "country passed can not be null or empty",
                    DisplayMessageConstants.GENERAL_ERROR );
            }
            if ( zipcode == null || zipcode.isEmpty() ) {
                throw new InvalidInputException( "zipcode passed can not be null or empty",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                contactDetailsSettings = companySettings.getContact_details();
                contactDetailsSettings = updateAddressDetail( contactDetailsSettings, address1, address2, country, countryCode,
                    state, city, zipcode );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, contactDetailsSettings );
                companySettings.setContact_details( contactDetailsSettings );
                userSettings.setCompanySettings( companySettings );
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.ADDRESS, true );
                    userManagementService.updateCompany( company );
                }
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                contactDetailsSettings = regionSettings.getContact_details();
                contactDetailsSettings = updateAddressDetail( contactDetailsSettings, address1, address2, country, countryCode,
                    state, city, zipcode );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, contactDetailsSettings );
                regionSettings.setContact_details( contactDetailsSettings );
                userSettings.getRegionSettings().put( entityId, regionSettings );
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.ADDRESS, true );
                    userManagementService.updateRegion( region );
                }

            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                contactDetailsSettings = branchSettings.getContact_details();
                contactDetailsSettings = updateAddressDetail( contactDetailsSettings, address1, address2, country, countryCode,
                    state, city, zipcode );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, contactDetailsSettings );
                branchSettings.setContact_details( contactDetailsSettings );
                userSettings.getRegionSettings().put( entityId, branchSettings );
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.ADDRESS, true );
                    userManagementService.updateBranch( branch );
                }
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                contactDetailsSettings = agentSettings.getContact_details();
                contactDetailsSettings = updateAddressDetail( contactDetailsSettings, address1, address2, country, countryCode,
                    state, city, zipcode );
                contactDetailsSettings = profileManagementService.updateAgentContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, contactDetailsSettings );
                agentSettings.setContact_details( contactDetailsSettings );
                userSettings.setAgentSettings( agentSettings );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred in editing Address details.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setContact_details( contactDetailsSettings );

            LOG.info( "Profile addresses updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.PROFILE_ADDRESSES_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating profile address details. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.PROFILE_ADDRESSES_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateProfileAddress() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    // Update address details
    private ContactDetailsSettings updateAddressDetail( ContactDetailsSettings contactDetailsSettings, String address1,
        String address2, String country, String countryCode, String state, String city, String zipcode )
    {
        LOG.debug( "Method updateAddressDetail() called from ProfileManagementController" );
        contactDetailsSettings.setAddress( address1 + ", " + address2 );
        contactDetailsSettings.setAddress1( address1 );
        contactDetailsSettings.setAddress2( address2 );
        contactDetailsSettings.setCountry( country );
        contactDetailsSettings.setCountryCode( countryCode );
        contactDetailsSettings.setState( state );
        contactDetailsSettings.setCity( city );
        contactDetailsSettings.setZipcode( zipcode );
        LOG.debug( "Method updateAddressDetail() finished from ProfileManagementController" );
        return contactDetailsSettings;
    }


    @RequestMapping ( value = "/editcompanyinformation", method = RequestMethod.POST)
    public String editCompanyInformation( Model model, HttpServletRequest request )
    {
        LOG.info( "Method editCompanyInformation of ProfileManagementController called" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        ContactDetailsSettings contactDetailsSettings = null;

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            String address1 = request.getParameter( "address1" );
            String address2 = request.getParameter( "address2" );
            String country = request.getParameter( "country" );
            String state = request.getParameter( "state" );
            String city = request.getParameter( "city" );
            String countryCode = request.getParameter( "countrycode" );
            String zipcode = request.getParameter( "zipcode" );
            String companyContactNo = request.getParameter( "contactno" );
            if ( address1 == null || address1.isEmpty() ) {
                throw new InvalidInputException( "Address is null or empty while adding company information",
                    DisplayMessageConstants.INVALID_ADDRESS );
            }

            if ( country == null || country.isEmpty() ) {
                throw new InvalidInputException( "Country is null or empty while adding company information",
                    DisplayMessageConstants.INVALID_COUNTRY );
            }

            if ( countryCode == null || countryCode.isEmpty() ) {
                throw new InvalidInputException( "Country code is null or empty while adding company information",
                    DisplayMessageConstants.INVALID_COUNTRY );
            }

            if ( zipcode == null || zipcode.isEmpty() ) {
                throw new InvalidInputException( "Zipcode is not valid while adding company information",
                    DisplayMessageConstants.INVALID_ZIPCODE );
            }

            if ( companyContactNo == null || companyContactNo.isEmpty() ) {
                throw new InvalidInputException( "Company contact number is not valid while adding company information",
                    DisplayMessageConstants.INVALID_COMPANY_PHONEN0 );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                contactDetailsSettings = companySettings.getContact_details();
                contactDetailsSettings = editCompanyInformation( contactDetailsSettings, address1, address2, country, state,
                    city, countryCode, zipcode, companyContactNo );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, contactDetailsSettings );
                companySettings.setContact_details( contactDetailsSettings );
                userSettings.setCompanySettings( companySettings );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                contactDetailsSettings = regionSettings.getContact_details();
                contactDetailsSettings = editCompanyInformation( contactDetailsSettings, address1, address2, country, state,
                    city, countryCode, zipcode, companyContactNo );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, contactDetailsSettings );
                regionSettings.setContact_details( contactDetailsSettings );
                userSettings.getRegionSettings().put( entityId, regionSettings );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                contactDetailsSettings = branchSettings.getContact_details();
                contactDetailsSettings = editCompanyInformation( contactDetailsSettings, address1, address2, country, state,
                    city, countryCode, zipcode, companyContactNo );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, contactDetailsSettings );
                branchSettings.setContact_details( contactDetailsSettings );
                userSettings.getRegionSettings().put( entityId, branchSettings );
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                contactDetailsSettings = agentSettings.getContact_details();
                contactDetailsSettings = editCompanyInformation( contactDetailsSettings, address1, address2, country, state,
                    city, countryCode, zipcode, companyContactNo );
                contactDetailsSettings = profileManagementService.updateAgentContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, contactDetailsSettings );
                agentSettings.setContact_details( contactDetailsSettings );
                userSettings.setAgentSettings( agentSettings );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred in editing Address details.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setContact_details( contactDetailsSettings );

            LOG.info( "Profile addresses updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.PROFILE_ADDRESSES_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating profile address details. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.PROFILE_ADDRESSES_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method editCompanyInformation() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    // Update address details
    private ContactDetailsSettings editCompanyInformation( ContactDetailsSettings contactDetailsSettings, String address1,
        String address2, String country, String state, String city, String countryCode, String zipcode, String contactNo )
    {
        LOG.debug( "Method updateAddressDetail() called from ProfileManagementController" );
        contactDetailsSettings.setAddress( address1 + ", " + address2 );
        contactDetailsSettings.setAddress1( address1 );
        contactDetailsSettings.setAddress2( address2 );
        contactDetailsSettings.setCountry( country );
        contactDetailsSettings.setState( state );
        contactDetailsSettings.setCity( city );
        contactDetailsSettings.setCountryCode( countryCode );
        contactDetailsSettings.setZipcode( zipcode );

        if ( contactDetailsSettings.getContact_numbers() == null ) {
            contactDetailsSettings.setContact_numbers( new ContactNumberSettings() );
        }
        contactDetailsSettings.getContact_numbers().setWork( contactNo );
        LOG.debug( "Method updateAddressDetail() finished from ProfileManagementController" );
        return contactDetailsSettings;
    }


    @RequestMapping ( value = "/updatesummarydata", method = RequestMethod.POST)
    public String updateSummaryDetail( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateSummaryDetail() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        ContactDetailsSettings contactDetailsSettings = null;

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            // Get the profile address parameters
            String industry = request.getParameter( "industry" );
            String location = request.getParameter( "location" );
            String aboutme = request.getParameter( "aboutme" );

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                contactDetailsSettings = companySettings.getContact_details();
                contactDetailsSettings = updateSummaryDetail( contactDetailsSettings, industry, location, aboutme );
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.LOCATION, true );
                    userManagementService.updateCompany( company );
                }
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, contactDetailsSettings );
                companySettings.setContact_details( contactDetailsSettings );
                userSettings.setCompanySettings( companySettings );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                contactDetailsSettings = regionSettings.getContact_details();
                contactDetailsSettings = updateSummaryDetail( contactDetailsSettings, industry, location, aboutme );
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.LOCATION, true );
                    userManagementService.updateRegion( region );
                }
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, contactDetailsSettings );
                regionSettings.setContact_details( contactDetailsSettings );
                userSettings.getRegionSettings().put( entityId, regionSettings );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                contactDetailsSettings = branchSettings.getContact_details();
                contactDetailsSettings = updateSummaryDetail( contactDetailsSettings, industry, location, aboutme );
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.LOGO, true );
                    userManagementService.updateBranch( branch );
                }
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, contactDetailsSettings );
                branchSettings.setContact_details( contactDetailsSettings );
                userSettings.getRegionSettings().put( entityId, branchSettings );
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                contactDetailsSettings = agentSettings.getContact_details();
                contactDetailsSettings = updateSummaryDetail( contactDetailsSettings, industry, location, aboutme );
                contactDetailsSettings = profileManagementService.updateAgentContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, contactDetailsSettings );
                agentSettings.setContact_details( contactDetailsSettings );
                userSettings.setAgentSettings( agentSettings );

                // Modify Agent details in Solr
                solrSearchService.editUserInSolr( agentSettings.getIden(), CommonConstants.ABOUT_ME_SOLR, aboutme );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred in editing Summary details.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setContact_details( contactDetailsSettings );

            LOG.info( "Profile addresses updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.ABOUT_ME_DETAILS_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating profile address details. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.ABOUT_ME_DETAILS_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateSummaryDetail() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    // Update summary details
    private ContactDetailsSettings updateSummaryDetail( ContactDetailsSettings contactDetailsSettings, String industry,
        String location, String aboutme )
    {
        LOG.debug( "Method updateSummaryDetail() called from ProfileManagementController" );
        if ( industry != null ) {
            contactDetailsSettings.setIndustry( industry );
        }
        if ( location != null ) {
            contactDetailsSettings.setLocation( location );
        }
        if ( aboutme != null ) {
            contactDetailsSettings.setAbout_me( aboutme );
        }
        LOG.debug( "Method updateSummaryDetail() finished from ProfileManagementController" );
        return contactDetailsSettings;
    }


    /**
     * Method to add or update profile logo
     * 
     * @param model
     * @param request
     * @param fileLocal
     */
    @RequestMapping ( value = "/updatelogo", method = RequestMethod.POST)
    public String updateLogo( Model model, HttpServletRequest request, @RequestParam ( "logo") MultipartFile fileLocal )
    {
        LOG.info( "Method updateLogo() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        String logoUrl = "";

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            String logoFileName = request.getParameter( "logoFileName" );
            try {
                if ( logoFileName == null || logoFileName.isEmpty() ) {
                    throw new InvalidInputException( "Logo passed is null or empty" );
                }
                logoUrl = fileUploadService.fileUploadHandler( fileLocal, logoFileName );
                logoUrl = amazonEndpoint + CommonConstants.FILE_SEPARATOR + amazonLogoBucket + CommonConstants.FILE_SEPARATOR
                    + logoUrl;
            } catch ( NonFatalException e ) {
                LOG.error( "NonFatalException while uploading Logo. Reason :" + e.getMessage(), e );
                model.addAttribute( "message",
                    messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
                return JspResolver.MESSAGE_HEADER;
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.LOGO, true );
                    userManagementService.updateCompany( company );
                }
                profileManagementService.updateLogo( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
                    companySettings, logoUrl );
                companySettings.setLogo( logoUrl );
                userSettings.setCompanySettings( companySettings );


            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                profileManagementService.updateLogo( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION,
                    regionSettings, logoUrl );
                regionSettings.setLogo( logoUrl );
                userSettings.getRegionSettings().put( entityId, regionSettings );

                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.LOGO, true );
                    userManagementService.updateRegion( region );
                }


            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                profileManagementService.updateLogo( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION,
                    branchSettings, logoUrl );
                branchSettings.setLogo( logoUrl );
                userSettings.getRegionSettings().put( entityId, branchSettings );
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.LOGO, true );
                    userManagementService.updateBranch( branch );
                }

            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                profileManagementService.updateLogo( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
                    agentSettings, logoUrl );
                agentSettings.setLogo( logoUrl );
                userSettings.setAgentSettings( agentSettings );

            } else {
                throw new InvalidInputException( "Invalid input exception occurred in uploading logo.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setLogo( logoUrl );
            sessionHelper.setLogoInSession( session, userSettings );
            LOG.info( "Logo uploaded successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.LOGO_UPLOAD_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while uploading logo. Reason :" + nonFatalException.getMessage(), nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.LOGO_UPLOAD_UNSUCCESSFUL,
                DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateLogo() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    @RequestMapping ( value = "/updateprofileimage", method = RequestMethod.POST)
    public String updateProfileImage( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateProfileImage() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        String profileImageUrl = "";

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            try {
                int selectedX = Integer.parseInt( request.getParameter( "selected_x" ) );
                int selectedY = Integer.parseInt( request.getParameter( "selected_y" ) );
                int selectedW = Integer.parseInt( request.getParameter( "selected_w" ) );
                int selectedH = Integer.parseInt( request.getParameter( "selected_h" ) );
                int resizeWidth = Integer.parseInt( request.getParameter( "width" ) );
                int resizeHeight = Integer.parseInt( request.getParameter( "height" ) );

                String imageBase64 = request.getParameter( "imageBase64" );
                String imageFileName = request.getParameter( "imageFileName" );
                if ( imageBase64 == null || imageBase64.isEmpty() ) {
                    throw new InvalidInputException( "image passed is null or empty" );
                }
                if ( imageFileName == null || imageFileName.isEmpty() ) {
                    throw new InvalidInputException( "image passed is null or empty" );
                }

                // reading image
                File dir = new File( CommonConstants.IMAGE_DIR );
                if ( !dir.exists() ) {
                    dir.mkdirs();
                }
                String filePath = dir.getAbsolutePath() + CommonConstants.FILE_SEPARATOR + CommonConstants.IMAGE_NAME;

                BASE64Decoder decoder = new BASE64Decoder();
                byte[] decodedBytes = decoder.decodeBuffer( imageBase64.split( "," )[1] );
                ByteArrayInputStream bis = new ByteArrayInputStream( decodedBytes );

                // resizing image
                LOG.debug( "Dimensions for resizing: resizeWidth: " + resizeWidth + " resizeHeight: " + resizeHeight );
                BufferedImage bufferedImage = ImageIO.read( bis );
                FileOutputStream fileOuputStream = new FileOutputStream( filePath );
                ImageIO.write( bufferedImage, CommonConstants.IMAGE_FORMAT_PNG, fileOuputStream );
                fileOuputStream.close();
                uploadUtils.resizeImage( filePath, filePath, resizeWidth, resizeHeight );

                // cropping image
                LOG.debug( "Co-ordinates for cropping: x: " + selectedX + " y: " + selectedY + " h: " + selectedH + " w: "
                    + selectedW );
                BufferedImage resized = ImageIO.read( new File( filePath ) );
                BufferedImage croppedImage = uploadUtils.cropImage( resized, selectedW, selectedH, selectedX, selectedY );
                fileOuputStream = new FileOutputStream( filePath );
                ImageIO.write( croppedImage, CommonConstants.IMAGE_FORMAT_PNG, fileOuputStream );
                fileOuputStream.close();

                // uploading image
                File fileLocal = new File( filePath );
                profileImageUrl = fileUploadService.fileUploadHandler( fileLocal, imageFileName );
                profileImageUrl = amazonEndpoint + CommonConstants.FILE_SEPARATOR + amazonImageBucket
                    + CommonConstants.FILE_SEPARATOR + profileImageUrl;
            } catch ( NonFatalException e ) {
                LOG.error( "NonFatalException while uploading Profile Image. Reason :" + e.getMessage(), e );
                model.addAttribute( "message",
                    messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
                return JspResolver.MESSAGE_HEADER;
            } catch ( IOException e ) {
                LOG.error( "IOException while uploading Profile Image. Reason :" + e.getMessage(), e );
                model.addAttribute( "message", "Unable to upload profile image" );
                return JspResolver.MESSAGE_HEADER;
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                profileManagementService.updateProfileImage( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
                    companySettings, profileImageUrl );
                companySettings.setProfileImageUrl( profileImageUrl );
                userSettings.setCompanySettings( companySettings );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                profileManagementService.updateProfileImage( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION,
                    regionSettings, profileImageUrl );
                regionSettings.setProfileImageUrl( profileImageUrl );
                userSettings.getRegionSettings().put( entityId, regionSettings );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                profileManagementService.updateProfileImage( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION,
                    branchSettings, profileImageUrl );
                branchSettings.setProfileImageUrl( profileImageUrl );
                userSettings.getRegionSettings().put( entityId, branchSettings );
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                profileManagementService.updateProfileImage( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
                    agentSettings, profileImageUrl );
                agentSettings.setProfileImageUrl( profileImageUrl );
                userSettings.setAgentSettings( agentSettings );

                // Modify Agent details in Solr
                solrSearchService.editUserInSolr( agentSettings.getIden(), CommonConstants.PROFILE_IMAGE_URL_SOLR,
                    profileImageUrl );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred while uploading profile image.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setProfileImageUrl( profileImageUrl );
            sessionHelper.setProfileImageInSession( session, userSettings );

            LOG.info( "Profile Image uploaded successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.PROFILE_IMAGE_UPLOAD_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while uploading logo. Reason :" + nonFatalException.getMessage(), nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.PROFILE_IMAGE_UPLOAD_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateProfileImage() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    /**
     * Method to update email id for profile
     * 
     * @param model
     * @param request
     */
    @RequestMapping ( value = "/updateemailids", method = RequestMethod.POST)
    public String updateEmailds( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateEmailds() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        ContactDetailsSettings contactDetailsSettings = null;

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            List<MiscValues> mailIds = null;
            try {
                String payload = request.getParameter( "mailIds" );
                if ( payload == null || payload.isEmpty() ) {
                    throw new InvalidInputException( "Maild ids passed was null or empty" );
                }
                ObjectMapper mapper = new ObjectMapper();
                mailIds = mapper.readValue( payload,
                    TypeFactory.defaultInstance().constructCollectionType( List.class, MiscValues.class ) );
                String primaryMailId = "";
                for ( MiscValues mailid : mailIds ) {
                    if ( mailid.getKey().equalsIgnoreCase( "work" ) ) {
                        primaryMailId = mailid.getValue();
                    }
                }
                try {
                    userManagementService.getUserByEmail( primaryMailId );
                    throw new UserAlreadyExistsException( "User already exists with emailId : " + primaryMailId );
                } catch ( NoRecordsFetchedException e ) {
                    LOG.debug( "User not registerd already with email Id : " + primaryMailId );
                }

            } catch ( IOException ioException ) {
                throw new NonFatalException( "Error occurred while parsing json.", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                contactDetailsSettings = companySettings.getContact_details();

                // Send verification Links
                sendVerificationLinks( contactDetailsSettings, mailIds,
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings );

                contactDetailsSettings = updateMailSettings( companySettings.getIden(), contactDetailsSettings, mailIds,
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, contactDetailsSettings );
                companySettings.setContact_details( contactDetailsSettings );
                userSettings.setCompanySettings( companySettings );
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    for ( MiscValues mailId : mailIds ) {
                        String key = mailId.getKey();
                        if ( key.equalsIgnoreCase( CommonConstants.EMAIL_TYPE_WORK ) ) {
                            settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.EMAIL_ID_WORK, true );
                        } else if ( key.equalsIgnoreCase( CommonConstants.EMAIL_TYPE_PERSONAL ) ) {
                            settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.EMAIL_ID_PERSONAL, true );
                        }
                    }
                    userManagementService.updateCompany( company );
                }
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                contactDetailsSettings = regionSettings.getContact_details();

                // Send verification Links
                sendVerificationLinks( contactDetailsSettings, mailIds,
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings );

                contactDetailsSettings = updateMailSettings( regionSettings.getIden(), contactDetailsSettings, mailIds,
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, contactDetailsSettings );
                regionSettings.setContact_details( contactDetailsSettings );
                userSettings.getRegionSettings().put( entityId, regionSettings );
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    for ( MiscValues mailId : mailIds ) {
                        String key = mailId.getKey();
                        if ( key.equalsIgnoreCase( CommonConstants.EMAIL_TYPE_WORK ) ) {
                            settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.EMAIL_ID_WORK, true );
                        } else if ( key.equalsIgnoreCase( CommonConstants.EMAIL_TYPE_PERSONAL ) ) {
                            settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.EMAIL_ID_PERSONAL, true );
                        }
                    }
                    userManagementService.updateRegion( region );
                }
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                contactDetailsSettings = branchSettings.getContact_details();

                // Send verification Links
                sendVerificationLinks( contactDetailsSettings, mailIds,
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings );

                contactDetailsSettings = updateMailSettings( branchSettings.getIden(), contactDetailsSettings, mailIds,
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, contactDetailsSettings );
                branchSettings.setContact_details( contactDetailsSettings );
                userSettings.getRegionSettings().put( entityId, branchSettings );
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    for ( MiscValues mailId : mailIds ) {
                        String key = mailId.getKey();
                        if ( key.equalsIgnoreCase( CommonConstants.EMAIL_TYPE_WORK ) ) {
                            settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.EMAIL_ID_WORK, true );
                        } else if ( key.equalsIgnoreCase( CommonConstants.EMAIL_TYPE_PERSONAL ) ) {
                            settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.EMAIL_ID_PERSONAL, true );
                        }
                    }
                    userManagementService.updateBranch( branch );
                }
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                contactDetailsSettings = agentSettings.getContact_details();

                // Send verification Links
                sendVerificationLinks( contactDetailsSettings, mailIds,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings );

                contactDetailsSettings = updateMailSettings( agentSettings.getIden(), contactDetailsSettings, mailIds,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                contactDetailsSettings = profileManagementService.updateAgentContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, contactDetailsSettings );
                agentSettings.setContact_details( contactDetailsSettings );
                userSettings.setAgentSettings( agentSettings );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred while updating emailids.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setContact_details( contactDetailsSettings );

            LOG.info( "Maild ids updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.MAIL_IDS_UPDATE_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( UserAlreadyExistsException userAlreadyExistsException ) {
            LOG.error(
                "UserAlreadyExistsException while updating Mail ids. Reason :" + userAlreadyExistsException.getMessage(),
                userAlreadyExistsException );
            model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.USERNAME_ALREADY_TAKEN,
                DisplayMessageType.ERROR_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating Mail ids. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.MAIL_IDS_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateEmailds() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    // send verification links
    private void sendVerificationLinks( ContactDetailsSettings oldSettings, List<MiscValues> mailIds, String entityType,
        OrganizationUnitSettings userSettings ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "Method sendVerificationLinks() called from ProfileManagementController" );
        Map<String, String> urlParams = null;

        if ( oldSettings == null ) {
            throw new InvalidInputException( "No contact details object found for user" );
        }
        MailIdSettings mailIdSettings = oldSettings.getMail_ids();
        if ( mailIdSettings == null ) {
            LOG.debug( "No maild ids added, create new mail id object in contact details" );
            mailIdSettings = new MailIdSettings();
        }

        for ( MiscValues mailId : mailIds ) {
            String key = mailId.getKey();
            String emailId = mailId.getValue();
            if ( key.equalsIgnoreCase( CommonConstants.EMAIL_TYPE_WORK ) ) {
                urlParams = new HashMap<String, String>();
                urlParams.put( CommonConstants.EMAIL_ID, emailId );
                urlParams.put( CommonConstants.EMAIL_TYPE, CommonConstants.EMAIL_TYPE_WORK );
                urlParams.put( CommonConstants.ENTITY_ID_COLUMN, userSettings.getIden() + "" );
                urlParams.put( CommonConstants.ENTITY_TYPE_COLUMN, entityType );

                profileManagementService.generateVerificationUrl( urlParams, applicationBaseUrl
                    + CommonConstants.REQUEST_MAPPING_EMAIL_EDIT_VERIFICATION, emailId, userSettings.getContact_details()
                    .getName() );
            }
        }
        LOG.debug( "Method sendVerificationLinks() finished from ProfileManagementController" );
    }


    // Update mail ids
    private ContactDetailsSettings updateMailSettings( long entityId, ContactDetailsSettings contactDetailsSettings,
        List<MiscValues> mailIds, String entityType ) throws InvalidInputException
    {
        LOG.debug( "Method updateMailSettings() called from ProfileManagementController" );
        if ( contactDetailsSettings == null ) {
            throw new InvalidInputException( "No contact details object found for user" );
        }

        MailIdSettings mailIdSettings = contactDetailsSettings.getMail_ids();
        if ( mailIdSettings == null ) {
            LOG.debug( "No maild ids added, create new mail id object in contact details" );
            mailIdSettings = new MailIdSettings();
        }

        List<MiscValues> others = null;
        for ( MiscValues mailId : mailIds ) {
            String key = mailId.getKey();
            String value = mailId.getValue();
            if ( key.equalsIgnoreCase( CommonConstants.EMAIL_TYPE_WORK ) ) {
                mailIdSettings.setWorkEmailToVerify( value );
                mailIdSettings.setWorkEmailVerified( false );
            } else if ( key.equalsIgnoreCase( CommonConstants.EMAIL_TYPE_PERSONAL ) ) {
                mailIdSettings.setPersonal( value );
                mailIdSettings.setPersonalEmailToVerify( value );
                mailIdSettings.setPersonalEmailVerified( false );
            } else {
                if ( others == null ) {
                    others = new ArrayList<>();
                }
                others.add( mailId );
            }
        }

        mailIdSettings.setOthers( others );
        contactDetailsSettings.setMail_ids( mailIdSettings );
        LOG.debug( "Method updateMailSettings() finished from ProfileManagementController" );
        return contactDetailsSettings;
    }


    /**
     * Method to update phone numbers of a profile
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/updatephonenumbers", method = RequestMethod.POST)
    public String updatePhoneNumbers( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updatePhoneNumbers() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        ContactDetailsSettings contactDetailsSettings = null;

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            String payload = request.getParameter( "phoneNumbers" );
            List<MiscValues> phoneNumbers = null;
            try {
                if ( payload == null || payload.isEmpty() ) {
                    throw new InvalidInputException( "Phone numbers passed was null or empty" );
                }
                ObjectMapper mapper = new ObjectMapper();
                phoneNumbers = mapper.readValue( payload,
                    TypeFactory.defaultInstance().constructCollectionType( List.class, MiscValues.class ) );
            } catch ( IOException ioException ) {
                throw new NonFatalException( "Error occurred while parsing json.", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                contactDetailsSettings = companySettings.getContact_details();
                contactDetailsSettings = updatePhoneNumbers( contactDetailsSettings, phoneNumbers );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, contactDetailsSettings );
                companySettings.setContact_details( contactDetailsSettings );
                userSettings.setCompanySettings( companySettings );
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.PHONE, true );
                    userManagementService.updateCompany( company );
                }
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                contactDetailsSettings = regionSettings.getContact_details();
                contactDetailsSettings = updatePhoneNumbers( contactDetailsSettings, phoneNumbers );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, contactDetailsSettings );
                regionSettings.setContact_details( contactDetailsSettings );
                userSettings.getRegionSettings().put( entityId, regionSettings );
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.PHONE, true );
                    userManagementService.updateRegion( region );
                }
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                contactDetailsSettings = branchSettings.getContact_details();
                contactDetailsSettings = updatePhoneNumbers( contactDetailsSettings, phoneNumbers );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, contactDetailsSettings );
                branchSettings.setContact_details( contactDetailsSettings );
                userSettings.getRegionSettings().put( entityId, branchSettings );
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.PHONE, true );
                    userManagementService.updateBranch( branch );
                }
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                contactDetailsSettings = agentSettings.getContact_details();
                contactDetailsSettings = updatePhoneNumbers( contactDetailsSettings, phoneNumbers );
                contactDetailsSettings = profileManagementService.updateAgentContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, contactDetailsSettings );
                agentSettings.setContact_details( contactDetailsSettings );
                userSettings.setAgentSettings( agentSettings );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred while updating phone numbers.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setContact_details( contactDetailsSettings );

            LOG.info( "Contact numbers updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.CONTACT_NUMBERS_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating contact numbers. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.CONTACT_NUMBERS_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updatePhoneNumbers() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    // update phone numbers
    private ContactDetailsSettings updatePhoneNumbers( ContactDetailsSettings contactDetailsSettings,
        List<MiscValues> phoneNumbers ) throws InvalidInputException
    {
        LOG.debug( "Method updatePhoneNumbers() called from ProfileManagementController" );
        if ( contactDetailsSettings == null ) {
            throw new InvalidInputException( "No contact details object found for user" );
        }
        ContactNumberSettings phoneNumberSettings = contactDetailsSettings.getContact_numbers();
        if ( phoneNumberSettings == null ) {
            LOG.debug( "No phone numbers are added, create phone numbers object in contact details" );
            phoneNumberSettings = new ContactNumberSettings();
        }
        List<MiscValues> others = null;
        for ( MiscValues phoneNumber : phoneNumbers ) {
            String key = phoneNumber.getKey();
            String value = phoneNumber.getValue();
            if ( key.equalsIgnoreCase( "work" ) ) {
                phoneNumberSettings.setWork( value );
            } else if ( key.equalsIgnoreCase( "personal" ) ) {
                phoneNumberSettings.setPersonal( value );
            } else if ( key.equalsIgnoreCase( "fax" ) ) {
                phoneNumberSettings.setFax( value );
            } else {
                if ( others == null ) {
                    others = new ArrayList<>();
                }
                others.add( phoneNumber );
            }
        }
        phoneNumberSettings.setOthers( others );
        contactDetailsSettings.setContact_numbers( phoneNumberSettings );
        LOG.debug( "Method updatePhoneNumbers() called from ProfileManagementController" );
        return contactDetailsSettings;
    }


    /**
     * Method to update web addresses for a profile
     * 
     * @param model
     * @param request
     */
    @RequestMapping ( value = "/updatewebaddresses", method = RequestMethod.POST)
    public String updateWebAddresses( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateWebAddresses() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        ContactDetailsSettings contactDetailsSettings = null;

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session" );
            }

            List<MiscValues> webAddresses = null;
            try {
                String payload = request.getParameter( "webAddresses" );
                LOG.info( payload );
                if ( payload == null || payload.isEmpty() ) {
                    throw new InvalidInputException( "Web addresses passed was null or empty" );
                }
                ObjectMapper mapper = new ObjectMapper();
                webAddresses = mapper.readValue( payload,
                    TypeFactory.defaultInstance().constructCollectionType( List.class, MiscValues.class ) );
            } catch ( IOException ioException ) {
                throw new NonFatalException( "Error occurred while parsing json.", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                contactDetailsSettings = companySettings.getContact_details();
                contactDetailsSettings = updateWebAddresses( contactDetailsSettings, webAddresses );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, contactDetailsSettings );
                companySettings.setContact_details( contactDetailsSettings );
                userSettings.setCompanySettings( companySettings );
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    for ( MiscValues webAddress : webAddresses ) {
                        String key = webAddress.getKey();
                        if ( key.equalsIgnoreCase( "work" ) ) {
                            settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.WEB_ADDRESS_WORK, true );
                        } else if ( key.equalsIgnoreCase( "personal" ) ) {
                            settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.WEB_ADDRESS_PERSONAL,
                                true );
                        } 
                    }
                    userManagementService.updateCompany( company );
                }
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                contactDetailsSettings = regionSettings.getContact_details();
                contactDetailsSettings = updateWebAddresses( contactDetailsSettings, webAddresses );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, contactDetailsSettings );
                regionSettings.setContact_details( contactDetailsSettings );
                userSettings.getRegionSettings().put( entityId, regionSettings );
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    for ( MiscValues webAddress : webAddresses ) {
                        String key = webAddress.getKey();
                        if ( key.equalsIgnoreCase( "work" ) ) {
                            settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.WEB_ADDRESS_WORK, true );
                        } else if ( key.equalsIgnoreCase( "personal" ) ) {
                            settingsSetter
                                .setSettingsValueForRegion( region, SettingsForApplication.WEB_ADDRESS_PERSONAL, true );
                        } 
                    }
                    userManagementService.updateRegion( region );
                }
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                contactDetailsSettings = branchSettings.getContact_details();
                contactDetailsSettings = updateWebAddresses( contactDetailsSettings, webAddresses );
                contactDetailsSettings = profileManagementService.updateContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, contactDetailsSettings );
                branchSettings.setContact_details( contactDetailsSettings );
                userSettings.getRegionSettings().put( entityId, branchSettings );
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    for ( MiscValues webAddress : webAddresses ) {
                        String key = webAddress.getKey();
                        if ( key.equalsIgnoreCase( "work" ) ) {
                            settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.WEB_ADDRESS_WORK, true );
                        } else if ( key.equalsIgnoreCase( "personal" ) ) {
                            settingsSetter
                                .setSettingsValueForBranch( branch, SettingsForApplication.WEB_ADDRESS_PERSONAL, true );
                        }
                    }
                    userManagementService.updateBranch( branch );
                }

            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                contactDetailsSettings = agentSettings.getContact_details();
                contactDetailsSettings = updateWebAddresses( contactDetailsSettings, webAddresses );
                contactDetailsSettings = profileManagementService.updateAgentContactDetails(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, contactDetailsSettings );
                agentSettings.setContact_details( contactDetailsSettings );
                userSettings.setAgentSettings( agentSettings );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred while updating web addresses.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setContact_details( contactDetailsSettings );

            LOG.info( "Web addresses updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.WEB_ADDRESSES_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating web addresses. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.WEB_ADDRESSES_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateWebAddresses() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    // update web addresses
    private ContactDetailsSettings updateWebAddresses( ContactDetailsSettings contactDetailsSettings,
        List<MiscValues> webAddresses ) throws InvalidInputException
    {
        LOG.debug( "Method updateWebAddresses() called from ProfileManagementController" );
        if ( contactDetailsSettings == null ) {
            throw new InvalidInputException( "No contact details object found for user" );
        }
        WebAddressSettings webAddressSettings = contactDetailsSettings.getWeb_addresses();
        if ( webAddressSettings == null ) {
            LOG.debug( "No web addresses are added, create new web address object in contact details" );
            webAddressSettings = new WebAddressSettings();
        }

        List<MiscValues> others = null;
        for ( MiscValues webAddress : webAddresses ) {
            String key = webAddress.getKey();
            String value = webAddress.getValue();
            try {
                urlValidationHelper.validateUrl( value );
            } catch ( IOException ioException ) {
                throw new InvalidInputException( "Web address passed was invalid", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }
            if ( key.equalsIgnoreCase( "work" ) ) {
                webAddressSettings.setWork( value );
            } else if ( key.equalsIgnoreCase( "personal" ) ) {
                webAddressSettings.setPersonal( value );
            } else if ( key.equalsIgnoreCase( "blogs" ) ) {
                webAddressSettings.setBlogs( value );
            } else {
                if ( others == null ) {
                    others = new ArrayList<>();
                }
                others.add( webAddress );
            }
        }
        webAddressSettings.setOthers( others );
        contactDetailsSettings.setWeb_addresses( webAddressSettings );
        LOG.debug( "Method updateWebAddresses() finished from ProfileManagementController" );
        return contactDetailsSettings;
    }


    @RequestMapping ( value = "/updatefacebooklink", method = RequestMethod.POST)
    public String updateFacebookLink( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateFacebookLink() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        SocialMediaTokens socialMediaTokens = null;

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }

            String fbLink = request.getParameter( "fblink" );
            try {
                if ( fbLink == null || fbLink.isEmpty() ) {
                    throw new InvalidInputException( "Facebook link passed was null or empty",
                        DisplayMessageConstants.GENERAL_ERROR );
                }
                urlValidationHelper.validateUrl( fbLink );
            } catch ( IOException ioException ) {
                throw new InvalidInputException( "Facebook link passed was invalid", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                socialMediaTokens = companySettings.getSocialMediaTokens();
                socialMediaTokens = updateFacebookToken( socialMediaTokens, fbLink );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, socialMediaTokens );
                companySettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.setCompanySettings( companySettings );
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.FACEBOOK, true );
                    userManagementService.updateCompany( company );
                }
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                socialMediaTokens = regionSettings.getSocialMediaTokens();
                socialMediaTokens = updateFacebookToken( socialMediaTokens, fbLink );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, socialMediaTokens );
                regionSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.getRegionSettings().put( entityId, regionSettings );
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.FACEBOOK, true );
                    userManagementService.updateRegion( region );
                }
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                socialMediaTokens = branchSettings.getSocialMediaTokens();
                socialMediaTokens = updateFacebookToken( socialMediaTokens, fbLink );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, socialMediaTokens );
                branchSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.getRegionSettings().put( entityId, branchSettings );
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.FACEBOOK, true );
                    userManagementService.updateBranch( branch );
                }
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                socialMediaTokens = agentSettings.getSocialMediaTokens();
                socialMediaTokens = updateFacebookToken( socialMediaTokens, fbLink );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, socialMediaTokens );
                agentSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.setAgentSettings( agentSettings );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred in updating fb token.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setSocialMediaTokens( socialMediaTokens );

            LOG.info( "Facebook link updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.FACEBOOK_TOKEN_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating facebook link in profile. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.FACEBOOK_TOKEN_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateFacebookLink() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    private SocialMediaTokens updateFacebookToken( SocialMediaTokens socialMediaTokens, String fbLink )
    {
        LOG.debug( "Method updateFacebookToken() called from ProfileManagementController" );
        if ( socialMediaTokens == null ) {
            LOG.debug( "No social media token in profile added" );
            socialMediaTokens = new SocialMediaTokens();
        }
        if ( socialMediaTokens.getFacebookToken() == null ) {
            socialMediaTokens.setFacebookToken( new FacebookToken() );
        }
        FacebookToken facebookToken = socialMediaTokens.getFacebookToken();
        facebookToken.setFacebookPageLink( fbLink );
        socialMediaTokens.setFacebookToken( facebookToken );
        LOG.debug( "Method updateFacebookToken() finished from ProfileManagementController" );
        return socialMediaTokens;
    }


    @RequestMapping ( value = "/updatetwitterlink", method = RequestMethod.POST)
    public String updateTwitterLink( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateTwitterLink() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        SocialMediaTokens socialMediaTokens = null;

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }

            String twitterLink = request.getParameter( "twitterlink" );
            try {
                if ( twitterLink == null || twitterLink.isEmpty() ) {
                    throw new InvalidInputException( "Twitter link passed was null or empty",
                        DisplayMessageConstants.GENERAL_ERROR );
                }
                urlValidationHelper.validateUrl( twitterLink );
            } catch ( IOException ioException ) {
                throw new InvalidInputException( "LinkedIn link passed was invalid", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                socialMediaTokens = companySettings.getSocialMediaTokens();
                socialMediaTokens = updateTwitterToken( socialMediaTokens, twitterLink );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, socialMediaTokens );
                companySettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.setCompanySettings( companySettings );
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.TWITTER, true );
                    userManagementService.updateCompany( company );
                }
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                socialMediaTokens = regionSettings.getSocialMediaTokens();
                socialMediaTokens = updateTwitterToken( socialMediaTokens, twitterLink );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, socialMediaTokens );
                regionSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.getRegionSettings().put( entityId, regionSettings );

                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.TWITTER, true );
                    userManagementService.updateRegion( region );
                }
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                socialMediaTokens = branchSettings.getSocialMediaTokens();
                socialMediaTokens = updateTwitterToken( socialMediaTokens, twitterLink );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, socialMediaTokens );
                branchSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.getRegionSettings().put( entityId, branchSettings );
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.TWITTER, true );
                    userManagementService.updateBranch( branch );
                }
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                socialMediaTokens = agentSettings.getSocialMediaTokens();
                socialMediaTokens = updateTwitterToken( socialMediaTokens, twitterLink );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, socialMediaTokens );
                agentSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.setAgentSettings( agentSettings );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred in updating twitter token.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setSocialMediaTokens( socialMediaTokens );

            LOG.info( "Twitter link updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.TWITTER_TOKEN_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating twitter link in profile. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.TWITTER_TOKEN_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateTwitterLink() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    private SocialMediaTokens updateTwitterToken( SocialMediaTokens socialMediaTokens, String twitterLink )
    {
        LOG.debug( "Method updateTwitterToken() called from ProfileManagementController" );
        if ( socialMediaTokens == null ) {
            LOG.debug( "No social media token in profile added" );
            socialMediaTokens = new SocialMediaTokens();
        }
        if ( socialMediaTokens.getTwitterToken() == null ) {
            socialMediaTokens.setTwitterToken( new TwitterToken() );
        }
        TwitterToken twitterToken = socialMediaTokens.getTwitterToken();
        twitterToken.setTwitterPageLink( twitterLink );
        socialMediaTokens.setTwitterToken( twitterToken );
        LOG.debug( "Method updateTwitterToken() finished from ProfileManagementController" );
        return socialMediaTokens;
    }


    @RequestMapping ( value = "/updatelinkedinlink", method = RequestMethod.POST)
    public String updateLinkedInLink( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateLinkedInLink() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        SocialMediaTokens socialMediaTokens = null;

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }

            String linkedinLink = request.getParameter( "linkedinlink" );
            try {
                if ( linkedinLink == null || linkedinLink.isEmpty() ) {
                    throw new InvalidInputException( "LinkedIn link passed was null or empty",
                        DisplayMessageConstants.GENERAL_ERROR );
                }
                urlValidationHelper.validateUrl( linkedinLink );
            } catch ( IOException ioException ) {
                throw new InvalidInputException( "LinkedIn link passed was invalid", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                socialMediaTokens = companySettings.getSocialMediaTokens();
                socialMediaTokens = updateLinkedinToken( linkedinLink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, socialMediaTokens );
                companySettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.setCompanySettings( companySettings );
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.LINKED_IN, true );
                    userManagementService.updateCompany( company );
                }
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                socialMediaTokens = regionSettings.getSocialMediaTokens();
                socialMediaTokens = updateLinkedinToken( linkedinLink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, socialMediaTokens );
                regionSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.getRegionSettings().put( entityId, regionSettings );
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.LINKED_IN, true );
                    userManagementService.updateRegion( region );
                }
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                socialMediaTokens = branchSettings.getSocialMediaTokens();
                socialMediaTokens = updateLinkedinToken( linkedinLink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, socialMediaTokens );
                branchSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.getRegionSettings().put( entityId, branchSettings );

                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.LINKED_IN, true );
                    userManagementService.updateBranch( branch );
                }
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                socialMediaTokens = agentSettings.getSocialMediaTokens();
                socialMediaTokens = updateLinkedinToken( linkedinLink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, socialMediaTokens );
                agentSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.setAgentSettings( agentSettings );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred in upadting linkedin token.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setSocialMediaTokens( socialMediaTokens );

            LOG.info( "LinkedIn link updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.LINKEDIN_TOKEN_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating linkedIn link in profile. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.LINKEDIN_TOKEN_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateLinkedInLink() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    private SocialMediaTokens updateLinkedinToken( String linkedinLink, SocialMediaTokens socialMediaTokens )
    {
        LOG.debug( "Method updateLinkedinToken() called from ProfileManagementController" );
        if ( socialMediaTokens == null ) {
            LOG.debug( "No social media token in profile added" );
            socialMediaTokens = new SocialMediaTokens();
        }
        if ( socialMediaTokens.getLinkedInToken() == null ) {
            socialMediaTokens.setLinkedInToken( new LinkedInToken() );
        }
        LinkedInToken linkedIntoken = socialMediaTokens.getLinkedInToken();
        linkedIntoken.setLinkedInPageLink( linkedinLink );
        socialMediaTokens.setLinkedInToken( linkedIntoken );
        LOG.debug( "Method updateLinkedinToken() finished from ProfileManagementController" );
        return socialMediaTokens;
    }


    @RequestMapping ( value = "/updateyelplink", method = RequestMethod.POST)
    public String updateYelpLink( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateYelpLink() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        SocialMediaTokens socialMediaTokens = null;
        long branchId = 0;
        long regionId = 0;
        long companyId = 0;
        long agentId = 0;
        
        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }

            String yelpLink = request.getParameter( "yelplink" );
            try {
                if ( yelpLink == null || yelpLink.isEmpty() ) {
                    throw new InvalidInputException( "Yelp link passed was null or empty",
                        DisplayMessageConstants.GENERAL_ERROR );
                }
                urlValidationHelper.validateUrl( yelpLink );
            } catch ( IOException ioException ) {
                throw new InvalidInputException( "Yelp link passed was invalid", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }
            
            try {
                Map<String, Long> hierarchyDetails = profileManagementService.getHierarchyDetailsByEntity( entityType, entityId );
                branchId = hierarchyDetails.get( CommonConstants.BRANCH_ID_COLUMN );
                regionId = hierarchyDetails.get( CommonConstants.REGION_ID_COLUMN );
                companyId = hierarchyDetails.get( CommonConstants.COMPANY_ID_COLUMN );
                agentId = hierarchyDetails.get( CommonConstants.AGENT_ID_COLUMN );
            } catch ( ProfileNotFoundException e ) {
                LOG.error( "Profile not found for user id : " + entityId + " of type : " + entityType, e );
            }
            
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                socialMediaTokens = companySettings.getSocialMediaTokens();
                socialMediaTokens = updateYelpLink( yelpLink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, socialMediaTokens );
                for ( ProfileStage stage : companySettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "YELP_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( companySettings.getProfileStages(), companySettings,
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );
                companySettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.setCompanySettings( companySettings );
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.YELP, true );
                    userManagementService.updateCompany( company );
                }
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                socialMediaTokens = regionSettings.getSocialMediaTokens();
                socialMediaTokens = updateYelpLink( yelpLink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, socialMediaTokens );
                for ( ProfileStage stage : regionSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "YELP_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( regionSettings.getProfileStages(), regionSettings,
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
                regionSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.getRegionSettings().put( entityId, regionSettings );

                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.LOGO, true );
                    userManagementService.updateRegion( region );
                }
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                socialMediaTokens = branchSettings.getSocialMediaTokens();
                socialMediaTokens = updateYelpLink( yelpLink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, socialMediaTokens );
                for ( ProfileStage stage : branchSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "YELP_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( branchSettings.getProfileStages(), branchSettings,
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                branchSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.getRegionSettings().put( entityId, branchSettings );

                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.YELP, true );
                    userManagementService.updateBranch( branch );
                }
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                socialMediaTokens = agentSettings.getSocialMediaTokens();
                socialMediaTokens = updateYelpLink( yelpLink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, socialMediaTokens );
                for ( ProfileStage stage : agentSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "YELP_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( agentSettings.getProfileStages(), agentSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                agentSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.setAgentSettings( agentSettings );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred in updating yelp token.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setSocialMediaTokens( socialMediaTokens );

            //Add action to social connection history
            String action = "connected";
            SocialUpdateAction socialUpdateAction = new SocialUpdateAction();
            socialUpdateAction.setAction( action );
            socialUpdateAction.setAgentId( agentId );
            socialUpdateAction.setBranchId( branchId );
            socialUpdateAction.setRegionId( regionId );
            socialUpdateAction.setCompanyId( companyId );
            socialUpdateAction.setSocialMediaSource( CommonConstants.YELP_SOCIAL_SITE );
            socialPostDao.addActionToSocialConnectionHistory( socialUpdateAction );
            
            LOG.info( "YelpLinked in link updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.YELP_TOKEN_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating yelp link in profile. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.YELP_TOKEN_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateYelpLink() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    private SocialMediaTokens updateYelpLink( String yelpLink, SocialMediaTokens socialMediaTokens )
    {
        LOG.debug( "Method updateYelpLink() called from ProfileManagementController" );
        if ( socialMediaTokens == null ) {
            LOG.debug( "No social media token in profile added" );
            socialMediaTokens = new SocialMediaTokens();
        }
        if ( socialMediaTokens.getYelpToken() == null ) {
            socialMediaTokens.setYelpToken( new YelpToken() );
        }
        YelpToken yelpToken = socialMediaTokens.getYelpToken();
        yelpToken.setYelpPageLink( yelpLink );
        socialMediaTokens.setYelpToken( yelpToken );
        LOG.debug( "Method updateYelpLink() finished from ProfileManagementController" );
        return socialMediaTokens;
    }


    @RequestMapping ( value = "/updategooglelink", method = RequestMethod.POST)
    public String updateGoogleLink( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateGoogleLink() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        SocialMediaTokens socialMediaTokens = null;

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }

            String gplusLink = request.getParameter( "gpluslink" );
            try {
                if ( gplusLink == null || gplusLink.isEmpty() ) {
                    throw new InvalidInputException( "GooglePlus link passed was null or empty",
                        DisplayMessageConstants.GENERAL_ERROR );
                }
                urlValidationHelper.validateUrl( gplusLink );
            } catch ( IOException ioException ) {
                throw new InvalidInputException( "GooglePlus link passed was invalid", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                socialMediaTokens = companySettings.getSocialMediaTokens();
                socialMediaTokens = updateGoogleToken( socialMediaTokens, gplusLink );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, socialMediaTokens );
                companySettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.setCompanySettings( companySettings );
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.GOOGLE_PLUS, true );
                    userManagementService.updateCompany( company );
                }
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                socialMediaTokens = regionSettings.getSocialMediaTokens();
                socialMediaTokens = updateGoogleToken( socialMediaTokens, gplusLink );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, socialMediaTokens );
                regionSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.getRegionSettings().put( entityId, regionSettings );
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.GOOGLE_PLUS, true );
                    userManagementService.updateRegion( region );
                }
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                socialMediaTokens = branchSettings.getSocialMediaTokens();
                socialMediaTokens = updateGoogleToken( socialMediaTokens, gplusLink );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, socialMediaTokens );
                branchSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.getRegionSettings().put( entityId, branchSettings );
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.GOOGLE_PLUS, true );
                    userManagementService.updateBranch( branch );
                }
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                socialMediaTokens = agentSettings.getSocialMediaTokens();
                socialMediaTokens = updateGoogleToken( socialMediaTokens, gplusLink );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, socialMediaTokens );
                agentSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.setAgentSettings( agentSettings );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred in updating GooglePlus url",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setSocialMediaTokens( socialMediaTokens );

            LOG.info( "GooglePlus link updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.GPLUS_TOKEN_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error(
                "NonFatalException while updating GooglePlus link in profile. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.GPLUS_TOKEN_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateGoogleLink() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    private SocialMediaTokens updateGoogleToken( SocialMediaTokens socialMediaTokens, String gplusLink )
    {
        LOG.debug( "Method updateGoogleToken() called from ProfileManagementController" );
        if ( socialMediaTokens == null ) {
            LOG.debug( "No social media token in profile added" );
            socialMediaTokens = new SocialMediaTokens();
        }
        if ( socialMediaTokens.getGoogleToken() == null ) {
            socialMediaTokens.setGoogleToken( new GoogleToken() );
        }
        GoogleToken googleToken = socialMediaTokens.getGoogleToken();
        googleToken.setProfileLink( gplusLink );
        socialMediaTokens.setGoogleToken( googleToken );
        LOG.debug( "Method updateGoogleToken() finished from ProfileManagementController" );
        return socialMediaTokens;
    }


    @RequestMapping ( value = "/updatezillowlink", method = RequestMethod.POST)
    public String updateZillowLink( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateZillowLink() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        SocialMediaTokens socialMediaTokens = null;

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }

            String zillowlink = request.getParameter( "zillowlink" );
            try {
                if ( zillowlink == null || zillowlink.isEmpty() ) {
                    throw new InvalidInputException( "Zillow link passed was null or empty",
                        DisplayMessageConstants.GENERAL_ERROR );
                }
                urlValidationHelper.validateUrl( zillowlink );
            } catch ( IOException ioException ) {
                throw new InvalidInputException( "Zillow link passed was invalid", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                socialMediaTokens = companySettings.getSocialMediaTokens();
                socialMediaTokens = socialManagementService.checkOrAddZillowLastUpdated( socialMediaTokens );
                socialMediaTokens = updateZillowLink( zillowlink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, socialMediaTokens );
                companySettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.setCompanySettings( companySettings );
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.ZILLOW, true );
                    userManagementService.updateCompany( company );
                }
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                socialMediaTokens = regionSettings.getSocialMediaTokens();
                socialMediaTokens = socialManagementService.checkOrAddZillowLastUpdated( socialMediaTokens );
                socialMediaTokens = updateZillowLink( zillowlink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, socialMediaTokens );
                regionSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.getRegionSettings().put( entityId, regionSettings );
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.ZILLOW, true );
                    userManagementService.updateRegion( region );
                }
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                socialMediaTokens = branchSettings.getSocialMediaTokens();
                socialMediaTokens = socialManagementService.checkOrAddZillowLastUpdated( socialMediaTokens );
                socialMediaTokens = updateZillowLink( zillowlink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, socialMediaTokens );
                branchSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.getRegionSettings().put( entityId, branchSettings );
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.ZILLOW, true );
                    userManagementService.updateBranch( branch );
                }
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                socialMediaTokens = agentSettings.getSocialMediaTokens();
                socialMediaTokens = socialManagementService.checkOrAddZillowLastUpdated( socialMediaTokens );
                socialMediaTokens = updateZillowLink( zillowlink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, socialMediaTokens );
                agentSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.setAgentSettings( agentSettings );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred in updating zillow token.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setSocialMediaTokens( socialMediaTokens );

            LOG.info( "ZillowLink updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.ZILLOW_TOKEN_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating ZillowLink in profile. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.ZILLOW_TOKEN_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateZillowLink() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    private SocialMediaTokens updateZillowLink( String zillowLink, SocialMediaTokens socialMediaTokens )
    {
        LOG.debug( "Method updateZillowLink() called from ProfileManagementController" );
        if ( socialMediaTokens == null ) {
            LOG.debug( "No social media token in profile added" );
            socialMediaTokens = new SocialMediaTokens();
        }
        if ( socialMediaTokens.getZillowToken() == null ) {
            socialMediaTokens.setZillowToken( new ZillowToken() );
        }

        ZillowToken zillowToken = socialMediaTokens.getZillowToken();
        zillowToken.setZillowProfileLink( zillowLink );
        socialMediaTokens.setZillowToken( zillowToken );
        LOG.debug( "Method updateZillowLink() finished from ProfileManagementController" );
        return socialMediaTokens;
    }


    @RequestMapping ( value = "/updateRealtorlink", method = RequestMethod.POST)
    public String updateRealtorLink( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateRealtorLink() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        SocialMediaTokens socialMediaTokens = null;
        long branchId = 0;
        long regionId = 0;
        long companyId = 0;
        long agentId = 0;
        
        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }
            
            try {
                Map<String, Long> hierarchyDetails = profileManagementService.getHierarchyDetailsByEntity( entityType, entityId );
                branchId = hierarchyDetails.get( CommonConstants.BRANCH_ID_COLUMN );
                regionId = hierarchyDetails.get( CommonConstants.REGION_ID_COLUMN );
                companyId = hierarchyDetails.get( CommonConstants.COMPANY_ID_COLUMN );
                agentId = hierarchyDetails.get( CommonConstants.AGENT_ID_COLUMN );
            } catch ( ProfileNotFoundException e ) {
                LOG.error( "Profile not found for user id : " + entityId + " of type : " + entityType, e );
            }
            
            String realtorLink = request.getParameter( "realtorLink" );
            try {
                if ( realtorLink == null || realtorLink.isEmpty() ) {
                    throw new InvalidInputException( "realtorLink passed was null or empty",
                        DisplayMessageConstants.GENERAL_ERROR );
                }
                urlValidationHelper.validateUrl( realtorLink );
            } catch ( IOException ioException ) {
                throw new InvalidInputException( "realtorLink passed was invalid", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                socialMediaTokens = companySettings.getSocialMediaTokens();
                socialMediaTokens = updateRealtorLink( realtorLink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, socialMediaTokens );
                companySettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.setCompanySettings( companySettings );
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.REALTOR, true );
                    userManagementService.updateCompany( company );
                }
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                socialMediaTokens = regionSettings.getSocialMediaTokens();
                socialMediaTokens = updateRealtorLink( realtorLink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, socialMediaTokens );
                regionSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.getRegionSettings().put( entityId, regionSettings );
                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.REALTOR, true );
                    userManagementService.updateRegion( region );
                }
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                socialMediaTokens = branchSettings.getSocialMediaTokens();
                socialMediaTokens = updateRealtorLink( realtorLink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, socialMediaTokens );
                branchSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.getRegionSettings().put( entityId, branchSettings );
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.REALTOR, true );
                    userManagementService.updateBranch( branch );
                }
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                socialMediaTokens = agentSettings.getSocialMediaTokens();
                socialMediaTokens = updateRealtorLink( realtorLink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, socialMediaTokens );
                agentSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.setAgentSettings( agentSettings );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred in updating lendingTree token.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setSocialMediaTokens( socialMediaTokens );

            //Add action to social connection history
            String action = "connected";
            SocialUpdateAction socialUpdateAction = new SocialUpdateAction();
            socialUpdateAction.setAction( action );
            socialUpdateAction.setAgentId( agentId );
            socialUpdateAction.setBranchId( branchId );
            socialUpdateAction.setRegionId( regionId );
            socialUpdateAction.setCompanyId( companyId );
            socialUpdateAction.setSocialMediaSource( CommonConstants.REALTOR_SOCIAL_SITE );
            
            socialPostDao.addActionToSocialConnectionHistory( socialUpdateAction );
            
            LOG.info( "realtor updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.REALTOR_TOKEN_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating RealtorLink in profile. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.REALTOR_TOKEN_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateRealtorLink() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    private SocialMediaTokens updateRealtorLink( String realtorLink, SocialMediaTokens socialMediaTokens )
    {
        LOG.debug( "Method updateRealtorLink() called from ProfileManagementController" );
        if ( socialMediaTokens == null ) {
            LOG.debug( "No social media token in profile added" );
            socialMediaTokens = new SocialMediaTokens();
        }
        if ( socialMediaTokens.getRealtorToken() == null ) {
            socialMediaTokens.setRealtorToken( new RealtorToken() );
        }

        RealtorToken realtorToken = socialMediaTokens.getRealtorToken();
        realtorToken.setRealtorProfileLink( realtorLink );
        socialMediaTokens.setRealtorToken( realtorToken );
        LOG.debug( "Method updateLendingTreeLink() finished from ProfileManagementController" );
        return socialMediaTokens;
    }


    @RequestMapping ( value = "/updatelendingtreelink", method = RequestMethod.POST)
    public String updateLendingTreeLink( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateLendingTreeLink() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        SocialMediaTokens socialMediaTokens = null;
        long branchId = 0;
        long regionId = 0;
        long companyId = 0;
        long agentId = 0;
        
        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }
            
            try {
                Map<String, Long> hierarchyDetails = profileManagementService.getHierarchyDetailsByEntity( entityType, entityId );
                branchId = hierarchyDetails.get( CommonConstants.BRANCH_ID_COLUMN );
                regionId = hierarchyDetails.get( CommonConstants.REGION_ID_COLUMN );
                companyId = hierarchyDetails.get( CommonConstants.COMPANY_ID_COLUMN );
                agentId = hierarchyDetails.get( CommonConstants.AGENT_ID_COLUMN );
            } catch ( ProfileNotFoundException e ) {
                LOG.error( "Profile not found for user id : " + entityId + " of type : " + entityType, e );
            }
            
            String lendingTreeLink = request.getParameter( "lendingTreeLink" );
            try {
                if ( lendingTreeLink == null || lendingTreeLink.isEmpty() ) {
                    throw new InvalidInputException( "lendingTreeLink passed was null or empty",
                        DisplayMessageConstants.GENERAL_ERROR );
                }
                urlValidationHelper.validateUrl( lendingTreeLink );
            } catch ( IOException ioException ) {
                throw new InvalidInputException( "lendingTreeLink passed was invalid", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                socialMediaTokens = companySettings.getSocialMediaTokens();
                socialMediaTokens = updateLendingTreeLink( lendingTreeLink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, socialMediaTokens );
                companySettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.setCompanySettings( companySettings );
                Company company = userManagementService.getCompanyById( companySettings.getIden() );
                if ( company != null ) {
                    settingsSetter.setSettingsValueForCompany( company, SettingsForApplication.LENDING_TREE, true );
                    userManagementService.updateCompany( company );
                }
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                socialMediaTokens = regionSettings.getSocialMediaTokens();
                socialMediaTokens = updateLendingTreeLink( lendingTreeLink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, socialMediaTokens );
                regionSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.getRegionSettings().put( entityId, regionSettings );

                Region region = userManagementService.getRegionById( regionSettings.getIden() );
                if ( region != null ) {
                    settingsSetter.setSettingsValueForRegion( region, SettingsForApplication.LENDING_TREE, true );
                    userManagementService.updateRegion( region );
                }
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                socialMediaTokens = branchSettings.getSocialMediaTokens();
                socialMediaTokens = updateLendingTreeLink( lendingTreeLink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, socialMediaTokens );
                branchSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.getRegionSettings().put( entityId, branchSettings );
                Branch branch = userManagementService.getBranchById( branchSettings.getIden() );
                if ( branch != null ) {
                    settingsSetter.setSettingsValueForBranch( branch, SettingsForApplication.LENDING_TREE, true );
                    userManagementService.updateBranch( branch );
                }
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                socialMediaTokens = agentSettings.getSocialMediaTokens();
                socialMediaTokens = updateLendingTreeLink( lendingTreeLink, socialMediaTokens );
                profileManagementService.updateSocialMediaTokens(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, socialMediaTokens );
                agentSettings.setSocialMediaTokens( socialMediaTokens );
                userSettings.setAgentSettings( agentSettings );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred in updating lendingTree token.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setSocialMediaTokens( socialMediaTokens );

            //Add action to social connection history
            String action = "connected";
            SocialUpdateAction socialUpdateAction = new SocialUpdateAction();
            socialUpdateAction.setAction( action );
            socialUpdateAction.setAgentId( agentId );
            socialUpdateAction.setBranchId( branchId );
            socialUpdateAction.setRegionId( regionId );
            socialUpdateAction.setCompanyId( companyId );
            socialUpdateAction.setSocialMediaSource( CommonConstants.LENDINGTREE_SOCIAL_SITE );
            
            socialPostDao.addActionToSocialConnectionHistory( socialUpdateAction );
            
            LOG.info( "lendingTree updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.LENDINGTREE_TOKEN_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating ZillowLink in profile. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.LENDINGTREE_TOKEN_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateLendingTreeLink() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    private SocialMediaTokens updateLendingTreeLink( String lendingTreeLink, SocialMediaTokens socialMediaTokens )
    {
        LOG.debug( "Method updateLendingTreeLink() called from ProfileManagementController" );
        if ( socialMediaTokens == null ) {
            LOG.debug( "No social media token in profile added" );
            socialMediaTokens = new SocialMediaTokens();
        }
        if ( socialMediaTokens.getLendingTreeToken() == null ) {
            socialMediaTokens.setLendingTreeToken( new LendingTreeToken() );
        }

        LendingTreeToken lendingTreeToken = socialMediaTokens.getLendingTreeToken();
        lendingTreeToken.setLendingTreeProfileLink( lendingTreeLink );
        socialMediaTokens.setLendingTreeToken( lendingTreeToken );
        LOG.debug( "Method updateLendingTreeLink() finished from ProfileManagementController" );
        return socialMediaTokens;
    }


    /**
     * Method to update achievements in profile
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/updateachievements", method = RequestMethod.POST)
    public String updateAchievements( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateAchievements() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        List<Achievement> achievements = null;

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }

            String payload = request.getParameter( "achievementList" );
            try {
                if ( payload == null ) {
                    throw new InvalidInputException( "Acheivements passed was null" );
                }
                ObjectMapper mapper = new ObjectMapper();
                achievements = mapper.readValue( payload,
                    TypeFactory.defaultInstance().constructCollectionType( List.class, Achievement.class ) );
            } catch ( IOException ioException ) {
                throw new NonFatalException( "Error occurred while parsing json", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                achievements = profileManagementService.addAchievements(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, achievements );
                companySettings.setAchievements( achievements );
                userSettings.setCompanySettings( companySettings );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                achievements = profileManagementService.addAchievements(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, achievements );
                regionSettings.setAchievements( achievements );
                userSettings.getRegionSettings().put( entityId, regionSettings );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                achievements = profileManagementService.addAchievements(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, achievements );
                branchSettings.setAchievements( achievements );
                userSettings.getRegionSettings().put( entityId, branchSettings );
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                achievements = profileManagementService.addAgentAchievements(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, achievements );
                agentSettings.setAchievements( achievements );
                userSettings.setAgentSettings( agentSettings );
                for ( ProfileStage stage : agentSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "ACHIEVEMENTS_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( agentSettings.getProfileStages(), agentSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );

            } else {
                throw new InvalidInputException( "Invalid input exception occurred in adding achievements.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setAchievements( achievements );

            LOG.info( "Achievements updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.ACHIEVEMENT_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating achievement details. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.ACHIEVEMENT_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateAchievements() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    /**
     * Method to update associations in profile
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/updateassociations", method = RequestMethod.POST)
    public String updateAssociations( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateAssociations() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        List<Association> associations = null;

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }

            String payload = request.getParameter( "associationList" );
            try {
                if ( payload == null ) {
                    throw new InvalidInputException( "Association passed was null or empty" );
                }
                ObjectMapper mapper = new ObjectMapper();
                associations = mapper.readValue( payload,
                    TypeFactory.defaultInstance().constructCollectionType( List.class, Association.class ) );
            } catch ( IOException ioException ) {
                throw new NonFatalException( "Error occurred while parsing the Json.", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                associations = profileManagementService.addAssociations(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, associations );
                companySettings.setAssociations( associations );
                userSettings.setCompanySettings( companySettings );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                associations = profileManagementService.addAssociations(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, associations );
                regionSettings.setAssociations( associations );
                userSettings.getRegionSettings().put( entityId, regionSettings );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                associations = profileManagementService.addAssociations(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, associations );
                branchSettings.setAssociations( associations );
                userSettings.getRegionSettings().put( entityId, branchSettings );
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                associations = profileManagementService.addAgentAssociations(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, associations );
                agentSettings.setAssociations( associations );
                userSettings.setAgentSettings( agentSettings );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred in adding associations.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setAssociations( associations );

            LOG.info( "Associations updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.ASSOCIATION_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating licence details. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.ASSOCIATION_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateAssociations() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    /**
     * Method to update licenses for profile
     * 
     * @param model
     * @param request
     */
    @RequestMapping ( value = "/updatelicenses", method = RequestMethod.POST)
    public String updateProfileLicenses( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateProfileLicenses() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        Licenses licenses = null;

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }

            String payload = request.getParameter( "licenceList" );
            List<String> authorisedIn = null;
            try {
                if ( payload == null ) {
                    throw new InvalidInputException( "Licenses passed was null or empty" );
                }
                ObjectMapper mapper = new ObjectMapper();
                authorisedIn = mapper.readValue( payload,
                    TypeFactory.defaultInstance().constructCollectionType( List.class, String.class ) );
            } catch ( IOException ioException ) {
                throw new NonFatalException( "Error occurred while parsing json.", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                licenses = profileManagementService.addLicences(
                    MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, authorisedIn );
                companySettings.setLicenses( licenses );
                userSettings.setCompanySettings( companySettings );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                licenses = profileManagementService.addLicences(
                    MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, regionSettings, authorisedIn );
                regionSettings.setLicenses( licenses );
                userSettings.getRegionSettings().put( entityId, regionSettings );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                licenses = profileManagementService.addLicences(
                    MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, branchSettings, authorisedIn );
                branchSettings.setLicenses( licenses );
                userSettings.getRegionSettings().put( entityId, branchSettings );
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                licenses = profileManagementService.addAgentLicences(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings, authorisedIn );
                agentSettings.setLicenses( licenses );
                userSettings.setAgentSettings( agentSettings );
                for ( ProfileStage stage : agentSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "LICENSE_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( agentSettings.getProfileStages(), agentSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred in adding associations.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setLicenses( licenses );

            LOG.info( "Licence details updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.LICENSES_UPDATE_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating licence details. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.LICENSES_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateProfileLicenses() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    @RequestMapping ( value = "/updateexpertise", method = RequestMethod.POST)
    public String updateExpertise( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateExpertise() called from ProfileManagementController" );
        HttpSession session = request.getSession( false );

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }

            String payload = request.getParameter( "expertiseList" );
            List<String> expertiseList = null;
            try {
                if ( payload == null ) {
                    throw new InvalidInputException( "Expertise passed was null or empty" );
                }
                ObjectMapper mapper = new ObjectMapper();
                expertiseList = mapper.readValue( payload,
                    TypeFactory.defaultInstance().constructCollectionType( List.class, String.class ) );
            } catch ( IOException ioException ) {
                throw new NonFatalException( "Error occurred while parsing json.", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }

            if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                profileManagementService.updateAgentExpertise( agentSettings, expertiseList );
                agentSettings.setExpertise( expertiseList );
                userSettings.setAgentSettings( agentSettings );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred in adding expertise.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            LOG.info( "Expertise list updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.EXPERTISE_UPDATE_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating expertise. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.EXPERTISE_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateExpertise() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    @RequestMapping ( value = "/updatehobbies", method = RequestMethod.POST)
    public String updateHobbies( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateHobbies() called from ProfileManagementController" );
        HttpSession session = request.getSession( false );

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }

            String payload = request.getParameter( "hobbiesList" );
            List<String> hobbiesList = null;
            try {
                if ( payload == null ) {
                    throw new InvalidInputException( "Hobbies passed was null or empty" );
                }
                ObjectMapper mapper = new ObjectMapper();
                hobbiesList = mapper.readValue( payload,
                    TypeFactory.defaultInstance().constructCollectionType( List.class, String.class ) );
            } catch ( IOException ioException ) {
                throw new NonFatalException( "Error occurred while parsing json.", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }

            if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                profileManagementService.updateAgentHobbies( agentSettings, hobbiesList );
                for ( ProfileStage stage : agentSettings.getProfileStages() ) {
                    if ( stage.getProfileStageKey().equalsIgnoreCase( "HOBBIES_PRF" ) ) {
                        stage.setStatus( CommonConstants.STATUS_INACTIVE );
                    }
                }
                profileManagementService.updateProfileStages( agentSettings.getProfileStages(), agentSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
                agentSettings.setHobbies( hobbiesList );
                userSettings.setAgentSettings( agentSettings );
            } else {
                throw new InvalidInputException( "Invalid input exception occurred in adding hobbies.",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            LOG.info( "Hobbies list updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.HOBBIES_UPDATE_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating hobbies. Reason :" + nonFatalException.getMessage(), nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.HOBBIES_UPDATE_UNSUCCESSFUL,
                DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateHobbies() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    // JIRA SS-97 by RM-06 : EOC

    /*
     * Method to find a user on the basis of first and last names provided.
     */
    @RequestMapping ( value = "/findapro", method = RequestMethod.GET)
    public String findAProfile( Model model, HttpServletRequest request )
    {
        LOG.info( "Method findAProfile called." );

        boolean isBotRequest = botRequestUtils.checkBotRequest( request );

        String patternFirst = request.getParameter( "find-pro-first-name" );
        String patternLast = request.getParameter( "find-pro-last-name" );

        if ( patternFirst == null && patternLast == null ) {
            LOG.error( "Invalid search key passed in method findAProfile()." );
        } else {
            model.addAttribute( "patternFirst", patternFirst.trim() );
            model.addAttribute( "patternLast", patternLast.trim() );
        }

        LOG.info( "Method findAProfile finished." );

        if ( isBotRequest ) {
            UserListFromSearch userList = new UserListFromSearch();
            List<Long> userIds = new ArrayList<Long>();
            List<ProListUser> users = new ArrayList<ProListUser>();
            try {
                if ( patternFirst == null && patternLast == null ) {
                    LOG.error( "Invalid search key passed in method findAProfileScroll()." );
                    throw new InvalidInputException( messageUtils.getDisplayMessage(
                        DisplayMessageConstants.INVALID_FIRSTORLAST_NAME_PATTERN, DisplayMessageType.ERROR_MESSAGE )
                        .getMessage() );
                }
                if ( !patternFirst.trim().matches( CommonConstants.FINDAPRO_FIRST_NAME_REGEX )
                    && !patternLast.trim().matches( CommonConstants.FINDAPRO_LAST_NAME_REGEX ) ) {
                    LOG.error( "Invalid search key passed in method findAProfileScroll()." );
                    throw new InvalidInputException( messageUtils.getDisplayMessage(
                        DisplayMessageConstants.INVALID_FIRSTORLAST_NAME_PATTERN, DisplayMessageType.ERROR_MESSAGE )
                        .getMessage() );
                }

                int startIndex = 0;
                int batchSize = 100;

                try {
                    SolrDocumentList results = solrSearchService.searchUsersByFirstOrLastName( patternFirst.trim(),
                        patternLast.trim(), startIndex, batchSize );
                    for ( SolrDocument solrDocument : results ) {
                        userIds.add( (Long) solrDocument.getFieldValue( "userId" ) );
                    }
                    users = userManagementService.getMultipleUsersByUserId( userIds );

                    userList.setUsers( users );
                    userList.setUserFound( results.getNumFound() );
                    model.addAttribute( "usersList", userList );
                } catch ( MalformedURLException e ) {
                    LOG.error( "Error occured while searching in findAProfileScroll(). Reason is ", e );
                    throw new NonFatalException( "Error occured while searching in findAProfileScroll()", e );
                }
            } catch ( NonFatalException nonFatalException ) {
                LOG.error(
                    "NonFatalException while searching in findAProfileScroll(). Reason : " + nonFatalException.getMessage(),
                    nonFatalException );
                return JspResolver.ERROR_PAGE;
            }
            return JspResolver.PROFILE_LIST_NOSCRIPT;
        } else {
            return JspResolver.PROFILE_LIST;
        }
    }


    @ResponseBody
    @RequestMapping ( value = "/findaproscroll", method = RequestMethod.POST)
    public String findAProfileScroll( Model model, HttpServletRequest request )
    {
        LOG.info( "Method findAProfileScroll called." );
        UserListFromSearch userList = new UserListFromSearch();
        List<Long> userIds = new ArrayList<Long>();
        List<ProListUser> users = new ArrayList<ProListUser>();

        try {
            String patternFirst = request.getParameter( "find-pro-first-name" );
            String patternLast = request.getParameter( "find-pro-last-name" );

            if ( patternFirst == null && patternLast == null ) {
                LOG.error( "Invalid search key passed in method findAProfileScroll()." );
                throw new InvalidInputException( messageUtils.getDisplayMessage(
                    DisplayMessageConstants.INVALID_FIRSTORLAST_NAME_PATTERN, DisplayMessageType.ERROR_MESSAGE ).getMessage() );
            }
            if ( !patternFirst.trim().matches( CommonConstants.FINDAPRO_FIRST_NAME_REGEX )
                && !patternLast.trim().matches( CommonConstants.FINDAPRO_LAST_NAME_REGEX ) ) {
                LOG.error( "Invalid search key passed in method findAProfileScroll()." );
                throw new InvalidInputException( messageUtils.getDisplayMessage(
                    DisplayMessageConstants.INVALID_FIRSTORLAST_NAME_PATTERN, DisplayMessageType.ERROR_MESSAGE ).getMessage() );
            }

            int startIndex;
            try {
                startIndex = Integer.parseInt( request.getParameter( "find-pro-start-index" ) );
            } catch ( NumberFormatException e ) {
                startIndex = CommonConstants.FIND_PRO_START_INDEX;
            }

            int batchSize;
            try {
                batchSize = Integer.parseInt( request.getParameter( "find-pro-row-size" ) );
            } catch ( NumberFormatException e ) {
                batchSize = CommonConstants.FIND_PRO_BATCH_SIZE;
            }

            try {
                SolrDocumentList results = solrSearchService.searchUsersByFirstOrLastName( patternFirst.trim(),
                    patternLast.trim(), startIndex, batchSize );
                for ( SolrDocument solrDocument : results ) {
                    userIds.add( (Long) solrDocument.getFieldValue( "userId" ) );
                }
                users = userManagementService.getMultipleUsersByUserId( userIds );

                userList.setUsers( users );
                userList.setUserFound( results.getNumFound() );
            } catch ( MalformedURLException e ) {
                LOG.error( "Error occured while searching in findAProfileScroll(). Reason is ", e );
                throw new NonFatalException( "Error occured while searching in findAProfileScroll()", e );
            }
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while searching in findAProfileScroll(). Reason : " + nonFatalException.getMessage(),
                nonFatalException );
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrCode( ErrorCodes.REQUEST_FAILED );
            errorResponse.setErrMessage( nonFatalException.getMessage() );
            return new Gson().toJson( errorResponse );
        }

        LOG.info( "Method findAProfileScroll finished." );
        return new Gson().toJson( userList );
    }


    /**
     * Method to initiate find a pro page based on profile level and iden
     * 
     * @param profileLevel
     * @param iden
     * @return
     */
    @RequestMapping ( "/initfindapro")
    public String initProListByProfileLevelPage( Model model, HttpServletRequest request,
        @QueryParam ( value = "profileLevel") String profileLevel, @QueryParam ( value = "iden") Long iden,
        @QueryParam ( value = "searchCriteria") String searchCriteria )
    {
        LOG.info( "Method initProListByProfileLevelPage called for profileLevel:" + profileLevel + " and iden:" + iden
            + " and searchCriteria:" + searchCriteria );
        DisplayMessage message = null;

        boolean isBotRequest = botRequestUtils.checkBotRequest( request );

        try {
            if ( profileLevel == null || profileLevel.isEmpty() ) {
                throw new InvalidInputException( "profile level is invalid in initProListByProfileLevelPage",
                    DisplayMessageConstants.INVALID_PROFILE_LEVEL );
            }
            if ( iden == null || iden <= 0l ) {
                throw new InvalidInputException( "iden is invalid in initProListByProfileLevelPage",
                    DisplayMessageConstants.GENERAL_ERROR );
            }
            Collection<UserFromSearch> solrResult = profileManagementService
                .getProListByProfileLevel( iden, profileLevel, 0, 0 );
            if ( solrResult != null ) {
                model.addAttribute( "numfound", solrResult.size() );
            }
            model.addAttribute( "profileLevel", profileLevel );
            model.addAttribute( "iden", iden );
            model.addAttribute( "searchCriteria", searchCriteria );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException in initProListByProfileLevelPage.Reason:" + e.getMessage(), e );
            message = messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE );
            model.addAttribute( "message", message );
            return JspResolver.MESSAGE_HEADER;
        }
        LOG.info( "Method initProListByProfileLevelPage executed successfully" );
        if ( isBotRequest ) {
            int start = -1;
            int numRows = -1;

            try {
                try {
                    UserListFromSearch userList = new UserListFromSearch();
                    List<Long> userIds = new ArrayList<Long>();
                    List<ProListUser> users = new ArrayList<ProListUser>();
                    String idenFieldName = null;
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
                    model.addAttribute( "usersList", userList );
                } catch ( InvalidInputException e ) {
                    throw new InternalServerException( new ProfileServiceErrorCode(
                        CommonConstants.ERROR_CODE_PRO_LIST_FETCH_FAILURE, CommonConstants.SERVICE_CODE_PRO_LIST_FETCH,
                        "Could not fetch users list." ), e.getMessage() );
                } catch ( SolrException e ) {
                    throw new InternalServerException( new ProfileServiceErrorCode(
                        CommonConstants.ERROR_CODE_PRO_LIST_FETCH_FAILURE, CommonConstants.SERVICE_CODE_PRO_LIST_FETCH,
                        "Could not fetch users list." ), e.getMessage() );
                }
            } catch ( InternalServerException e ) {
                return JspResolver.ERROR_PAGE;
            }
            return JspResolver.PROFILE_LIST_NOSCRIPT;
        } else {
            return JspResolver.PROFILE_LIST;
        }
    }


    @RequestMapping ( value = "/findcompany")
    public String findCompanies( Model model, HttpServletRequest request )
    {

        LOG.info( "Method findCompanies() called" );

        String verticalName = request.getParameter( "verticalName" );
        try {
            if ( verticalName == null || verticalName.isEmpty() ) {
                throw new InvalidInputException( "Null/Empty vertical name" );
            }

            List<OrganizationUnitSettings> companyList = profileManagementService.getCompanyList( verticalName );
            if ( companyList != null && companyList.size() > 0 ) {
                model.addAttribute( "companyList", companyList );
                model.addAttribute( "numFound", companyList.size() );
            }
            model.addAttribute( "verticalName", verticalName );

        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException caught in findCompanies() method, reason : " + e.getMessage() );
        }
        return JspResolver.COMPANY_LIST;
    }


    /**
     * Method to return company profile page
     * 
     * @param profileName
     * @param model
     * @return
     */
    /*
     * @RequestMapping(value = "/companyprofile/{profileName}", method =
     * RequestMethod.GET) public String initCompanyProfilePage(@PathVariable
     * String profileName, Model model) {
     * LOG.info("Service to initiate company profile page called"); String
     * message = null; if (profileName == null || profileName.isEmpty()) {
     * message = messageUtils.getDisplayMessage(DisplayMessageConstants.
     * INVALID_COMPANY_PROFILENAME, DisplayMessageType.ERROR_MESSAGE)
     * .getMessage(); model.addAttribute("message", message); return
     * JspResolver.MESSAGE_HEADER; } model.addAttribute("companyProfileName",
     * profileName); model.addAttribute("profileLevel",
     * CommonConstants.PROFILE_LEVEL_COMPANY);
     * LOG.info("Service to initiate company profile page executed successfully"
     * ); return JspResolver.PROFILE_PAGE; }
     */

    /**
     * Method to return region profile page
     * 
     * @param companyProfileName
     * @param regionProfileName
     * @param model
     * @return
     */
    /*
     * @RequestMapping(value =
     * "/regionprofile/{companyProfileName}/region/{regionProfileName}") public
     * String initRegionProfilePage(@PathVariable String companyProfileName,
     * @PathVariable String regionProfileName, Model model) {
     * LOG.info("Service to initiate region profile page called"); String
     * message = null; if (companyProfileName == null ||
     * companyProfileName.isEmpty()) { message =
     * messageUtils.getDisplayMessage(DisplayMessageConstants
     * .INVALID_COMPANY_PROFILENAME, DisplayMessageType.ERROR_MESSAGE)
     * .getMessage(); model.addAttribute("message", message); return
     * JspResolver.MESSAGE_HEADER; } if (regionProfileName == null ||
     * regionProfileName.isEmpty()) { message =
     * messageUtils.getDisplayMessage(DisplayMessageConstants
     * .INVALID_REGION_PROFILENAME, DisplayMessageType.ERROR_MESSAGE)
     * .getMessage(); model.addAttribute("message", message); return
     * JspResolver.MESSAGE_HEADER; } model.addAttribute("companyProfileName",
     * companyProfileName); model.addAttribute("regionProfileName",
     * regionProfileName); model.addAttribute("profileLevel",
     * CommonConstants.PROFILE_LEVEL_REGION);
     * LOG.info("Service to initiate region profile page executed successfully"
     * ); return JspResolver.PROFILE_PAGE; }
     */

    /**
     * Method to return branch profile page
     * 
     * @param companyProfileName
     * @param branchProfileName
     * @param model
     * @return
     */
    /*
     * @RequestMapping(value =
     * "/branchprofile/{companyProfileName}/branch/{branchProfileName}") public
     * String initBranchProfilePage(@PathVariable String companyProfileName,
     * @PathVariable String branchProfileName, Model model) {
     * LOG.info("Service to initiate branch profile page called"); String
     * message = null; if (companyProfileName == null ||
     * companyProfileName.isEmpty()) { message =
     * messageUtils.getDisplayMessage(DisplayMessageConstants
     * .INVALID_COMPANY_PROFILENAME, DisplayMessageType.ERROR_MESSAGE)
     * .getMessage(); model.addAttribute("message", message); return
     * JspResolver.MESSAGE_HEADER; } if (branchProfileName == null ||
     * branchProfileName.isEmpty()) { message =
     * messageUtils.getDisplayMessage(DisplayMessageConstants
     * .INVALID_BRANCH_PROFILENAME, DisplayMessageType.ERROR_MESSAGE)
     * .getMessage(); model.addAttribute("message", message); return
     * JspResolver.MESSAGE_HEADER; } model.addAttribute("companyProfileName",
     * companyProfileName); model.addAttribute("branchProfileName",
     * branchProfileName); model.addAttribute("profileLevel",
     * CommonConstants.PROFILE_LEVEL_BRANCH);
     * LOG.info("Service to initiate branch profile page executed successfully"
     * ); return JspResolver.PROFILE_PAGE; }
     */

    /**
     * Method to return agent profile page
     * 
     * @param agentProfileName
     * @param model
     * @return
     */
    /*
     * @RequestMapping(value = "/individualprofile/{agentProfileName}") public
     * String initBranchProfilePage(@PathVariable String agentProfileName, Model
     * model) { LOG.info("Service to initiate agent profile page called");
     * String message = null; if (agentProfileName == null ||
     * agentProfileName.isEmpty()) { message =
     * messageUtils.getDisplayMessage(DisplayMessageConstants
     * .INVALID_INDIVIDUAL_PROFILENAME, DisplayMessageType.ERROR_MESSAGE)
     * .getMessage(); model.addAttribute("message", message); return
     * JspResolver.MESSAGE_HEADER; } model.addAttribute("agentProfileName",
     * agentProfileName); model.addAttribute("profileLevel",
     * CommonConstants.PROFILE_LEVEL_INDIVIDUAL);
     * LOG.info("Service to initiate agent profile page executed successfully");
     * return JspResolver.PROFILE_PAGE; }
     */

    // Fetch Admin hierarchy
    @RequestMapping ( value = "/getadminhierarchy", method = RequestMethod.GET)
    public String getAdminHierarchy( Model model, HttpServletRequest request ) throws ProfileNotFoundException
    {
        LOG.info( "Method getAdminHierarchy() called from ProfileManagementController" );

        try {
            String entityType = (String) request.getSession( false ).getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                model = getCompanyHierarchy( model, request );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                model = getRegionHierarchy( model, request );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                model = getBranchHierarchy( model, request );
            }
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException while fetching hierarchy. Reason: " + e.getMessage(), e );
            model
                .addAttribute( "message", messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method getAdminHierarchy() finished from ProfileManagementController" );
        return JspResolver.PROFILE_HIERARCHY;
    }


    private Model getCompanyHierarchy( Model model, HttpServletRequest request ) throws InvalidInputException,
        ProfileNotFoundException
    {
        LOG.debug( "Method getCompanyHierarchy() called from ProfileManagementController" );
        List<Region> regions;
        List<Branch> branches;
        List<AgentSettings> individuals;
        String companyProfileName = request.getParameter( "companyProfileName" );
        if ( companyProfileName == "" ) {
            LOG.error( "Invalid companyProfileName passed in method getCompanyHierarchy()." );
            throw new InvalidInputException( "Invalid companyProfileName passed in method getCompanyHierarchy()." );
        }

        // Fetching Regions under Company
        regions = organizationManagementService.getRegionsForCompany( companyProfileName );
        model.addAttribute( "regions", regions );

        // Fetching Branches under Company
        try {
            branches = organizationManagementService.getBranchesUnderCompany( companyProfileName );
            model.addAttribute( "branches", branches );
        } catch ( NoRecordsFetchedException e ) {
            LOG.error( "NoRecordsFetchedException while fetching company hierarchy branches. Reason: " + e.getMessage(), e );
            model
                .addAttribute( "message", messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        // Fetching Individuals under Company
        try {
            individuals = profileManagementService.getIndividualsForCompany( companyProfileName );
            model.addAttribute( "individuals", individuals );
        } catch ( NoRecordsFetchedException e ) {
            LOG.error( "NoRecordsFetchedException while fetching company hierarchy individuals. Reason: " + e.getMessage(), e );
            model
                .addAttribute( "message", messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.debug( "Method getCompanyHierarchy() finished from ProfileManagementController" );
        return model;
    }


    @RequestMapping ( value = "/getcompanyhierarchy", method = RequestMethod.GET)
    public String getCompanyHierarchyDirect( Model model, HttpServletRequest request ) throws ProfileNotFoundException
    {
        LOG.info( "Method getCompanyHierarchyDirect() called from ProfileManagementController" );
        try {
            model = getCompanyHierarchy( model, request );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException while fetching company hierarchy. Reason: " + e.getMessage(), e );
            model
                .addAttribute( "message", messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method getCompanyHierarchyDirect() finished from ProfileManagementController" );
        return JspResolver.PROFILE_HIERARCHY;
    }


    @RequestMapping ( value = "/getregionhierarchy", method = RequestMethod.GET)
    public String getRegionHierarchyOnClick( Model model, HttpServletRequest request )
    {
        LOG.info( "Method getRegionHierarchyOnClick() called from ProfileManagementController" );
        try {
            model = getRegionHierarchy( model, request );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException while fetching region hierarchy. Reason: " + e.getMessage(), e );
            model
                .addAttribute( "message", messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method getRegionHierarchyOnClick() finished from ProfileManagementController" );
        return JspResolver.PROFILE_HIERARCHY_CLICK_REGION;
    }


    private Model getRegionHierarchy( Model model, HttpServletRequest request ) throws InvalidInputException
    {
        LOG.debug( "Method getRegionHierarchy() called from ProfileManagementController" );
        List<Branch> branches;
        List<AgentSettings> individuals;

        long regionId = 0l;
        try {
            regionId = Long.parseLong( request.getParameter( "regionId" ) );
        } catch ( NumberFormatException e ) {
            LOG.error( "NumberFormatException while parsing regionId in getRegionHierarchy.Reason :" + e.getMessage() );
            throw new InvalidInputException( "Invalid regionId passed in method getRegionHierarchy()." );
        }
        if ( regionId == 0l ) {
            throw new InvalidInputException( "Invalid regionId passed in method getRegionHierarchy().",
                DisplayMessageConstants.GENERAL_ERROR );
        }

        // Fetching Branches under Region
        branches = organizationManagementService.getBranchesByRegionId( regionId );
        model.addAttribute( "branches", branches );

        // Fetching Individuals under Region
        try {
            individuals = profileManagementService.getIndividualsByRegionId( regionId );
            model.addAttribute( "individuals", individuals );
        } catch ( NoRecordsFetchedException e ) {
            LOG.error( "NoRecordsFetchedException while fetching region hierarchy. Reason: " + e.getMessage(), e );
            model
                .addAttribute( "message", messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.debug( "Method getRegionHierarchy() finished from ProfileManagementController" );
        return model;
    }


    @RequestMapping ( value = "/getbranchhierarchy", method = RequestMethod.GET)
    public String getBranchHierarchyOnClick( Model model, HttpServletRequest request )
    {
        LOG.info( "Method getBranchHierarchyOnClick() called from ProfileManagementController" );
        try {
            model = getBranchHierarchy( model, request );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException while fetching branch hierarchy. Reason: " + e.getMessage(), e );
            model
                .addAttribute( "message", messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method getBranchHierarchyOnClick() finished from ProfileManagementController" );
        return JspResolver.PROFILE_HIERARCHY_CLICK_BRANCH;
    }


    private Model getBranchHierarchy( Model model, HttpServletRequest request ) throws InvalidInputException
    {
        LOG.debug( "Method getBranchHierarchy() finished from ProfileManagementController" );
        List<AgentSettings> individuals;
        long branchId = Long.parseLong( request.getParameter( "branchId" ) );
        if ( branchId == 0l ) {
            LOG.error( "Invalid branchId passed in method getBranchHierarchy()." );
            throw new InvalidInputException( "Invalid branchId passed in method getBranchHierarchy()." );
        }

        // Fetching Individuals under Branch
        individuals = profileManagementService.getIndividualsByBranchId( branchId );
        model.addAttribute( "individuals", individuals );

        LOG.debug( "Method getBranchHierarchy() finished from ProfileManagementController" );
        return model;
    }


    @RequestMapping ( value = "/fetchreviews", method = RequestMethod.GET)
    public String fetchReviews( Model model, HttpServletRequest request )
    {
        LOG.info( "Method fetchReviews() called from ProfileManagementController" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();

        boolean fetchAbusive = true;
        List<SurveyDetails> reviewItems = null;
        try {
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }

            double maxScore = CommonConstants.MAX_RATING_SCORE;
            double minScore = Double.parseDouble( request.getParameter( "minScore" ) );
            int startIndex = Integer.parseInt( request.getParameter( "startIndex" ) );
            int numRows = Integer.parseInt( request.getParameter( "numOfRows" ) );

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                reviewItems = profileManagementService.getReviews( user.getCompany().getCompanyId(), minScore, maxScore,
                    startIndex, numRows, CommonConstants.PROFILE_LEVEL_COMPANY, fetchAbusive, null, null, null );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                reviewItems = profileManagementService.getReviews( entityId, minScore, maxScore, startIndex, numRows,
                    CommonConstants.PROFILE_LEVEL_REGION, fetchAbusive, null, null, null );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                reviewItems = profileManagementService.getReviews( entityId, minScore, maxScore, startIndex, numRows,
                    CommonConstants.PROFILE_LEVEL_BRANCH, fetchAbusive, null, null, null );
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                reviewItems = profileManagementService.getReviews( user.getUserId(), minScore, maxScore, startIndex, numRows,
                    CommonConstants.PROFILE_LEVEL_INDIVIDUAL, fetchAbusive, null, null, null );
            }

            // Setting agent's profile URL in each of the review.
            profileManagementService.setAgentProfileUrlForReview( reviewItems );

            model.addAttribute( "reviews", reviewItems );
        } catch ( InvalidInputException e ) {
            throw new InternalServerException( new ProfileServiceErrorCode(
                CommonConstants.ERROR_CODE_COMPANY_REVIEWS_FETCH_FAILURE, CommonConstants.SERVICE_CODE_COMPANY_REVIEWS,
                "Something went wrong while fetching reviews" ), e.getMessage() );
        }

        LOG.info( "Method fetchReviews() finished from ProfileManagementController" );
        return JspResolver.PROFILE_REVIEWS;
    }


    @ResponseBody
    @RequestMapping ( value = "/fetchreviewcount", method = RequestMethod.GET)
    public String fetchReviewCount( Model model, HttpServletRequest request )
    {
        LOG.info( "Method fetchReviewCount() called from ProfileManagementController" );

        boolean fetchAbusive = true;
        long reviewCount = 0l;
        try {
            String entityType = (String) request.getSession( false ).getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }

            double maxScore = CommonConstants.MAX_RATING_SCORE;
            double minScore = Double.parseDouble( request.getParameter( "minScore" ) );

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                long companyId = Long.parseLong( request.getParameter( "companyId" ) );
                if ( companyId == 0l ) {
                    LOG.error( "Invalid companyId passed in method fetchReviews()." );
                    throw new InvalidInputException( "Invalid companyId passed in method fetchReviews()." );
                }

                reviewCount = profileManagementService.getReviewsCount( companyId, minScore, maxScore,
                    CommonConstants.PROFILE_LEVEL_COMPANY, fetchAbusive );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                long regionId = Long.parseLong( request.getParameter( "regionId" ) );
                if ( regionId == 0l ) {
                    LOG.error( "Invalid regionId passed in method fetchReviews()." );
                    throw new InvalidInputException( "Invalid regionId passed in method fetchReviews()." );
                }

                reviewCount = profileManagementService.getReviewsCount( regionId, minScore, maxScore,
                    CommonConstants.PROFILE_LEVEL_REGION, fetchAbusive );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                long branchId = Long.parseLong( request.getParameter( "branchId" ) );
                if ( branchId == 0l ) {
                    LOG.error( "Invalid branchId passed in method fetchReviews()." );
                    throw new InvalidInputException( "Invalid branchId passed in method fetchReviews()." );
                }

                reviewCount = profileManagementService.getReviewsCount( branchId, minScore, maxScore,
                    CommonConstants.PROFILE_LEVEL_BRANCH, fetchAbusive );
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                long agentId = Long.parseLong( request.getParameter( "agentId" ) );
                if ( agentId == 0l ) {
                    LOG.error( "Invalid agentId passed in method fetchReviews()." );
                    throw new InvalidInputException( "Invalid agentId passed in method fetchReviews()." );
                }

                reviewCount = profileManagementService.getReviewsCount( agentId, minScore, maxScore,
                    CommonConstants.PROFILE_LEVEL_INDIVIDUAL, fetchAbusive );
            }
        } catch ( InvalidInputException e ) {
            throw new InternalServerException( new ProfileServiceErrorCode(
                CommonConstants.ERROR_CODE_REVIEWS_COUNT_FETCH_FAILURE, CommonConstants.SERVICE_CODE_COMPANY_REVIEWS,
                "Something went wrong while fetching reviews" ), e.getMessage() );
        }

        LOG.info( "Method fetchReviewCount() finished from ProfileManagementController" );
        return reviewCount + "";
    }


    @ResponseBody
    @RequestMapping ( value = "/fetchaveragerating", method = RequestMethod.GET)
    public String fetchAverageRating( Model model, HttpServletRequest request )
    {
        LOG.info( "Method fetchAverageRating() called from ProfileManagementController" );

        boolean aggregateAbusive = true;
        double averageRating = 0l;
        try {
            String entityType = (String) request.getSession( false ).getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                long companyId = Long.parseLong( request.getParameter( "companyId" ) );
                if ( companyId == 0l ) {
                    LOG.error( "Invalid companyId passed in method fetchReviews()." );
                    throw new InvalidInputException( "Invalid companyId passed in method fetchReviews()." );
                }

                averageRating = profileManagementService.getAverageRatings( companyId, CommonConstants.PROFILE_LEVEL_COMPANY,
                    aggregateAbusive );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                long regionId = Long.parseLong( request.getParameter( "regionId" ) );
                if ( regionId == 0l ) {
                    LOG.error( "Invalid regionId passed in method fetchReviews()." );
                    throw new InvalidInputException( "Invalid regionId passed in method fetchReviews()." );
                }

                averageRating = profileManagementService.getAverageRatings( regionId, CommonConstants.PROFILE_LEVEL_REGION,
                    aggregateAbusive );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                long branchId = Long.parseLong( request.getParameter( "branchId" ) );
                if ( branchId == 0l ) {
                    LOG.error( "Invalid branchId passed in method fetchReviews()." );
                    throw new InvalidInputException( "Invalid branchId passed in method fetchReviews()." );
                }

                averageRating = profileManagementService.getAverageRatings( branchId, CommonConstants.PROFILE_LEVEL_BRANCH,
                    aggregateAbusive );
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                long agentId = Long.parseLong( request.getParameter( "agentId" ) );
                if ( agentId == 0l ) {
                    LOG.error( "Invalid agentId passed in method fetchReviews()." );
                    throw new InvalidInputException( "Invalid agentId passed in method fetchReviews()." );
                }

                averageRating = profileManagementService.getAverageRatings( agentId, CommonConstants.PROFILE_LEVEL_INDIVIDUAL,
                    aggregateAbusive );
            }
        } catch ( InvalidInputException e ) {
            throw new InternalServerException( new ProfileServiceErrorCode(
                CommonConstants.ERROR_CODE_AVERAGE_RATING_FETCH_FAILURE, CommonConstants.SERVICE_CODE_COMPANY_REVIEWS,
                "Something went wrong while fetching Average rating" ), e.getMessage() );
        }

        LOG.info( "Method fetchAverageRating() finished from ProfileManagementController" );
        return averageRating + "";
    }


    /**
     * Method to verify a new emailId
     * 
     * @param encryptedUrlParams
     * @param request
     * @param model
     * @return
     */
    @RequestMapping ( value = "/emailverification")
    public String verifyAccount( @RequestParam ( "q") String encryptedUrlParams, HttpServletRequest request, Model model )
    {
        LOG.info( "Method to verify email called" );

        // Check for existing session
        if ( sessionHelper.isUserActiveSessionExists() ) {
            LOG.info( "Existing Active Session detected" );

            // Invalidate session in browser
            request.getSession( false ).invalidate();
            SecurityContextHolder.clearContext();
        }

        try {
            profileManagementService.updateEmailVerificationStatus( encryptedUrlParams );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.EMAIL_VERIFICATION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException while verifying email. Reason : " + e.getMessage(), e );
            model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.INVALID_VERIFICATION_URL,
                DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.info( "Method to verify email finished" );
        return JspResolver.LOGIN;
    }


    /*
     * Method to store status of the user.
     */
    @ResponseBody
    @RequestMapping ( value = "/savestatus")
    public String saveStatus( HttpServletRequest request, Model model )
    {
        LOG.info( "Method to store status of the user started" );
        HttpSession session = request.getSession( false );

        try {
            User user = sessionHelper.getCurrentUser();
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) request.getSession( false ).getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }

            String text = request.getParameter( "text" );
            profileManagementService.addSocialPosts( user, entityId, entityType, text );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while saving status of user. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.SAVE_STATUS_UNSUCCESSFUL,
                DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.info( "Method to store status of the user finished" );
        return "Added the status successfully";
    }


    /*
     * Method to delete status of the user.
     */
    @ResponseBody
    @RequestMapping ( value = "/deletestatus")
    public String deleteStatus( HttpServletRequest request, Model model )
    {
        LOG.info( "Method to delete status of the user started" );

        try {
            String statusMongoId = request.getParameter( "statusmongoid" );
            profileManagementService.deleteSocialPost( statusMongoId );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while deleting status of user. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            return messageUtils.getDisplayMessage( DisplayMessageConstants.STATUS_CAN_NOT_BE_DELETED,
                DisplayMessageType.ERROR_MESSAGE ).getMessage();
        }
        LOG.info( "Method to delete status of the user finished" );
        return messageUtils.getDisplayMessage( DisplayMessageConstants.STATUS_DELETED_SUCCESSFULLY,
            DisplayMessageType.SUCCESS_MESSAGE ).getMessage();
    }


    /*
     * Method to fetch posts for the logged in user.
     */
    @ResponseBody
    @RequestMapping ( value = "/postsforuser")
    public String getSocialPosts( HttpServletRequest request, Model model )
    {
        LOG.info( "Method to get posts for the user, getSocialPosts() started" );
        String startIndexStr = request.getParameter( "startIndex" );
        String batchSizeStr = request.getParameter( "batchSize" );
        HttpSession session = request.getSession( false );

        if ( startIndexStr == null || batchSizeStr == null ) {
            LOG.error( "Null value found for startIndex or batch size." );
            return "Null value found for startIndex or batch size.";
        }

        int startIndex = Integer.parseInt( startIndexStr );
        int batchSize = Integer.parseInt( batchSizeStr );
        List<SocialPost> posts = null;
        try {
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );

            posts = profileManagementService.getSocialPosts( entityId, entityType, startIndex, batchSize );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while fetching posts. Reason :" + nonFatalException.getMessage(), nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.FETCH_SOCIAL_POSTS_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.info( "Method to get posts for the user, getSocialPosts() finished" );
        return new Gson().toJson( posts );
    }


    /*
     * Method to fetch count of posts for the logged in user.
     */
    @ResponseBody
    @RequestMapping ( value = "/postscountforuser")
    public String getPostsCountForUser( HttpServletRequest request )
    {
        LOG.info( "Method to get posts for the user, getPostsCountForUser() started" );
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        long count = profileManagementService.getPostsCountForUser( entityType, entityId );
        LOG.info( "Method to get posts for the user, getPostsCountForUser() finished" );
        return count + "";
    }


    /**
     * Method to update about profile details
     * 
     * @param model
     * @param request
     */
    @RequestMapping ( value = "/updatedisclaimer", method = RequestMethod.POST)
    public String updateDisclaimer( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateDisclaimer() called from ProfileManagementController" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );

        try {
            UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
            OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
                .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
            long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
            String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
            if ( userSettings == null || profileSettings == null || entityType == null ) {
                throw new InvalidInputException( "No user settings found in session", DisplayMessageConstants.GENERAL_ERROR );
            }

            String disclaimer = request.getParameter( "disclaimer" );
            if ( disclaimer == null || disclaimer.isEmpty() ) {
                throw new InvalidInputException( "Disclaimer can not be null or empty", DisplayMessageConstants.GENERAL_ERROR );
            }

            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings == null ) {
                    throw new InvalidInputException( "No company settings found in current session" );
                }
                companySettings.setDisclaimer( disclaimer );
                profileManagementService.updateDisclaimer( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
                    companySettings, disclaimer );

                userSettings.setCompanySettings( companySettings );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                if ( regionSettings == null ) {
                    throw new InvalidInputException( "No Region settings found in current session" );
                }
                regionSettings.setDisclaimer( disclaimer );
                profileManagementService.updateDisclaimer( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION,
                    regionSettings, disclaimer );

                userSettings.getRegionSettings().put( entityId, regionSettings );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( entityId );
                if ( branchSettings == null ) {
                    throw new InvalidInputException( "No Branch settings found in current session" );
                }
                branchSettings.setDisclaimer( disclaimer );
                profileManagementService.updateDisclaimer( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION,
                    branchSettings, disclaimer );

                userSettings.getRegionSettings().put( entityId, branchSettings );
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                AgentSettings agentSettings = userManagementService.getUserSettings( entityId );
                if ( agentSettings == null ) {
                    throw new InvalidInputException( "No Agent settings found in current session" );
                }
                agentSettings.setDisclaimer( disclaimer );
                profileManagementService.updateDisclaimer( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
                    agentSettings, disclaimer );

                userSettings.setAgentSettings( agentSettings );
            } else {
                throw new InvalidInputException( "Error occurred while updating Disclaimer",
                    DisplayMessageConstants.GENERAL_ERROR );
            }

            profileSettings.setDisclaimer( disclaimer );

            LOG.info( "Disclaimer details updated successfully" );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.DISCLAIMER_UPDATE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "NonFatalException while updating Disclaimer details. Reason :" + nonFatalException.getMessage(),
                nonFatalException );
            model.addAttribute( "message", messageUtils.getDisplayMessage(
                DisplayMessageConstants.DISCLAIMER_UPDATE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method updateDisclaimer() finished from ProfileManagementController" );
        return JspResolver.MESSAGE_HEADER;
    }


    @RequestMapping ( value = "/geteditpositions")
    public String getEditPositions( HttpServletRequest request, Model model )
    {

        LOG.info( "Method called to get the edit positions container" );

        return JspResolver.PROFILE_POSITIONS_EDIT;
    }


    @RequestMapping ( value = "/showurleditwarning")
    public String showUrlEditWarning( HttpServletRequest request, Model model )
    {

        LOG.info( "Method called to display profile url change warning" );
        return JspResolver.PROFILE_URL_WARNING;
    }


    @RequestMapping ( value = "/editprofileurl")
    public String getProfileUrlEditPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Method called to display profile url change page" );
        HttpSession session = request.getSession( false );
        String entityType = (String) session.getAttribute( "entityType" );
        String profileBaseUrl = applicationBaseUrl + "pages/";
        UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
        String companyProfileUrl = userSettings.getCompanySettings().getProfileUrl();
        switch ( entityType ) {
            case CommonConstants.AGENT_ID_COLUMN:
                break;

            case CommonConstants.BRANCH_ID_COLUMN:
                profileBaseUrl += "office" + companyProfileUrl;
                break;

            case CommonConstants.REGION_ID_COLUMN:
                profileBaseUrl += "region" + companyProfileUrl;
                break;

            case CommonConstants.COMPANY_ID_COLUMN:
                profileBaseUrl += "company";
                break;

        }
        model.addAttribute( "profileBaseUrl", profileBaseUrl );
        return JspResolver.PROFILE_URL_CHANGE;
    }


    @ResponseBody
    @RequestMapping ( value = "/updatepositions")
    public String updatePositions( HttpServletRequest request, Model model )
    {

        LOG.info( "Method called to get the update positions" );

        HttpSession session = request.getSession();

        AgentSettings agentSettings = (AgentSettings) session.getAttribute( CommonConstants.USER_PROFILE_SETTINGS );

        String postionsJson = request.getParameter( "positions" );
        List<CompanyPositions> companyPositions = new Gson().fromJson( postionsJson,
            new TypeToken<List<CompanyPositions>>() {}.getType() );

        profileManagementService.addOrUpdateAgentPositions( companyPositions, agentSettings );
        agentSettings.setPositions( companyPositions );
        return CommonConstants.SUCCESS_ATTRIBUTE;
    }


    @ResponseBody
    @RequestMapping ( value = "/updateprofileurl", method = RequestMethod.GET)
    public String validateAndUpdateProfileUrl( Model model, HttpServletRequest request )
    {
        LOG.info( "Method called to validate and update profile urls" );
        String profileName = request.getParameter( "searchKey" );
        String profileBaseUrl = applicationBaseUrl + "pages";
        profileName = utils.prepareProfileName( profileName );
        String profileUrl = "";
        HttpSession session = request.getSession();
        OrganizationUnitSettings profileSettings = (OrganizationUnitSettings) session
            .getAttribute( CommonConstants.USER_PROFILE_SETTINGS );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
        LOG.debug( "Checking uniqueness of profileName:" + profileName );
        String companyProfileName = userSettings.getCompanySettings().getProfileName();
        LOG.info( "COMPANY PROFILE NAME : " + companyProfileName );
        boolean profileExists = false;

        switch ( entityType ) {
            case CommonConstants.AGENT_ID_COLUMN:
                try {
                    profileManagementService.getUserByProfileName( profileName, false );
                    profileExists = true;
                } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                    LOG.error( "Error occured. Reason : " + e );
                } catch ( ProfileNotFoundException e ) {
                    LOG.info( "no such profile name exists." );
                    AgentSettings agentSettings = userSettings.getAgentSettings();
                    profileUrl = utils.generateAgentProfileUrl( profileName );
                    agentSettings.setProfileName( profileName );
                    agentSettings.setProfileUrl( profileUrl );
                    userSettings.setAgentSettings( agentSettings );
                    profileSettings.setProfileName( profileName );
                    profileSettings.setProfileUrl( profileUrl );
                    userManagementService.updateProfileUrlInAgentSettings( profileName, profileUrl, agentSettings );
                    profileSettings.setCompleteProfileUrl( profileBaseUrl + profileUrl );
                }
                break;

            case CommonConstants.BRANCH_ID_COLUMN:

                try {
                    profileManagementService.getBranchByProfileName( companyProfileName, profileName );
                    profileExists = true;
                } catch ( InvalidInputException e ) {
                    LOG.error( "Invalid input entered" );
                } catch ( ProfileNotFoundException e ) {
                    LOG.info( "no such profile name exists." );
                    profileUrl = utils.generateBranchProfileUrl( companyProfileName, profileName );
                    try {
                        OrganizationUnitSettings branchSettings = organizationManagementService
                            .getBranchSettingsDefault( entityId );
                        branchSettings.setProfileName( profileName );
                        branchSettings.setProfileUrl( profileUrl );
                        userSettings.getBranchSettings().put( entityId, branchSettings );
                        profileSettings.setProfileName( profileName );
                        profileSettings.setProfileUrl( profileUrl );
                        userManagementService.updateProfileUrlInBranchSettings( profileName, profileUrl, branchSettings );
                        profileSettings.setCompleteProfileUrl( profileBaseUrl + profileUrl );
                        organizationManagementService.updateBranchProfileName( entityId, profileName );
                    } catch ( InvalidInputException | NoRecordsFetchedException e1 ) {
                        LOG.error( "Error occured. Reason: " + e1 );
                    }
                }
                break;

            case CommonConstants.REGION_ID_COLUMN:
                try {
                    profileManagementService.getRegionByProfileName( companyProfileName, profileName );
                    profileExists = true;
                } catch ( InvalidInputException e ) {
                    LOG.error( "Error occured. Reason : " + e );
                } catch ( ProfileNotFoundException e ) {
                    LOG.info( "no such profile name exists." );
                    profileUrl = utils.generateRegionProfileUrl( companyProfileName, profileName );
                    try {
                        OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( entityId );
                        regionSettings.setProfileName( profileName );
                        regionSettings.setProfileUrl( profileUrl );
                        userSettings.getRegionSettings().put( entityId, regionSettings );
                        userManagementService.updateProfileUrlInRegionSettings( profileName, profileUrl, regionSettings );
                        profileSettings.setProfileName( profileName );
                        profileSettings.setProfileUrl( profileUrl );
                        profileSettings.setCompleteProfileUrl( profileBaseUrl + profileUrl );
                        organizationManagementService.updateRegionProfileName( entityId, profileName );
                    } catch ( InvalidInputException e1 ) {
                        LOG.error( "Error occured. Reason : " + e1 );
                    }
                }
                break;

            case CommonConstants.COMPANY_ID_COLUMN:
                try {
                    profileManagementService.getCompanyProfileByProfileName( profileName );
                    profileExists = true;
                } catch ( ProfileNotFoundException e ) {
                    LOG.info( "no such profile name exists." );
                    profileUrl = utils.generateCompanyProfileUrl( profileName );
                    LOG.info( "PROFILE URL : " + profileUrl + "\n PROFILE NAME : " + profileName );
                    OrganizationUnitSettings companySettings = userSettings.getCompanySettings();
                    companySettings.setProfileName( profileName );
                    companySettings.setProfileUrl( profileUrl );
                    userSettings.setCompanySettings( companySettings );
                    userManagementService.updateProfileUrlInCompanySettings( profileName, profileUrl, companySettings );
                    profileSettings.setProfileName( profileName );
                    profileSettings.setProfileUrl( profileUrl );
                    profileSettings.setCompleteProfileUrl( profileBaseUrl + profileUrl );
                }
                break;
        }
        String response = "";
        if ( profileExists ) {
            LOG.info( "Profile already exists" );
            response = "true";
        } else {
            LOG.info( "Profile didn't exist" );
            String completeProfileUrl = profileSettings.getCompleteProfileUrl();
            response = "<a href=\"" + completeProfileUrl + "\" target=\"_blank\">" + completeProfileUrl + "</a>";
            session.setAttribute( CommonConstants.USER_PROFILE_SETTINGS, profileSettings );
        }
        return response;

    }

}
