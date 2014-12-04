package com.realtech.socialsurvey.core.services.usermanagement.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.usermanagement.UserManagementService;

/**
 * JIRA:SS-34 BY RM02 Implementation for User management services
 */
@Component
public class UserManagementServiceImpl implements UserManagementService {

	private static final Logger LOG = LoggerFactory.getLogger(UserManagementService.class);
	private static Map<Integer, ProfilesMaster> profileMasters = new HashMap<Integer, ProfilesMaster>();
	private static boolean isProfileMastersMapPopulated = false;

	@Autowired
	private GenericDao<ProfilesMaster, Integer> profilesMasterDao;

	/**
	 * Method to get profile master based on profileId, gets the profile master from Map if it is
	 * not empty, else fetches it from db
	 */
	@Override
	public ProfilesMaster getProfilesMasterById(int profileId) throws InvalidInputException {
		LOG.info("Method getProfilesMasterById called for profileId : " + profileId);
		if (profileId <= 0) {
			throw new InvalidInputException("profile Id is not set for getting profile master");
		}
		ProfilesMaster profilesMaster = null;
		if (!isProfileMastersMapPopulated) {
			synchronized (profileMasters) {
				if (profileMasters == null || profileMasters.isEmpty()) {
					LOG.debug("Fetching profile masters from db and caching in map");
					populateProfileMastersMap();
					isProfileMastersMapPopulated = true;
				}
			}
		}
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

}
