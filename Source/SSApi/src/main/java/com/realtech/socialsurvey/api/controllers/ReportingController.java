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
    
    @RequestMapping( value = "/getoverview", method = RequestMethod.GET)
    @ApiOperation ( value = "Fetch Data for Overview ")
    public String getReportingOverview( Long entityId, String entityType ) throws NonFatalException 
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
            overview_map.put( "TotalIncompleteTransactions", overviewUser.getTotalIncompleteTransactions() );
            overview_map.put( "CorruptedPercentage", overviewUser.getCorruptedPercentage() );
            overview_map.put( "DuplicatePercentage", overviewUser.getDuplicatePercentage());
            overview_map.put( "ArchievedPercentage", overviewUser.getArchievedPercentage() );
            overview_map.put( "MismatchedPercentage", overviewUser.getMismatchedPercentage() );
            overview_map.put( "TotalSurveySent",overviewUser.getTotalSurveySent() );
            overview_map.put( "TotalSurveyCompleted",overviewUser.getTotalSurveyCompleted() );
            overview_map.put( "TotalSocialPost", overviewUser.getTotalSocialPost() );
            overview_map.put( "TotalZillowReviews", overviewUser.getTotalZillowReviews() );
        }else if(entityType.equals( CommonConstants.BRANCH_ID_COLUMN )){
            OverviewBranch overviewBranch = overviewManagement.fetchOverviewBranchDetails( entityId, entityType );
            overview_map.put( "SpsScore", overviewBranch.getSpsScore() );
            overview_map.put( "DetractorPercentage", overviewBranch.getDetractorPercentage());
            overview_map.put( "PassivesPercentage",  overviewBranch.getPassivesPercentage() );
            overview_map.put( "PromoterPercentage", overviewBranch.getPromoterPercentage() );
            overview_map.put( "TotalIncompleteTransactions", overviewBranch.getTotalIncompleteTransactions() );
            overview_map.put( "CorruptedPercentage", overviewBranch.getCorruptedPercentage() );
            overview_map.put( "DuplicatePercentage", overviewBranch.getDuplicatePercentage());
            overview_map.put( "ArchievedPercentage", overviewBranch.getArchievedPercentage() );
            overview_map.put( "MismatchedPercentage", overviewBranch.getMismatchedPercentage() );
            overview_map.put( "TotalSurveySent",overviewBranch.getTotalSurveySent() );
            overview_map.put( "TotalSurveyCompleted",overviewBranch.getTotalSurveyCompleted() );
            overview_map.put( "TotalSocialPost", overviewBranch.getTotalSocialPost() );
            overview_map.put( "TotalZillowReviews", overviewBranch.getTotalZillowReviews() );
        }else if(entityType.equals( CommonConstants.REGION_ID_COLUMN )){
           OverviewRegion overviewRegion = overviewManagement.fetchOverviewRegionDetails( entityId, entityType );
           overview_map.put( "SpsScore", overviewRegion.getSpsScore() );
           overview_map.put( "DetractorPercentage", overviewRegion.getDetractorPercentage());
           overview_map.put( "PassivesPercentage",  overviewRegion.getPassivesPercentage() );
           overview_map.put( "PromoterPercentage", overviewRegion.getPromoterPercentage() );
           overview_map.put( "TotalIncompleteTransactions", overviewRegion.getTotalIncompleteTransactions() );
           overview_map.put( "CorruptedPercentage", overviewRegion.getCorruptedPercentage() );
           overview_map.put( "DuplicatePercentage", overviewRegion.getDuplicatePercentage());
           overview_map.put( "ArchievedPercentage", overviewRegion.getArchievedPercentage() );
           overview_map.put( "MismatchedPercentage", overviewRegion.getMismatchedPercentage() );
           overview_map.put( "TotalSurveySent",overviewRegion.getTotalSurveySent() );
           overview_map.put( "TotalSurveyCompleted",overviewRegion.getTotalSurveyCompleted() );
           overview_map.put( "TotalSocialPost", overviewRegion.getTotalSocialPost() );
           overview_map.put( "TotalZillowReviews", overviewRegion.getTotalZillowReviews() );
        }else if(entityType.equals( CommonConstants.COMPANY_ID_COLUMN )){
            OverviewCompany overviewCompany = overviewManagement.fetchOverviewCompanyDetails( entityId, entityType );
            overview_map.put( "SpsScore", overviewCompany.getSpsScore() );
            overview_map.put( "DetractorPercentage", overviewCompany.getDetractorPercentage());
            overview_map.put( "PassivesPercentage",  overviewCompany.getPassivesPercentage() );
            overview_map.put( "PromoterPercentage", overviewCompany.getPromoterPercentage() );
            overview_map.put( "TotalIncompleteTransactions", overviewCompany.getTotalIncompleteTransactions() );
            overview_map.put( "CorruptedPercentage", overviewCompany.getCorruptedPercentage() );
            overview_map.put( "DuplicatePercentage", overviewCompany.getDuplicatePercentage());
            overview_map.put( "ArchievedPercentage", overviewCompany.getArchievedPercentage() );
            overview_map.put( "MismatchedPercentage", overviewCompany.getMismatchedPercentage() );
            overview_map.put( "TotalSurveySent",overviewCompany.getTotalSurveySent() );
            overview_map.put( "TotalSurveyCompleted",overviewCompany.getTotalSurveyCompleted() );
            overview_map.put( "TotalSocialPost", overviewCompany.getTotalSocialPost() );
            overview_map.put( "TotalZillowReviews", overviewCompany.getTotalZillowReviews() );
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
    
    

}
