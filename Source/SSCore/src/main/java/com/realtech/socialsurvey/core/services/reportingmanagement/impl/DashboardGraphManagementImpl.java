package com.realtech.socialsurvey.core.services.reportingmanagement.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.services.reportingmanagement.DashboardGraphManagement;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyStatsReportBranchDao;
import com.realtech.socialsurvey.core.dao.SurveyStatsReportCompanyDao;
import com.realtech.socialsurvey.core.dao.SurveyStatsReportRegionDao;
import com.realtech.socialsurvey.core.dao.SurveyStatsReportUserDao;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportBranch;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportCompany;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportRegion;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportUser;

@Component
public class DashboardGraphManagementImpl implements DashboardGraphManagement
{
    private static final Logger LOG = LoggerFactory.getLogger( DashboardGraphManagementImpl.class );
    
    
    @Autowired
    private SurveyStatsReportCompanyDao SurveyStatsReportCompanyDao;
    
    @Autowired
    private SurveyStatsReportBranchDao SurveyStatsReportBranchDao;
    
    @Autowired
    private SurveyStatsReportRegionDao SurveyStatsReportRegionDao;
    
    @Autowired
    private SurveyStatsReportUserDao SurveyStatsReportUserDao;


  
    @Override
    public List<List<Object>> getSpsStatsGraph(Long entityId , String entityType){
        LOG.info("Method for getting SPS stats for SPS stats graph, getSpsStatsGraph() has started");
        //List<List<SurveyStatsReportCompany>> SurveyStats = SurveyStatsReportCompanyDao.fetchCompanySurveyStatsById(companyId );
        List<List<Object>> spsStats = new ArrayList<>();
        Calendar calender = Calendar.getInstance();
        calender.getTime();
        String endTrxMonth = new SimpleDateFormat("yyyy_MM").format(calender.getTime()); //example passes 2016_07
        calender.add(Calendar.MONTH, -6); 
        String startTrxMonth = new SimpleDateFormat("yyyy_MM").format(calender.getTime()); //example passes 2016_01
        
        if(entityType.equals( CommonConstants.COMPANY_ID_COLUMN )){
        	LOG.debug( "Calling method for fetching SPS stats for company {}",entityId );
        	for(SurveyStatsReportCompany SurveyStatsReportCompany : SurveyStatsReportCompanyDao.fetchCompanySurveyStatsById(entityId, startTrxMonth, endTrxMonth ) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportCompany.getYear() );
                list.add( SurveyStatsReportCompany.getMonth() );
                list.add( SurveyStatsReportCompany.getDetractors() );
                list.add( SurveyStatsReportCompany.getPassives() );
                list.add( SurveyStatsReportCompany.getPromoters() );            
                spsStats.add( list );
            }
        }else if(entityType.equals( CommonConstants.REGION_ID_COLUMN )){
        	LOG.debug( "Calling method for fetching SPS stats for region {}",entityId );
            for(SurveyStatsReportRegion SurveyStatsReportRegion : SurveyStatsReportRegionDao.fetchRegionSurveyStatsById(entityId, startTrxMonth, endTrxMonth) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportRegion.getYear() );
                list.add( SurveyStatsReportRegion.getMonth() );
                list.add( SurveyStatsReportRegion.getDetractors() );
                list.add( SurveyStatsReportRegion.getPassives() );
                list.add( SurveyStatsReportRegion.getPromoters() );            
                spsStats.add( list );
            }
        }else if(entityType.equals( CommonConstants.BRANCH_ID_COLUMN )){
        	LOG.debug( "Calling method for fetching SPS stats for branch {}",entityId );
            for(SurveyStatsReportBranch SurveyStatsReportBranch : SurveyStatsReportBranchDao.fetchBranchSurveyStatsById(entityId, startTrxMonth, endTrxMonth ) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportBranch.getYear() );
                list.add( SurveyStatsReportBranch.getMonth() );
                list.add( SurveyStatsReportBranch.getDetractors() );
                list.add( SurveyStatsReportBranch.getPassives() );
                list.add( SurveyStatsReportBranch.getPromoters() );            
                spsStats.add( list );
            }
        }else if(entityType.equals( CommonConstants.AGENT_ID_COLUMN )){
        	LOG.debug( "Calling method for fetching SPS stats for user {}",entityId );
            for(SurveyStatsReportUser SurveyStatsReportUser : SurveyStatsReportUserDao.fetchUserSurveyStatsById( entityId, startTrxMonth, endTrxMonth ) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportUser.getYear() );
                list.add( SurveyStatsReportUser.getMonth() );
                list.add( SurveyStatsReportUser.getDetractors() );
                list.add( SurveyStatsReportUser.getPassives() );
                list.add( SurveyStatsReportUser.getPromoters() );   
                spsStats.add( list );
            }
        }
        
        LOG.info("Method for getting SPS stats for SPS stats graph, getSpsStatsGraph() has finished");
        return spsStats;
    }
    
    @Override
    public List<List<Object>> getCompletionRate(Long entityId , String entityType){
        LOG.info("Method for getting completion Rate for Completion Rate graph, getCompletionRate() has started");

        List<List<Object>> completionRate = new ArrayList<>();
        Calendar calender = Calendar.getInstance();
        calender.getTime();
        String endTrxMonth = new SimpleDateFormat("yyyy_MM").format(calender.getTime()); //example passes 2016_07
        calender.add(Calendar.MONTH, -6); 
        String startTrxMonth = new SimpleDateFormat("yyyy_MM").format(calender.getTime()); //example passes 2016_01

        if(entityType.equals( CommonConstants.COMPANY_ID_COLUMN )){
        	LOG.debug( "Calling method for fetching Completion Rate for company {}",entityId );
            for(SurveyStatsReportCompany SurveyStatsReportCompany : SurveyStatsReportCompanyDao.fetchCompanySurveyStatsById(entityId, startTrxMonth, endTrxMonth ) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportCompany.getYear() );
                list.add( SurveyStatsReportCompany.getMonth() );
                list.add( SurveyStatsReportCompany.getCompleted() );
                list.add( SurveyStatsReportCompany.getIncomplete() );            
                completionRate.add( list );
            }
        }else if(entityType.equals( CommonConstants.REGION_ID_COLUMN )){
        	LOG.debug( "Calling method for fetching Completion Rate for region {}",entityId );
            for(SurveyStatsReportRegion SurveyStatsReportRegion : SurveyStatsReportRegionDao.fetchRegionSurveyStatsById(entityId, startTrxMonth, endTrxMonth) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportRegion.getYear() );
                list.add( SurveyStatsReportRegion.getMonth() );
                list.add( SurveyStatsReportRegion.getCompleted() );
                list.add( SurveyStatsReportRegion.getIncomplete() );            
                completionRate.add( list );
            }
        }else if(entityType.equals( CommonConstants.BRANCH_ID_COLUMN )){
        	LOG.debug( "Calling method for fetching Completion Rate for branch {}",entityId );
            for(SurveyStatsReportBranch SurveyStatsReportBranch : SurveyStatsReportBranchDao.fetchBranchSurveyStatsById(entityId, startTrxMonth, endTrxMonth ) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportBranch.getYear() );
                list.add( SurveyStatsReportBranch.getMonth() );
                list.add( SurveyStatsReportBranch.getCompleted() );
                list.add( SurveyStatsReportBranch.getIncomplete() );            
                completionRate.add( list );
            }
        }else if(entityType.equals( CommonConstants.AGENT_ID_COLUMN )){
        	LOG.debug( "Calling method for fetching Completion Rate for user {}",entityId );
            for(SurveyStatsReportUser SurveyStatsReportUser : SurveyStatsReportUserDao.fetchUserSurveyStatsById( entityId, startTrxMonth, endTrxMonth ) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportUser.getYear() );
                list.add( SurveyStatsReportUser.getMonth() );
                list.add( SurveyStatsReportUser.getCompleted() );
                list.add( SurveyStatsReportUser.getIncomplete() );     
                completionRate.add( list );
            }
        }
        
        LOG.info("Method for getting completion Rate for Completion Rate graph, getCompletionRate() has finished");

        return completionRate;
    }
}
