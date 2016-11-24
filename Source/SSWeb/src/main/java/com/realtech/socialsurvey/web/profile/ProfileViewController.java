/**
 * Entry point for profile view pages.
 */
package com.realtech.socialsurvey.web.profile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.LockSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserCompositeEntity;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.ProfileServiceErrorCode;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;
import com.realtech.socialsurvey.web.util.BotRequestUtils;


@Controller
public class ProfileViewController
{

    private static final Logger LOG = LoggerFactory.getLogger( ProfileViewController.class );

    @Resource
    @Qualifier ( "nocaptcha")
    private CaptchaValidation captchaValidation;

    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private BotRequestUtils botRequestUtils;

    @Value ( "${VALIDATE_CAPTCHA}")
    private String validateCaptcha;

    @Value ( "${CAPTCHA_SECRET}")
    private String captchaSecretKey;


    /**
     * Method to return company profile page
     * 
     * @param profileName
     * @param model
     * @return
     * @throws NoRecordsFetchedException 
     */
    @RequestMapping ( value = { "/company/{profileName}", "/company/{profileName}/*" }, method = RequestMethod.GET)
    public String initCompanyProfilePage( @PathVariable String profileName, Model model, HttpServletRequest request,
        RedirectAttributes redirectAttributes ) throws NoRecordsFetchedException
    {
        LOG.info( "Service to initiate company profile page called" );
        String message = null;
        // check if the request is from bot
        boolean isBotRequest = botRequestUtils.checkBotRequest( request );

        if ( profileName == null || profileName.isEmpty() ) {
            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.INVALID_COMPANY_PROFILENAME, DisplayMessageType.ERROR_MESSAGE )
                .getMessage();
            model.addAttribute( "message", message );
            return JspResolver.MESSAGE_HEADER;
        }

        // making case insensitive
        profileName = profileName.toLowerCase();

        OrganizationUnitSettings companyProfile = null;
        try {
            companyProfile = profileManagementService.getCompanyProfileByProfileName( profileName );
            if ( companyProfile == null || ( companyProfile.getStatus() != null
                && ( companyProfile.getStatus().equalsIgnoreCase( CommonConstants.STATUS_DELETED_MONGO )
                    || companyProfile.getStatus().equalsIgnoreCase( CommonConstants.STATUS_INCOMPLETE_MONGO ) ) ) ) {
                throw new ProfileNotFoundException( "No settings found for company while fetching company profile" );
            }

            //remove sensitive info frm profile json
            profileManagementService.removeTokensFromProfile( companyProfile );

            String json = new Gson().toJson( companyProfile );
            model.addAttribute( "profileJson", json );

            Long companyId = companyProfile.getIden();
            double averageRating = profileManagementService.getAverageRatings( companyId, CommonConstants.PROFILE_LEVEL_COMPANY,
                false, false, 0, 0 );
            model.addAttribute( "averageRating", averageRating );

            // should show all review count
            long reviewsCount = profileManagementService.getReviewsCount( companyId, -1, -1,
                CommonConstants.PROFILE_LEVEL_COMPANY, false, false, false, 0 );

            model.addAttribute( "reviewsCount", reviewsCount );

            if ( isBotRequest ) {
                List<SurveyDetails> reviews = profileManagementService.getReviews( companyId, -1, -1, -1,
                    CommonConstants.USER_AGENT_NUMBER_REVIEWS, CommonConstants.PROFILE_LEVEL_COMPANY, false, null, null,
                    CommonConstants.REVIEWS_SORT_CRITERIA_FEATURE );
                model.addAttribute( "reviews", reviews );

                List<SocialPost> posts = profileManagementService.getSocialPosts( companyProfile.getIden(),
                    CommonConstants.COMPANY_ID_COLUMN, -1, CommonConstants.USER_AGENT_NUMBER_POST );
                model.addAttribute( "posts", posts );
            }
        } catch ( InvalidInputException e ) {
            throw new InternalServerException(
                new ProfileServiceErrorCode( CommonConstants.ERROR_CODE_COMPANY_PROFILE_SERVICE_FAILURE,
                    CommonConstants.SERVICE_CODE_COMPANY_PROFILE, "Error occured while fetching company profile" ),
                e.getMessage() );
        } catch ( ProfileNotFoundException e ) {
            LOG.error( "Excpetion caught " + e.getMessage() );
            Map<String, String> nameMap = profileManagementService.findNamesfromProfileName( profileName );
            redirectAttributes.addFlashAttribute( "patternFirst", nameMap.get( CommonConstants.PATTERN_FIRST ) );
            redirectAttributes.addFlashAttribute( "patternLast", nameMap.get( CommonConstants.PATTERN_LAST ) );
            return "redirect:/" + JspResolver.FINDAPRO + ".do";
        }

        model.addAttribute( "profile", companyProfile );
        model.addAttribute( "companyProfileName", profileName );
        model.addAttribute( "profileLevel", CommonConstants.PROFILE_LEVEL_COMPANY );
        model.addAttribute( "findProCompanyProfileName", companyProfile.getProfileName() );

        LOG.info( "Service to initiate company profile page executed successfully" );
        if ( isBotRequest ) {
            return JspResolver.PROFILE_PAGE_NOSCRIPT;
        } else {
            return JspResolver.PROFILE_PAGE;
        }
    }


    /**
     * Method to return region profile page
     * 
     * @param companyProfileName
     * @param regionProfileName
     * @param model
     * @return
     * @throws NoRecordsFetchedException 
     */
    @RequestMapping ( value = { "/region/{companyProfileName}/{regionProfileName}", "/region/{companyProfileName}/{regionProfileName}/*" } )
    public String initRegionProfilePage( @PathVariable String companyProfileName, @PathVariable String regionProfileName,
        Model model, HttpServletRequest request, RedirectAttributes redirectAttributes ) throws NoRecordsFetchedException
    {
        LOG.info( "Service to initiate region profile page called" );
        String message = null;
        Map<SettingsForApplication, OrganizationUnit> map = null;
        boolean isBotRequest = botRequestUtils.checkBotRequest( request );
        if ( companyProfileName == null || companyProfileName.isEmpty() ) {
            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.INVALID_COMPANY_PROFILENAME, DisplayMessageType.ERROR_MESSAGE )
                .getMessage();
            model.addAttribute( "message", message );
            return JspResolver.MESSAGE_HEADER;
        }
        if ( regionProfileName == null || regionProfileName.isEmpty() ) {
            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.INVALID_REGION_PROFILENAME, DisplayMessageType.ERROR_MESSAGE )
                .getMessage();
            model.addAttribute( "message", message );
            return JspResolver.MESSAGE_HEADER;
        }

        // making case insensitive
        companyProfileName = companyProfileName.toLowerCase();
        regionProfileName = regionProfileName.toLowerCase();

        OrganizationUnitSettings regionProfile = null;
        OrganizationUnitSettings companyProfile = null;
        try {
            regionProfile = profileManagementService.getRegionSettingsByProfileName( companyProfileName, regionProfileName );
            if ( regionProfile == null || ( regionProfile.getStatus() != null
                && ( regionProfile.getStatus().equalsIgnoreCase( CommonConstants.STATUS_DELETED_MONGO )
                    || regionProfile.getStatus().equalsIgnoreCase( CommonConstants.STATUS_INCOMPLETE_MONGO ) ) ) ) {
                throw new ProfileNotFoundException( "No settings found for region while fetching region profile" );
            }

            companyProfile = profileManagementService.getCompanyProfileByProfileName( companyProfileName );
            if ( companyProfile == null || ( companyProfile.getStatus() != null
                && companyProfile.getStatus().equalsIgnoreCase( CommonConstants.STATUS_DELETED_MONGO ) ) ) {
                throw new ProfileNotFoundException( "No settings found for company while fetching region profile" );
            }
            // 	migrating the hideSectionsFromProfilePage value from company to region
            regionProfile.setHideSectionsFromProfilePage( companyProfile.getHideSectionsFromProfilePage() );
            regionProfile.setHiddenSection( companyProfile.isHiddenSection() );

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
            }

            regionProfile = profileManagementService.fillUnitSettings( regionProfile,
                MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, companyProfile, regionProfile, null, null, map,
                true );

            //remove sensitive info frm profile json
            profileManagementService.removeTokensFromProfile( regionProfile );

            // aggregated disclaimer
            String disclaimer = profileManagementService.aggregateDisclaimer( regionProfile, CommonConstants.REGION_ID );
            regionProfile.setDisclaimer( disclaimer );

            String json = new Gson().toJson( regionProfile );
            model.addAttribute( "profileJson", json );

            Long regionId = regionProfile.getIden();
            double averageRating = profileManagementService.getAverageRatings( regionId, CommonConstants.PROFILE_LEVEL_REGION,
                false, false, 0, 0 );
            model.addAttribute( "averageRating", averageRating );

            long reviewsCount = profileManagementService.getReviewsCount( regionId, -1, -1,
                CommonConstants.PROFILE_LEVEL_REGION, false, false, false, 0 );
            model.addAttribute( "reviewsCount", reviewsCount );

            if ( isBotRequest ) {
                List<SurveyDetails> reviews = profileManagementService.getReviews( regionId, -1, -1, -1,
                    CommonConstants.USER_AGENT_NUMBER_REVIEWS, CommonConstants.PROFILE_LEVEL_REGION, false, null, null,
                    CommonConstants.REVIEWS_SORT_CRITERIA_FEATURE );
                model.addAttribute( "reviews", reviews );

                List<SocialPost> posts = profileManagementService.getSocialPosts( regionProfile.getIden(),
                    CommonConstants.REGION_ID_COLUMN, -1, CommonConstants.USER_AGENT_NUMBER_POST );
                model.addAttribute( "posts", posts );
            }
        } catch ( ProfileNotFoundException e ) {
            LOG.error( "Excpetion caught " + e.getMessage() );
            Map<String, String> nameMap = profileManagementService.findNamesfromProfileName( regionProfileName );
            redirectAttributes.addFlashAttribute( "patternFirst", nameMap.get( CommonConstants.PATTERN_FIRST ) );
            redirectAttributes.addFlashAttribute( "patternLast", nameMap.get( CommonConstants.PATTERN_LAST ) );
            return "redirect:/" + JspResolver.FINDAPRO + ".do";
        } catch ( InvalidInputException e ) {
            throw new InternalServerException(
                new ProfileServiceErrorCode( CommonConstants.ERROR_CODE_REGION_PROFILE_SERVICE_FAILURE,
                    CommonConstants.SERVICE_CODE_REGION_PROFILE, "Error occured while fetching region profile" ),
                e.getMessage() );
        }

        model.addAttribute( "profile", regionProfile );
        model.addAttribute( "companyProfileName", companyProfileName );
        model.addAttribute( "regionProfileName", regionProfileName );
        model.addAttribute( "profileLevel", CommonConstants.PROFILE_LEVEL_REGION );
        model.addAttribute( "findProCompanyProfileName", companyProfile.getProfileName() );

        LOG.info( "Service to initiate region profile page executed successfully" );
        if ( isBotRequest ) {
            return JspResolver.PROFILE_PAGE_NOSCRIPT;
        } else {
            return JspResolver.PROFILE_PAGE;
        }
    }


    /**
     * Method to return branch profile page
     * 
     * @param companyProfileName
     * @param branchProfileName
     * @param model
     * @return
     * @throws NoRecordsFetchedException 
     */
    @RequestMapping ( value = { "/office/{companyProfileName}/{branchProfileName}", "/office/{companyProfileName}/{branchProfileName}/*" } )
    public String initBranchProfilePage( @PathVariable String companyProfileName, @PathVariable String branchProfileName,
        Model model, HttpServletRequest request, RedirectAttributes redirectAttributes ) throws NoRecordsFetchedException
    {
        LOG.info( "Service to initiate branch profile page called" );
        String message = null;
        Map<SettingsForApplication, OrganizationUnit> map = null;
        boolean isBotRequest = botRequestUtils.checkBotRequest( request );
        if ( companyProfileName == null || companyProfileName.isEmpty() ) {
            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.INVALID_COMPANY_PROFILENAME, DisplayMessageType.ERROR_MESSAGE )
                .getMessage();
            model.addAttribute( "message", message );
            return JspResolver.MESSAGE_HEADER;
        }
        if ( branchProfileName == null || branchProfileName.isEmpty() ) {
            message = messageUtils
                .getDisplayMessage( DisplayMessageConstants.INVALID_BRANCH_PROFILENAME, DisplayMessageType.ERROR_MESSAGE )
                .getMessage();
            model.addAttribute( "message", message );
            return JspResolver.MESSAGE_HEADER;
        }

        // making case insensitive
        companyProfileName = companyProfileName.toLowerCase();
        branchProfileName = branchProfileName.toLowerCase();

        OrganizationUnitSettings companyProfile = null;
        OrganizationUnitSettings branchProfile = null;
        OrganizationUnitSettings regionProfile = null;
        try {
            branchProfile = profileManagementService.getBranchSettingsByProfileName( companyProfileName, branchProfileName );
            if ( branchProfile == null || ( branchProfile.getStatus() != null
                && ( branchProfile.getStatus().equalsIgnoreCase( CommonConstants.STATUS_DELETED_MONGO )
                    || branchProfile.getStatus().equalsIgnoreCase( CommonConstants.STATUS_INCOMPLETE_MONGO ) ) ) ) {
                throw new ProfileNotFoundException( "No settings found for branch while fetching branch profile" );
            }

            companyProfile = profileManagementService.getCompanyProfileByProfileName( companyProfileName );
            if ( companyProfile == null || ( companyProfile.getStatus() != null
                && companyProfile.getStatus().equalsIgnoreCase( CommonConstants.STATUS_DELETED_MONGO ) ) ) {
                throw new ProfileNotFoundException( "No settings found for company while fetching office profile" );
            }
            // migrating the hideSectionsFromProfilePage value from company to branch
            branchProfile.setHideSectionsFromProfilePage( companyProfile.getHideSectionsFromProfilePage() );
            branchProfile.setHiddenSection( companyProfile.isHiddenSection() );

            regionProfile = profileManagementService.getRegionProfileByBranch( branchProfile );
            if ( regionProfile == null || ( regionProfile.getStatus() != null
                && regionProfile.getStatus().equalsIgnoreCase( CommonConstants.STATUS_DELETED_MONGO ) ) ) {
                throw new ProfileNotFoundException( "No settings found for region while fetching office profile" );
            }
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
            }
            branchProfile = profileManagementService.fillUnitSettings( branchProfile,
                MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, companyProfile, regionProfile, branchProfile,
                null, map, true );

            //remove sensitive info frm profile json
            profileManagementService.removeTokensFromProfile( branchProfile );

            // aggregated disclaimer
            String disclaimer = profileManagementService.aggregateDisclaimer( branchProfile, CommonConstants.BRANCH_ID );
            branchProfile.setDisclaimer( disclaimer );

            String json = new Gson().toJson( branchProfile );
            model.addAttribute( "profileJson", json );

            Long branchId = branchProfile.getIden();
            double averageRating = profileManagementService.getAverageRatings( branchId, CommonConstants.PROFILE_LEVEL_BRANCH,
                false, false, 0, 0 );
            model.addAttribute( "averageRating", averageRating );

            long reviewsCount = profileManagementService.getReviewsCount( branchId, -1, -1,
                CommonConstants.PROFILE_LEVEL_BRANCH, false, false, false, 0 );
            model.addAttribute( "reviewsCount", reviewsCount );

            if ( isBotRequest ) {
                List<SurveyDetails> reviews = profileManagementService.getReviews( branchId, -1, -1, -1,
                    CommonConstants.USER_AGENT_NUMBER_REVIEWS, CommonConstants.PROFILE_LEVEL_BRANCH, false, null, null,
                    CommonConstants.REVIEWS_SORT_CRITERIA_FEATURE );
                model.addAttribute( "reviews", reviews );

                List<SocialPost> posts = profileManagementService.getSocialPosts( branchProfile.getIden(),
                    CommonConstants.BRANCH_ID_COLUMN, -1, CommonConstants.USER_AGENT_NUMBER_POST );
                model.addAttribute( "posts", posts );
            }
        } catch ( ProfileNotFoundException e ) {
            LOG.error( "Excpetion caught " + e.getMessage() );
            Map<String, String> nameMap = profileManagementService.findNamesfromProfileName( branchProfileName );
            redirectAttributes.addFlashAttribute( "patternFirst", nameMap.get( CommonConstants.PATTERN_FIRST ) );
            redirectAttributes.addFlashAttribute( "patternLast", nameMap.get( CommonConstants.PATTERN_LAST ) );
            return "redirect:/" + JspResolver.FINDAPRO + ".do";
        } catch ( InvalidInputException e ) {
            throw new InternalServerException(
                new ProfileServiceErrorCode( CommonConstants.ERROR_CODE_BRANCH_PROFILE_SERVICE_FAILURE,
                    CommonConstants.SERVICE_CODE_BRANCH_PROFILE, "Error occured while fetching branch profile" ),
                e.getMessage() );
        }

        model.addAttribute( "profile", branchProfile );
        model.addAttribute( "companyProfileName", companyProfileName );
        model.addAttribute( "branchProfileName", branchProfileName );
        model.addAttribute( "profileLevel", CommonConstants.PROFILE_LEVEL_BRANCH );
        model.addAttribute( "findProCompanyProfileName", companyProfile.getProfileName() );

        LOG.info( "Service to initiate branch profile page executed successfully" );
        if ( isBotRequest ) {
            return JspResolver.PROFILE_PAGE_NOSCRIPT;
        } else {
            return JspResolver.PROFILE_PAGE;
        }
    }


    /**
     * Method to return agent profile page
     * 
     * @param agentProfileName
     * @param model
     * @return
     * @throws NoRecordsFetchedException 
     */
    @RequestMapping ( value = { "/{agentProfileName}", "/{agentProfileName}/*" } )
    public String initAgentProfilePage( @PathVariable String agentProfileName, Model model, HttpServletResponse response,
        HttpServletRequest request, RedirectAttributes redirectAttributes ) throws NoRecordsFetchedException
    {
        LOG.info( "Service to initiate agent profile page called" );
        boolean isBotRequest = botRequestUtils.checkBotRequest( request );
        long companyId = 0;
        long branchId = 0;
        long regionId = 0;
        OrganizationUnitSettings companyProfile = null;
        OrganizationUnitSettings regionProfile = null;
        OrganizationUnitSettings branchProfile = null;
        Map<SettingsForApplication, OrganizationUnit> settingsByOrganizationUnitMap = null;
        if ( agentProfileName == null || agentProfileName.isEmpty() ) {
            model.addAttribute( "message", messageUtils
                .getDisplayMessage( DisplayMessageConstants.INVALID_INDIVIDUAL_PROFILENAME, DisplayMessageType.ERROR_MESSAGE )
                .getMessage() );
            return JspResolver.MESSAGE_HEADER;
        }

        // making case insensitive
        agentProfileName = agentProfileName.toLowerCase();
        UserCompositeEntity userCompositeObject = null;

        // check for profiles and redirect to company if admin only
        try {
            // get the user composite object
            userCompositeObject = profileManagementService.getCompositeUserObjectByProfileName( agentProfileName, true );
            User user = userCompositeObject.getUser();
            List<UserProfile> userProfiles = user.getUserProfiles();
            if ( userProfiles == null || userProfiles.size() < 1 ) {
                throw new ProfileNotFoundException( DisplayMessageConstants.INVALID_INDIVIDUAL_PROFILENAME );
            }

            boolean hasAgentProfile = false;
            for ( UserProfile profile : userProfiles ) {
                if ( profile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                    hasAgentProfile = true;
                    break;
                }
            }

            // redirect to company profile page
            if ( !hasAgentProfile ) {
                OrganizationUnitSettings companySettings = organizationManagementService
                    .getCompanySettings( user.getCompany().getCompanyId() );
                String companyProfileUrl = companySettings.getCompleteProfileUrl();

                try {
                    LOG.info( "Service to redirect to company profile page executed successfully" );
                    response.sendRedirect( companyProfileUrl );
                } catch ( IOException e ) {
                    LOG.error( "IOException : message : " + e.getMessage(), e );
                    model.addAttribute( "message",
                        messageUtils.getDisplayMessage( DisplayMessageConstants.INVALID_COMPANY_PROFILENAME,
                            DisplayMessageType.ERROR_MESSAGE ).getMessage() );
                    return JspResolver.MESSAGE_HEADER;
                }
            }

            AgentSettings individualProfile = null;
            try {
                individualProfile = userCompositeObject.getAgentSettings();

                if ( individualProfile == null || ( individualProfile.getStatus() != null
                    && ( individualProfile.getStatus().equalsIgnoreCase( CommonConstants.STATUS_DELETED_MONGO ) ) ) ) {
                    throw new ProfileNotFoundException( "Unable to find agent profile for profile name " + agentProfileName );
                }
                Map<String, Long> hierarchyMap = profileManagementService
                    .getPrimaryHierarchyByAgentProfile( individualProfile );
                LOG.debug( "Got the primary hierarchy." );
                if ( hierarchyMap == null ) {
                    LOG.error( "Unable to fetch primary profile for this user " );
                    throw new FatalException( "Unable to fetch primary profile for this user " + individualProfile.getIden() );
                }
                companyId = hierarchyMap.get( CommonConstants.COMPANY_ID_COLUMN );
                regionId = hierarchyMap.get( CommonConstants.REGION_ID_COLUMN );
                branchId = hierarchyMap.get( CommonConstants.BRANCH_ID_COLUMN );
                LOG.debug( "Company ID : " + companyId + " Region ID : " + regionId + " Branch ID : " + branchId );

                companyProfile = organizationManagementService.getCompanySettings( companyId );
                if ( companyProfile == null || ( companyProfile.getStatus() != null
                    && companyProfile.getStatus().equalsIgnoreCase( CommonConstants.STATUS_DELETED_MONGO ) ) ) {
                    throw new ProfileNotFoundException( "Unable to find company profile for profile name " + agentProfileName );
                }

                model.addAttribute( "findProCompanyProfileName", companyProfile.getProfileName() );

                regionProfile = organizationManagementService.getRegionSettings( regionId );
                if ( regionProfile == null || ( regionProfile.getStatus() != null
                    && regionProfile.getStatus().equalsIgnoreCase( CommonConstants.STATUS_DELETED_MONGO ) ) ) {
                    throw new ProfileNotFoundException( "Unable to find region profile for profile name " + agentProfileName );
                }
                branchProfile = organizationManagementService.getBranchSettingsDefault( branchId );
                if ( branchProfile == null || ( branchProfile.getStatus() != null
                    && branchProfile.getStatus().equalsIgnoreCase( CommonConstants.STATUS_DELETED_MONGO ) ) ) {
                    throw new ProfileNotFoundException( "Unable to find branch profile for profile name " + agentProfileName );
                }

                if ( companyProfile.isHiddenSection() ) {
                    Branch branch = userManagementService.getBranchById( branchId );
                    if ( branch.getIsDefaultBySystem() == 1 ) {
                        Region region = userManagementService.getRegionById( regionId );
                        if ( region.getIsDefaultBySystem() == 1 ) {
                            try {
                                LOG.info( "Service to redirect to company profile page executed successfully" );
                                response.sendRedirect( companyProfile.getCompleteProfileUrl() );
                            } catch ( IOException e ) {
                                LOG.error( "IOException : message : " + e.getMessage(), e );
                                model.addAttribute( "message",
                                    messageUtils.getDisplayMessage( DisplayMessageConstants.INVALID_COMPANY_PROFILENAME,
                                        DisplayMessageType.ERROR_MESSAGE ).getMessage() );
                                return JspResolver.MESSAGE_HEADER;
                            }
                        } else {
                            try {
                                LOG.info( "Service to redirect to region profile page executed successfully" );
                                response.sendRedirect( regionProfile.getCompleteProfileUrl() );
                            } catch ( IOException e ) {
                                LOG.error( "IOException : message : " + e.getMessage(), e );
                                model.addAttribute( "message",
                                    messageUtils.getDisplayMessage( DisplayMessageConstants.INVALID_REGION_PROFILENAME,
                                        DisplayMessageType.ERROR_MESSAGE ).getMessage() );
                                return JspResolver.MESSAGE_HEADER;
                            }
                        }
                    } else {
                        try {
                            LOG.info( "Service to redirect to branch profile page executed successfully" );
                            response.sendRedirect( branchProfile.getCompleteProfileUrl() );
                        } catch ( IOException e ) {
                            LOG.error( "IOException : message : " + e.getMessage(), e );
                            model.addAttribute( "message",
                                messageUtils.getDisplayMessage( DisplayMessageConstants.INVALID_BRANCH_PROFILENAME,
                                    DisplayMessageType.ERROR_MESSAGE ).getMessage() );
                            return JspResolver.MESSAGE_HEADER;
                        }
                    }
                }

                LOG.debug( "Getting settings by organization unit map" );
                settingsByOrganizationUnitMap = profileManagementService
                    .getPrimaryHierarchyByEntity( CommonConstants.AGENT_ID_COLUMN, individualProfile.getIden() );
                LOG.debug( "Extracted settings by organization unit map" );
                if ( settingsByOrganizationUnitMap == null ) {
                    LOG.error( "Unable to fetch primary profile for this user " );
                    throw new FatalException( "Unable to fetch primary profile this user " + individualProfile.getIden() );
                }

                LOG.debug( "Filling the unit settings in the profile" );
                individualProfile = (AgentSettings) profileManagementService.fillUnitSettings( individualProfile,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, companyProfile, regionProfile, branchProfile,
                    individualProfile, settingsByOrganizationUnitMap, true );

                //remove sensitive info frm profile json
                profileManagementService.removeTokensFromProfile( individualProfile );
                LOG.debug( "Finished filling the unit settings in the profile" );
                //set vertical name from the company
                individualProfile.setVertical( user.getCompany().getVerticalsMaster().getVerticalName() );
                // migrating the hideSectionsFromProfilePage value from company to branch
                individualProfile.setHideSectionsFromProfilePage( companyProfile.getHideSectionsFromProfilePage() );
                individualProfile.setHiddenSection( companyProfile.isHiddenSection() );

                //set survey settings in individual profile
                if ( individualProfile.getSurvey_settings() == null )
                    individualProfile.setSurvey_settings( companyProfile.getSurvey_settings() );

                //Aggregate agent details
                LockSettings lockSettings = individualProfile.getLockSettings();
                if ( lockSettings == null )
                    lockSettings = new LockSettings();
                individualProfile = (AgentSettings) profileManagementService.aggregateAgentDetails( user, individualProfile,
                    lockSettings );
                String json = new Gson().toJson( individualProfile );
                model.addAttribute( "profileJson", json );

                long agentId = user.getUserId();
                double averageRating = profileManagementService.getAverageRatings( agentId,
                    CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false, false, 0, 0 );
                model.addAttribute( "averageRating", averageRating );

                long reviewsCount = profileManagementService.getReviewsCount( agentId, -1, -1,
                    CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false, false, false, 0 );
                model.addAttribute( "reviewsCount", reviewsCount );

                if ( isBotRequest ) {
                    List<SurveyDetails> reviews = profileManagementService.getReviews( agentId, -1, -1, -1,
                        CommonConstants.USER_AGENT_NUMBER_REVIEWS, CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false, null, null,
                        CommonConstants.REVIEWS_SORT_CRITERIA_FEATURE );
                    model.addAttribute( "reviews", reviews );

                    List<SocialPost> posts = profileManagementService.getSocialPosts( individualProfile.getIden(),
                        CommonConstants.AGENT_ID_COLUMN, -1, CommonConstants.USER_AGENT_NUMBER_POST );
                    model.addAttribute( "posts", posts );
                }

                model.addAttribute( "agentFirstName", individualProfile.getContact_details().getFirstName() );
                model.addAttribute( "profile", individualProfile );
            } catch ( ProfileNotFoundException e ) {
                LOG.error( "Excpetion caught " + e.getMessage() );
                Map<String, String> nameMap = profileManagementService.findNamesfromProfileName( agentProfileName );
                redirectAttributes.addFlashAttribute( "patternFirst", nameMap.get( CommonConstants.PATTERN_FIRST ) );
                redirectAttributes.addFlashAttribute( "patternLast", nameMap.get( CommonConstants.PATTERN_LAST ) );
                return "redirect:/" + JspResolver.FINDAPRO + ".do";
            } catch ( InvalidInputException e ) {
                throw new InternalServerException(
                    new ProfileServiceErrorCode( CommonConstants.ERROR_CODE_INDIVIDUAL_PROFILE_SERVICE_FAILURE,
                        CommonConstants.SERVICE_CODE_INDIVIDUAL_PROFILE, "Profile name for individual is invalid" ),
                    e.getMessage() );
            }
        } catch ( ProfileNotFoundException e ) {
            LOG.error( "Excpetion caught " + e.getMessage() );
            Map<String, String> nameMap = profileManagementService.findNamesfromProfileName( agentProfileName );
            redirectAttributes.addFlashAttribute( "patternFirst", nameMap.get( CommonConstants.PATTERN_FIRST ) );
            redirectAttributes.addFlashAttribute( "patternLast", nameMap.get( CommonConstants.PATTERN_LAST ) );
            return "redirect:/" + JspResolver.FINDAPRO + ".do";
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException: message : " + e.getMessage(), e );
            model.addAttribute( "message", messageUtils
                .getDisplayMessage( DisplayMessageConstants.INVALID_INDIVIDUAL_PROFILENAME, DisplayMessageType.ERROR_MESSAGE )
                .getMessage() );
            return JspResolver.NOT_FOUND_PAGE;
        } catch ( NoRecordsFetchedException e ) {
            LOG.error( "NoRecordsFetchedException: message : " + e.getMessage(), e );
            throw e;
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException: message : " + e.getMessage(), e );
            model.addAttribute( "message", messageUtils
                .getDisplayMessage( DisplayMessageConstants.GENERAL_ERROR, DisplayMessageType.ERROR_MESSAGE ).getMessage() );
            return JspResolver.MESSAGE_HEADER;
        }

        model.addAttribute( "agentProfileName", agentProfileName );
        model.addAttribute( "profileLevel", CommonConstants.PROFILE_LEVEL_INDIVIDUAL );

        LOG.info( "Service to initiate agent profile page executed successfully" );
        if ( isBotRequest ) {
            return JspResolver.PROFILE_PAGE_NOSCRIPT;
        } else {
            return JspResolver.PROFILE_PAGE;
        }
    }


    private String makeJsonMessage( int status, String message )
    {

        JSONObject jsonMessage = new JSONObject();
        LOG.debug( "Building json response" );
        try {
            jsonMessage.put( "success", status );
            jsonMessage.put( "message", message );
        } catch ( JSONException e ) {
            LOG.error( "Exception occured while building json response : " + e.getMessage(), e );
        }

        LOG.info( "Returning json response : " + jsonMessage.toString() );
        return jsonMessage.toString();
    }


    /**
     * Method called on click of the contact us link on all profile pages
     * @param request
     * @return
     */
    @RequestMapping ( value = "/profile/sendmail", method = RequestMethod.POST)
    public @ResponseBody String sendEmail( HttpServletRequest request )
    {

        LOG.info( "Contact us mail controller called!" );

        String profileType = request.getParameter( "profiletype" );
        String returnMessage = null;

        try {

            if ( profileType == null || profileType.isEmpty() ) {
                LOG.error( "Profile type not mentioned!" );
                throw new InvalidInputException( "Profile type not mentioned!" );
            }

            String profileName = request.getParameter( "profilename" );
            String senderName = request.getParameter( "name" );
            String senderMailId = request.getParameter( "email" );
            String message = request.getParameter( "message" );

            if ( validateCaptcha.equals( CommonConstants.YES_STRING ) ) {

                try {
                    if ( !captchaValidation.isCaptchaValid( request.getRemoteAddr(), captchaSecretKey,
                        request.getParameter( "g-recaptcha-response" ) ) ) {
                        LOG.error( "Captcha Validation failed!" );
                        throw new InvalidInputException( "Captcha Validation failed!",
                            DisplayMessageConstants.INVALID_CAPTCHA );

                    }
                } catch ( InvalidInputException e ) {
                    returnMessage = messageUtils
                        .getDisplayMessage( DisplayMessageConstants.INVALID_CAPTCHA, DisplayMessageType.SUCCESS_MESSAGE )
                        .toString();
                    return makeJsonMessage( CommonConstants.STATUS_INACTIVE, returnMessage );
                }
            }

            LOG.debug( "Sending mail to :  " + profileName + " from : " + senderMailId );

            profileManagementService.findProfileMailIdAndSendMail( profileName, message, senderName, senderMailId,
                profileType );
            LOG.debug( "Mail sent!" );
            returnMessage = messageUtils
                .getDisplayMessage( DisplayMessageConstants.CONTACT_US_MESSAGE_SENT, DisplayMessageType.SUCCESS_MESSAGE )
                .toString();
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException : message : " + e.getMessage(), e );
            returnMessage = messageUtils
                .getDisplayMessage( DisplayMessageConstants.CONTACT_US_MESSAGE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE )
                .toString();
            return makeJsonMessage( CommonConstants.STATUS_INACTIVE, returnMessage );
        } catch ( NoRecordsFetchedException e ) {
            LOG.error( "NoRecordsFetchedException : message : " + e.getMessage(), e );
            returnMessage = messageUtils
                .getDisplayMessage( DisplayMessageConstants.CONTACT_US_MESSAGE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE )
                .toString();
            return makeJsonMessage( CommonConstants.STATUS_INACTIVE, returnMessage );
        } catch ( UndeliveredEmailException e ) {
            LOG.error( "UndeliveredEmailException : message : " + e.getMessage(), e );
            returnMessage = messageUtils
                .getDisplayMessage( DisplayMessageConstants.CONTACT_US_MESSAGE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE )
                .toString();
            return makeJsonMessage( CommonConstants.STATUS_INACTIVE, returnMessage );
        } catch ( Exception e ) {
            LOG.error( "Exception : message : " + e.getMessage(), e );
            returnMessage = messageUtils
                .getDisplayMessage( DisplayMessageConstants.CONTACT_US_MESSAGE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE )
                .toString();
            return makeJsonMessage( CommonConstants.STATUS_INACTIVE, returnMessage );
        }

        return makeJsonMessage( CommonConstants.STATUS_ACTIVE, returnMessage );
    }

}
