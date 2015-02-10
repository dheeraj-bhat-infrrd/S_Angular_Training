package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

// JIRA: SS-27: By RM05: BOC
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.Achievement;
import com.realtech.socialsurvey.core.entities.Association;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchSettings;
import com.realtech.socialsurvey.core.entities.CRMInfo;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.DisabledAccount;
import com.realtech.socialsurvey.core.entities.EncompassCrmInfo;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.Licenses;
import com.realtech.socialsurvey.core.entities.LockSettings;
import com.realtech.socialsurvey.core.entities.MailContent;
import com.realtech.socialsurvey.core.entities.MailContentSettings;
import com.realtech.socialsurvey.core.entities.MailIdSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.HierarchyManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;

@DependsOn("generic")
@Component
public class OrganizationManagementServiceImpl implements OrganizationManagementService, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(OrganizationManagementServiceImpl.class);
	private static Map<Integer, VerticalsMaster> verticalsMastersMap = new HashMap<Integer, VerticalsMaster>();

	@Autowired
	private OrganizationUnitSettingsDao organizationUnitSettingsDao;
	
	@Autowired
	private HierarchyManagementService hierarchyManagementService;

	@Autowired
	private GenericDao<Company, Long> companyDao;

	@Autowired
	private GenericDao<User, Long> userDao;

	@Autowired
	private GenericDao<Region, Long> regionDao;

	@Autowired
	private GenericDao<Branch, Long> branchDao;

	@Autowired
	private GenericDao<LicenseDetail, Long> licenceDetailDao;

	@Autowired
	private GenericDao<UserProfile, Long> userProfileDao;

	@Autowired
	private GenericDao<ProfilesMaster, Integer> profilesMasterDao;

	@Autowired
	private GenericDao<VerticalsMaster, Integer> verticalMastersDao;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private SolrSearchService solrSearchService;

	@Autowired
	private GenericDao<DisabledAccount, Long> disabledAccountDao;

	@Autowired
	private Payment gateway;

	@Autowired
	private EncryptionHelper encryptionHelper;

	@Autowired
	private Utils utils;

	/**
	 * This method adds a new company and updates the same for current user and all its user
	 * profiles.
	 * 
	 * @throws SolrException
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional(rollbackFor = { NonFatalException.class, FatalException.class })
	public User addCompanyInformation(User user, Map<String, String> organizationalDetails) throws SolrException, InvalidInputException {
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
	public AccountType addAccountTypeForCompany(User user, String strAccountType) throws InvalidInputException, SolrException {
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
			case FREE:
				addIndividualAccountType(user);
				break;
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
				LOG.debug("Selected account type as enterprise so no action required");
				break;
			default:
				throw new InvalidInputException("Account type is not valid");
		}
		user = userDao.findById(User.class, user.getUserId());
		userManagementService.setProfilesOfUser(user);
		solrSearchService.addUserToSolr(user);
		LOG.info("Method addAccountTypeForCompany finished.");
		return accountType;
	}

	// JIRA: SS-28: By RM05: EOC

	/**
	 * Fetch the account type master id passing
	 * 
	 * @author RM-06
	 * @param company
	 * @return account master id
	 */
	@Override
	@Transactional
	public long fetchAccountTypeMasterIdForCompany(Company company) throws InvalidInputException {

		LOG.info("Fetch account type for company :" + company.getCompany());

		List<LicenseDetail> licenseDetails = licenceDetailDao.findByColumn(LicenseDetail.class, CommonConstants.COMPANY, company);
		if (licenseDetails == null || licenseDetails.isEmpty()) {
			LOG.error("No license object present for the company : " + company.getCompany());
			return 0;
		}
		LOG.info("Successfully fetched the License detail for the current user's company");

		// return the account type master ID
		return licenseDetails.get(0).getAccountsMaster().getAccountsMasterId();
	};

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
		user.setIsOwner(CommonConstants.IS_OWNER);
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
		// List<UserProfile> userProfiles = userProfileDao.findByColumn(UserProfile.class, "user",
		// user);
		List<UserProfile> userProfiles = user.getUserProfiles();
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
	 * This method adds all the key and value pairs into mongo collection COMPANY_SETTINGS
	 * 
	 * @param user
	 * @param company
	 * @param organizationalDetails
	 * @throws InvalidInputException
	 */
	private void addOrganizationalDetails(User user, Company company, Map<String, String> organizationalDetails) throws InvalidInputException {
		LOG.debug("Method addOrganizationalDetails called.");
		// create a organization settings object
		OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
		companySettings.setIden(company.getCompanyId());
		if (organizationalDetails.get(CommonConstants.LOGO_NAME) != null) {
			companySettings.setLogo(organizationalDetails.get(CommonConstants.LOGO_NAME));
		}
		ContactDetailsSettings contactDetailSettings = new ContactDetailsSettings();
		contactDetailSettings.setName(company.getCompany());
		contactDetailSettings.setAddress(organizationalDetails.get(CommonConstants.ADDRESS));
		contactDetailSettings.setAddress1(organizationalDetails.get(CommonConstants.ADDRESS1));
		contactDetailSettings.setAddress2(organizationalDetails.get(CommonConstants.ADDRESS2));
		contactDetailSettings.setZipcode(organizationalDetails.get(CommonConstants.ZIPCODE));
		contactDetailSettings.setCountry(organizationalDetails.get(CommonConstants.COUNTRY));
		contactDetailSettings.setCountryCode(organizationalDetails.get(CommonConstants.COUNTRY_CODE));
		// Add work phone number in contact details
		ContactNumberSettings contactNumberSettings = new ContactNumberSettings();
		contactNumberSettings.setWork(organizationalDetails.get(CommonConstants.COMPANY_CONTACT_NUMBER));
		contactDetailSettings.setContact_numbers(contactNumberSettings);
		// Add work Mail id in contact details
		MailIdSettings mailIdSettings = new MailIdSettings();
		mailIdSettings.setWork(user.getEmailId());
		contactDetailSettings.setMail_ids(mailIdSettings);
		companySettings.setVertical(organizationalDetails.get(CommonConstants.VERTICAL));
		companySettings.setContact_details(contactDetailSettings);
		companySettings.setProfileName(generateProfileNameForCompany(company.getCompany(), company.getCompanyId()));
		companySettings.setCreatedOn(System.currentTimeMillis());
		companySettings.setCreatedBy(String.valueOf(user.getUserId()));
		// TODO set lock settings
		companySettings.setLockSettings(new LockSettings());
		LOG.debug("Inserting company settings.");
		organizationUnitSettingsDao.insertOrganizationUnitSettings(companySettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);

		LOG.debug("Method addOrganizationalDetails finished");
	}

	/**
	 * JIRA:SS-117 by RM02 Method to generate profile name for a company based on some rules
	 * 
	 * @param companyName
	 * @param iden
	 * @return
	 * @throws InvalidInputException
	 */
	private String generateProfileNameForCompany(String companyName, long iden) throws InvalidInputException {
		LOG.debug("Generating profile name for companyName:" + companyName + " and iden:" + iden);
		String profileName = null;
		if (companyName == null || companyName.isEmpty()) {
			throw new InvalidInputException("Company name is null or empty while generating profile name");
		}
		profileName = companyName.replaceAll(" ", "-").toLowerCase();

		LOG.debug("Checking uniqueness of profile name generated : " + profileName + " by querying mongo");

		OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName(profileName,
				MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		/**
		 * if there exists a company with the profile name formed, append company iden to get the
		 * unique profile name
		 */
		if (companySettings != null) {

			LOG.debug("Profile name generated is already taken by a company, appending iden to get a new and unique one");
			profileName = profileName + iden;
		}
		LOG.debug("Successfully generated profile name. Returning : " + profileName);
		return profileName;

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
		Region region = addRegion(user, CommonConstants.YES, CommonConstants.DEFAULT_REGION_NAME);
		ProfilesMaster profilesMaster = userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID);

		LOG.debug("Creating user profile for region admin");
		UserProfile userProfileRegionAdmin = createUserProfile(user, user.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID,
				CommonConstants.DEFAULT_BRANCH_ID, region.getRegionId(), profilesMaster.getProfileId(), CommonConstants.PROFILE_STAGES_COMPLETE,
				CommonConstants.STATUS_ACTIVE, String.valueOf(user.getUserId()), String.valueOf(user.getUserId()));
		userProfileDao.save(userProfileRegionAdmin);

		profilesMaster = userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID);

		LOG.debug("Adding a new branch");
		Branch branch = addBranch(user, region, CommonConstants.DEFAULT_BRANCH_NAME, CommonConstants.YES);

		LOG.debug("Creating user profile for branch admin");
		UserProfile userProfileBranchAdmin = createUserProfile(user, user.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID,
				branch.getBranchId(), CommonConstants.DEFAULT_REGION_ID, profilesMaster.getProfileId(), CommonConstants.PROFILE_STAGES_COMPLETE,
				CommonConstants.STATUS_ACTIVE, String.valueOf(user.getUserId()), String.valueOf(user.getUserId()));
		userProfileDao.save(userProfileBranchAdmin);
		profilesMaster = userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID);

		LOG.debug("Creating user profile for agent");
		UserProfile userProfileAgent = createUserProfile(user, user.getCompany(), user.getEmailId(), user.getUserId(),
				CommonConstants.DEFAULT_BRANCH_ID, CommonConstants.DEFAULT_REGION_ID, profilesMaster.getProfileId(),
				CommonConstants.PROFILE_STAGES_COMPLETE, CommonConstants.STATUS_ACTIVE, String.valueOf(user.getUserId()),
				String.valueOf(user.getUserId()));
		userProfileDao.save(userProfileAgent);
		/**
		 * For an individual, only the company admin's profile completion stage is updated, all the
		 * other profiles created by default need no action so their profile completion stage is
		 * marked completed at the time of insert
		 */
		LOG.debug("Updating profile stage for company to payment stage");
		userManagementService.updateProfileCompletionStage(user, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID,
				CommonConstants.DASHBOARD_STAGE);

		LOG.info("Method addIndividual finished.");
	}

	/**
	 * Method to add a Team. Makes entry in Region table.
	 * 
	 * @param user
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	private void addTeamAccountType(User user) throws InvalidInputException, SolrException {
		LOG.debug("Method addTeam started for user : " + user.getLoginName());

		LOG.debug("Adding a new region");
		Region region = addRegion(user, CommonConstants.YES, CommonConstants.DEFAULT_REGION_NAME);
		ProfilesMaster profilesMaster = userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID);

		LOG.debug("Creating user profile for region admin");
		UserProfile userProfileRegionAdmin = createUserProfile(user, user.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID,
				CommonConstants.DEFAULT_BRANCH_ID, region.getRegionId(), profilesMaster.getProfileId(), CommonConstants.PROFILE_STAGES_COMPLETE,
				CommonConstants.STATUS_ACTIVE, String.valueOf(user.getUserId()), String.valueOf(user.getUserId()));
		userProfileDao.save(userProfileRegionAdmin);

		LOG.debug("Adding a new branch");
		Branch branch = addBranch(user, region, CommonConstants.DEFAULT_BRANCH_NAME, CommonConstants.YES);
		solrSearchService.addOrUpdateBranchToSolr(branch);
		profilesMaster = userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID);

		LOG.debug("Creating user profile for branch admin");
		UserProfile userProfileBranchAdmin = createUserProfile(user, user.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID,
				branch.getBranchId(), region.getRegionId(), profilesMaster.getProfileId(), CommonConstants.PROFILE_STAGES_COMPLETE,
				CommonConstants.STATUS_ACTIVE, String.valueOf(user.getUserId()), String.valueOf(user.getUserId()));
		userProfileDao.save(userProfileBranchAdmin);

		LOG.debug("Updating profile stage to payment stage for account type team");
		userManagementService.updateProfileCompletionStage(user, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID,
				CommonConstants.DASHBOARD_STAGE);

		LOG.debug("Method addTeam finished.");
	}

	/**
	 * Method to add company account type
	 * 
	 * @param user
	 * @throws InvalidInputException
	 */
	private void addCompanyAccountType(User user) throws InvalidInputException {
		LOG.debug("Method addCompanyAccountType started for user : " + user.getLoginName());

		LOG.debug("Adding a new region");
		Region region = addRegion(user, CommonConstants.YES, CommonConstants.DEFAULT_REGION_NAME);
		ProfilesMaster profilesMaster = userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID);

		LOG.debug("Creating user profile for region admin");
		UserProfile userProfile = createUserProfile(user, user.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID,
				CommonConstants.DEFAULT_BRANCH_ID, region.getRegionId(), profilesMaster.getProfileId(), CommonConstants.PROFILE_STAGES_COMPLETE,
				CommonConstants.STATUS_ACTIVE, String.valueOf(user.getUserId()), String.valueOf(user.getUserId()));
		userProfileDao.save(userProfile);

		LOG.debug("Method addCompanyAccountType finished.");

	}

	/**
	 * Method to add a new region
	 * 
	 * @param user
	 * @param isDefaultBySystem
	 * @param regionName
	 * @return
	 */
	@Override
	public Region addRegion(User user, int isDefaultBySystem, String regionName) {
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
	@Override
	public Branch addBranch(User user, Region region, String branchName, int isDefaultBySystem) {
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

	private UserProfile createUserProfile(User user, Company company, String emailId, long agentId, long branchId, long regionId,
			int profileMasterId, String profileCompletionStage, int isProfileComplete, String createdBy, String modifiedBy) {
		LOG.info("Method createUserProfile called for username : " + user.getLoginName());
		UserProfile userProfile = new UserProfile();
		userProfile.setAgentId(agentId);
		userProfile.setBranchId(branchId);
		userProfile.setCompany(company);
		userProfile.setEmailId(emailId);
		userProfile.setIsProfileComplete(isProfileComplete);
		userProfile.setProfilesMaster(profilesMasterDao.findById(ProfilesMaster.class, profileMasterId));
		userProfile.setProfileCompletionStage(profileCompletionStage);
		userProfile.setRegionId(regionId);
		userProfile.setStatus(CommonConstants.STATUS_ACTIVE);
		userProfile.setUser(user);
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		userProfile.setCreatedOn(currentTimestamp);
		userProfile.setModifiedOn(currentTimestamp);
		userProfile.setCreatedBy(createdBy);
		userProfile.setModifiedBy(modifiedBy);
		LOG.debug("Method createUserProfile() finished");
		return userProfile;
	}

	@Override
	public void editCompanySettings(User user) {
		LOG.info("Editing company information by user: " + user.toString());
	}

	@Override
	public OrganizationUnitSettings getCompanySettings(User user) throws InvalidInputException {
		if (user == null) {
			throw new InvalidInputException("User is not set");
		}
		LOG.info("Get company settings for the user: " + user.toString());
		// get the company id
		if (user.getCompany() == null) {
			throw new InvalidInputException("User object is partially set. Could not find the comany details");
		}
		long companyId = user.getCompany().getCompanyId();
		OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(companyId,
				MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);

		// Decrypting the encompass password
		if (companySettings != null && companySettings.getCrm_info() != null
				&& companySettings.getCrm_info().getCrm_source().equalsIgnoreCase(CommonConstants.CRM_SOURCE_ENCOMPASS)) {
			EncompassCrmInfo crmInfo = (EncompassCrmInfo) companySettings.getCrm_info();

			String encryptedPassword = crmInfo.getCrm_password();
			String decryptedPassword = encryptionHelper.decryptAES(encryptedPassword, "");

			crmInfo.setCrm_password(decryptedPassword);
		}
		return companySettings;
	}

	@Override
	public Map<Long, OrganizationUnitSettings> getRegionSettingsForUserProfiles(List<UserProfile> userProfiles) throws InvalidInputException {
		Map<Long, OrganizationUnitSettings> regionSettings = null;
		if (userProfiles != null && userProfiles.size() > 0) {
			LOG.info("Get region settings for the user profiles: " + userProfiles.toString());
			regionSettings = new HashMap<Long, OrganizationUnitSettings>();
			OrganizationUnitSettings regionSetting = null;
			// get the region profiles and get the settings for each of them.
			for (UserProfile userProfile : userProfiles) {
				regionSetting = new OrganizationUnitSettings();
				if (userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID) {
					LOG.debug("Getting settings for " + userProfile);
					// get the region id and get the profile
					if (userProfile.getRegionId() > 0l) {
						regionSetting = getRegionSettings(userProfile.getRegionId());
						if (regionSetting != null) {
							regionSettings.put(userProfile.getRegionId(), regionSetting);
						}
					}
					else {
						LOG.warn("Not a valid region id for region profile: " + userProfile + ". Skipping the record");
					}
				}
			}
		}
		else {
			throw new InvalidInputException("User profiles are not set");
		}

		return regionSettings;
	}

	@Override
	public Map<Long, OrganizationUnitSettings> getBranchSettingsForUserProfiles(List<UserProfile> userProfiles) throws InvalidInputException,
			NoRecordsFetchedException {
		Map<Long, OrganizationUnitSettings> branchSettings = null;
		if (userProfiles != null && userProfiles.size() > 0) {
			LOG.info("Get branch settings for the user profiles: " + userProfiles.toString());
			branchSettings = new HashMap<Long, OrganizationUnitSettings>();
			BranchSettings branchSetting = null;
			// get the branch profiles and get the settings for each of them.
			for (UserProfile userProfile : userProfiles) {
				branchSetting = new BranchSettings();
				if (userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID) {
					LOG.debug("Getting settings for " + userProfile);
					// get the branch id and get the profile
					if (userProfile.getBranchId() > 0l) {
						branchSetting = getBranchSettings(userProfile.getBranchId());
						if (branchSetting != null && branchSetting.getOrganizationUnitSettings() != null) {
							branchSettings.put(userProfile.getBranchId(), branchSetting.getOrganizationUnitSettings());
						}
					}
					else {
						LOG.warn("Not a valid branch id for branch profile: " + userProfile + ". Skipping the record");
					}
				}
			}
		}
		else {
			throw new InvalidInputException("User profiles are not set");
		}

		return branchSettings;
	}

	@Override
	public OrganizationUnitSettings getRegionSettings(long regionId) throws InvalidInputException {
		OrganizationUnitSettings regionSettings = null;
		if (regionId <= 0l) {
			throw new InvalidInputException("Invalid region id. :" + regionId);
		}
		LOG.info("Get the region settings for region id: " + regionId);
		regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(regionId,
				MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION);
		return regionSettings;
	}

	/**
	 * Method to fetch branch settings along with the required region settings of region to which
	 * the branch belongs
	 */
	@Transactional
	@Override
	public BranchSettings getBranchSettings(long branchId) throws InvalidInputException, NoRecordsFetchedException {
		OrganizationUnitSettings organizationUnitSettings = null;
		BranchSettings branchSettings = null;
		if (branchId <= 0l) {
			throw new InvalidInputException("Invalid branch id. :" + branchId);
		}
		LOG.info("Get the branch settings for branch id: " + branchId);
		organizationUnitSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(branchId,
				MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION);

		branchSettings = new BranchSettings();
		branchSettings.setOrganizationUnitSettings(organizationUnitSettings);

		Branch branch = branchDao.findById(Branch.class, branchId);
		if (branch == null) {
			throw new NoRecordsFetchedException("No branch present in db for branchId : " + branchId);
		}
		long regionId = branch.getRegion().getRegionId();

		if (branch.getRegion().getIsDefaultBySystem() != CommonConstants.YES) {
			LOG.debug("fetching region settings for regionId : " + regionId);
			OrganizationUnitSettings regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(regionId,
					MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION);
			if (regionSettings == null) {
				throw new NoRecordsFetchedException("No region settings present in mongo for regionId : " + regionId);
			}
			LOG.debug("Successfully fetched region settings for regionId : " + regionId + " adding the info to branch settings");
			branchSettings.setRegionId(regionSettings.getIden());
			branchSettings.setRegionName(regionSettings.getContact_details().getName());
		}
		else {
			LOG.debug("Branch belongs to default region");
		}

		LOG.info("Successfully fetched the branch settings for branch id: " + branchId + " returning : " + branchSettings);
		return branchSettings;
	}

	@Override
	public void updateCRMDetails(OrganizationUnitSettings companySettings, CRMInfo crmInfo) throws InvalidInputException {
		if (companySettings == null) {
			throw new InvalidInputException("Company settings cannot be null.");
		}
		if (crmInfo == null) {
			throw new InvalidInputException("CRM info cannot be null.");
		}
		LOG.info("Updating comapnySettings: " + companySettings + " with crm info: " + crmInfo);
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_CRM_INFO, crmInfo,
				companySettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		LOG.info("Updated the record successfully");
	}

	@Override
	public boolean updateSurveySettings(OrganizationUnitSettings companySettings, SurveySettings surveySettings) throws InvalidInputException {
		if (companySettings == null) {
			throw new InvalidInputException("Company settings cannot be null.");
		}

		LOG.info("Updating comapnySettings: " + companySettings + " with surveySettings: " + surveySettings);
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_SURVEY_SETTINGS,
				surveySettings, companySettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		LOG.info("Updated the record successfully");

		return true;
	}

	@Override
	public void updateLocationEnabled(OrganizationUnitSettings companySettings, boolean isLocationEnabled) throws InvalidInputException {
		if (companySettings == null) {
			throw new InvalidInputException("Company settings cannot be null.");
		}

		LOG.info("Updating companySettings: " + companySettings + " with locationEnabled: " + isLocationEnabled);
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_LOCATION_ENABLED,
				isLocationEnabled, companySettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		LOG.info("Updated the record successfully");
	}

	@Override
	public void updateAccountDisabled(OrganizationUnitSettings companySettings, boolean isAccountDisabled) throws InvalidInputException {
		if (companySettings == null) {
			throw new InvalidInputException("Company settings cannot be null.");
		}

		LOG.info("Updating companySettings: " + companySettings + " with AccountDisabled: " + isAccountDisabled);
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_ACCOUNT_DISABLED,
				isAccountDisabled, companySettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		LOG.info("Updated the isAccountDisabled successfully");
	}

	@Override
	public MailContentSettings updateSurveyParticipationMailBody(OrganizationUnitSettings companySettings, String mailBody, String mailCategory)
			throws InvalidInputException {
		if (companySettings == null) {
			throw new InvalidInputException("Company settings cannot be null.");
		}
		if (mailBody == null || mailBody.isEmpty()) {
			throw new InvalidInputException("Mail body cannot be empty.");
		}
		if (mailCategory == null) {
			throw new InvalidInputException("Invalid mail category.");
		}
		LOG.debug("Updating " + mailCategory + " for settings: " + companySettings.toString() + " with mail body: " + mailBody);
		MailContentSettings originalContentSettings = companySettings.getMail_content();
		MailContentSettings mailContentSettings = new MailContentSettings();
		MailContent mailContent = new MailContent();
		mailContent.setMail_body(mailBody);
		if (mailCategory.equals(CommonConstants.SURVEY_MAIL_BODY_CATEGORY)) {
			if (originalContentSettings != null) {
				mailContentSettings.setTake_survey_reminder_mail(originalContentSettings.getTake_survey_reminder_mail());
			}
			mailContentSettings.setTake_survey_mail(mailContent);
		}
		else if (mailCategory.equals(CommonConstants.SURVEY_REMINDER_MAIL_BODY_CATEGORY)) {
			if (originalContentSettings != null) {
				mailContentSettings.setTake_survey_mail(originalContentSettings.getTake_survey_mail());
			}
			mailContentSettings.setTake_survey_reminder_mail(mailContent);
		}
		else {
			throw new InvalidInputException("Invalid mail category");
		}
		LOG.info("Updating company settings mail content");
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_MAIL_CONTENT,
				mailContentSettings, companySettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		LOG.info("Updated company settings mail content");
		return mailContentSettings;
	}

	@Override
	@Transactional
	public void addDisabledAccount(long companyId) throws InvalidInputException, NoRecordsFetchedException, PaymentException {
		LOG.info("Adding the disabled account to the database for company id : " + companyId);
		if (companyId <= 0) {
			LOG.error("addDisabledAccount : Invalid companyId has been given.");
			throw new InvalidInputException("addDisabledAccount : Invalid companyId has been given.");
		}
		List<LicenseDetail> licenseDetails = null;

		// Fetching the company entity from database
		LOG.info("Fetching the company record from the database");
		Company company = companyDao.findById(Company.class, companyId);

		// Fetching the license details for the company
		LOG.info("Fetching the License Detail record from the database");
		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		licenseDetails = licenceDetailDao.findByKeyValue(LicenseDetail.class, queries);

		if (licenseDetails == null || licenseDetails.isEmpty()) {
			LOG.error("No license detail records have been found for company id : " + companyId);
			throw new NoRecordsFetchedException("No license detail records have been found for company id : " + companyId);
		}

		LicenseDetail licenseDetail = licenseDetails.get(CommonConstants.INITIAL_INDEX);

		LOG.debug("Preparing the DisabledAccount entity to be saved in the database.");
		DisabledAccount disabledAccount = new DisabledAccount();
		disabledAccount.setCompany(company);
		disabledAccount.setLicenseDetail(licenseDetail);
		disabledAccount.setDisableDate(gateway.getDateForCompanyDeactivation(licenseDetail.getSubscriptionId()));
		disabledAccount.setStatus(CommonConstants.STATUS_ACTIVE);
		disabledAccount.setCreatedBy(CommonConstants.ADMIN_USER_NAME);
		disabledAccount.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		disabledAccount.setModifiedBy(CommonConstants.ADMIN_USER_NAME);
		disabledAccount.setModifiedOn(new Timestamp(System.currentTimeMillis()));

		LOG.info("Adding the Disabled Account entity to the database");
		disabledAccountDao.save(disabledAccount);
		LOG.info("Added Disabled Account entity to the database.");
	}

	@Override
	@Transactional
	public void deleteDisabledAccount(long companyId) throws InvalidInputException, NoRecordsFetchedException {
		LOG.info("Deleting the Disabled Account pertaining to company id : " + companyId);
		if (companyId <= 0) {
			LOG.error("addDisabledAccount : Invalid companyId has been given.");
			throw new InvalidInputException("addDisabledAccount : Invalid companyId has been given.");
		}
		List<DisabledAccount> disabledAccounts = null;

		// Fetching the company entity from database
		LOG.info("Fetching the company record from the database");
		Company company = companyDao.findById(Company.class, companyId);

		// Fetching the disabled account entity for the company
		LOG.info("Fetching the Disabled Account from the database");
		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		disabledAccounts = disabledAccountDao.findByKeyValue(DisabledAccount.class, queries);

		if (disabledAccounts == null || disabledAccounts.isEmpty()) {
			LOG.error("No disabled account records have been found for company id : " + companyId);
			throw new NoRecordsFetchedException("No disabled account records have been found for company id : " + companyId);
		}

		DisabledAccount disabledAccount = disabledAccounts.get(CommonConstants.INITIAL_INDEX);
		disabledAccount.setStatus(CommonConstants.STATUS_INACTIVE);
		LOG.info("Removing the disabled account record with id : " + disabledAccount.getId() + "from the database.");

		// Perform soft delete of the record in the database
		disabledAccountDao.update(disabledAccount);
		LOG.info("Record successfully deleted from the database!");
	}
	
	/**
	 * Method to upgrade a default region to a region
	 * @param company
	 * @return
	 * @throws InvalidInputException
	 */
	private Region upgradeDefaultRegion(Region region) throws InvalidInputException{
		
		LOG.info("Upgrading the default region to a user made region");
		if( region == null){
			LOG.error("upgradeDefaultRegion Region parameter is invalid or null");
			throw new InvalidInputException("upgradeDefaultRegion Region parameter is invalid or null");
		}
		
		LOG.debug("Changing the record to change the flag IS_DEFAULT_BY_SYSTEM ");
		region.setIsDefaultBySystem(CommonConstants.STATUS_INACTIVE);
		region.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		LOG.debug("Updating the database to show change from default region to region");
		regionDao.update(region);
		LOG.info(" Region upgrade successful. Returning the region");
		
		return region;		
	}
	
	/**
	 * Method to upgrade a default branch to a branch
	 * @param company
	 * @return
	 * @throws InvalidInputException
	 */
	private Branch upgradeDefaultBranch(Branch branch) throws InvalidInputException{
		
		LOG.info("Upgrading default branch to a user made branch");
		if( branch == null){
			LOG.error("upgradeDefaultBranch Branch parameter is invalid or null");
			throw new InvalidInputException("upgradeDefaultBranch Branch parameter is invalid or null");
		}
		
		// Update the branch record in the database to make it into a user made branch by changing the
		// IS_DEFAULT_BY_SYSTEM flag in the record
		LOG.debug("Changing the record to change the flag IS_DEFAULT_BY_SYSTEM ");
		branch.setIsDefaultBySystem(CommonConstants.STATUS_INACTIVE);
		branch.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		LOG.debug("Updating the database to show change from default branch to branch");
		branchDao.update(branch);
		LOG.info(" Branch upgrade successful. Returning the branch");
		return branch;
		
	}
	
	/**
	 * Function to check if only default region exists for a company
	 * @param company
	 * @return
	 * @throws InvalidInputException 
	 */
	private Region fetchDefaultRegion(Company company) throws InvalidInputException{
		
		if (company == null) {
			LOG.error(" fetchDefaultRegion : Company parameter is null");
			throw new InvalidInputException(" fetchDefaultRegion : Company parameter is null");
		}
		
		LOG.info("Checking if only default region exists");
		Region defaultRegion = null;
		
		//We fetch all the regions for a particular company
		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		
		LOG.debug("Fetching all regions for company with id : " + company.getCompanyId());
		List<Region> regionList = regionDao.findByKeyValue(Region.class, queries);
		
		if(regionList.isEmpty() || regionList==null){
			LOG.info("No regions found for company with id : " +  company.getCompanyId());
			defaultRegion = null;
		}
		//Check if only default region exists
		else if( regionList.size() == CommonConstants.STATUS_ACTIVE  && regionList.get(CommonConstants.INITIAL_INDEX).getIsDefaultBySystem() == CommonConstants.STATUS_ACTIVE){
			//Only default region exists
			LOG.info("Only default region exists for company with id : " + company.getCompanyId());
			defaultRegion = regionList.get(CommonConstants.INITIAL_INDEX);
		}	
		else {
			// More than one regions exist so no default region exists. Return null.
			LOG.info("More than one regions exist. So returning null");
			defaultRegion = null;
		}
		return defaultRegion;
		
	}
	
	/**
	 * Function to check if only default branch exists for a company
	 * @param company
	 * @return
	 * @throws InvalidInputException 
	 */
	private Branch fetchDefaultBranch(Company company) throws InvalidInputException{
		
		if (company == null) {
			LOG.error(" fetchDefaultBranch : Company parameter is null");
			throw new InvalidInputException(" fetchDefaultBranch : Company parameter is null");
		}
		
		LOG.info("Checking if only default branch exists");
		Branch defaultBranch = null;
		
		//We fetch all the branches for a particular company
		HashMap<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		
		LOG.debug("Fetching all branches for company with id : " + company.getCompanyId());
		List<Branch> branchList = branchDao.findByKeyValue(Branch.class, queries);
		
		if(branchList.isEmpty() || branchList==null){
			LOG.info("No branches found for company with id : " +  company.getCompanyId());
			defaultBranch = null;
		}
		//Check if only default branch exists
		else if( branchList.size() == CommonConstants.STATUS_ACTIVE  && branchList.get(CommonConstants.INITIAL_INDEX).getIsDefaultBySystem() == CommonConstants.STATUS_ACTIVE){
			//Only default branch exists
			LOG.info("Only default branch exists for company with id : " + company.getCompanyId());
			defaultBranch = branchList.get(CommonConstants.INITIAL_INDEX);
		}
		else{
			// More than one branches exist so no default branch exists. Return null.
			LOG.info(branchList.size() + " branches found. So returning null.");
			defaultBranch = null;
		}
		
		return defaultBranch;
		
	}
	
	/**
	 * Method to upgrade plan to Company
	 * @param company
	 * @throws InvalidInputException
	 * @throws SolrException
	 * @throws NoRecordsFetchedException
	 */
	private void upgradeToCompany(Company company) throws InvalidInputException, SolrException, NoRecordsFetchedException{
		
		LOG.info("Upgrading to Company");
		if (company == null) {
			LOG.error(" upgradeToCompany : Company parameter is null");
			throw new InvalidInputException(" upgradeToCompany : Company parameter is null");
		}
		
		// In case of upgrading to Company plan we check if default branch exists and upgrade it to a branch
		// We also add the branch settings to mongo collection BRANCH_SETTINGS and to solr.
		
		LOG.debug("checking if only default branch exists and fetching it");
		Branch defaultBranch = fetchDefaultBranch(company);
		if(defaultBranch != null){
			LOG.debug("Default branch exists. Upgrading it to branch");
			Branch upgradedBranch = upgradeDefaultBranch(defaultBranch);
			LOG.debug("Adding the upgraded branch to mongo collection BRANCH_SETTINGS");
			hierarchyManagementService.insertBranchSettings(upgradedBranch);
			LOG.debug("Successfully added settings to mongo, adding the new branch to solr");
			solrSearchService.addOrUpdateBranchToSolr(upgradedBranch);
			LOG.debug("Solr update successful");
		}	
		else {
			LOG.error("No default branch found for company with id : " + company.getCompanyId());
			throw new NoRecordsFetchedException("No default branch found for company with id : " + company.getCompanyId());
		}
		
		LOG.info("Databases upgraded successfully!");
	}
	
	/**
	 * Method to upgrade plan to Enterprise
	 * @param company
	 * @param fromAccountsMasterId
	 * @throws InvalidInputException
	 * @throws SolrException
	 * @throws NoRecordsFetchedException
	 */
	private void upgradeToEnterprise(Company company,int fromAccountsMasterId) throws InvalidInputException, SolrException, NoRecordsFetchedException{
		
		LOG.info("Upgrading to Enterprise");
		if (company == null) {
			LOG.error(" upgradeToEnterprise : Company parameter is null");
			throw new InvalidInputException(" upgradeToEnterprise : Company parameter is null");
		}
		if(fromAccountsMasterId <=0 || fromAccountsMasterId >3){
			LOG.error(" upgradeToCompany : fromAccountsMaster parameter is invalid" );
			throw new InvalidInputException(" upgradeToCompany : fromAccountsMaster parameter is invalid");
		}
		
		// In case of upgrading to Enterprise plan we check if default branch exists and upgrade it to a branch
		// And then we upgrade find the default region and upgrade it user made region.
		// We also add the branch settings to mongo collection BRANCH_SETTINGS and to solr.
		
		LOG.debug("checking if only default branch exists and fetching it");
		Branch defaultBranch = fetchDefaultBranch(company);
		if(defaultBranch != null){
			LOG.debug("Default branch exists. Upgrading it to branch");
			Branch upgradedBranch = upgradeDefaultBranch(defaultBranch);
			LOG.debug("Adding the upgraded branch to mongo collection BRANCH_SETTINGS");
			hierarchyManagementService.insertBranchSettings(upgradedBranch);
			LOG.debug("Successfully added settings to mongo, adding the new branch to solr");
			solrSearchService.addOrUpdateBranchToSolr(upgradedBranch);
			LOG.debug("Solr update successful");
			LOG.debug("Fetching the default region");
			Region defaultRegion = fetchDefaultRegion(company);
			if(defaultRegion == null){
				LOG.error("No default region found for company with id : " + company.getCompanyId());
				throw new NoRecordsFetchedException("No default region found for company with id : " + company.getCompanyId());
			}
			LOG.debug("Default region exists, upgrading it");
			Region upgradedRegion = upgradeDefaultRegion(defaultRegion);
			LOG.debug("Adding the upgraded region to mongo collection REGION_SETTINGS");
			hierarchyManagementService.insertRegionSettings(upgradedRegion);
			LOG.debug("Successfully added settings to mongo, adding the new region to solr");
			solrSearchService.addOrUpdateRegionToSolr(upgradedRegion);
			LOG.debug("Solr update successful");
		}	
		else {
			if(fromAccountsMasterId != 3){
				LOG.error("No default branch found for company with id : " + company.getCompanyId());
				throw new NoRecordsFetchedException("No default branch found for company with id : " + company.getCompanyId());
			}
			LOG.debug("Fetching the default region");
			Region defaultRegion = fetchDefaultRegion(company);
			if(defaultRegion == null){
				LOG.error("No default region found for company with id : " + company.getCompanyId());
				throw new NoRecordsFetchedException("No default region found for company with id : " + company.getCompanyId());
			}
			LOG.debug("Default region exists, upgrading it");
			Region upgradedRegion = upgradeDefaultRegion(defaultRegion);
			LOG.debug("Adding the upgraded region to mongo collection REGION_SETTINGS");
			hierarchyManagementService.insertRegionSettings(upgradedRegion);
			LOG.debug("Successfully added settings to mongo, adding the new region to solr");
			solrSearchService.addOrUpdateRegionToSolr(upgradedRegion);
			LOG.debug("Solr update successful");			
		}
		
		LOG.info("Databases upgraded successfully!");
	}
	
	
	/**
	 * Method called to update databases on plan upgrade
	 * @param company
	 * @param newAccountsMasterPlanId
	 * @throws NoRecordsFetchedException
	 * @throws InvalidInputException
	 * @throws SolrException
	 */
	@Override
	@Transactional
	public void upgradeAccount(Company company,int newAccountsMasterPlanId) throws NoRecordsFetchedException, InvalidInputException, SolrException{
		
		if( company == null){
			LOG.error("upgradePlanAtBackend Company parameter is invalid or null");
			throw new InvalidInputException("upgradePlanAtBackend Company parameter is invalid or null");
		}
		if(newAccountsMasterPlanId <= 0){
			LOG.error("upgradePlanAtBackend AccountsMaster id parameter is invalid");
			throw new InvalidInputException("upgradePlanAtBackend AccountsMaster id parameter is invalid");
		}
		
		//We fetch the license detail record to find the current plan
		LOG.info("Finding the current plan");
		List<LicenseDetail> licenseDetails = null;
		LicenseDetail currentLicenseDetail = null;
		
		LOG.debug("Making the database call to find record for company with id : " + company.getCompanyId());
		Map<String, Object> queries = new HashMap<>();
		queries.put(CommonConstants.COMPANY_COLUMN, company);
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		licenseDetails = licenceDetailDao.findByKeyValue(LicenseDetail.class, queries);
		
		//Check if license details exist
		if (licenseDetails == null || licenseDetails.isEmpty()) {
			LOG.error("No license details records found for company with id : " + company.getCompanyId());
			throw new NoRecordsFetchedException("No license details records found for company with id : " + company.getCompanyId());
		}
		
		currentLicenseDetail = licenseDetails.get(CommonConstants.INITIAL_INDEX);
		LOG.debug("License detail object for company with id : " + company.getCompanyId() + " fetched");
		int currentAccountsMasterId = currentLicenseDetail.getAccountsMaster().getAccountsMasterId();
		
		//Now we update the Region and Branch tables in the database to reflect changes
		LOG.info("Updating the regions and the branches for plan upgrade");
		
		switch (newAccountsMasterPlanId) {
			case CommonConstants.ACCOUNTS_MASTER_TEAM:
				if (currentAccountsMasterId <= 0 || currentAccountsMasterId >1 ) {
					LOG.error(" upgradeAccount : fromAccountsMaster parameter is invalid : value is : " + currentAccountsMasterId );
					throw new InvalidInputException(" upgradeAccount : fromAccountsMaster parameter is invalid: value is : " + currentAccountsMasterId);			
				}
				//In case of upgrading to the team account we need to change only the license details table and add default branch to solr.
				LOG.debug("checking if only default branch exists and fetching it");
				Branch defaultBranch = fetchDefaultBranch(company);
				if(defaultBranch != null){
					LOG.debug("Adding the new branch to solr");
					solrSearchService.addOrUpdateBranchToSolr(defaultBranch);
					LOG.debug("Solr update successful");
				}	
				else {
					LOG.error("No default branch found for company with id : " + company.getCompanyId());
					throw new NoRecordsFetchedException("No default branch found for company with id : " + company.getCompanyId());
				}
				LOG.info("Databases updated to Team plan");
				break;
				
			case CommonConstants.ACCOUNTS_MASTER_COMPANY:
				// We check if the plan we are changing from and the plan we are changing to are correct
				if (currentAccountsMasterId <= 0 || currentAccountsMasterId >2 ) {
					LOG.error(" upgradeAccount : fromAccountsMaster parameter is invalid: value is : " + currentAccountsMasterId );
					throw new InvalidInputException(" upgradeAccount : fromAccountsMaster parameter is invalid: value is : " + currentAccountsMasterId);			
				}
				LOG.info("Calling the database update method for Company plan");
				upgradeToCompany(company);		
				LOG.info("Databases updated to Company plan");
				break;
				
			case CommonConstants.ACCOUNTS_MASTER_ENTERPRISE:
				// We check if the plan we are changing from and the plan we are changing to are correct
				if (currentAccountsMasterId <= 0 || currentAccountsMasterId >3 ) {
					LOG.error(" upgradeAccount : fromAccountsMaster parameter is invalid: value is : " + currentAccountsMasterId );
					throw new InvalidInputException(" upgradeAccount : fromAccountsMaster parameter is invalid: value is : " + currentAccountsMasterId);			
				}
				LOG.info("Calling the database update method for Enterprise plan");
				upgradeToEnterprise(company,currentAccountsMasterId);
				LOG.info("Databases updated to Enterprise plan");
				break;

			default:
				LOG.error(" upgradeAccount : Invalid accounts master id parameter given");
				throw new InvalidInputException(" upgradeAccount : Invalid accounts master id parameter given");
		}
		LOG.info("Upgrade successful!");	
	}

	@Override
	public void updateLogo(String collection, OrganizationUnitSettings companySettings, String logo) throws InvalidInputException {
		if (logo == null || logo.isEmpty()) {
			throw new InvalidInputException("Logo passed can not be null or empty");
		}
		LOG.info("Updating logo");
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_LOGO, logo, companySettings,
				collection);
		LOG.info("Logo updated successfully");
	}

	@Override
	public ContactDetailsSettings updateContactDetails(String collection, OrganizationUnitSettings unitSettings,
			ContactDetailsSettings contactDetailsSettings) throws InvalidInputException {
		if (contactDetailsSettings == null) {
			throw new InvalidInputException("Contact details passed can not be null");
		}
		LOG.info("Updating contact detail information");
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS,
				contactDetailsSettings, unitSettings, collection);
		LOG.info("Contact details updated successfully");
		return contactDetailsSettings;
	}

	@Override
	public List<Association> addAssociations(String collection, OrganizationUnitSettings unitSettings, List<Association> associations)
			throws InvalidInputException {
		if (associations == null || associations.isEmpty()) {
			throw new InvalidInputException("Association name passed can not be null");
		}
		for (Association association : associations) {
			if (association.getName() == null || association.getName().isEmpty()) {
				associations.remove(association);
			}
		}
		LOG.info("Adding associations");
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_ASSOCIATION, associations,
				unitSettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
		LOG.info("Associations added successfully");
		return associations;
	}

	@Override
	public List<Achievement> addAchievements(String collection, OrganizationUnitSettings unitSettings, List<Achievement> achievements)
			throws InvalidInputException {
		if (achievements == null || achievements.isEmpty()) {
			throw new InvalidInputException("Achievements passed can not be null or empty");
		}
		LOG.info("Adding achievements");
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_ACHIEVEMENTS, achievements,
				unitSettings, collection);
		LOG.info("Achievements added successfully");
		return achievements;
	}

	@Override
	public Licenses addLicences(String collection, OrganizationUnitSettings unitSettings, List<String> authorisedIn) throws InvalidInputException {
		if (authorisedIn == null) {
			throw new InvalidInputException("Contact details passed can not be null");
		}

		Licenses licenses = unitSettings.getLicenses();
		if (licenses == null) {
			LOG.debug("Licenses not present for current profile, create a new license object");
			licenses = new Licenses();
		}
		licenses.setAuthorized_in(authorisedIn);
		LOG.info("Adding Licences list");
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_LICENCES, licenses,
				unitSettings, collection);
		LOG.info("Licence authorisations added successfully");
		return licenses;
	}

	@Override
	public void updateSocialMediaTokens(String collection, OrganizationUnitSettings unitSettings, SocialMediaTokens mediaTokens)
			throws InvalidInputException {
		if (mediaTokens == null) {
			throw new InvalidInputException("Media tokens passed was null");
		}
		LOG.info("Updating the social media tokens in profile.");
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_SOCIAL_MEDIA_TOKENS,
				mediaTokens, unitSettings, collection);
		LOG.info("Successfully updated the social media tokens.");
	}

	/**
	 * JIRA:SS-117 by RM02 Method to get the company details based on profile name
	 */
	@Override
	@Transactional
	public OrganizationUnitSettings getCompanyProfileByProfileName(String profileName) throws InvalidInputException {
		LOG.info("Method getCompanyDetailsByProfileName called for profileName : " + profileName);
		if (profileName == null || profileName.isEmpty()) {
			throw new InvalidInputException("profile name is null or empty while getting company details");
		}
		OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName(profileName,
				MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);

		LOG.info("Successfully executed method getCompanyDetailsByProfileName. Returning :" + companySettings);
		return companySettings;
	}

	/**
	 * Method to get the region based on profile name
	 */
	@Override
	public OrganizationUnitSettings getRegionByProfileName(String companyProfileName, String regionProfileName) throws InvalidInputException {
		LOG.info("Method getRegionByProfileName called for companyProfileName:" + companyProfileName + " and regionProfileName:" + regionProfileName);

		/**
		 * generate profileUrl and fetch the region by profileUrl since profileUrl for any region is
		 * unique, whereas profileName is unique only within a company
		 */
		String profileUrl = utils.generateRegionProfileUrl(companyProfileName, regionProfileName);
		OrganizationUnitSettings regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileUrl(profileUrl,
				MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION);

		LOG.info("Method getRegionByProfileName excecuted successfully");
		return regionSettings;
	}

	/**
	 * Method to get the branch based on profile name
	 */
	@Override
	public OrganizationUnitSettings getBranchByProfileName(String companyProfileName, String branchProfileName) throws InvalidInputException {
		LOG.info("Method getBranchByProfileName called for companyProfileName:" + companyProfileName + " and branchProfileName:" + branchProfileName);

		/**
		 * generate profileUrl and fetch the branch by profileUrl since profileUrl for any branch is
		 * unique, whereas profileName is unique only within a company
		 */
		String profileUrl = utils.generateBranchProfileUrl(companyProfileName, branchProfileName);
		OrganizationUnitSettings branchSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileUrl(profileUrl,
				MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION);

		LOG.info("Method getBranchByProfileName excecuted successfully");
		return branchSettings;
	}

	/**
	 * Method to fetch all regions of a company
	 * 
	 * @param companyProfileName
	 * @return
	 * @throws InvalidInputException
	 */
	@Override
	@Transactional
	public List<Region> getRegionsForCompany(String companyProfileName) throws InvalidInputException {
		LOG.info("Method getRegionsForCompany called for companyProfileName:" + companyProfileName);
		OrganizationUnitSettings companySettings = getCompanyProfileByProfileName(companyProfileName);
		List<Region> regions = null;
		if (companySettings != null) {
			long companyId = companySettings.getIden();
			LOG.debug("Fetching regions for company : " + companyId);
			
			/**
			 * Adding columns to be fetched in the list
			 */
			List<String> columnNames = new ArrayList<String>();
			columnNames.add(CommonConstants.REGION_NAME_COLUMN);
			columnNames.add(CommonConstants.REGION_ID_COLUMN);
			
			/**
			 * Building criteria
			 */
			Map<String, Object> queries = new HashMap<String, Object>();
			queries.put(CommonConstants.COMPANY_COLUMN, companyDao.findById(Company.class, companyId));
			queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
			queries.put(CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.STATUS_INACTIVE);
			regions = regionDao.findProjectionsByKeyValue(Region.class, columnNames, queries);
		}
		else {
			LOG.warn("No company settings found for profileName : " + companyProfileName);
		}
		return regions;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("afterPropertiesSet called for organization managemnet service");
		// Populate the verticals master map
		populateVerticalMastersMap();
		LOG.info("afterPropertiesSet finished for organization managemnet service");

	}

	/**
	 * Method to populate the vertical master map
	 */
	private void populateVerticalMastersMap() {
		LOG.debug("Method called to populate vertical masters table");
		List<VerticalsMaster> verticalsMasters = verticalMastersDao.findAllActive(VerticalsMaster.class);
		if (verticalsMasters != null && !verticalsMasters.isEmpty()) {
			for (VerticalsMaster verticalsMaster : verticalsMasters) {
				verticalsMastersMap.put(verticalsMaster.getVerticalsMasterId(), verticalsMaster);
			}
		}
	}

	@Override
	public List<VerticalsMaster> getAllVerticalsMaster() throws InvalidInputException {
		LOG.info("Method getAllVerticalsMaster called to fetch the list of vertical masters");
		List<VerticalsMaster> verticalsMasters = new ArrayList<>();
		for (Map.Entry<Integer, VerticalsMaster> entry : verticalsMastersMap.entrySet()) {
			verticalsMasters.add(entry.getValue());
		}
		if (verticalsMasters.isEmpty()) {
			throw new InvalidInputException("No verticals master found");
		}
		LOG.info("Method getAllVerticalsMaster successfully finished");
		return verticalsMasters;
	}
}
// JIRA: SS-27: By RM05: EOC