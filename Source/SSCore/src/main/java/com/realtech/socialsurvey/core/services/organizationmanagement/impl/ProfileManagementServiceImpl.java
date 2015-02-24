package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

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
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.Achievement;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Association;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.Licenses;
import com.realtech.socialsurvey.core.entities.LockSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;

@DependsOn("generic")
@Component
public class ProfileManagementServiceImpl implements ProfileManagementService, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(ProfileManagementServiceImpl.class);

	@Autowired
	private OrganizationUnitSettingsDao organizationUnitSettingsDao;

	@Autowired
	private OrganizationManagementService organizationManagementService;

	@Autowired
	private GenericDao<UserProfile, Long> userProfileDao;

	@Autowired
	private GenericDao<Company, Long> companyDao;

	@Autowired
	private GenericDao<Branch, Long> branchDao;

	@Autowired
	private GenericDao<User, Long> userDao;

	@Autowired
	private SurveyDetailsDao surveyDetailsDao;

	@Autowired
	private Utils utils;

	@Autowired
	private UserManagementService userManagementService;

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("afterPropertiesSet called for profile management service");
	}

	@Override
	public LockSettings aggregateParentLockSettings(User user, AccountType accountType, UserSettings settings, long branchId, long regionId)
			throws InvalidInputException {
		LOG.info("Method aggregateParentLockSettings() called from ProfileManagementService");
		if (user == null) {
			throw new InvalidInputException("User is not set.");
		}
		if (settings == null) {
			throw new InvalidInputException("Invalid user settings.");
		}
		if (accountType == null) {
			throw new InvalidInputException("Invalid account type.");
		}

		LockSettings parentLockSettings = null;
		// If user is Company Admin, Lock settings would be default
		if (user.isCompanyAdmin()) {
			LOG.debug("Setting default LockSettings for Company Admin");
			parentLockSettings = new LockSettings();
		}

		// If user is not Company Admin, Lock settings need to be aggregated
		else {
			switch (accountType) {
				case INDIVIDUAL:
				case TEAM:
					// Individual
					if (user.isAgent()) {
						LOG.debug("Setting company LockSettings for Agent of Individual/Team account type");
						parentLockSettings = settings.getCompanySettings().getLockSettings();
					}
					break;

				case COMPANY:
					// Branch Admin
					if (user.isBranchAdmin()) {
						LOG.debug("Setting company LockSettings for Branch Admin of Company account type");
						parentLockSettings = settings.getCompanySettings().getLockSettings();
					}

					// Individual
					else if (user.isAgent()) {
						LOG.debug("Aggregating LockSettings till Branch for Agent of Company account type");
						parentLockSettings = lockSettingsTillBranch(settings.getCompanySettings(), null, settings.getBranchSettings().get(branchId));
					}
					break;

				case ENTERPRISE:
					// Region Admin
					if (user.isRegionAdmin()) {
						LOG.debug("Setting company LockSettings for Region Admin of Enterprise account type");
						parentLockSettings = settings.getCompanySettings().getLockSettings();
					}

					// Branch Admin
					else if (user.isBranchAdmin()) {
						LOG.debug("Aggregating LockSettings till Region for Branch Admin of Enterprise account type");
						parentLockSettings = lockSettingsTillRegion(settings.getCompanySettings(), settings.getRegionSettings().get(regionId));
					}

					// Individual
					else if (user.isAgent()) {
						LOG.debug("Aggregating LockSettings till Branch for Agent of Enterprise account type");
						parentLockSettings = lockSettingsTillBranch(settings.getCompanySettings(), settings.getRegionSettings().get(regionId), settings
								.getBranchSettings().get(branchId));
					}
					break;

				default:
					throw new InvalidInputException("Account type is invalid in aggregateParentLockSettings");
			}
		}
		LOG.info("Method aggregateParentLockSettings() finished from ProfileManagementService");
		return parentLockSettings;
	}

	private LockSettings lockSettingsTillRegion(OrganizationUnitSettings companySettings, OrganizationUnitSettings regionSettings)
			throws InvalidInputException {
		LOG.debug("Method lockSettingsTillRegion() called from ProfileManagementService");
		if (companySettings == null) {
			throw new InvalidInputException("No Settings found");
		}

		// Fetching Company Lock settings
		LockSettings parentLock = companySettings.getLockSettings();

		// Aggregate Region Lock settings if exists
		if (regionSettings != null) {
			parentLock = aggregateLockSettings(regionSettings.getLockSettings(), parentLock);
		}
		LOG.debug("Method lockSettingsTillRegion() finished from ProfileManagementService");
		return parentLock;
	}

	private LockSettings lockSettingsTillBranch(OrganizationUnitSettings companySettings, OrganizationUnitSettings regionSettings,
			OrganizationUnitSettings branchSettings) throws InvalidInputException {
		LOG.debug("Method lockSettingsTillBranch() called from ProfileManagementService");
		if (companySettings == null) {
			throw new InvalidInputException("No Settings found");
		}

		// Fetching Company Lock settings
		LockSettings parentLock = companySettings.getLockSettings();

		// Aggregate Region Lock settings if exists
		if (regionSettings != null) {
			parentLock = aggregateLockSettings(regionSettings.getLockSettings(), parentLock);
		}

		// Aggregate Branch Lock settings if exists
		if (branchSettings != null) {
			parentLock = aggregateLockSettings(branchSettings.getLockSettings(), parentLock);
		}
		LOG.debug("Method lockSettingsTillBranch() finished from ProfileManagementService");
		return parentLock;
	}

	private LockSettings aggregateLockSettings(LockSettings higherLock, LockSettings parentLock) {
		LOG.debug("Method aggregateLockSettings() called from ProfileManagementService");
		
		// Aggregate parentLockSettings with higherLockSettings
		if (higherLock != null) {
			if (higherLock.getIsLogoLocked()) {
				parentLock.setLogoLocked(true);
			}
			if (higherLock.getIsDisplayNameLocked()) {
				parentLock.setDisplayNameLocked(true);
			}
			if (higherLock.getIsWebAddressLocked()) {
				parentLock.setWebAddressLocked(true);
			}
			if (higherLock.getIsWorkPhoneLocked()) {
				parentLock.setWorkPhoneLocked(true);
			}
			if (higherLock.getIsPersonalPhoneLocked()) {
				parentLock.setPersonalPhoneLocked(true);
			}
			if (higherLock.getIsFaxPhoneLocked()) {
				parentLock.setFaxPhoneLocked(true);
			}
			if (higherLock.getIsAboutMeLocked()) {
				parentLock.setAboutMeLocked(true);
			}
		}
		LOG.debug("Method aggregateLockSettings() finished from ProfileManagementService");
		return parentLock;
	}

	@Override
	public OrganizationUnitSettings aggregateUserProfile(User user, AccountType accountType, UserSettings settings, long agentId, long branchId,
			long regionId) throws InvalidInputException {
		LOG.info("Method aggregateUserProfile() called from ProfileManagementService");
		if (user == null) {
			throw new InvalidInputException("User is not set.");
		}
		if (settings == null) {
			throw new InvalidInputException("Invalid user settings.");
		}
		if (accountType == null) {
			throw new InvalidInputException("Invalid account type.");
		}

		OrganizationUnitSettings userProfile = null;
		// If user is Company Admin, returning CompanyAdmin Profile
		if (user.isCompanyAdmin()) {
			LOG.debug("Setting Company Profile for Company Admin");
			userProfile = settings.getCompanySettings();
		}
		
		// If user is not Company Admin, Profile need to be aggregated
		else {
			switch (accountType) {
				case INDIVIDUAL:
				case TEAM:
					// Individual
					if (user.isAgent()) {
						LOG.debug("Aggregate Profile for Agent of Individual/Team account type");
						userProfile = aggregateAgentProfile(settings.getCompanySettings(), null, null, settings.getAgentSettings().get(agentId));
					}
					break;

				case COMPANY:
					LOG.info("Company account type");
					// Branch Admin
					if (user.isBranchAdmin()) {
						LOG.debug("Aggregate Profile for BranchAdmin of Company account type");
						userProfile = aggregateBranchProfile(settings.getCompanySettings(), null, settings.getBranchSettings().get(branchId));
					}

					// Individual
					else if (user.isAgent()) {
						LOG.debug("Aggregate Profile for Agent of Company account type");
						userProfile = aggregateAgentProfile(settings.getCompanySettings(), null, settings.getBranchSettings().get(branchId), settings
								.getAgentSettings().get(agentId));
					}
					break;

				case ENTERPRISE:
					LOG.info("Enterprise account type");
					// Region Admin
					if (user.isRegionAdmin()) {
						LOG.debug("Aggregate Profile for RegionAdmin of Enterprise account type");
						userProfile = aggregateRegionProfile(settings.getCompanySettings(), settings.getRegionSettings().get(regionId));
					}

					// Branch Admin
					else if (user.isBranchAdmin()) {
						LOG.debug("Aggregate Profile for BranchAdmin of Enterprise account type");
						userProfile = aggregateBranchProfile(settings.getCompanySettings(), settings.getRegionSettings().get(regionId), settings
								.getBranchSettings().get(branchId));
					}

					// Individual
					else if (user.isAgent()) {
						LOG.debug("Aggregate Profile for Agent of Enterprise account type");
						userProfile = aggregateAgentProfile(settings.getCompanySettings(), settings.getRegionSettings().get(regionId), settings
								.getBranchSettings().get(branchId), settings.getAgentSettings().get(agentId));
					}
					break;

				default:
					throw new InvalidInputException("Account type is invalid in aggregateUserProfile");
			}
		}
		LOG.info("Method aggregateUserProfile() finished from ProfileManagementService");
		return userProfile;
	}

	private OrganizationUnitSettings aggregateRegionProfile(OrganizationUnitSettings companySettings, OrganizationUnitSettings regionSettings)
			throws InvalidInputException {
		LOG.debug("Method aggregateRegionProfile() called from ProfileManagementService");
		if (companySettings == null || regionSettings == null) {
			throw new InvalidInputException("No Settings found");
		}

		// Aggregate Company Profile settings
		LockSettings userLock = new LockSettings();
		regionSettings = aggregateProfileData(companySettings, regionSettings, userLock);

		// Aggregate Region Profile Settings
		// to reflect lockSettings of Region
		regionSettings = aggregateProfileData(regionSettings, regionSettings, userLock);
		regionSettings.setLockSettings(userLock);

		LOG.debug("Method aggregateRegionProfile() finished from ProfileManagementService");
		return regionSettings;
	}

	private OrganizationUnitSettings aggregateBranchProfile(OrganizationUnitSettings companySettings, OrganizationUnitSettings regionSettings,
			OrganizationUnitSettings branchSettings) throws InvalidInputException {
		LOG.debug("Method aggregateBranchProfile() called from ProfileManagementService");
		if (companySettings == null || branchSettings == null) {
			throw new InvalidInputException("No Settings found");
		}

		// Aggregate Company Profile settings
		LockSettings userLock = new LockSettings();
		branchSettings = aggregateProfileData(companySettings, branchSettings, userLock);

		// Aggregate Region Profile settings if exists
		if (regionSettings != null) {
			branchSettings = aggregateProfileData(regionSettings, branchSettings, userLock);
		}
		
		// Aggregate Branch Profile Settings
		// to reflect lockSettings of Branch
		branchSettings = aggregateProfileData(branchSettings, branchSettings, userLock);
		branchSettings.setLockSettings(userLock);

		LOG.debug("Method aggregateBranchProfile() finished from ProfileManagementService");
		return branchSettings;
	}

	private OrganizationUnitSettings aggregateAgentProfile(OrganizationUnitSettings companySettings, OrganizationUnitSettings regionSettings,
			OrganizationUnitSettings branchSettings, OrganizationUnitSettings agentSettings) throws InvalidInputException {
		LOG.debug("Method aggregateAgentProfile() called from ProfileManagementService");
		if (companySettings == null || agentSettings == null) {
			throw new InvalidInputException("No Settings found");
		}

		// Aggregate Company Profile settings
		LockSettings userLock = new LockSettings();
		agentSettings = aggregateProfileData(companySettings, agentSettings, userLock);

		// Aggregate Region Profile settings if exists
		if (regionSettings != null) {
			agentSettings = aggregateProfileData(regionSettings, agentSettings, userLock);
		}

		// Aggregate Branch Profile settings if exists
		if (branchSettings != null) {
			agentSettings = aggregateProfileData(branchSettings, agentSettings, userLock);
		}

		// No Aggregation needed Agent Profile Settings
		// manully setting since agent do not have lockSettings
		agentSettings.setLockSettings(userLock);
		
		LOG.debug("Method aggregateAgentProfile() finished from ProfileManagementService");
		return agentSettings;
	}

	private OrganizationUnitSettings aggregateProfileData(OrganizationUnitSettings parentProfile, OrganizationUnitSettings userProfile,
			LockSettings userLock) {
		LOG.debug("Method aggregateProfileData() called from ProfileManagementService");

		// Aggregate parentProfile data with userProfile
		LockSettings parentLock = parentProfile.getLockSettings();
		if (parentLock != null) {
			if (parentLock.getIsLogoLocked() && !userLock.getIsLogoLocked() && parentProfile.getLogo() != null) {
				userProfile.setLogo(parentProfile.getLogo());
				userLock.setLogoLocked(true);
			}
			if (parentLock.getIsDisplayNameLocked() && !userLock.getIsDisplayNameLocked() && parentProfile.getContact_details().getName() != null) {
				userProfile.getContact_details().setName(parentProfile.getContact_details().getName());
				userLock.setDisplayNameLocked(true);
			}
			if (parentLock.getIsWebAddressLocked() && !userLock.getIsWebAddressLocked() && userProfile.getContact_details().getWeb_addresses() != null) {
				userProfile.getContact_details().getWeb_addresses().setWork(parentProfile.getContact_details().getWeb_addresses().getWork());
				userLock.setLogoLocked(true);
			}
			if (parentLock.getIsWorkPhoneLocked() && !userLock.getIsWorkPhoneLocked() && userProfile.getContact_details().getContact_numbers() != null) {
				userProfile.getContact_details().getContact_numbers().setWork(parentProfile.getContact_details().getContact_numbers().getWork());
				userLock.setWorkPhoneLocked(true);
			}
			if (parentLock.getIsPersonalPhoneLocked() && !userLock.getIsPersonalPhoneLocked() && userProfile.getContact_details().getContact_numbers() != null) {
				userProfile.getContact_details().getContact_numbers().setPersonal(parentProfile.getContact_details().getContact_numbers().getPersonal());
				userLock.setPersonalPhoneLocked(true);
			}
			if (parentLock.getIsFaxPhoneLocked() && !userLock.getIsFaxPhoneLocked() && userProfile.getContact_details().getContact_numbers() != null) {
				userProfile.getContact_details().getContact_numbers().setFax(parentProfile.getContact_details().getContact_numbers().getFax());
				userLock.setFaxPhoneLocked(true);
			}
			if (parentLock.getIsAboutMeLocked() && !userLock.getIsAboutMeLocked() && parentProfile.getContact_details().getAbout_me() != null) {
				userProfile.getContact_details().setAbout_me(parentProfile.getContact_details().getAbout_me());
				userLock.setAboutMeLocked(true);
			}
		}
		LOG.debug("Method aggregateProfileData() finished from ProfileManagementService");
		return userProfile;
	}

	// Logo
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

	// ProfileImage
	@Override
	public void updateProfileImage(String collection, OrganizationUnitSettings companySettings, String image) throws InvalidInputException {
		if (image == null || image.isEmpty()) {
			throw new InvalidInputException("image passed can not be null or empty");
		}
		LOG.info("Updating image");
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_IMAGE, image,
				companySettings, collection);
		LOG.info("Image updated successfully");
	}

	// Associations
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
	public List<Association> addAgentAssociations(String collection, AgentSettings agentSettings, List<Association> associations)
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
		organizationUnitSettingsDao
				.updateParticularKeyAgentSettings(MongoOrganizationUnitSettingDaoImpl.KEY_ASSOCIATION, associations, agentSettings);
		LOG.info("Associations added successfully");
		return associations;
	}

	// Lock Settings
	@Override
	public LockSettings updateLockSettings(String collection, OrganizationUnitSettings unitSettings, LockSettings lockSettings)
			throws InvalidInputException {
		if (lockSettings == null) {
			throw new InvalidInputException("LockSettings passed can not be null");
		}
		LOG.info("Updating lock detail information");
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_LOCK_SETTINGS, lockSettings,
				unitSettings, collection);
		LOG.info("lock details updated successfully");
		return lockSettings;
	}

	// Contact details
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
	public ContactDetailsSettings updateAgentContactDetails(String collection, AgentSettings agentSettings,
			ContactDetailsSettings contactDetailsSettings) throws InvalidInputException {
		if (contactDetailsSettings == null) {
			throw new InvalidInputException("Contact details passed can not be null");
		}
		LOG.info("Updating contact detail information");
		organizationUnitSettingsDao.updateParticularKeyAgentSettings(MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS,
				contactDetailsSettings, agentSettings);
		LOG.info("Contact details updated successfully");
		return contactDetailsSettings;
	}

	// Achievements
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
	public List<Achievement> addAgentAchievements(String collection, AgentSettings agentSettings, List<Achievement> achievements)
			throws InvalidInputException {
		if (achievements == null || achievements.isEmpty()) {
			throw new InvalidInputException("Achievements passed can not be null or empty");
		}
		LOG.info("Adding achievements");
		organizationUnitSettingsDao.updateParticularKeyAgentSettings(MongoOrganizationUnitSettingDaoImpl.KEY_ACHIEVEMENTS, achievements,
				agentSettings);
		LOG.info("Achievements added successfully");
		return achievements;
	}

	// Licenses
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
	public Licenses addAgentLicences(String collection, AgentSettings agentSettings, List<String> authorisedIn) throws InvalidInputException {
		if (authorisedIn == null) {
			throw new InvalidInputException("Contact details passed can not be null");
		}

		Licenses licenses = agentSettings.getLicenses();
		if (licenses == null) {
			LOG.debug("Licenses not present for current profile, create a new license object");
			licenses = new Licenses();
		}
		licenses.setAuthorized_in(authorisedIn);
		LOG.info("Adding Licences list");
		organizationUnitSettingsDao.updateParticularKeyAgentSettings(MongoOrganizationUnitSettingDaoImpl.KEY_LICENCES, licenses, agentSettings);
		LOG.info("Licence authorisations added successfully");
		return licenses;
	}

	// Social Tokens
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
	 * Method to fetch all users under the specified branch of specified company
	 */
	@Override
	@Transactional
	public List<AgentSettings> getIndividualsForBranch(String companyProfileName, String branchProfileName) throws InvalidInputException {
		if (companyProfileName == null || companyProfileName.isEmpty()) {
			throw new InvalidInputException("companyProfileName is null or empty in getIndividualsForBranch");
		}
		if (branchProfileName == null || branchProfileName.isEmpty()) {
			throw new InvalidInputException("branchProfileName is null or empty in getIndividualsForBranch");
		}
		LOG.info("Method getIndividualsForBranch called for companyProfileName: " + companyProfileName + " branchProfileName:" + branchProfileName);
		List<AgentSettings> users = null;
		OrganizationUnitSettings branchSettings = getBranchByProfileName(companyProfileName, branchProfileName);
		if (branchSettings != null) {
			LOG.debug("Fetching user profiles for branchId: " + branchSettings.getIden());
			users = getIndividualsByBranchId(branchSettings.getIden());
		}
		LOG.info("Method getIndividualsForBranch executed successfully");
		return users;
	}

	/**
	 * Method to fetch all users under the specified region of specified company
	 * 
	 * @throws NoRecordsFetchedException
	 */
	@Override
	@Transactional
	public List<AgentSettings> getIndividualsForRegion(String companyProfileName, String regionProfileName) throws InvalidInputException,
			NoRecordsFetchedException {
		if (companyProfileName == null || companyProfileName.isEmpty()) {
			throw new InvalidInputException("companyProfileName is null or empty in getIndividualsForRegion");
		}
		if (regionProfileName == null || regionProfileName.isEmpty()) {
			throw new InvalidInputException("regionProfileName is null or empty in getIndividualsForRegion");
		}
		LOG.info("Method getIndividualsForRegion called for companyProfileName:" + companyProfileName + " and branchProfileName:" + regionProfileName);
		List<AgentSettings> users = null;
		OrganizationUnitSettings regionSettings = getRegionByProfileName(companyProfileName, regionProfileName);
		if (regionSettings != null) {
			users = getIndividualsByRegionId(regionSettings.getIden());
		}

		LOG.info("Method getIndividualsForRegion executed successfully");
		return users;
	}

	/**
	 * Method to fetch all individuals directly linked to a company
	 */
	@Override
	@Transactional
	public List<AgentSettings> getIndividualsForCompany(String companyProfileName) throws InvalidInputException, NoRecordsFetchedException {
		if (companyProfileName == null || companyProfileName.isEmpty()) {
			throw new InvalidInputException("companyProfileName is null or empty in getIndividualsForCompany");
		}
		LOG.info("Method getIndividualsForCompany called for companyProfileName: " + companyProfileName);
		List<AgentSettings> users = null;
		OrganizationUnitSettings companySettings = getCompanyProfileByProfileName(companyProfileName);
		if (companySettings != null) {
			Region defaultRegion = organizationManagementService.getDefaultRegionForCompany(companyDao.findById(Company.class,
					companySettings.getIden()));
			if (defaultRegion != null) {
				Branch defaultBranch = organizationManagementService.getDefaultBranchForRegion(defaultRegion.getRegionId());
				users = getIndividualsByBranchId(defaultBranch.getBranchId());
			}
		}
		LOG.info("Method getIndividualsForCompany executed successfully");
		return users;
	}

	/**
	 * Method to get the region based on profile name
	 */
	@Override
	@Transactional
	public OrganizationUnitSettings getRegionByProfileName(String companyProfileName, String regionProfileName) throws InvalidInputException {
		LOG.info("Method getRegionByProfileName called for companyProfileName:" + companyProfileName + " and regionProfileName:" + regionProfileName);
		if (companyProfileName == null || companyProfileName.isEmpty()) {
			throw new InvalidInputException("companyProfileName is null or empty in getRegionByProfileName");
		}
		if (regionProfileName == null || regionProfileName.isEmpty()) {
			throw new InvalidInputException("regionProfileName is null or empty in getRegionByProfileName");
		}
		/**
		 * generate profileUrl and fetch the region by profileUrl since profileUrl for any region is
		 * unique, whereas profileName is unique only within a company
		 */
		String profileUrl = utils.generateRegionProfileUrl(companyProfileName, regionProfileName);
		OrganizationUnitSettings companySettings = getCompanyProfileByProfileName(companyProfileName);
		OrganizationUnitSettings regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileUrl(profileUrl,
				MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION);

		LOG.debug("Generating final region settings based on lock settings");
		regionSettings = aggregateRegionProfile(companySettings, regionSettings);
		LOG.info("Method getRegionByProfileName excecuted successfully");
		return regionSettings;
	}

	/**
	 * Method to get the branch based on profile name
	 */
	@Override
	public OrganizationUnitSettings getBranchByProfileName(String companyProfileName, String branchProfileName) throws InvalidInputException {
		LOG.info("Method getBranchByProfileName called for companyProfileName:" + companyProfileName + " and branchProfileName:" + branchProfileName);

		OrganizationUnitSettings companySettings = getCompanyProfileByProfileName(companyProfileName);
		/**
		 * generate profileUrl and fetch the branch by profileUrl since profileUrl for any branch is
		 * unique, whereas profileName is unique only within a company
		 */
		String profileUrl = utils.generateBranchProfileUrl(companyProfileName, branchProfileName);
		OrganizationUnitSettings branchSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileUrl(profileUrl,
				MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION);

		LOG.debug("Fetching branch from db to identify the region");
		Branch branch = branchDao.findById(Branch.class, branchSettings.getIden());
		OrganizationUnitSettings regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(branch.getRegion().getRegionId(),
				MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION);

		branchSettings = aggregateBranchProfile(companySettings, regionSettings, branchSettings);

		LOG.info("Method getBranchByProfileName excecuted successfully");
		return branchSettings;
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
	 * Method to get profile of an individual
	 */
	@Override
	public OrganizationUnitSettings getIndividualByProfileName(String companyProfileName, String agentProfileName) throws InvalidInputException {
		LOG.info("Method getIndividualByProfileName called for companyProfileName:" + companyProfileName + " and agentProfileName:"
				+ agentProfileName);
		OrganizationUnitSettings agentSettings = null;

		if (companyProfileName == null || companyProfileName.isEmpty()) {
			throw new InvalidInputException("company profile name is null or empty while getting agent settings");
		}
		if (agentProfileName == null || agentProfileName.isEmpty()) {
			throw new InvalidInputException("agentProfileName is null or empty while getting agent settings");
		}
		agentSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName(agentProfileName,
				MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION);
		User user = userDao.findById(User.class, agentSettings.getIden());
		List<UserProfile> userProfiles = user.getUserProfiles();

		if (userProfiles != null && !userProfiles.isEmpty()) {
			userProfiles.get(0);
		}
		// TODO fetch final agent settings based on the locks

		LOG.info("Method getIndividualByProfileName executed successfully");
		return agentSettings;
	}

	/**
	 * Method to get aggregated reviews of all agents of a company
	 */
	@Override
	public List<SurveyDetails> getReviewsForCompany(long companyId, double startScore, double limitScore, int startIndex, int numOfRows)
			throws InvalidInputException {
		LOG.info("Method getReviewsForCompany called for companyId:" + companyId + " and limitScore:" + limitScore);
		List<SurveyDetails> surveyDetails = surveyDetailsDao.getFeedbacks(CommonConstants.COMPANY_ID_COLUMN, companyId,startIndex,numOfRows, startScore, limitScore);
		LOG.info("Method getReviewsForCompany executed successfully");
		return surveyDetails;
	}

	/**
	 * Method to get average rating for individuals of a company
	 */
	@Override
	public double getAverageRatingForCompany(long companyId) throws InvalidInputException {
		LOG.info("Method getAverageRatingForCompany called for companyId:" + companyId);
		if (companyId <= 0l) {
			throw new InvalidInputException("Company id is invalid for getting average rating os a company");
		}
		double averageRating = surveyDetailsDao.getRatingForPastNdays(CommonConstants.COMPANY_ID_COLUMN, companyId, -1);

		LOG.info("Method getAverageRatingForCompany executed successfully");
		return averageRating;
	}

	@Override
	@Transactional
	public List<AgentSettings> getIndividualsByBranchId(long branchId) throws InvalidInputException {
		LOG.info("Method getIndividualsByBranchId called for branchId:" + branchId);
		List<AgentSettings> users = null;
		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		queries.put(CommonConstants.BRANCH_ID_COLUMN, branchId);
		queries.put(CommonConstants.PROFILE_MASTER_COLUMN,
				userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID));
		List<UserProfile> userProfiles = userProfileDao.findByKeyValue(UserProfile.class, queries);
		if (userProfiles != null && !userProfiles.isEmpty()) {
			users = new ArrayList<AgentSettings>();
			for (UserProfile userProfile : userProfiles) {
				users.add(organizationUnitSettingsDao.fetchAgentSettingsById(userProfile.getUser().getUserId()));
			}
			LOG.debug("Returning :" + users.size() + " individuals for branch : " + branchId);
		}
		LOG.info("Method getIndividualsByBranchId executed successfully");
		return users;
	}

	@Override
	public long getReviewsCountForCompany(long companyId, double minScore, double maxScore) {
		LOG.info("Method getReviewsCountForCompany called for companyId:" + companyId + " minscore:" + minScore + " maxscore:" + maxScore);
		long reviewsCount = 0;
		reviewsCount = surveyDetailsDao.getFeedBacksCount(CommonConstants.COMPANY_ID_COLUMN, companyId, minScore, maxScore);
		LOG.info("Method getReviewsCountForCompany executed successfully");
		return reviewsCount;
	}

	/**
	 * Method to fetch all users under the specified region
	 */
	@Override
	@Transactional
	public List<AgentSettings> getIndividualsByRegionId(long regionId) throws InvalidInputException, NoRecordsFetchedException {
		LOG.info("Method getIndividualsByRegionId called for regionId: " + regionId);
		List<AgentSettings> users = null;
		if (regionId <= 0l) {
			throw new InvalidInputException("Region id is not set for getIndividualsByRegionId");
		}
		Branch defaultBranch = organizationManagementService.getDefaultBranchForRegion(regionId);

		Map<String, Object> queries = new HashMap<String, Object>();
		queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
		queries.put(CommonConstants.REGION_ID_COLUMN, regionId);
		queries.put(CommonConstants.BRANCH_ID_COLUMN, defaultBranch.getBranchId());
		queries.put(CommonConstants.PROFILE_MASTER_COLUMN,
				userManagementService.getProfilesMasterById(CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID));

		LOG.debug("calling method to fetch user profiles under region :" + regionId);
		List<UserProfile> userProfiles = userProfileDao.findByKeyValue(UserProfile.class, queries);

		if (userProfiles != null && !userProfiles.isEmpty()) {
			LOG.debug("Obtained userProfiles with size : " + userProfiles.size());
			users = new ArrayList<AgentSettings>();
			for (UserProfile userProfile : userProfiles) {
				users.add(organizationUnitSettingsDao.fetchAgentSettingsById(userProfile.getUser().getUserId()));
			}
		}
		LOG.info("Method getIndividualsByRegionId executed successfully");
		return users;
	}

}