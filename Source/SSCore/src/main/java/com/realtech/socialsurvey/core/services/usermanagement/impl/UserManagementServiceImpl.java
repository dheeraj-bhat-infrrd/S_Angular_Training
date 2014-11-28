package com.realtech.socialsurvey.core.services.usermanagement.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationLevelSetting;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.enums.AccountType;
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

	@Resource
	@Qualifier("userProfile")
	private UserProfileDao userProfileDao;

	@Autowired
	private GenericDao<ProfilesMaster, Integer> profilesMasterDao;

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

	
	// JIRA: SS-28: By RM05: BOC
	/*
	 * To add account as per the choice of User.
	 */
	@Override
	@Transactional(rollbackFor = { NonFatalException.class, FatalException.class })
	public String addAccountTypeForCompany(User user, String accountType) {
		LOG.info("Method addAccountTypeForCompany started for user : " + user.getLoginName());
		switch (AccountType.valueOf(accountType).getValue()) {
			case 1:
				addIndividual(user);
				break;
			case 2:
				addTeam(user);
				break;
			case 3:
				addCompany(user);
				break;
			case 4:
				addEnterprise();
		}
		LOG.info("Method addAccountTypeForCompany finished.");
		return accountType;
	}
	// JIRA: SS-28: By RM05: EOC

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
		if (company != null)
			organizationLevelSetting.setCompany(company);
		organizationLevelSetting.setRegionId(CommonConstants.DEFAULT_REGION_ID);
		organizationLevelSetting.setStatus(CommonConstants.STATUS_ACTIVE);
		organizationLevelSetting.setCreatedBy(String.valueOf(user.getUserId()));
		organizationLevelSetting.setModifiedBy(String.valueOf(user.getUserId()));
		organizationLevelSetting.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		organizationLevelSetting.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		if (organizationalDetails != null)
			for (Entry<String, String> organizationalDetail : organizationalDetails.entrySet()) {
				organizationLevelSetting.setSettingKey(organizationalDetail.getKey());
				organizationLevelSetting.setSettingValue(organizationalDetail.getValue());
				organizationLevelSettingDao.save(organizationLevelSetting);
				organizationLevelSettingDao.flush();
			}
		LOG.debug("Method addCompanyDetails finished");
	}

	/*
	 * Method to add an Individual. Makes entry in Region, Branch and UserProfile tables.
	 */
	private void addIndividual(User user) {
		LOG.debug("Method addIndividual started for user : " + user.getLoginName());
		// Add a new Region.
		Region region = addRegion(user, CommonConstants.IS_DEFAULT_BY_SYSTEM_YES, CommonConstants.DEFAULT_BRANCH_NAME);
		ProfilesMaster profilesMaster = profilesMasterDao.findById(ProfilesMaster.class, CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID);
		// Create a new User Profile for Region Admin.
		userProfileDao.createUserProfile(user, user.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID,
				CommonConstants.DEFAULT_BRANCH_ID, region.getRegionId(), profilesMaster.getProfileId());
		// Add a new branch.
		Branch branch = addBranch(user, region, CommonConstants.DEFAULT_BRANCH_NAME, CommonConstants.IS_DEFAULT_BY_SYSTEM_YES);
		// Create a new User Profile for Branch Admin.
		userProfileDao.createUserProfile(user, user.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID, branch.getBranchId(),
				CommonConstants.DEFAULT_REGION_ID, profilesMaster.getProfileId());
		// Create a new User Profile for Agent.
		userProfileDao.createUserProfile(user, user.getCompany(), user.getEmailId(), user.getUserId(), CommonConstants.DEFAULT_BRANCH_ID,
				CommonConstants.DEFAULT_REGION_ID, profilesMaster.getProfileId());
		LOG.debug("Method addIndividual finished.");
	}

	/*
	 * Method to add a Team. Makes entry in Region table.
	 */
	private void addTeam(User user) {
		LOG.debug("Method addTeam started for user : " + user.getLoginName());
		// Add a new Region.
		Region region = addRegion(user, CommonConstants.IS_DEFAULT_BY_SYSTEM_YES, CommonConstants.DEFAULT_BRANCH_NAME);
		ProfilesMaster profilesMaster = profilesMasterDao.findById(ProfilesMaster.class, CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID);
		// Create a new User Profile for Region Admin.
		userProfileDao.createUserProfile(user, user.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID,
				CommonConstants.DEFAULT_BRANCH_ID, region.getRegionId(), profilesMaster.getProfileId());
		LOG.debug("Method addTeam finished.");
	}

	/*
	 * Method to add a company.
	 */
	private void addCompany(User user) {
		LOG.debug("Method addCompany started for user : " + user.getLoginName());

		LOG.debug("Method addCompany finished.");
	}

	/*
	 * Method to add an Enterprise.
	 */
	private void addEnterprise() {
		LOG.debug("Method addEnterprise started.");

		LOG.debug("Method addEnterprise finished.");
	}

	/*
	 * Method to add a new Region.
	 */
	private Region addRegion(User user, int isDefaultBySystem, String regionName) {
		LOG.debug("Method addRegion started for user : " + user.getLoginName());
		Region region = new Region();
		region.setCompany(user.getCompany());
		region.setIsDefaultBySystem(isDefaultBySystem);
		region.setStatus(CommonConstants.STATUS_ACTIVE);
		region.setRegion(regionName);
		region.setCreatedBy(String.valueOf(user.getUserId()));
		region.setModifiedBy(String.valueOf(user.getUserId()));
		region.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		region.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		LOG.debug("Method addRegion finished.");
		return region;
	}

	/*
	 * Method to add a new Branch.
	 */
	private Branch addBranch(User user, Region region, String branchName, int isDefaultBySystem) {
		LOG.debug("Method addBranch started for user : " + user.getLoginName());
		Branch branch = new Branch();
		branch.setCompany(user.getCompany());
		branch.setRegion(region);
		branch.setStatus(CommonConstants.STATUS_ACTIVE);
		branch.setBranch(branchName);
		branch.setIsDefaultBySystem(isDefaultBySystem);
		branch.setCreatedBy(String.valueOf(user.getUserId()));
		branch.setModifiedBy(String.valueOf(user.getUserId()));
		branch.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		branch.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		LOG.debug("Method addBranch finished.");
		return branch;
	}
}

// JIRA: SS-27: By RM05: EOC