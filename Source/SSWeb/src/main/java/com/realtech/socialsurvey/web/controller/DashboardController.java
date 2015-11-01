package com.realtech.socialsurvey.web.controller;

// JIRA SS-137 : by RM-05 : BOC
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfileStage;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyRecipient;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.batchTracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.organizationmanagement.DashboardService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.SurveyPreInitiationService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.services.surveybuilder.impl.DuplicateSurveyRequestException;
import com.realtech.socialsurvey.core.services.surveybuilder.impl.SelfSurveyInitiationException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;


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
    private EmailFormatHelper emailFormatHelper;
    
    @Autowired
    BatchTrackerService batchTrackerService;

    @Value ( "${ENABLE_KAFKA}")
    private String enableKafka;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String applicationAdminEmail;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String applicationAdminName;

    @Value ( "${APPLICATION_LOGO_URL}")
    private String appLogoUrl;

    @Value ( "${APPLICATION_BASE_URL}")
    private String appBaseUrl;

    private final String EXCEL_FORMAT = "application/vnd.ms-excel";
    private final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    private final String EXCEL_FILE_EXTENSION = ".xlsx";

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
            throw new NonFatalException( "NonFatalException while logging in. " );
        }

        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );

        boolean modelSet = false;
        if ( user.getCompany() != null && user.getCompany().getLicenseDetails() != null
            && !user.getCompany().getLicenseDetails().isEmpty()
            && user.getCompany().getLicenseDetails().get( 0 ).getAccountsMaster() != null ) {
            if ( user.getCompany().getLicenseDetails().get( 0 ).getAccountsMaster().getAccountsMasterId() == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                model.addAttribute( "columnName", CommonConstants.AGENT_ID_COLUMN );
                model.addAttribute( "columnValue", entityId );
                modelSet = true;
            }
        }

        if ( !modelSet ) {
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                model.addAttribute( "columnName", entityType );
                model.addAttribute( "columnValue", entityId );
                model.addAttribute( "showSendSurveyPopupAdmin", String.valueOf( true ) );
            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                model.addAttribute( "columnName", entityType );
                model.addAttribute( "columnValue", entityId );
                model.addAttribute( "showSendSurveyPopupAdmin", String.valueOf( true ) );
            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                model.addAttribute( "columnName", entityType );
                model.addAttribute( "columnValue", entityId );
                model.addAttribute( "showSendSurveyPopupAdmin", String.valueOf( true ) );
            } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                model.addAttribute( "columnName", CommonConstants.AGENT_ID_COLUMN );
                model.addAttribute( "columnValue", entityId );
            }
        }

        model.addAttribute( "userId", user.getUserId() );
        model.addAttribute( "emailId", user.getEmailId() );

        return JspResolver.DASHBOARD;
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
            } else if ( columnName.equalsIgnoreCase( CommonConstants.REGION_ID_COLUMN ) ) {

                try {
                    columnValue = Long.parseLong( request.getParameter( "columnValue" ) );
                } catch ( NumberFormatException e ) {
                    LOG.error( "NumberFormatException caught in getProfileDetails() while converting columnValue for regionId/branchId/agentId." );
                    throw e;
                }

                unitSettings = organizationManagementService.getRegionSettings( columnValue );
                if ( unitSettings.getContact_details() != null && unitSettings.getContact_details().getName() != null ) {
                    model.addAttribute( "name", unitSettings.getContact_details().getName() );
                }
                model.addAttribute( "title", unitSettings.getContact_details().getTitle() );
                model.addAttribute( "company", user.getCompany().getCompany() );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.BRANCH_ID_COLUMN ) ) {

                try {
                    columnValue = Long.parseLong( request.getParameter( "columnValue" ) );
                } catch ( NumberFormatException e ) {
                    LOG.error( "NumberFormatException caught in getProfileDetails() while converting columnValue for regionId/branchId/agentId." );
                    throw e;
                }

                unitSettings = organizationManagementService.getBranchSettingsDefault( columnValue );
                if ( unitSettings.getContact_details() != null && unitSettings.getContact_details().getName() != null ) {
                    model.addAttribute( "name", unitSettings.getContact_details().getName() );
                }
                model.addAttribute( "title", unitSettings.getContact_details().getTitle() );
                model.addAttribute( "company", user.getCompany().getCompany() );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) ) {
                columnValue = user.getUserId();

                unitSettings = userManagementService.getUserSettings( columnValue );
                model
                    .addAttribute( "name", user.getFirstName() + " " + ( user.getLastName() != null ? user.getLastName() : "" ) );
                model.addAttribute( "title", unitSettings.getContact_details().getTitle() );
                model.addAttribute( "company", user.getCompany().getCompany() );
            } else if ( realtechAdminStr != null && !realtechAdminStr.isEmpty() ) {
                realtechAdmin = Boolean.parseBoolean( realtechAdminStr );
            }
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "NonFatalException while fetching profile details. Reason :" + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( DisplayMessageConstants.GENERAL_ERROR, DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }

        // calculating details for circles
        int numberOfDays = 30;
        try {
            if ( request.getParameter( "numberOfDays" ) != null ) {
                numberOfDays = Integer.parseInt( request.getParameter( "numberOfDays" ) );
            }
        } catch ( NumberFormatException e ) {
            LOG.error( "NumberFormatException caught in getProfileDetails() while converting numberOfDays." );
            throw e;
        }

        if ( realtechAdmin ){
            columnName = null;
        }
        LOG.debug("Getting the survey score.");
        double surveyScore = (double) Math.round( dashboardService.getSurveyScore( columnName, columnValue, numberOfDays,
            realtechAdmin ) * 1000.0 ) / 1000.0;
        LOG.debug("Getting the sent surveys count.");
        int sentSurveyCount = (int) dashboardService.getAllSurveyCountForPastNdays( columnName, columnValue, numberOfDays );
        LOG.debug("Getting the social posts count with hierarchy.");
        int socialPostsCount = (int) dashboardService.getSocialPostsForPastNdaysWithHierarchy( columnName, columnValue,
            numberOfDays );
        LOG.debug("Getting the social posts count.");
        socialPostsCount += (int) dashboardService.getSocialPostsForPastNdays( columnName, columnValue, numberOfDays );
        int profileCompleteness = 0;
        if ( !realtechAdmin ){
        	LOG.debug("Getting profile completeness.");
            profileCompleteness = dashboardService.getProfileCompletionPercentage( user, columnName, columnValue, unitSettings );
        }
        model.addAttribute( "socialScore", surveyScore );
        if ( sentSurveyCount > 999 )
            model.addAttribute( "surveyCount", "1K+" );
        else
            model.addAttribute( "surveyCount", sentSurveyCount );

        if ( socialPostsCount > 999 )
            model.addAttribute( "socialPosts", "1K+" );
        else
            model.addAttribute( "socialPosts", socialPostsCount );

        model.addAttribute( "profileCompleteness", profileCompleteness );
        LOG.debug("Getting the badges.");
        model.addAttribute( "badges",
            dashboardService.getBadges( surveyScore, sentSurveyCount, socialPostsCount, profileCompleteness ) );

        model.addAttribute( "columnName", columnName );
        model.addAttribute( "columnValue", columnValue );

        LOG.info( "Method to get profile of company/region/branch/agent getProfileDetails() finished" );
        return JspResolver.DASHBOARD_PROFILEDETAIL;
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
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );

        try {
            String columnValueStr = request.getParameter( "columnValue" );
            columnValue = Long.parseLong( columnValueStr );
        } catch ( NumberFormatException e ) {
            LOG.error( "NumberFormatException caught in getSurveyCount() while converting columnValue for regionId/branchId/agentId." );
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
            LOG.error( "NumberFormatException caught in getSurveyCount() while converting numberOfDays." );
            throw e;
        }

        model.addAttribute( "allSurveySent",
            dashboardService.getAllSurveyCountForPastNdays( columnName, columnValue, numberOfDays ) );
        model.addAttribute( "completedSurvey",
            dashboardService.getCompletedSurveyCountForPastNdays( columnName, columnValue, numberOfDays ) );
        model.addAttribute( "clickedSurvey",
            dashboardService.getClickedSurveyCountForPastNdays( columnName, columnValue, numberOfDays ) );
        model.addAttribute( "socialPosts", dashboardService.getSocialPostsForPastNdays( columnName, columnValue, numberOfDays )
            + dashboardService.getSocialPostsForPastNdaysWithHierarchy( entityType, entityId, numberOfDays ) );

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
                LOG.error( "Null/Empty value found for field columnName." );
                throw new NonFatalException( "Null/Empty value found for field columnName." );
            }

            long columnValue = 0;
            try {
                String columnValueStr = request.getParameter( "columnValue" );
                columnValue = Long.parseLong( columnValueStr );
            } catch ( NumberFormatException e ) {
                LOG.error( "NumberFormatException in getSurveyCountForCompany() while converting columnValue for regionId/branchId/agentId." );
                throw e;
            }

            int numberOfDays = -1;
            try {
                if ( request.getParameter( "numberOfDays" ) != null ) {
                    numberOfDays = Integer.parseInt( request.getParameter( "numberOfDays" ) );
                }
            } catch ( NumberFormatException e ) {
                LOG.error( "NumberFormatException caught in getSurveyCount() while converting numberOfDays." );
                throw e;
            }

            if ( !realtechAdmin && columnName.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) ) {
                columnValue = user.getCompany().getCompanyId();
            }
            LOG.info( "Method to get details for generating graph, getGraphDetailsForWeek() finished." );

            try {
                return new Gson().toJson( dashboardService.getSurveyDetailsForGraph( columnName, columnValue, numberOfDays,
                    realtechAdmin ) );
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
                if ( columnValue != null && !columnValue.isEmpty() ) {
                    try {
                        iden = Long.parseLong( columnValue );
                    } catch ( NumberFormatException e ) {
                        LOG.error(
                            "NumberFormatException caught while parsing columnValue in getReviews(). Nested exception is ", e );
                        throw e;
                    }
                }
            }

            try {
                surveyDetails = profileManagementService.getReviews( iden, -1, -1, startIndex, batchSize, profileLevel, false,
                    null, null, "date" );
                profileManagementService.setAgentProfileUrlForReview( surveyDetails );
            } catch ( InvalidInputException e ) {
                LOG.error( "InvalidInputException caught in getReviews() while fetching reviews. Nested exception is ", e );
                throw e;
            }
            model.addAttribute( "reviews", surveyDetails );
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
                    throw new InvalidInputException( "Null or empty value passed for Region/BranchId. Please pass valid value." );
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
                LOG.error( "Invalid value (null/empty) passed for profile level." );
                throw new InvalidInputException( "Invalid value (null/empty) passed for profile level." );
            }

            long id = 0;
            if ( columnName.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                return new Gson().toJson( user.getCompany().getCompany() );
            } else if ( columnName.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                return new Gson().toJson( user.getFirstName() + " " + user.getLastName() );
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

        try {
            surveyDetails = fetchIncompleteSurveys( request, user, realtechAdmin );
            for ( SurveyPreInitiation surveyPreInitiation : surveyDetails ) {
                if ( surveyPreInitiation.getAgentId() > 0 ) {
                    surveyPreInitiation.setAgentEmailId( surveyPreInitiation.getUser().getEmailId() );
                    surveyPreInitiation.setAgentName( surveyPreInitiation.getUser().getFirstName() + " "
                        + surveyPreInitiation.getUser().getLastName() );
                }
            }

            model.addAttribute( "incompleteSurveys", surveyDetails );

        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in getReviews() while fetching reviews. Nested exception is ", e );
            model.addAttribute( "message", e.getMessage() );
        }

        LOG.info( "Method to get reviews of company, region, branch, agent getReviews() finished." );
        return JspResolver.HEADER_DASHBOARD_INCOMPLETESURVEYS;
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
                    throw new NonFatalException( "Number format exception occured while parsing incomplet survey id : "
                        + incompleteSurveyIdStr, e );
                }
            }
            surveyPreInitiationService.deleteSurveyReminder( incompleteSurveyIds );
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "Nonfatal exception occured in method cancelSurveyReminder, reason : " + nonFatalException.getMessage() );
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
        LOG.info( "Method to get reviews of company, region, branch, agent getReviews() started." );
        List<SurveyPreInitiation> surveyDetails;
        User user = sessionHelper.getCurrentUser();
        String realtechAdminStr = request.getParameter( "realtechAdmin" );
        boolean realtechAdmin = false;
        realtechAdmin = Boolean.parseBoolean( realtechAdminStr );

        try {
            surveyDetails = fetchIncompleteSurveys( request, user, realtechAdmin );
        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in getReviews() while fetching reviews. Nested exception is ", e );
            return e.getMessage();
        }

        LOG.info( "Method to get reviews of company, region, branch, agent getReviews() finished." );
        return String.valueOf( surveyDetails.size() );
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
                LOG.error( "Invalid value (null/empty) passed for profile level." );
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
                    LOG.error( "Null or empty value passed for Region/BranchId. Please pass valid value." );
                    throw new InvalidInputException( "Null or empty value passed for Region/BranchId. Please pass valid value." );
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
            solrDocuments = solrSearchService.searchBranchRegionOrAgentByName( CommonConstants.USER_DISPLAY_NAME_SOLR,
                searchKey, idenFieldName, iden );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatal exception caught in fetchAgentsForAdmin(). Nested exception is ", e );
            return "";
        }

        LOG.info( "Method fetchAgentsForAdmin() finished." );
        return new Gson().toJson( solrDocuments );
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
                throw new InvalidInputException( "Invalid batch size passed" );
            }
        }

        String columnName = request.getParameter( "columnName" );
        if ( !realtechAdmin && ( columnName == null || columnName.isEmpty() ) ) {
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
            surveyDetails = profileManagementService.getIncompleteSurvey( iden, 0, 0, startIndex, batchSize, profileLevel,
                null, null, realtechAdmin );
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
        boolean isRealTechAdmin = user.isSuperAdmin();

        try {
            String searchColumn = request.getParameter( "searchColumn" );
            if ( searchColumn == null || searchColumn.isEmpty() ) {
                LOG.error( "Invalid value (null/empty) passed for search criteria." );
                throw new InvalidInputException( "Invalid value (null/empty) passed for search criteria." );
            }
            model.addAttribute( "searchColumn", searchColumn );

            String columnName = request.getParameter( "columnName" );
            String columnValueStr = request.getParameter( "columnValue" );

            if ( !isRealTechAdmin ) {
                if ( columnName == null || columnName.isEmpty() ) {
                    LOG.error( "Invalid value (null/empty) passed for profile level." );
                    throw new InvalidInputException( "Invalid value (null/empty) passed for profile level." );
                }

                if ( columnValueStr == null || columnValueStr.isEmpty() ) {
                    LOG.error( "Invalid value (null/empty) passed for Region/branch Id." );
                    throw new InvalidInputException( "Invalid value (null/empty) passed for Region/branch Id." );
                }
            }

            String searchKey = request.getParameter( "searchKey" );
            if ( searchKey == null ) {
                searchKey = "";
            }

            if ( isRealTechAdmin ) {
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
                    result = solrSearchService.searchBranchRegionOrAgentByName( searchColumn, searchKey, columnName, user
                        .getCompany().getCompanyId() );
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

        try {
            String surveyPreInitiationIdStr = request.getParameter( "surveyPreInitiationId" );

            if ( surveyPreInitiationIdStr == null || surveyPreInitiationIdStr.isEmpty() ) {
                LOG.error( "Invalid value (null/empty) passed for surveyPreInitiationIdStr." );
                throw new InvalidInputException( "Invalid value (null/empty) passed for surveyPreInitiationIdStr." );
            }
            Map<String, Long> hierarchyMap = null;
            Map<SettingsForApplication, OrganizationUnit> map = null;
            String logoUrl = null;
            String surveyLink = "";
            long surveyPreInitiationId;
            try {
                surveyPreInitiationId = Integer.parseInt( surveyPreInitiationIdStr );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "Invalid surveyPreInitiationIdStr passed", e.getMessage(), e );
            }
            SurveyPreInitiation survey = surveyHandler.getPreInitiatedSurveyById( surveyPreInitiationId );
            long agentId = survey.getAgentId();
            String customerEmail = survey.getCustomerEmailId();
            String custFirstName = survey.getCustomerFirstName();
            String custLastName = survey.getCustomerLastName();
            if ( survey != null ) {
                surveyLink = surveyHandler.getSurveyUrl( agentId, customerEmail,
                    surveyHandler.composeLink( agentId, customerEmail, custFirstName, custLastName ) );
            }

            try {
                AgentSettings agentSettings = userManagementService.getUserSettings( agentId );
                String agentTitle = "";
                if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getTitle() != null ) {
                    agentTitle = agentSettings.getContact_details().getTitle();
                }

                String agentPhone = "";
                if ( agentSettings.getContact_details() != null
                    && agentSettings.getContact_details().getContact_numbers() != null
                    && agentSettings.getContact_details().getContact_numbers().getWork() != null ) {
                    agentPhone = agentSettings.getContact_details().getContact_numbers().getWork();
                }

                String agentEmailId = "";
                if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getMail_ids() != null
                    && agentSettings.getContact_details().getMail_ids().getWork() != null ) {
                    agentEmailId = agentSettings.getContact_details().getMail_ids().getWork();
                }

                hierarchyMap = profileManagementService.getPrimaryHierarchyByAgentProfile( agentSettings );

                long companyId = hierarchyMap.get( CommonConstants.COMPANY_ID_COLUMN );
                long regionId = hierarchyMap.get( CommonConstants.REGION_ID_COLUMN );
                long branchId = hierarchyMap.get( CommonConstants.BRANCH_ID_COLUMN );


                try {
                    try {
                        map = profileManagementService.getPrimaryHierarchyByEntity( CommonConstants.AGENT_ID_COLUMN,
                            agentSettings.getIden() );
                        if ( map == null ) {
                            LOG.error( "Unable to fetch primary profile for this user " );
                            throw new FatalException( "Unable to fetch primary profile this user " + agentSettings.getIden() );
                        }
                    } catch ( InvalidInputException e ) {
                        LOG.error( "Exception caught " + e.getMessage() );
                    }
                } catch ( InvalidSettingsStateException e ) {
                    LOG.error( "Exception caught " + e.getMessage() );
                }


                User user = userManagementService.getUserByUserId( agentId );
                String companyName = user.getCompany().getCompany();
                String agentName = "";

                if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getName() != null ) {
                    agentName = agentSettings.getContact_details().getName();
                }

                if ( enableKafka.equals( CommonConstants.YES ) ) {
                    emailServices.queueSurveyReminderMail( customerEmail, custFirstName, agentName, surveyLink, agentPhone,
                        agentTitle, companyName );
                } else {
                    // TODO: add call to emailservice method.
                    OrganizationUnitSettings companySettings = null;
                    try {
                        companySettings = organizationManagementService.getCompanySettings( user.getCompany().getCompanyId() );
                    } catch ( InvalidInputException e ) {
                        LOG.error( "InvalidInputException occured while trying to fetch company settings." );
                    }

                    OrganizationUnit organizationUnit = map.get( SettingsForApplication.LOGO );
                    if ( organizationUnit == OrganizationUnit.COMPANY ) {
                        logoUrl = companySettings.getLogo();
                    } else if ( organizationUnit == OrganizationUnit.REGION ) {
                        OrganizationUnitSettings regionSettings = null;
                        try {
                            regionSettings = organizationManagementService.getRegionSettings( regionId );
                        } catch ( InvalidInputException e ) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if ( regionSettings != null )
                            logoUrl = regionSettings.getLogo();
                    } else if ( organizationUnit == OrganizationUnit.BRANCH ) {
                        OrganizationUnitSettings branchSettings = null;
                        try {
                            branchSettings = organizationManagementService.getBranchSettingsDefault( branchId );
                        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if ( branchSettings != null ) {
                            logoUrl = branchSettings.getLogo();
                        }
                    } else if ( organizationUnit == OrganizationUnit.AGENT ) {
                        logoUrl = agentSettings.getLogo();
                    }

                    emailServices.sendManualSurveyReminderMail( companySettings, user, agentName, agentEmailId, agentPhone,
                        agentTitle, companyName, survey, surveyLink, logoUrl );
                }
            } catch ( InvalidInputException e ) {
                LOG.error( "Exception occurred while trying to send survey reminder mail to : " + customerEmail );
                throw e;
            }

            // Increasing value of reminder count by 1.
            if ( survey != null ) {
                surveyHandler.updateReminderCount( survey.getSurveyPreIntitiationId(), true );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException caught in sendReminderMailForSurvey() while sending mail. Nested exception is ", e );
        }

        LOG.info( "Method to send email to remind customer for survey sendReminderMailForSurvey() finished." );
        return new Gson().toJson( "success" );
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
        try {
            for ( String incompleteSurveyIdStr : surveysSelectedArray ) {
                try {
                    long incompleteSurveyId = Long.parseLong( incompleteSurveyIdStr );
                    SurveyPreInitiation survey = surveyHandler.getPreInitiatedSurvey( incompleteSurveyId );
                    Map<String, Long> hierarchyMap = null;
                    Map<SettingsForApplication, OrganizationUnit> map = null;
                    String logoUrl = null;
                    long agentId = survey.getAgentId();
                    String customerEmail = survey.getCustomerEmailId();
                    String custFirstName = survey.getCustomerFirstName();
                    String custLastName = "";
                    if ( survey.getCustomerLastName() != null ) {
                        custLastName = survey.getCustomerLastName();
                    }

                    String surveyLink = "";
                    if ( survey != null ) {
                        surveyLink = surveyHandler.getSurveyUrl( agentId, customerEmail,
                            surveyHandler.composeLink( agentId, customerEmail, custFirstName, custLastName ) );
                    }

                    AgentSettings agentSettings = userManagementService.getUserSettings( agentId );
                    String agentTitle = "";
                    if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getTitle() != null ) {
                        agentTitle = agentSettings.getContact_details().getTitle();
                    }

                    String agentName = "";
                    if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getName() != null ) {
                        agentName = agentSettings.getContact_details().getName();
                    }
                    String agentEmailId = "";
                    if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getMail_ids() != null
                        && agentSettings.getContact_details().getMail_ids().getWork() != null ) {
                        agentEmailId = agentSettings.getContact_details().getMail_ids().getWork();
                    }

                    String agentPhone = "";
                    if ( agentSettings.getContact_details() != null
                        && agentSettings.getContact_details().getContact_numbers() != null
                        && agentSettings.getContact_details().getContact_numbers().getWork() != null ) {
                        agentPhone = agentSettings.getContact_details().getContact_numbers().getWork();
                    }

                    hierarchyMap = profileManagementService.getPrimaryHierarchyByAgentProfile( agentSettings );
                    long companyId = hierarchyMap.get( CommonConstants.COMPANY_ID_COLUMN );
                    long regionId = hierarchyMap.get( CommonConstants.REGION_ID_COLUMN );
                    long branchId = hierarchyMap.get( CommonConstants.BRANCH_ID_COLUMN );
                    try {
                        try {
                            map = profileManagementService.getPrimaryHierarchyByEntity( CommonConstants.AGENT_ID_COLUMN,
                                agentSettings.getIden() );
                            if ( map == null ) {
                                LOG.error( "Unable to fetch primary profile for this user " );
                                throw new FatalException( "Unable to fetch primary profile this user "
                                    + agentSettings.getIden() );
                            }
                        } catch ( InvalidInputException e ) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } catch ( InvalidSettingsStateException e ) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    User user = userManagementService.getUserByUserId( agentId );
                    String companyName = user.getCompany().getCompany();

                    if ( enableKafka.equals( CommonConstants.YES ) ) {
                        emailServices.queueSurveyReminderMail( customerEmail, custFirstName, agentName, surveyLink, agentPhone,
                            agentTitle, companyName );
                    } else {
                        OrganizationUnitSettings companySettings = null;
                        try {
                            companySettings = organizationManagementService.getCompanySettings( user.getCompany()
                                .getCompanyId() );
                        } catch ( InvalidInputException e ) {
                            LOG.error( "InvalidInputException occured while trying to fetch company settings." );
                        }

                        OrganizationUnit organizationUnit = map.get( SettingsForApplication.LOGO );
                        if ( organizationUnit == OrganizationUnit.COMPANY ) {
                            logoUrl = companySettings.getLogo();
                        } else if ( organizationUnit == OrganizationUnit.REGION ) {
                            OrganizationUnitSettings regionSettings = null;
                            try {
                                regionSettings = organizationManagementService.getRegionSettings( regionId );
                            } catch ( InvalidInputException e ) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            if ( regionSettings != null )
                                logoUrl = regionSettings.getLogo();
                        } else if ( organizationUnit == OrganizationUnit.BRANCH ) {
                            OrganizationUnitSettings branchSettings = null;
                            try {
                                branchSettings = organizationManagementService.getBranchSettingsDefault( branchId );
                            } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            if ( branchSettings != null ) {
                                logoUrl = branchSettings.getLogo();
                            }
                        } else if ( organizationUnit == OrganizationUnit.AGENT ) {
                            logoUrl = agentSettings.getLogo();
                        }

                        emailServices.sendManualSurveyReminderMail( companySettings, user, agentName, agentEmailId, agentPhone,
                            agentTitle, companyName, survey, surveyLink, logoUrl );
                    }

                    surveyHandler.updateReminderCount( survey.getSurveyPreIntitiationId(), true );
                } catch ( NumberFormatException e ) {
                    throw new NonFatalException( "Number format exception occured while parsing incomplete survey id : "
                        + incompleteSurveyIdStr, e );
                }
            }
        } catch ( NonFatalException nonFatalException ) {
            LOG.error( "Nonfatal exception occured in method sendMultipleSurveyReminders, reason : "
                + nonFatalException.getMessage() );
            return CommonConstants.ERROR;
        }
        return CommonConstants.SUCCESS_ATTRIBUTE;
    }


    /*
     * Method to download file containing incomplete surveys
     */
    @RequestMapping ( value = "/downloaddashboardincompletesurvey")
    public void getIncompleteSurveyFile( Model model, HttpServletRequest request, HttpServletResponse response )
    {
        LOG.info( "Method to get file containg incomplete surveys list getIncompleteSurveyFile() started." );
        List<SurveyPreInitiation> surveyDetails = new ArrayList<>();

        try {
            String realtechAdminStr = request.getParameter( "realtechAdmin" );
            boolean realtechAdmin = Boolean.parseBoolean( realtechAdminStr );

            String columnName = request.getParameter( "columnName" );
            if ( columnName == null || columnName.isEmpty() ) {
                LOG.error( "Invalid value (null/empty) passed for profile level." );
                throw new InvalidInputException( "Invalid value (null/empty) passed for profile level." );
            }

            Date startDate = null;
            String startDateStr = request.getParameter( "startDate" );
            if ( startDateStr != null ) {
                try {
                    startDate = new SimpleDateFormat( CommonConstants.DATE_FORMAT ).parse( startDateStr );
                } catch ( ParseException e ) {
                    LOG.error(
                        "ParseException caught in getCompleteSurveyFile() while parsing startDate. Nested exception is ", e );
                }
            }

            Date endDate = Calendar.getInstance().getTime();
            String endDateStr = request.getParameter( "endDate" );
            if ( endDateStr != null ) {
                try {
                    endDate = new SimpleDateFormat( CommonConstants.DATE_FORMAT ).parse( endDateStr );
                } catch ( ParseException e ) {
                    LOG.error(
                        "ParseException caught in getCompleteSurveyFile() while parsing startDate. Nested exception is ", e );
                }
            }

            String profileLevel = getProfileLevel( columnName );
            if ( realtechAdmin ) {
                profileLevel = CommonConstants.PROFILE_LEVEL_REALTECH_ADMIN;
            }
            long iden = 0;

            User user = sessionHelper.getCurrentUser();
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

            try {
                Date date = new Date();
                surveyDetails = profileManagementService.getIncompleteSurvey( iden, 0, 0, -1, -1, profileLevel, startDate,
                    endDate, realtechAdmin );
                String fileName = "Incomplete_Survey_" + profileLevel + "-" + user.getFirstName() + "_" + user.getLastName()
                    + "-" + ( new Timestamp( date.getTime() ) ) + EXCEL_FILE_EXTENSION;
                XSSFWorkbook workbook = dashboardService.downloadIncompleteSurveyData( surveyDetails, fileName );
                response.setContentType( EXCEL_FORMAT );
                String headerKey = CONTENT_DISPOSITION_HEADER;
                String headerValue = String.format( "attachment; filename=\"%s\"", new File( fileName ).getName() );
                response.setHeader( headerKey, headerValue );

                // write into file
                OutputStream responseStream = null;
                try {
                    responseStream = response.getOutputStream();
                    workbook.write( responseStream );
                } catch ( IOException e ) {
                    LOG.error( "IOException caught in getIncompleteSurveyFile(). Nested exception is ", e );
                } finally {
                    try {
                        responseStream.close();
                    } catch ( IOException e ) {
                        LOG.error( "IOException caught in getIncompleteSurveyFile(). Nested exception is ", e );
                    }
                }
                response.flushBuffer();
            } catch ( InvalidInputException e ) {
                LOG.error(
                    "InvalidInputException caught in getIncompleteSurveyFile() while fetching incomplete reviews file. Nested exception is ",
                    e );
                throw e;
            } catch ( IOException e ) {
                LOG.error(
                    "IOException caught in getIncompleteSurveyFile() while fetching incomplete reviews file. Nested exception is ",
                    e );
            }
        } catch ( NonFatalException e ) {
            LOG.error(
                "Non fatal exception caught in getReviews() while fetching incomplete reviews file. Nested exception is ", e );
        }
        LOG.info( "Method to get file containg incomplete surveys list getIncompleteSurveyFile() finished." );
    }


    /*
     * Method to download file containing incomplete surveys
     */
    @RequestMapping ( value = "/downloadcustomersurveyresults")
    public void getCustomerSurveyResultsFile( Model model, HttpServletRequest request, HttpServletResponse response )
    {
        LOG.info( "Method to get file containg customer survey results getCustomerSurveyResultsFile() started." );
        User user = sessionHelper.getCurrentUser();
        boolean realTechAdmin = user.isSuperAdmin();
        boolean fetchAbusive = false;
        List<SurveyDetails> surveyDetails = new ArrayList<>();

        try {
            String columnName = request.getParameter( "columnName" );
            if ( !realTechAdmin && ( columnName == null || columnName.isEmpty() ) ) {
                LOG.error( "Invalid value (null/empty) passed for profile level." );
                throw new InvalidInputException( "Invalid value (null/empty) passed for profile level." );
            }

            Date startDate = null;
            String startDateStr = request.getParameter( "startDate" );
            if ( startDateStr != null && !startDateStr.isEmpty() ) {
                try {
                    startDate = new SimpleDateFormat( CommonConstants.DATE_FORMAT ).parse( startDateStr );
                } catch ( ParseException e ) {
                    LOG.error(
                        "ParseException caught in getCustomerSurveyResultsFile() while parsing startDate. Nested exception is ",
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
                        "ParseException caught in getCustomerSurveyResultsFile() while parsing startDate. Nested exception is ",
                        e );
                }
            }

            String profileLevel = getProfileLevel( columnName );
            long iden = 0;

            if ( realTechAdmin ) {
                String columnValue = request.getParameter( "columnValue" );
                if ( columnValue != null && !columnValue.isEmpty() ) {
                    try {
                        iden = Long.parseLong( columnValue );
                    } catch ( NumberFormatException e ) {
                        LOG.error(
                            "NumberFormatException caught while parsing columnValue in getCustomerSurveyResultsFile(). Nested exception is ",
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
                            "NumberFormatException caught while parsing columnValue in getCustomerSurveyResultsFile(). Nested exception is ",
                            e );
                        throw e;
                    }
                }
            }

            try {
                Date date = new Date();
                surveyDetails = profileManagementService.getReviews( iden, -1, -1, -1, -1, profileLevel, fetchAbusive,
                    startDate, endDate, null );
                String fileName = "Survey_Results-" + profileLevel + "-" + user.getFirstName() + "_" + user.getLastName() + "-"
                    + ( new Timestamp( date.getTime() ) ) + EXCEL_FILE_EXTENSION;
                XSSFWorkbook workbook = dashboardService.downloadCustomerSurveyResultsData( surveyDetails, fileName );
                response.setContentType( EXCEL_FORMAT );
                String headerKey = CONTENT_DISPOSITION_HEADER;
                String headerValue = String.format( "attachment; filename=\"%s\"", new File( fileName ).getName() );
                response.setHeader( headerKey, headerValue );

                // write into file
                OutputStream responseStream = null;
                try {
                    responseStream = response.getOutputStream();
                    workbook.write( responseStream );
                } catch ( IOException e ) {
                    LOG.error( "IOException caught in getCustomerSurveyResultsFile(). Nested exception is ", e );
                } finally {
                    try {
                        responseStream.close();
                    } catch ( IOException e ) {
                        LOG.error( "IOException caught in getCustomerSurveyResultsFile(). Nested exception is ", e );
                    }
                }
                response.flushBuffer();
            } catch ( InvalidInputException e ) {
                LOG.error( "InvalidInputException caught in getCustomerSurveyResultsFile(). Nested exception is ", e );
                throw e;
            } catch ( IOException e ) {
                LOG.error( "IOException caught in getCustomerSurveyResultsFile(). Nested exception is ", e );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in getCustomerSurveyResultsFile(). Nested exception is ", e );
        }
        LOG.info( "Method getCustomerSurveyResultsFile() finished." );
    }


    /*
     * Method to download file containing incomplete surveys
     */
    @RequestMapping ( value = "/downloaddashboardsocialmonitor")
    public void getSocialMonitorFile( Model model, HttpServletRequest request, HttpServletResponse response )
    {
        LOG.info( "Method to get file containg Social Monitors list getSocialMonitorFile() started." );
        User user = sessionHelper.getCurrentUser();
        boolean realTechAdmin = user.isSuperAdmin();
        List<SocialPost> socialPosts = new ArrayList<>();

        try {
            String columnName = request.getParameter( "columnName" );
            if ( !realTechAdmin && ( columnName == null || columnName.isEmpty() ) ) {
                LOG.error( "Invalid value (null/empty) passed for profile level." );
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

            if ( realTechAdmin ) {
                profileLevel = CommonConstants.PROFILE_LEVEL_REALTECH_ADMIN;
            }

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
                            "NumberFormatExcept;ion caught while parsing columnValue in getSocialMonitorFile(). Nested exception is ",
                            e );
                        throw e;
                    }
                }
            }

            try {
                Date date = new Date();
                socialPosts = profileManagementService.getCumulativeSocialPosts( iden, columnName, -1, -1, profileLevel,
                    startDate, endDate );
                String fileName = "Social_Monitor-" + profileLevel + "-" + user.getFirstName() + "_" + user.getLastName() + "-"
                    + ( new Timestamp( date.getTime() ) ) + EXCEL_FILE_EXTENSION;
                XSSFWorkbook workbook = dashboardService.downloadSocialMonitorData( socialPosts, fileName );
                response.setContentType( EXCEL_FORMAT );
                String headerKey = CONTENT_DISPOSITION_HEADER;
                String headerValue = String.format( "attachment; filename=\"%s\"", new File( fileName ).getName() );
                response.setHeader( headerKey, headerValue );

                // write into file
                OutputStream responseStream = null;
                try {
                    responseStream = response.getOutputStream();
                    workbook.write( responseStream );
                } catch ( IOException e ) {
                    LOG.error( "IOException caught in getSocialMonitorFile(). Nested exception is ", e );
                } finally {
                    try {
                        responseStream.close();
                    } catch ( IOException e ) {
                        LOG.error( "IOException caught in getSocialMonitorFile(). Nested exception is ", e );
                    }
                }
                response.flushBuffer();
            } catch ( InvalidInputException e ) {
                LOG.error( "InvalidInputException caught in getSocialMonitorFile(). Nested exception is ", e );
                throw e;
            } catch ( IOException e ) {
                LOG.error( "IOException caught in getSocialMonitorFile(). Nested exception is ", e );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in getSocialMonitorFile(). Nested exception is ", e );
        }
        LOG.info( "Method getSocialMonitorFile() finished." );
    }

  
    /*
     * Method to download file containing incomplete surveys
     */
    @RequestMapping ( value = "/downloadagentrankingreport")
    public void getAgentRankingFile( Model model, HttpServletRequest request, HttpServletResponse response )
    {
        LOG.info( "Method to get file containg Agent's data getAgentSurveyFile() started." );
        User user = sessionHelper.getCurrentUser();
        List<AgentRankingReport> agentRanking = new ArrayList<>();
        boolean realtechAdmin = user.isSuperAdmin();

        try {
            String columnName = request.getParameter( "columnName" );
            if ( !realtechAdmin && ( columnName == null || columnName.isEmpty() ) ) {
                LOG.error( "Invalid value (null/empty) passed for profile level." );
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
                    LOG.error( "ParseException caught in getAgentSurveyFile() while parsing startDate. Nested exception is ", e );
                }
            }
            if ( endDateStr != null && !endDateStr.isEmpty() ) {
                try {
                    endDate = new SimpleDateFormat( CommonConstants.DATE_FORMAT ).parse( endDateStr );
                } catch ( ParseException e ) {
                    LOG.error( "ParseException caught in getAgentSurveyFile() while parsing startDate. Nested exception is ", e );
                }
            }

            String profileLevel = getProfileLevel( columnName );
            long iden = 0;

            if ( realtechAdmin ) {
                profileLevel = CommonConstants.PROFILE_LEVEL_REALTECH_ADMIN;
            }
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

            try {
                Date date = new Date();
                agentRanking = profileManagementService.getAgentReport( iden, columnName, startDate, endDate, null );
                String fileName = "User_Ranking_Report-" + profileLevel + "-" + user.getFirstName() + "_" + user.getLastName()
                    + "-" + ( new Timestamp( date.getTime() ) ) + EXCEL_FILE_EXTENSION;
                XSSFWorkbook workbook = dashboardService.downloadAgentRankingData( agentRanking, fileName );
                response.setContentType( EXCEL_FORMAT );
                String headerKey = CONTENT_DISPOSITION_HEADER;
                String headerValue = String.format( "attachment; filename=\"%s\"", new File( fileName ).getName() );
                response.setHeader( headerKey, headerValue );

                // write into file
                OutputStream responseStream = null;
                try {
                    responseStream = response.getOutputStream();
                    workbook.write( responseStream );
                } catch ( IOException e ) {
                    LOG.error( "IOException caught in getAgentSurveyFile(). Nested exception is ", e );
                } finally {
                    try {
                        responseStream.close();
                    } catch ( IOException e ) {
                        LOG.error( "IOException caught in getAgentSurveyFile(). Nested exception is ", e );
                    }
                }
                response.flushBuffer();
            } catch ( InvalidInputException e ) {
                LOG.error( "InvalidInputException caught in getAgentSurveyFile(). Nested exception is ", e );
                throw e;
            } catch ( IOException e ) {
                LOG.error( "IOException caught in getAgentSurveyFile(). Nested exception is ", e );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in getAgentSurveyFile(). Nested exception is ", e );
        }
        LOG.info( "Method to get file containg Agent's data getAgentSurveyFile() finished." );
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
        String custRelationWithAgent = request.getParameter( "relation" );
        User user = sessionHelper.getCurrentUser();
        String errorMsg = null;
        try {
            try {
                surveyHandler.initiateSurveyRequest( user.getUserId(), custEmail, custFirstName, custLastName, "customer" );
            } catch ( SelfSurveyInitiationException e ) {
                errorMsg = messageUtils.getDisplayMessage( DisplayMessageConstants.SELF_SURVEY_INITIATION,
                    DisplayMessageType.ERROR_MESSAGE ).getMessage();
                throw new NonFatalException( e.getMessage(), e.getErrorCode() );
            } catch ( DuplicateSurveyRequestException e ) {
                errorMsg = messageUtils.getDisplayMessage( DisplayMessageConstants.DUPLICATE_SURVEY_REQUEST,
                    DisplayMessageType.ERROR_MESSAGE ).getMessage();
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
    @RequestMapping ( value = "/sendmultiplesurveyinvites", method = RequestMethod.POST)
    public String sendMultipleSurveyInvitations( HttpServletRequest request )
    {
        LOG.info( "Method sendMultipleSurveyInvitations() called from DashboardController." );
        User user = sessionHelper.getCurrentUser();
        List<SurveyRecipient> surveyRecipients = null;
        long agentId = 0;
        String errorMsg = null;

        try {
            String source = request.getParameter( "source" );
            String payload = request.getParameter( "receiversList" );
            // String columnName = request.getParameter("columnName");
            try {
                if ( payload == null ) {
                    throw new InvalidInputException( "SurveyRecipients passed was null or empty" );
                }
                surveyRecipients = new ObjectMapper().readValue( payload, TypeFactory.defaultInstance()
                    .constructCollectionType( List.class, SurveyRecipient.class ) );
            } catch ( IOException ioException ) {
                throw new NonFatalException( "Error occurred while parsing the Json.", DisplayMessageConstants.GENERAL_ERROR,
                    ioException );
            }
            /*
             * if (columnName != null) { String profileLevel =
             * getProfileLevel(columnName); if
             * (profileLevel.equalsIgnoreCase(CommonConstants
             * .PROFILE_LEVEL_INDIVIDUAL)) { agentId = user.getUserId(); } }
             * else { agentId = user.getUserId(); }
             */
            if ( source.equalsIgnoreCase( CommonConstants.SURVEY_REQUEST_AGENT ) ) {
                agentId = user.getUserId();
            }

            //check if no duplicate entries in list
            if ( !surveyRecipients.isEmpty() ) {
                for ( int i = 0; i < surveyRecipients.size(); i++ ) {
                    for ( int j = i + 1; j < surveyRecipients.size(); j++ ) {
                        if ( surveyRecipients.get( i ).getEmailId().equalsIgnoreCase( surveyRecipients.get( j ).getEmailId() ) ) {
                            if ( surveyRecipients.get( i ).getAgentEmailId()
                                .equalsIgnoreCase( surveyRecipients.get( j ).getAgentEmailId() ) ) {
                                throw new InvalidInputException( "Can't enter same email address multiple times for same user" );
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
                        throw new InvalidInputException( "Agent id can not be null" );
                    }


                    try {
                        surveyHandler.initiateSurveyRequest( currentAgentId, recipient.getEmailId(), recipient.getFirstname(),
                            recipient.getLastname(), source );
                    } catch ( SelfSurveyInitiationException e ) {
                        errorMsg = messageUtils.getDisplayMessage( DisplayMessageConstants.SELF_SURVEY_INITIATION,
                            DisplayMessageType.ERROR_MESSAGE ).getMessage();
                        throw new NonFatalException( e.getMessage(), e.getErrorCode() );
                    } catch ( DuplicateSurveyRequestException e ) {
                        errorMsg = messageUtils.getDisplayMessage( DisplayMessageConstants.DUPLICATE_SURVEY_REQUEST,
                            DisplayMessageType.ERROR_MESSAGE ).getMessage();
                        throw new NonFatalException( e.getMessage(), e.getErrorCode() );

                    }

                }
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException caught in sendMultipleSurveyInvitations(). Nested exception is ", e );
            if ( errorMsg == null )
                errorMsg = "error";
            return errorMsg;

        }

        LOG.info( "Method sendMultipleSurveyInvitations() finished from DashboardController." );
        return "Success";
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
                stages = new ArrayList<>( organizationManagementService.getBranchSettingsDefault( columnValue )
                    .getProfileStages() );
            } else if ( columnName.equalsIgnoreCase( CommonConstants.AGENT_ID_COLUMN ) ) {
                stages = new ArrayList<>( userManagementService.getUserSettings( columnValue ).getProfileStages() );
            }
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            LOG.error( "NonFatalException while fetching badge details. Reason :" + e.getMessage(), e );
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


    private String getProfileLevel( String columnName )
    {
        LOG.debug( "Method to return profile level based upon column to be queried started." );
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
        LOG.info( "Method updateSelectedProfile() started." );
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
            LOG.error( "Number format exception occurred while parsing the entity id. Reason :" + e.getMessage(), e );
        }

        String entityType = request.getParameter( "entityType" );
        sessionHelper.updateSelectedProfile( session, entityId, entityType );

        LOG.info( "Method updateSelectedProfile() finished." );
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
        String agentIdStr = request.getParameter( "agentId" );
        String customerEmail = request.getParameter( "customerEmail" );
        String firstName = request.getParameter( "firstName" );
        String lastName = request.getParameter( "lastName" );
        try {
            if ( agentIdStr == null || agentIdStr.isEmpty() ) {
                throw new InvalidInputException( "Invalid value (Null/Empty) found for agentId." );
            }
            long agentId = Long.parseLong( agentIdStr );
            surveyHandler.changeStatusOfSurvey( agentId, customerEmail, firstName, lastName, true );
            SurveyDetails survey = surveyHandler.getSurveyDetails( agentId, customerEmail, firstName, lastName );
            User user = userManagementService.getUserByUserId( agentId );
            surveyHandler.decreaseSurveyCountForAgent( agentId );
            surveyHandler.sendSurveyRestartMail( firstName, lastName, customerEmail, survey.getCustRelationWithAgent(), user,
                survey.getUrl() );
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
        String customerEmail = request.getParameter( "customerEmail" );
        String firstName = request.getParameter( "firstName" );
        String lastName = request.getParameter( "lastName" );
        String review = request.getParameter( "review" );
        String reason = request.getParameter( "reportText" );
        String surveyMongoId = request.getParameter( "surveyMongoId" );

        try {
            long agentId = 0;
            try {
                String agentIdStr = request.getParameter( "agentId" );
                if ( agentIdStr == null || agentIdStr.isEmpty() ) {
                    throw new InvalidInputException( "Invalid value (Null/Empty) found for agentId." );
                }
                agentId = Long.parseLong( agentIdStr );
            } catch ( NumberFormatException e ) {
                LOG.error( "NumberFormatException caught in reportAbuse() while converting agentId." );
                throw e;
            }

            if ( surveyMongoId == null || surveyMongoId.isEmpty() ) {
                throw new InvalidInputException( "Invalid value (Null/Empty) found for surveyMongoId." );
            }

            String customerName = firstName + " " + lastName;
            if ( firstName == null || firstName.isEmpty() ) {
                User user = sessionHelper.getCurrentUser();
                customerName = user.getFirstName() + " " + user.getLastName();
            }

            String agentName = "";
            try {
                agentName = solrSearchService.getUserDisplayNameById( agentId );
            } catch ( SolrException e ) {
                LOG.info( "Solr Exception occured while fetching agent name. Nested exception is ", e );
                throw e;
            }

            //make survey as abusive
            surveyHandler.updateSurveyAsAbusive( surveyMongoId, customerEmail, customerName );

            // Calling email services method to send mail to the Application
            // level admin.
            emailServices.sendReportAbuseMail( applicationAdminEmail, applicationAdminName, agentName,
                customerName.replaceAll( "null", "" ), customerEmail, review, reason, null, null );
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
            if ( tokens.getLinkedInToken() != null && tokens.getLinkedInToken().getLinkedInPageLink() != null ) {
                model.addAttribute( "linkedinProfileUrl", tokens.getLinkedInToken().getLinkedInPageLink() );
            }
            if ( tokens.getZillowToken() != null && tokens.getZillowToken().getZillowProfileLink() != null ) {
                model.addAttribute( "zillowProfileUrl", tokens.getZillowToken().getZillowProfileLink() );
            }
        }

        return JspResolver.LINKEDIN_IMPORT_SOCIAL_LINKS;
    }
}
// JIRA SS-137 : by RM-05 : EOC
