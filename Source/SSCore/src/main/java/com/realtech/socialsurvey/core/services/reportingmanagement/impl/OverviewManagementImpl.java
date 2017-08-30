package com.realtech.socialsurvey.core.services.reportingmanagement.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OverviewBranchDao;
import com.realtech.socialsurvey.core.dao.OverviewBranchMonthDao;
import com.realtech.socialsurvey.core.dao.OverviewBranchYearDao;
import com.realtech.socialsurvey.core.dao.OverviewCompanyDao;
import com.realtech.socialsurvey.core.dao.OverviewCompanyMonthDao;
import com.realtech.socialsurvey.core.dao.OverviewCompanyYearDao;
import com.realtech.socialsurvey.core.dao.OverviewRegionDao;
import com.realtech.socialsurvey.core.dao.OverviewRegionMonthDao;
import com.realtech.socialsurvey.core.dao.OverviewRegionYearDao;
import com.realtech.socialsurvey.core.dao.OverviewUserDao;
import com.realtech.socialsurvey.core.dao.OverviewUserMonthDao;
import com.realtech.socialsurvey.core.dao.OverviewUserYearDao;
import com.realtech.socialsurvey.core.entities.OverviewBranch;
import com.realtech.socialsurvey.core.entities.OverviewBranchMonth;
import com.realtech.socialsurvey.core.entities.OverviewBranchYear;
import com.realtech.socialsurvey.core.entities.OverviewCompany;
import com.realtech.socialsurvey.core.entities.OverviewCompanyMonth;
import com.realtech.socialsurvey.core.entities.OverviewCompanyYear;
import com.realtech.socialsurvey.core.entities.OverviewRegion;
import com.realtech.socialsurvey.core.entities.OverviewRegionMonth;
import com.realtech.socialsurvey.core.entities.OverviewRegionYear;
import com.realtech.socialsurvey.core.entities.OverviewUser;
import com.realtech.socialsurvey.core.entities.OverviewUserMonth;
import com.realtech.socialsurvey.core.entities.OverviewUserYear;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.reportingmanagement.OverviewManagement;

@DependsOn ( "generic")
@Component
public class OverviewManagementImpl implements OverviewManagement
{
    private static final Logger LOG = LoggerFactory.getLogger( OverviewManagementImpl.class );

    @Autowired
    private OverviewUserDao overviewUserDao ;
    
    @Autowired
    private OverviewBranchDao overviewBranchDao;
    
    @Autowired
    private OverviewRegionDao overviewRegionDao;
    
    @Autowired
    private OverviewCompanyDao overviewCompanyDao;
    
    @Autowired
    private OverviewUserMonthDao overviewUserMonthDao;
    
    @Autowired
    private OverviewBranchMonthDao overviewBranchMonthDao;
    
    @Autowired
    private OverviewRegionMonthDao overviewRegionMonthDao;
    
    @Autowired
    private OverviewCompanyMonthDao overviewCompanyMonthDao;
    
    @Autowired
    private OverviewUserYearDao overviewUserYearDao;
    
    @Autowired
    private OverviewBranchYearDao overviewBranchYearDao;
    
    @Autowired
    private OverviewRegionYearDao overviewRegionYearDao;
    
    @Autowired
    private OverviewCompanyYearDao overviewCompanyYearDao;
    
    @Override
    public OverviewUser fetchOverviewUserDetails(long entityId , String entityType)throws NonFatalException{
        String overviewUserId = overviewUserDao.getOverviewUserId( entityId );
        OverviewUser overviewUser;
        if(overviewUserId == null){
            overviewUser = null;
        }
        overviewUser =  overviewUserDao.findOverviewUser( OverviewUser.class, overviewUserId ); 
        return overviewUser;
    }


    @Override
    public OverviewBranch fetchOverviewBranchDetails( long entityId, String entityType )throws NonFatalException{
      
      
           String overviewBranchId = overviewBranchDao.getOverviewBranchId( entityId );
           OverviewBranch overviewBranch;
           if(overviewBranchId == null){
               overviewBranch =  null;
            }else{
               overviewBranch = overviewBranchDao.findOverviewBranch( OverviewBranch.class , overviewBranchId ); 
            }
           return overviewBranch;
       
    }


    @Override
    public OverviewRegion fetchOverviewRegionDetails( long entityId, String entityType )throws NonFatalException{
       String overviewRegionId = overviewRegionDao.getOverviewRegionId( entityId );
       OverviewRegion overviewRegion;
       if(overviewRegionId == null){
           overviewRegion = null;
       }else{
           overviewRegion = overviewRegionDao.findOverviewRegion( OverviewRegion.class, overviewRegionId );
       }
       return overviewRegion;
    }


    @Override
    public OverviewCompany fetchOverviewCompanyDetails( long entityId, String entityType )throws NonFatalException{
        String overviewCompanyId = overviewCompanyDao.getOverviewCompanyId( entityId );
        OverviewCompany overviewCompany;
        if(overviewCompanyId == null){
            overviewCompany = null;
         }else{
             overviewCompany = overviewCompanyDao.findOverviewCompany( OverviewCompany.class, overviewCompanyId );
         }
        
        return overviewCompany;
    }

    @Override
    @Transactional(value = "transactionManagerForReporting")
    public Map<String, Object> fetchOverviewDetailsBasedOnMonth(long entityId ,String entityType , int month , int year)throws NonFatalException{
       
        Map<String,Object> overview_map = new HashMap<String,Object>();
       
        if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN )) {
            OverviewUserMonth overviewUserMonth = overviewUserMonthDao.fetchOverviewForUserBasedOnMonth(entityId, month, year);
            if(overviewUserMonth!=null){
                overview_map.put( "Processed", overviewUserMonth.getProcessed() );
                overview_map.put( "Completed", overviewUserMonth.getCompleted() );
                overview_map.put( "CompletePercentage", overviewUserMonth.getCompletePercentage() );
                overview_map.put( "Incomplete", overviewUserMonth.getIncomplete() );
                overview_map.put( "IncompletePercentage", overviewUserMonth.getIncompletePercentage() );
                overview_map.put( "SocialPosts", overviewUserMonth.getSocialPosts());
                overview_map.put( "ZillowReviews", overviewUserMonth.getZillowReviews() );
                overview_map.put( "Unprocessed", overviewUserMonth.getUnprocessed() );
                overview_map.put( "Unassigned", overviewUserMonth.getUnassigned() );
                overview_map.put( "Duplicate", overviewUserMonth.getDuplicate() );
                overview_map.put( "Corrupted", overviewUserMonth.getCorrupted() );
                overview_map.put( "Rating",overviewUserMonth.getRating() );
                overview_map.put( "TotalReview",overviewUserMonth.getTotalReview() );
            }
            
        }else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN )) {
            OverviewBranchMonth overviewBranchMonth = overviewBranchMonthDao.fetchOverviewForBranchBasedOnMonth(entityId, month, year);
            if(overviewBranchMonth!=null){
                overview_map.put( "Processed", overviewBranchMonth.getProcessed() );
                overview_map.put( "Completed", overviewBranchMonth.getCompleted() );
                overview_map.put( "CompletePercentage", overviewBranchMonth.getCompletePercentage() );
                overview_map.put( "Incomplete", overviewBranchMonth.getIncomplete() );
                overview_map.put( "IncompletePercentage", overviewBranchMonth.getIncompletePercentage() );
                overview_map.put( "SocialPosts", overviewBranchMonth.getSocialPosts());
                overview_map.put( "ZillowReviews", overviewBranchMonth.getZillowReviews() );
                overview_map.put( "Unprocessed", overviewBranchMonth.getUnprocessed() );
                overview_map.put( "Unassigned", overviewBranchMonth.getUnassigned() );
                overview_map.put( "Duplicate", overviewBranchMonth.getDuplicate() );
                overview_map.put( "Corrupted", overviewBranchMonth.getCorrupted() );
                overview_map.put( "Rating",overviewBranchMonth.getRating() );
                overview_map.put( "TotalReview",overviewBranchMonth.getTotalReview() );
            }
            
        }else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN  )) {
            OverviewRegionMonth overviewRegionMonth = overviewRegionMonthDao.fetchOverviewForRegionBasedOnMonth(entityId, month, year);
            if(overviewRegionMonth!=null){
                overview_map.put( "Processed", overviewRegionMonth.getProcessed() );
                overview_map.put( "Completed", overviewRegionMonth.getCompleted() );
                overview_map.put( "CompletePercentage", overviewRegionMonth.getCompletePercentage() );
                overview_map.put( "Incomplete", overviewRegionMonth.getIncomplete() );
                overview_map.put( "IncompletePercentage", overviewRegionMonth.getIncompletePercentage() );
                overview_map.put( "SocialPosts", overviewRegionMonth.getSocialPosts());
                overview_map.put( "ZillowReviews", overviewRegionMonth.getZillowReviews() );
                overview_map.put( "Unprocessed", overviewRegionMonth.getUnprocessed() );
                overview_map.put( "Unassigned", overviewRegionMonth.getUnassigned() );
                overview_map.put( "Duplicate", overviewRegionMonth.getDuplicate() );
                overview_map.put( "Corrupted", overviewRegionMonth.getCorrupted() );
                overview_map.put( "Rating",overviewRegionMonth.getRating() );
                overview_map.put( "TotalReview",overviewRegionMonth.getTotalReview() );
            }
            
        }else if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN)) {
            OverviewCompanyMonth overviewCompanyMonth = overviewCompanyMonthDao.fetchOverviewForCompanyBasedOnMonth(entityId, month, year);
            if(overviewCompanyMonth!=null){
                overview_map.put( "Processed", overviewCompanyMonth.getProcessed() );
                overview_map.put( "Completed", overviewCompanyMonth.getCompleted() );
                overview_map.put( "CompletePercentage", overviewCompanyMonth.getCompletePercentage() );
                overview_map.put( "Incomplete", overviewCompanyMonth.getIncomplete() );
                overview_map.put( "IncompletePercentage", overviewCompanyMonth.getIncompletePercentage() );
                overview_map.put( "SocialPosts", overviewCompanyMonth.getSocialPosts());
                overview_map.put( "ZillowReviews", overviewCompanyMonth.getZillowReviews() );
                overview_map.put( "Unprocessed", overviewCompanyMonth.getUnprocessed() );
                overview_map.put( "Unassigned", overviewCompanyMonth.getUnassigned() );
                overview_map.put( "Duplicate", overviewCompanyMonth.getDuplicate() );
                overview_map.put( "Corrupted", overviewCompanyMonth.getCorrupted() );
                overview_map.put( "Rating",overviewCompanyMonth.getRating() );
                overview_map.put( "TotalReview",overviewCompanyMonth.getTotalReview() );
            }
            
        }
        return overview_map;
    }
    
    @Override
    @Transactional(value = "transactionManagerForReporting")
    public Map<String, Object> fetchOverviewDetailsBasedOnYear(long entityId ,String entityType , int year)throws NonFatalException{
       
        Map<String,Object> overview_map = new HashMap<String,Object>();
       
        if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN )) {
            OverviewUserYear overviewUserYear = overviewUserYearDao.fetchOverviewForUserBasedOnYear( entityId, year );
            if(overviewUserYear!=null){
                overview_map.put( "Processed", overviewUserYear.getProcessed() );
                overview_map.put( "Completed", overviewUserYear.getCompleted() );
                overview_map.put( "CompletePercentage", overviewUserYear.getCompletePercentage() );
                overview_map.put( "Incomplete", overviewUserYear.getIncomplete() );
                overview_map.put( "IncompletePercentage", overviewUserYear.getIncompletePercentage() );
                overview_map.put( "SocialPosts", overviewUserYear.getSocialPosts());
                overview_map.put( "ZillowReviews", overviewUserYear.getZillowReviews() );
                overview_map.put( "Unprocessed", overviewUserYear.getUnprocessed() );
                overview_map.put( "Unassigned", overviewUserYear.getUnassigned() );
                overview_map.put( "Duplicate", overviewUserYear.getDuplicate() );
                overview_map.put( "Corrupted", overviewUserYear.getCorrupted() );
                overview_map.put( "Rating",overviewUserYear.getRating() );
                overview_map.put( "TotalReview",overviewUserYear.getTotalReview() );
            }
            
        }else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN )) {
            OverviewBranchYear overviewBranchYear = overviewBranchYearDao.fetchOverviewForBranchBasedOnYear(entityId, year);
            if(overviewBranchYear!=null){
                overview_map.put( "Processed", overviewBranchYear.getProcessed() );
                overview_map.put( "Completed", overviewBranchYear.getCompleted() );
                overview_map.put( "CompletePercentage", overviewBranchYear.getCompletePercentage() );
                overview_map.put( "Incomplete", overviewBranchYear.getIncomplete() );
                overview_map.put( "IncompletePercentage", overviewBranchYear.getIncompletePercentage() );
                overview_map.put( "SocialPosts", overviewBranchYear.getSocialPosts());
                overview_map.put( "ZillowReviews", overviewBranchYear.getZillowReviews() );
                overview_map.put( "Unprocessed", overviewBranchYear.getUnprocessed() );
                overview_map.put( "Unassigned", overviewBranchYear.getUnassigned() );
                overview_map.put( "Duplicate", overviewBranchYear.getDuplicate() );
                overview_map.put( "Corrupted", overviewBranchYear.getCorrupted() );
                overview_map.put( "Rating",overviewBranchYear.getRating() );
                overview_map.put( "TotalReview",overviewBranchYear.getTotalReview() );
            }
            
        }else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN  )) {
            OverviewRegionYear overviewRegionYear = overviewRegionYearDao.fetchOverviewForRegionBasedOnYear(entityId, year);
            if(overviewRegionYear!=null){
                overview_map.put( "Processed", overviewRegionYear.getProcessed() );
                overview_map.put( "Completed", overviewRegionYear.getCompleted() );
                overview_map.put( "CompletePercentage", overviewRegionYear.getCompletePercentage() );
                overview_map.put( "Incomplete", overviewRegionYear.getIncomplete() );
                overview_map.put( "IncompletePercentage", overviewRegionYear.getIncompletePercentage() );
                overview_map.put( "SocialPosts", overviewRegionYear.getSocialPosts());
                overview_map.put( "ZillowReviews", overviewRegionYear.getZillowReviews() );
                overview_map.put( "Unprocessed", overviewRegionYear.getUnprocessed() );
                overview_map.put( "Unassigned", overviewRegionYear.getUnassigned() );
                overview_map.put( "Duplicate", overviewRegionYear.getDuplicate() );
                overview_map.put( "Corrupted", overviewRegionYear.getCorrupted() );
                overview_map.put( "Rating",overviewRegionYear.getRating() );
                overview_map.put( "TotalReview",overviewRegionYear.getTotalReview() );
            }
            
        }else if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN)) {
            OverviewCompanyYear overviewCompanyYear = overviewCompanyYearDao.fetchOverviewForCompanyBasedOnYear(entityId, year);
            if(overviewCompanyYear!=null){
                overview_map.put( "Processed", overviewCompanyYear.getProcessed() );
                overview_map.put( "Completed", overviewCompanyYear.getCompleted() );
                overview_map.put( "CompletePercentage", overviewCompanyYear.getCompletePercentage() );
                overview_map.put( "Incomplete", overviewCompanyYear.getIncomplete() );
                overview_map.put( "IncompletePercentage", overviewCompanyYear.getIncompletePercentage() );
                overview_map.put( "SocialPosts", overviewCompanyYear.getSocialPosts());
                overview_map.put( "ZillowReviews", overviewCompanyYear.getZillowReviews() );
                overview_map.put( "Unprocessed", overviewCompanyYear.getUnprocessed() );
                overview_map.put( "Unassigned", overviewCompanyYear.getUnassigned() );
                overview_map.put( "Duplicate", overviewCompanyYear.getDuplicate() );
                overview_map.put( "Corrupted", overviewCompanyYear.getCorrupted() );
                overview_map.put( "Rating",overviewCompanyYear.getRating() );
                overview_map.put( "TotalReview",overviewCompanyYear.getTotalReview() );
            }
            
        }
        return overview_map;
    }
}
