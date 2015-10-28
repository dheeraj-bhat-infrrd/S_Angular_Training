package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.DatabaseException;


@Component ( "userProfile")
public class UserProfileDaoImpl extends GenericDaoImpl<UserProfile, Long> implements UserProfileDao
{

    private static final Logger LOG = LoggerFactory.getLogger( UserProfileDaoImpl.class );


    /*
     * Method to deactivate all the user profiles for a given user.
     */

    @Override
    public void deactivateAllUserProfilesForUser( User admin, User userToBeDeactivated )
    {

        LOG.info( "Method deactivateUserProfileByUser called to deactivate user : " + userToBeDeactivated.getFirstName() );
        Query query = getSession().getNamedQuery( "UserProfile.updateProfileByUser" );
        // Setting status for user profile as inactive.
        query.setParameter( 0, CommonConstants.STATUS_INACTIVE );
        query.setParameter( 1, String.valueOf( admin.getUserId() ) );
        query.setParameter( 2, new Timestamp( System.currentTimeMillis() ) );
        query.setParameter( 3, userToBeDeactivated );
        query.executeUpdate();
        LOG.info( "Method deactivateUserProfileByUser called to deactivate user : " + userToBeDeactivated.getFirstName() );

    }


    /*
     * Method to remove a branch admin.
     */
    @Override
    public void deactivateUserProfileForBranch( User admin, long branchId, User userToBeDeactivated )
    {
        LOG.info( "Method deactivateUserProfileForBranch called to deactivate user : " + userToBeDeactivated.getFirstName() );
        Query query = getSession().getNamedQuery( "UserProfile.updateByUser" );
        // Setting status for user profile as inactive.
        query.setParameter( 0, CommonConstants.STATUS_INACTIVE );
        query.setParameter( 1, String.valueOf( admin.getUserId() ) );
        query.setParameter( 2, String.valueOf( new Timestamp( System.currentTimeMillis() ) ) );
        query.setParameter( 3, userToBeDeactivated );
        query.setParameter( 4, branchId );
        query.executeUpdate();
        LOG.info( "Method deactivateUserProfileForBranch called to deactivate user : " + userToBeDeactivated.getFirstName() );
    }


    /*
     * Method to deactivate a region admin.
     */
    @Override
    public void deactivateUserProfileForRegion( User admin, long regionId, User userToBeDeactivated )
    {
        LOG.info( "Method deactivateUserProfileForBranch called to deactivate user : " + userToBeDeactivated.getFirstName() );
        Query query = getSession().getNamedQuery( "UserProfile.updateByUser" );
        // Setting status for user profile as inactive.
        query.setParameter( 0, CommonConstants.STATUS_INACTIVE );
        query.setParameter( 1, String.valueOf( admin.getUserId() ) );
        query.setParameter( 2, String.valueOf( new Timestamp( System.currentTimeMillis() ) ) );
        query.setParameter( 3, userToBeDeactivated );
        query.setParameter( 4, regionId );
        query.executeUpdate();
        LOG.info( "Method deactivateUserProfileForBranch called to deactivate user : " + userToBeDeactivated.getFirstName() );
    }


    @Override
    @SuppressWarnings ( "unchecked")
    public List<Long> getBranchIdsForUser( User user )
    {
        LOG.info( "Method getBranchIdsForUser called to fetch branch ids assigned to user : " + user.getFirstName() );
        Criteria criteria = getSession().createCriteria( UserProfile.class );
        List<Long> branchIds = new ArrayList<>();
        try {
            criteria.add( Restrictions.eq( CommonConstants.USER_COLUMN, user ) );

            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ) );
            criteria.add( criterion );
            criteria.setProjection( Projections.distinct( Projections.projectionList().add( Projections.property( "branchId" ),
                "branchId" ) ) );
            branchIds = criteria.list();
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in getBranchIdsForUser() ", hibernateException );
            throw new DatabaseException( "Exception caught in getBranchIdsForUser() ", hibernateException );
        }
        LOG.info( "Method getBranchIdsForUser finished to fetch branch ids assigned to user : " + user.getFirstName() );
        return branchIds;
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<Long> getBranchesForAdmin( User user, List<ProfilesMaster> profilesMasters )
    {
        LOG.info( "Method getBranchesForAdmin() called to fetch branches assigned to user : " + user.getFirstName() );
        Criteria criteria = getSession().createCriteria( UserProfile.class );
        List<Long> branchIds = new ArrayList<>();
        try {
            criteria.add( Restrictions.eq( CommonConstants.USER_COLUMN, user ) );

            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ) );
            criteria.add( criterion );
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_COLUMN, user.getCompany() ) );
            criteria.add( Restrictions.in( CommonConstants.PROFILE_MASTER_COLUMN, profilesMasters ) );
            criteria.setProjection( Projections.distinct( Projections.projectionList().add( Projections.property( "branchId" ),
                "branchId" ) ) );
            branchIds = criteria.list();
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in getBranchesForAdmin() ", hibernateException );
            throw new DatabaseException( "Exception caught in getBranchesForAdmin() ", hibernateException );
        }
        LOG.info( "Method getBranchesForAdmin() finished to fetch branches assigned to user : " + user.getFirstName() );
        return branchIds;
    }


    @Override
    public void deleteUserProfilesByCompany( long companyId )
    {
        LOG.info( "Method deleteUserProfilesByCompany() called to delete profiles of company id : " + companyId );
        Query query = getSession().createQuery( "delete from UserProfile where company.companyId=?" );
        query.setParameter( 0, companyId );
        query.executeUpdate();
        LOG.info( "Method deleteUserProfilesByCompany() finished." );
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<UserProfile> findUserProfilesInBatch( Map<String, Object> queries, int startIndex, int batchSize )
    {

        LOG.info( "Method findUserProfilesInBatch() called" );

        Criteria criteria = getSession().createCriteria( UserProfile.class );
        try {
            for ( Entry<String, Object> query : queries.entrySet() ) {
                criteria.add( Restrictions.eq( query.getKey(), query.getValue() ) );
            }
            criteria.createAlias( "user", "alias" );
            criteria.addOrder( Order.asc( "alias.firstName" ) );
            if ( startIndex > -1 ) {
                criteria.setFirstResult( startIndex );
            }
            if ( batchSize > -1 ) {
                criteria.setMaxResults( batchSize );
            }
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findByKeyValueAscendingWithAlias().", hibernateException );
            throw new DatabaseException( "HibernateException caught in findByKeyValueAscendingWithAlias().", hibernateException );
        }
        return criteria.list();
    }


    @Override
    public Map<String, Long> findPrimaryUserProfileByAgentId( long entityId )
    {
        LOG.info( "Method findPrimaryUserProfileByAgentId() called for agent id : " + entityId );
        Map<String, Long> hierarchyMap = new HashMap<String, Long>();
        String hqlQuery = "select u.company.companyId, u.regionId, u.branchId, u.agentId from UserProfile u where u.agentId=? AND u.isPrimary = ?";
        Query query = getSession().createQuery( hqlQuery );
        query.setParameter( 0, entityId );
        query.setParameter( 1, 1 );
        List<Object[]> rows = (List<Object[]>) query.list();
        for ( Object[] row : rows ) {
            hierarchyMap.put( CommonConstants.COMPANY_ID_COLUMN, Long.valueOf( String.valueOf( row[0] ) ) );
            hierarchyMap.put( CommonConstants.REGION_ID_COLUMN, Long.valueOf( String.valueOf( row[1] ) ) );
            hierarchyMap.put( CommonConstants.BRANCH_ID_COLUMN, Long.valueOf( String.valueOf( row[2] ) ) );
            hierarchyMap.put( CommonConstants.AGENT_ID_COLUMN, Long.valueOf( String.valueOf( row[3] ) ) );
        }
        LOG.info( "Method deleteUserProfilesByCompany() finished." );
        return hierarchyMap;
    }


    @Override
    public Set<Long> findUserIdsByBranch( long branchId )
    {
        LOG.info( "Method call started for findUserIdsByBranch for branch : " + branchId );
        Set<Long> agentIds = new HashSet<Long>();

        LOG.info( "Fetching users for branch : " + branchId );
        Query query = getSession().getNamedQuery( "UserProfile.getProfileIdsByBranch" );
        query.setParameter( 0, CommonConstants.STATUS_ACTIVE );
        query.setParameter( 1, branchId );

        List<Long> rows = (List<Long>) query.list();
        for ( Long row : rows ) {
            agentIds.add( Long.valueOf( String.valueOf( row ) ) );
        }

        LOG.info( "Fetched users for branch : " + branchId );
        LOG.info( "Method call ended for findUserIdsByBranch for branch : " + branchId );
        return agentIds;
    }


    @Override
    public Set<Long> findUserIdsByRegion( long regionId )
    {
        LOG.info( "Method call started for findUserIdsByRegion for region : " + regionId );
        Set<Long> agentIds = new HashSet<Long>();

        LOG.info( "Fetching users for region : " + regionId );
        Query query = getSession().getNamedQuery( "UserProfile.getProfileIdsByRegion" );
        query.setParameter( 0, CommonConstants.STATUS_ACTIVE );
        query.setParameter( 1, regionId );

        List<Long> rows = (List<Long>) query.list();
        for ( Long row : rows ) {
            agentIds.add( Long.valueOf( String.valueOf( row ) ) );
        }

        LOG.info( "Fetched users for region : " + regionId );
        LOG.info( "Method call ended for findUserIdsByRegion for region : " + regionId );
        return agentIds;
    }
}
