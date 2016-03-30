package com.realtech.socialsurvey.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

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

import scala.Array;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
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
        LOG.info( "Method to get count of active and unauthorized users belonging to a company, getUsersCountForCompany() started." );

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

        LOG.info( "Method to get count of active and unauthorized users belonging to a company, getUsersCountForCompany() finished." );
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
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ) );
            criteria.add( criterion );
            criteria.addOrder( Order.asc( "firstName" ) );
            criteria.addOrder( Order.asc( "lastName" ) );
        } catch ( HibernateException hibernateException ) {
            throw new DatabaseException( "Exception caught in getUsersForCompany() ", hibernateException );
        }
        LOG.info( "Method getUsersForCompany finished to fetch list of users of company : " + company.getCompany() );
        return (List<User>) criteria.list();
    }


    /*
     * Method to check if any user exist with the email-id and is still active in a company
     */
    @Override
    public User getActiveUser( String emailId ) throws NoRecordsFetchedException
    {
        LOG.debug( "Method checkIfAnyActiveUserExists() called to check if any active user present with the Email id : "
            + emailId );
        Criteria criteria = getSession().createCriteria( User.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.LOGIN_NAME, emailId ) );
            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ) );
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
        LOG.debug( "Method checkIfAnyActiveUserExists() called to check if any active user present with the Email id : "
            + emailId );
        Criteria criteria = getSession().createCriteria( User.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.LOGIN_NAME, emailId ) );
            criteria.add( Restrictions.eq( CommonConstants.COMPANY, company ) );
            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ) );
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
        LOG.info( "Method getUsersForUserIds called to fetch users for user ids : " + userIds );
        try {
            criteria.add( Restrictions.in( CommonConstants.USER_ID, userIds ) );

            criteria.addOrder( Order.asc( "firstName" ) );
            criteria.addOrder( Order.asc( "lastName" ) );

        } catch ( HibernateException hibernateException ) {
            throw new DatabaseException( "Exception caught in getUsersForUserIds() ", hibernateException );
        }
        @SuppressWarnings ( "unchecked") List<User> users = criteria.list();
        LOG.info( "Method getUsersForUserIds call ended to fetch users for user ids." );
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
        LOG.info( "Method getUsersForCompany called to fetch list of users of company : " + company.getCompany() );
        Criteria criteria = getSession().createCriteria( User.class );
        try {
            criteria.add( Restrictions.eq( CommonConstants.COMPANY, company ) );

            Criterion criterion = Restrictions.or(
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED ),
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ) );
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
        LOG.info( "Method getUsersForCompany finished to fetch list of users of company : " + company.getCompany() );
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
            criteria.add( Restrictions
                .sqlRestriction( "USER_ID in (select up.USER_ID from USER_PROFILE up where PROFILES_MASTER_ID="
                    + profileMasterId + " and STATUS=" + CommonConstants.STATUS_ACTIVE + ")" ) );
        } catch ( HibernateException e ) {
            LOG.error(
                "HibernateException caught in getUserIdsUnderCompanyBasedOnProfileMasterId(). Reason: " + e.getMessage(), e );
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
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ) );
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
                Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE ) );
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

}
// JIRA SS-42 By RM-05 EOC
