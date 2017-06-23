package com.realtech.socialsurvey.core.services.reportingmanagement.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.dao.OverviewUserDao;
import com.realtech.socialsurvey.core.entities.OverviewUser;
import com.realtech.socialsurvey.core.services.reportingmanagement.OverviewManagement;

@DependsOn ( "generic")
@Component
public class OverviewManagementImpl implements OverviewManagement
{
    private static final Logger LOG = LoggerFactory.getLogger( OverviewManagementImpl.class );

    @Autowired
    private OverviewUserDao OverviewUserDao ;
    
    
    @Override
    public OverviewUser fetchOverviewDetails(long entityId , String entityType){
        OverviewUser overviewUser = null;
        String overviewUserId = OverviewUserDao.getOverviewUserId( entityId );
        overviewUser =  OverviewUserDao.findOverviewUser( OverviewUser.class, overviewUserId ); 
        return overviewUser;
    }

}
