package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;

/**
 * JIRA:SS-34 BY RM02 Implementation for User management services
 */
@DependsOn("generic")
@Component
public class UserManagementServiceImpl implements UserManagementService, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(UserManagementServiceImpl.class);
	private static Map<Integer, ProfilesMaster> profileMasters = new HashMap<Integer, ProfilesMaster>();

	@Autowired
	private GenericDao<ProfilesMaster, Integer> profilesMasterDao;

	@Autowired
	private GenericDao<User, Long> userDao;

	@Resource
	@Qualifier("userProfile")
	private UserProfileDao userProfileDao;

	/**
	 * Method to get profile master based on profileId, gets the profile master from Map which is
	 * pre-populated with afterPropertiesSet method
	 */
	@Override
	@Transactional
	public ProfilesMaster getProfilesMasterById(int profileId) throws InvalidInputException {
		LOG.info("Method getProfilesMasterById called for profileId : " + profileId);
		if (profileId <= 0) {
			throw new InvalidInputException("profile Id is not set for getting profile master");
		}
		ProfilesMaster profilesMaster = null;
		if (profileMasters.containsKey(profileId)) {
			profilesMaster = profileMasters.get(profileId);
		}
		else {
			throw new InvalidInputException("No profile master detected for profileID : " + profileId);
		}
		LOG.info("Method getProfilesMasterById finished for profileId : " + profileId);
		return profilesMaster;
	}

	/**
	 * Method to fetch profile masters from db and store in the map
	 */
	private void populateProfileMastersMap() {
		LOG.debug("Getting all profile masters from database and storing in map");
		List<ProfilesMaster> profileMasterList = profilesMasterDao.findAll(ProfilesMaster.class);
		if (profileMasterList != null && !profileMasterList.isEmpty()) {
			for (ProfilesMaster profilesMaster : profileMasterList) {
				profileMasters.put(profilesMaster.getProfileId(), profilesMaster);
			}
		}
		else {
			LOG.warn("No profile master found in database");
		}
		LOG.debug("Successfully populated profile masters from database into map");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("afterPropertiesSet for UserManagementServiceImpl called");

		LOG.debug("Populating profile master from db into the hashMap");
		populateProfileMastersMap();
		LOG.debug("Successfully populated profile master from db into the hashMap");

		LOG.info("afterPropertiesSet for UserManagementServiceImpl completed");
	}

	/**
	 * Method to create profile for a branch admin
	 */
	@Override
	@Transactional
	public User createBranchAdmin(User assigneeUser, long branchId, long userId) throws InvalidInputException {
		if (assigneeUser == null) {
			throw new InvalidInputException("Company is null in createBranchAdmin");
		}
		if (branchId <= 0l) {
			throw new InvalidInputException("Branch id is invalid in createBranchAdmin");
		}
		if (userId <= 0l) {
			throw new InvalidInputException("User id is invalid in createBranchAdmin");
		}
		LOG.info("Method to createBranchAdmin called for branchId : " + branchId + " and userId : " + userId);

		LOG.debug("Selecting user for the userId provided for branch admin : " + userId);
		User user = userDao.findById(User.class, userId);
		if (user == null) {
			throw new InvalidInputException("No user found for userId specified in createBranchAdmin");
		}
		/**
		 * created and modified by are of the logged in user, rest user attributes come from
		 */
		userProfileDao.createUserProfile(user, assigneeUser.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID, branchId,
				CommonConstants.DEFAULT_REGION_ID, CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID, CommonConstants.LOGIN_STAGE,
				CommonConstants.STATUS_ACTIVE, String.valueOf(assigneeUser.getUserId()), String.valueOf(assigneeUser.getUserId()));

		LOG.info("Method to createBranchAdmin finished for branchId : " + branchId + " and userId : " + userId);

		return user;
	}

	/**
	 * Method to create profile for a region admin
	 */
	@Override
	public User createRegionAdmin(User assigneeUser, long regionId, long userId) throws InvalidInputException {
		if (assigneeUser == null) {
			throw new InvalidInputException("Company is null in createRegionAdmin");
		}
		if (regionId <= 0l) {
			throw new InvalidInputException("Region id is invalid in createRegionAdmin");
		}
		if (userId <= 0l) {
			throw new InvalidInputException("User id is invalid in createRegionAdmin");
		}
		LOG.info("Method to createRegionAdmin called for regionId : " + regionId + " and userId : " + userId);

		LOG.debug("Selecting user for the userId provided for region admin : " + userId);
		User user = userDao.findById(User.class, userId);
		if (user == null) {
			throw new InvalidInputException("No user found for userId specified in createRegionAdmin");
		}
		userProfileDao.createUserProfile(user, assigneeUser.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID,
				CommonConstants.DEFAULT_BRANCH_ID, regionId, CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID, CommonConstants.LOGIN_STAGE,
				CommonConstants.STATUS_ACTIVE, String.valueOf(assigneeUser.getUserId()), String.valueOf(assigneeUser.getUserId()));

		LOG.info("Method to createRegionAdmin finished for regionId : " + regionId + " and userId : " + userId);

		return user;
	}
}
