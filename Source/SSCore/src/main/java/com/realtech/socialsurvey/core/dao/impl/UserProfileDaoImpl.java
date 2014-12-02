package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.services.registration.impl.RegistrationServiceImpl;

@Component("userProfile")
public class UserProfileDaoImpl extends GenericDaoImpl<UserProfile, Integer> implements UserProfileDao {
	
	private static final Logger LOG = LoggerFactory.getLogger(RegistrationServiceImpl.class);
	
	@Autowired
	private GenericDao<ProfilesMaster, Integer> profilesMasterDao;
	
	/*
	 * To add a new user profile into the USER_ROFILE table
	 */
	@Override
	public void createUserProfile(User user, Company company, String emailId, int agentId, int branchId, int regionId, int profileMasterId) {
		LOG.info("Method createUserProfile called for username : " + user.getLoginName());
		UserProfile userProfile = new UserProfile();
		userProfile.setAgentId(agentId);
		userProfile.setBranchId(branchId);
		userProfile.setCompany(company);
		userProfile.setEmailId(emailId);
		userProfile.setIsProfileComplete(CommonConstants.STATUS_INACTIVE);
		userProfile.setProfilesMaster(profilesMasterDao.findById(ProfilesMaster.class, profileMasterId));
		userProfile.setRegionId(regionId);
		userProfile.setStatus(CommonConstants.STATUS_ACTIVE);
		userProfile.setUser(user);
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		userProfile.setCreatedOn(currentTimestamp);
		userProfile.setModifiedOn(currentTimestamp);
		userProfile.setCreatedBy(String.valueOf(user.getUserId()));
		userProfile.setModifiedBy(String.valueOf(user.getUserId()));
		save(userProfile);
		LOG.info("Method createUserProfile() finished");
	}
}
