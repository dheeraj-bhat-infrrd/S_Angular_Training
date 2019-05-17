package com.realtech.socialsurvey.core.dao.impl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
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
import com.realtech.socialsurvey.core.commons.SqlQueries;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.StateLookup;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.entities.ZipCodeLookup;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;


// JIRA SS-42 By RM-05 : BOC
@Component ( "user")
public class UserDaoImpl extends GenericDaoImpl<User, Long> implements UserDao
{

    private static final Logger LOG = LoggerFactory.getLogger( UserDaoImpl.class );

    @Autowired
    private CompanyDao companyDao;

    /**
     * Method to get name of the user
     * @param userId
     * @return
     */
    @Override
    public String getUserName( long userId )
    {
        LOG.debug( "Method getUserName started for userId : {}" , userId );
        Query query = getSession().createSQLQuery( SqlQueries.USER_NAME );

        query.setParameter( 0, userId );
        
        return (String) query.uniqueResult();
    }

    /*
     * Method to return all the users that match email id passed.
     */
    @SuppressWarnings ( "unchecked")
    @Override
    public List<User> fetchUsersBySimilarEmailId( User user, String emailId )
    {
        LOG.info( "Method to fetch all the users by email id,fetchUsersBySimilarEmailId() started." );
        Criteria criteria = getSession().createCriteria( User.class );
        try {
            criteria.add( Restrictions.ilike( CommonConstants.EMAIL_ID, "%" + emailId + "%" ) );
            criteria.add( Restrictions.eq( CommonConstants.COMPANY, user.getCompany() ) );

            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ) );
            criteria.add( criterion );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUsersBySimilarEmailId() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUsersBySimilarEmailId() ", hibernateException );
        }

        LOG.info( "Method to fetch all the users by email id, fetchUsersBySimilarEmailId() finished." );

        return (List<User>) criteria.list();
    }


    /*
     * Method to get count of active and unauthorized users belonging to a company.
     */
    @Override
    public long getUsersCountForCompany( Company company )
    {
        LOG.info(
            "Method to get count of active and unauthorized users belonging to a company, getUsersCountForCompany() started." );

        Criteria criteria = getSession().createCriteria( User.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY, company ) );

            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ) );
            criteria.add( criterion );
        } catch ( HibernateException hibernateException ) {
            throw new DatabaseException( "Exception caught in getUsersCountForCompany() ", hibernateException );
        }

        LOG.info(
            "Method to get count of active and unauthorized users belonging to a company, getUsersCountForCompany() finished." );
        return (long) criteria.setProjection( Projections.rowCount() ).uniqueResult();
    }


    /*
     * Method to get list of active and unauthorized users belonging to a company.
     */
    @SuppressWarnings ( "unchecked")
    @Override
    public List<User> getUsersForCompany( Company company )
    {
        LOG.info( "Method getUsersForCompany called to fetch list of users of company : " + company.getCompany() );
        Criteria criteria = getSession().createCriteria( User.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY, company ) );

            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_INCOMPLETE ) );
            criteria.add( criterion );
            criteria.addOrder( Order.asc( "firstName" ) );
            criteria.addOrder( Order.asc( "lastName" ) );
        } catch ( HibernateException hibernateException ) {
            throw new DatabaseException( "Exception caught in getUsersForCompany() ", hibernateException );
        }
        LOG.info( "Method getUsersForCompany finished to fetch list of users of company : " + company.getCompany() );
        return (List<User>) criteria.list();
    }


    /**
     * Method to get a list of all the active users' IDs in a company
     * @param company
     * @return
     */
    @SuppressWarnings ( "unchecked")
    @Override
    public Set<Long> getActiveUserIdsForCompany( Company company )
    {
        LOG.info( "Method getActiveUserIdsForCompany started for company : " + company.getCompany() );
        Criteria criteria = getSession().createCriteria( User.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY, company ) );

            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_INCOMPLETE ) );
            criteria.add( criterion );
            criteria.addOrder( Order.asc( "firstName" ) );
            criteria.addOrder( Order.asc( "lastName" ) );
            criteria.setProjection( Projections.property( CommonConstants.USER_ID ) );
        } catch ( HibernateException hibernateException ) {
            throw new DatabaseException( "Exception caught in getUsersForCompany() ", hibernateException );
        }
        List<Long> agentIds = (List<Long>) criteria.list();
        if ( agentIds == null ) {
            return new HashSet<Long>();
        }
        return new HashSet<Long>( agentIds );
    }


    /*
     * Method to check if any user exist with the email-id and is still active in a company
     */
    @Override
    public User getActiveUser( String emailId ) throws NoRecordsFetchedException
    {
        LOG.debug(
            "Method checkIfAnyActiveUserExists() called to check if any active user present with the Email id : " + emailId );
        Criteria criteria = getSession().createCriteria( User.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.LOGIN_NAME, emailId ).ignoreCase() );
            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_INCOMPLETE ) );
            criteria.add( criterion );
        } catch ( HibernateException hibernateException ) {
            throw new DatabaseException( "Exception caught in getUsersForCompany() ", hibernateException );
        }
        @SuppressWarnings ( "unchecked") List<User> users = criteria.list();
        if ( users == null || users.isEmpty() ) {
            LOG.debug( "No active users found with the emaild id " + emailId );
            throw new NoRecordsFetchedException( "No active user found for the emailid" );
        }
        LOG.debug( "Method checkIfAnyActiveUserExists() successfull, active user with the emailId " + emailId );
        return users.get( CommonConstants.INITIAL_INDEX );
    }


    /*
     * Method to return all the users that match email id passed.
     */
    @SuppressWarnings ( "unchecked")
    @Override
    public List<User> fetchUsersByEmailId( List<String> emailIds )
    {
        LOG.info( "Method to fetch all the users by email id,fetchUsersBySimilarEmailId() started." );
        Criteria criteria = getSession().createCriteria( User.class );
        try {
            criteria.add( Restrictions.in( CommonConstants.EMAIL_ID, emailIds ) );
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in fetchUsersBySimilarEmailId() ", hibernateException );
            throw new DatabaseException( "Exception caught in fetchUsersBySimilarEmailId() ", hibernateException );
        }

        LOG.info( "Method to fetch all the users by email id, fetchUsersBySimilarEmailId() finished." );

        return (List<User>) criteria.list();
    }


    /*
     * Method to delete all the users of a company.
     */
    @Override
    public void deleteUsersByCompanyId( long companyId )
    {
        LOG.info( "Method to delete all the users by company id,deleteUsersByCompanyId() started." );
        try {
            Query query = getSession().createQuery( "delete from User where company.companyId=?" );
            query.setParameter( 0, companyId );
            query.executeUpdate();
        } catch ( HibernateException hibernateException ) {
            LOG.error( "Exception caught in deleteUsersByCompanyId() ", hibernateException );
            throw new DatabaseException( "Exception caught in deleteUsersByCompanyId() ", hibernateException );
        }
        LOG.info( "Method to fetch all the users by email id, deleteUsersByCompanyId() finished." );
    }


    /*
     * (non-Javadoc)
     * @see com.realtech.socialsurvey.core.dao.UserDao#getActiveUserByEmailAndCompany(java.lang.String, com.realtech.socialsurvey.core.entities.Company)
     */
    @Override
    public User getActiveUserByEmailAndCompany( String emailId, Company company ) throws NoRecordsFetchedException
    {
        LOG.debug(
            "Method checkIfAnyActiveUserExists() called to check if any active user present with the Email id : " + emailId );
        Criteria criteria = getSession().createCriteria( User.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.LOGIN_NAME, emailId ) );
            criteria.add( Restrictions.eq( CommonConstants.COMPANY, company ) );
            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_INCOMPLETE ) );
            criteria.add( criterion );
        } catch ( HibernateException hibernateException ) {
            throw new DatabaseException( "Exception caught in getActiveUserByEmailAndCompany() ", hibernateException );
        }
        @SuppressWarnings ( "unchecked") List<User> users = criteria.list();
        if ( users == null || users.isEmpty() ) {
            LOG.debug( "No active users found with the emaild id " + emailId );
            throw new NoRecordsFetchedException( "No active user found for the emailid" );
        }
        LOG.debug( "Method getActiveUserByEmailAndCompany() successfull, active user with the emailId " + emailId );
        return users.get( CommonConstants.INITIAL_INDEX );
    }


    @Override
    public List<User> getUsersForUserIds( List<Long> userIds ) throws InvalidInputException
    {
        if ( userIds == null || userIds.size() == 0 ) {
            LOG.error( "User ids passed cannot be null or empty" );
            throw new InvalidInputException( "User ids passed cannot be null or empty" );
        }
        Criteria criteria = getSession().createCriteria( User.class );
        try {
            criteria.add( Restrictions.in( CommonConstants.USER_ID, userIds ) );

            criteria.addOrder( Order.asc( "firstName" ) );
            criteria.addOrder( Order.asc( "lastName" ) );

        } catch ( HibernateException hibernateException ) {
            throw new DatabaseException( "Exception caught in getUsersForUserIds() ", hibernateException );
        }
        @SuppressWarnings ( "unchecked") List<User> users = criteria.list();
        return users;
    }


    /*
     * Method to get list of active and unauthorized users belonging to a company with settings batch wise.
     */
    @SuppressWarnings ( "unchecked")
    @Override
    public List<User> getUsersForCompany( Company company, int start, int batch ) throws InvalidInputException
    {
        if ( company == null )
            throw new InvalidInputException( "Company passed in getBranchesForCompany() cannot be null" );
        LOG.debug( "Method getUsersForCompany called to fetch list of users of company : " + company.getCompany() );
        Criteria criteria = getSession().createCriteria( User.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY, company ) );

            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_INCOMPLETE ) );
            criteria.add( criterion );
            criteria.addOrder( Order.asc( "firstName" ) );
            criteria.addOrder( Order.asc( "lastName" ) );
            if ( start > 0 )
                criteria.setFirstResult( start );
            if ( batch > 0 )
                criteria.setFetchSize( batch );
        } catch ( HibernateException hibernateException ) {
            throw new DatabaseException( "Exception caught in getUsersForCompany() ", hibernateException );
        }
        LOG.debug( "Method getUsersForCompany finished to fetch list of users of company : " + company.getCompany() );
        return (List<User>) criteria.list();
    }


    /**
     * Method to fetch all user ids under company based on profile master
     * @param companyId
     * @throws InvalidInputException
     * */
    @SuppressWarnings ( "unchecked")
    @Override
    @Transactional
    public List<Long> getUserIdsUnderCompanyBasedOnProfileMasterId( long companyId, int profileMasterId, int start,
        int batchSize ) throws InvalidInputException
    {
        if ( companyId <= 0 ) {
            throw new InvalidInputException( "Invalid company id passed in getAgentIdsUnderCompany method" );
        }
        LOG.info( "Method to get all user ids under company id : " + companyId + " based on profile master id : "
            + profileMasterId + ",getUserIdsUnderCompanyBasedOnProfileMasterId() started." );
        Criteria criteria = null;
        try {
            criteria = getSession().createCriteria( User.class );
            criteria.setProjection( Projections.property( CommonConstants.USER_ID ) );
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_COLUMN, companyDao.findById( Company.class, companyId ) ) );
            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ) );
            criteria.add( criterion );
            if ( start >= 0 )
                criteria.setFirstResult( start );
            if ( batchSize > 0 ) {
                criteria.setMaxResults( batchSize );
            }
            criteria.add(
                Restrictions.sqlRestriction( "USER_ID in (select up.USER_ID from USER_PROFILE up where PROFILES_MASTER_ID="
                    + profileMasterId + " and STATUS=" + CommonConstants.STATUS_ACTIVE + ")" ) );
        } catch ( HibernateException e ) {
            LOG.error( "HibernateException caught in getUserIdsUnderCompanyBasedOnProfileMasterId(). Reason: " + e.getMessage(),
                e );
            throw new DatabaseException( "HibernateException caught in getUserIdsUnderCompanyBasedOnProfileMasterId().", e );
        }
        LOG.info( "Method to get all user ids under company id : " + companyId + " based on profile master id : "
            + profileMasterId + ",getUserIdsUnderCompanyBasedOnProfileMasterId() ended." );
        return criteria.list();
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<User> getUsersAndEmailMappingForCompany( Company company, int start, int batch ) throws InvalidInputException
    {
        if ( company == null )
            throw new InvalidInputException( "Company passed in getBranchesForCompany() cannot be null" );
        LOG.info( "Method getUsersForCompany called to fetch list of users of company : " + company.getCompany() );
        Criteria criteria = getSession().createCriteria( User.class, "user" );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY, company ) );

            criteria.createAlias( "user.userEmailMappings", "emailMapping" );
            criteria.add( Restrictions.eq( "emailMapping.status", CommonConstants.STATUS_ACTIVE ) );

            //criteria.add( Restrictions.sizeGt("userEmailMappings", 0) );
            //  criteria.setResultTransformer( CriteriaSpecification.DISTINCT_ROOT_ENTITY );


            criteria.setProjection( Projections.distinct( Projections.property( CommonConstants.USER_ID ) ) );


            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_INCOMPLETE ) );
            criteria.add( criterion );

            if ( start > -1 )
                criteria.setFirstResult( start );
            if ( batch > -1 )
                criteria.setMaxResults( batch );
        } catch ( HibernateException hibernateException ) {
            throw new DatabaseException( "Exception caught in getUsersForCompany() ", hibernateException );
        }
        LOG.info( "Method getUsersForCompany finished to fetch list of users of company : " + company.getCompany() );

        List<Long> userIds = criteria.list();
        List<User> users = new ArrayList<User>();
        if ( userIds != null && !userIds.isEmpty() )
            users = getUsersForUserIds( userIds );

        return users;
    }


    @Override
    public Long getCountOfUsersAndEmailMappingForCompany( Company company ) throws InvalidInputException
    {
        if ( company == null )
            throw new InvalidInputException( "Company passed in getBranchesForCompany() cannot be null" );
        LOG.info( "Method getUsersForCompany called to fetch list of users of company : " + company.getCompany() );
        Criteria criteria = getSession().createCriteria( User.class, "user" );
        Long count;
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY, company ) );

            criteria.createAlias( "user.userEmailMappings", "emailMapping" );
            criteria.add( Restrictions.eq( "emailMapping.status", CommonConstants.STATUS_ACTIVE ) );


            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_INCOMPLETE ) );
            criteria.add( criterion );

            //count distinct users by email id
            criteria.setProjection( Projections.countDistinct( CommonConstants.USER_ID ) );
            count = (Long) criteria.uniqueResult();

        } catch ( HibernateException hibernateException ) {
            throw new DatabaseException( "Exception caught in getUsersForCompany() ", hibernateException );
        }
        LOG.info( "Method getUsersForCompany finished to fetch list of users of company : " + company.getCompany() );
        return count;
    }


    @Override
    public boolean isEmailAlreadyTaken( String emailId )
    {
        LOG.debug( "Method isEmailAlreadyTaken() called to check if any user present with the Email id : " + emailId );
        Criteria criteria = getSession().createCriteria( User.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.LOGIN_NAME, emailId ) );
            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_INCOMPLETE ) );
            criteria.add( criterion );
        } catch ( HibernateException hibernateException ) {
            throw new DatabaseException( "Exception caught in getUsersForCompany() ", hibernateException );
        }
        @SuppressWarnings ( "unchecked") List<User> users = criteria.list();
        if ( users == null || users.isEmpty() ) {
            LOG.debug( "No active users found with the emaild id " + emailId );
            return false;
        }
        LOG.debug( "Method isEmailAlreadyTaken() successful, user with the emailId " + emailId );

        return true;
    }


    @Override
    public User getActiveOrIncompleteUser( String emailId ) throws NoRecordsFetchedException
    {
        LOG.debug( "Method getActiveOrIncompleteUser() called to check if any user present with the Email id : " + emailId );
        Criteria criteria = getSession().createCriteria( User.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.LOGIN_NAME, emailId ) );
            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_INCOMPLETE ) );
            criteria.add( criterion );
        } catch ( HibernateException hibernateException ) {
            throw new DatabaseException( "Exception caught in getUsersForCompany() ", hibernateException );
        }
        @SuppressWarnings ( "unchecked") List<User> users = criteria.list();
        if ( users == null || users.isEmpty() ) {
            LOG.debug( "No active users found with the emaild id " + emailId );
            throw new NoRecordsFetchedException( "No active user found for the emailid" );
        }
        LOG.debug( "Method getActiveOrIncompleteUser() successfull, active user with the emailId " + emailId );
        return users.get( CommonConstants.INITIAL_INDEX );
    }

    
    @SuppressWarnings ( "unchecked")
    @Override
    @Transactional
    public List<String> getRegisteredEmailsInOtherCompanies( Company company ) throws InvalidInputException{
        if ( company == null ) {
            throw new InvalidInputException( "Invalid company id passed in getAgentIdsUnderCompany method" );
        }
        LOG.info( "Method to get all user emails address other than under  company id : " + company.getCompanyId() + " started." );
        Criteria criteria = null;
        try {
            criteria = getSession().createCriteria( User.class );
            criteria.setProjection( Projections.property( CommonConstants.EMAIL_ID ) );
            criteria.add( Restrictions.ne( CommonConstants.COMPANY_COLUMN , company));

            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_INCOMPLETE ) );

            criteria.add( criterion );
            
            LOG.info( "Method to get all user emails address other than under  company id : " + company.getCompanyId() + " ended." );
            return criteria.list();
    } catch ( HibernateException hibernateException ) {
        throw new DatabaseException( "Exception caught in getUsersForCompany() ", hibernateException );
    }
    } 
    
    
    @SuppressWarnings ( "unchecked")
    @Override
    public Map<Long,Long> getUsersCountForCompanies()
    {
        LOG.info( "Method getUsersCountForCompanies() started." );

        Criteria criteria = getSession().createCriteria( User.class );
        Criterion criterion = Restrictions.or( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
            Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ) );
        criteria.add( criterion );

        criteria.setProjection( Projections.projectionList()
            .add( Projections.groupProperty( CommonConstants.COMPANY + "." + CommonConstants.COMPANY_ID_COLUMN ) )
            .add( Projections.rowCount() ) );
        List<Object[]> companyUserCounts = criteria.list();
        
        Map<Long,Long> activeUserCountsMap = new HashMap<Long,Long>();
        for ( Object[] activeUserCount : companyUserCounts ) {
            activeUserCountsMap.put( (Long) activeUserCount[0], (Long) activeUserCount[1] );
        }
        LOG.info( "Method getUsersCountForCompanies() finished." );        
        return activeUserCountsMap;
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<String> getRegisteredEmailsInTheCompany( Company company ) throws InvalidInputException
    {
        if ( company == null ) {
            throw new InvalidInputException( "Invalid company id passed in getRegisteredEmailsInTheCompany method" );
        }
        LOG.info( "Method to get all user emails address under  company id : {} started.", company.getCompanyId() );
        Criteria criteria = null;
        try {
            criteria = getSession().createCriteria( User.class );
            criteria.setProjection( Projections.property( CommonConstants.EMAIL_ID ) );
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_COLUMN , company));

            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_INCOMPLETE ) );

            criteria.add( criterion );
            
            LOG.info( "Method to get all user emails address under  company id : {} ended.", company.getCompanyId() );
            return criteria.list();
    } catch ( HibernateException hibernateException ) {
        LOG.warn( "Exception caught in getRegisteredEmailsInTheCompany() ", hibernateException );
        throw new DatabaseException( "Exception caught in getRegisteredEmailsInTheCompany() ", hibernateException );
    }
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public Map<Long, Long> getCompanyIdsForUserIds( List<Long> userIds )
    {
        LOG.debug( "Inside method getCompanyIdsForUserIds {}", userIds );
        if ( userIds.isEmpty() ) {
            return Collections.emptyMap();
        }
        Query query = null;
        try {
            query = getSession().createQuery( "select u.userId, u.company.companyId from User u where u.userId in (:ids)" );
            query.setParameterList( "ids", userIds );

            Map<Long, Long> userCompanyIdsMap = new HashMap<>();

            List<Object> result = (List<Object>) query.list();
            Iterator<Object> itr = result.iterator();

            while ( itr.hasNext() ) {
                Object[] obj = (Object[]) itr.next();
                Long userId = (Long) obj[0];
                Long companyId = (Long) obj[1];
                userCompanyIdsMap.put( userId, companyId );
                LOG.trace( "userId {} and company Id {}", userId, companyId );
            }

            return userCompanyIdsMap;

        } catch ( HibernateException e ) {
            LOG.error( "HibernateException caught in getCompanyIdsForUserIds(). Reason: " + e.getMessage(), e );
            throw new DatabaseException( "HibernateException caught in getCompanyIdsForUserIds().", e );
        }
    }

    /*
     * Method to fetch the list of cities and zipcodes in the state
     */
    @Override
    @Transactional
    public List<ZipCodeLookup> getCityAndCountySuggestion( String searchString, int startIndex, int batchSize, boolean onlyUsFilter )
    {
        LOG.debug( "Method getZipCodesByStateId called to fetch the list of cities and zipcodes in the state" );
        Criteria criteria = getSession().createCriteria(ZipCodeLookup.class);
        Criterion criterion = null;
        if(searchString != null && searchString.charAt(0) == ',') {
        	criterion = Restrictions.like(CommonConstants.CITY_STATE_NAME, "%"+searchString+"%");
        } else {
        	criterion = Restrictions.like(CommonConstants.CITY_STATE_NAME, searchString+"%");
        }
        if(onlyUsFilter)
        	criteria.add(Restrictions.not(Restrictions.in(CommonConstants.STATE_LOOKUP, getStatesNotInUs())));
        criteria.add( criterion );

        if ( startIndex > -1 )
            criteria.setFirstResult( startIndex );
        if ( batchSize > -1 )
            criteria.setMaxResults( batchSize );
        
        return criteria.list();
    }

    /*
     * Method to fetch the list of cities and zipcodes in the state
     */
    @Override
    @Transactional
    public List<ZipCodeLookup> getZipcodeSuggestion( String zipcode, int startIndex, int batchSize, boolean onlyUsFilter )
    {
        LOG.debug( "Method getZipCodesByStateId called to fetch the list of zipcodes in the state" );
        Criteria criteria = getSession().createCriteria(ZipCodeLookup.class);
        Criterion criterion = Restrictions.like(CommonConstants.ZIPCODE_COL, zipcode+"%");
        if(onlyUsFilter)
        	criteria.add(Restrictions.not(Restrictions.in(CommonConstants.STATE_LOOKUP, getStatesNotInUs())));
        criteria.add( criterion );

        if ( startIndex > -1 )
            criteria.setFirstResult( startIndex );
        if ( batchSize > -1 )
            criteria.setMaxResults( batchSize );
        
        return criteria.list();
    }
    
    @Override
    @Transactional
	public List<StateLookup> getStatesNotInUs() {
		Criteria criteria = getSession().createCriteria(StateLookup.class);
		criteria.add(Restrictions.in(CommonConstants.ID, CommonConstants.arrayOfNonUsStateId));
		return criteria.list();
	}
    
    @Override
	public String getCompanyNameForUserId(long userId) {
		LOG.debug("method to get companyName for user with userId : {}", userId);
		User user = findById(User.class, userId);
		if(user != null && user.getCompany() != null)
			return user.getCompany().getCompany();
		return "";
	}
    	
   /**
     * Method to fetch admin of the company based on the companyId
     * @param companyId
     * @throws InvalidInputException
     * @return adminId
     */
    @Override
    public Long getOwnerForCompany(Long companyId) {        
        LOG.debug("Method to get the owner of the company, getOwnerForCompany() started.");
        Criteria criteria = getSession().createCriteria(User.class);
        try {
            Criterion criterion1 = Restrictions.and((
                    Restrictions.eq(CommonConstants.COMPANY + "." + CommonConstants.COMPANY_ID_COLUMN, companyId)),
                    Restrictions.eq(CommonConstants.IS_OWNER_COLUMN, CommonConstants.IS_OWNER));
            Criterion criterion2 = Restrictions.or(
            		Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE),
            		Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_UNDER_PROCESSING));
            criteria.add(Restrictions.and(criterion1, criterion2));
        } catch (HibernateException hibernateException) {
            throw new DatabaseException("Exception caught in getOwnerForCompany() ", hibernateException);
        }
        LOG.debug("Method to get the owner of the company, getOwnerForCompany() finished.");
        Long userId = (Long) criteria.setProjection(Projections.property(CommonConstants.USER_ID)).uniqueResult();      
        return userId;
    }

    public List<Object[]> getSocialSurveyAdmins() throws HibernateException {

        LOG.debug("Method to get the all social survey admins, getSocialSurveyAdmins() started.");

        try {
            Query query = getSession().createSQLQuery( "select USER_ID, FIRST_NAME, LAST_NAME from USERS "
                + "where USER_ID in (select USER_ID from USER_PROFILE where PROFILES_MASTER_ID = 5 and STATUS = 1)" );

            LOG.debug("Method to get the all social survey admins, getSocialSurveyAdmins() finished.");

            return query.list();
        } catch (HibernateException hibernateException) {

            throw new DatabaseException("Exception caught in getSocialSurveyAdmins() ", hibernateException);
        }
    }
}
// JIRA SS-42 By RM-05 EOC
