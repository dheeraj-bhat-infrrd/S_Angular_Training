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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.JobLogDetails;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;


@Component ( "userProfile")
public class UserProfileDaoImpl extends GenericDaoImpl<UserProfile, Long> implements UserProfileDao
{
	@Autowired
	CompanyDao companyDao;

    private static final Logger LOG = LoggerFactory.getLogger( UserProfileDaoImpl.class );
    private final String regionUserSearchQuery = "SELECT US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS, group_concat(UP.BRANCH_ID) as BRANCH_ID, group_concat(UP.REGION_ID) as REGION_ID, group_concat(UP.PROFILES_MASTER_ID) as PROFILES_MASTER_ID, CONCAT(US.FIRST_NAME, ( CASE WHEN US.LAST_NAME IS NOT NULL THEN CONCAT (' ', US.LAST_NAME) ELSE '' END)) as DISPLAY_NAME FROM  USER_PROFILE AS UP JOIN (SELECT USER_ID, REGION_ID, COMPANY_ID FROM USER_PROFILE where USER_ID = ? and PROFILES_MASTER_ID = ? and COMPANY_ID = ?) AS subQuery_UP ON subQuery_UP.REGION_ID = UP.REGION_ID and subQuery_UP.COMPANY_ID = UP.COMPANY_ID and UP.STATUS != ? JOIN USERS AS US ON US.USER_ID = UP.USER_ID GROUP BY US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS ORDER BY DISPLAY_NAME ASC";
    private final String branchUserSearchQuery = "SELECT US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS, group_concat(UP.BRANCH_ID) as BRANCH_ID, group_concat(UP.REGION_ID) as REGION_ID, group_concat(UP.PROFILES_MASTER_ID) as PROFILES_MASTER_ID, CONCAT(US.FIRST_NAME, ( CASE WHEN US.LAST_NAME IS NOT NULL THEN CONCAT (' ', US.LAST_NAME) ELSE '' END)) as DISPLAY_NAME FROM  USER_PROFILE AS UP JOIN (SELECT USER_ID, BRANCH_ID, REGION_ID, COMPANY_ID FROM USER_PROFILE where USER_ID = ? and PROFILES_MASTER_ID = ? and COMPANY_ID = ?) AS subQuery_UP ON subQuery_UP.BRANCH_ID = UP.BRANCH_ID and subQuery_UP.REGION_ID = UP.REGION_ID and subQuery_UP.COMPANY_ID = UP.COMPANY_ID  and UP.STATUS != ? JOIN USERS AS US ON US.USER_ID = UP.USER_ID GROUP BY US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS ORDER BY DISPLAY_NAME ASC";
    private final String companyUserSearchQuery = "SELECT US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS, group_concat(UP.BRANCH_ID) as BRANCH_ID, group_concat(UP.REGION_ID) as REGION_ID, group_concat(UP.PROFILES_MASTER_ID) as PROFILES_MASTER_ID, CONCAT(US.FIRST_NAME, ( CASE WHEN US.LAST_NAME IS NOT NULL THEN CONCAT (' ', US.LAST_NAME) ELSE '' END)) as DISPLAY_NAME FROM  USER_PROFILE AS UP JOIN (SELECT USER_ID, COMPANY_ID FROM USER_PROFILE where USER_ID = ? and PROFILES_MASTER_ID = ? and COMPANY_ID = ? ) AS subQuery_UP ON subQuery_UP.COMPANY_ID = UP.COMPANY_ID and UP.STATUS != ? JOIN USERS AS US ON US.USER_ID = UP.USER_ID GROUP BY US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS ORDER BY DISPLAY_NAME ASC";


    private static final String getUserProfileByUserIdsQuery = "select UP.USER_PROFILE_ID , UP.STATUS  , UP.AGENT_ID , UP.BRANCH_ID , UP.REGION_ID ,  "
        + " UP.PROFILES_MASTER_ID , B.BRANCH , R.REGION from " + "USER_PROFILE UP JOIN BRANCH B ON UP.BRANCH_ID = B.BRANCH_ID JOIN REGION R ON UP.REGION_ID = R.REGION_ID where UP.AGENT_ID IN (:userIds)";


    /*
     * Method to deactivate all the user profiles for a given user.
     */

    @Override
    public void deactivateAllUserProfilesForUser( User admin, User userToBeDeactivated, int status )
    {

        LOG.debug( "Method deactivateUserProfileByUser called to deactivate user : {}", userToBeDeactivated.getFirstName() );
        Query query = getSession().getNamedQuery( "UserProfile.updateProfileByUser" );
        // Setting status for user profile as inactive.
        query.setParameter( 0, status );
        query.setParameter( 1, String.valueOf( admin.getUserId() ) );
        query.setParameter( 2, new Timestamp( System.currentTimeMillis() ) );
        query.setParameter( 3, userToBeDeactivated );
        query.executeUpdate();
        LOG.debug( "Method deactivateUserProfileByUser called to deactivate user : {}", userToBeDeactivated.getFirstName() );

    }


    /*
     * Method to activate all the user profiles for a given user.
     */

    @Override
    public void activateAllUserProfilesForUser( User userToBeActivated )
    {

        LOG.debug( "Method activateUserProfileByUser called to deactivate user : " + userToBeActivated.getFirstName() );
        Query query = getSession().getNamedQuery( "UserProfile.updateProfileByUser" );
        // Setting status for user profile as inactive.
        query.setParameter( 0, CommonConstants.STATUS_ACTIVE );
        query.setParameter( 1, String.valueOf( CommonConstants.REALTECH_ADMIN_ID ) );
        query.setParameter( 2, new Timestamp( System.currentTimeMillis() ) );
        query.setParameter( 3, userToBeActivated );
        query.executeUpdate();
        LOG.debug( "Method activateUserProfileByUser called to deactivate user : " + userToBeActivated.getFirstName() );

    }


    /*
     * Method to remove a branch admin.
     */
    @Override
    public void deactivateUserProfileForBranch( User admin, long branchId, User userToBeDeactivated )
    {
        LOG.debug( "Method deactivateUserProfileForBranch called to deactivate user : " + userToBeDeactivated.getFirstName() );
        Query query = getSession().getNamedQuery( "UserProfile.updateByUser" );
        // Setting status for user profile as inactive.
        query.setParameter( 0, CommonConstants.STATUS_INACTIVE );
        query.setParameter( 1, String.valueOf( admin.getUserId() ) );
        query.setParameter( 2, String.valueOf( new Timestamp( System.currentTimeMillis() ) ) );
        query.setParameter( 3, userToBeDeactivated );
        query.setParameter( 4, branchId );
        query.executeUpdate();
        LOG.debug( "Method deactivateUserProfileForBranch called to deactivate user : " + userToBeDeactivated.getFirstName() );
    }


    /*
     * Method to deactivate a region admin.
     */
    @Override
    public void deactivateUserProfileForRegion( User admin, long regionId, User userToBeDeactivated )
    {
        LOG.debug( "Method deactivateUserProfileForBranch called to deactivate user : " + userToBeDeactivated.getFirstName() );
        Query query = getSession().getNamedQuery( "UserProfile.updateByUser" );
        // Setting status for user profile as inactive.
        query.setParameter( 0, CommonConstants.STATUS_INACTIVE );
        query.setParameter( 1, String.valueOf( admin.getUserId() ) );
        query.setParameter( 2, String.valueOf( new Timestamp( System.currentTimeMillis() ) ) );
        query.setParameter( 3, userToBeDeactivated );
        query.setParameter( 4, regionId );
        query.executeUpdate();
        LOG.debug( "Method deactivateUserProfileForBranch called to deactivate user : " + userToBeDeactivated.getFirstName() );
    }


    @Override
    @SuppressWarnings ( "unchecked")
    public List<Long> getBranchIdsForUser( User user )
    {
        LOG.debug( "Method getBranchIdsForUser called to fetch branch ids assigned to user : " + user.getFirstName() );
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
        LOG.debug( "Method getBranchIdsForUser finished to fetch branch ids assigned to user : " + user.getFirstName() );
        return branchIds;
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<Long> getBranchesForAdmin( User user, List<ProfilesMaster> profilesMasters )
    {
        LOG.debug( "Method getBranchesForAdmin() called to fetch branches assigned to user : " + user.getFirstName() );
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
        LOG.debug( "Method getBranchesForAdmin() finished to fetch branches assigned to user : " + user.getFirstName() );
        return branchIds;
    }


    @Override
    public void deleteUserProfilesByCompany( long companyId )
    {
        LOG.debug( "Method deleteUserProfilesByCompany() called to delete profiles of company id : " + companyId );
        Query query = getSession().createQuery( "delete from UserProfile where company.companyId=?" );
        query.setParameter( 0, companyId );
        query.executeUpdate();
        LOG.debug( "Method deleteUserProfilesByCompany() finished." );
    }

    @Override
    public void deleteUserProfilesByUser( long userId )
    {
        LOG.debug( "Method deleteUserProfilesByUser() called to delete profiles of userId id : " + userId );
        Query query = getSession().createQuery( "delete from UserProfile where user.userId=?" );
        query.setParameter( 0, userId );
        query.executeUpdate();
        LOG.debug( "Method deleteUserProfilesByUser() finished." );
    }

    @SuppressWarnings ( "unchecked")
    @Override
    public List<UserProfile> findUserProfilesInBatch( Map<String, Object> queries, int startIndex, int batchSize )
    {

        LOG.debug( "Method findUserProfilesInBatch() called" );

        Criteria criteria = getSession().createCriteria( UserProfile.class );
        try {
            for ( Entry<String, Object> query : queries.entrySet() ) {
                criteria.add( Restrictions.eq( query.getKey(), query.getValue() ) );
            }
            criteria.createAlias( "user", "alias" );
            if ( startIndex > -1 ) {
                criteria.setFirstResult( startIndex );
            }
            if ( batchSize > -1 ) {
                criteria.setMaxResults( batchSize );
            }
            criteria.addOrder( Order.asc( "alias.firstName" ) );
            criteria.addOrder( Order.asc( "alias.lastName" ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "HibernateException caught in findByKeyValueAscendingWithAlias().", hibernateException );
            throw new DatabaseException( "HibernateException caught in findByKeyValueAscendingWithAlias().", hibernateException );
        }
        return criteria.list();
    }


    @Override
    @Transactional
    public Map<String, Long> findPrimaryUserProfileByAgentId( long entityId )
    {
        LOG.debug( "Method findPrimaryUserProfileByAgentId() called for agent id : {}", entityId );
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
        LOG.debug( "Method deleteUserProfilesByCompany() finished." );
        return hierarchyMap;
    }


    @Override
    public Set<Long> findUserIdsByBranch( long branchId )
    {
        LOG.debug( "Method call started for findUserIdsByBranch for branch : {}", branchId );
        Set<Long> userIds = new HashSet<Long>();

        LOG.debug( "Fetching users for branch : {}", branchId );
        Query query = getSession().createSQLQuery( "SELECT USER_ID FROM USER_PROFILE WHERE STATUS = ? and BRANCH_ID = ?" );
        query.setParameter( 0, CommonConstants.STATUS_ACTIVE );
        query.setParameter( 1, branchId );

        List<Integer> rows = (List<Integer>) query.list();
        for ( Integer row : rows ) {
            userIds.add( Long.valueOf( row.intValue() ) );
        }

        if(LOG.isDebugEnabled()){
        	LOG.debug( "Fetched {} users for branch : {}",userIds.size(),branchId );
        }
        LOG.debug( "Method call ended for findUserIdsByBranch for branch : {}",branchId );
        return userIds;
    }


    @Override
    public Set<Long> findUserIdsByRegion( long regionId )
    {
        LOG.debug( "Method call started for findUserIdsByRegion for region : {}", regionId );
        Set<Long> userIds = new HashSet<Long>();

        LOG.debug( "Fetching users for region : {}", regionId );
        Query query = getSession().createSQLQuery( "SELECT USER_ID FROM USER_PROFILE WHERE STATUS = ? and REGION_ID = ?" );
        query.setParameter( 0, CommonConstants.STATUS_ACTIVE );
        query.setParameter( 1, regionId );

        List<Integer> rows = (List<Integer>) query.list();
        for ( Integer row : rows ) {
            userIds.add( Long.valueOf( row.intValue() ) );
        }

        if(LOG.isDebugEnabled()){
        	LOG.debug( "Fetched {} users for region : {}",userIds.size(),regionId );
        }
        LOG.debug( "Method call ended for findUserIdsByRegion for region : {}", regionId );
        return userIds;
    }


    @Override
    @Transactional
    public int getUsersUnderBranchAdminCount( User user )
    {
        LOG.debug( "Method call started for getUsersUnderBranchAdminCount for branch admin id : {}", user.getUserId() );
        Query query = getSession().createSQLQuery( "SELECT COUNT(*) FROM ( " + branchUserSearchQuery + " ) as subQuery" );
        query.setParameter( 0, user.getUserId() );
        query.setParameter( 1, CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID );
        query.setParameter( 2, user.getCompany().getCompanyId() );
        query.setParameter( 3, CommonConstants.STATUS_INACTIVE );

        LOG.debug( "Method call ended for getUsersUnderBranchAdminCount for branch admin id : {}", user.getUserId() );
        return ( (BigInteger) query.uniqueResult() ).intValue();
    }


    @Override
    @Transactional
    public int getUsersUnderRegionAdminCount( User user )
    {
        LOG.debug( "Method call started for getUsersUnderRegionAdminCount for region admin id : {}" , user.getUserId() );
        Query query = getSession().createSQLQuery( "SELECT COUNT(*) FROM ( " + regionUserSearchQuery + " ) as subQuery" );
        query.setParameter( 0, user.getUserId() );
        query.setParameter( 1, CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID );
        query.setParameter( 2, user.getCompany().getCompanyId() );
        query.setParameter( 3, CommonConstants.STATUS_INACTIVE );

        LOG.debug( "Method call ended for getUsersUnderRegionAdminCount for region admin id : {}" , user.getUserId() );
        return ( (BigInteger) query.uniqueResult() ).intValue();
    }


    @Override
    @Transactional
    public int getUsersUnderCompanyAdminCount( User user )
    {
        LOG.debug( "Method call started for getUsersUnderCompanyAdminCount for company admin id : {}", user.getUserId() );
        Query query = getSession().createSQLQuery( "SELECT COUNT(*) FROM ( " + companyUserSearchQuery + " ) as subQuery" );
        query.setParameter( 0, user.getUserId() );
        query.setParameter( 1, CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID );
        query.setParameter( 2, user.getCompany().getCompanyId() );
        query.setParameter( 3, CommonConstants.STATUS_INACTIVE );

        LOG.debug( "Method call ended for getUsersUnderCompanyAdminCount for company admin id : {}",  user.getUserId() );
        return ( (BigInteger) query.uniqueResult() ).intValue();
    }


    @Override
    @Transactional
    public List<UserFromSearch> findUsersUnderBranchAdmin( User user, int startIndex, int batchSize )
    {
        List<UserFromSearch> userList = new ArrayList<UserFromSearch>();
        LOG.debug( "Method call started for findUsersUnderBranchAdmin for branch admin id : {}", user.getUserId() );
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

        LOG.debug( "Method call ended for findUsersUnderBranchAdmin for branch admin id : {}", user.getUserId() );
        return userList;
    }


    @Override
    @Transactional
    public List<UserFromSearch> findUsersUnderRegionAdmin( User admin, int startIndex, int batchSize )
    {
        List<UserFromSearch> userList = new ArrayList<UserFromSearch>();
        LOG.debug( "Method call started for findUsersUnderRegionAdmin for region admin id : {}", admin.getUserId() );
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

        LOG.debug( "Method call ended for findUsersUnderRegionAdmin for region admin id : {}", admin.getUserId() );
        return userList;
    }


    @Override
    @Transactional
    public List<UserFromSearch> findUsersUnderCompanyAdmin( User admin, int startIndex, int batchSize )
    {
        List<UserFromSearch> userList = new ArrayList<UserFromSearch>();
        LOG.debug( "Method call started for findUsersUnderCompanyAdmin for company admin id : {}" , admin.getUserId() );
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
        LOG.debug( "Method call ended for findUsersUnderCompanyAdmin for company admin id : {}", admin.getUserId() );
        return userList;
    }


    @Override
    @Transactional
    public List<UserFromSearch> getUserFromSearchByUserIds( Set<Long> userIds )
    {
        List<UserFromSearch> userList = new ArrayList<UserFromSearch>();
        LOG.debug( "Method call started for getUserFromSearchByUserIds for user ids : " + userIds );
        String queryStr = "SELECT US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS, group_concat(UP.BRANCH_ID) as BRANCH_ID, group_concat(UP.REGION_ID) as REGION_ID, group_concat(UP.PROFILES_MASTER_ID) as PROFILES_MASTER_ID, CONCAT(US.FIRST_NAME, ( CASE WHEN US.LAST_NAME IS NOT NULL THEN CONCAT (' ', US.LAST_NAME) ELSE '' END)) as DISPLAY_NAME FROM USER_PROFILE UP JOIN USERS US ON US.USER_ID = UP.USER_ID WHERE UP.USER_ID IN ( :userIds ) GROUP BY US.USER_ID, US.FIRST_NAME, US.LAST_NAME, US.EMAIL_ID, US.LOGIN_NAME, US.IS_OWNER, US.COMPANY_ID, US.STATUS ORDER BY DISPLAY_NAME";
        Query query = getSession().createSQLQuery( queryStr );
        query.setParameterList( "userIds", userIds );
        List<Object[]> rows = (List<Object[]>) query.list();

        userList = buildUserFromSearch( rows );
        LOG.debug( "Method call ended for getUserFromSearchByUserIds for user ids" );
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
        LOG.debug( "Method call buildUserFromSearch started" );
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
        LOG.debug( "Method call buildUserFromSearch ended" );
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
        LOG.debug( "Method to update regionId to " + regionId + " for branchId : " + branchId + " in USER_PROFILE started." );
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
        LOG.debug( "Method to update regionId to " + regionId + " for branchId : " + branchId + " in USER_PROFILE finished." );
    }


    /**
     * Method to update emailId for a user's user profiles
     * @param userId
     * @param emailId
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public void updateEmailIdForUserProfile( long userId, String emailId ) throws InvalidInputException
    {
        LOG.debug( "Method to update emailId to : " + emailId + " for user profiles of user ID : " + userId + " started." );
        //Check if userId and emailId are null
        if ( userId <= 0l ) {
            throw new InvalidInputException( "Invalid user ID  : " + userId );
        }

        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "EmailId cannot be empty" );
        }

        String hqlUpdate = "update UserProfile up set up.emailId = :emailId where up.user.userId = :userId";
        Query query = getSession().createQuery( hqlUpdate );
        query.setString( "emailId", emailId );
        query.setLong( "userId", userId );
        query.executeUpdate();
        LOG.debug( "Method to update emailId to : " + emailId + " for user profiles of user ID : " + userId + " finished." );
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public Map<Long, List<UserProfile>> getUserProfilesForUsers( List<Long> userIds )
    {
        LOG.debug( "Method getUserProfilesForUsers started for user ids : " + userIds );
        Query query = getSession().createSQLQuery( getUserProfileByUserIdsQuery );
        query.setParameterList( "userIds", userIds );


        Map<Long, List<UserProfile>> userUserProfileMap = new HashMap<Long, List<UserProfile>>();
        LOG.debug( "QUERY : " + query.getQueryString() );
        List<Object[]> rows = (List<Object[]>) query.list();
 
        for ( Long userId : userIds ) {
            userUserProfileMap.put( userId, new ArrayList<UserProfile>() );
        }

        for ( Object[] row : rows ) {
            Long userId = Long.parseLong( String.valueOf( row[2] ) );
            UserProfile userProfile = new UserProfile();

            userProfile.setUserProfileId( Long.parseLong( String.valueOf( row[0] ) ) );
            userProfile.setStatus( Integer.parseInt( String.valueOf( row[1] ) ) );
            userProfile.setAgentId( Long.parseLong( String.valueOf( row[2] ) ) );
            userProfile.setBranchId( Long.parseLong( String.valueOf( row[3] ) ) );
            userProfile.setRegionId( Long.parseLong( String.valueOf( row[4] ) ) );

            ProfilesMaster profilesMaster = new ProfilesMaster();
            profilesMaster.setProfileId( Integer.parseInt( String.valueOf( row[5] ) ) );
            userProfile.setProfilesMaster( profilesMaster );
            
            if(row[6] != null){
                userProfile.setBranchName( String.valueOf( row[6] ) );
            }
            
            if(row[7] != null){
                userProfile.setRegionName( String.valueOf( row[7] ) );
            }

            List<UserProfile> profileListForUser = userUserProfileMap.get( userId );

            //add profile to the particular user's profile list
            profileListForUser.add( userProfile );
            //update profile list in map
            userUserProfileMap.put( userId, profileListForUser );
        }

        LOG.debug( "Method getUserProfilesForUsers ended for user ids : " + userIds );
        return userUserProfileMap;

    }
    
    /**
     * Method to get userProfile given the userId, branchId and regionId
     * 
     * @param userId
     * @param branchId
     * @param regionId
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    @Override
    @Transactional
    public UserProfile findUserProfile( long userId, long branchId, long regionId, int profilesMasterId ) throws NoRecordsFetchedException
    {
        LOG.debug( "Method to find userProfile for userId: " + userId + " branchId: " + branchId + " regionId : " + regionId + " started." );
        Criteria criteria = getSession().createCriteria( UserProfile.class );
        criteria.add( Restrictions.eq( CommonConstants.USER_COLUMN + "." + CommonConstants.USER_ID, userId ) );
        criteria.add( Restrictions.eq( CommonConstants.BRANCH_ID_COLUMN, branchId ) );
        criteria.add( Restrictions.eq( CommonConstants.REGION_ID_COLUMN, regionId ) );
        criteria.add( Restrictions.eq( CommonConstants.PROFILE_MASTER_COLUMN + "." + "profileId", profilesMasterId ) );
        @SuppressWarnings ( "unchecked")
        List<UserProfile> userProfiles = criteria.list();
        if ( userProfiles == null || userProfiles.isEmpty() ) {
            throw new NoRecordsFetchedException( "No records fetched for userId : " + userId + " branchId: " + branchId + " regionId : " + regionId );
        }
        LOG.debug( "Method to find userProfile for userId: " + userId + " branchId: " + branchId + " regionId : " + regionId + " finished." );
        return userProfiles.get( 0 );
    }


	@SuppressWarnings("unchecked")
	@Override
	public List<UserProfile> getUserProfiles(Long companyId) {
		LOG.debug("Method to get userProfiles for companyId {} started", companyId);
		Criteria criteria = getSession().createCriteria(UserProfile.class).createAlias(CommonConstants.COMPANY , "comp" );
		try {
			criteria.add(Restrictions.eq("comp.companyId", companyId));
			criteria.add(Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE));
		} catch (HibernateException hibernateException) {
			LOG.warn("Exception caught in getUserProfiles() ", hibernateException);
			throw new DatabaseException("Exception caught in getUserProfiles() ", hibernateException);
		}
		LOG.debug("Method to get userProfiles for companyId {} finished", companyId);
		return (List<UserProfile>) criteria.list();
	}

    @Override
    public List<UserProfile> getUserProfiles( long userId )
    {
        LOG.debug( "Method to find userProfile for userId: " + userId + " started." );
        Criteria criteria = getSession().createCriteria( UserProfile.class );
        criteria.add( Restrictions.eq( CommonConstants.USER_COLUMN + "." + CommonConstants.USER_ID, userId ) );
        @SuppressWarnings ( "unchecked")
        List<UserProfile> userProfiles = criteria.list();
        LOG.debug( "Method to find userProfile for userId: " + userId + " finished." );        
        return userProfiles;
    }
 }
