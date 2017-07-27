package com.realtech.socialsurvey.api.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.api.utils.RestUtils;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.OverviewBranch;
import com.realtech.socialsurvey.core.entities.OverviewCompany;
import com.realtech.socialsurvey.core.entities.OverviewRegion;
import com.realtech.socialsurvey.core.entities.OverviewUser;
import com.realtech.socialsurvey.core.entities.OverviewUserMonth;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.reportingmanagement.DashboardGraphManagement;
import com.realtech.socialsurvey.core.services.reportingmanagement.OverviewManagement;
import com.realtech.socialsurvey.core.services.reportingmanagement.ReportingDashboardManagement;
import com.wordnik.swagger.annotations.ApiOperation;


@RestController
@RequestMapping ( "/v1")
public class ReportingController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( SurveyApiController.class );

    @Autowired
    private RestUtils restUtils;

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
    
    @RequestMapping( value = "/getaveragerating", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for Average Rating Graph")
    public String getReportingAverageRating( Long entityId, String entityType ) throws NonFatalException 
    {
        LOGGER.info( "Fetching Survey Stats Graph" );
        
        String json = null;
        List<List <Object>> averageRating = dashboardGraphManagement.getAverageReviewRating( entityId , entityType );
        json = new Gson().toJson( averageRating );
        return json;
    }
    
    @RequestMapping( value = "/getspsfromoverview", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for Overview ")
    public String getSpsStatsFromOverview( Long entityId, String entityType ) throws NonFatalException 
    {
        LOGGER.info( "Fetching Survey Stats Graph" );
        
        String json = null;
        
        List<Object> overview = new ArrayList<>();
        Map<String,Object> overview_map = new HashMap<String,Object>();
        if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN )) {
            OverviewUser overviewUser = overviewManagement.fetchOverviewUserDetails(entityId, entityType); 
            overview_map.put( "SpsScore", overviewUser.getSpsScore() );
            overview_map.put( "DetractorPercentage", overviewUser.getDetractorPercentage());
            overview_map.put( "PassivesPercentage",  overviewUser.getPassivesPercentage() );
            overview_map.put( "PromoterPercentage", overviewUser.getPromoterPercentage() );
          
        }else if(entityType.equals( CommonConstants.BRANCH_ID_COLUMN )){
            OverviewBranch overviewBranch = overviewManagement.fetchOverviewBranchDetails( entityId, entityType );
            overview_map.put( "SpsScore", overviewBranch.getSpsScore() );
            overview_map.put( "DetractorPercentage", overviewBranch.getDetractorPercentage());
            overview_map.put( "PassivesPercentage",  overviewBranch.getPassivesPercentage() );
            overview_map.put( "PromoterPercentage", overviewBranch.getPromoterPercentage() );
            
        }else if(entityType.equals( CommonConstants.REGION_ID_COLUMN )){
           OverviewRegion overviewRegion = overviewManagement.fetchOverviewRegionDetails( entityId, entityType );
           overview_map.put( "SpsScore", overviewRegion.getSpsScore() );
           overview_map.put( "DetractorPercentage", overviewRegion.getDetractorPercentage());
           overview_map.put( "PassivesPercentage",  overviewRegion.getPassivesPercentage() );
           overview_map.put( "PromoterPercentage", overviewRegion.getPromoterPercentage() );
         
        }else if(entityType.equals( CommonConstants.COMPANY_ID_COLUMN )){
            OverviewCompany overviewCompany = overviewManagement.fetchOverviewCompanyDetails( entityId, entityType );
            overview_map.put( "SpsScore", overviewCompany.getSpsScore() );
            overview_map.put( "DetractorPercentage", overviewCompany.getDetractorPercentage());
            overview_map.put( "PassivesPercentage",  overviewCompany.getPassivesPercentage() );
            overview_map.put( "PromoterPercentage", overviewCompany.getPromoterPercentage() );
           
        }
        json = new Gson().toJson( overview_map );
        if(json == null && json.length() <= 0){
            throw new NonFatalException( "NonFatalException while fetching data. " );
        }
        return json; 
    }
    
    @RequestMapping( value = "/getalltimefromoverview", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for Overview ")
    public String getAllTimeDataOverview( Long entityId, String entityType ) throws NonFatalException 
    {
        LOGGER.info( "Fetching Survey Stats Graph" );
        
        String json = null;
        
        List<Object> overview = new ArrayList<>();
        Map<String,Object> overview_map = new HashMap<String,Object>();
        if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN )) {
            OverviewUser overviewUser = overviewManagement.fetchOverviewUserDetails(entityId, entityType); 
            overview_map.put( "Processed", 0 );
            overview_map.put( "Completed", overviewUser.getTotalSurveyCompleted() );
            overview_map.put( "CompletePercentage", 0 );
            overview_map.put( "Incomplete", overviewUser.getTotalIncompleteTransactions() );
            overview_map.put( "IncompletePercentage", 0 );
            overview_map.put( "SocialPosts", overviewUser.getTotalSocialPost());
            overview_map.put( "ZillowReviews", overviewUser.getTotalZillowReviews() );
            overview_map.put( "Unprocessed", 0 );
            overview_map.put( "Unassigned", overviewUser.getTotalMismatched() );
            overview_map.put( "Duplicate", overviewUser.getTotalDuplicate() );
            overview_map.put( "Corrupted", overviewUser.getTotalCorrupted() );
            overview_map.put( "Rating",overviewUser.getRating() );
            overview_map.put( "TotalReview",overviewUser.getTotalReviews() );
          
        }else if(entityType.equals( CommonConstants.BRANCH_ID_COLUMN )){
            OverviewBranch overviewBranch = overviewManagement.fetchOverviewBranchDetails( entityId, entityType );
            overview_map.put( "Processed", 0 );
            overview_map.put( "Completed", overviewBranch.getTotalSurveyCompleted() );
            overview_map.put( "CompletePercentage", 0 );
            overview_map.put( "Incomplete", overviewBranch.getTotalIncompleteTransactions() );
            overview_map.put( "IncompletePercentage", 0 );
            overview_map.put( "SocialPosts", overviewBranch.getTotalSocialPost());
            overview_map.put( "ZillowReviews", overviewBranch.getTotalZillowReviews() );
            overview_map.put( "Unprocessed", 0 );
            overview_map.put( "Unassigned", overviewBranch.getTotalMismatched() );
            overview_map.put( "Duplicate", overviewBranch.getTotalDuplicate() );
            overview_map.put( "Corrupted", overviewBranch.getTotalCorrupted() );
            overview_map.put( "Rating",overviewBranch.getRating() );
            overview_map.put( "TotalReview",overviewBranch.getTotalReviews() );
            
        }else if(entityType.equals( CommonConstants.REGION_ID_COLUMN )){
           OverviewRegion overviewRegion = overviewManagement.fetchOverviewRegionDetails( entityId, entityType );
           overview_map.put( "Processed", 0 );
           overview_map.put( "Completed", overviewRegion.getTotalSurveyCompleted() );
           overview_map.put( "CompletePercentage", 0 );
           overview_map.put( "Incomplete", overviewRegion.getTotalIncompleteTransactions() );
           overview_map.put( "IncompletePercentage", 0 );
           overview_map.put( "SocialPosts", overviewRegion.getTotalSocialPost());
           overview_map.put( "ZillowReviews", overviewRegion.getTotalZillowReviews() );
           overview_map.put( "Unprocessed", 0 );
           overview_map.put( "Unassigned", overviewRegion.getTotalMismatched() );
           overview_map.put( "Duplicate", overviewRegion.getTotalDuplicate() );
           overview_map.put( "Corrupted", overviewRegion.getTotalCorrupted() );
           overview_map.put( "Rating",overviewRegion.getRating() );
           overview_map.put( "TotalReview",overviewRegion.getTotalReviews() );
         
        }else if(entityType.equals( CommonConstants.COMPANY_ID_COLUMN )){
            OverviewCompany overviewCompany = overviewManagement.fetchOverviewCompanyDetails( entityId, entityType );
            overview_map.put( "Processed", 0 );
            overview_map.put( "Completed", overviewCompany.getTotalSurveyCompleted() );
            overview_map.put( "CompletePercentage", 0 );
            overview_map.put( "Incomplete", overviewCompany.getTotalIncompleteTransactions() );
            overview_map.put( "IncompletePercentage", 0 );
            overview_map.put( "SocialPosts", overviewCompany.getTotalSocialPost());
            overview_map.put( "ZillowReviews", overviewCompany.getTotalZillowReviews() );
            overview_map.put( "Unprocessed", 0 );
            overview_map.put( "Unassigned", overviewCompany.getTotalMismatched() );
            overview_map.put( "Duplicate", overviewCompany.getTotalDuplicate() );
            overview_map.put( "Corrupted", overviewCompany.getTotalCorrupted() );
            overview_map.put( "Rating",overviewCompany.getRating() );
            overview_map.put( "TotalReview",overviewCompany.getTotalReviews() );
           
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
    
    @RequestMapping( value = "/getsurveyresultscompanyreportsforreporting", method= RequestMethod.GET)
    @ApiOperation( value = "Fetch Survey Results Company Report For Reporting")
    public String getSurveyResultsCompany(Long entityId, String entityType, Timestamp startDate, Timestamp endDate) throws NonFatalException
    {
    	LOGGER.info( "Fetch Survey Results Company Report For Reporting");
    	String json = null;
    	List<List <Object>> surveyResultsCompanyList = reportingDashboardManagement.getSurveyResultsCompanyReport(entityId, entityType,startDate,endDate);
    	json = new Gson().toJson(surveyResultsCompanyList);
    	return json;
    }
    
    //Survey Response api for testing. Not being used anywhere else
    @RequestMapping( value = "/getsurveyresponseforreporting", method= RequestMethod.GET)
    @ApiOperation( value = "Fetch Survey Response For Reporting")
    public String getSurveyResultsCompany(String surveyDetailsId) throws NonFatalException
    {
    	LOGGER.info( "Fetch Survey Response For Reporting");
    	String json = null;
    	List<String> surveyResponseList = reportingDashboardManagement.getSurveyResponseData(surveyDetailsId);
    	json = new Gson().toJson(surveyResponseList);
    	return json;
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
    

}
