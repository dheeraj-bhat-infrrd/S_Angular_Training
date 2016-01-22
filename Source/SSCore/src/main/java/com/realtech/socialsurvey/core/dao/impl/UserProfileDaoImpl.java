package com.realtech.socialsurvey.core.dao.impl;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
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
import com.realtech.socialsurvey.core.exception.InvalidInputException;


@Component ( "userProfile")
public class UserProfileDaoImpl extends GenericDaoImpl<UserProfile, Long> implements UserProfileDao
{

    private static final Logger LOG = LoggerFactory.getLogger( UserProfileDaoImpl.class );
    private final String regionUserSearchQuery = "SELECT US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS, group_concat(UP.BRANCH_ID) as BRANCH_ID, group_concat(UP.REGION_ID) as REGION_ID, group_concat(UP.PROFILES_MASTER_ID) as PROFILES_MASTER_ID FROM  USER_PROFILE AS UP JOIN (SELECT USER_ID, REGION_ID, COMPANY_ID FROM USER_PROFILE where USER_ID = ? and PROFILES_MASTER_ID = ? and COMPANY_ID = ?) AS subQuery_UP ON subQuery_UP.REGION_ID = UP.REGION_ID and subQuery_UP.COMPANY_ID = UP.COMPANY_ID and UP.STATUS != ? JOIN USERS AS US ON US.USER_ID = UP.USER_ID GROUP BY US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS";
    private final String branchUserSearchQuery = "SELECT US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS, group_concat(UP.BRANCH_ID) as BRANCH_ID, group_concat(UP.REGION_ID) as REGION_ID, group_concat(UP.PROFILES_MASTER_ID) as PROFILES_MASTER_ID FROM  USER_PROFILE AS UP JOIN (SELECT USER_ID, BRANCH_ID, REGION_ID, COMPANY_ID FROM USER_PROFILE where USER_ID = ? and PROFILES_MASTER_ID = ? and COMPANY_ID = ?) AS subQuery_UP ON subQuery_UP.BRANCH_ID = UP.BRANCH_ID and subQuery_UP.REGION_ID = UP.REGION_ID and subQuery_UP.COMPANY_ID = UP.COMPANY_ID  and UP.STATUS != ? JOIN USERS AS US ON US.USER_ID = UP.USER_ID GROUP BY US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS";
    private final String companyUserSearchQuery = "SELECT US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS, group_concat(UP.BRANCH_ID) as BRANCH_ID, group_concat(UP.REGION_ID) as REGION_ID, group_concat(UP.PROFILES_MASTER_ID) as PROFILES_MASTER_ID FROM  USER_PROFILE AS UP JOIN (SELECT USER_ID, COMPANY_ID FROM USER_PROFILE where USER_ID = ? and PROFILES_MASTER_ID = ? and COMPANY_ID = ? ) AS subQuery_UP ON subQuery_UP.COMPANY_ID = UP.COMPANY_ID and UP.STATUS != ? JOIN USERS AS US ON US.USER_ID = UP.USER_ID GROUP BY US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS";


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
     * Method to activate all the user profiles for a given user.
     */

    @Override
    public void activateAllUserProfilesForUser( User userToBeActivated )
    {

        LOG.info( "Method activateUserProfileByUser called to deactivate user : " + userToBeActivated.getFirstName() );
        Query query = getSession().getNamedQuery( "UserProfile.updateProfileByUser" );
        // Setting status for user profile as inactive.
        query.setParameter( 0, CommonConstants.STATUS_ACTIVE );
        query.setParameter( 1, String.valueOf( CommonConstants.REALTECH_ADMIN_ID ) );
        query.setParameter( 2, new Timestamp( System.currentTimeMillis() ) );
        query.setParameter( 3, userToBeActivated );
        query.executeUpdate();
        LOG.info( "Method activateUserProfileByUser called to deactivate user : " + userToBeActivated.getFirstName() );

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
        Query query = getSession().createSQLQuery( "SELECT COUNT(*) FROM ( " + branchUserSearchQuery + " ) as subQuery" );
        query.setParameter( 0, user.getUserId() );
        query.setParameter( 1, CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID );
        query.setParameter( 2, user.getCompany().getCompanyId() );
        query.setParameter( 3, CommonConstants.STATUS_INACTIVE );

        LOG.info( "Method call ended for getUsersUnderBranchAdminCount for branch admin id : " + user.getUserId() );
        return ( (BigInteger) query.uniqueResult() ).intValue();
    }


    @Override
    @Transactional
    public int getUsersUnderRegionAdminCount( User user )
    {
        LOG.info( "Method call started for getUsersUnderRegionAdminCount for region admin id : " + user.getUserId() );
        Query query = getSession().createSQLQuery( "SELECT COUNT(*) FROM ( " + regionUserSearchQuery + " ) as subQuery" );
        query.setParameter( 0, user.getUserId() );
        query.setParameter( 1, CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID );
        query.setParameter( 2, user.getCompany().getCompanyId() );
        query.setParameter( 3, CommonConstants.STATUS_INACTIVE );

        LOG.info( "Method call ended for getUsersUnderRegionAdminCount for region admin id : " + user.getUserId() );
        return ( (BigInteger) query.uniqueResult() ).intValue();
    }


    @Override
    @Transactional
    public int getUsersUnderCompanyAdminCount( User user )
    {
        LOG.info( "Method call started for getUsersUnderCompanyAdminCount for company admin id : " + user.getUserId() );
        Query query = getSession().createSQLQuery( "SELECT COUNT(*) FROM ( " + companyUserSearchQuery + " ) as subQuery" );
        query.setParameter( 0, user.getUserId() );
        query.setParameter( 1, CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID );
        query.setParameter( 2, user.getCompany().getCompanyId() );
        query.setParameter( 3, CommonConstants.STATUS_INACTIVE );

        LOG.info( "Method call ended for getUsersUnderCompanyAdminCount for company admin id : " + user.getUserId() );
        return ( (BigInteger) query.uniqueResult() ).intValue();
    }


    @Override
    @Transactional
    public List<UserFromSearch> findUsersUnderBranchAdmin( User user, int startIndex, int batchSize )
    {
        List<UserFromSearch> userList = new ArrayList<UserFromSearch>();
        LOG.info( "Method call started for findUsersUnderBranchAdmin for branch admin id : " + user.getUserId() );
        Query query = getSession().createSQLQuery( branchUserSearchQuery );
        query.setParameter( 0, user.getUserId() );
        query.setParameter( 1, CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID );
        query.setParameter( 2, user.getCompany().getCompanyId() );
        query.setParameter( 3, CommonConstants.STATUS_INACTIVE );
        if ( startIndex > -1 ) {
            query.setFirstResult( startIndex );
        }
        if ( batchSize > -1 ) {
            query.setMaxResults( batchSize );
        }

        List<Object[]> rows = (List<Object[]>) query.list();

        userList = buildUserFromSearch( rows );

        LOG.info( "Method call ended for findUsersUnderBranchAdmin for branch admin id : " + user.getUserId() );
        return userList;
    }


    @Override
    @Transactional
    public List<UserFromSearch> findUsersUnderRegionAdmin( User admin, int startIndex, int batchSize )
    {
        List<UserFromSearch> userList = new ArrayList<UserFromSearch>();
        LOG.info( "Method call started for findUsersUnderRegionAdmin for region admin id : " + admin.getUserId() );
        Query query = getSession().createSQLQuery( regionUserSearchQuery );

        query.setParameter( 0, admin.getUserId() );
        query.setParameter( 1, CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID );
        query.setParameter( 2, admin.getCompany().getCompanyId() );
        query.setParameter( 3, CommonConstants.STATUS_INACTIVE );
        if ( startIndex > -1 ) {
            query.setFirstResult( startIndex );
        }
        if ( batchSize > -1 ) {
            query.setMaxResults( batchSize );
        }

        List<Object[]> rows = (List<Object[]>) query.list();

        userList = buildUserFromSearch( rows );

        LOG.info( "Method call ended for findUsersUnderRegionAdmin for region admin id : " + admin.getUserId() );
        return userList;
    }


    @Override
    @Transactional
    public List<UserFromSearch> findUsersUnderCompanyAdmin( User admin, int startIndex, int batchSize )
    {
        List<UserFromSearch> userList = new ArrayList<UserFromSearch>();
        LOG.info( "Method call started for findUsersUnderCompanyAdmin for company admin id : " + admin.getUserId() );
        Query query = getSession().createSQLQuery( companyUserSearchQuery );

        query.setParameter( 0, admin.getUserId() );
        query.setParameter( 1, CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID );
        query.setParameter( 2, admin.getCompany().getCompanyId() );
        query.setParameter( 3, CommonConstants.STATUS_INACTIVE );
        if ( startIndex > -1 ) {
            query.setFirstResult( startIndex );
        }
        if ( batchSize > -1 ) {
            query.setMaxResults( batchSize );
        }

        List<Object[]> rows = (List<Object[]>) query.list();

        userList = buildUserFromSearch( rows );
        LOG.info( "Method call ended for findUsersUnderCompanyAdmin for company admin id : " + admin.getUserId() );
        return userList;
    }


    @Override
    @Transactional
    public List<UserFromSearch> getUserFromSearchByUserIds( Set<Long> userIds )
    {
        List<UserFromSearch> userList = new ArrayList<UserFromSearch>();
        Long[] defaultIds = new Long[] { CommonConstants.DEFAULT_REGION_ID, CommonConstants.DEFAULT_COMPANY_ID };
        LOG.info( "Method call started for getUserFromSearchByUserIds for user ids : " + userIds );
        String queryStr = "SELECT US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS, group_concat(UP.BRANCH_ID) as BRANCH_ID, group_concat(UP.REGION_ID) as REGION_ID, group_concat(UP.PROFILES_MASTER_ID) as PROFILES_MASTER_ID FROM USER_PROFILE UP JOIN USERS US ON US.USER_ID = UP.USER_ID WHERE UP.USER_ID IN ( :userIds ) AND UP.BRANCH_ID NOT IN ( :branchIds ) AND UP.REGION_ID NOT IN ( :regionIds ) GROUP BY US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS";
        Query query = getSession().createSQLQuery( queryStr );
        query.setParameterList( "userIds", userIds );
        query.setParameterList( "branchIds", defaultIds );
        query.setParameterList( "regionIds", defaultIds );
        List<Object[]> rows = (List<Object[]>) query.list();

        userList = buildUserFromSearch( rows );
        LOG.info( "Method call ended for getUserFromSearchByUserIds for user ids" );
        return userList;
    }

    private String getCustomerDisplayName( String firstName, String lastName )
    {
        String displayName = firstName;
        if ( lastName != null && !lastName.isEmpty() )
            displayName += " " + lastName;
        return displayName;
    }


    private void setAdminLevelsForUserFromSearch( UserFromSearch userFromSearch, String[] profileMasterIds )
    {
        List<Integer> profMasterIds = new ArrayList<Integer>();
        for ( String profileMasterId : profileMasterIds ) {
            profMasterIds.add( Integer.parseInt( profileMasterId ) );
        }

        setAdminLevelsForUserFromSearch( userFromSearch, ArrayUtils.toPrimitive( profMasterIds.toArray( new Integer[0] ) ) );
    }


    private void setAdminLevelsForUserFromSearch( UserFromSearch userFromSearch, int[] profileMasterIds )
    {
        if ( userFromSearch.getIsOwner() == CommonConstants.IS_OWNER ) {
            userFromSearch.setRegionAdmin( true );
            userFromSearch.setBranchAdmin( true );
        }

        for ( int profileMasterId : profileMasterIds ) {
            switch ( profileMasterId ) {
                case CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID:
                    userFromSearch.setRegionAdmin( true );
                    break;
                case CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID:
                    userFromSearch.setBranchAdmin( true );
                    break;
                case CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID:
                    userFromSearch.setAgent( true );
                    break;
            }
        }
    }


    private List<UserFromSearch> buildUserFromSearch( List<Object[]> rows )
    {
        LOG.info( "Method call buildUserFromSearch started" );
        List<UserFromSearch> userList = new ArrayList<UserFromSearch>();
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

            String[] bIds = String.valueOf( row[8] ).split( "," );
            for ( String bId : bIds ) {
                long branchId = Long.parseLong( String.valueOf( bId ) );
                if ( !branchIds.contains( branchId ) )
                    branchIds.add( branchId );
            }

            List<Long> regionIds = new ArrayList<Long>();
            String[] rIds = String.valueOf( row[9] ).split( "," );
            for ( String rId : rIds ) {
                long regionId = Long.parseLong( String.valueOf( rId ) );
                if ( !regionIds.contains( regionId ) )
                    regionIds.add( regionId );
            }

            userFromSearch.setBranches( branchIds );
            userFromSearch.setRegions( regionIds );

            setAdminLevelsForUserFromSearch( userFromSearch, String.valueOf( row[10] ).split( "," ) );

            userFromSearch
                .setDisplayName( getCustomerDisplayName( userFromSearch.getFirstName(), userFromSearch.getLastName() ) );

            userList.add( userFromSearch );
        }
        LOG.info( "Method call buildUserFromSearch ended" );
        return userList;
    }


    /**
     * Method to update regionId for a specific branchId in user profiles
     * @param branchId
     * @param regionId
     * @throws InvalidInputException 
     */
    @Override
    public void updateRegionIdForBranch( long branchId, long regionId ) throws InvalidInputException
    {
        LOG.info( "Method to update regionId to " + regionId + " for branchId : " + branchId + " in USER_PROFILE started." );
        //Check if regionId is invalid
        if ( regionId <= 0l ) {
            throw new InvalidInputException( "Invalid regionId : " + regionId );
        }

        if ( branchId <= 0l ) {
            throw new InvalidInputException( "Invalid branchId : " + branchId );
        }
        String queryStr = "UPDATE USER_PROFILE SET REGION_ID = ? WHERE BRANCH_ID = ?";
        Query query = getSession().createSQLQuery( queryStr );
        query.setParameter( 0, regionId );
        query.setParameter( 1, branchId );
        query.executeUpdate();
        LOG.info( "Method to update regionId to " + regionId + " for branchId : " + branchId + " in USER_PROFILE finished." );
    }
 }
