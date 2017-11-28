package com.realtech.socialsurvey.api.controllers;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.entities.CompanyDetailsReport;
import com.realtech.socialsurvey.core.entities.CompanyDigestRequestData;
import com.realtech.socialsurvey.core.entities.SurveyResultsReportVO;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.reportingmanagement.DashboardGraphManagement;
import com.realtech.socialsurvey.core.services.reportingmanagement.OverviewManagement;
import com.realtech.socialsurvey.core.services.reportingmanagement.ReportingDashboardManagement;
import com.wordnik.swagger.annotations.ApiOperation;


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


    @RequestMapping ( value = "/getcompletionrate", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for Completion Rate Graph")
    public String getReportingCompletionRateApi( Long entityId, String entityType ) 
    {
        LOGGER.info( "Fetching Completion Rate Graph for entityType :{} and entityId : {}",entityType,entityId );

        String json = null;
        
        List<List<Object>> completionRate = dashboardGraphManagement.getCompletionRate( entityId, entityType );
        json = new Gson().toJson( completionRate );
        return json;

    }


    @RequestMapping ( value = "/getspsstats", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for Survey Stats Graph")
    public String getReportingSpsStats( Long entityId, String entityType ) 
    {
    	LOGGER.info( "Fetching SPS stats Graph for entityType :{} and entityId : {}",entityType,entityId );

        String json = null;
        List<List<Object>> spsStats = dashboardGraphManagement.getSpsStatsGraph( entityId, entityType );
        json = new Gson().toJson( spsStats );
        return json;

    }


    @RequestMapping ( value = "/getspsfromoverview", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for Overview ")
    public String getSpsStatsFromOverview( Long entityId, String entityType ) throws NonFatalException
    {
        LOGGER.info( "Fetching sps for overview for entityType : {} and entityId: {}",entityType,entityId );

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
        json = new Gson().toJson( accountStatisticsStatus );
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

    
    @RequestMapping( value = "/getmaxquestionforcompany", method= RequestMethod.GET)
    @ApiOperation( value = "Fetch Max Question For Company Report For Reporting")
    public int getCompanyMaxQuestion(String entityType,Long entityId, Timestamp startDate, Timestamp endDate) 
    {
        LOGGER.info( "Fetch Max Question For Company Report For Reporting");
        return reportingDashboardManagement.getMaxQuestionForSurveyResultsReport(entityType, entityId, startDate, endDate );
    }
    
    @RequestMapping( value = "/getsurveyresultsreport", method= RequestMethod.GET)
    @ApiOperation( value = "Fetch Survey Results Report For Entity Type.")
    public String getSurveyResultsReport(String entityType, Long entityId, Timestamp startDate, Timestamp endDate , int startIndex , int batchSize)
    {
        LOGGER.info( "Fetch Survey Results Report For Entity Type.");
        Map<String,SurveyResultsReportVO> surveyResultsReportList = reportingDashboardManagement.getSurveyResultsReport(entityType,entityId,startDate,endDate,startIndex,batchSize);
        return new Gson().toJson(surveyResultsReportList);
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
    public String getSurveyTransactionReport( Long entityId, String entityType, Timestamp startDate, Timestamp endDate )
        
    {
        LOGGER.info( "Fetch Survey Transaction Report For Reporting" );

        String json = null;
        List<List<Object>> surveyTransactionList = reportingDashboardManagement.getSurveyTransactionReport( entityId,
            entityType, startDate, endDate );
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

        List<List<Object>> userRankingList ;

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

        List<List<Object>> userRankingList ;

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
        Map<String, Object> rankingCountStartIndex ;
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
        Map<String, Object> rankingCountStartIndex = reportingDashboardManagement.fetchRankingCountPastYears( entityId, entityType, batchSize );
        json = new Gson().toJson( rankingCountStartIndex );

        return json;

    }


    @RequestMapping ( value = "/getscorestatsoverall", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Score Stats Overall")
    public String getScoreStatsOverall( Long entityId, String entityType, int currentMonth, int currentYear )
    {

        LOGGER.info( "Fetching Score Stats Overall" );

        String json = null;
        List<List<Object>> scoreStatsOverall = reportingDashboardManagement.getScoreStatsForOverall( entityId, entityType, currentMonth,
            currentYear );
        json = new Gson().toJson( scoreStatsOverall );
        return json;
    }


    @RequestMapping ( value = "/getscorestatsquestion", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Score Stats Question")
    public String getScoreStatsQuestion( Long entityId, String entityType, int currentMonth, int currentYear )
    {

        LOGGER.debug( "Fetching Score Stats for Questions." );

        String json = null;
        List<List<Object>> scoreStatsQuestion ;
        scoreStatsQuestion = reportingDashboardManagement.getScoreStatsForQuestion( entityId, entityType, currentMonth,
            currentYear );
        json = new Gson().toJson( scoreStatsQuestion );
        return json;
    }


    @RequestMapping ( value = "/getcompaniesoptedfordigestmail", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch the list of companies that have digest Mail enabled")
    public String getCompaniesOptedForDigestMail( int startIndex, int batchSize )
    {

        LOGGER.info( "Fetching the list of companies that have digest Mail enabled" );

        String json = null;
        List<CompanyDigestRequestData> digestRequestData;
        digestRequestData = reportingDashboardManagement.getCompaniesOptedForDigestMail( startIndex, batchSize );
        json = new Gson().toJson( digestRequestData );
        return json;
    }


    @RequestMapping ( value = "/buildmonthlydigestaggregate", method = RequestMethod.GET)
    @ApiOperation ( value = "Build the monthly digest aggregate for a company for a given month")
    public String buildMonthlyDigestAggregate( long companyId, String companyName, int monthUnderConcern, int year,
        String recipientMail ) throws NonFatalException
    {

        LOGGER.info( "Building the monthly digest aggregate for a company for a given month" );
        return new Gson().toJson( reportingDashboardManagement.prepareMonthlyDigestMailData( companyId, companyName,
            monthUnderConcern, year, recipientMail ) );
    }

	@RequestMapping(value = "/getcompanydetailsreport", method = RequestMethod.GET)
	@ApiOperation(value = "Social Survey Admin level report to fetch Company Details for all companies.")
	public String getCompanyDetailsReport(String entityType, Long entityId, int startIndex, int batchSize) throws InvalidInputException {
		LOGGER.info("Social Survey Admin level report to fetch Company Details for all companies.");
		List<CompanyDetailsReport> companyDetailsReportList = reportingDashboardManagement
				.getCompanyDetailsReport(entityId, startIndex, batchSize);
		return new Gson().toJson(companyDetailsReportList);
	}
    
    @RequestMapping ( value = "/getincompletesurveys", method = RequestMethod.GET)
    @ApiOperation ( value = "get incomplete surveys")
    public String getIncompleteSurveys( Long entityId, String entityType, Timestamp startDate, Timestamp endDate,
        int startIndex , int batchSize ) throws NonFatalException
    {

        LOGGER.info( "Fetching list of incomplete surveys for entityType : {} , entityId : {}",entityType,entityId );
        return new Gson().toJson( reportingDashboardManagement.getIncompleteSurvey( entityId, entityType,
            startDate, endDate, startIndex, batchSize ) );
    }
}
