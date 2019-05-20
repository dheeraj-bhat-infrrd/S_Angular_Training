package com.realtech.socialsurvey.web.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.entities.*;
import com.realtech.socialsurvey.core.enums.NotificationType;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
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
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
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
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.common.JspResolver;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


@Controller
public class ReportingWebController
{
    private static final Logger LOG = LoggerFactory.getLogger( ReportingWebController.class );

    private static final String COLUMN_NAME = "columnName";
    private static final String COLUMN_VALUE = "columnValue";
    private static final String COLUMN_ID = "columnId";
    private static final String PROFILE_NAME = "profileName";
    private static final String USER_ID = "userId";
    private static final String MESSAGE = "message";
    private static final String MONTH = "month";
    private static final String YEAR = "year";
    private static final String BATCH_SIZE = "batchSize";
    private static final String ENTITY_ID = "entityId";
    private static final String ENTITY_TYPE = "entityType";
    private static final String TIME_FRAME = "timeFrame";
    private static final String SOCIAL_MONITOR_FLAG = "isSocialMonitorEnabled";
    

    private SessionHelper sessionHelper;

    private OrganizationManagementService organizationManagementService;

    private UserManagementService userManagementService;

    private SolrSearchService solrSearchService;

    private MessageUtils messageUtils;

    private ProfileManagementService profileManagementService;

    private SSApiIntergrationBuilder ssApiIntergrationBuilder;

    private ReportingDashboardManagement reportingDashboardManagement;
    
    @Autowired
    public void setSessionHelper( SessionHelper sessionHelper )
    {
        this.sessionHelper = sessionHelper;
    }


    @Autowired
    public void setOrganizationManagementService( OrganizationManagementService organizationManagementService )
    {
        this.organizationManagementService = organizationManagementService;
    }


    @Autowired
    public void setUserManagementService( UserManagementService userManagementService )
    {
        this.userManagementService = userManagementService;
    }


    @Autowired
    public void setSolrSearchService( SolrSearchService solrSearchService )
    {
        this.solrSearchService = solrSearchService;
    }


    @Autowired
    public void setMessageUtils( MessageUtils messageUtils )
    {
        this.messageUtils = messageUtils;
    }


    @Autowired
    public void setProfileManagementService( ProfileManagementService profileManagementService )
    {
        this.profileManagementService = profileManagementService;
    }


    @Autowired
    public void setSsApiIntergrationBuilder( SSApiIntergrationBuilder ssApiIntergrationBuilder )
    {
        this.ssApiIntergrationBuilder = ssApiIntergrationBuilder;
    }


    @Autowired
    public void setReportingDashboardManagement( ReportingDashboardManagement reportingDashboardManagement )
    {
        this.reportingDashboardManagement = reportingDashboardManagement;
    }


    private boolean isIndividualAccount( User user )
    {
        LOG.debug( "Checking if individual account." );
        return ( user.getCompany() != null && user.getCompany().getLicenseDetails() != null
            && !user.getCompany().getLicenseDetails().isEmpty()
            && user.getCompany().getLicenseDetails().get( 0 ).getAccountsMaster() != null
            && user.getCompany().getLicenseDetails().get( 0 ).getAccountsMaster()
                .getAccountsMasterId() == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL );
    }


    private String getProfileName( String entityType, long entityId, User user ) throws InvalidInputException, SolrException
    {
        LOG.debug( "Getting profile name for entityType {}, entityId {} and user {}", entityType, entityId, user );
        String profileName = null;
        switch ( entityType ) {
            case CommonConstants.COMPANY_ID_COLUMN:
                profileName = user.getCompany().getCompany();
                break;
            case CommonConstants.REGION_ID_COLUMN:
                profileName = solrSearchService.searchRegionById( entityId );
                break;
            case CommonConstants.BRANCH_ID_COLUMN:
                profileName = solrSearchService.searchBranchNameById( entityId );
                break;
            case CommonConstants.AGENT_ID_COLUMN:
                profileName = user.getFirstName() + " " + user.getLastName();
                break;
            default:
                profileName = "";
                break;
        }
        LOG.trace( "Returning profile name {}", profileName );
        return profileName;
    }


    private OrganizationUnitSettings getOrganizationUnitSettings( long companyId, long regionId, long branchId, long agentId,
        User user )
        throws InvalidInputException, NoRecordsFetchedException, InvalidSettingsStateException, ProfileNotFoundException
    {
        LOG.debug( "Getting OrganizationUnitSettings for companyId: {}, regionId: {}, branchId: {}, agentId: {}", companyId,
            regionId, branchId, agentId );
        OrganizationUnitSettings companyProfile = null;
        OrganizationUnitSettings regionProfile = null;
        OrganizationUnitSettings branchProfile = null;
        OrganizationUnitSettings individualProfile = null;
        OrganizationUnitSettings profileSettings = null;
        // Get all details if all the inputs are greater than 0
        // Should not have condition where the higher hierarchy level is 0 and the lower ones are greater than 0
        if ( companyId > 0l ) {
            companyProfile = organizationManagementService.getCompanySettings( companyId );
            profileSettings = companyProfile;
            if ( regionId > 0l ) {
                regionProfile = organizationManagementService.getRegionSettings( regionId );
                if ( branchId > 0l ) {
                    branchProfile = organizationManagementService.getBranchSettingsDefault( branchId );
                    if ( agentId > 0l ) {
                        individualProfile = userManagementService.getAgentSettingsForUserProfiles( agentId );
                        Map<SettingsForApplication, OrganizationUnit> settingsMap = profileManagementService
                            .getPrimaryHierarchyByEntity( CommonConstants.AGENT_ID_COLUMN, agentId );
                        if ( settingsMap == null ) {
                            LOG.warn( "Could not fetch settings for agent: {}", agentId );
                            throw new InvalidInputException( "Could not fetch settings for agent: " + agentId );
                        }
                        individualProfile = profileManagementService.fillUnitSettings( individualProfile,
                            MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, companyProfile, regionProfile,
                            branchProfile, individualProfile, settingsMap, false );
                        individualProfile.setVertical( user.getCompany().getVerticalsMaster().getVerticalName() );
                        String disclaimer = profileManagementService.aggregateDisclaimer( individualProfile,
                            CommonConstants.AGENT_ID );
                        individualProfile.setDisclaimer( disclaimer );
                        individualProfile.setHiddenSection( companyProfile.isHiddenSection() );
                        profileSettings = individualProfile;
                    } else {
                        Map<SettingsForApplication, OrganizationUnit> settingsMap = profileManagementService
                            .getPrimaryHierarchyByEntity( CommonConstants.BRANCH_ID_COLUMN, branchId );
                        if ( settingsMap == null ) {
                            throw new InvalidInputException( "Could not fetch settings for branch: " + branchId );
                        }
                        branchProfile = profileManagementService.fillUnitSettings( branchProfile,
                            MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION, companyProfile, regionProfile,
                            branchProfile, null, settingsMap, false );
                        // aggregated disclaimer
                        String disclaimer = profileManagementService.aggregateDisclaimer( branchProfile,
                            CommonConstants.BRANCH_ID );
                        branchProfile.setDisclaimer( disclaimer );
                        branchProfile.setHiddenSection( companyProfile.isHiddenSection() );
                        profileSettings = branchProfile;
                    }
                } else {
                    Map<SettingsForApplication, OrganizationUnit> settingsMap = profileManagementService
                        .getPrimaryHierarchyByEntity( CommonConstants.REGION_ID, regionId );
                    if ( settingsMap == null ) {
                        throw new InvalidInputException( "Could not fetch settings for region: " + regionId );
                    }
                    regionProfile = profileManagementService.fillUnitSettings( regionProfile,
                        MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION, companyProfile, regionProfile, null,
                        null, settingsMap, false );

                    // aggregated disclaimer
                    String disclaimer = profileManagementService.aggregateDisclaimer( regionProfile,
                        CommonConstants.REGION_ID );
                    regionProfile.setDisclaimer( disclaimer );
                    regionProfile.setHiddenSection( companyProfile.isHiddenSection() );
                    profileSettings = regionProfile;
                }

            }
        }
        return profileSettings;
    }

    
    @RequestMapping ( value = "/reportingtransactiondetails", method = RequestMethod.GET)
    public String getReportingProfileDetails( Model model, HttpServletRequest request ) throws NonFatalException
    {
    	LOG.info( "get Reporting transaction details page" );
    	
    	return JspResolver.REPORTING_TRANSACTION_DETAILS;
    }
   
    @RequestMapping ( value = "/showreportingpage", method = RequestMethod.GET)
    public String showReportingPage( Model model, HttpServletRequest request ) throws NonFatalException
    {

        LOG.info( "showReportingPage: Started" );
        
        long branchId = 0;
        long regionId = 0;
        long companyId = 0;
        long agentId = 0;
        OrganizationUnitSettings profileSettings = null;
        JobLogDetailsResponse jobLogDetailsResponse = null;
        String lastSuccessfulRun = "";
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getLastSuccessfulEtlTimeApi();
        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
        if(responseString != null){
            responseString = responseString.substring( 1, responseString.length() - 1 );
            responseString = StringEscapeUtils.unescapeJava( responseString );
            Type responseType = new TypeToken<JobLogDetailsResponse>() {}.getType();
            jobLogDetailsResponse = new Gson().fromJson( responseString, responseType );
            if(jobLogDetailsResponse.getStatus().equalsIgnoreCase( CommonConstants.STATUS_DUMMY )){
                lastSuccessfulRun = "";
            } else {
                lastSuccessfulRun = jobLogDetailsResponse.getTimestampInEst();
            } 
        }
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();

        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );

        String profileName = "";
        
       /* boolean hasRegisteredForSummit = organizationManagementService.hasRegisteredForSummit(entityId);
        session.setAttribute("hasRegisteredForSummit", hasRegisteredForSummit);*/
        boolean isShowSummitPopup = organizationManagementService.isShowSummitPopup(entityId, entityType);
        session.setAttribute("isShowSummitPopup", isShowSummitPopup);
        
        //get unitSetting's and set session attribute column's 
        sessionHelper.updateSelectedProfile( session, entityId, entityType );

        // check if individual account
        boolean isIndividualAccount = isIndividualAccount( user );
        LOG.debug( "isIndividualAccount: {}", isIndividualAccount );

        // set profile name and model attribute
        if ( !isIndividualAccount ) {
            model.addAttribute( COLUMN_NAME, entityType );
            model.addAttribute( COLUMN_VALUE, entityId );
            profileName = getProfileName( entityType, entityId, user );
        } else {
            model.addAttribute( COLUMN_NAME, CommonConstants.AGENT_ID_COLUMN );
            model.addAttribute( COLUMN_VALUE, entityId );
        }

        model.addAttribute( PROFILE_NAME, profileName );
        model.addAttribute( USER_ID, user.getUserId() );
        
        //get detail of expire social media
        boolean isSocialMediaExpired = false;
        List<String> expiredSocialMediaList = organizationManagementService.getExpiredSocailMedia( entityType, entityId );
        if ( !expiredSocialMediaList.isEmpty() ) {
            isSocialMediaExpired = true;
        }
        session.setAttribute( "isSocialMediaExpired", isSocialMediaExpired );
        session.setAttribute( "expiredSocialMediaList", new Gson().toJson( expiredSocialMediaList )   );

        //Get the hierarchy details associated with the current profile get all the id's like companyId, regionId , branchId
        long hasBranch = 1;
        long hasRegion = 1;
        try {
            LOG.debug( "Getting hierarchy details for entityType {} and entityId {}", entityType, entityId );
            Map<String, Long> hierarchyDetails = profileManagementService.getHierarchyDetailsByEntity( entityType, entityId );
            branchId = hierarchyDetails.get( CommonConstants.BRANCH_ID_COLUMN );
            regionId = hierarchyDetails.get( CommonConstants.REGION_ID_COLUMN );
            companyId = hierarchyDetails.get( CommonConstants.COMPANY_ID_COLUMN );
            agentId = hierarchyDetails.get( CommonConstants.AGENT_ID_COLUMN );
            hasBranch = hierarchyDetails.get(CommonConstants.HAS_BRANCH);
            hasRegion = hierarchyDetails.get(CommonConstants.HAS_REGION);
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException while showing profile page. Reason :" + e.getMessage(), e );
            model.addAttribute( MESSAGE, messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        } catch ( ProfileNotFoundException e ) {
            LOG.error( "User profile not found", e );
            return JspResolver.NO_PROFILES_FOUND;
        }

        LOG.debug( "Getting organization unit settings for companyId {}, regionId {}, branchId {}, agentId {} and user {}",
            companyId, regionId, branchId, agentId, user );
        try {
            profileSettings = getOrganizationUnitSettings( companyId, regionId, branchId, agentId, user );
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            throw new InternalServerException( new ProfileServiceErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                CommonConstants.SERVICE_CODE_GENERAL, "Error occured while fetching profile" ), e.getMessage(), e );
        }

        //REALTECH_USER_ID is set only for real tech and SS admin
        boolean isRealTechOrSSAdmin = false;
        Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
        if ( adminUserid != null ) {
            isRealTechOrSSAdmin = true;
        }
        model.addAttribute( "isRealTechOrSSAdmin", isRealTechOrSSAdmin );

        boolean allowOverrideForSocialMedia = false;
        boolean hiddenSection = false;
        //Code to determine if social media can be overridden during autologin
        OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user.getCompany().getCompanyId() );
        
        allowOverrideForSocialMedia = companySettings.isAllowOverrideForSocialMedia();
        hiddenSection = companySettings.isHiddenSection();
            
        boolean smsSurveyOptionEnabled = false;
        boolean smsSurveyReminderEnabled = false;
        boolean manualSmsSurveyReminder = false;
        
        if(companySettings != null && companySettings.getSurvey_settings() != null ) {
            smsSurveyReminderEnabled = companySettings.getSurvey_settings().isSmsSurveyReminderEnabled();
        }
        
        if(profileSettings != null && profileSettings.getSurvey_settings() != null) {
            manualSmsSurveyReminder = profileSettings.getSurvey_settings().isManualSmsSurveyReminderEnabled();
        }
        
        if(smsSurveyReminderEnabled && manualSmsSurveyReminder ) {
            smsSurveyOptionEnabled = true;
        }
        
        
        // show sms survey reminder options if 
        
        model.addAttribute( "allowOverrideForSocialMedia", allowOverrideForSocialMedia );
        model.addAttribute( "hiddenSection", hiddenSection );
        model.addAttribute( "smsSurveyOptionEnabled", smsSurveyOptionEnabled);
        session.setAttribute( "smsSurveyOptionEnabled", smsSurveyOptionEnabled );
        model.addAttribute( "profileSettings", profileSettings );
        model.addAttribute( "lastSuccessfulRun", lastSuccessfulRun );

        model.addAttribute( CommonConstants.HAS_BRANCH, hasBranch );
        model.addAttribute( CommonConstants.HAS_REGION, hasRegion );
        
        // Added for LinkedIn v2 changes
        
        String linkedUpdateClass= "";
        String linkedV2TokenClass= "";
        String linkedProfileUrlClass= "";
        
        // logic to show/hide LinkedIn v2 migration banner.
        if(profileSettings != null && profileSettings.getSocialMediaTokens() != null && profileSettings.getSocialMediaTokens().getLinkedInV2Token() != null ) {
            if(StringUtils.isNotEmpty(profileSettings.getSocialMediaTokens().getLinkedInV2Token().getLinkedInPageLink())) {
                // For hiding whole banner
                linkedUpdateClass = "hide";
            } else {
                // For hiding migration v2 message
                linkedV2TokenClass = "hide";
            }
        }
        else {
            // For hiding profile URL empty message
            linkedProfileUrlClass = "hide";
        }

        model.addAttribute( "linkedProfileUrlClass", linkedProfileUrlClass );
        model.addAttribute( "linkedV2TokenClass", linkedV2TokenClass );
        model.addAttribute( "linkedUpdateClass", linkedUpdateClass );        
        
        // EOM LinkedIn V2 changes

        Notification notification = companySettings.getNotification();
        String notificationMessage = null ;
        if(notification != null && notification.getMessage()!= null){
            if(notification.getNotificationType() == NotificationType.ERROR )
                 notificationMessage = new Utils().convertDateToTimeZone( notification.getRecievedOn(),
                     CommonConstants.TIMEZONE_EST )+" "+notification.getMessage();
            else if(notification.getNotificationType().equals( NotificationType.SUCCESS )){
                notificationMessage = notification.getMessage();
            }
            model.addAttribute( "encompassStatus",notification.getNotificationType() );
            model.addAttribute( "encompassNotification", notificationMessage );
            model.addAttribute( "isEncompassDisabled", notification.isDisabled() );
        }
        session.setAttribute( CommonConstants.USER_PROFILE_SETTINGS, profileSettings );
        model.addAttribute( "vertical", profileSettings.getVertical().toLowerCase() );
        model.addAttribute("isIncompleteSurveyDeleteEnabled",companySettings.isIncompleteSurveyDeleteEnabled());
        return JspResolver.REPORTING_DASHBOARD;
    }

    @ResponseBody
    @RequestMapping ( value = "/setactivesessionforpopup", method = RequestMethod.GET)
    public String setActiveSessionForPopup( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to setactivesessionforpopup() Started" );
        HttpSession session = request.getSession( false );

        session.setAttribute("activeSession","true");

        LOG.info( "setactivesessionforpopup() Finished" );
        return new String( "Active Session set as true" );

    }
    
    @ResponseBody
    @RequestMapping ( value = "/fetchspsfromreportingoverview", method = RequestMethod.GET)
    public String reportingOverviewSpsStats( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to fetch Sps from Reporting Overview,  reportingOverviewSpsStats Started" );
        HttpSession session = request.getSession( false );

        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );

        LOG.debug( "Getting Overview SPS stats for entityType:{} with entityId: {}", entityType, entityId );
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getSpsStatsFromOverview( entityId, entityType );

        LOG.info( "Method to fetch Sps from Reporting Overview,  reportingOverviewSpsStats Finished" );
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );

    }


    @ResponseBody
    @RequestMapping ( value = "/fetchalltimefromreportingoverview", method = RequestMethod.GET)
    public String reportingOverviewAllTimeStats( Model model, HttpServletRequest request )
    {
        LOG.info( "Reporting overview for all time stats started." );
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        LOG.debug( "Getting Overview all time stats for entityType:{} with entityId: {}", entityType, entityId );
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getAllTimeDataOverview( entityId, entityType );
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
    }


    @ResponseBody
    @RequestMapping ( value = "/fetchreportingspsstats", method = RequestMethod.GET)
    public String fetchSpsStats( Model model, HttpServletRequest request )
    {
        LOG.info( " Method for Fetching SPS stats Graph started" );
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );

        LOG.debug( "Getting SPS stats for entityType:{} with entityId: {}", entityType, entityId );
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getReportingSpsStats( entityId, entityType );

        LOG.info( " Method for Fetching SPS stats Graph Finished" );
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );

    }

    @ResponseBody
    @RequestMapping ( value = "/reporting/npsgraph", method = RequestMethod.GET)
    public String fetchNpsStats( Model model, HttpServletRequest request )
    {
        LOG.info( " Method for Fetching NPS stats Graph started" );
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );

        LOG.debug( "Getting NPS stats for entityType:{} with entityId: {}", entityType, entityId );
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getReportingNpsStats( entityId, entityType );

        LOG.info( " Method for Fetching NPS stats Graph Finished" );
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );

    }

    @ResponseBody
    @RequestMapping ( value = "/fetchreportingcompletionrate", method = RequestMethod.GET)
    public String fetchCompletionRate( Model model, HttpServletRequest request )
    {
        LOG.info( " Method for Fetching Completion Rate Graph started" );

        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );

        LOG.debug( "Getting completion rate for entityType:{} with entityId: {}", entityType, entityId );
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getReportingCompletionRateApi( entityId, entityType );

        LOG.info( " Method for Fetching Completion Rate Graph Finished" );
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );

    }


    @ResponseBody
    @RequestMapping ( value = "/fetchmonthdataforoverview", method = RequestMethod.GET)
    public String fetchMonthDataForOverview( Model model, HttpServletRequest request ) throws NonFatalException
    {
        LOG.info( "Fetching Overview Based On Month" );
        HttpSession session = request.getSession( false );
        int month = 0;
        int year = 0;

        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        String sMonth = request.getParameter( MONTH );
        if ( sMonth != null && !sMonth.isEmpty() ) {
            month = Integer.valueOf( sMonth );
        } else {
            throw new InvalidInputException( "Month not present in criteria." );
        }
        String sYear = request.getParameter( YEAR );
        if ( sYear != null && !sYear.isEmpty() ) {
            year = Integer.valueOf( sYear );
        } else {
            throw new InvalidInputException( "Year not present in criteria." );
        }
        LOG.debug( "Getting month data overview for entityType:{} with entityId: {} and month {} and year {}", entityType,
            entityId, month, year );
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getMonthDataOverviewForDashboard( entityId, entityType,
            month, year );
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );

    }


    @ResponseBody
    @RequestMapping ( value = "/fetchyeardataforoverview", method = RequestMethod.GET)
    public String fetchYearDataForOverview( Model model, HttpServletRequest request ) throws NonFatalException
    {
        LOG.info( "Fetching Overview Based On Year" );
        HttpSession session = request.getSession( false );
        int year = 0;

        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        String sYear = request.getParameter( YEAR );
        if ( sYear != null && !sYear.isEmpty() ) {
            year = Integer.valueOf( sYear );
        } else {
            LOG.warn( "Year not present in criteria." );
            throw new InvalidInputException( "Year not present in criteria." );
        }
        LOG.debug( "Getting year data overview for entityType:{} with entityId: {} and year {}", entityType, entityId, year );
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getYearDataOverviewForDashboard( entityId, entityType,
            year );
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );

    }


    /*
    * Generate Reports For the reporting UI
    */
    @ResponseBody
    @RequestMapping ( value = "/savereportingdata", method = RequestMethod.POST)
    public String saveReportingData( Model model, HttpServletRequest request, HttpServletResponse response )
        throws NonFatalException, ParseException, IOException
    {
        LOG.info( "the step to generate reporting reports :generateReportingReports started " );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();
        Long adminUserid = user.getUserId();
        String message = "";
        //since we need to store the current time stamp

        Date startDate = null;
        String startDateStr = request.getParameter( "startDate" );
        if ( startDateStr != null && !startDateStr.isEmpty() ) {
            startDate = new SimpleDateFormat( "MM/dd/yyyy" ).parse( startDateStr );
        }
        Date endDate = null;
        String endDateStr = request.getParameter( "endDate" );
        if ( endDateStr != null && !endDateStr.isEmpty() ) {
            endDate = new SimpleDateFormat( "MM/dd/yyyy" ).parse( endDateStr );
        }
        String reportIdString = request.getParameter( "reportId" );
        int reportId = Integer.parseInt( reportIdString );
        String actualTimeZoneString = request.getParameter("clientTimeZone");
        int actualTimeZoneOffset = Integer.parseInt( actualTimeZoneString );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        Company company = user.getCompany();
        String keyword = request.getParameter("keyword");
        GenericReportingObject genericReportingObject = new GenericReportingObject();
        genericReportingObject.setKeyword( keyword );
        LOG.debug(
            "Creating entry in file upload for reportId {} with start date {} and end date {} for entity id {}, entity type {}",
            reportId, startDate, endDate, entityId, entityType );
        reportingDashboardManagement.createEntryInFileUploadForReporting( reportId, startDate, endDate, entityId, entityType,
            company, adminUserid, actualTimeZoneOffset, genericReportingObject);
        message = "The report is being generated";
        if ( reportId == CommonConstants.FILE_UPLOAD_SURVEY_INVITATION_EMAIL_REPORT ) {
            if ( startDate == null || endDate == null ) {
                message = message.concat( " for one month" );
            }
        }
        return message;

    }


    @ResponseBody
    @RequestMapping ( value = "/downloadaccountstatisticsreport", method = RequestMethod.POST)
    public String saveAccountStatisticsDataToFileUpload( Model model, HttpServletRequest request, HttpServletResponse response )
        throws InvalidInputException, NoRecordsFetchedException, FileNotFoundException, IOException
    {
        LOG.info( "Creating entry to file upload for account statistics report." );
        String message = "";
        User user = sessionHelper.getCurrentUser();
        Long adminUserid = user.getUserId();
        int reportId = CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_DETAILS_REPORT;
        LOG.debug( "Creating entry in file upload for report id {}", reportId );
        reportingDashboardManagement.createEntryInFileUploadForReporting( reportId, null, null, adminUserid,
            CommonConstants.AGENT_ID_COLUMN, user.getCompany(), adminUserid, 0, null );
        message = "The report is being generated";
        return message;
    }


    @ResponseBody
    @RequestMapping ( value = "/getaccountstatisticsreportstatus", method = RequestMethod.GET)
    public String getAccountStatisticsReportStatus( Model model, HttpServletRequest request )
    {
        LOG.info( "Fetching latest status for account statistics report." );

        long reportId = CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_DETAILS_REPORT;
        LOG.trace( "getting account statistics from api" );
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getAccountStatisticsRecentActivity( reportId );
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
    }


    /*
    * Method to get count of all the recent activities
    */
    @ResponseBody
    @RequestMapping ( value = "/fetchrecentactivitiescount")
    public String getIncompleteSurveyCount( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to get incomplete survey count." );
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        LOG.debug( "Getting incomplete survey count for entityType {} and entityId {}", entityType, entityId );
        long count = reportingDashboardManagement.getRecentActivityCount( entityId, entityType );
        LOG.info( "Method to get reviews of company, region, branch, agent getReviews() finished." );
        return String.valueOf( count );
    }


    @ResponseBody
    @RequestMapping ( value = "/fetchrecentactivities", method = RequestMethod.GET)
    public String fetchRecentActivity( Model model, HttpServletRequest request )
    {
        LOG.info( "Fetching Recent Activity Graph" );
        HttpSession session = request.getSession( false );

        int startIndex = 0;
        int batchSize = 0;
        String startIndexStr = request.getParameter( "startIndex" );
        String batchSizeStr = request.getParameter( BATCH_SIZE );
        if ( startIndexStr != null && !startIndexStr.isEmpty() ) {
            startIndex = Integer.parseInt( startIndexStr );
        }
        if ( batchSizeStr != null && !batchSizeStr.isEmpty() ) {
            batchSize = Integer.parseInt( batchSizeStr );
        }
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        LOG.debug( "Getting recent activity for entity id {}, entity type {}, startIndex {} and barch size {}", entityId,
            entityType, startIndex, batchSize );
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getRecentActivity( entityId, entityType, startIndex,
            batchSize );
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );

    }


    @ResponseBody
    @RequestMapping ( value = "/deletefromrecentactivities", method = RequestMethod.POST)
    public String deleteFromRecentActivity( HttpServletRequest request )
    {
        String message = "The row is deleted from the recentActivity and will not be displayed again";
        try {
            LOG.info( "deleting from recent activity." );
            long fileUploadId = 0;
            String fileUploadIdStr = request.getParameter( "fileUploadId" );
            if ( fileUploadIdStr != null && !fileUploadIdStr.isEmpty() ) {
                fileUploadId = Integer.parseInt( fileUploadIdStr );
            } else {
                message = "The row Id was null or an empty string";
            }
            LOG.debug( "Deleting {}", fileUploadId );
            reportingDashboardManagement.deleteRecentActivity( fileUploadId );
            return message;
        } catch ( Exception e ) {
            message = "There was an exception :" + e;
            return message;
        }

    }


    //TO SHOW REPORTING UI
    @RequestMapping ( value = "/showreportspage", method = RequestMethod.GET)
    public String showReportsPage( Model model, HttpServletRequest request ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.info( "Showing reports page" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        boolean isIndividualAccount = isIndividualAccount( user );
        if ( !isIndividualAccount ) {
            if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                model.addAttribute( COLUMN_NAME, CommonConstants.AGENT_ID_COLUMN );
                model.addAttribute( COLUMN_VALUE, entityId );
            } else {
                model.addAttribute( COLUMN_NAME, entityType );
                model.addAttribute( COLUMN_VALUE, entityId );
            }
        }
        
        
        // add the month name for month before last month
        Calendar calendar = Calendar.getInstance(); 
        calendar.add(Calendar.MONTH, -2);
        model.addAttribute( "monthBeforeLastMonth", new DateFormatSymbols().getMonths()[ calendar.get( Calendar.MONTH ) ] );
        
        return JspResolver.REPORTS;
    }
    
    @RequestMapping ( value = "/showsocialmonitorreportspage", method = RequestMethod.GET)
    public String showSocialMonitorReportsPage( Model model, HttpServletRequest request ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.info( "Showing social monitor reports page" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        boolean isIndividualAccount = isIndividualAccount( user );
        if ( !isIndividualAccount ) {
            if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
                model.addAttribute( COLUMN_NAME, CommonConstants.AGENT_ID_COLUMN );
                model.addAttribute( COLUMN_VALUE, entityId );
            } else {
                model.addAttribute( COLUMN_NAME, entityType );
                model.addAttribute( COLUMN_VALUE, entityId );
            }
        }
         
        return JspResolver.SOCIAL_MONITOR_REPORTS;
    }


    //Ranking Settings Page
    @RequestMapping ( value = "/showrankingsettings", method = RequestMethod.GET)
    public String showRankingPage( Model model, HttpServletRequest request ) throws NonFatalException
    {
        LOG.info( "showRankingPage called" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();
        OrganizationUnitSettings companySettings = organizationManagementService
            .getCompanySettings( user.getCompany().getCompanyId() );
        RankingRequirements rankingRequirements = companySettings.getRankingRequirements();
        model.addAttribute( "minDaysOfRegistration", rankingRequirements.getMinDaysOfRegistration() );
        model.addAttribute( "minCompletedPercentage", rankingRequirements.getMinCompletedPercentage() );
        model.addAttribute( "minNoOfReviews", rankingRequirements.getMinNoOfReviews() );
        model.addAttribute( "monthOffset", rankingRequirements.getMonthOffset() );
        model.addAttribute( "yearOffset", rankingRequirements.getYearOffset() );

        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        JobLogDetailsResponse jobLogDetailsResponse = null;
        String lastSuccessfulRun = "";

        model.addAttribute( COLUMN_NAME, entityType );
        model.addAttribute( COLUMN_ID, entityId );

        //REALTECH_USER_ID is set only for real tech and SS admin
        boolean isRealTechOrSSAdmin = false;
        Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
        if ( adminUserid != null ) {
            isRealTechOrSSAdmin = true;
        }
        model.addAttribute( "isRealTechOrSSAdmin", isRealTechOrSSAdmin );
        //add attribute to see if etl is running
        Response responseIsRun = ssApiIntergrationBuilder.getIntegrationApi().isEtlRunning();
        String responseIsRunString = responseIsRun != null ? new String( ( (TypedByteArray) responseIsRun.getBody() ).getBytes() ) : null;
        if(responseIsRunString != null)
        	responseIsRunString = responseIsRunString.substring( 1, responseIsRunString.length() - 1 );
        model.addAttribute( "isEtlRunning", responseIsRunString );
        //add attribute to pass last user ranking run info
        
        Response responseLastRun = ssApiIntergrationBuilder.getIntegrationApi().lastRunForEntity(entityId, entityType);
        String responseLastRunString = responseLastRun != null ? new String( ( (TypedByteArray) responseLastRun.getBody() ).getBytes() ) : null;
        if(responseLastRunString != null){
        	responseLastRunString = responseLastRunString.substring( 1, responseLastRunString.length() - 1 );
        	responseLastRunString = StringEscapeUtils.unescapeJava( responseLastRunString );
            Type responseType = new TypeToken<JobLogDetailsResponse>() {}.getType();
            jobLogDetailsResponse = new Gson().fromJson( responseLastRunString, responseType );
            lastSuccessfulRun = jobLogDetailsResponse.getTimestampInEst();
        }
        model.addAttribute( "lastSuccessfulRun", lastSuccessfulRun );

        return JspResolver.RANKING_SETTINGS;
    }

    //insert a record for user ranking job , in job log details and recalculate by kicking of script 
    @ResponseBody
    @RequestMapping ( value = "/recalranking", method = RequestMethod.GET)
    public String recalRanking( Model model, HttpServletRequest request ) 
    {
        LOG.info( "Recalculating user ranking for a particular company" );
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        
        Response response = ssApiIntergrationBuilder.getIntegrationApi().recalUserRanking( entityId, entityType);
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );

    }

    //Updating Ranking settings
    @ResponseBody
    @RequestMapping ( value = "/saverankingsettings", method = RequestMethod.PUT)
    public String saveRankingSettings( Model model, HttpServletRequest request ) throws NonFatalException
    {
        LOG.info( "Method changeSelectedRankingSettings() " );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession( false );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        String minDaysOfRegistrationStr = request.getParameter( "minDaysOfRegistration" );
        String minCompletedPercentageStr = request.getParameter( "minCompletedPercentage" );
        String minNoOfReviewsStr = request.getParameter( "minNoOfReviews" );
        String monthOffsetStr = request.getParameter( "monthOffset" );
        String yearOffsetStr = request.getParameter( "yearOffset" );
        int minDaysOfRegistration = 0;
        float minCompletedPercentage = 0;
        int minNoOfReviews = 0;
        double monthOffset = 0.0;
        int yearOffset = 0;
        String message = null;


        if ( minDaysOfRegistrationStr != null && !minDaysOfRegistrationStr.isEmpty() ) {
            minDaysOfRegistration = Integer.parseInt( minDaysOfRegistrationStr );
        }
        if ( minCompletedPercentageStr != null && !minCompletedPercentageStr.isEmpty() ) {
            minCompletedPercentage = Float.parseFloat( minCompletedPercentageStr );
        }
        if ( minNoOfReviewsStr != null && !minNoOfReviewsStr.isEmpty() ) {
            minNoOfReviews = Integer.parseInt( minNoOfReviewsStr );
        }
        if ( monthOffsetStr != null && !monthOffsetStr.isEmpty() ) {
            monthOffset = Double.parseDouble( monthOffsetStr );
        }
        if ( yearOffsetStr != null && !yearOffsetStr.isEmpty() ) {
            yearOffset = Integer.parseInt( yearOffsetStr );
        }


        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            OrganizationUnitSettings companySettings = organizationManagementService
                .getCompanySettings( user.getCompany().getCompanyId() );
            if ( companySettings == null ) {
                throw new InvalidInputException( "No company settings found in current session" );
            }
            RankingRequirements rankingRequirements = reportingDashboardManagement.updateRankingRequirements(
                minDaysOfRegistration, minCompletedPercentage, minNoOfReviews, monthOffset, yearOffset );
            reportingDashboardManagement.updateRankingRequirementsMongo(
                MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, companySettings, rankingRequirements );
        }

        message = "The ranking requirements are changed";
        return message;
    }


    private long getEntityIdFromAgentSettings( AgentSettings agentSettings, String entityType, long entityId,
        String sessionColumnType, long sessionColumnId ) throws InvalidInputException, ProfileNotFoundException
    {
        LOG.debug(
            "Getting enity id from agent settings. Entity type {}, entity id {}, session column type {}, session column id {}",
            entityType, entityId, sessionColumnType, sessionColumnId );
        Map<String, Long> hierarchyMap = profileManagementService.getPrimaryHierarchyByAgentProfile( agentSettings );

        long viewAsEntityId = entityId;

        if ( entityType.equals( CommonConstants.REGION_ID_COLUMN )
            && sessionColumnType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
            viewAsEntityId = hierarchyMap.get( CommonConstants.REGION_ID_COLUMN );
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN )
            && sessionColumnType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {
            viewAsEntityId = hierarchyMap.get( CommonConstants.BRANCH_ID_COLUMN );
        } else if ( sessionColumnType.equals( CommonConstants.BRANCH_ID_COLUMN )
            && entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            viewAsEntityId = reportingDashboardManagement.getRegionIdFromBranchId( sessionColumnId );
        }

        return viewAsEntityId;
    }


    @ResponseBody
    @RequestMapping ( value = "/getuserrankingrankandcount", method = RequestMethod.GET)
    public String getUserRankingRankAndCount( Model model, HttpServletRequest request ) throws NonFatalException
    {
        LOG.info( "Get User Ranking Rank And Count" );

        User user = sessionHelper.getCurrentUser();
        Long userId = user.getUserId();
        Integer batchSize = 0;
        int timeFrame = 1;
        Long entityId = 0l;
        int year = 0;
        int month = 0;
        String batchSizeStr = request.getParameter( BATCH_SIZE );
        String entityIdStr = request.getParameter( ENTITY_ID );
        String entityType = request.getParameter( ENTITY_TYPE );
        String timeFrameStr = request.getParameter( TIME_FRAME );
        String yearStr = request.getParameter( YEAR );
        String monthStr = request.getParameter( MONTH );
        Response response = null;
        if ( batchSizeStr != null && !batchSizeStr.isEmpty() ) {
            batchSize = Integer.parseInt( batchSizeStr );
        }
        if ( ( entityType == null || entityType.isEmpty() ) ) {
            LOG.warn( "Entity type is blank fir user ranking and count." );
            throw new InvalidInputException( "Entity type is blank fir user ranking and count." );
        }
        if ( timeFrameStr != null && !timeFrameStr.isEmpty() ) {
            timeFrame = Integer.parseInt( timeFrameStr );
        }
        if ( entityIdStr != null && !entityIdStr.isEmpty() ) {
            try {
                entityId = Long.parseLong( entityIdStr );
            } catch ( NumberFormatException e ) {
                LOG.error( "NumberFormatException caught while parsing columnValue in getReviews(). Nested exception is ", e );
                throw e;
            }
        }
        if ( yearStr != null && !yearStr.isEmpty() ) {
            year = Integer.parseInt( yearStr );
        }
        if ( monthStr != null && !monthStr.isEmpty() ) {
            month = Integer.parseInt( monthStr );
        }

        HttpSession session = request.getSession( false );
        long sessionColumnId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String sessionColumnType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );

        AgentSettings agentSettings = userManagementService.getUserSettings( userId );
        entityId = ( entityType.equals( sessionColumnType ) ) ? entityId
            : getEntityIdFromAgentSettings( agentSettings, entityType, entityId, sessionColumnType, sessionColumnId );

        response = getUserRankingRankCount( timeFrame, userId, entityId, entityType, month, year, batchSize );
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
    }


    private Response getUserRankingRankCount( int timeFrame, long userId, long entityId, String entityType, int month, int year,
        int batchSize ) throws NonFatalException
    {
        LOG.debug(
            "Getting user ranking rank count. Time frame {}, user id {}, entity type {}, month {}, year {}, batch size {}",
            timeFrame, userId, entityId, entityType, month, year, batchSize );
        Response response = null;
        switch ( timeFrame ) {
            case 1:
                response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingRankCountForThisYear( userId, entityId,
                    entityType, year, batchSize );
                break;
            case 2:
                response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingRankCountForThisMonth( userId, entityId,
                    entityType, month, year, batchSize );
                break;
            case 3:
                response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingRankCountForPastYear( userId, entityId,
                    entityType, year, batchSize );
                break;
            case 4:
                response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingRankCountForPastMonth( userId, entityId,
                    entityType, month, year, batchSize );
                break;
            case 5:
                response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingRankCountForPastYears( userId, entityId,
                    entityType, batchSize );
                break;
            default:
                LOG.warn( "Invalid timeframe {}", timeFrame );
                throw new NonFatalException( "Invalid timeframe " + timeFrame );
        }
        return response;
    }


    @ResponseBody
    @RequestMapping ( value = "/getuserrankingcount", method = RequestMethod.GET)
    public String getUserRankingCount( Model model, HttpServletRequest request ) throws NonFatalException
    {
        LOG.info( "Method to get User Ranking Count. " );

        Integer batchSize = 0;
        int timeFrame = 1;
        Long entityId = (long) 0;
        int year = 0;
        int month = 0;
        String batchSizeStr = request.getParameter( BATCH_SIZE );
        String entityIdStr = request.getParameter( ENTITY_ID );
        String entityType = request.getParameter( ENTITY_TYPE );
        String timeFrameStr = request.getParameter( TIME_FRAME );
        String yearStr = request.getParameter( YEAR );
        String monthStr = request.getParameter( MONTH );
        Response response = null;
        if ( batchSizeStr != null && !batchSizeStr.isEmpty() ) {
            batchSize = Integer.parseInt( batchSizeStr );
        }
        if ( ( entityType == null || entityType.isEmpty() ) ) {
            LOG.warn( "Invalid value (null/empty) passed for profile level." );
            throw new InvalidInputException( "Invalid value (null/empty) passed for entityType" );
        }
        if ( timeFrameStr != null && !timeFrameStr.isEmpty() ) {
            timeFrame = Integer.parseInt( timeFrameStr );
        }
        if ( entityIdStr != null && !entityIdStr.isEmpty() ) {
            try {
                entityId = Long.parseLong( entityIdStr );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "NumberFormatException caught while parsing columnValue in getUserRankingCount(). Nested exception is ",
                    e );
                throw e;
            }
        }
        if ( yearStr != null && !yearStr.isEmpty() ) {
            year = Integer.parseInt( yearStr );
        }
        if ( monthStr != null && !monthStr.isEmpty() ) {
            month = Integer.parseInt( monthStr );
        }

        User user = sessionHelper.getCurrentUser();
        Long userId = user.getUserId();

        HttpSession session = request.getSession( false );
        long sessionColumnId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String sessionColumnType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );

        AgentSettings agentSettings = userManagementService.getUserSettings( userId );
        entityId = ( entityType.equals( sessionColumnType ) ) ? entityId
            : getEntityIdFromAgentSettings( agentSettings, entityType, entityId, sessionColumnType, sessionColumnId );

        response = getUserRankingCount( timeFrame, entityId, entityType, month, year, batchSize );

        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
    }


    private Response getUserRankingCount( int timeFrame, long entityId, String entityType, int month, int year, int batchSize )
        throws NonFatalException
    {
        LOG.debug( "Get user ranking count. Time frame {}, entity id {}, entity type {}, month {}, year {}, batch size {}",
            timeFrame, entityId, entityType, month, year, batchSize );
        Response response = null;
        switch ( timeFrame ) {
            case 1:
                response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingCountForThisYear( entityId, entityType,
                    year, batchSize );
                break;
            case 2:
                response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingCountForThisMonth( entityId, entityType,
                    month, year, batchSize );
                break;
            case 3:
                response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingCountForPastYear( entityId, entityType,
                    year, batchSize );
                break;
            case 4:
                response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingCountForPastMonth( entityId, entityType,
                    month, year, batchSize );
                break;
            case 5:
                response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingCountForPastYears( entityId, entityType,
                    batchSize );
                break;
            default:
                LOG.warn( "Invalid timeframe {}", timeFrame );
                throw new NonFatalException( "Invalid timeframe " + timeFrame );
        }
        return response;
    }


    @ResponseBody
    @RequestMapping ( value = "/getuserranking", method = RequestMethod.GET)
    public String getUserRanking( Model model, HttpServletRequest request ) throws NonFatalException
    {
        LOG.info( "Method TO Get User Ranking started" );

        String entityType = request.getParameter( CommonConstants.ENTITY_TYPE_COLUMN );
        String entityIdStr = request.getParameter( CommonConstants.ENTITY_ID_COLUMN );
        String timeFrameStr = request.getParameter( TIME_FRAME );
        String startIndexStr = request.getParameter( "startIndex" );
        String batchSizeStr = request.getParameter( BATCH_SIZE );
        String yearStr = request.getParameter( YEAR );
        String monthStr = request.getParameter( MONTH );
        int startIndex = 0;
        int batchSize = 11;
        int timeFrame = 1;
        long entityId = 0;

        int year = ( Calendar.getInstance() ).get( Calendar.YEAR );

        Response response = null;

        if ( startIndexStr != null && !startIndexStr.isEmpty() ) {
            startIndex = Integer.parseInt( startIndexStr );
        }
        if ( batchSizeStr != null && !batchSizeStr.isEmpty() ) {
            batchSize = Integer.parseInt( batchSizeStr );
        }
        if ( timeFrameStr != null && !timeFrameStr.isEmpty() ) {
            timeFrame = Integer.parseInt( timeFrameStr );
        }
        if ( entityIdStr != null && !entityIdStr.isEmpty() ) {
            entityId = Long.parseLong( entityIdStr );
        }
        if ( yearStr != null && !yearStr.isEmpty() ) {
            year = Integer.parseInt( yearStr );
        }

        User user = sessionHelper.getCurrentUser();
        Long userId = user.getUserId();
        HttpSession session = request.getSession( false );

        long sessionColumnId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String sessionColumnType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );

        AgentSettings agentSettings = userManagementService.getUserSettings( userId );
        entityId = ( entityType.equals( sessionColumnType ) ) ? entityId
            : getEntityIdFromAgentSettings( agentSettings, entityType, entityId, sessionColumnType, sessionColumnId );

        response = getUserRankingList( timeFrame, entityId, entityType, monthStr, year, startIndex, batchSize );
        LOG.info( "Method TO Get User Ranking ended " );

        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
    }


    private Response getUserRankingList( int timeFrame, long entityId, String entityType, String monthStr, int year,
        int startIndex, int batchSize ) throws NonFatalException
    {
        LOG.debug(
            "Method to Get User Ranking List based on time frame started for entityType : {} , entityId : {} , timeFrame : {}",
            entityType, entityId, timeFrame );
        Response response = null;
        switch ( timeFrame ) {
            case 1:
                response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingForThisYear( entityId, entityType, year,
                    startIndex, batchSize );
                break;
            case 2:
                int month = Integer.parseInt( monthStr );
                response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingForThisMonth( entityId, entityType, month,
                    year, startIndex, batchSize );
                break;
            case 3:
                response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingForPastYear( entityId, entityType, year,
                    startIndex, batchSize );
                break;
            case 4:
                month = Integer.parseInt( monthStr );
                response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingForPastMonth( entityId, entityType, month,
                    year, startIndex, batchSize );
                break;
            case 5:
                response = ssApiIntergrationBuilder.getIntegrationApi().getUserRankingForPastYears( entityId, entityType,
                    startIndex, batchSize );
                break;
            default:
                LOG.warn( "Invalid timeframe {}", timeFrame );
                throw new NonFatalException( "Invalid timeframe " + timeFrame );
        }
        LOG.debug(
            "Method TO Get User Ranking List based on time frame ended for entityType : {} , entityId : {} , timeFrame : {}",
            entityType, entityId, timeFrame );
        return response;
    }


    @ResponseBody
    @RequestMapping ( value = "/getoverallscorestats", method = RequestMethod.GET)
    public String getOverallScoreStats( Model model, HttpServletRequest request ) throws NonFatalException
    {
        LOG.info( "Method to get overall score stats getOverallScoreStats() started." );
        Long entityId = 0l;
        int currentYear = 0;
        int currentMonth = 0;
        String entityIdStr = request.getParameter( ENTITY_ID );
        String entityType = request.getParameter( ENTITY_TYPE );
        String currentYearStr = request.getParameter( "currentYear" );
        String currentMonthStr = request.getParameter( "currentMonth" );
        Response response = null;

        if ( ( entityType == null || entityType.isEmpty() ) ) {
            LOG.warn( "entityType is null for get overall score stats" );
            throw new InvalidInputException( "entityType is null for get overall score stats." );
        }

        if ( entityIdStr != null && !entityIdStr.isEmpty() ) {
            try {
                entityId = Long.parseLong( entityIdStr );
            } catch ( NumberFormatException e ) {
                LOG.error( "NumberFormatException caught while parsing entityId in getReviews(). Nested exception is ", e );
                throw e;
            }
        }
        if ( currentYearStr != null && !currentYearStr.isEmpty() ) {
            currentYear = Integer.parseInt( currentYearStr );
        }
        if ( currentMonthStr != null && !currentMonthStr.isEmpty() ) {
            currentMonth = Integer.parseInt( currentMonthStr );
        }

        response = ssApiIntergrationBuilder.getIntegrationApi().getScoreStatsOverall( entityId, entityType, currentMonth,
            currentYear );

        LOG.info( "Method to get overall score stats getOverallScoreStats() finished." );
        String responseString = null;
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }


    @ResponseBody
    @RequestMapping ( value = "/getquestionscorestats", method = RequestMethod.GET)
    public String getQuestionScoreStats( Model model, HttpServletRequest request ) throws NonFatalException
    {
        LOG.info( "Method to get score stats for questions started." );
        Long entityId = 0l;
        int currentYear = 0;
        int currentMonth = 0;
        String entityIdStr = request.getParameter( ENTITY_ID );
        String entityType = request.getParameter( ENTITY_TYPE );
        String currentYearStr = request.getParameter( "currentYear" );
        String currentMonthStr = request.getParameter( "currentMonth" );
        Response response = null;
        User user = sessionHelper.getCurrentUser();

        if ( ( entityType == null || entityType.isEmpty() ) ) {
            LOG.warn( "Invalid value (null/empty) passed for entityType." );
            throw new InvalidInputException( "Invalid value (null/empty) passed for entityType." );
        }

        if ( entityIdStr != null && !entityIdStr.isEmpty() ) {
            try {
                entityId = Long.parseLong( entityIdStr );
            } catch ( NumberFormatException e ) {
                LOG.warn( "NumberFormatException caught while parsing entityId in getReviews(). Nested exception is ", e );
                throw e;
            }
        }
        if ( currentYearStr != null && !currentYearStr.isEmpty() ) {
            currentYear = Integer.parseInt( currentYearStr );
        }
        if ( currentMonthStr != null && !currentMonthStr.isEmpty() ) {
            currentMonth = Integer.parseInt( currentMonthStr );
        }

        response = ssApiIntergrationBuilder.getIntegrationApi().getScoreStatsQuestion( entityId, entityType, currentMonth,
            currentYear, user.getUserId() );

        LOG.info( "Method to get score stats for questions finished." );
        String responseString = null;
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }
    
    @RequestMapping ( value = "/sethasregisteredforsummit", method = RequestMethod.POST)
    @ResponseBody
    public String setHasRegisteredForSummit( HttpServletRequest request )
    {
        LOG.info( "Method setHasregisteredforsummit() started" );
        HttpSession session = request.getSession();

        long companyId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        
        String hasRegisteredForSummitStr = request.getParameter( "hasRegisteredForSummit" );
        boolean hasRegisteredForSummit = false;
        if(hasRegisteredForSummitStr.equalsIgnoreCase("true")){
        	hasRegisteredForSummit = true;
        }
        
        Response response = null;
        
        try {
        	 response = ssApiIntergrationBuilder.getIntegrationApi().setHasRegisteredForSummit(companyId, hasRegisteredForSummit);

        	 LOG.info( "Method to setHasregisteredforsummit() finished." );
        	        String responseString = null;
        	        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        	        return responseString;
        } catch ( Exception error ) {
            LOG.error(
                "Exception occured in setHasregisteredforsummit(). Nested exception is ",
                error );
            return "false";
        }
    }
    
    @RequestMapping ( value = "/setshowsummitpopup", method = RequestMethod.POST)
    @ResponseBody
    public String setShowSummitPopup( HttpServletRequest request )
    {
        LOG.info( "Method setShowSummitPopup() started" );
        HttpSession session = request.getSession();

        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        
        String isShowSummitPopupStr = request.getParameter( "isShowSummitPopup" );
        boolean isShowSummitPopup = false;
        if(isShowSummitPopupStr.equalsIgnoreCase("true")){
        	isShowSummitPopup = true;
        }
        
        Response response = null;
        
        try {
        	 response = ssApiIntergrationBuilder.getIntegrationApi().setShowSummitPopup(entityId,entityType, isShowSummitPopup);

        	 LOG.info( "Method to setShowSummitPopup() finished." );
        	        String responseString = null;
        	        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        	        return responseString;
        } catch ( Exception error ) {
            LOG.error(
                "Exception occured in setShowSummitPopup(). Nested exception is ",
                error );
            return "false";
        }
    }

}

