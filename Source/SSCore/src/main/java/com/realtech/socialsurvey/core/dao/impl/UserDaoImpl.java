package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;

// JIRA SS-42 By RM-05 : BOC
@Component("user")
public class UserDaoImpl extends GenericDaoImpl<User, Long> implements UserDao {

	private static final Logger LOG = LoggerFactory.getLogger(UserDaoImpl.class);

	/*
	 * Method to fetch User by email id.
	 */
	@Override
	public User fetchUserByEmailId(User user, String emailId) throws NoRecordsFetchedException {
		LOG.info("Method to fetch the user by email id, fetchUserByEmailId() started.");

		Criteria criteria = getSession().createCriteria(User.class);
		try {
			criteria.add(Restrictions.ilike(CommonConstants.EMAIL_ID, "%" + emailId + "%"));
			criteria.add(Restrictions.eq(CommonConstants.COMPANY, user.getCompany()));

			Criterion criterion = Restrictions.or(Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE),
					Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED));
			criteria.add(criterion);
		}
		catch (HibernateException hibernateException) {
			LOG.error("Exception caught in fetchUserByEmailId() ", hibernateException);
			throw new DatabaseException("Exception caught in fetchUserByEmailId() ", hibernateException);
		}

		@SuppressWarnings("unchecked") List<User> users = (List<User>) criteria.list();

		if (users == null || users.isEmpty()) {
			LOG.error("Could not find any record in USER for : " + user.getFirstName());
			throw new NoRecordsFetchedException("Could not find any record in License_Details for : " + user.getCompany().getCompany());
		}

		LOG.info("Method to fetch the user by email id, fetchUserByEmailId() finished.");

		return users.get(CommonConstants.INITIAL_INDEX);
	}

	/*
	 * Method to return all the users that match email id passed.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> fetchUsersBySimilarEmailId(User user, String emailId) {
		LOG.info("Method to fetch all the users by email id,fetchUsersBySimilarEmailId() started.");
		Criteria criteria = getSession().createCriteria(User.class);
		try {
			criteria.add(Restrictions.ilike(CommonConstants.EMAIL_ID, "%" + emailId + "%"));
			criteria.add(Restrictions.eq(CommonConstants.COMPANY, user.getCompany()));

			Criterion criterion = Restrictions.or(Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE),
					Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED));
			criteria.add(criterion);
		}
		catch (HibernateException hibernateException) {
			LOG.error("Exception caught in fetchUsersBySimilarEmailId() ", hibernateException);
			throw new DatabaseException("Exception caught in fetchUsersBySimilarEmailId() ", hibernateException);
		}

		LOG.info("Method to fetch all the users by email id, fetchUsersBySimilarEmailId() finished.");

		return (List<User>) criteria.list();
	}

	/*
	 * Method to get count of active and unauthorized users belonging to a company.
	 */
	@Override
	public long getUsersCountForCompany(Company company) {
		LOG.info("Method to get count of active and unauthorized users belonging to a company, getUsersCountForCompany() started.");

		Criteria criteria = getSession().createCriteria(User.class);
		try {
			criteria.add(Restrictions.eq(CommonConstants.COMPANY, company));

			Criterion criterion = Restrictions.or(Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE),
					Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED));
			criteria.add(criterion);
		}
		catch (HibernateException hibernateException) {
			throw new DatabaseException("Exception caught in getUsersCountForCompany() ", hibernateException);
		}

		LOG.info("Method to get count of active and unauthorized users belonging to a company, getUsersCountForCompany() finished.");
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	/*
	 * Method to get list of active and unauthorized users belonging to a company.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsersForCompany(Company company) {
		LOG.info("Method getUsersForCompany called to fetch list of users of company : " + company.getCompany());
		Criteria criteria = getSession().createCriteria(User.class);
		try {
			criteria.add(Restrictions.eq(CommonConstants.COMPANY, company));

			Criterion criterion = Restrictions.or(Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE),
					Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED),
					Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE));
			criteria.add(criterion);
			criteria.addOrder(Order.asc("firstName"));
			criteria.addOrder(Order.asc("lastName"));
		}
		catch (HibernateException hibernateException) {
			throw new DatabaseException("Exception caught in getUsersForCompany() ", hibernateException);
		}
		LOG.info("Method getUsersForCompany finished to fetch list of users of company : " + company.getCompany());
		return (List<User>) criteria.list();
	}

	/*
	 * Method to check if any user exist with the email-id and is still active in a company
	 */
	@Override
	public User getActiveUser(String emailId) throws NoRecordsFetchedException {
		LOG.debug("Method checkIfAnyActiveUserExists() called to check if any active user present with the Email id : " + emailId);
		Criteria criteria = getSession().createCriteria(User.class);
		try {
			criteria.add(Restrictions.eq(CommonConstants.LOGIN_NAME, emailId));
			Criterion criterion = Restrictions.or(Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE),
					Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED),
					Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_TEMPORARILY_INACTIVE));
			criteria.add(criterion);
		}
		catch (HibernateException hibernateException) {
			throw new DatabaseException("Exception caught in getUsersForCompany() ", hibernateException);
		}
		@SuppressWarnings("unchecked") List<User> users = criteria.list();
		if (users == null || users.isEmpty()) {
			LOG.debug("No active users found with the emaild id " + emailId);
			throw new NoRecordsFetchedException("No active user found for the emailid");
		}
		LOG.debug("Method checkIfAnyActiveUserExists() successfull, active user with the emailId " + emailId);
		return users.get(CommonConstants.INITIAL_INDEX);
	}
}
// JIRA SS-42 By RM-05 EOC
