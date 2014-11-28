package com.realtech.socialsurvey.core.services.usermanagement.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationLevelSetting;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.registration.impl.RegistrationServiceImpl;
import com.realtech.socialsurvey.core.services.usermanagement.UserManagementService;

@Component
public class UserManagementServiceImpl implements UserManagementService {

	private static final Logger LOG = LoggerFactory.getLogger(RegistrationServiceImpl.class);

	@Autowired
	private GenericDao<OrganizationLevelSetting, Integer> organizationLevelSettingDao;

	@Autowired
	private GenericDao<Company, Integer> companyDao;

	@Autowired
	private GenericDao<User, Integer> userDao;

	@Autowired
	private GenericDao<UserProfile, Integer> userProfileDao;

	// JIRA: SS-25: By RM05: BOC
	/*
	 * This method adds a new company and updates the same for current user and all its user
	 * profiles.
	 */
	@Override
	@Transactional(rollbackFor = { NonFatalException.class, FatalException.class })
	public User addCompanyInformation(User user, Map<String, String> organizationalDetails) {
		LOG.info("Method addCompanyInformation started for user " + user.getLoginName());
		Company company = addCompany(user, organizationalDetails.get(CommonConstants.COMPANY_NAME));
		updateCompanyForUser(user, company);
		updateCompanyForUserProfile(user, company);
		addOrganizationalDetails(user, organizationalDetails);
		LOG.info("Method addCompanyInformation finished for user " + user.getLoginName());
		return user;
	}

	/*
	 * This method adds a new company into the COMPANY table.
	 */
	private Company addCompany(User user, String companyName) {
		LOG.debug("Method addCompany started for user " + user.getLoginName());
		Company company = new Company();
		company.setCompany(companyName);
		company.setCreatedBy(String.valueOf(user.getUserId()));
		company.setModifiedBy(String.valueOf(user.getUserId()));
		company.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		company.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		LOG.debug("Method addCompany finished.");
		return companyDao.save(company);
	}

	/*
	 * This method updates company details for current user.
	 */
	private User updateCompanyForUser(User user, Company company) {
		LOG.debug("Method updateCompanyForUser started for user " + user.getLoginName());
		user.setCompany(company);
		userDao.update(user);
		LOG.debug("Method updateCompanyForUser finished for user " + user.getLoginName());
		return user;
	}

	/*
	 * This method updates company details in all the user profiles of current user.
	 */

	private void updateCompanyForUserProfile(User user, Company company) {
		LOG.debug("Method updateCompanyForUserProfile started for user " + user.getLoginName());
		List<UserProfile> userProfiles = user.getUserProfiles();
		if (userProfiles != null)
			for (UserProfile userProfile : userProfiles) {
				userProfile.setCompany(company);
				userProfileDao.update(userProfile);
			}
		LOG.debug("Method updateCompanyForUserProfile finished for user " + user.getLoginName());
	}

	/*
	 * This method adds all the key and value pairs into the ORGANIZATION_LEVEL_SETTINGS table.
	 */

	private void addOrganizationalDetails(User user, Map<String, String> organizationalDetails) {
		LOG.debug("Method addOrganizationalDetails called.");
		Company company = companyDao.findById(Company.class, CommonConstants.DEFAULT_COMPANY_ID);
		OrganizationLevelSetting organizationLevelSetting = new OrganizationLevelSetting();
		organizationLevelSetting.setAgentId(CommonConstants.DEFAULT_AGENT_ID);
		organizationLevelSetting.setBranchId(CommonConstants.DEFAULT_BRANCH_ID);
		organizationLevelSetting.setCompany(company);
		organizationLevelSetting.setRegionId(CommonConstants.DEFAULT_REGION_ID);
		organizationLevelSetting.setStatus(CommonConstants.STATUS_ACTIVE);
		organizationLevelSetting.setCreatedBy(String.valueOf(user.getUserId()));
		organizationLevelSetting.setModifiedBy(String.valueOf(user.getUserId()));
		organizationLevelSetting.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		organizationLevelSetting.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		for (Entry<String, String> organizationalDetail : organizationalDetails.entrySet()) {
			organizationLevelSetting.setSettingKey(organizationalDetail.getKey());
			organizationLevelSetting.setSettingValue(organizationalDetail.getValue());
			organizationLevelSettingDao.save(organizationLevelSetting);
			organizationLevelSettingDao.flush();
		}
		LOG.debug("Method addCompanyDetails finished");
	}
}

// JIRA: SS-27: By RM05: EOC