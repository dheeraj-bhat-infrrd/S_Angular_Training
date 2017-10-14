package com.realtech.socialsurvey.api.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.CompanyDigestRequestData;
import com.realtech.socialsurvey.core.entities.OverviewBranch;
import com.realtech.socialsurvey.core.entities.OverviewCompany;
import com.realtech.socialsurvey.core.entities.OverviewRegion;
import com.realtech.socialsurvey.core.entities.OverviewUser;
import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;
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
    
    @RequestMapping( value = "/getcompletionrate", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for Completion Rate Graph")
    public String getReportingCompletionRateApi( Long entityId, String entityType ) throws NonFatalException 
    {
         LOGGER.info( "Fetching Completion Rate Graph" );
          
         String json = null;    
         List<List <Object>> completionRate = dashboardGraphManagement.getCompletionRate( entityId , entityType );
         json = new Gson().toJson( completionRate );
         return json;
         
    }
    
    @RequestMapping( value = "/getspsstats", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for Survey Stats Graph")
    public String getReportingSpsStats( Long entityId, String entityType ) throws NonFatalException 
    {
        LOGGER.info( "Fetching Survey Stats Graph" );
        
        String json = null;
        List<List <Object>> spsStats = dashboardGraphManagement.getSpsStatsGraph( entityId , entityType );
        json = new Gson().toJson( spsStats );
        return json;

    }

    @SuppressWarnings ( "null")
    @RequestMapping( value = "/getspsfromoverview", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for Overview ")
    public String getSpsStatsFromOverview( Long entityId, String entityType ) throws NonFatalException 
    {
        LOGGER.info( "Fetching Survey Stats Graph" );
        
        String json = null;
        
        Map<String,Object> overview_map = new HashMap<String,Object>();
        if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN )) {
            OverviewUser overviewUser = overviewManagement.fetchOverviewUserDetails(entityId, entityType);
            if(overviewUser != null){
                overview_map.put( "SpsScore", overviewUser.getSpsScore() );
                overview_map.put( "DetractorPercentage", overviewUser.getDetractorPercentage());
                overview_map.put( "PassivesPercentage",  overviewUser.getPassivesPercentage() );
                overview_map.put( "PromoterPercentage", overviewUser.getPromoterPercentage() );
            }

          
        }else if(entityType.equals( CommonConstants.BRANCH_ID_COLUMN )){
            OverviewBranch overviewBranch = overviewManagement.fetchOverviewBranchDetails( entityId, entityType );
            if(overviewBranch != null){
                overview_map.put( "SpsScore", overviewBranch.getSpsScore() );
                overview_map.put( "DetractorPercentage", overviewBranch.getDetractorPercentage());
                overview_map.put( "PassivesPercentage",  overviewBranch.getPassivesPercentage() );
                overview_map.put( "PromoterPercentage", overviewBranch.getPromoterPercentage() );
            }

            
        }else if(entityType.equals( CommonConstants.REGION_ID_COLUMN )){
           OverviewRegion overviewRegion = overviewManagement.fetchOverviewRegionDetails( entityId, entityType );
           if(overviewRegion != null){
               overview_map.put( "SpsScore", overviewRegion.getSpsScore() );
               overview_map.put( "DetractorPercentage", overviewRegion.getDetractorPercentage());
               overview_map.put( "PassivesPercentage",  overviewRegion.getPassivesPercentage() );
               overview_map.put( "PromoterPercentage", overviewRegion.getPromoterPercentage() );
           }
          
        }else if(entityType.equals( CommonConstants.COMPANY_ID_COLUMN )){
            OverviewCompany overviewCompany = overviewManagement.fetchOverviewCompanyDetails( entityId, entityType );
            if(overviewCompany != null){
                overview_map.put( "SpsScore", overviewCompany.getSpsScore() );
                overview_map.put( "DetractorPercentage", overviewCompany.getDetractorPercentage());
                overview_map.put( "PassivesPercentage",  overviewCompany.getPassivesPercentage() );
                overview_map.put( "PromoterPercentage", overviewCompany.getPromoterPercentage() );
            }
           
        }
        json = new Gson().toJson( overview_map );
        if(json == null && json.length() <= 0){
            throw new NonFatalException( "NonFatalException while fetching data. " );
        }
        return json; 
    }
    
    @SuppressWarnings ( "null")
    @RequestMapping( value = "/getalltimefromoverview", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for Overview ")
    public String getAllTimeDataOverview( Long entityId, String entityType ) throws NonFatalException 
    {
        LOGGER.info( "Fetching Survey Stats Graph" );
        
        String json = null;
        
        Map<String,Object> overview_map = new HashMap<String,Object>();
        if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN )) {
            OverviewUser overviewUser = overviewManagement.fetchOverviewUserDetails(entityId, entityType); 
            if(overviewUser != null){
                overview_map.put( "Processed", overviewUser.getProcessed() );
                overview_map.put( "Completed", overviewUser.getCompleted() );
                overview_map.put( "CompletePercentage", overviewUser.getCompletedPercentage() );
                overview_map.put( "Incomplete", overviewUser.getIncomplete() );
                overview_map.put( "IncompletePercentage", overviewUser.getIncompletePercentage() );
                overview_map.put( "SocialPosts", overviewUser.getSocialPosts());
                overview_map.put( "ZillowReviews", overviewUser.getZillowReviews() );
                overview_map.put( "Unprocessed", overviewUser.getUnprocessed() );
                overview_map.put( "Unassigned", overviewUser.getUnassigned() );
                overview_map.put( "Duplicate", overviewUser.getTotalDuplicate() );
                overview_map.put( "Corrupted", overviewUser.getTotalCorrupted() );
                overview_map.put( "Rating",overviewUser.getRating() );
                overview_map.put( "TotalReview",overviewUser.getTotalReviews() );
            }     
          
        }else if(entityType.equals( CommonConstants.BRANCH_ID_COLUMN )){
            OverviewBranch overviewBranch = overviewManagement.fetchOverviewBranchDetails( entityId, entityType );
            if(overviewBranch != null){
                overview_map.put( "Processed", overviewBranch.getProcessed() );
                overview_map.put( "Completed", overviewBranch.getCompleted() );
                overview_map.put( "CompletePercentage", overviewBranch.getCompletedPercentage() );
                overview_map.put( "Incomplete", overviewBranch.getIncomplete() );
                overview_map.put( "IncompletePercentage", overviewBranch.getIncompletePercentage() );
                overview_map.put( "SocialPosts", overviewBranch.getSocialPosts());
                overview_map.put( "ZillowReviews", overviewBranch.getZillowReviews() );
                overview_map.put( "Unprocessed", overviewBranch.getUnprocessed() );
                overview_map.put( "Unassigned", overviewBranch.getUnassigned() );
                overview_map.put( "Duplicate", overviewBranch.getTotalDuplicate() );
                overview_map.put( "Corrupted", overviewBranch.getTotalCorrupted() );
                overview_map.put( "Rating",overviewBranch.getRating() );
                overview_map.put( "TotalReview",overviewBranch.getTotalReviews() );
            }
           
            
        }else if(entityType.equals( CommonConstants.REGION_ID_COLUMN )){
           OverviewRegion overviewRegion = overviewManagement.fetchOverviewRegionDetails( entityId, entityType );
           if(overviewRegion != null){
               overview_map.put( "Processed", overviewRegion.getProcessed() );
               overview_map.put( "Completed", overviewRegion.getCompleted() );
               overview_map.put( "CompletePercentage", overviewRegion.getCompletedPercentage() );
               overview_map.put( "Incomplete", overviewRegion.getIncomplete() );
               overview_map.put( "IncompletePercentage", overviewRegion.getIncompletePercentage() );
               overview_map.put( "SocialPosts", overviewRegion.getSocialPosts());
               overview_map.put( "ZillowReviews", overviewRegion.getZillowReviews() );
               overview_map.put( "Unprocessed", overviewRegion.getUnprocessed() );
               overview_map.put( "Unassigned", overviewRegion.getUnassigned() );
               overview_map.put( "Duplicate", overviewRegion.getTotalDuplicate() );
               overview_map.put( "Corrupted", overviewRegion.getTotalCorrupted() );
               overview_map.put( "Rating",overviewRegion.getRating() );
               overview_map.put( "TotalReview",overviewRegion.getTotalReviews() );
             
           }
         
        }else if(entityType.equals( CommonConstants.COMPANY_ID_COLUMN )){
            OverviewCompany overviewCompany = overviewManagement.fetchOverviewCompanyDetails( entityId, entityType );
            if(overviewCompany != null){
                overview_map.put( "Processed", overviewCompany.getProcessed() );
                overview_map.put( "Completed", overviewCompany.getCompleted() );
                overview_map.put( "CompletePercentage", overviewCompany.getCompletedPercentage() );
                overview_map.put( "Incomplete", overviewCompany.getIncomplete() );
                overview_map.put( "IncompletePercentage", overviewCompany.getIncompletePercentage() );
                overview_map.put( "SocialPosts", overviewCompany.getSocialPosts());
                overview_map.put( "ZillowReviews", overviewCompany.getZillowReviews() );
                overview_map.put( "Unprocessed", overviewCompany.getUnprocessed() );
                overview_map.put( "Unassigned", overviewCompany.getUnassigned() );
                overview_map.put( "Duplicate", overviewCompany.getTotalDuplicate() );
                overview_map.put( "Corrupted", overviewCompany.getTotalCorrupted() );
                overview_map.put( "Rating",overviewCompany.getRating() );
                overview_map.put( "TotalReview",overviewCompany.getTotalReviews() );
            }
            
           
        }
        json = new Gson().toJson( overview_map );
        if(json == null && json.length() <= 0){
            throw new NonFatalException( "NonFatalException while fetching data. " );
        }
        return json; 
    }
    
    @RequestMapping( value = "/getsurveystatsreport", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for Survey Stats Reporting ")
    public String getReportingSurveyStatsReport( Long entityId, String entityType ) throws NonFatalException 
    {
        LOGGER.info( "Fetching Survey Stats Graph" );
        
        String json = null;
        List<List <Object>> surveyStatsReport = reportingDashboardManagement.getSurveyStatsReport( entityId, entityType );
        json = new Gson().toJson( surveyStatsReport );
        return json;
    }
    
    @RequestMapping( value = "/getrecentactivityforreporting", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Recent Activity Reporting ")
    public String getRecentActivity( Long entityId, String entityType , int startIndex , int batchSize ) throws NonFatalException 
    {
        LOGGER.info( "Fetching Recent Activity Reporting" );
        
        String json = null;
        List<List <Object>> recentActivityList = reportingDashboardManagement.getRecentActivityList( entityId, entityType , startIndex , batchSize);
        json = new Gson().toJson( recentActivityList );
        return json;
    }
    
    @RequestMapping( value = "/getuseradoptionreportsforreporting", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch User Adoption Report For Reporting ")
    public String getUserAdoption( Long entityId, String entityType ) throws NonFatalException 
    {
        LOGGER.info( "Fetch User Adoption Report For Reporting" );
        
        String json = null;
        List<List <Object>> userAdoptionList = reportingDashboardManagement.getUserAdoptionReport( entityId, entityType );
        json = new Gson().toJson( userAdoptionList );
        return json;
    }
    
    @RequestMapping( value = "/getmaxquestionforcompany", method= RequestMethod.GET)
    @ApiOperation( value = "Fetch Max Question For Company Report For Reporting")
    public int getCompanyMaxQuestion(Long companyId, Timestamp startDate, Timestamp endDate) throws NonFatalException
    {
        LOGGER.info( "Fetch Max Question For Company Report For Reporting");
        return reportingDashboardManagement.getMaxQuestionForSurveyCompanyReport( companyId, startDate, endDate );
    }
    
    @RequestMapping( value = "/getsurveyresultscompanyreportsforreporting", method= RequestMethod.GET)
    @ApiOperation( value = "Fetch Survey Results Company Report For Reporting")
    public String getSurveyResultsCompany(Long companyId, Timestamp startDate, Timestamp endDate , int startIndex , int batchSize) throws NonFatalException
    {
        LOGGER.info( "Fetch Survey Results Company Report For Reporting");
        Map<String,SurveyResultsCompanyReport> surveyResultsCompanyList = reportingDashboardManagement.getSurveyResultsCompanyReport(companyId,startDate,endDate,startIndex,batchSize);
        return new Gson().toJson(surveyResultsCompanyList);
    }
    

    @RequestMapping( value = "/getcompanyuserreportsforreporting", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Company User Report For Reporting ")
    public String getCompanyUserReport( Long entityId, String entityType ) throws NonFatalException 
    {
        LOGGER.info( "Fetch User Adoption Report For Reporting" );
        
        String json = null;
        List<List <Object>> userAdoptionList = reportingDashboardManagement.getCompanyUserReport( entityId, entityType );
        json = new Gson().toJson( userAdoptionList );
        return json;
    }
    
    @RequestMapping( value = "/getsurveytransactionreportforreporting", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Survey Transaction Report For Reporting ")
    public String getSurveyTransactionReport( Long entityId, String entityType , Timestamp startDate, Timestamp endDate ) throws NonFatalException 
    {
        LOGGER.info( "Fetch Survey Transaction Report For Reporting" );
        
        String json = null;
        List<List <Object>> surveyTransactionList = reportingDashboardManagement.getSurveyTransactionReport( entityId, entityType ,startDate ,endDate );
        json = new Gson().toJson( surveyTransactionList );
        return json;
    }
    
    @RequestMapping( value = "/getuserrankingreportforreporting", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch User Ranking Report For Reporting ")
    public String getUserRankingReport( Long entityId, String entityType , int year , int month , int type) throws NonFatalException 
    {
        LOGGER.info( "Fetch Survey Transaction Report For Reporting" );
        
        String json = null;
        List<List <Object>> userRankingList = null;
        if(type == 1){
            userRankingList = reportingDashboardManagement.getUserRankingReportForYear( entityId, entityType, year );
        }else {
            userRankingList = reportingDashboardManagement.getUserRankingReportForMonth( entityId, entityType, year, month );
        }
        json = new Gson().toJson( userRankingList );
        return json;
    }
    
    @RequestMapping( value = "/getmonthdataoverviewfordashboard", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Month Data For Overview ")
    public String getMonthDataOverviewForDashboard( Long entityId, String entityType , int month , int year ) throws NonFatalException 
    {
        LOGGER.info( "Fetch Month Data For Overview " );
        
        String json = null;
        
        Map<String,Object> overview_map = overviewManagement.fetchOverviewDetailsBasedOnMonth( entityId , entityType , month , year);
        json = new Gson().toJson( overview_map );
        return json;
    }
    
    @RequestMapping( value = "/getyeardataoverviewfordashboard", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetching Year Data For Overview")
    public String getYearDataOverviewForDashboard( Long entityId, String entityType, int year ) throws NonFatalException 
    {
        LOGGER.info( "Fetching Year Data For Overview" );
        
        String json = null;
        
        Map<String,Object> overview_map = overviewManagement.fetchOverviewDetailsBasedOnYear( entityId , entityType , year);
        json = new Gson().toJson( overview_map );
        return json;
    }
    
    @RequestMapping( value = "/getuserrankingforthisyear")
    @ApiOperation( value = "Fetch User Ranking for this year")
    public String getUserRankingForThisYear(Long entityId, String entityType,int year,int startIndex,int batchSize) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking for this year");
        
        String json = null;
        
        List<List<Object>> userRankingList =  new ArrayList<>();
        
        userRankingList = reportingDashboardManagement.getUserRankingThisYear(entityType,entityId, year,startIndex,batchSize);
        
        json = new Gson().toJson(userRankingList);
        
        return json;
    }
    
    @RequestMapping( value = "/getuserrankingforthismonth")
    @ApiOperation( value = "Fetch User Ranking for this year")
    public String getUserRankingForThisMonth(Long entityId, String entityType,int month,int year,int startIndex,int batchSize) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking for this month");
        
        String json = null;
        
        List<List<Object>> userRankingList =  new ArrayList<>();
                
        userRankingList = reportingDashboardManagement.getUserRankingThisMonth(entityType,entityId,month,year,startIndex,batchSize);
  
        json = new Gson().toJson(userRankingList);
        
        return json;
    }
    
    @RequestMapping( value = "/getuserrankingforpastyear")
    @ApiOperation( value = "Fetch User Ranking for this year")
    public String getUserRankingForPastYear(Long entityId, String entityType,int year,int startIndex,int batchSize) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking for past year");
        
        String json = null;
        
        List<List<Object>> userRankingList =  new ArrayList<>();
        
        userRankingList = reportingDashboardManagement.getUserRankingPastYear(entityType,entityId, year,startIndex,batchSize);
        
        json = new Gson().toJson(userRankingList);
        
        return json;
    }
    
    @RequestMapping( value = "/getuserrankingforpastmonth")
    @ApiOperation( value = "Fetch User Ranking for this year")
    public String getUserRankingForPastMonth(Long entityId, String entityType,int month,int year,int startIndex,int batchSize) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking for past month");
        
        String json = null;
        
        List<List<Object>> userRankingList =  new ArrayList<>();
        
        userRankingList = reportingDashboardManagement.getUserRankingPastMonth(entityType,entityId,month,year,startIndex,batchSize);
        
        json = new Gson().toJson(userRankingList);
        
        return json;
    }

    @RequestMapping( value = "/getuserrankingforpastyears")
    @ApiOperation( value = "Fetch User Ranking for past years")
    public String getUserRankingForPastYears(Long entityId, String entityType,int startIndex,int batchSize) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking for past years");
        
        String json = null;
        
        List<List<Object>> userRankingList =  new ArrayList<>();
        
        userRankingList = reportingDashboardManagement.getUserRankingPastYears(entityType,entityId,startIndex,batchSize);
        
        json = new Gson().toJson(userRankingList);
        
        return json;
    }
 
    @RequestMapping( value = "/getuserrankingrankcountthisyear" , method = RequestMethod.GET)
    @ApiOperation( value = "Fetch User Ranking Rank Count for this year")
    public String getUserRankingRankCountForThisYear(Long userId , Long entityId, String entityType,int year , int batchSize) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For this year");
        
        String json = null;
        Map<String, Object> RankingCountStartIndex = new HashMap<String,Object>();
        RankingCountStartIndex = reportingDashboardManagement.fetchRankingRankCountThisYear(userId,entityId, entityType, year, batchSize );
        json = new Gson().toJson(RankingCountStartIndex);
        
        return json;
        
    }
    
    @RequestMapping( value = "/getuserrankingrankcountthismonth" , method = RequestMethod.GET)
    @ApiOperation( value = "Fetch User Ranking Rank Count for this month")
    public String getUserRankingRankCountForThisMonth(Long userId , Long entityId, String entityType,int year , int month , int batchSize) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For this month");
        
        String json = null;
        Map<String, Object> RankingCountStartIndex = new HashMap<String,Object>();
        RankingCountStartIndex = reportingDashboardManagement.fetchRankingRankCountThisMonth(userId, entityId, entityType, year, month, batchSize );
        json = new Gson().toJson(RankingCountStartIndex);
        
        return json;
        
    }
    
    @RequestMapping( value = "/getuserrankingrankcountpastyear" , method = RequestMethod.GET)
    @ApiOperation( value = "Fetch User Ranking Rank Count for past year")
    public String getUserRankingRankCountForPastYear(Long userId , Long entityId, String entityType,int year , int batchSize) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For past year");
        
        String json = null;
        Map<String, Object> RankingCountStartIndex = new HashMap<String,Object>();
        RankingCountStartIndex = reportingDashboardManagement.fetchRankingRankCountPastYear(userId, entityId, entityType, year, batchSize );
        json = new Gson().toJson(RankingCountStartIndex);
        
        return json;
        
    }
    
    @RequestMapping( value = "/getuserrankingrankcountpastmonth" , method = RequestMethod.GET)
    @ApiOperation( value = "Fetch User Ranking Rank Count for past month")
    public String getUserRankingRankCountForPastMonth(Long userId , Long entityId, String entityType,int year , int month , int batchSize) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For past month");
        
        String json = null;
        Map<String, Object> RankingCountStartIndex = new HashMap<String,Object>();
        RankingCountStartIndex = reportingDashboardManagement.fetchRankingRankCountPastMonth(userId, entityId, entityType, year, month, batchSize );
        json = new Gson().toJson(RankingCountStartIndex);
        
        return json;
        
    }
    
    @RequestMapping( value = "/getuserrankingrankcountpastyears" , method = RequestMethod.GET)
    @ApiOperation( value = "Fetch User Ranking Rank Count for past years")
    public String getUserRankingRankCountForPastYears(Long userId , Long entityId, String entityType, int batchSize) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For past year");
        
        String json = null;
        Map<String, Object> RankingCountStartIndex = new HashMap<String,Object>();
        RankingCountStartIndex = reportingDashboardManagement.fetchRankingRankCountPastYears(userId, entityId, entityType, batchSize );
        json = new Gson().toJson(RankingCountStartIndex);
        
        return json;
        
    }
    
    @RequestMapping( value = "/getuserrankingcountthisyear" , method = RequestMethod.GET)
    @ApiOperation( value = "Fetch User Ranking Count for this year")
    public String getUserRankingCountForThisYear(Long entityId, String entityType,int year , int batchSize) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For this year");
        
        String json = null;
        Map<String, Object> RankingCountStartIndex = new HashMap<String,Object>();
        RankingCountStartIndex = reportingDashboardManagement.fetchRankingCountThisYear( entityId, entityType, year, batchSize );
        json = new Gson().toJson(RankingCountStartIndex);
        
        return json;
        
    }
    
    @RequestMapping( value = "/getuserrankingcountthismonth" , method = RequestMethod.GET)
    @ApiOperation( value = "Fetch User Ranking Count for this month")
    public String getUserRankingCountForThisMonth(Long entityId, String entityType,int year , int month , int batchSize) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For this month");
        
        String json = null;
        Map<String, Object> RankingCountStartIndex = new HashMap<String,Object>();
        RankingCountStartIndex = reportingDashboardManagement.fetchRankingCountThisMonth( entityId, entityType, year, month, batchSize );
        json = new Gson().toJson(RankingCountStartIndex);
        
        return json;
        
    }
    
    @RequestMapping( value = "/getuserrankingcountpastyear" , method = RequestMethod.GET)
    @ApiOperation( value = "Fetch User Ranking Count for past year")
    public String getUserRankingCountForPastYear(Long entityId, String entityType,int year , int batchSize) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For past year");
        
        String json = null;
        Map<String, Object> RankingCountStartIndex = new HashMap<String,Object>();
        RankingCountStartIndex = reportingDashboardManagement.fetchRankingCountPastYear( entityId, entityType, year, batchSize );
        json = new Gson().toJson(RankingCountStartIndex);
        
        return json;
        
    }
    
    @RequestMapping( value = "/getuserrankingcountpastmonth" , method = RequestMethod.GET)
    @ApiOperation( value = "Fetch User Ranking Count for past month")
    public String getUserRankingCountForPastMonth(Long entityId, String entityType,int year , int month , int batchSize) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For past month");
        
        String json = null;
        Map<String, Object> RankingCountStartIndex = new HashMap<String,Object>();
        RankingCountStartIndex = reportingDashboardManagement.fetchRankingCountPastMonth( entityId, entityType, year, month, batchSize );
        json = new Gson().toJson(RankingCountStartIndex);
        
        return json;
        
    }
    
    @RequestMapping( value = "/getuserrankingcountpastyears" , method = RequestMethod.GET)
    @ApiOperation( value = "Fetch User Ranking Count for past years")
    public String getUserRankingCountForPastYears(Long entityId, String entityType, int batchSize) throws NonFatalException
    {
        LOGGER.info( "Fetching User Ranking Count For past years");
        
        String json = null;
        Map<String, Object> RankingCountStartIndex = new HashMap<String,Object>();
        RankingCountStartIndex = reportingDashboardManagement.fetchRankingCountPastYears( entityId, entityType, batchSize );
        json = new Gson().toJson(RankingCountStartIndex);
        
        return json;
        
    }
    
    @RequestMapping( value = "/getscorestatsoverall" , method = RequestMethod.GET)
    @ApiOperation( value = "Fetch Score Stats Overall")
    public String getScoreStatsOverall(Long entityId, String entityType, int currentMonth, int currentYear){
    	
    	LOGGER.info("Fetching Score Stats Overall");
    	
    	String json = null;
    	List<List<Object>> scoreStatsOverall = new ArrayList<>();
    	scoreStatsOverall = reportingDashboardManagement.getScoreStatsForOverall(entityId, entityType, currentMonth, currentYear);
    	json = new Gson().toJson(scoreStatsOverall);
    	return json;
    }
    
    @RequestMapping( value = "/getscorestatsquestion" , method = RequestMethod.GET)
    @ApiOperation( value = "Fetch Score Stats Question")
    public String getScoreStatsQuestion(Long entityId, String entityType, int currentMonth, int currentYear){
    
    	LOGGER.info("Fetching Score Stats Question");
    	
    	String json = null;
    	List<List<Object>> scoreStatsQuestion = new ArrayList<>();
    	scoreStatsQuestion = reportingDashboardManagement.getScoreStatsForQuestion(entityId, entityType, currentMonth, currentYear);
    	json = new Gson().toJson(scoreStatsQuestion);
    	return json;
    }
    

    @RequestMapping ( value = "/getcompaniesoptedfordigestmail", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch the list of companies that have digest Mail enabled")
    public String getCompaniesOptedForDigestMail( int startIndex, int batchSize )
    {

        LOGGER.info( "Fetching the list of companies that have digest Mail enabled" );

        String json = null;
        List<CompanyDigestRequestData> digestRequestData = new ArrayList<>();
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
}
