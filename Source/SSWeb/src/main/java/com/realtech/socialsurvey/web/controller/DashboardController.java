package com.realtech.socialsurvey.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.AbusiveMailSettings;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfileStage;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyRecipient;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.organizationmanagement.DashboardService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.SurveyPreInitiationService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.reports.AdminReports;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.services.surveybuilder.impl.DuplicateSurveyRequestException;
import com.realtech.socialsurvey.core.services.surveybuilder.impl.SelfSurveyInitiationException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.core.vo.SurveyInviteResponse;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.common.ErrorResponse;
import com.realtech.socialsurvey.web.common.JspResolver;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


@Controller
public class DashboardController
{

    private static final Logger LOG = LoggerFactory.getLogger( DashboardController.class );

    @Autowired
    private SessionHelper sessionHelper;

    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private SolrSearchService solrSearchService;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private SurveyHandler surveyHandler;

    @Autowired
    private URLGenerator urlGenerator;

    @Autowired
    private SurveyPreInitiationService surveyPreInitiationService;

    @Autowired
    BatchTrackerService batchTrackerService;
    
    @Autowired
    SocialManagementService socialManagementService;

    @Autowired
    private AdminReports adminReport;
    
    @Autowired
    private SSApiIntergrationBuilder sSApiIntergrationBuilder;

    @Value ( "${APPLICATION_SUPPORT_EMAIL}")
    private String applicationSupportEmail;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String applicationAdminEmail;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String applicationAdminName;

    @Value ( "${APPLICATION_LOGO_URL}")
    private String appLogoUrl;

    @Value ( "${APPLICATION_BASE_URL}")
    private String appBaseUrl;
    
    @Autowired
    SSApiIntergrationBuilder ssApiIntergrationBuilder;
    
    @Value("${SOCIAL_MONITOR_AUTH_HEADER}")
    private String authHeader;

    /*
     * Method to initiate dashboard
     */
    @RequestMapping ( value = "/dashboard")
    public String initDashboardPage( Model model, HttpServletRequest request ) throws NonFatalException
    {
        LOG.info( "Dashboard Page started" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();
        if ( user == null ) {
        	LOG.warn("NonFatalException while logging in.");
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

        model.addAttribute( "userId", user.getUserId() );
        model.addAttribute( "emailId", adminUserid != null ? applicationAdminEmail : user.getEmailId() );
        model.addAttribute( "profileName", profileName );

        //get detail of expire social media
        boolean isSocialMediaExpired = false;
        if ( organizationManagementService.getExpiredSocailMedia( entityType, entityId ).size() > 0 ) {
            isSocialMediaExpired = true;
        }
        session.setAttribute( "isSocialMediaExpired", isSocialMediaExpired );

        return JspResolver.DASHBOARD;
    }


    @RequestMapping ( value = "/ishiddensection")
    @ResponseBody
    public String isHiddenSection()
    {
        User user = sessionHelper.getCurrentUser();
        boolean hiddenSection = false;
        try {
            OrganizationUnitSettings settings = organizationManagementService
                .getCompanySettings( user.getCompany().getCompanyId() );
            if ( settings != null ) {
                hiddenSection = settings.isHiddenSection();
            }
        } catch ( InvalidInputException e ) {
            LOG.error( "fetching hiddensction varibale value failed." , e );
        }
        return String.valueOf( hiddenSection );
    }


    /*
     * Method to get profile details for displaying
     */
    @RequestMapping ( value = "/profiledetails")
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
                        "NumberFormatException caught in getProfileDetails() while converting columnValue for regionId/branchId/agentId", e );
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
                        "NumberFormatException caught in getProfileDetails() while converting columnValue for regionId/branchId/agentId", e );
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
            //Code to determine if social media can be overridden during autologin
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

            // calculating details for circles
            int numberOfDays = -1;
            try {
                if ( request.getParameter( "numberOfDays" ) != null && !request.getParameter( "numberOfDays" ).isEmpty() ) {
                    numberOfDays = Integer.parseInt( request.getParameter( "numberOfDays" ) );
                }
            } catch ( NumberFormatException e ) {
                LOG.error( "NumberFormatException caught in getProfileDetails() while converting numberOfDays", e );
                throw e;
            }

            if ( realtechAdmin ) {
                columnName = null;
            }
            LOG.debug( "Getting the survey score" );
            double surveyScore = dashboardService.getSurveyScore( columnName, columnValue, numberOfDays, realtechAdmin );
            //get formatted survey score using rating format  
            surveyScore = surveyHandler.getFormattedSurveyScore( surveyScore );
            LOG.debug( "Getting the sent surveys count" );
            int sentSurveyCount = (int) dashboardService.getAllSurveyCount( columnName, columnValue, numberOfDays );
            LOG.debug( "Getting the social posts count with hierarchy" );
            
            int socialPostsCount=0;
            if(columnName!=null ){
            		socialPostsCount = (int) dashboardService.getSocialPostsForPastNdaysWithHierarchy( columnName, columnValue, numberOfDays );
            }
            
            int profileCompleteness = 0;
            if ( !realtechAdmin ) {
                LOG.debug( "Getting profile completeness" );
                profileCompleteness = dashboardService.getProfileCompletionPercentage( user, columnName, columnValue,
                    unitSettings );
            }
            model.addAttribute( "socialScore", surveyScore );
            if ( sentSurveyCount > 999 ) {
                int quotient = sentSurveyCount / 1000;
                model.addAttribute( "surveyCount", quotient + "K+" );
            } else {
                model.addAttribute( "surveyCount", sentSurveyCount );
            }
            if ( socialPostsCount > 999 ) {
                int quotient = socialPostsCount / 1000;
                model.addAttribute( "socialPosts", quotient + "K+" );
            } else {
                model.addAttribute( "socialPosts", socialPostsCount );
            }

            model.addAttribute( "profileCompleteness", profileCompleteness );
            LOG.debug( "Getting the badges" );
            model.addAttribute( "badges",
                dashboardService.getBadges( surveyScore, sentSurveyCount, socialPostsCount, profileCompleteness ) );

            model.addAttribute( "columnName", columnName );
            model.addAttribute( "columnValue", columnValue );

            LOG.info( "Method to get profile of company/region/branch/agent getProfileDetails() finished" );
            return JspResolver.DASHBOARD_PROFILEDETAIL;
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "NonFatalException while fetching profile details", e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( DisplayMessageConstants.GENERAL_ERROR, DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }
    }


    /*
     * Method to get survey details for showing details
     */
    @RequestMapping ( value = "/surveycount")
    public String getSurveyCount( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to get count of all, completed and clicked surveys, getSurveyCount() started" );

        String columnName = request.getParameter( "columnName" );
        long columnValue = 0;
        User user = sessionHelper.getCurrentUser();
        boolean realtechAdmin = user.isSuperAdmin();

        try {
            String columnValueStr = request.getParameter( "columnValue" );
            columnValue = Long.parseLong( columnValueStr );
        } catch ( NumberFormatException e ) {
            LOG.error(
                "NumberFormatException caught in getSurveyCount() while converting columnValue for regionId/branchId/agentId.",
                e );
            throw e;
        }

        if ( columnName.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) && !realtechAdmin ) {
            columnValue = user.getCompany().getCompanyId();
        }

        int numberOfDays = -1;
        try {
            if ( request.getParameter( "numberOfDays" ) != null ) {
                numberOfDays = Integer.parseInt( request.getParameter( "numberOfDays" ) );
            }
        } catch ( NumberFormatException e ) {
            LOG.error( "NumberFormatException caught in getSurveyCount() while converting numberOfDays.", e );
            throw e;
        }

        try {
            model.addAttribute( "allSurveySent",
                dashboardService.getAllSurveyCountForStatistics( columnName, columnValue, numberOfDays ) );
            model.addAttribute( "completedSurvey",
                dashboardService.getCompleteSurveyCount( columnName, columnValue, numberOfDays ) );
            model.addAttribute( "clickedSurvey",
                dashboardService.getClickedSurveyCountForPastNdays( columnName, columnValue, numberOfDays ) );
            model.addAttribute( "socialPosts", dashboardService
                .getSocialPostsForPastNdaysWithHierarchyForStatistics( columnName, columnValue, numberOfDays ) );
            model.addAttribute( "importedFromZillow",
                dashboardService.getZillowImportCount( columnName, columnValue, numberOfDays ) );
            model.addAttribute( "importedFrom3rdParty",
                dashboardService.get3rdPartyImportCount( columnName, columnValue, numberOfDays ) );
        } catch ( InvalidInputException e ) {
            LOG.error( "Found InvalidInputException.", e );

        }

        LOG.info( "Method to get count of all, completed and clicked surveys, getSurveyCount() finished" );
        return JspResolver.DASHBOARD_SURVEYSTATUS;
    }


    /*
     * Method to get survey details for generating graph.
     */
    @ResponseBody
    @RequestMapping ( value = "/surveydetailsforgraph")
    public String getSurveyDetailsForGraph( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to get survey details for generating graph, getGraphDetailsForWeek() started." );
        User user = sessionHelper.getCurrentUser();
        boolean realtechAdmin = user.isSuperAdmin();

        try {
            String columnName = request.getParameter( "columnName" );
            if ( !realtechAdmin && ( columnName == null || columnName.isEmpty() ) ) {
                LOG.warn( "Null/Empty value found for field columnName." );
                throw new NonFatalException( "Null/Empty value found for field columnName." );
            }

            long columnValue = 0;
            try {
                String columnValueStr = request.getParameter( "columnValue" );
                columnValue = Long.parseLong( columnValueStr );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "NumberFormatException in getSurveyCountForCompany() while converting columnValue for regionId/branchId/agentId.", e );
                throw e;
            }

            int numberOfDays = -1;
            try {
                if ( request.getParameter( "numberOfDays" ) != null ) {
                    numberOfDays = Integer.parseInt( request.getParameter( "numberOfDays" ) );
                }
            } catch ( NumberFormatException e ) {
                LOG.error( "NumberFormatException caught in getSurveyCount() while converting numberOfDays.", e );
                throw e;
            }

            if ( !realtechAdmin && columnName.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
                columnValue = user.getCompany().getCompanyId();
            }
            LOG.info( "Method to get details for generating graph, getGraphDetailsForWeek() finished." );

            try {
                return new Gson().toJson(
                    dashboardService.getSurveyDetailsForGraph( columnName, columnValue, numberOfDays, realtechAdmin ) );
            } catch ( ParseException e ) {
                LOG.error( "Parse Exception occurred in getSurveyDetailsForGraph(). Nested exception is ", e );
                return e.getMessage();
            }
        } catch ( NonFatalException e ) {
            LOG.error(
                "NonFatalException caught in getSurveyDetailsForGraph() while getting details of surveys for graph. Nested exception is ",
                e );
            return e.getMessage();
        }
    }


    /*
     * Method to fetch reviews for showing on dash board based upon start index
     * and batch size.
     */
    @RequestMapping ( value = "/fetchdashboardreviews")
    public String getReviews( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to get reviews of company, region, branch, agent getReviews() started." );
        User user = sessionHelper.getCurrentUser();
        List<SurveyDetails> surveyDetails = new ArrayList<>();

        try {
            String startIndexStr = request.getParameter( "startIndex" );
            String batchSizeStr = request.getParameter( "batchSize" );
            int startIndex = Integer.parseInt( startIndexStr );
            int batchSize = Integer.parseInt( batchSizeStr );

            String columnName = request.getParameter( "columnName" );
            String columnValue = request.getParameter( "columnValue" );
            long iden = 0;
            
            String currentSessionUserId = Long.toString(user.getUserId());

            if ( columnName == null || columnName.isEmpty() ) {
                LOG.warn( "Invalid value (null/empty) passed for profile level." );
                throw new InvalidInputException( "Invalid value (null/empty) passed for profile level." );
            }

            try {
                iden = Long.parseLong( columnValue );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "NumberFormatException caught while parsing columnValue in getReviews(). Nested exception is ", e );
                throw e;
            }
            
            String profileLevel = getProfileLevel( columnName );
            OrganizationUnitSettings unitSettings = null;
            
            if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_COMPANY ) ) {
                unitSettings = organizationManagementService.getCompanySettings( iden );
            } else if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_INDIVIDUAL ) ) {
                unitSettings  = organizationManagementService.getAgentSettings( iden );          
            } else if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_REGION ) ) {
                unitSettings = organizationManagementService.getRegionSettings( iden );
            } else if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_BRANCH ) ){
                unitSettings = organizationManagementService.getBranchSettingsDefault( iden );
            } else {
                throw new InvalidInputException( "Invalid profile level." );
            }

            boolean hiddenSection = false;
            boolean isReplyEnabledForCompany = false;
            try {
                surveyDetails = profileManagementService.getReviews( iden, -1, -1, startIndex, batchSize, profileLevel, false,
                    null, null, "date", null, null, false );
                profileManagementService.setAgentProfileUrlForReview( surveyDetails );

                OrganizationUnitSettings settings = organizationManagementService
                    .getCompanySettings( user.getCompany().getCompanyId() );
                if ( settings != null ) {
                    hiddenSection = settings.isHiddenSection();
                    SurveySettings surveySetting = null;
                    surveySetting = settings.getSurvey_settings();
                    
                    isReplyEnabledForCompany = surveySetting.isReplyEnabledForCompany();
                }
            } catch ( InvalidInputException e ) {
                LOG.warn( "InvalidInputException caught in getReviews() while fetching reviews. Nested exception is ", e );
                throw e;
            }
            SurveySettings surveySettings = null;
            surveySettings = unitSettings.getSurvey_settings();
                
            if ( surveySettings == null ) {
                surveySettings = new SurveySettings();
            }
            
            boolean isReplyEnabled =false;
            isReplyEnabled = surveySettings.isReplyEnabled();
            
            boolean allowReply = false;
            if(isReplyEnabled == true && isReplyEnabledForCompany== true ) {
                allowReply = true;
            }
            
            model.addAttribute( "profileUrl", unitSettings.getCompleteProfileUrl() );
            model.addAttribute( "reviews", surveyDetails );
            model.addAttribute( "hiddenSection", hiddenSection );
            model.addAttribute( "startIndex", startIndex );
            model.addAttribute( "minReplyScore", surveySettings.getReviewReplyScore());
            model.addAttribute( "allowReply", allowReply);
            model.addAttribute( "allowReplyForCompany", isReplyEnabledForCompany);
            model.addAttribute( "currentSessionUserId", currentSessionUserId);
        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in getReviews() while fetching reviews. Nested exception is ", e );
            model.addAttribute( "message", e.getMessage() );
        }

        LOG.info( "Method to get reviews of company, region, branch, agent getReviews() finished." );
        return JspResolver.DASHBOARD_REVIEWS;
    }


    /*
     * Method to fetch count of all the reviews in SURVEY_DETAILS collection. It
     * returns dta on the basis of columnName and columnValue which can be
     * either of companyId/RegionId/BranchId/AgentId.
     */
    @ResponseBody
    @RequestMapping ( value = "/fetchdashboardreviewCount")
    public String getReviewCount( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to get reviews count getReviewCount() started." );
        User user = sessionHelper.getCurrentUser();
        long reviewCount = 0;

        try {
            String columnName = request.getParameter( "columnName" );
            if ( columnName == null || columnName.isEmpty() ) {
                LOG.error( "Invalid value (null/empty) passed for profile level." );
                throw new InvalidInputException( "Invalid value (null/empty) passed for profile level." );
            }
            String profileLevel = getProfileLevel( columnName );

            long iden = 0;
            if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_COMPANY ) ) {
                iden = user.getCompany().getCompanyId();
            } else if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_INDIVIDUAL ) ) {
                iden = user.getUserId();
            } else {
                String columnValue = request.getParameter( "columnValue" );
                if ( columnValue == null || columnValue.isEmpty() ) {
                    LOG.error( "Null or empty value passed for Region/BranchId. Please pass valid value." );
                    throw new InvalidInputException(
                        "Null or empty value passed for Region/BranchId. Please pass valid value." );
                }

                try {
                    iden = Long.parseLong( columnValue );
                } catch ( NumberFormatException e ) {
                    LOG.error( "NumberFormatException caught while parsing columnValue in getReviews(). Nested exception is ",
                        e );
                    throw e;
                }
            }

            // Calling service method to count number of reviews stored in
            // database.
            reviewCount = profileManagementService.getReviewsCount( iden, -1, -1, profileLevel, false, false );

        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in getReviewCount() while fetching reviews count. Nested exception is ", e );
            return new Gson().toJson( e.getMessage() );
        }
        LOG.info( "Method to get reviews count getReviewCount() finished." );
        return String.valueOf( reviewCount );
    }


    /*
     * Method to fetch name which has to be displayed in Review section of dash
     * board.
     */
    @ResponseBody
    @RequestMapping ( value = "/fetchName")
    public String getName( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to get name to display in review section getName() started." );
        User user = sessionHelper.getCurrentUser();
        String name = "";

        try {
            String columnName = request.getParameter( "columnName" );
            if ( columnName == null || columnName.isEmpty() ) {
                LOG.warn( "Invalid value (null/empty) passed for profile level." );
                throw new InvalidInputException( "Invalid value (null/empty) passed for profile level." );
            }

            long id = 0;
            if ( columnName.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                return user.getCompany().getCompany();
            } else if ( columnName.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                return user.getFirstName() + " " + user.getLastName();
            } else {
                String columnValue = request.getParameter( "columnValue" );
                if ( columnValue != null && !columnValue.isEmpty() ) {
                    try {
                        id = Long.parseLong( columnValue );
                    } catch ( NumberFormatException e ) {
                        LOG.error(
                            "NumberFormatException caught while parsing columnValue in getReviews(). Nested exception is ", e );
                        throw e;
                    }
                }

                if ( columnName.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) )
                    name = solrSearchService.searchBranchNameById( id );
                else if ( columnName.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) )
                    name = solrSearchService.searchRegionById( id );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in getReviewCount() while fetching reviews count. Nested exception is ", e );
            return new Gson().toJson( e.getMessage() );
        }

        LOG.info( "Method to get name to display in review section getName() finished." );
        return name;
    }


    /*
     * Method to fetch incomplete survey data.
     */
    @RequestMapping ( value = "/fetchdashboardincompletesurvey")
    public String getIncompleteSurvey( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to get reviews of company, region, branch, agent getReviews() started." );
        List<SurveyPreInitiation> surveyDetails;
        User user = sessionHelper.getCurrentUser();
        boolean realtechAdmin = user.isSuperAdmin();

        try {
            surveyDetails = fetchIncompleteSurveys( request, user, realtechAdmin );
            model.addAttribute( "incompleteSurveys", surveyDetails );
            String agentName = user.getFirstName() + " " + user.getLastName();
            agentName = agentName.replaceAll( "null", "" );
            model.addAttribute( "agentName", agentName );
        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in getReviews() while fetching reviews. Nested exception is ", e );
            model.addAttribute( "message", e.getMessage() );
        }

        LOG.info( "Method to get reviews of company, region, branch, agent getReviews() finished." );
        return JspResolver.DASHBOARD_INCOMPLETESURVEYS;
    }


    /*
     * Method to get the incomplete survey popup
     */
    @RequestMapping ( value = "/fetchincompletesurveypopup")
    public String getIncompleteSurveyPopup( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to get reviews of company, region, branch, agent getReviews() started." );
        List<SurveyPreInitiation> surveyDetails;
        User user = sessionHelper.getCurrentUser();
        boolean realtechAdmin = user.isSuperAdmin();
        String origin = request.getParameter("origin");

        try {
            surveyDetails = fetchIncompleteSurveys( request, user, realtechAdmin );
            for ( SurveyPreInitiation surveyPreInitiation : surveyDetails ) {
                if ( surveyPreInitiation.getAgentId() > 0 ) {
                    surveyPreInitiation.setAgentEmailId( surveyPreInitiation.getUser().getEmailId() );
                    surveyPreInitiation.setAgentName(
                        surveyPreInitiation.getUser().getFirstName() + " " + surveyPreInitiation.getUser().getLastName() );
                }
            }

            model.addAttribute( "incompleteSurveys", surveyDetails );
            HttpSession session = request.getSession( false );
            model.addAttribute( "manualSmsSurveyReminderEnabled", session.getAttribute( "manualSmsSurveyReminderEnabled" ) );

        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in getReviews() while fetching reviews. Nested exception is ", e );
            model.addAttribute( "message", e.getMessage() );
        }

        LOG.info( "Method to get reviews of company, region, branch, agent getReviews() finished." );
        if( StringUtils.equals("newDashboard", origin) ) {
            return JspResolver.HEADER_NEW_DASHBOARD_INCOMPLETESURVEYS;	
        } else {
        	return JspResolver.HEADER_DASHBOARD_INCOMPLETESURVEYS;
        }
    }


    /**
     * Method to delete multiple pre intiated survey to cancel sending reminders
     * 
     * @param model
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/deletemultipleincompletesurveyrequest")
    public String cancelMultipleSurveyReminder( Model model, HttpServletRequest request )
    {
        LOG.info( "Method cancelSurveyReminder() called" );

        String surveySetToDeleteStr = request.getParameter( "surveySetToDelete" );
        String[] surveySetToDeleteArray = surveySetToDeleteStr.split( "," );
        Set<Long> incompleteSurveyIds = new HashSet<>();
        try {
            for ( String incompleteSurveyIdStr : surveySetToDeleteArray ) {
                try {
                    long incompleteSurveyId = 0;
                    incompleteSurveyId = Long.parseLong( incompleteSurveyIdStr );
                    incompleteSurveyIds.add( incompleteSurveyId );
                } catch ( NumberFormatException e ) {
                	LOG.error("Number format exception occured while parsing incomplet survey id : {}",incompleteSurveyIdStr,e);
                    throw new NonFatalException(
                        "Number format exception occured while parsing incomplet survey id : " + incompleteSurveyIdStr, e );
                }
            }
            //Remove records from Survey Pre Initiation table
            List<SurveyPreInitiation> surveys = surveyPreInitiationService.deleteSurveyReminder( incompleteSurveyIds );

            //Remove records from Survey Details collection, if any
            dashboardService.deleteSurveyDetailsByPreInitiation( surveys );

        } catch ( NonFatalException nonFatalException ) {
            LOG.error(
                "Nonfatal exception occured in method cancelSurveyReminder, reason : ",nonFatalException );
            return CommonConstants.ERROR;
        }
        return CommonConstants.SUCCESS_ATTRIBUTE;
    }


    /*
     * Method to get count of all the incomplete surveys.
     */
    @ResponseBody
    @RequestMapping ( value = "/fetchdashboardincompletesurveycount")
    public String getIncompleteSurveyCount( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to get the incomplete survey counts for all time. " );
        long count = 0l;
        User user = sessionHelper.getCurrentUser();

        try {
            count = fetchIncompleteSurveyCountForDashboardAllTime( request, user );
        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in getReviews() while fetching reviews. Nested exception is ", e );
            return e.getMessage();
        }
        return String.valueOf( count );
    }


    @RequestMapping ( value = "/redirecttosurveyrequestpage")
    public String redirectToSurveyRequestPage( Model model, HttpServletRequest request )
    {
        User agent = sessionHelper.getCurrentUser();
        model.addAttribute( "agentId", agent.getUserId() );
        model.addAttribute( "agentName", agent.getFirstName() + agent.getLastName() );
        model.addAttribute( "agentEmail", agent.getEmailId() );
        return JspResolver.SURVEY_REQUEST;
    }


    @ResponseBody
    @RequestMapping ( value = "/fetchagentsforadmin")
    public String fetchAgentsForAdmin( Model model, HttpServletRequest request )
    {
        LOG.info( "Method fetchAgentsForAdmin() started." );
        User user = sessionHelper.getCurrentUser();

        List<SolrDocument> solrDocuments = null;
        try {
            String columnName = request.getParameter( "columnName" );
            if ( columnName == null || columnName.isEmpty() ) {
                LOG.warn( "Invalid value (null/empty) passed for profile level." );
                throw new InvalidInputException( "Invalid value (null/empty) passed for profile level." );
            }

            String idenFieldName = "";
            String profileLevel = getProfileLevel( columnName );
            switch ( profileLevel ) {
                case CommonConstants.PROFILE_LEVEL_COMPANY:
                    idenFieldName = CommonConstants.COMPANY_ID_SOLR;
                    break;
                case CommonConstants.PROFILE_LEVEL_REGION:
                    idenFieldName = CommonConstants.REGION_ID_COLUMN;
                    break;
                case CommonConstants.PROFILE_LEVEL_BRANCH:
                    idenFieldName = CommonConstants.BRANCH_ID_COLUMN;
                    break;
                default:
                    throw new InvalidInputException( "profile level is invalid in getProListByProfileLevel" );
            }

            long iden = 0;
            if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_COMPANY ) ) {
                iden = user.getCompany().getCompanyId();
            } else if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_INDIVIDUAL ) ) {
                iden = user.getUserId();
            } else {
                String columnValue = request.getParameter( "columnValue" );
                if ( columnValue == null || columnValue.isEmpty() ) {
                    LOG.warn( "Null or empty value passed for Region/BranchId. Please pass valid value." );
                    throw new InvalidInputException(
                        "Null or empty value passed for Region/BranchId. Please pass valid value." );
                }

                try {
                    iden = Long.parseLong( columnValue );
                } catch ( NumberFormatException e ) {
                    LOG.error( "NumberFormatException caught while parsing columnValue in getReviews(). Nested exception is ",
                        e );
                    throw e;
                }
            }

            // Searching based on searchKey
            String searchKey = request.getParameter( "searchKey" );
            if ( searchKey.contains( "<" ) ) {
                searchKey = searchKey.substring( 0, searchKey.indexOf( "<" ) ).trim();
            }
            solrDocuments = solrSearchService.searchBranchRegionOrAgentByName( CommonConstants.USER_DISPLAY_NAME_SOLR,
                searchKey, idenFieldName, iden );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatal exception caught in fetchAgentsForAdmin(). Nested exception is ", e );
            return "";
        }

        LOG.info( "Method fetchAgentsForAdmin() finished." );
        return new Gson().toJson( solrDocuments );
    }


    // fetch the count of incomplete survey
    private long fetchIncompleteSurveyCountForDashboardAllTime(HttpServletRequest request, User user) throws InvalidInputException{
    	LOG.debug("Fetching incomplete survey count.");
    	long count = 0;
    	String columnName = request.getParameter( "columnName" );
        if ( columnName == null || columnName.isEmpty() ) {
            LOG.warn( " Invalid value (null/empty) passed for columnName. " );
            throw new InvalidInputException( "Invalid value (null/empty) passed for columnName." );
        }
        String profileLevel = getProfileLevel( columnName );

        long iden = 0;
        if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_COMPANY ) ) {
            iden = user.getCompany().getCompanyId();
        } else if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_INDIVIDUAL ) ) {
            iden = user.getUserId();
        } else {
            String columnValue = request.getParameter( "columnValue" );
            if ( columnValue != null && !columnValue.isEmpty() ) {
                try {
                    iden = Long.parseLong( columnValue );
                } catch ( NumberFormatException e ) {
                    LOG.error( "NumberFormatException caught while parsing columnValue in fetchIncompleteSurveyCountForDashboardAllTime(). Nested exception is ",
                        e );
                    throw e;
                }
            }
        }

        try {
            count = profileManagementService.getIncompleteSurveyCount( iden, profileLevel, null, null );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException caught in fetchIncompleteSurveyCountForDashboardAllTime() while fetching reviews. Nested exception is ", e );
            throw e;
        }

        return count;
    }


    /*
     * Fetches incomplete surveys based upon the criteria. Criteria can be
     * startIndex and/or batchSize.
     */
    private List<SurveyPreInitiation> fetchIncompleteSurveys( HttpServletRequest request, User user, boolean realtechAdmin )
        throws InvalidInputException
    {
        LOG.debug( "Method fetchIncompleteSurveys() started" );
        List<SurveyPreInitiation> surveyDetails;
        String startIndexStr = request.getParameter( "startIndex" );
        String batchSizeStr = request.getParameter( "batchSize" );
        int startIndex = -1;
        int batchSize = -1;

        if ( startIndexStr != null && !startIndexStr.isEmpty() ) {
            try {
                startIndex = Integer.parseInt( startIndexStr );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "Invalid start index passed" );
            }
        }

        if ( batchSizeStr != null && !batchSizeStr.isEmpty() ) {
            try {
                batchSize = Integer.parseInt( batchSizeStr );
            } catch ( NumberFormatException e ) {
                LOG.error( "NumberFormatException while parsing {}", batchSize, e );
                throw new InvalidInputException( "Invalid batch size passed" );
            }
        }

        String columnName = request.getParameter( "columnName" );
        if ( !realtechAdmin && ( columnName == null || columnName.isEmpty() ) ) {
            LOG.warn( "Invalid value (null/empty) passed for profile level." );
            throw new InvalidInputException( "Invalid value (null/empty) passed for profile level." );
        }
        String profileLevel = getProfileLevel( columnName );

        long iden = 0;
        if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_COMPANY ) ) {
            iden = user.getCompany().getCompanyId();
        } else if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_INDIVIDUAL ) ) {
            iden = user.getUserId();
        } else {
            String columnValue = request.getParameter( "columnValue" );
            if ( columnValue != null && !columnValue.isEmpty() ) {
                try {
                    iden = Long.parseLong( columnValue );
                } catch ( NumberFormatException e ) {
                    LOG.error( "NumberFormatException caught while parsing columnValue in getReviews(). Nested exception is ",
                        e );
                    throw e;
                }
            }
        }

        try {
            surveyDetails = profileManagementService.getIncompleteSurvey( iden, 0, 0, startIndex, batchSize, profileLevel, null,
                null, realtechAdmin );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException caught in getReviews() while fetching reviews. Nested exception is ", e );
            throw e;
        }
        LOG.debug( "Method fetchIncompleteSurveys() finished" );
        return surveyDetails;
    }


    @RequestMapping ( value = "/findregionbranchorindividual")
    public String getRegionBranchOrAgent( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to get list of regions, branches, agents getRegionBranchOrAgent() started." );
        User user = sessionHelper.getCurrentUser();
        long regionOrBranchId = 0;
        List<SolrDocument> result = null;
        boolean isRealTechOrSSAdmin = false;


        try {

            if ( user.isSuperAdmin() || userManagementService.isUserSocialSurveyAdmin( user.getUserId() ) ) {
                isRealTechOrSSAdmin = true;
            }

            String searchColumn = request.getParameter( "searchColumn" );
            if ( searchColumn == null || searchColumn.isEmpty() ) {
                LOG.warn( "Invalid value (null/empty) passed for search criteria." );
                throw new InvalidInputException( "Invalid value (null/empty) passed for search criteria." );
            }
            model.addAttribute( "searchColumn", searchColumn );

            String columnName = request.getParameter( "columnName" );
            String columnValueStr = request.getParameter( "columnValue" );

            if ( !isRealTechOrSSAdmin ) {
                if ( columnName == null || columnName.isEmpty() ) {
                    LOG.warn( "Invalid value (null/empty) passed for profile level." );
                    throw new InvalidInputException( "Invalid value (null/empty) passed for profile level." );
                }

                if ( columnValueStr == null || columnValueStr.isEmpty() ) {
                    LOG.warn( "Invalid value (null/empty) passed for Region/branch Id." );
                    throw new InvalidInputException( "Invalid value (null/empty) passed for Region/branch Id." );
                }
            }

            String searchKey = request.getParameter( "searchKey" );
            if ( searchKey == null ) {
                searchKey = "";
            }

            if ( isRealTechOrSSAdmin ) {
                try {
                    if ( searchColumn.equalsIgnoreCase( "company" ) ) {
                        model.addAttribute( "results", organizationManagementService.getCompaniesByName( searchKey ) );
                        return JspResolver.DASHBOARD_SEARCHRESULTS;
                    } else {
                        result = solrSearchService.searchBranchRegionOrAgentByName( searchColumn, searchKey, columnName, -1 );
                    }
                } catch ( InvalidInputException e ) {
                    LOG.error(
                        "InvalidInputException caught in getRegionBranchOrAgent() while fetching details. Nested exception is ",
                        e );
                    throw e;
                }
            } else if ( columnName.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
                try {
                    result = solrSearchService.searchBranchRegionOrAgentByName( searchColumn, searchKey, columnName,
                        user.getCompany().getCompanyId() );
                } catch ( InvalidInputException e ) {
                    LOG.error(
                        "InvalidInputException caught in getRegionBranchOrAgent() while fetching details. Nested exception is ",
                        e );
                    throw e;
                }
            } else if ( columnName.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) ) {
                try {
                    regionOrBranchId = Long.parseLong( columnValueStr );
                    result = solrSearchService.searchBranchRegionOrAgentByName( searchColumn, searchKey, columnName,
                        regionOrBranchId );
                } catch ( NumberFormatException e ) {
                    LOG.error(
                        "NumberFormatException caught in getRegionBranchOrAgent() while fetching details. Nested exception is ",
                        e );
                    throw e;
                } catch ( InvalidInputException e ) {
                    LOG.error(
                        "InvalidInputException caught in getRegionBranchOrAgent() while fetching details. Nested exception is ",
                        e );
                    throw e;
                }
            } else if ( columnName.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) ) {
                try {
                    result = solrSearchService.searchBranchRegionOrAgentByName( searchColumn, searchKey, columnName,
                        regionOrBranchId );
                } catch ( InvalidInputException e ) {
                    LOG.error(
                        "InvalidInputException caught in getRegionBranchOrAgent() while fetching details. Nested exception is ",
                        e );
                    throw e;
                }
            }
            model.addAttribute( "results", result );
        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in getReviews() while fetching reviews. Nested exception is ", e );
            model.addAttribute( "message", e.getMessage() );
        }
        LOG.info( "Method to get list of regions, branches, agents getRegionBranchOrAgent() finished." );
        return JspResolver.DASHBOARD_SEARCHRESULTS;
    }


    /*
     * Method to send a reminder email to the customer if not completed survey
     * already.
     */
    @ResponseBody
    @RequestMapping ( value = "/sendsurveyremindermail")
    public String sendReminderMailForSurvey( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to send email to remind customer for survey sendReminderMailForSurvey() started." );

        Map<String, String> response = new HashMap<String, String>();

        try {
            String surveyPreInitiationIdStr = request.getParameter( "surveyPreInitiationId" );

            if ( surveyPreInitiationIdStr == null || surveyPreInitiationIdStr.isEmpty() ) {
                LOG.warn( "Invalid value (null/empty) passed for surveyPreInitiationIdStr." );
                throw new InvalidInputException( "Invalid value (null/empty) passed for surveyPreInitiationIdStr." );
            }

            long surveyPreInitiationId;
            try {
                surveyPreInitiationId = Integer.parseInt( surveyPreInitiationIdStr );
            } catch ( NumberFormatException e ) {
            	LOG.error("Invalid surveyPreInitiationIdStr passed",e);
                throw new InvalidInputException( "Invalid surveyPreInitiationIdStr passed", e );
            }
            SurveyPreInitiation survey = surveyHandler.getPreInitiatedSurveyById( surveyPreInitiationId );

            if ( survey == null ) {
            	LOG.warn("Invalid surveyPreInitiationIdStr passed");
                throw new InvalidInputException( "Invalid surveyPreInitiationIdStr passed" );
            }
            OrganizationUnitSettings companySettings = organizationManagementService
                .getCompanySettings( survey.getCompanyId() );
            if ( companySettings == null ){
            	LOG.warn("No company settings found.");
            	throw new InvalidInputException( "No company settings found" );
            }
                


            int maxReminderCount = CommonConstants.DEFAULT_MAX_REMINDER_COUNT;
            if ( companySettings.getSurvey_settings() != null
                && companySettings.getSurvey_settings().getMax_number_of_survey_reminders() > 0 ) {
                maxReminderCount = companySettings.getSurvey_settings().getMax_number_of_survey_reminders();
            }

            //check if max survey reminder has been reached
            if ( survey.getReminderCounts() >= maxReminderCount ) {
                response.put( "success",
                    "Cannot send the reminder email to " + survey.getCustomerEmailId() + ". No more reminders are allowed." );
                return new Gson().toJson( response );
            }

            Calendar yesterDayDate = Calendar.getInstance();
            yesterDayDate.add( Calendar.DATE, -1 );
            if ( survey.getLastReminderTime().after( yesterDayDate.getTime() ) ) {
                response.put( "success", "Cannot send the reminder email to " + survey.getCustomerEmailId()
                    + ". You have sent a reminder in last 24 hrs" );
                return new Gson().toJson( response );
            }

            surveyHandler.sendSurveyReminderEmail( survey );
            // Increasing value of reminder count by 1.
            surveyHandler.updateReminderCount( survey.getSurveyPreIntitiationId(), true );

            response.put( "success", "Reminder mail sent successfully to " + survey.getCustomerEmailId() );

        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException caught in sendReminderMailForSurvey() while sending mail. Nested exception is ", e );
            response.put( "errMsg", e.getMessage() );
        }

        LOG.info( "Method to send email to remind customer for survey sendReminderMailForSurvey() finished." );
        return new Gson().toJson( response );
    }
    
    /**
	 * Method to send multiple survey reminders via sms
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@PostMapping ( value = "/surveys/sendsmsreminder/manual")
	public String sendMultipleSurveyRemindersViaSms( Model model, HttpServletRequest request )
	{
		LOG.info( "Method sendMultipleSurveyRemindersViaSms() called" );
		final String authorizationHeader = "Basic " + authHeader;
		try {
			String surveysSelectedStr = request.getParameter( "surveysSelected" );

			User user = sessionHelper.getCurrentUser();

			long companyId = user.getCompany().getCompanyId();
			
			LOG.info( "Company Id is - {}", companyId);

			Response apiResponse = ssApiIntergrationBuilder.getIntegrationApi().sendMultipleSurveyRemindersViaSms(authorizationHeader, companyId, surveysSelectedStr);
			return new String( ( (TypedByteArray) apiResponse.getBody() ).getBytes() );
		} catch ( Exception exception ) {
			LOG.error( "Exception occured in method sendMultipleSurveyRemindersViaSms, reason : ", exception );
			Map<String, String> response = new HashMap<>();
			response.put( "errMsg", "Something went wrong, Please try again after sometime." );
			return new Gson().toJson( response );
		}
		

	}

    /**
     * Method to resend multiple pre intiated survey reminders
     * 
     * @param model
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/resendmultipleincompletesurveyrequest")
    public String sendMultipleSurveyReminders( Model model, HttpServletRequest request )
    {
        LOG.info( "Method sendMultipleSurveyReminders() called" );

        String surveysSelectedStr = request.getParameter( "surveysSelected" );
        String[] surveysSelectedArray = surveysSelectedStr.split( "," );
        Map<String, String> response = new HashMap<String, String>();
        String responseMsg = "";

        try {
            for ( String incompleteSurveyIdStr : surveysSelectedArray ) {
                try {
                    long surveyPreInitiationId;
                    try {
                        surveyPreInitiationId = Integer.parseInt( incompleteSurveyIdStr );
                    } catch ( NumberFormatException e ) {
                    	LOG.error("Invalid surveyPreInitiationIdStr passed",e);
                        throw new InvalidInputException( "Invalid surveyPreInitiationIdStr passed", e );
                    }
                    SurveyPreInitiation survey = surveyHandler.getPreInitiatedSurveyById( surveyPreInitiationId );

                    if ( survey == null ) {
                    	LOG.warn("Invalid surveyPreInitiationIdStr passed");
                        throw new InvalidInputException( "Invalid surveyPreInitiationIdStr passed" );
                    }
                    OrganizationUnitSettings companySettings = organizationManagementService
                        .getCompanySettings( survey.getCompanyId() );
                    if ( companySettings == null ){
                    	LOG.warn("No company settings found");
                    	throw new InvalidInputException( "No company settings found" );
                    }

                    int maxReminderCount = CommonConstants.DEFAULT_MAX_REMINDER_COUNT;
                    if ( companySettings.getSurvey_settings() != null
                        && companySettings.getSurvey_settings().getMax_number_of_survey_reminders() > 0 ) {
                        maxReminderCount = companySettings.getSurvey_settings().getMax_number_of_survey_reminders();
                    }

                    //check if max survey reminder has been reached
                    if ( survey.getReminderCounts() >= maxReminderCount ) {
                        responseMsg += "Cannot send the reminder email to " + survey.getCustomerEmailId()
                            + ". No more reminders are allowed. ";
                        continue;
                    }

                    Calendar yesterDayDate = Calendar.getInstance();
                    yesterDayDate.add( Calendar.DATE, -1 );
                    if ( survey.getLastReminderTime().after( yesterDayDate.getTime() ) ) {
                        responseMsg += "Cannot send the reminder email to " + survey.getCustomerEmailId()
                            + ". You have sent a reminder in last 24 hrs. ";
                        continue;
                    }

                    surveyHandler.sendSurveyReminderEmail( survey );
                    // Increasing value of reminder count by 1.
                    surveyHandler.updateReminderCount( survey.getSurveyPreIntitiationId(), true );
                    responseMsg += "Reminder mail sent successfully to " + survey.getCustomerEmailId();

                    responseMsg += "\n";

                } catch ( NumberFormatException e ) {
                	LOG.error("Number format exception occured while parsing incomplete survey id : "+incompleteSurveyIdStr,e);
                    throw new NonFatalException(
                        "Number format exception occured while parsing incomplete survey id : " + incompleteSurveyIdStr, e );
                }
            }
            response.put( "success", responseMsg );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "Nonfatal exception occured in method sendMultipleSurveyReminders, reason : ", nonFatalException );
            response.put( "errMsg", nonFatalException.getMessage() );
        }
        return new Gson().toJson( response );
    }


    /*
     * Method to download file containing incomplete surveys
     */
    @RequestMapping ( value = "/downloaddashboardincompletesurvey")
    @ResponseBody
    public String getIncompleteSurveyFile( Model model, HttpServletRequest request, HttpServletResponse response )
    {
        LOG.info( "Method to get file containg incomplete surveys list getIncompleteSurveyFile() started." );
        User user = sessionHelper.getCurrentUser();
        String message = "";
        boolean isRealTechOrSSAdmin = false;
        HttpSession session = request.getSession( false );
        try {
            Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
            if ( adminUserid != null || user.isSuperAdmin()
                || userManagementService.isUserSocialSurveyAdmin( user.getUserId() ) ) {
                isRealTechOrSSAdmin = true;
            }
            String mailId = request.getParameter( "mailid" );
            if ( !isRealTechOrSSAdmin && ( mailId == null || mailId.isEmpty() ) ) {
                mailId = user.getEmailId();
            }

            String columnName = request.getParameter( "columnName" );
            if ( columnName == null || columnName.isEmpty() ) {
                LOG.warn( "Invalid value (null/empty) passed for profile level." );
                throw new InvalidInputException( "Invalid value (null/empty) passed for profile level." );
            }

            Date startDate = null;
            String startDateStr = request.getParameter( "startDate" );
            if ( startDateStr != null && !startDateStr.isEmpty() ) {
                try {
                    startDate = new SimpleDateFormat( CommonConstants.DATE_FORMAT ).parse( startDateStr );
                } catch ( ParseException e ) {
                    LOG.error( "ParseException caught in getCompleteSurveyFile() while parsing startDate. Nested exception is ",
                        e );
                }
            }

            Date endDate = Calendar.getInstance().getTime();
            String endDateStr = request.getParameter( "endDate" );
            if ( endDateStr != null && !endDateStr.isEmpty() ) {
                try {
                    endDate = new SimpleDateFormat( CommonConstants.DATE_FORMAT ).parse( endDateStr );
                } catch ( ParseException e ) {
                    LOG.error( "ParseException caught in getCompleteSurveyFile() while parsing startDate. Nested exception is ",
                        e );
                }
            }

            String profileLevel = getProfileLevel( columnName );
            long iden = 0;
            if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_COMPANY ) ) {
                iden = user.getCompany().getCompanyId();
            } else if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_INDIVIDUAL ) ) {
                iden = user.getUserId();
            } else {
                String columnValue = request.getParameter( "columnValue" );
                if ( columnValue != null && !columnValue.isEmpty() ) {
                    try {
                        iden = Long.parseLong( columnValue );
                    } catch ( NumberFormatException e ) {
                        LOG.error(
                            "NumberFormatExcept;ion caught while parsing columnValue in getIncompleteSurveyFile(). Nested exception is ",
                            e );
                        throw e;
                    }
                }
            }
            adminReport.createEntryInFileUploadForIncompleteSurveyReport( mailId, startDate, endDate, iden, profileLevel,
                user.getUserId(), user.getCompany() );
            message = "The Incomplete Survey Report will be mailed to you shortly";
        } catch ( NonFatalException e ) {
            LOG.error(
                "Non fatal exception caught in getReviews() while fetching incomplete reviews file. Nested exception is ", e );
            message = "Error while generating incomplete survey report request";
        }
        LOG.info( "Method to get file containg incomplete surveys list getIncompleteSurveyFile() finished." );
        return message;
    }


    @ResponseBody
    @RequestMapping ( value = "/generatecustomersurveyresults")
    public String generateCustomerSurveyResultsFile( Model model, HttpServletRequest request, HttpServletResponse response )
    {
        LOG.info( "Method to get file containg customer survey results generateCustomerSurveyResultsFile() started." );
        User user = sessionHelper.getCurrentUser();
        String message = "";
        boolean isRealTechOrSSAdmin = false;
        HttpSession session = request.getSession( false );
        try {
            Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
            if ( adminUserid != null || user.isSuperAdmin()
                || userManagementService.isUserSocialSurveyAdmin( user.getUserId() ) ) {
                isRealTechOrSSAdmin = true;
            }
            String mailId = request.getParameter( "mailid" );
            if ( !isRealTechOrSSAdmin && ( mailId == null || mailId.isEmpty() ) ) {
                mailId = user.getEmailId();
            }

            String columnName = request.getParameter( "columnName" );
            if ( !isRealTechOrSSAdmin && ( columnName == null || columnName.isEmpty() ) ) {
                LOG.warn( "Invalid value (null/empty) passed for profile level." );
                throw new InvalidInputException( "Invalid value (null/empty) passed for profile level." );
            }

            Date startDate = null;
            String startDateStr = request.getParameter( "startDate" );
            if ( startDateStr != null && !startDateStr.isEmpty() ) {
                try {
                    startDate = new SimpleDateFormat( CommonConstants.DATE_FORMAT ).parse( startDateStr );
                } catch ( ParseException e ) {
                    LOG.error(
                        "ParseException caught in generateCustomerSurveyResultsFile() while parsing startDate. Nested exception is ",
                        e );
                }
            }

            Date endDate = Calendar.getInstance().getTime();
            String endDateStr = request.getParameter( "endDate" );
            if ( endDateStr != null && !endDateStr.isEmpty() ) {
                try {
                    endDate = new SimpleDateFormat( CommonConstants.DATE_FORMAT ).parse( endDateStr );
                } catch ( ParseException e ) {
                    LOG.error(
                        "ParseException caught in generateCustomerSurveyResultsFile() while parsing startDate. Nested exception is ",
                        e );
                }
            }

            String profileLevel = getProfileLevel( columnName );
            long iden = 0;

            if ( isRealTechOrSSAdmin ) {
                String columnValue = request.getParameter( "columnValue" );
                if ( columnValue != null && !columnValue.isEmpty() ) {
                    try {
                        iden = Long.parseLong( columnValue );
                    } catch ( NumberFormatException e ) {
                        LOG.error(
                            "NumberFormatException caught while parsing columnValue in generateCustomerSurveyResultsFile(). Nested exception is ",
                            e );
                        throw e;
                    }
                }
            } else if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_COMPANY ) ) {
                iden = user.getCompany().getCompanyId();
            } else if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_INDIVIDUAL ) ) {
                iden = user.getUserId();
            } else {
                String columnValue = request.getParameter( "columnValue" );
                if ( columnValue != null && !columnValue.isEmpty() ) {
                    try {
                        iden = Long.parseLong( columnValue );
                    } catch ( NumberFormatException e ) {
                        LOG.error(
                            "NumberFormatException caught while parsing columnValue in generateCustomerSurveyResultsFile(). Nested exception is ",
                            e );
                        throw e;
                    }
                }
            }

            adminReport.createEntryInFileUploadForSurveyDataReport( mailId, startDate, endDate, iden, profileLevel,
                user.getUserId(), user.getCompany() );
            message = "The Survey Data Report will be mailed to you shortly";
        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in getCustomerSurveyResultsFile(). Nested exception is ", e );
            message = "Error while generating survey data report request";
        }
        LOG.info( "Method getCustomerSurveyResultsFile() finished." );
        return message;
    }


    @RequestMapping ( value = "/downloaddashboardsocialmonitor")
    @ResponseBody
    public String getSocialMonitorFile( Model model, HttpServletRequest request, HttpServletResponse response )
    {
        LOG.info( "Method to get file containg Social Monitors list getSocialMonitorFile() started." );
        User user = sessionHelper.getCurrentUser();
        String message = null;
        try {
            boolean isRealTechOrSSAdmin = false;
            HttpSession session = request.getSession( false );
            Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
            if ( adminUserid != null || user.isSuperAdmin()
                || userManagementService.isUserSocialSurveyAdmin( user.getUserId() ) ) {
                isRealTechOrSSAdmin = true;
            }
            String mailId = request.getParameter( "mailid" );
            if ( !isRealTechOrSSAdmin && ( mailId == null || mailId.isEmpty() ) ) {
                mailId = user.getEmailId();
            }

            String columnName = request.getParameter( "columnName" );
            if ( !isRealTechOrSSAdmin && ( columnName == null || columnName.isEmpty() ) ) {
                LOG.warn( "Invalid value (null/empty) passed for profile level." );
                throw new InvalidInputException( "Invalid value (null/empty) passed for profile level." );
            }

            String startDateStr = request.getParameter( "startDate" );
            String endDateStr = request.getParameter( "endDate" );

            Date startDate = null;
            Date endDate = Calendar.getInstance().getTime();
            if ( startDateStr != null && !startDateStr.isEmpty() ) {
                try {
                    startDate = new SimpleDateFormat( CommonConstants.DATE_FORMAT ).parse( startDateStr );
                } catch ( ParseException e ) {
                    LOG.error( "ParseException caught in getSocialMonitorFile() while parsing startDate. Nested exception is ",
                        e );
                }
            }
            if ( endDateStr != null && !endDateStr.isEmpty() ) {
                try {
                    endDate = new SimpleDateFormat( CommonConstants.DATE_FORMAT ).parse( endDateStr );
                } catch ( ParseException e ) {
                    LOG.error( "ParseException caught in getSocialMonitorFile() while parsing startDate. Nested exception is ",
                        e );
                }
            }

            String profileLevel = getProfileLevel( columnName );
            long iden = 0;

            if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_COMPANY ) ) {
                iden = user.getCompany().getCompanyId();
            } else if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_INDIVIDUAL ) ) {
                iden = user.getUserId();
            } else {
                String columnValue = request.getParameter( "columnValue" );
                if ( columnValue != null && !columnValue.isEmpty() ) {
                    try {
                        iden = Long.parseLong( columnValue );
                    } catch ( NumberFormatException e ) {
                        LOG.error(
                            "NumberFormatException caught while parsing columnValue in getSocialMonitorFile(). Nested exception is ",
                            e );
                        throw e;
                    }
                }
            }
            adminReport.createEntryInFileUploadForSocialMonitorReport( mailId, startDate, endDate, iden, profileLevel,
                user.getUserId(), user.getCompany() );
            message = "The Social Monitor Report will be mailed to you shortly";

        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in getSocialMonitorFile(). Nested exception is ", e );
            message = "Error while generating social monitor report request";
        }
        LOG.info( "Method getSocialMonitorFile() finished." );
        return message;
    }


    /*
     * Method to download file agent ranking report
     */
    @RequestMapping ( value = "/downloadagentrankingreport")
    @ResponseBody
    public String getAgentRankingFile( Model model, HttpServletRequest request, HttpServletResponse response )
    {
        LOG.info( "Method to get file containg Agent's data getAgentSurveyFile() started." );
        User user = sessionHelper.getCurrentUser();
        String message = null;
        try {
            boolean isRealTechOrSSAdmin = false;
            HttpSession session = request.getSession( false );
            Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
            if ( adminUserid != null || user.isSuperAdmin()
                || userManagementService.isUserSocialSurveyAdmin( user.getUserId() ) ) {
                isRealTechOrSSAdmin = true;
            }
            String mailId = request.getParameter( "mailid" );
            if ( !isRealTechOrSSAdmin && ( mailId == null || mailId.isEmpty() ) ) {
                mailId = user.getEmailId();
            }

            String columnName = request.getParameter( "columnName" );
            if ( !isRealTechOrSSAdmin && ( columnName == null || columnName.isEmpty() ) ) {
                LOG.warn( "Invalid value (null/empty) passed for profile level." );
                throw new InvalidInputException( "Invalid value (null/empty) passed for profile level." );
            }

            String startDateStr = request.getParameter( "startDate" );
            String endDateStr = request.getParameter( "endDate" );

            Date startDate = null;
            Date endDate = Calendar.getInstance().getTime();
            if ( startDateStr != null && !startDateStr.isEmpty() ) {
                try {
                    startDate = new SimpleDateFormat( CommonConstants.DATE_FORMAT ).parse( startDateStr );
                } catch ( ParseException e ) {
                    LOG.error( "ParseException caught in getAgentSurveyFile() while parsing startDate. Nested exception is ",
                        e );
                }
            }
            if ( endDateStr != null && !endDateStr.isEmpty() ) {
                try {
                    endDate = new SimpleDateFormat( CommonConstants.DATE_FORMAT ).parse( endDateStr );
                } catch ( ParseException e ) {
                    LOG.error( "ParseException caught in getAgentSurveyFile() while parsing startDate. Nested exception is ",
                        e );
                }
            }

            String profileLevel = getProfileLevel( columnName );
            long iden = 0;

            if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_COMPANY ) ) {
                iden = user.getCompany().getCompanyId();
            } else if ( profileLevel.equals( CommonConstants.PROFILE_LEVEL_INDIVIDUAL ) ) {
                iden = user.getUserId();
            } else {
                String columnValue = request.getParameter( "columnValue" );
                if ( columnValue != null && !columnValue.isEmpty() ) {
                    try {
                        iden = Long.parseLong( columnValue );
                    } catch ( NumberFormatException e ) {
                        LOG.error(
                            "NumberFormatExcept;ion caught while parsing columnValue in getAgentSurveyFile(). Nested exception is ",
                            e );
                        throw e;
                    }
                }
            }
            adminReport.createEntryInFileUploadForAgentRankingReport( mailId, startDate, endDate, iden, profileLevel,
                user.getUserId(), user.getCompany() );
            message = "The User Ranking Report will be mailed to you shortly";
        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in getAgentSurveyFile(). Nested exception is ", e );
            message = "Error while generating user ranking report request";
        }
        LOG.info( "Method to get file containg Agent's data getAgentSurveyFile() finished." );
        return message;
    }


    /*
     * Method to 1. Store initial details of customer. 2. Send Invitation mail
     * to the customer to take survey.
     */
    @ResponseBody
    @RequestMapping ( value = "/sendsurveyinvite")
    public String sendSurveyInvitation( HttpServletRequest request )
    {
        LOG.info( "Method sendSurveyInvitation() called from DashboardController." );
        String custFirstName = request.getParameter( "firstName" );
        String custLastName = request.getParameter( "lastName" );
        String custEmail = request.getParameter( "email" );
        User user = sessionHelper.getCurrentUser();
        String errorMsg = null;
        try {
            try {
                surveyHandler.initiateSurveyRequest( user.getUserId(), custEmail, custFirstName, custLastName, "customer", null );
            } catch ( SelfSurveyInitiationException e ) {
                errorMsg = messageUtils
                    .getDisplayMessage( DisplayMessageConstants.SELF_SURVEY_INITIATION, DisplayMessageType.ERROR_MESSAGE )
                    .getMessage();
                throw new NonFatalException( e.getMessage(), e.getErrorCode() );
            } catch ( DuplicateSurveyRequestException e ) {
                errorMsg = messageUtils
                    .getDisplayMessage( DisplayMessageConstants.DUPLICATE_SURVEY_REQUEST, DisplayMessageType.ERROR_MESSAGE )
                    .getMessage();
                throw new NonFatalException( e.getMessage(), e.getErrorCode() );
            }


        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException caught in sendSurveyInvitation(). Nested exception is ", e );
            if ( errorMsg == null )
                errorMsg = "error";
            return errorMsg;
        }

        LOG.info( "Method sendSurveyInvitation() finished from DashboardController." );
        return "Success";
    }


    @ResponseBody
    @PostMapping ( value = "/getalreadysurveyedemailids")
    public ResponseEntity<?> getAlreadySurveyedEmailIds( @QueryParam ( "source") String source,
        @RequestBody List<SurveyRecipient> surveyRecipients )
    {
        LOG.info( "Method getAlreadySurveyedEmailIds() called from DashboardController." );
        User user = sessionHelper.getCurrentUser();
        
        List<SurveyRecipient> alreadySurveyedList =new ArrayList<>();

        long agentId = 0;
        
        List<String> alreadySurveyedEmails = new ArrayList<>();
        try {

            if ( CollectionUtils.isEmpty( surveyRecipients ) ) {
             
                return new ResponseEntity<>(getErrorResponse( "400", "SurveyRecipients passed was null or empty" ), HttpStatus.BAD_REQUEST );
            }

            if ( source.equalsIgnoreCase( CommonConstants.SURVEY_REQUEST_AGENT ) ) {
                agentId = user.getUserId();
            }

            for ( SurveyRecipient recipient : surveyRecipients ) {
                long currentAgentId = 0;
                if ( agentId != 0 ) {
                    currentAgentId = agentId;
                } else if ( recipient.getAgentId() != 0 ) {
                    currentAgentId = recipient.getAgentId();
                } else {
                    return new ResponseEntity<>(getErrorResponse("400", "Agent id can not be null"), HttpStatus.BAD_REQUEST );
                }
                boolean foundSurveys = false;
                SurveyRecipient alreadySurveyed = new SurveyRecipient();
                if ( surveyHandler.hasCustomerAlreadySurveyed( currentAgentId, recipient.getEmailId() ) ) {
                    alreadySurveyed.setRefId( recipient.getRefId() );
                    alreadySurveyed.setEmailId( recipient.getEmailId() );
                    foundSurveys = true;
                }

                if (StringUtils.isNotEmpty( recipient.getContactNumber() ) && surveyHandler.hasCustomerAlreadySurveyedForContactNumber( currentAgentId, recipient.getContactNumber())) {
                    alreadySurveyed.setRefId( recipient.getRefId() );
                    alreadySurveyed.setContactNumber( recipient.getContactNumber());
                    foundSurveys = true;
                }
                
                if(foundSurveys) {
                    alreadySurveyedList.add( alreadySurveyed );
                }
            }
        } catch ( Exception e ) {
            LOG.error( "NonFatalException caught in getAlreadySurveyedEmailIds(). Nested exception is ", e );
            return new ResponseEntity<>(getErrorResponse("500", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR );
        }

        LOG.info( "Method getAlreadySurveyedEmailIds() finished from DashboardController." );

        return new ResponseEntity<>( alreadySurveyedList, HttpStatus.OK );
    }


    @ResponseBody
    @PostMapping ( value = "/sendmultiplesurveyinvites")
    public ResponseEntity<?> sendMultipleSurveyInvitations(  @QueryParam ( "source") String source,
        @RequestBody List<SurveyRecipient> surveyRecipients )
    {
        LOG.info( "Method sendMultipleSurveyInvitations() called from DashboardController." );
        User user = sessionHelper.getCurrentUser();
        long agentId = 0;
        String selfSurveyErrorMsg = null;
        String duplicateSurveyErrorMsg = null;
        String errorMsg = "";
        int surveySentCount = 0;

        try {
            
            if ( CollectionUtils.isEmpty( surveyRecipients ) ) {
                
                return new ResponseEntity<>(getErrorResponse( "400", "SurveyRecipients passed was null or empty" ), HttpStatus.BAD_REQUEST );
            }
           
            if ( source.equalsIgnoreCase( CommonConstants.SURVEY_REQUEST_AGENT ) ) {
                agentId = user.getUserId();
            }

            //check if no duplicate entries in list
            if ( !surveyRecipients.isEmpty() ) {
                for ( int i = 0; i < surveyRecipients.size(); i++ ) {
                    for ( int j = i + 1; j < surveyRecipients.size(); j++ ) {
                        if ( surveyRecipients.get( i ).getEmailId()
                            .equalsIgnoreCase( surveyRecipients.get( j ).getEmailId() ) ) {
                            if ( surveyRecipients.get( i ).getAgentEmailId()
                                .equalsIgnoreCase( surveyRecipients.get( j ).getAgentEmailId() ) ) {
                            	LOG.warn("Can't enter same email address multiple times for same user");
                                throw new InvalidInputException(
                                    "Can't enter same email address multiple times for same user" );
                            }
                        }
                    }
                }
            }

            // sending mails on traversing the list
            if ( !surveyRecipients.isEmpty() ) {
                for ( SurveyRecipient recipient : surveyRecipients ) {
                    long currentAgentId = 0;
                    if ( agentId != 0 ) {
                        currentAgentId = agentId;
                    } else if ( recipient.getAgentId() != 0 ) {
                        currentAgentId = recipient.getAgentId();
                    } else {
                    	LOG.warn("Agent id can not be null.");
                        throw new InvalidInputException( "Agent id can not be null" );
                    }


                    try {
                        surveyHandler.initiateSurveyRequest( currentAgentId, recipient.getEmailId(), recipient.getFirstname(),
                            recipient.getLastname(), source, recipient.getContactNumber() );
                        surveySentCount++;
                    } catch ( SelfSurveyInitiationException e ) {
                        if ( selfSurveyErrorMsg == null ) {
                            selfSurveyErrorMsg = messageUtils.getDisplayMessage( DisplayMessageConstants.SELF_SURVEY_INITIATION,
                                DisplayMessageType.ERROR_MESSAGE ).getMessage();
                        }
                    } catch ( DuplicateSurveyRequestException e ) {
                        if ( duplicateSurveyErrorMsg == null ) {
                            duplicateSurveyErrorMsg = messageUtils
                                .getDisplayMessage( DisplayMessageConstants.DUPLICATE_SURVEY_REQUEST,
                                    DisplayMessageType.ERROR_MESSAGE )
                                .getMessage() + " The duplicate addresses are : " + recipient.getEmailId();
                        } else {
                            duplicateSurveyErrorMsg += ", " + recipient.getEmailId();
                        }
                    }
                }
                if ( selfSurveyErrorMsg != null ) {
                    errorMsg += selfSurveyErrorMsg + "\n";
                }
                if ( duplicateSurveyErrorMsg != null ) {
                    errorMsg += duplicateSurveyErrorMsg;
                }
                if ( !errorMsg.isEmpty() ) {
                    return new ResponseEntity<>(getErrorResponse("500", errorMsg), HttpStatus.INTERNAL_SERVER_ERROR );
                }
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException caught in sendMultipleSurveyInvitations(). Nested exception is ", e );
            return new ResponseEntity<>(getErrorResponse("500", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR );
        }

        LOG.info( "Method sendMultipleSurveyInvitations() finished from DashboardController." );
        return new ResponseEntity<>(new SurveyInviteResponse( "SUCCESS", surveySentCount ), HttpStatus.OK );
    }


    @ResponseBody
    @RequestMapping ( value = "/dashboardbuttonsorder", method = RequestMethod.GET)
    public String getDashboardButtonsOrder( HttpServletRequest request )
    {
        LOG.info( "Method sendMultipleSurveyInvitations() called from DashboardController." );
        User user = sessionHelper.getCurrentUser();
        String columnName = request.getParameter( "columnName" );
        String columnValueStr = request.getParameter( "columnValue" );
        long columnValue = Long.parseLong( columnValueStr );

        List<ProfileStage> stages = new ArrayList<>();
        try {
            if ( columnName.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
                stages = new ArrayList<>( organizationManagementService.getCompanySettings( user ).getProfileStages() );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) ) {
                stages = new ArrayList<>( organizationManagementService.getRegionSettings( columnValue ).getProfileStages() );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) ) {
                stages = new ArrayList<>(
                    organizationManagementService.getBranchSettingsDefault( columnValue ).getProfileStages() );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) ) {
                stages = new ArrayList<>( userManagementService.getUserSettings( columnValue ).getProfileStages() );
            }
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "NonFatalException while fetching badge details." , e );
        }
        Collections.sort( stages );
        for ( int index = stages.size() - 1; index >= 0; index-- ) {
            if ( stages.get( index ).getStatus() == CommonConstants.STATUS_INACTIVE )
                stages.remove( index );
        }

        Map<String, Object> stagesAndColumn = new HashMap<>();
        stagesAndColumn.put( "columnName", columnName );
        stagesAndColumn.put( "columnValue", columnValue );
        stagesAndColumn.put( "stages", stages );

        LOG.info( "Method sendMultipleSurveyInvitations() finished from DashboardController." );
        return new Gson().toJson( stagesAndColumn );
    }


    @ResponseBody
    @RequestMapping ( value = "/socialmediatofix", method = RequestMethod.GET)
    public String socialMediaTofFix( HttpServletRequest request )
    {
        LOG.info( "Method socialMediaTofFix() called from DashboardController." );
        User user = sessionHelper.getCurrentUser();
        String columnName = request.getParameter( "columnName" );
        String columnValueStr = request.getParameter( "columnValue" );
        long columnValue = Long.parseLong( columnValueStr );

        OrganizationUnitSettings settings = null;
        try {
            if ( columnName.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
                settings = organizationManagementService.getCompanySettings( user );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) ) {
                settings = organizationManagementService.getRegionSettings( columnValue );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) ) {
                settings = organizationManagementService.getBranchSettingsDefault( columnValue );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) ) {
                settings = userManagementService.getUserSettings( columnValue );
            }
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "NonFatalException while fetching badge details. Reason :", e );
        }


        List<String> socialMedias = new ArrayList<String>();
        try {
            socialMedias = organizationManagementService.getExpiredSocailMedia( columnName, columnValue );
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "NonFatalException while fetching badge details. Reason :", e );
        }


        Map<String, Object> stagesAndColumn = new HashMap<>();
        stagesAndColumn.put( "columnName", columnName );
        stagesAndColumn.put( "columnValue", columnValue );
        stagesAndColumn.put( "socialMedias", socialMedias );

        LOG.info( "Method sendMultipleSurveyInvitations() finished from DashboardController." );
        return new Gson().toJson( stagesAndColumn );
    }


    private String getProfileLevel( String columnName )
    {
        LOG.debug( "Method to return profile level based upon column to be queried started. Column Name: {}", columnName );
        String profileLevel = "";
        switch ( columnName ) {
            case CommonConstants.COMPANY_ID_COLUMN:
                profileLevel = CommonConstants.PROFILE_LEVEL_COMPANY;
                break;
            case CommonConstants.REGION_ID_COLUMN:
                profileLevel = CommonConstants.PROFILE_LEVEL_REGION;
                break;
            case CommonConstants.BRANCH_ID_COLUMN:
                profileLevel = CommonConstants.PROFILE_LEVEL_BRANCH;
                break;
            case CommonConstants.AGENT_ID_COLUMN:
                profileLevel = CommonConstants.PROFILE_LEVEL_INDIVIDUAL;
                break;
        }
        LOG.debug( "Method to return profile level based upon column to be quried finished." );
        return profileLevel;
    }


    @ResponseBody
    @RequestMapping ( value = "/updatecurrentprofile")
    public String updateSelectedProfile( Model model, HttpServletRequest request )
    {
        LOG.debug( "Method updateSelectedProfile() started." );
        HttpSession session = request.getSession( false );

        String entityIdStr = request.getParameter( "entityId" );
        long entityId = 0;
        try {
            if ( entityIdStr != null && !entityIdStr.equals( "" ) ) {
                entityId = Long.parseLong( entityIdStr );
            } else {
                throw new NumberFormatException();
            }
        } catch ( NumberFormatException e ) {
            LOG.error( "Number format exception occurred while parsing the entity id. Reason :", e );
        }

        String entityType = request.getParameter( "entityType" );
        sessionHelper.updateSelectedProfile( session, entityId, entityType );
        LOG.info( "Updated current profile" );
        return CommonConstants.SUCCESS_ATTRIBUTE;
    }


    /*
     * Method to mark a particular survey as editable and re-send the link to a
     * customer.
     */
    @ResponseBody
    @RequestMapping ( value = "/restartsurvey")
    public void restartSurvey( HttpServletRequest request )
    {
        String surveyId = request.getParameter( "surveyId" );
        try {

            if ( surveyId == null || surveyId.isEmpty() ) {
                throw new InvalidInputException( "Passed parameter survey id is null or empty" );
            }

            surveyHandler.markSurveyAsRetake( surveyId, true, CommonConstants.RETAKE_REQUEST_AGENT );
            SurveyDetails survey = surveyHandler.getSurveyDetails( surveyId );
            long agentId = survey.getAgentId();
            User user = userManagementService.getUserByUserId( agentId );
            Map<String, String> urlParams = urlGenerator.decryptUrl( survey.getUrl() );
            urlParams.put( CommonConstants.URL_PARAM_RETAKE_SURVEY, "true" );
            String updatedUrl = urlGenerator.generateUrl( urlParams,
                surveyHandler.getApplicationBaseUrl() + CommonConstants.SHOW_SURVEY_PAGE_FOR_URL );
            surveyHandler.sendSurveyRestartMail( survey.getCustomerFirstName(), survey.getCustomerLastName(),
                survey.getCustomerEmail(), survey.getCustRelationWithAgent(), user, updatedUrl );
        } catch ( NonFatalException e ) {
            LOG.error( "NonfatalException caught in makeSurveyEditable(). Nested exception is ", e );
        }
    }


    // Method to report a feedback of customer as abusive.
    // Anybody in the hierarchy can report a feedback from dash board.
    @ResponseBody
    @RequestMapping ( value = "/reportabuse")
    public String reportAbuse( HttpServletRequest request )
    {
        String reason = request.getParameter( "reportText" );
        String surveyMongoId = request.getParameter( "surveyMongoId" );
        User user = sessionHelper.getCurrentUser();
        user.isSuperAdmin();
        

        try {
            try {
            } catch ( NumberFormatException e ) {
                LOG.error( "NumberFormatException caught in reportAbuse() while converting agentId.",e );
                throw e;
            }

            if ( surveyMongoId == null || surveyMongoId.isEmpty() ) {
            	LOG.warn("Invalid value (Null/Empty) found for surveyMongoId.");
                throw new InvalidInputException( "Invalid value (Null/Empty) found for surveyMongoId." );
            }

            SurveyDetails surveyDetails = surveyHandler.getSurveyDetails( surveyMongoId );
            
            String customerName = surveyDetails.getCustomerFirstName() + " " + surveyDetails.getCustomerLastName();

            //make survey as abusive
            surveyHandler.updateSurveyAsAbusive( surveyMongoId, surveyDetails.getCustomerEmail(), customerName, reason );

            // Calling email services method to send mail to the Application
            // level admin.
            emailServices.sendReportAbuseMail( applicationSupportEmail, applicationAdminName, surveyDetails.getAgentName(),
                customerName.replaceAll( "null", "" ), surveyDetails.getCustomerEmail(), surveyDetails.getReview(), reason,
                null, null );
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
				String loggedUser = user.getFirstName();
	            Date currentDate = new Date( System.currentTimeMillis() );

				if(user.getLastName() != null)
					loggedUser = loggedUser + " " + user.getLastName();
				emailServices.sendAbusiveNotifyMail(loggedUser, abusiveMailSettings.getMailId(), displayName,surveyDetails.getCustomerEmail(), surveyDetails.getAgentName(), 
						userObj.getEmailId(),surveyDetails.getMood(), String.valueOf(surveyDetails.getScore()), surveyDetails.getSourceId(), surveyDetails.getReview(),currentDate.toString());
			}
        } catch ( NonFatalException e ) {
            LOG.error( "NonfatalException caught in reportAbuse(). Nested exception is ", e );
            return CommonConstants.ERROR;
        }

        return CommonConstants.SUCCESS_ATTRIBUTE;
    }


    @RequestMapping ( value = "/fetchsociallinksinpopup")
    public String fetchSocialLinksInPopup( HttpServletRequest request, Model model )
    {
        LOG.info( "Method fetchSocialLinksInPopup() called" );
        User user = sessionHelper.getCurrentUser();
        OrganizationUnitSettings unitSettings;
        try {
            unitSettings = userManagementService.getUserSettings( user.getUserId() );
        } catch ( InvalidInputException e ) {
            LOG.error( "NonFatalException while fetching profile details. Reason :" + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( DisplayMessageConstants.GENERAL_ERROR, DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }

        SocialMediaTokens tokens = unitSettings.getSocialMediaTokens();
        if ( tokens != null ) {
            if ( tokens.getFacebookToken() != null && tokens.getFacebookToken().getFacebookPageLink() != null ) {
                model.addAttribute( "facebookProfileUrl", tokens.getFacebookToken().getFacebookPageLink() );
            }
            if ( tokens.getGoogleToken() != null && tokens.getGoogleToken().getProfileLink() != null ) {
                model.addAttribute( "googleProfileUrl", tokens.getGoogleToken().getProfileLink() );
            }
            if ( tokens.getTwitterToken() != null && tokens.getTwitterToken().getTwitterPageLink() != null ) {
                model.addAttribute( "twitterProfileUrl", tokens.getTwitterToken().getTwitterPageLink() );
            }
            if ( tokens.getLinkedInV2Token() != null && tokens.getLinkedInV2Token().getLinkedInPageLink() != null ) {
                model.addAttribute( "linkedinProfileUrl", tokens.getLinkedInV2Token().getLinkedInPageLink() );
            }
            if ( tokens.getZillowToken() != null && tokens.getZillowToken().getZillowProfileLink() != null ) {
                model.addAttribute( "zillowProfileUrl", tokens.getZillowToken().getZillowProfileLink() );
            }
            if ( tokens.getInstagramToken() != null && tokens.getInstagramToken().getPageLink() != null ) {
                model.addAttribute( "instagramProfileUrl", tokens.getInstagramToken().getPageLink() );
            }
        }

        return JspResolver.LINKEDIN_IMPORT_SOCIAL_LINKS;
    }


    /*
     * Method to download file agent ranking report
     */
    @RequestMapping ( value = "/downloaduseradoptionreport")
    @ResponseBody
    public String getUserAdoptionReportFile( Model model, HttpServletRequest request, HttpServletResponse response )
    {
        LOG.info( "Method to get user adoption report file getUserAdoptionReportFile() started." );
        User user = sessionHelper.getCurrentUser();
        String message = null;
        try {
            String mailId = request.getParameter( "mailid" );

            String columnName = request.getParameter( "columnName" );
            if ( columnName == null || columnName.isEmpty() ) {
                LOG.warn( "Invalid value (null/empty) passed for column name." );
                throw new InvalidInputException( "Invalid value (null/empty) passed for column name." );
            }

            long iden = 0;
            String columnValue = request.getParameter( "columnValue" );
            if ( columnValue != null && !columnValue.isEmpty() ) {
                try {
                    iden = Long.parseLong( columnValue );
                } catch ( NumberFormatException e ) {
                    LOG.error(
                        "NumberFormatException caught while parsing columnValue in getUserAdoptionReportFile(). Nested exception is ",
                        e );
                    throw e;
                }
            }

            String profileLevel = getProfileLevel( columnName );
            adminReport.createEntryInFileUploadForUserAdoptionReport( iden, profileLevel, user.getUserId(), user.getCompany(),
                mailId );
            message = "The User Adoption Report will be mailed to you shortly";

        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in getUserAdoptionReportFile(). Nested exception is ", e );
            message = "Error while generating user adoption report request";
        }
        LOG.info( "Method to get user adoption report file getUserAdoptionReportFile() finished." );
        return message;
    }


    /**
     * Controller to generate and send billing report by mail
     * @param model
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping ( value = "/downloadbillingreport")
    public String getBillingReportFile( Model model, HttpServletRequest request, HttpServletResponse response )
    {
        LOG.info( "Method to get billing report file getBillingReportFile() started." );
        String message = "";
        try {
            User user = sessionHelper.getCurrentUser();
            if ( !( user.isSuperAdmin() || userManagementService.isUserSocialSurveyAdmin( user.getUserId() ) ) ) {
                throw new UnsupportedOperationException( "User is not authorized to perform this action" );
            }
            //Check if a request already exists
            try {
                List<FileUpload> fileUpload = dashboardService.getActiveBillingReports();
                LOG.debug( "A request already exists for getting the billing report." );

                message = "A request already exists for getting the billing report.";
                if ( fileUpload != null && fileUpload.size() > 0 )
                    if ( fileUpload.get( 0 ) != null && fileUpload.get( 0 ).getFileName() != null
                        && !fileUpload.get( 0 ).getFileName().isEmpty() )
                        message += " Mail Address is : " + fileUpload.get( 0 ).getFileName();


            } catch ( NoRecordsFetchedException e ) {
                //Request doesn't already exist. Create one.
                LOG.error( "There is no existing request for getting the billing report" );

                //Get value from Mail ID column
                String mailId = request.getParameter( "mailid" );
                adminReport.createEntryInFileUploadForBillingReport( mailId );
                message = "The Billing Report will be mailed to you shortly";
            }

        } catch ( NonFatalException e ) {
            LOG.error( "NonfatalException caught in reportAbuse(). Nested exception is ", e );
            message = "Error while generating billing report request";
        }
        LOG.info( "Method to get billing report file getBillingReportFile() finished." );
        return message;
    }


    @ResponseBody
    @RequestMapping ( value = "/downloadcompanyregistrationreport")
    public String getCompanyRegistrationReport( HttpServletRequest request, HttpServletResponse response )
    {
        LOG.info( "Method called to generate the company registration report" );
        String message = "";
        try {
            User user = sessionHelper.getCurrentUser();
            if ( !( user.isSuperAdmin() || userManagementService.isUserSocialSurveyAdmin( user.getUserId() ) ) ) {
                throw new UnsupportedOperationException( "User is not authorized to perform this action" );
            }

            Date startDate = null;
            String startDateStr = request.getParameter( "startDate" );
            if ( startDateStr != null && !startDateStr.isEmpty() ) {
                try {
                    startDate = new SimpleDateFormat( CommonConstants.DATE_FORMAT ).parse( startDateStr );
                } catch ( ParseException e ) {
                	LOG.error("ParseException caught in getCompanyRegistrationReport() while parsing startDate. Nested exception is ",e);
                    throw new InvalidInputException(
                        "ParseException caught in getCompanyRegistrationReport() while parsing startDate. Nested exception is ",
                        e );
                }
            }

            Date endDate = Calendar.getInstance().getTime();
            String endDateStr = request.getParameter( "endDate" );
            if ( endDateStr != null && !endDateStr.isEmpty() ) {
                try {
                    endDate = new SimpleDateFormat( CommonConstants.DATE_FORMAT ).parse( endDateStr );
                } catch ( ParseException e ) {
                	LOG.error("ParseException caught in getCompanyRegistrationReport() while parsing startDate. Nested exception is ",e);
                    throw new InvalidInputException(
                        "ParseException caught in getCompanyRegistrationReport() while parsing startDate. Nested exception is ",
                        e );
                }
            }

            String mailId = request.getParameter( "mailid" );

            adminReport.createEntryInFileUploadForCompanyRegistrationReport( mailId, startDate, endDate );
            message = "The Company Registration Report will be mailed to you shortly";
        } catch ( NonFatalException e ) {
            LOG.error( "NonfatalException caught in getCompanyRegistrationReport(). Nested exception is ", e );
            message = "Error while generating company registration report request";
        }
        LOG.info( "Method to get company registration report file getCompanyRegistrationReport() finished." );
        return message;
    }


    @ResponseBody
    @RequestMapping ( value = "/downloadcompanyuserreport")
    public String getCompanyUsersReport( HttpServletRequest request )
    {
        LOG.info( "Method getCompanyUsersReport() started." );
        String status = CommonConstants.SUCCESS_ATTRIBUTE;
        try {
            User user = sessionHelper.getCurrentUser();
            if ( !( user.isSuperAdmin() || userManagementService.isUserSocialSurveyAdmin( user.getUserId() ) ) ) {
                throw new UnsupportedOperationException( "User is not authorized to perform this action" );
            }

            String mailId = request.getParameter( "mailid" );
            String companyIdStr = request.getParameter( "companyId" );
            long companyId;

            if ( companyIdStr == null || companyIdStr.isEmpty() ) {
                throw new InvalidInputException( "Passed parameter companyId is invalid" );
            }
            try {
                companyId = Long.parseLong( companyIdStr );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "NumberFormatException caught while parsing companyId in getCompanyUsersReport(). Nested exception is ",
                    e );
                throw e;
            }
            adminReport.createEntryInFileUploadForCompanyUserReport( mailId, companyId );

        } catch ( NonFatalException e ) {
            status = CommonConstants.ERROR;
            LOG.error( "Error while getting Company Users Report", e );
        }
        LOG.info( "Method getCompanyUsersReport() finished." );

        return status;
    }


    /**
     * Method to download hierarchy report for a company
     * */
    @ResponseBody
    @RequestMapping ( value = "/downloadcompanyhierarchyreport")
    public String getCompanyHierarchyReportFile( Model model, HttpServletRequest request, HttpServletResponse response )
    {
        LOG.info( "Method to get company hierarchy report file, getCompanyHierarchyReportFile() started." );
        String status = CommonConstants.SUCCESS_ATTRIBUTE;

        try {
            User user = sessionHelper.getCurrentUser();
            if ( !( user.isSuperAdmin() || userManagementService.isUserSocialSurveyAdmin( user.getUserId() ) ) ) {
                throw new UnsupportedOperationException( "User is not authorized to perform this action" );
            }

            String mailId = request.getParameter( "mailid" );
            String companyIdStr = request.getParameter( "companyId" );
            long companyId;

            if ( companyIdStr == null || companyIdStr.isEmpty() ) {
                throw new InvalidInputException( "Passed parameter companyId is invalid" );
            }
            try {
                companyId = Long.parseLong( companyIdStr );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "NumberFormatException caught while parsing companyId in getCompanyHierarchyReportFile(). Nested exception is ",
                    e );
                throw e;
            }
            adminReport.createEntryInFileUploadForCompanyHierarchyReport( mailId, companyId );

        } catch ( NonFatalException e ) {
            status = CommonConstants.ERROR;
            LOG.error( "Error while getting Company Users Report", e );
        }
        LOG.info( "Method to get company hierarchy report file, getCompanyHierarchyReportFile() ended." );
        return status;
    }
    
    private ErrorResponse getErrorResponse(String code, String message){
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrCode(code);
        errorResponse.setErrMessage(message);
        
        return errorResponse;
    }
        
    @ResponseBody
    @RequestMapping ( value = "/createreviewreply")
    public String createReviewReply( HttpServletRequest request ) throws InvalidInputException
    {
        Thread.currentThread().setName("Creating Reply");
        LOG.info( "Method to create reply to a review started" );
        
        HttpSession session = request.getSession();
        User user = sessionHelper.getCurrentUser();

        String replyByName = user.getFirstName();
        String replyById = Long.toString(user.getUserId());
        Response response = null;
        if(user.getLastName()!=null && !user.getLastName().isEmpty()) {
            replyByName = replyByName + " " + user.getLastName();
        }
        
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        String replyText = request.getParameter( "replyText" );
        String surveyId = request.getParameter( "surveyMongoId" );
        try {
        if ( surveyId == null || surveyId.isEmpty() ) {
            LOG.warn( "Invalid value (Null/Empty) found for surveyMongoId." );
            throw new InvalidInputException( "Invalid value (Null/Empty) found for surveyMongoId." );
        }
        
        Thread.currentThread().setName("Creating Reply surveyId:" + surveyId);

        response = sSApiIntergrationBuilder.getIntegrationApi().createReviewReply( surveyId, replyText, replyByName, replyById, entityType );
        } catch ( NonFatalException e ) {
            LOG.error( "NonfatalException caught in reviewsReply(). Nested exception is ", e );
            return CommonConstants.ERROR;
        }
        
        String responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        
        LOG.info( "Method to create reply to a review finished successfully" );
        return responseString;
    }
    
    @ResponseBody
    @RequestMapping ( value = "/updatereviewreply" )
    public String updateReviewReply( HttpServletRequest request ) throws InvalidInputException
    {
        Thread.currentThread().setName("Editing Reply");
        
        LOG.info( "Method to update reply to a review started" );
        
        User user = sessionHelper.getCurrentUser();
        String replyByName = user.getFirstName();
        String replyById = Long.toString(user.getUserId());
        Response response = null;
        if(user.getLastName()!=null && !user.getLastName().isEmpty()) {
            replyByName = replyByName + " " + user.getLastName();
        }
        String replyText = request.getParameter( "replyText" );
        String surveyId = request.getParameter( "surveyMongoId" );
        String replyId = request.getParameter("replyId");
        
        try {
        if ( surveyId == null || surveyId.isEmpty() ) {
            LOG.error( "Invalid value (Null/Empty) found for surveyMongoId." );
            throw new InvalidInputException( "Invalid value (Null/Empty) found for surveyMongoId." );
        }
        
        if(replyId == null || replyId.isEmpty()) {
            LOG.error( "Invalid value (Null/Empty) found for replyId." );
            throw new InvalidInputException( "Invalid value (Null/Empty) found for replyId." );
        }
        
        Thread.currentThread().setName("Creating Reply replyId:" + replyId);

        response = sSApiIntergrationBuilder.getIntegrationApi().updateReviewReply( surveyId, replyId, replyText, replyByName, replyById );
        } catch ( NonFatalException e ) {
            LOG.error( "NonfatalException caught in reviewsReply(). Nested exception is ", e );
            return CommonConstants.ERROR;
        }
        
        String responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        
        LOG.info( "Method to update reply to a review finished successfully" );
        return responseString;
    }
    
    
    @ResponseBody
    @RequestMapping ( value = "/deletereviewreply")
    public String deleteReviewReply( HttpServletRequest request ) throws InvalidInputException
    {
        Thread.currentThread().setName("Deleting Reply");
        
        LOG.info( "Method to delete reply to a review started" );
        
        User user = sessionHelper.getCurrentUser();
        String replyById = Long.toString(user.getUserId());
        
        String replyId = request.getParameter("replyId");
        String surveyId = request.getParameter( "surveyMongoId" );
        try {
        if ( surveyId == null || surveyId.isEmpty() ) {
            LOG.error( "Invalid value (Null/Empty) found for surveyMongoId." );
            throw new InvalidInputException( "Invalid value (Null/Empty) found for surveyMongoId." );
        }
        
        if(replyId == null || replyId.isEmpty()) {
            LOG.error( "Invalid value (Null/Empty) found for replyId." );
            throw new InvalidInputException( "Invalid value (Null/Empty) found for replyId." );
        }
        
        Thread.currentThread().setName("Deleting Reply replyId:" + replyId);

        sSApiIntergrationBuilder.getIntegrationApi().deleteReviewReply( surveyId, replyId );
        } catch ( NonFatalException e ) {
            LOG.error( "NonfatalException caught in reviewsReply(). Nested exception is ", e );
            return CommonConstants.ERROR;
        }
        
        LOG.info( "Method to delete reply to a review finished successfully" );
        return CommonConstants.SUCCESS_ATTRIBUTE;
    }

}
// JIRA SS-137 : by RM-05 : EOC
