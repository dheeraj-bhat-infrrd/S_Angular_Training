package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.DatabaseException;

public class UserProfileDaoImpl extends GenericDaoImpl<UserProfile, Long> implements UserProfileDao {

	private static final Logger LOG = LoggerFactory.getLogger(UserProfileDaoImpl.class);

	/*
	 * Method to deactivate all the user profiles for a given user.
	 */

	@Override
	public void deactivateAllUserProfilesForUser(User admin, User userToBeDeactivated) {

		LOG.info("Method deactivateUserProfileByUser called to deactivate user : " + userToBeDeactivated.getDisplayName());
		Query query = getSession().getNamedQuery("UserProfile.updateByUser");
		// Setting status for user profile as inactive.
		query.setParameter(0, CommonConstants.STATUS_INACTIVE);
		query.setParameter(1, String.valueOf(admin.getUserId()));
		query.setParameter(2, String.valueOf(new Timestamp(System.currentTimeMillis())));
		query.setParameter(3, userToBeDeactivated);
		query.executeUpdate();
		LOG.info("Method deactivateUserProfileByUser called to deactivate user : " + userToBeDeactivated.getDisplayName());

	}

	/*
	 * Method to remove a branch admin.
	 */
	@Override
	public void deactivateUserProfileForBranch(User admin, long branchId, User userToBeDeactivated) {
		LOG.info("Method deactivateUserProfileForBranch called to deactivate user : " + userToBeDeactivated.getDisplayName());
		Query query = getSession().getNamedQuery("UserProfile.updateByUser");
		// Setting status for user profile as inactive.
		query.setParameter(0, CommonConstants.STATUS_INACTIVE);
		query.setParameter(1, String.valueOf(admin.getUserId()));
		query.setParameter(2, String.valueOf(new Timestamp(System.currentTimeMillis())));
		query.setParameter(3, userToBeDeactivated);
		query.setParameter(4, branchId);
		query.executeUpdate();
		LOG.info("Method deactivateUserProfileForBranch called to deactivate user : " + userToBeDeactivated.getDisplayName());
	}

	/*
	 * Method to deactivate a region admin.
	 */
	@Override
	public void deactivateUserProfileForRegion(User admin, long regionId, User userToBeDeactivated) {
		LOG.info("Method deactivateUserProfileForBranch called to deactivate user : " + userToBeDeactivated.getDisplayName());
		Query query = getSession().getNamedQuery("UserProfile.updateByUser");
		// Setting status for user profile as inactive.
		query.setParameter(0, CommonConstants.STATUS_INACTIVE);
		query.setParameter(1, String.valueOf(admin.getUserId()));
		query.setParameter(2, String.valueOf(new Timestamp(System.currentTimeMillis())));
		query.setParameter(3, userToBeDeactivated);
		query.setParameter(4, regionId);
		query.executeUpdate();
		LOG.info("Method deactivateUserProfileForBranch called to deactivate user : " + userToBeDeactivated.getDisplayName());
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> getBranchIdsForUser(User user) {
		LOG.info("Method getBranchIdsForUser called to fetch branch ids assigned to user : " + user.getDisplayName());
		Criteria criteria = getSession().createCriteria(UserProfile.class);
		List<Long> branchIds = new ArrayList<>();
		try {
			criteria.add(Restrictions.eq(CommonConstants.USER_COLUMN, user));

			Criterion criterion = Restrictions.or(Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE),
					Restrictions.eq(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED));
			criteria.add(criterion);
			criteria.setProjection(Projections.distinct(Projections.projectionList().add(Projections.property("branchId"), "branchId")));
			branchIds = criteria.list();
		}
		catch (HibernateException hibernateException) {
			LOG.error("Exception caught in fetchUserByEmailId() ", hibernateException);
			throw new DatabaseException("Exception caught in fetchUserByEmailId() ", hibernateException);
		}
		LOG.info("Method getBranchIdsForUser finished to fetch branche ids assigned to user : " + user.getDisplayName());
		return branchIds;
	}
}
