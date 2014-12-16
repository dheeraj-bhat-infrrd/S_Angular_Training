package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

// JIRA: SS-27: By RM05: BOC
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
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.registration.RegistrationService;
import com.realtech.socialsurvey.core.services.registration.impl.RegistrationServiceImpl;
import com.realtech.socialsurvey.core.services.usermanagement.UserManagementService;

@Component
public class OrganizationManagementServiceImpl implements OrganizationManagementService {

	private static final Logger LOG = LoggerFactory.getLogger(RegistrationServiceImpl.class);

	@Autowired
	private GenericDao<OrganizationLevelSetting, Long> organizationLevelSettingDao;

	@Autowired
	private GenericDao<Company, Long> companyDao;

	@Autowired
	private GenericDao<User, Long> userDao;

	@Autowired
	private GenericDao<Region, Long> regionDao;

	@Autowired
	private GenericDao<Branch, Long> branchDao;

	@Resource
	@Qualifier("userProfile")
	private UserProfileDao userProfileDao;

	@Autowired
	private GenericDao<ProfilesMaster, Integer> profilesMasterDao;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private RegistrationService registrationService;

	/**
	 * This method adds a new company and updates the same for current user and all its user
	 * profiles.
	 */
	@Override
	@Transactional(rollbackFor = { NonFatalException.class, FatalException.class })
	public User addCompanyInformation(User user, Map<String, String> organizationalDetails) {
		LOG.info("Method addCompanyInformation started for user " + user.getLoginName());
		Company company = addCompany(user, organizationalDetails.get(CommonConstants.COMPANY_NAME), CommonConstants.STATUS_ACTIVE);

		LOG.debug("Calling method for updating company of user");
		updateCompanyForUser(user, company);

		LOG.debug("Calling method for updating company for user profiles");
		updateCompanyForUserProfile(user, company);

		LOG.debug("Calling method for adding organizational details");
		addOrganizationalDetails(user, company, organizationalDetails);

		LOG.info("Method addCompanyInformation finished for user " + user.getLoginName());
		return user;
	}

	// JIRA: SS-28: By RM05: BOC
	/*
	 * To add account as per the choice of User.
	 */
	@Override
	@Transactional(rollbackFor = { NonFatalException.class, FatalException.class })
	public AccountType addAccountTypeForCompanyAndUpdateStage(User user, String strAccountType) throws InvalidInputException {
		LOG.info("Method addAccountTypeForCompany started for user : " + user.getLoginName());
		if (strAccountType == null || strAccountType.isEmpty()) {
			throw new InvalidInputException("account type is null or empty while adding account type fro company");
		}
		int accountTypeValue = 0;
		try {
			accountTypeValue = Integer.parseInt(strAccountType);
		}
		catch (NumberFormatException e) {
			LOG.error("NumberFormatException for account type :" + strAccountType);
			throw new InvalidInputException("account type is not valid while adding account type fro company");
		}
		AccountType accountType = AccountType.getAccountType(accountTypeValue);
		switch (accountType) {
			case INDIVIDUAL:
				addIndividualAccountType(user);
				break;
			case TEAM:
				addTeamAccountType(user);
				break;
			case COMPANY:
				addCompanyAccountType(user);
				break;
			case ENTERPRISE:
				addEnterpriseAccountType();
				break;
			default:
				throw new InvalidInputException("Account type is not valid");
		}
		LOG.info("Method addAccountTypeForCompany finished.");
		return accountType;
	}

	// JIRA: SS-28: By RM05: EOC

	/*
	 * This method adds a new company into the COMPANY table.
	 */
	private Company addCompany(User user, String companyName, int isRegistrationComplete) {
		LOG.debug("Method addCompany started for user " + user.getLoginName());
		Company company = new Company();
		company.setCompany(companyName);
		company.setIsRegistrationComplete(isRegistrationComplete);
		company.setStatus(CommonConstants.STATUS_ACTIVE);
		company.setCreatedBy(String.valueOf(user.getUserId()));
		company.setModifiedBy(String.valueOf(user.getUserId()));
		company.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		company.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		LOG.debug("Method addCompany finished.");
		return companyDao.save(company);
	}

	/**
	 * This method updates company details for current user
	 * 
	 * @param user
	 * @param company
	 * @return
	 */
	private User updateCompanyForUser(User user, Company company) {
		LOG.debug("Method updateCompanyForUser started for user " + user.getLoginName());
		user.setCompany(company);
		userDao.update(user);
		LOG.debug("Method updateCompanyForUser finished for user " + user.getLoginName());
		return user;
	}

	/**
	 * This method updates company details in all the user profiles of current user.
	 * 
	 * @param user
	 * @param company
	 */
	private void updateCompanyForUserProfile(User user, Company company) {
		LOG.debug("Method updateCompanyForUserProfile started for user " + user.getLoginName());
		user = userDao.findById(User.class, user.getUserId());
		List<UserProfile> userProfiles = userProfileDao.findByColumn(UserProfile.class, "user", user);
		if (userProfiles != null) {
			for (UserProfile userProfile : userProfiles) {
				userProfile.setCompany(company);
				userProfileDao.update(userProfile);
			}
		}
		else {
			LOG.warn("No profiles found for user : " + user.getUserId());
		}
		LOG.debug("Method updateCompanyForUserProfile finished for user " + user.getLoginName());
	}

	/**
	 * This method adds all the key and value pairs into the ORGANIZATION_LEVEL_SETTINGS table.
	 * 
	 * @param user
	 * @param organizationalDetails
	 */
	private void addOrganizationalDetails(User user, Company company, Map<String, String> organizationalDetails) {
		LOG.debug("Method addOrganizationalDetails called.");
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
		LOG.debug("Method addOrganizationalDetails finished");
	}

	/**
	 * Method to add an Individual. Makes entry in Region, Branch and UserProfile tables.
	 * 
	 * @param user
	 * @throws InvalidInputException
	 */
	private void addIndividualAccountType(User user) throws InvalidInputException {
		LOG.info("Method addIndividual started for user : " + user.getLoginName());

		LOG.debug("Adding a new region");
		Region region = addRegion(user, CommonConstants.IS_DEFAULT_BY_SYSTEM_YES, CommonConstants.DEFAULT_BRANCH_NAME);
		ProfilesMaster profilesMaster = userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID);

		LOG.debug("Creating user profile for region admin");
		userProfileDao.createUserProfile(user, user.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID,
				CommonConstants.DEFAULT_BRANCH_ID, region.getRegionId(), profilesMaster.getProfileId(), CommonConstants.PROFILE_STAGES_COMPLETE,
				CommonConstants.STATUS_ACTIVE);
		profilesMaster = userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID);

		LOG.debug("Adding a new branch");
		Branch branch = addBranch(user, region, CommonConstants.DEFAULT_BRANCH_NAME, CommonConstants.IS_DEFAULT_BY_SYSTEM_YES);

		LOG.debug("Creating user profile for branch admin");
		userProfileDao.createUserProfile(user, user.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID, branch.getBranchId(),
				CommonConstants.DEFAULT_REGION_ID, profilesMaster.getProfileId(), CommonConstants.PROFILE_STAGES_COMPLETE,
				CommonConstants.STATUS_ACTIVE);
		profilesMaster = userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID);

		LOG.debug("Creating user profile for agent");
		userProfileDao.createUserProfile(user, user.getCompany(), user.getEmailId(), user.getUserId(), CommonConstants.DEFAULT_BRANCH_ID,
				CommonConstants.DEFAULT_REGION_ID, profilesMaster.getProfileId(), CommonConstants.PROFILE_STAGES_COMPLETE,
				CommonConstants.STATUS_ACTIVE);
		/**
		 * For an individual, only the company admin's profile completion stage is updated, all the
		 * other profiles created by default need no action so their profile completion stage is
		 * marked completed at the time of insert
		 */
		LOG.debug("Updating profile stage for company to payment stage");
		registrationService.updateProfileCompletionStage(user, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID,
				CommonConstants.DASHBOARD_STAGE);

		LOG.info("Method addIndividual finished.");
	}

	/**
	 * Method to add a Team. Makes entry in Region table.
	 * 
	 * @param user
	 * @throws InvalidInputException
	 */
	private void addTeamAccountType(User user) throws InvalidInputException {
		LOG.debug("Method addTeam started for user : " + user.getLoginName());

		LOG.debug("Adding a new region");
		Region region = addRegion(user, CommonConstants.IS_DEFAULT_BY_SYSTEM_YES, CommonConstants.DEFAULT_BRANCH_NAME);
		ProfilesMaster profilesMaster = userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID);

		LOG.debug("Creating user profile for region admin");
		userProfileDao.createUserProfile(user, user.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID,
				CommonConstants.DEFAULT_BRANCH_ID, region.getRegionId(), profilesMaster.getProfileId(), CommonConstants.PROFILE_STAGES_COMPLETE,
				CommonConstants.STATUS_ACTIVE);

		LOG.debug("Updating profile stage to payment stage for account type team");
		registrationService.updateProfileCompletionStage(user, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID,
				CommonConstants.DASHBOARD_STAGE);

		LOG.debug("Method addTeam finished.");
	}

	/*
	 * Method to add a company.
	 */
	private void addCompanyAccountType(User user) {
		LOG.debug("Method addCompany started for user : " + user.getLoginName());

		LOG.debug("Method addCompany finished.");
	}

	/*
	 * Method to add an Enterprise.
	 */
	private void addEnterpriseAccountType() {
		LOG.debug("Method addEnterprise started.");

		LOG.debug("Method addEnterprise finished.");
	}

	/**
	 * Method to add a new region
	 * 
	 * @param user
	 * @param isDefaultBySystem
	 * @param regionName
	 * @return
	 */
	private Region addRegion(User user, int isDefaultBySystem, String regionName) {
		LOG.debug("Method addRegion started for user : " + user.getLoginName() + " isDefaultBySystem : " + isDefaultBySystem + " regionName :"
				+ regionName);
		Region region = new Region();
		region.setCompany(user.getCompany());
		region.setIsDefaultBySystem(isDefaultBySystem);
		region.setStatus(CommonConstants.STATUS_ACTIVE);
		region.setRegion(regionName);
		region.setCreatedBy(String.valueOf(user.getUserId()));
		region.setModifiedBy(String.valueOf(user.getUserId()));
		region.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		region.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		region = regionDao.save(region);
		LOG.debug("Method addRegion finished.");
		return region;
	}

	/**
	 * Method to add a new Branch
	 * 
	 * @param user
	 * @param region
	 * @param branchName
	 * @param isDefaultBySystem
	 * @return
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
		branch = branchDao.save(branch);
		LOG.debug("Method addBranch finished.");
		return branch;
	}
}

// JIRA: SS-27: By RM05: EOC