package com.realtech.socialsurvey.core.dao.impl;

import java.math.BigInteger;
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
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.DatabaseException;


@Component ( "userProfile")
public class UserProfileDaoImpl extends GenericDaoImpl<UserProfile, Long> implements UserProfileDao
{

    private static final Logger LOG = LoggerFactory.getLogger( UserProfileDaoImpl.class );
    private final String regionUserSearchQuery = "SELECT US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS, group_concat(UP.BRANCH_ID) as BRANCH_ID, UP.REGION_ID, UP.PROFILES_MASTER_ID FROM  USER_PROFILE AS UP JOIN (SELECT USER_ID, BRANCH_ID, REGION_ID FROM USER_PROFILE where USER_ID = ? and PROFILES_MASTER_ID = ? ) AS subQuery_UP ON subQuery_UP.REGION_ID = UP.REGION_ID and UP.STATUS != ? JOIN USERS AS US ON US.USER_ID = UP.USER_ID GROUP BY US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS, UP.PROFILES_MASTER_ID, UP.REGION_ID";


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
        Set<Long> userIds = new HashSet<Long>();

        LOG.info( "Fetching users for branch : " + branchId );
        Query query = getSession().createSQLQuery( "SELECT USER_ID FROM USER_PROFILE WHERE STATUS = ? and BRANCH_ID = ?" );
        query.setParameter( 0, CommonConstants.STATUS_ACTIVE );
        query.setParameter( 1, branchId );

        List<Integer> rows = (List<Integer>) query.list();
        for ( Integer row : rows ) {
            userIds.add( Long.valueOf( row.intValue() ) );
        }

        LOG.info( "Fetched " + userIds.size() + " users for branch : " + branchId );
        LOG.info( "Method call ended for findUserIdsByBranch for branch : " + branchId );
        return userIds;
    }


    @Override
    public Set<Long> findUserIdsByRegion( long regionId )
    {
        LOG.info( "Method call started for findUserIdsByRegion for region : " + regionId );
        Set<Long> userIds = new HashSet<Long>();

        LOG.info( "Fetching users for region : " + regionId );
        Query query = getSession().createSQLQuery( "SELECT USER_ID FROM USER_PROFILE WHERE STATUS = ? and REGION_ID = ?" );
        query.setParameter( 0, CommonConstants.STATUS_ACTIVE );
        query.setParameter( 1, regionId );

        List<Integer> rows = (List<Integer>) query.list();
        for ( Integer row : rows ) {
            userIds.add( Long.valueOf( row.intValue() ) );
        }

        LOG.info( "Fetched " + userIds.size() + " users for region : " + regionId );
        LOG.info( "Method call ended for findUserIdsByRegion for region : " + regionId );
        return userIds;
    }


    @Override
    @Transactional
    public int getUsersUnderBranchAdminCount( User user )
    {
        LOG.info( "Method call started for getUsersUnderBranchAdminCount for branch admin id : " + user.getUserId() );
        Query query = getSession()
            .createSQLQuery(
                "SELECT COUNT(DISTINCT US.USER_ID) FROM  USER_PROFILE AS UP JOIN (SELECT USER_ID, BRANCH_ID, REGION_ID FROM USER_PROFILE where USER_ID = ? and PROFILES_MASTER_ID = ?) AS subQuery_UP ON subQuery_UP.BRANCH_ID = UP.BRANCH_ID and subQuery_UP.REGION_ID = UP.REGION_ID and UP.STATUS != ? JOIN USERS AS US ON US.USER_ID = UP.USER_ID" );
        query.setParameter( 0, user.getUserId() );
        query.setParameter( 1, CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID );
        query.setParameter( 2, CommonConstants.STATUS_INACTIVE );

        LOG.info( "Method call ended for getUsersUnderBranchAdminCount for branch admin id : " + user.getUserId() );
        return ( (BigInteger) query.uniqueResult() ).intValue();
    }


    @Override
    @Transactional
    public int getUsersUnderRegionAdminCount( User user )
    {
        LOG.info( "Method call started for getUsersUnderRegionAdminCount for branch admin id : " + user.getUserId() );
        Query query = getSession().createSQLQuery( "SELECT COUNT(*) FROM ( " + regionUserSearchQuery + " ) as subQuery" );
        query.setParameter( 0, user.getUserId() );
        query.setParameter( 1, CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID );
        query.setParameter( 2, CommonConstants.STATUS_INACTIVE );

        LOG.info( "Method call started for getUsersUnderRegionAdminCount for branch admin id : " + user.getUserId() );
        return ( (BigInteger) query.uniqueResult() ).intValue();
    }

    @Override
    @Transactional
    public List<UserFromSearch> findUsersUnderBranchAdmin( User user, int startIndex, int batchSize )
    {
        List<UserFromSearch> userList = new ArrayList<UserFromSearch>();
        LOG.info( "Method call started for findUsersForBranchAdmin for branch admin id : " + user.getUserId() );
        Query query = getSession()
            .createSQLQuery(
                "SELECT DISTINCT US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS, UP.BRANCH_ID, UP.REGION_ID, UP.PROFILES_MASTER_ID FROM  USER_PROFILE AS UP JOIN (SELECT USER_ID, BRANCH_ID, REGION_ID FROM USER_PROFILE where USER_ID = ? and PROFILES_MASTER_ID = ?) AS subQuery_UP ON subQuery_UP.BRANCH_ID = UP.BRANCH_ID and subQuery_UP.REGION_ID = UP.REGION_ID and UP.STATUS != ? JOIN USERS AS US ON US.USER_ID = UP.USER_ID" );
        query.setParameter( 0, user.getUserId() );
        query.setParameter( 1, CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID );
        query.setParameter( 2, CommonConstants.STATUS_INACTIVE );
        if ( startIndex > -1 ) {
            query.setFirstResult( startIndex );
        }
        if ( batchSize > -1 ) {
            query.setMaxResults( batchSize );
        }

        List<Object[]> rows = (List<Object[]>) query.list();

        for ( Object[] row : rows ) {
            UserFromSearch userFromSearch = new UserFromSearch();
            userFromSearch.setUserId( Long.parseLong( String.valueOf( row[0] ) ) );
            userFromSearch.setFirstName( String.valueOf( row[1] ) );
            if ( row[2] != null )
                userFromSearch.setLastName( String.valueOf( row[2] ) );
            userFromSearch.setEmailId( String.valueOf( row[3] ) );
            userFromSearch.setLoginName( String.valueOf( row[4] ) );
            userFromSearch.setIsOwner( Integer.parseInt( String.valueOf( row[5] ) ) );
            userFromSearch.setCompanyId( Long.parseLong( String.valueOf( row[6] ) ) );
            userFromSearch.setStatus( Integer.parseInt( String.valueOf( row[7] ) ) );

            List<Long> branchIds = new ArrayList<Long>();
            branchIds.add( Long.parseLong( String.valueOf( row[8] ) ) );

            List<Long> regionIds = new ArrayList<Long>();
            regionIds.add( Long.parseLong( String.valueOf( row[9] ) ) );

            userFromSearch.setBranches( branchIds );
            userFromSearch.setRegions( regionIds );

            setAdminLevelsForUserFromSearch( userFromSearch, Integer.parseInt( String.valueOf( row[10] ) ) );

            userFromSearch
                .setDisplayName( getCustomerDisplayName( userFromSearch.getFirstName(), userFromSearch.getLastName() ) );

            userList.add( userFromSearch );
        }

        LOG.info( "Method call ended for findUsersForBranchAdmin for branch admin id : " + user.getUserId() );
        return userList;
    }


    @Override
    @Transactional
    public List<UserFromSearch> findUsersUnderRegionAdmin( User user, int startIndex, int batchSize )
    {
        List<UserFromSearch> userList = new ArrayList<UserFromSearch>();
        LOG.info( "Method call started for findUsersUnderRegionAdmin for branch admin id : " + user.getUserId() );
        Query query = getSession().createSQLQuery( regionUserSearchQuery );

        query.setParameter( 0, user.getUserId() );
        query.setParameter( 1, CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID );
        query.setParameter( 2, CommonConstants.STATUS_INACTIVE );
        if ( startIndex > -1 ) {
            query.setFirstResult( startIndex );
        }
        if ( batchSize > -1 ) {
            query.setMaxResults( batchSize );
        }

        List<Object[]> rows = (List<Object[]>) query.list();

        for ( Object[] row : rows ) {
            UserFromSearch userFromSearch = new UserFromSearch();
            userFromSearch.setUserId( Long.parseLong( String.valueOf( row[0] ) ) );
            userFromSearch.setFirstName( String.valueOf( row[1] ) );
            if ( row[2] != null )
                userFromSearch.setLastName( String.valueOf( row[2] ) );
            userFromSearch.setEmailId( String.valueOf( row[3] ) );
            userFromSearch.setLoginName( String.valueOf( row[4] ) );
            userFromSearch.setIsOwner( Integer.parseInt( String.valueOf( row[5] ) ) );
            userFromSearch.setCompanyId( Long.parseLong( String.valueOf( row[6] ) ) );
            userFromSearch.setStatus( Integer.parseInt( String.valueOf( row[7] ) ) );

            List<Long> branchIds = new ArrayList<Long>();

            String[] bIds = String.valueOf( row[8] ).split(",");
            for(String bId : bIds) {
                branchIds.add( Long.parseLong( String.valueOf( bId ) ) );
            }

            List<Long> regionIds = new ArrayList<Long>();
            regionIds.add( Long.parseLong( String.valueOf( row[9] ) ) );

            userFromSearch.setBranches( branchIds );
            userFromSearch.setRegions( regionIds );

            setAdminLevelsForUserFromSearch( userFromSearch, Integer.parseInt( String.valueOf( row[10] ) ) );

            userFromSearch
                .setDisplayName( getCustomerDisplayName( userFromSearch.getFirstName(), userFromSearch.getLastName() ) );

            userList.add( userFromSearch );
        }

        LOG.info( "Method call ended for findUsersUnderRegionAdmin for branch admin id : " + user.getUserId() );
        return userList;
    }


    private String getCustomerDisplayName( String firstName, String lastName )
    {
        String displayName = firstName;
        if ( lastName != null && !lastName.isEmpty() )
            displayName += " " + lastName;
        return displayName;
    }


    private void setAdminLevelsForUserFromSearch( UserFromSearch userFromSearch, int profileMasterId )
    {
        if ( userFromSearch.getIsOwner() == CommonConstants.IS_OWNER ) {
            userFromSearch.setRegionAdmin( true );
            userFromSearch.setBranchAdmin( true );
        }

        switch ( profileMasterId ) {
            case CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID:
                userFromSearch.setRegionAdmin( true );
            case CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID:
                userFromSearch.setBranchAdmin( true );
                break;
            case CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID:
                userFromSearch.setAgent( true );
                break;
        }
    }

}
