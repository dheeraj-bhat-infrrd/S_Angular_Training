package com.realtech.socialsurvey.core.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.ReportingUserProfileDao;
import com.realtech.socialsurvey.core.entities.ReportingUserProfile;

public class ReportingUserProfileDaoImpl extends GenericReportingDaoImpl<ReportingUserProfile, String> implements ReportingUserProfileDao
{
    private static final Logger LOG = LoggerFactory.getLogger( ReportingUserProfileDaoImpl.class );


    @Override
    @SuppressWarnings ( "unchecked")
    public Set<Long> findUserIdsByRegion( long regionId )
    {
        LOG.info( "Method call started for findUserIdsByRegion for region : {}",regionId );
        Set<Long> userIds = new HashSet<>();

        LOG.debug( "Fetching users for region : {}",regionId );
        Query query = getSession().createSQLQuery( "SELECT USER_ID FROM USER_PROFILE WHERE STATUS = ? and REGION_ID = ?" );
        query.setParameter( 0, CommonConstants.STATUS_ACTIVE );
        query.setParameter( 1, regionId );

        List<Integer> rows = (List<Integer>) query.list();
        for ( Integer row : rows ) {
            userIds.add( Long.valueOf( row ) );
        }

        LOG.debug( "Fetched {} users for region : {}" ,userIds.size(),regionId );
        LOG.info( "Method call ended for findUserIdsByRegion for region : {}",regionId );
        return userIds;
    }
    

    @Override
    @SuppressWarnings ( "unchecked")
    public Set<Long> findUserIdsByBranch( long branchId )
    {
        LOG.info( "Method call started for findUserIdsByBranch for branch : {}",branchId );
        Set<Long> userIds = new HashSet<>();

        LOG.debug( "Fetching users for branch : {}",branchId );
        Query query = getSession().createSQLQuery( "SELECT USER_ID FROM USER_PROFILE WHERE STATUS = ? and BRANCH_ID = ?" );
        query.setParameter( 0, CommonConstants.STATUS_ACTIVE );
        query.setParameter( 1, branchId );

        List<Integer> rows = (List<Integer>) query.list();
        for ( Integer row : rows ) {
            userIds.add( Long.valueOf( row ) );
        }

        LOG.debug( "Fetched {} users for branch : {}" ,userIds.size(),branchId );
        LOG.info( "Method call ended for findUserIdsByBranch for branch : {}",branchId );
        return userIds;
    }

}
