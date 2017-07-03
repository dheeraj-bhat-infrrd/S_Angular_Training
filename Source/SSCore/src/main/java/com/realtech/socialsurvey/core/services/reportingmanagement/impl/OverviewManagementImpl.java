package com.realtech.socialsurvey.core.services.reportingmanagement.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.dao.OverviewBranchDao;
import com.realtech.socialsurvey.core.dao.OverviewCompanyDao;
import com.realtech.socialsurvey.core.dao.OverviewRegionDao;
import com.realtech.socialsurvey.core.dao.OverviewUserDao;
import com.realtech.socialsurvey.core.entities.OverviewBranch;
import com.realtech.socialsurvey.core.entities.OverviewCompany;
import com.realtech.socialsurvey.core.entities.OverviewRegion;
import com.realtech.socialsurvey.core.entities.OverviewUser;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.reportingmanagement.OverviewManagement;

@DependsOn ( "generic")
@Component
public class OverviewManagementImpl implements OverviewManagement
{
    private static final Logger LOG = LoggerFactory.getLogger( OverviewManagementImpl.class );

    @Autowired
    private OverviewUserDao OverviewUserDao ;
    
    @Autowired
    private OverviewBranchDao OverviewBranchDao;
    
    @Autowired
    private OverviewRegionDao OverviewRegionDao;
    
    @Autowired
    private OverviewCompanyDao OverviewCompanyDao;
    
    
    @Override
    public OverviewUser fetchOverviewUserDetails(long entityId , String entityType)throws NonFatalException{
        String overviewUserId = OverviewUserDao.getOverviewUserId( entityId );
        if(overviewUserId == null){
          throw new  NonFatalException(); 
        }
        OverviewUser overviewUser =  OverviewUserDao.findOverviewUser( OverviewUser.class, overviewUserId ); 
        return overviewUser;
    }


    @Override
    public OverviewBranch fetchOverviewBranchDetails( long entityId, String entityType )throws NonFatalException{
      
      
           String overviewBranchId = OverviewBranchDao.getOverviewBranchId( entityId );
           if(overviewBranchId == null){
               throw new  NonFatalException(); 
            }
           OverviewBranch overviewBranch = OverviewBranchDao.findOverviewBranch( OverviewBranch.class , overviewBranchId ); 
           return overviewBranch;
       
    }


    @Override
    public OverviewRegion fetchOverviewRegionDetails( long entityId, String entityType )throws NonFatalException{
       String overviewRegionId = OverviewRegionDao.getOverviewRegionId( entityId );
       if(overviewRegionId == null){
           throw new  NonFatalException(); 
        }
       OverviewRegion overviewRegion = OverviewRegionDao.findOverviewRegion( OverviewRegion.class, overviewRegionId );
       return overviewRegion;
    }


    @Override
    public OverviewCompany fetchOverviewCompanyDetails( long entityId, String entityType )throws NonFatalException{
        String overviewCompanyId = OverviewCompanyDao.getOverviewCompanyId( entityId );
        if(overviewCompanyId == null){
            throw new  NonFatalException(); 
         }
        OverviewCompany overviewCompany = OverviewCompanyDao.findOverviewCompany( OverviewCompany.class, overviewCompanyId );
        return overviewCompany;
    }

}
