package com.realtech.socialsurvey.core.services.reportingmanagement.impl;

import java.util.ArrayList;
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
    public List<List<Object>> getAverageReviewRating(Long entityId , String entityType){

        LOG.info("getSurveyStatsForCompany has started");
        //List<List<SurveyStatsReportCompany>> SurveyStats = SurveyStatsReportCompanyDao.fetchCompanySurveyStatsById(companyId );
        List<List<Object>> averageRating = new ArrayList<>();;
        if(entityType.equals( CommonConstants.COMPANY_ID_COLUMN )){
            for(SurveyStatsReportCompany SurveyStatsReportCompany : SurveyStatsReportCompanyDao.fetchCompanySurveyStatsById(entityId ) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportCompany.getYear() );
                list.add( SurveyStatsReportCompany.getMonth() );
                list.add( SurveyStatsReportCompany.getAvgRating() );
                averageRating.add( list );
            }
        }else if(entityType.equals( CommonConstants.REGION_ID_COLUMN )){
            for(SurveyStatsReportRegion SurveyStatsReportRegion : SurveyStatsReportRegionDao.fetchRegionSurveyStatsById(entityId) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportRegion.getYear() );
                list.add( SurveyStatsReportRegion.getMonth() );
                list.add( SurveyStatsReportRegion.getAvgRating() );
                averageRating.add( list );
            }
        }else if(entityType.equals( CommonConstants.BRANCH_ID_COLUMN )){
            for(SurveyStatsReportBranch SurveyStatsReportBranch : SurveyStatsReportBranchDao.fetchBranchSurveyStatsById(entityId ) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportBranch.getYear() );
                list.add( SurveyStatsReportBranch.getMonth() );
                list.add( SurveyStatsReportBranch.getAvgRating() );
                averageRating.add( list );
            }
        }else if(entityType.equals( CommonConstants.AGENT_ID_COLUMN )){
            for(SurveyStatsReportUser SurveyStatsReportUser : SurveyStatsReportUserDao.fetchUserSurveyStatsById( entityId ) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportUser.getYear() );
                list.add( SurveyStatsReportUser.getMonth() );
                list.add( SurveyStatsReportUser.getAvgRating() );
                averageRating.add( list );
            }
        }
        
        return averageRating;
    }
    
    @Override
    public List<List<Object>> getSpsStatsGraph(Long entityId , String entityType){
        LOG.info("getSpsStatsGraphCompany has started");
        //List<List<SurveyStatsReportCompany>> SurveyStats = SurveyStatsReportCompanyDao.fetchCompanySurveyStatsById(companyId );
        List<List<Object>> spsStats = new ArrayList<>();;
        if(entityType.equals( CommonConstants.COMPANY_ID_COLUMN )){
            for(SurveyStatsReportCompany SurveyStatsReportCompany : SurveyStatsReportCompanyDao.fetchCompanySurveyStatsById(entityId ) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportCompany.getYear() );
                list.add( SurveyStatsReportCompany.getMonth() );
                list.add( SurveyStatsReportCompany.getDetractors() );
                list.add( SurveyStatsReportCompany.getPassives() );
                list.add( SurveyStatsReportCompany.getPromoters() );            
                spsStats.add( list );
            }
        }else if(entityType.equals( CommonConstants.REGION_ID_COLUMN )){
            for(SurveyStatsReportRegion SurveyStatsReportRegion : SurveyStatsReportRegionDao.fetchRegionSurveyStatsById(entityId) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportRegion.getYear() );
                list.add( SurveyStatsReportRegion.getMonth() );
                list.add( SurveyStatsReportRegion.getDetractors() );
                list.add( SurveyStatsReportRegion.getPassives() );
                list.add( SurveyStatsReportRegion.getPromoters() );            
                spsStats.add( list );
            }
        }else if(entityType.equals( CommonConstants.BRANCH_ID_COLUMN )){
            for(SurveyStatsReportBranch SurveyStatsReportBranch : SurveyStatsReportBranchDao.fetchBranchSurveyStatsById(entityId ) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportBranch.getYear() );
                list.add( SurveyStatsReportBranch.getMonth() );
                list.add( SurveyStatsReportBranch.getDetractors() );
                list.add( SurveyStatsReportBranch.getPassives() );
                list.add( SurveyStatsReportBranch.getPromoters() );            
                spsStats.add( list );
            }
        }else if(entityType.equals( CommonConstants.AGENT_ID_COLUMN )){
            for(SurveyStatsReportUser SurveyStatsReportUser : SurveyStatsReportUserDao.fetchUserSurveyStatsById( entityId ) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportUser.getYear() );
                list.add( SurveyStatsReportUser.getMonth() );
                list.add( SurveyStatsReportUser.getDetractors() );
                list.add( SurveyStatsReportUser.getPassives() );
                list.add( SurveyStatsReportUser.getPromoters() );   
                spsStats.add( list );
            }
        }
        return spsStats;
    }
    
    @Override
    public List<List<Object>> getCompletionRate(Long entityId , String entityType){
        LOG.info("getSpsStatsGraphCompany has started");
        //List<List<SurveyStatsReportCompany>> SurveyStats = SurveyStatsReportCompanyDao.fetchCompanySurveyStatsById(companyId );
        List<List<Object>> completionRate = new ArrayList<>();;

        if(entityType.equals( CommonConstants.COMPANY_ID_COLUMN )){
            for(SurveyStatsReportCompany SurveyStatsReportCompany : SurveyStatsReportCompanyDao.fetchCompanySurveyStatsById(entityId ) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportCompany.getYear() );
                list.add( SurveyStatsReportCompany.getMonth() );
                list.add( SurveyStatsReportCompany.getCompleted() );
                list.add( SurveyStatsReportCompany.getIncomplete() );            
                completionRate.add( list );
            }
        }else if(entityType.equals( CommonConstants.REGION_ID_COLUMN )){
            for(SurveyStatsReportRegion SurveyStatsReportRegion : SurveyStatsReportRegionDao.fetchRegionSurveyStatsById(entityId) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportRegion.getYear() );
                list.add( SurveyStatsReportRegion.getMonth() );
                list.add( SurveyStatsReportRegion.getCompleted() );
                list.add( SurveyStatsReportRegion.getIncomplete() );            
                completionRate.add( list );
            }
        }else if(entityType.equals( CommonConstants.BRANCH_ID_COLUMN )){
            for(SurveyStatsReportBranch SurveyStatsReportBranch : SurveyStatsReportBranchDao.fetchBranchSurveyStatsById(entityId ) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportBranch.getYear() );
                list.add( SurveyStatsReportBranch.getMonth() );
                list.add( SurveyStatsReportBranch.getCompleted() );
                list.add( SurveyStatsReportBranch.getIncomplete() );            
                completionRate.add( list );
            }
        }else if(entityType.equals( CommonConstants.AGENT_ID_COLUMN )){
            for(SurveyStatsReportUser SurveyStatsReportUser : SurveyStatsReportUserDao.fetchUserSurveyStatsById( entityId ) ){
                List<Object> list = new ArrayList<>();
                list.add( SurveyStatsReportUser.getYear() );
                list.add( SurveyStatsReportUser.getMonth() );
                list.add( SurveyStatsReportUser.getCompleted() );
                list.add( SurveyStatsReportUser.getIncomplete() );     
                completionRate.add( list );
            }
        }
        return completionRate;
    }
}
