package com.realtech.socialsurvey.web.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.RankingRequirements;
import com.realtech.socialsurvey.core.entities.User;
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
        return ( user.getCompany() != null && user.getCompany().getLicenseDetails() != null
            && !user.getCompany().getLicenseDetails().isEmpty()
            && user.getCompany().getLicenseDetails().get( 0 ).getAccountsMaster() != null
            && user.getCompany().getLicenseDetails().get( 0 ).getAccountsMaster()
                .getAccountsMasterId() == CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL );
    }


    private String getProfileName( String entityType, long entityId, User user ) throws InvalidInputException, SolrException
    {
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


    @RequestMapping ( value = "/showreportingpage", method = RequestMethod.GET)
    public String showReportingPage( Model model, HttpServletRequest request ) throws NonFatalException
    {

        LOG.info( "showReportingPage: Started" );

        long branchId = 0;
        long regionId = 0;
        long companyId = 0;
        long agentId = 0;
        OrganizationUnitSettings profileSettings = null;

        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();

        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );

        String profileName = "";

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

        //Get the hierarchy details associated with the current profile get all the id's like companyId, regionId , branchId
        try {
            Map<String, Long> hierarchyDetails = profileManagementService.getHierarchyDetailsByEntity( entityType, entityId );
            branchId = hierarchyDetails.get( CommonConstants.BRANCH_ID_COLUMN );
            regionId = hierarchyDetails.get( CommonConstants.REGION_ID_COLUMN );
            companyId = hierarchyDetails.get( CommonConstants.COMPANY_ID_COLUMN );
            agentId = hierarchyDetails.get( CommonConstants.AGENT_ID_COLUMN );
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException while showing profile page. Reason :" + e.getMessage(), e );
            model.addAttribute( MESSAGE, messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        } catch ( ProfileNotFoundException e ) {
            LOG.error( "User profile not found", e );
            return JspResolver.NO_PROFILES_FOUND;
        }

        try {
            profileSettings = getOrganizationUnitSettings( companyId, regionId, branchId, agentId, user );
        } catch ( InvalidInputException | NoRecordsFetchedException e ) {
            throw new InternalServerException( new ProfileServiceErrorCode( CommonConstants.ERROR_CODE_GENERAL,
                CommonConstants.SERVICE_CODE_GENERAL, "Error occured while fetching profile" ), e.getMessage() );
        }

        //REALTECH_USER_ID is set only for real tech and SS admin
        boolean isRealTechOrSSAdmin = false;
        Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
        if ( adminUserid != null ) {
            isRealTechOrSSAdmin = true;
        }
        model.addAttribute( "isRealTechOrSSAdmin", isRealTechOrSSAdmin );

        model.addAttribute( "profileSettings", profileSettings );
        session.setAttribute( CommonConstants.USER_PROFILE_SETTINGS, profileSettings );
        return JspResolver.REPORTING_DASHBOARD;
    }


    @ResponseBody
    @RequestMapping ( value = "/fetchspsfromreportingoverview", method = RequestMethod.GET)
    public String reportingOverviewSpsStats( Model model, HttpServletRequest request )
    {
        LOG.info( "Reporting Dashboard Page started" );
        HttpSession session = request.getSession( false );

        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        LOG.debug( "Getting Overview SPS stats for entityType:{} with entityId: {}", entityType, entityId );
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getSpsStatsFromOverview( entityId, entityType );
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );

    }


    @ResponseBody
    @RequestMapping ( value = "/fetchalltimefromreportingoverview", method = RequestMethod.GET)
    public String reportingOverviewAllTimeStats( Model model, HttpServletRequest request )
    {
        LOG.info( "Reporting Dashboard Page started" );
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
        LOG.info( "Fetching Sps Stats Graph" );
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        LOG.debug( "Getting SPS stats for entityType:{} with entityId: {}", entityType, entityId );
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getReportingSpsStats( entityId, entityType );
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );

    }


    @ResponseBody
    @RequestMapping ( value = "/fetchreportingcompletionrate", method = RequestMethod.GET)
    public String fetchCompletionRate( Model model, HttpServletRequest request )
    {
        LOG.info( "Fetching Completion Rate Graph" );
        HttpSession session = request.getSession( false );
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        LOG.debug( "Getting completion rate for entityType:{} with entityId: {}", entityType, entityId );
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getReportingCompletionRateApi( entityId, entityType );
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
        long entityId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String entityType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );
        Company company = user.getCompany();
        reportingDashboardManagement.createEntryInFileUploadForReporting( reportId, startDate, endDate, entityId, entityType,
            company, adminUserid );
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
            LOG.info( "Fetching Recent Activity Graph" );
            long fileUploadId = 0;
            String fileUploadIdStr = request.getParameter( "fileUploadId" );
            if ( fileUploadIdStr != null && !fileUploadIdStr.isEmpty() ) {
                fileUploadId = Integer.parseInt( fileUploadIdStr );
            } else {
                message = "The row Id was null or an empty string";
            }
            reportingDashboardManagement.deleteRecentActivity( fileUploadId );
            return message;
        } catch ( Exception e ) {
            message = "There was an exception :" + e;
            return message;
        }

    }


    //TO SHOW REPORTING UI
    @RequestMapping ( value = "/showreportspage", method = RequestMethod.GET)
    public String showReportsPage( Model model, HttpServletRequest request )
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
        return JspResolver.REPORTS;
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

        model.addAttribute( COLUMN_NAME, entityType );
        model.addAttribute( COLUMN_ID, entityId );

        //REALTECH_USER_ID is set only for real tech and SS admin
        boolean isRealTechOrSSAdmin = false;
        Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );
        if ( adminUserid != null ) {
            isRealTechOrSSAdmin = true;
        }
        model.addAttribute( "isRealTechOrSSAdmin", isRealTechOrSSAdmin );

        return JspResolver.RANKING_SETTINGS;
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
        int monthOffset = 0;
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
            monthOffset = Integer.parseInt( monthOffsetStr );
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


    private long getEntityIdFromAgentSettings( AgentSettings agentSettings, String entityType, long entityId, String sessionColumnType,
        long sessionColumnId ) throws InvalidInputException, ProfileNotFoundException
    {
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
            LOG.error( "Entity type is blank fir user ranking and count." );
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
        entityId = ( entityType.equals(sessionColumnType) ) ? entityId
            : getEntityIdFromAgentSettings( agentSettings, entityType,entityId, sessionColumnType, sessionColumnId );

        response = getUserRankingRankCount( timeFrame, userId, entityId, entityType, month, year, batchSize );
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
    }


    private Response getUserRankingRankCount( int timeFrame, long userId, long entityId, String entityType, int month, int year,
        int batchSize ) throws NonFatalException
    {
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
                throw new NonFatalException( "NonFatalException while getting User Ranking Count" );
        }
        return response;
    }


    @ResponseBody
    @RequestMapping ( value = "/getuserrankingcount", method = RequestMethod.GET)
    public String getUserRankingCount( Model model, HttpServletRequest request ) throws NonFatalException
    {
        LOG.info( "Get User Ranking Rank " );

        LOG.info( "Method to get reviews of company, region, branch, agent getReviews() started." );
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
            LOG.error( "Invalid value (null/empty) passed for profile level." );
            throw new InvalidInputException( "Invalid value (null/empty) passed for entityType" );
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

        User user = sessionHelper.getCurrentUser();
        Long userId = user.getUserId();

        HttpSession session = request.getSession( false );
        long sessionColumnId = (long) session.getAttribute( CommonConstants.ENTITY_ID_COLUMN );
        String sessionColumnType = (String) session.getAttribute( CommonConstants.ENTITY_TYPE_COLUMN );

        AgentSettings agentSettings = userManagementService.getUserSettings( userId );
        entityId = (entityType.equals(sessionColumnType)) ? entityId
            : getEntityIdFromAgentSettings( agentSettings, entityType,entityId, sessionColumnType, sessionColumnId );

        response = getUserRankingCount( timeFrame, entityId, entityType, month, year, batchSize );

        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
    }


    private Response getUserRankingCount( int timeFrame, long entityId, String entityType, int month, int year,
        int batchSize ) throws NonFatalException
    {
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
                throw new NonFatalException( "NonFatalException while getting User Ranking Count" );
        }
        return response;
    }


    @ResponseBody
    @RequestMapping ( value = "/getuserranking", method = RequestMethod.GET)
    public String getUserRanking( Model model, HttpServletRequest request ) throws NonFatalException
    {
        LOG.info( "Get User Ranking for this year" );

        LOG.info( "Method to get reviews of company, region, branch, agent getReviews() started." );

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
        entityId = ( entityType.equals(sessionColumnType) ) ? entityId
            : getEntityIdFromAgentSettings( agentSettings, entityType,entityId, sessionColumnType, sessionColumnId );

        response = getUserRankingList( timeFrame, entityId, entityType, monthStr, year, startIndex, batchSize );

        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );
    }
    
    private Response getUserRankingList( int timeFrame, long entityId, String entityType, String monthStr, int year, int startIndex,
        int batchSize ) throws NonFatalException{
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
                throw new NonFatalException( "NonFatalException while choosing time frame for leaderboard" );
        }
        return response;
    }


    @ResponseBody
    @RequestMapping ( value = "/getoverallscorestats", method = RequestMethod.GET)
    public String getOverallScoreStats( Model model, HttpServletRequest request ) throws NonFatalException
    {

        LOG.info( "Get Overall Score Stats " );

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

        LOG.info( "Get Overall Score Stats " );

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
            LOG.error( "Invalid value (null/empty) passed for entityType." );
            throw new InvalidInputException( "Invalid value (null/empty) passed for entityType." );
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

        response = ssApiIntergrationBuilder.getIntegrationApi().getScoreStatsQuestion( entityId, entityType, currentMonth,
            currentYear );

        LOG.info( "Method to get overall score stats getOverallScoreStats() finished." );
        String responseString = null;
        responseString = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
        return responseString;
    }

}

