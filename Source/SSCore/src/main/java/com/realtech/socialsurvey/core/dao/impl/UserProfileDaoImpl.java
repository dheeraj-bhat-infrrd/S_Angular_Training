package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;

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
}
