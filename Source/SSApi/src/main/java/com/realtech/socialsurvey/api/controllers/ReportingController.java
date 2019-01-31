package com.realtech.socialsurvey.api.controllers;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.google.gson.Gson;
import com.realtech.socialsurvey.api.models.response.FileUploadResponse;
import com.realtech.socialsurvey.core.entities.CompanyActiveUsersStats;
import com.realtech.socialsurvey.core.entities.CompanyDetailsReport;
import com.realtech.socialsurvey.core.entities.CompanySurveyStatusStats;
import com.realtech.socialsurvey.core.entities.CompanyView;
import com.realtech.socialsurvey.core.entities.DigestRequestData;
import com.realtech.socialsurvey.core.entities.SurveyInvitationEmailCountMonth;
import com.realtech.socialsurvey.core.entities.SurveyResultsReportVO;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.activitymanager.ActivityManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.reportingmanagement.DashboardGraphManagement;
import com.realtech.socialsurvey.core.services.reportingmanagement.OverviewManagement;
import com.realtech.socialsurvey.core.services.reportingmanagement.ReportingDashboardManagement;

import io.swagger.annotations.ApiOperation;
import com.realtech.socialsurvey.core.vo.SurveyTransactionReportVO;
import com.realtech.socialsurvey.core.vo.SurveyInvitationEmailCountVO;


@RestController
@RequestMapping ( "/v1")
public class ReportingController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ReportingController.class );

    @Autowired
    private DashboardGraphManagement dashboardGraphManagement;

    @Autowired
    private ReportingDashboardManagement reportingDashboardManagement;

    @Autowired
    private OverviewManagement overviewManagement;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private ActivityManagementService activityManagementService;


    @RequestMapping ( value = "/getcompletionrate", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for Completion Rate Graph")
    public String getReportingCompletionRateApi( Long entityId, String entityType )
    {
        LOGGER.info( "Fetching Completion Rate Graph for entityType :{} and entityId : {}", entityType, entityId );

        String json = null;

        List<List<Object>> completionRate = dashboardGraphManagement.getCompletionRate( entityId, entityType );
        json = new Gson().toJson( completionRate );
        return json;

    }


    @RequestMapping ( value = "/getspsstats", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for Survey Stats Graph")
    public String getReportingSpsStats( Long entityId, String entityType )
    {
        LOGGER.info( "Fetching SPS stats Graph for entityType :{} and entityId : {}", entityType, entityId );

        String json = null;
        List<List<Object>> spsStats = dashboardGraphManagement.getSpsStatsGraph( entityId, entityType );
        json = new Gson().toJson( spsStats );
        return json;

    }

    @RequestMapping ( value = "/npsstats", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for nps Graph")
    public String getReportingNpsStats( Long entityId, String entityType )
    {
        LOGGER.info( "Fetching NPS stats Graph for entityType :{} and entityId : {}",entityType,entityId );

        String json = null;
        List<List<Object>> npsStats = dashboardGraphManagement.getNpsStatsGraph( entityId, entityType );
        json = new Gson().toJson( npsStats );
        return json;

    }


    @RequestMapping ( value = "/getspsfromoverview", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for Overview ")
    public String getSpsStatsFromOverview( Long entityId, String entityType ) throws NonFatalException
    {
        LOGGER.info( "Fetching sps for overview for entityType : {} and entityId: {}", entityType, entityId );

        Map<String, Object> overviewMap = overviewManagement.fetchSpsAllTime( entityId, entityType );
        return new Gson().toJson( overviewMap );
    }


    @RequestMapping ( value = "/getalltimefromoverview", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for Overview ")
    public String getAllTimeDataOverview( Long entityId, String entityType ) throws NonFatalException
    {
        LOGGER.info( "Fetching all time data for overview" );

        Map<String, Object> overviewMap = overviewManagement.fetchAllTimeOverview( entityId, entityType );
        return new Gson().toJson( overviewMap );
    }


    @RequestMapping ( value = "/getsurveystatsreport", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for Survey Stats Reporting ")
    public String getReportingSurveyStatsReport( Long entityId, String entityType )
    {
        LOGGER.info( "Fetching Survey Stats Graph" );

        String json = null;
        List<List<Object>> surveyStatsReport = reportingDashboardManagement.getSurveyStatsReport( entityId, entityType );
        json = new Gson().toJson( surveyStatsReport );
        return json;
    }


    @RequestMapping ( value = "/getrecentactivityforreporting", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Recent Activity Reporting ")
    public String getRecentActivity( Long entityId, String entityType, int startIndex, int batchSize ) throws NonFatalException
    {
        LOGGER.info( "Fetching Recent Activity Reporting" );

        String json = null;
        List<List<Object>> recentActivityList = reportingDashboardManagement.getRecentActivityList( entityId, entityType,
                startIndex, batchSize );
        json = new Gson().toJson( recentActivityList );
        return json;
    }


    @RequestMapping ( value = "/getaccountstatisticsreportstatus", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch latest record for account statistics report. ")
    public String getAccountStatisticsRecentActivity( Long reportId ) throws NonFatalException
    {
        LOGGER.info( "Fetching latest record for account statistics report." );

        String json = null;
        Object accountStatisticsStatus = reportingDashboardManagement.getAccountStatisticsRecentActivity( reportId );
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Hibernate4Module());
        try {
            //json = new Gson().toJson( accountStatisticsStatus );
			json = mapper.writeValueAsString(accountStatisticsStatus);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while parsing object in getaccountstatisticsreportstatus " , e);
			throw new NonFatalException(e.getMessage());
		}
        return json;
    }


    @RequestMapping ( value = "/getuseradoptionreportsforreporting", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch User Adoption Report For Reporting ")
    public String getUserAdoption( Long entityId, String entityType )
    {
        LOGGER.info( "Fetch User Adoption Report For Reporting" );

        String json = null;
        List<List<Object>> userAdoptionList = reportingDashboardManagement.getUserAdoptionReport( entityId, entityType );
        json = new Gson().toJson( userAdoptionList );
        return json;
    }


    @RequestMapping ( value = "/getmaxquestionforcompany", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Max Question For Company Report For Reporting")
    public int getCompanyMaxQuestion( String entityType, Long entityId, Timestamp startDate, Timestamp endDate )
    {
        LOGGER.info( "Fetch Max Question For Company Report For Reporting" );
        return reportingDashboardManagement.getMaxQuestionForSurveyResultsReport( entityType, entityId, startDate, endDate );
    }


    @RequestMapping ( value = "/getsurveyresultsreport", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Survey Results Report For Entity Type.")
    public String getSurveyResultsReport( String entityType, Long entityId, Timestamp startDate, Timestamp endDate,
                                          int startIndex, int batchSize )
    {
        LOGGER.info( "Fetch Survey Results Report For Entity Type." );
        List<SurveyResultsReportVO> surveyResultsReportList = reportingDashboardManagement
                .getSurveyResultsReport( entityType, entityId, startDate, endDate, startIndex, batchSize );
        return new Gson().toJson( surveyResultsReportList );
    }


    @RequestMapping ( value = "/getcompanyuserreportsforreporting", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Company User Report For Reporting ")
    public String getCompanyUserReport( Long entityId, String entityType )
    {
        LOGGER.info( "Fetch User Adoption Report For Reporting" );

        String json = null;
        List<List<Object>> userAdoptionList = reportingDashboardManagement.getCompanyUserReport( entityId, entityType );
        json = new Gson().toJson( userAdoptionList );
        return json;
    }


    @RequestMapping ( value = "/getsurveytransactionreportforreporting", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Survey Transaction Report For Reporting ")
    public String getSurveyTransactionReport( Long entityId, String entityType, int month, int year )

    {
        LOGGER.info( "Fetch Survey Transaction Report For Reporting" );

        String json = null;
        List<SurveyTransactionReportVO> surveyTransactionList = reportingDashboardManagement.getSurveyTransactionReport( entityId,
                entityType, month, year );
        json = new Gson().toJson( surveyTransactionList );
        return json;
    }


    @RequestMapping ( value = "/getuserrankingreportforreporting", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch User Ranking Report For Reporting ")
    public String getUserRankingReport( Long entityId, String entityType, int year, int month, int type )

    {
        LOGGER.info( "Fetch Survey Transaction Report For Reporting" );

        String json = null;
        List<List<Object>> userRankingList = null;
        if ( type == 1 ) {
            userRankingList = reportingDashboardManagement.getUserRankingReportForYear( entityId, entityType, year );
        } else {
            userRankingList = reportingDashboardManagement.getUserRankingReportForMonth( entityId, entityType, year, month );
        }
        json = new Gson().toJson( userRankingList );
        return json;
    }


    @RequestMapping ( value = "/getmonthdataoverviewfordashboard", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Month Data For Overview ")
    public String getMonthDataOverviewForDashboard( Long entityId, String entityType, int month, int year )
            throws NonFatalException
    {
        LOGGER.info( "Fetch Month Data For Overview " );

        String json = null;

        Map<String, Object> overviewMap = overviewManagement.fetchOverviewDetailsBasedOnMonth( entityId, entityType, month,
                year );
        json = new Gson().toJson( overviewMap );
        return json;
    }


    @RequestMapping ( value = "/getyeardataoverviewfordashboard", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetching Year Data For Overview")
    public String getYearDataOverviewForDashboard( Long entityId, String entityType, int year ) throws NonFatalException
    {
        LOGGER.info( "Fetching Year Data For Overview" );
        Map<String, Object> overviewMap = overviewManagement.fetchOverviewDetailsBasedOnYear( entityId, entityType, year );
        return new Gson().toJson( overviewMap );
    }


    @RequestMapping ( value = "/getuserrankingforthisyear")
    @ApiOperation ( value = "Fetch User Ranking for this year")
    public String getUserRankingForThisYear( Long entityId, String entityType, int year, int startIndex, int batchSize )

    {
        LOGGER.info( "Fetching User Ranking for this year started" );

        String json = null;

        List<List<Object>> userRankingList;

        userRankingList = reportingDashboardManagement.getUserRankingThisYear( entityType, entityId, year, startIndex,
                batchSize );

        json = new Gson().toJson( userRankingList );

        LOGGER.info( "Fetching User Ranking for this year ended" );

        return json;
    }


    @RequestMapping ( value = "/getuserrankingforthismonth")
    @ApiOperation ( value = "Fetch User Ranking for this year")
    public String getUserRankingForThisMonth( Long entityId, String entityType, int month, int year, int startIndex,
                                              int batchSize )
    {
        LOGGER.info( "Fetching User Ranking for this month" );

        String json = null;

        List<List<Object>> userRankingList;

        userRankingList = reportingDashboardManagement.getUserRankingThisMonth( entityType, entityId, month, year, startIndex,
                batchSize );

        json = new Gson().toJson( userRankingList );

        return json;
    }


    @RequestMapping ( value = "/getuserrankingforpastyear")
    @ApiOperation ( value = "Fetch User Ranking for this year")
    public String getUserRankingForPastYear( Long entityId, String entityType, int year, int startIndex, int batchSize )
    {
        LOGGER.info( "Fetching User Ranking for past year" );

        String json = null;

        List<List<Object>> userRankingList;

        userRankingList = reportingDashboardManagement.getUserRankingPastYear( entityType, entityId, year, startIndex,
                batchSize );

        json = new Gson().toJson( userRankingList );

        return json;
    }


    @RequestMapping ( value = "/getuserrankingforpastmonth")
    @ApiOperation ( value = "Fetch User Ranking for this year")
    public String getUserRankingForPastMonth( Long entityId, String entityType, int month, int year, int startIndex,
                                              int batchSize )
    {
        LOGGER.info( "Fetching User Ranking for past month" );

        String json = null;

        List<List<Object>> userRankingList;

        userRankingList = reportingDashboardManagement.getUserRankingPastMonth( entityType, entityId, month, year, startIndex,
                batchSize );

        json = new Gson().toJson( userRankingList );

        return json;
    }


    @RequestMapping ( value = "/getuserrankingforpastyears")
    @ApiOperation ( value = "Fetch User Ranking for past years")
    public String getUserRankingForPastYears( Long entityId, String entityType, int startIndex, int batchSize )
    {
        LOGGER.info( "Fetching User Ranking for past years" );

        String json = null;

        List<List<Object>> userRankingList;

        userRankingList = reportingDashboardManagement.getUserRankingPastYears( entityType, entityId, startIndex, batchSize );

        json = new Gson().toJson( userRankingList );

        return json;
    }


    @RequestMapping ( value = "/getuserrankingrankcountthisyear", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch User Ranking Rank Count for this year")
    public String getUserRankingRankCountForThisYear( Long userId, Long entityId, String entityType, int year, int batchSize )
            throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For this year" );

        String json = null;
        Map<String, Object> rankingCountStartIndex;
        rankingCountStartIndex = reportingDashboardManagement.fetchRankingRankCountThisYear( userId, entityId, entityType, year,
                batchSize );
        json = new Gson().toJson( rankingCountStartIndex );

        return json;

    }


    @RequestMapping ( value = "/getuserrankingrankcountthismonth", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch User Ranking Rank Count for this month")
    public String getUserRankingRankCountForThisMonth( Long userId, Long entityId, String entityType, int year, int month,
                                                       int batchSize ) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For this month" );

        String json = null;
        Map<String, Object> rankingCountStartIndex;
        rankingCountStartIndex = reportingDashboardManagement.fetchRankingRankCountThisMonth( userId, entityId, entityType,
                year, month, batchSize );
        json = new Gson().toJson( rankingCountStartIndex );

        return json;

    }


    @RequestMapping ( value = "/getuserrankingrankcountpastyear", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch User Ranking Rank Count for past year")
    public String getUserRankingRankCountForPastYear( Long userId, Long entityId, String entityType, int year, int batchSize )
            throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For past year" );

        String json = null;
        Map<String, Object> rankingCountStartIndex;
        rankingCountStartIndex = reportingDashboardManagement.fetchRankingRankCountPastYear( userId, entityId, entityType, year,
                batchSize );
        json = new Gson().toJson( rankingCountStartIndex );

        return json;

    }


    @RequestMapping ( value = "/getuserrankingrankcountpastmonth", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch User Ranking Rank Count for past month")
    public String getUserRankingRankCountForPastMonth( Long userId, Long entityId, String entityType, int year, int month,
                                                       int batchSize ) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For past month" );

        String json = null;
        Map<String, Object> rankingCountStartIndex;
        rankingCountStartIndex = reportingDashboardManagement.fetchRankingRankCountPastMonth( userId, entityId, entityType,
                year, month, batchSize );
        json = new Gson().toJson( rankingCountStartIndex );

        return json;

    }


    @RequestMapping ( value = "/getuserrankingrankcountpastyears", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch User Ranking Rank Count for past years")
    public String getUserRankingRankCountForPastYears( Long userId, Long entityId, String entityType, int batchSize )
            throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Rank And Count For past year" );

        String json = null;
        Map<String, Object> rankingCountStartIndex;
        rankingCountStartIndex = reportingDashboardManagement.fetchRankingRankCountPastYears( userId, entityId, entityType,
                batchSize );
        json = new Gson().toJson( rankingCountStartIndex );

        return json;

    }


    @RequestMapping ( value = "/getuserrankingcountthisyear", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch User Ranking Count for this year")
    public String getUserRankingCountForThisYear( Long entityId, String entityType, int year, int batchSize )
            throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For this year." );

        String json = null;
        Map<String, Object> rankingCountStartIndex;
        rankingCountStartIndex = reportingDashboardManagement.fetchRankingCountThisYear( entityId, entityType, year,
                batchSize );
        json = new Gson().toJson( rankingCountStartIndex );

        return json;

    }


    @RequestMapping ( value = "/getuserrankingcountthismonth", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch User Ranking Count for this month")
    public String getUserRankingCountForThisMonth( Long entityId, String entityType, int year, int month, int batchSize )
            throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For this month" );

        String json = null;
        Map<String, Object> rankingCountStartIndex;
        rankingCountStartIndex = reportingDashboardManagement.fetchRankingCountThisMonth( entityId, entityType, year, month,
                batchSize );
        json = new Gson().toJson( rankingCountStartIndex );

        return json;

    }


    @RequestMapping ( value = "/getuserrankingcountpastyear", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch User Ranking Count for past year")
    public String getUserRankingCountForPastYear( Long entityId, String entityType, int year, int batchSize )
            throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For past year" );

        String json = null;
        Map<String, Object> rankingCountStartIndex;
        rankingCountStartIndex = reportingDashboardManagement.fetchRankingCountPastYear( entityId, entityType, year,
                batchSize );
        json = new Gson().toJson( rankingCountStartIndex );

        return json;

    }


    @RequestMapping ( value = "/getuserrankingcountpastmonth", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch User Ranking Count for past month")
    public String getUserRankingCountForPastMonth( Long entityId, String entityType, int year, int month, int batchSize )
            throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For past month" );

        String json = null;
        Map<String, Object> rankingCountStartIndex;
        rankingCountStartIndex = reportingDashboardManagement.fetchRankingCountPastMonth( entityId, entityType, year, month,
                batchSize );
        json = new Gson().toJson( rankingCountStartIndex );

        return json;

    }


    @RequestMapping ( value = "/getuserrankingcountpastyears", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch User Ranking Count for past years")
    public String getUserRankingCountForPastYears( Long entityId, String entityType, int batchSize ) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For past years" );

        String json = null;
        Map<String, Object> rankingCountStartIndex = reportingDashboardManagement.fetchRankingCountPastYears( entityId,
                entityType, batchSize );
        json = new Gson().toJson( rankingCountStartIndex );

        return json;

    }


    @RequestMapping ( value = "/getscorestatsoverall", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Score Stats Overall")
    public String getScoreStatsOverall( Long entityId, String entityType, int currentMonth, int currentYear )
    {

        LOGGER.info( "Fetching Score Stats Overall" );

        String json = null;
        List<List<Object>> scoreStatsOverall = reportingDashboardManagement.getScoreStatsForOverall( entityId, entityType,
                currentMonth, currentYear );
        json = new Gson().toJson( scoreStatsOverall );
        return json;
    }


    @RequestMapping ( value = "/getscorestatsquestion", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Score Stats Question")
    public String getScoreStatsQuestion( Long entityId, String entityType, int currentMonth, int currentYear, Long userId )
        throws InvalidInputException
    {

        LOGGER.debug( "Fetching Score Stats for Questions." );

        String json = null;
        List<List<Object>> scoreStatsQuestion;
        scoreStatsQuestion = reportingDashboardManagement.getScoreStatsForQuestion( entityId, entityType, currentMonth,
                currentYear, userId );
        json = new Gson().toJson( scoreStatsQuestion );
        return json;
    }


    @RequestMapping ( value = "/getentitiesoptedfordigestmail", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch the list of companies that have digest Mail enabled")
    public String getCompaniesOptedForDigestMail( String profileLevel, int startIndex, int batchSize ) throws NonFatalException
    {

        LOGGER.info( "Fetching the list of companies that have digest Mail enabled" );

        String json = null;
        List<DigestRequestData> digestRequestData;
        digestRequestData = reportingDashboardManagement.getEntitiesOptedForDigestMail( startIndex, batchSize, profileLevel );
        json = new Gson().toJson( digestRequestData );
        return json;
    }


    @RequestMapping ( value = "/buildmonthlydigestaggregate", method = RequestMethod.GET)
    @ApiOperation ( value = "Build the monthly digest aggregate for a hierarchy for a given month")
    public String buildMonthlyDigestAggregate( String  profileLevel, long entityId, String entityName, int monthUnderConcern, int year )
        throws NonFatalException
    {

        LOGGER.info( "Building the monthly digest aggregate for a company for a given month" );
        return new Gson().toJson( reportingDashboardManagement.prepareMonthlyDigestMailData( profileLevel, entityId, entityName, monthUnderConcern, year ) );
    }

    @RequestMapping ( value = "/getcompanydetailsreport", method = RequestMethod.GET)
    @ApiOperation ( value = "Social Survey Admin level report to fetch Company Details for all companies.")
    public String getCompanyDetailsReport( String entityType, Long entityId, int startIndex, int batchSize )
            throws InvalidInputException
    {
        LOGGER.info( "Social Survey Admin level report to fetch Company Details for all companies." );
        List<CompanyDetailsReport> companyDetailsReportList = reportingDashboardManagement.getCompanyDetailsReport( entityId,
                startIndex, batchSize );
        return new Gson().toJson( companyDetailsReportList );
    }

    @RequestMapping ( value = "/getallactiveenterprisecompanies", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch the list of active enterprise companies")
    public String getAllActiveEnterpriseCompanies()
    {
        LOGGER.info( "Fetching the list of active enterprise companies" );
        List<CompanyView> allActiveCompanies = organizationManagementService.getAllActiveEnterpriseCompanyViews();
        return new Gson().toJson( allActiveCompanies );
    }


    @RequestMapping ( value = "/getcompanieswithnotransactioninpastndays", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch the list of active enterprise companies with no transactions in past N days")
    public String getCompaniesWithNoTransactionInPastNDays( int noOfDays )
    {
        LOGGER.info( "Fetching the list of active enterprise companies with no transactions in past N days" );
        List<CompanyView> allActiveCompanies = organizationManagementService.getAllActiveEnterpriseCompanyViews();
        List<CompanyView> companiesWithNoTransactions = activityManagementService
                .getCompaniesWithNoTransactionInPastNDays( allActiveCompanies, noOfDays );
        return new Gson().toJson( companiesWithNoTransactions );
    }


    @RequestMapping ( value = "/validatesurveystatsforcompanies", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch the company ids with low sent surveys ")
    public String validateSurveyStatsForCompanies()
    {
        LOGGER.info( "Fetching the company ids with low sent surveys" );
        List<CompanySurveyStatusStats> companySurveyStatusStatsList = activityManagementService
                .getSurveyStatusStatsForPastDay();
        List<Long> companyIdsForLessSurveyAlerts = activityManagementService
                .validateSurveyStatsForCompanies( companySurveyStatusStatsList );
        return new Gson().toJson( companyIdsForLessSurveyAlerts );
    }


    @RequestMapping ( value = "/getsurveystatusstatsforpastonemonth", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch the survey stats for companies for the past month ")
    public String getSurveyStatusStatsForPastOneMonth()
    {
        LOGGER.info( "Fetch the survey stats for companies for the past month" );
        Map<Long, Long> companySurveyStatsCountsMap = activityManagementService.getSurveyStatusStatsForPastOneMonth();
        return new Gson().toJson( companySurveyStatsCountsMap );
    }


    @RequestMapping ( value = "/getcompanyactiveusercountforpastday", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch the number of active users for a company for past day ")
    public String getCompanyActiveUserCountForPastDay()
    {
        LOGGER.info( "Fetching the number of active users for a company for past day" );
        List<CompanyActiveUsersStats> companyActiveUserCounts = activityManagementService.getCompanyActiveUserCountForPastDay();
        return new Gson().toJson( companyActiveUserCounts );
    }

    @RequestMapping ( value = "/getincompletesurveys", method = RequestMethod.GET)
    @ApiOperation ( value = "get incomplete surveys")
    public String getIncompleteSurveys( Long entityId, String entityType, Timestamp startDate, Timestamp endDate,
        int startIndex, int batchSize ) throws NonFatalException
    {

        LOGGER.info( "Fetching list of incomplete surveys for entityType : {} , entityId : {}", entityType, entityId );
        return new Gson().toJson( reportingDashboardManagement.getIncompleteSurvey( entityId, entityType, startDate, endDate,
            startIndex, batchSize ) );
    }
    
    @RequestMapping ( value = "/gettotaltransactioncountforpast5days", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch the transaction counts for companies for  past 5 days ")
    public String getTotalTransactionCountForPast5Days()
    {
        LOGGER.info( "Fetch total survey counts for companies for the n days" );
        Map<Long, Long> companySurveyStatsCountsMap = activityManagementService.getTotalTransactionCountForPast5DaysForCompanies();
        return new Gson().toJson( companySurveyStatsCountsMap );
    }

    @RequestMapping ( value = "/nps/week/month", method = RequestMethod.GET)
    @ApiOperation ( value = "get nps report for a week or month")
    public String getNpsReportForWeekOrMonth( int week, int month, long companyId, int year, int type ) throws InvalidInputException
    {
        String json = null;
        if(type == 1){
            LOGGER.info( "Fetching nps report for week {} for company {}", week, companyId );
            json = new Gson().toJson( reportingDashboardManagement.getNpsReportForAWeek( companyId, week, year ) );
        }
        else if(type == 2){
            LOGGER.info( "Fetching nps report for month {} for company {}", month, companyId );
            json = new Gson().toJson( reportingDashboardManagement.getNpsReportForAMonth( companyId, month, year ) );
        }
        return json;
    }

    @RequestMapping ( value = "/gettransactioncountforpast3days", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch the transaction counts for companies for  pasr 3 days")
    public String getTransactionCountForPast3Days()
    {
        LOGGER.info( "Fetch total survey counts for companies for the n days" );
        Map<Long, Long> companySurveyStatsCountsMap = activityManagementService.getTransactionCountForPast3Days();
        return new Gson().toJson( companySurveyStatsCountsMap );
    }
    
    
    @RequestMapping ( value = "/getsendsurveycountforpast5days", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch the transaction counts for companies for past 5 days ")
    public String getSendSurveyCountForPast5Days()
    {
        LOGGER.info( "Fetch sent survey counts for companies for past 5 days" );
        Map<Long, Long> companySurveyStatsCountsMap = activityManagementService.getSendSurveyCountForPast5Days();
        return new Gson().toJson( companySurveyStatsCountsMap );
    }
    
    @RequestMapping ( value = "/getsurvestatsforpast7daysforallcompanies", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch  survey stats for companies for past 7 days")
    public String getSurveStatsForPast7daysForAllCompanies()
    {
        LOGGER.info( "Fetch  survey stats for companies for past 7 days" );
        Map<Long, List<CompanySurveyStatusStats>> companySurveyStatsMap = activityManagementService.getSurveStatsForPast7daysForAllCompanies();
        LOGGER.info( "Fetch  survey stats for companies for past 7 days result is "  + companySurveyStatsMap );
        return new Gson().toJson( companySurveyStatsMap );
    }
    
    @RequestMapping ( value = "/getsurvestatsforlasttoLatweekforallcompanies", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch  survey stats for companies for last to last week")
    public String getSurveStatsForLastToLatWeekForAllCompanies()
    {
        LOGGER.info( "Fetch  survey stats for companies for last to last week" );
        Map<Long, List<CompanySurveyStatusStats>> companySurveyStatsMap = activityManagementService.getSurveStatsForLastToLatWeekForAllCompanies();
        return new Gson().toJson( companySurveyStatsMap );
    }
    
    @RequestMapping ( value = "/getcompletedsurveycountforpastndays", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch the transaction counts for companies for  past n days ")
    public String getCompletedSurveyCountForpastNDays()
    {
        LOGGER.info( "Fetch total survey counts for companies for the n days" );
        Map<Long, Long> companySurveyStatsCountsMap = activityManagementService.getCompletedSurveyCountForPast3DaysForCompanies();
        return new Gson().toJson( companySurveyStatsCountsMap );
    }

    @RequestMapping( value = "/fileUpload/{fileUploadId}/status/{status}", method = RequestMethod.PUT)
    @ApiOperation(value = "Updates the status column of fileUpload table")
    public ResponseEntity<FileUploadResponse> updateFileUploadStatus(@PathVariable("fileUploadId") long fileUploadId,
                                                                     @PathVariable("status") int status) {
        FileUploadResponse fileUploadResponse = new FileUploadResponse();
        ResponseEntity<FileUploadResponse> responseEntity ;
        LOGGER.info("Updating the FileUpload of id " + fileUploadId + "to status" + status );
        try {
            int recordsUpdated = reportingDashboardManagement.updateFileUploadStatus(fileUploadId, status);
            if(recordsUpdated != 1) {
                fileUploadResponse.setRecordsUpdated(-1);
                fileUploadResponse.setMessage("Records were not updated incorrectly !!!");
                responseEntity  = new ResponseEntity<>(fileUploadResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                fileUploadResponse.setRecordsUpdated(recordsUpdated);
                fileUploadResponse.setMessage("Record updation success!!!");
                responseEntity  = new ResponseEntity<>(fileUploadResponse, HttpStatus.OK);
            }
        } catch (InvalidInputException e) {
            fileUploadResponse.setRecordsUpdated(0);
            fileUploadResponse.setMessage(e.getMessage());
            responseEntity = new ResponseEntity<>(fileUploadResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @RequestMapping( value = "/fileUpload/{fileUploadId}/status/{status}", method = RequestMethod.POST)
    @ApiOperation(value = "Updates the status column and location of fileUpload table")
    public ResponseEntity<FileUploadResponse> updateFileUploadStatusAndLocation(@PathVariable("fileUploadId") long fileUploadId,
                                                                                @PathVariable("status") int status,
                                                                                @RequestBody String fileName) {
        FileUploadResponse fileUploadResponse = new FileUploadResponse();
        ResponseEntity<FileUploadResponse> responseEntity;
        try {
            int recordsUpdated = reportingDashboardManagement.updateFileUploadStatusAndFileName(fileUploadId, status, fileName);
            if (recordsUpdated != 1) {
                fileUploadResponse.setRecordsUpdated(-1);
                fileUploadResponse.setMessage("Records were not updated incorrectly !!!");
                responseEntity = new ResponseEntity<>(fileUploadResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                fileUploadResponse.setRecordsUpdated(recordsUpdated);
                fileUploadResponse.setMessage("Record updation success!!!");
                responseEntity = new ResponseEntity<>(fileUploadResponse, HttpStatus.OK);
            }
        } catch (InvalidInputException e) {
            fileUploadResponse.setRecordsUpdated(0);
            fileUploadResponse.setMessage(e.getMessage());
            responseEntity = new ResponseEntity<>(fileUploadResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }
    
    
    @RequestMapping ( value = "/branchranking/month/year", method = RequestMethod.GET)
    @ApiOperation ( value = "get branch ranking report for month and year.")
    public String getBranchRankingReport( long companyId, int month, int year, int type ) throws InvalidInputException
    {
        String json = null;
        if(type == 1){
            LOGGER.info( "Fetching branch ranking report {} for company {}", month, companyId );
            json = new Gson().toJson( reportingDashboardManagement.getBranchRankingReportForMonth( companyId, month, year ) );
        }
        else if(type == 2){
            LOGGER.info( "Fetching nps report for month {} for company {}", month, companyId );
            json = new Gson().toJson( reportingDashboardManagement.getBranchRankingReportForYear( companyId, year ) );
        }
        return json;
    }
    
    @RequestMapping ( value = "/trxcount/agent", method = RequestMethod.GET)
    @ApiOperation ( value = "get received count for agents.")
    public List<SurveyInvitationEmailCountMonth> getReceivedCountsMonth(long startDateInGmt, long endDateInGmt,
    		int startIndex, int batchSize) {
    	LOGGER.info("API call for fetching transaction received count for each agent with date range.");
    	try {
    		List<SurveyInvitationEmailCountMonth> mailCount = reportingDashboardManagement.getReceivedCountsMonth(startDateInGmt,endDateInGmt,
    				startIndex,batchSize);
    		LOGGER.info("Fetched {} no of records",mailCount.size());
    		return mailCount;
		} catch (ParseException e) {
			LOGGER.error("Error getting received count.",e);
		} 
    	return null;
    }
    @RequestMapping( value = "/agentEmailCountsMonth", method = RequestMethod.POST)
    @ApiOperation(value = "Save the agent email counts data for month.")
    public ResponseEntity<Boolean> saveEmailCountMonthData(@RequestBody List<SurveyInvitationEmailCountMonth> agentEmailCountsMonth) {
    	LOGGER.info("API call to save agent email count data for month");
    	boolean status = false;
    	if(agentEmailCountsMonth != null && !agentEmailCountsMonth.isEmpty() && agentEmailCountsMonth.size() > 0) {
    		status = reportingDashboardManagement.saveEmailCountMonthData(agentEmailCountsMonth);
    	} else {
    		LOGGER.info("No data found to save for invitation mail count month.");
    	}
    	ResponseEntity<Boolean> responseEntity = null;
    	if(status) {
    		responseEntity = new ResponseEntity<Boolean>(status,HttpStatus.OK);
    	} else {
    		responseEntity = new ResponseEntity<Boolean>(status,HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    	return responseEntity;
    }
    @RequestMapping( value = "/emailreport/month/year", method = RequestMethod.GET)
    @ApiOperation(value = "Get email report for month and year value.")
    public String getSurveyInvitationEmailReport(long companyId,int month,int year) {
    	LOGGER.info("API call to get survey invitation email report for month.");
    	return new Gson().toJson( reportingDashboardManagement.getSurveyInvitationEmailReportForMonth( companyId, month, year ) );
    }
    
    @RequestMapping( value = "/surveyinvitationemailalltime", method = RequestMethod.GET)
    @ApiOperation(value = "Get all time data for survey initation mail.")
    public List<SurveyInvitationEmailCountMonth> getAllTimeDataForSurveyInvitationMail(int startIndex, int batchSize) {
    	LOGGER.info("API call to get survey invitation email counts for all time data.");
    	return reportingDashboardManagement.getAllTimeDataForSurveyInvitationMail( startIndex, batchSize );
    }
    
    
    @RequestMapping( value = "/surveyinvitationemail/month/year", method = RequestMethod.GET)
    @ApiOperation(value = "Get data for survey initation mail for month and year.")
    public List<SurveyInvitationEmailCountVO> getDataForEmailReport(int month, int year, long companyId) {
    	LOGGER.info("API call to get survey invitation email counts for month and year value.{}-{}",month,year);
    	return reportingDashboardManagement.getDataForSurveyInvitationMail(month,year,companyId ) ;
    }
    
    @RequestMapping ( value = "/getrecentactivityforsocialmonitorreporting", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Recent Activity for Social Monitor Reporting ")
    public String getRecentActivityForSocialMonitor( Long entityId, String entityType, int startIndex, int batchSize ) throws NonFatalException
    {
        LOGGER.info( "Fetching Recent Activity for Social Monitor Reporting" );

        String json = null;
        List<List<Object>> recentActivityList = reportingDashboardManagement.getRecentActivityListForSocialMonitor( entityId, entityType,
                startIndex, batchSize );
        json = new Gson().toJson( recentActivityList );
        return json;
    }
}
