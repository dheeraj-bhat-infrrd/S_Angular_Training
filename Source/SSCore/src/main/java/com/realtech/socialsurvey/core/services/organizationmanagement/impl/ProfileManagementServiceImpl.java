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

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("afterPropertiesSet called for profile management service");
	}

	public OrganizationUnitSettings finalizeProfile(User user, AccountType accountType, UserSettings settings, long agentId, long branchId,
			long regionId) throws InvalidInputException {
		LOG.info("Method finalizeProfileDetail() called from ProfileManagementService");
		if (user == null) {
			throw new InvalidInputException("User is not set.");
		}
		if (settings == null) {
			throw new InvalidInputException("Invalid user settings.");
		}
		if (accountType == null) {
			throw new InvalidInputException("Invalid account type.");
		}

		OrganizationUnitSettings finalSettings = null;
		switch (accountType) {
			case INDIVIDUAL:
			case TEAM:
				LOG.info("Individual/Team account type");
				// Company Admin
				if (user.isCompanyAdmin()) {
					finalSettings = settings.getCompanySettings();
				}

				// Individual
				else if (user.isAgent()) {
					finalSettings = generateAgentProfile(settings.getCompanySettings(), null, null, settings.getAgentSettings().get(agentId));
				}
				break;

			case COMPANY:
				LOG.info("Company account type");
				// Company Admin
				if (user.isCompanyAdmin()) {
					finalSettings = settings.getCompanySettings();
				}

				// Branch Admin
				else if (user.isBranchAdmin()) {
					finalSettings = generateBranchProfile(settings.getCompanySettings(), null, settings.getBranchSettings().get(branchId));
				}

				// Individual
				else if (user.isAgent()) {
					finalSettings = generateAgentProfile(settings.getCompanySettings(), null, settings.getBranchSettings().get(branchId), settings
							.getAgentSettings().get(agentId));
				}
				break;

			case ENTERPRISE:
				LOG.info("Enterprise account type");
				// Company Admin
				if (user.isCompanyAdmin()) {
					finalSettings = settings.getCompanySettings();
				}

				// Region Admin
				else if (user.isRegionAdmin()) {
					finalSettings = generateRegionProfile(settings.getCompanySettings(), settings.getRegionSettings().get(regionId));
				}

				// Branch Admin
				else if (user.isBranchAdmin()) {
					finalSettings = generateBranchProfile(settings.getCompanySettings(), settings.getRegionSettings().get(regionId), settings
							.getBranchSettings().get(branchId));
				}

				// Individual
				else if (user.isAgent()) {
					finalSettings = generateAgentProfile(settings.getCompanySettings(), settings.getRegionSettings().get(regionId), settings
							.getBranchSettings().get(branchId), settings.getAgentSettings().get(agentId));
				}
				break;

			default:
				throw new InvalidInputException("Account type is invalid in finalizeProfileDetail");
		}

		LOG.info("Method finalizeProfileDetail() finished from ProfileManagementService");
		return finalSettings;
	}

	private OrganizationUnitSettings generateRegionProfile(OrganizationUnitSettings companySettings, OrganizationUnitSettings regionSettings)
			throws InvalidInputException {
		if (companySettings == null || regionSettings == null) {
			throw new InvalidInputException("No Settings found");
		}

		// Company Lock settings
		LockSettings regionLock = new LockSettings();
		updateSettings(companySettings, regionSettings, regionLock);

		regionSettings.setLockSettings(regionLock);
		return regionSettings;
	}

	private OrganizationUnitSettings generateBranchProfile(OrganizationUnitSettings companySettings, OrganizationUnitSettings regionSettings,
			OrganizationUnitSettings branchSettings) throws InvalidInputException {
		if (companySettings == null || branchSettings == null) {
			throw new InvalidInputException("No Settings found");
		}

		// Company Lock settings
		LockSettings branchLock = new LockSettings();
		updateSettings(companySettings, branchSettings, branchLock);

		// Region Lock settings
		if (regionSettings != null) {
			updateSettings(regionSettings, branchSettings, branchLock);
		}

		branchSettings.setLockSettings(branchLock);
		return branchSettings;
	}

	private AgentSettings generateAgentProfile(OrganizationUnitSettings companySettings, OrganizationUnitSettings regionSettings,
			OrganizationUnitSettings branchSettings, AgentSettings agentSettings) throws InvalidInputException {
		if (companySettings == null || agentSettings == null) {
			throw new InvalidInputException("No Settings found");
		}

		// Company Lock settings
		LockSettings agentLock = new LockSettings();
		updateSettings(companySettings, agentSettings, agentLock);

		// Region Lock settings
		if (regionSettings != null) {
			updateSettings(regionSettings, agentSettings, agentLock);
		}

		// Branch Lock settings
		if (branchSettings != null) {
			updateSettings(branchSettings, agentSettings, agentLock);
		}

		agentSettings.setLockSettings(agentLock);
		return agentSettings;
	}

	private void updateSettings(OrganizationUnitSettings higherSettings, OrganizationUnitSettings lowerSettings, LockSettings finalLock) {
		LockSettings lock = higherSettings.getLockSettings();
		if (lock != null) {
			if (lock.getIsLogoLocked() && !finalLock.getIsLogoLocked() && higherSettings.getLogo() != null) {
				lowerSettings.setLogo(higherSettings.getLogo());
				finalLock.setLogoLocked(true);
			}
			if (lock.getIsDisplayNameLocked() && !finalLock.getIsDisplayNameLocked() && higherSettings.getContact_details().getName() != null) {
				lowerSettings.getContact_details().setName(higherSettings.getContact_details().getName());
				finalLock.setDisplayNameLocked(true);
			}
			if (lock.getIsWebAddressLocked() && !finalLock.getIsWebAddressLocked() && lowerSettings.getContact_details().getWeb_addresses() != null) {
				lowerSettings.getContact_details().getWeb_addresses().setWork(higherSettings.getContact_details().getWeb_addresses().getWork());
				finalLock.setLogoLocked(true);
			}
			if (lock.getIsWorkPhoneLocked() && !finalLock.getIsWorkPhoneLocked() && lowerSettings.getContact_details().getContact_numbers() != null) {
				lowerSettings.getContact_details().getContact_numbers().setWork(higherSettings.getContact_details().getContact_numbers().getWork());
				finalLock.setWorkPhoneLocked(true);
			}
			if (lock.getIsPersonalPhoneLocked() && !finalLock.getIsPersonalPhoneLocked() && lowerSettings.getContact_details().getContact_numbers() != null) {
				lowerSettings.getContact_details().getContact_numbers().setPersonal(higherSettings.getContact_details().getContact_numbers().getPersonal());
				finalLock.setPersonalPhoneLocked(true);
			}
			if (lock.getIsFaxPhoneLocked() && !finalLock.getIsFaxPhoneLocked() && lowerSettings.getContact_details().getContact_numbers() != null) {
				lowerSettings.getContact_details().getContact_numbers().setFax(higherSettings.getContact_details().getContact_numbers().getFax());
				finalLock.setFaxPhoneLocked(true);
			}
			if (lock.getIsAboutMeLocked() && !finalLock.getIsAboutMeLocked() && higherSettings.getContact_details().getAbout_me() != null) {
				lowerSettings.getContact_details().setAbout_me(higherSettings.getContact_details().getAbout_me());
				finalLock.setAboutMeLocked(true);
			}
		}
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
	public LockSettings updateLockSettings(String collection, OrganizationUnitSettings unitSettings,
			LockSettings lockSettings) throws InvalidInputException {
		if (lockSettings == null) {
			throw new InvalidInputException("LockSettings passed can not be null");
		}
		LOG.info("Updating lock detail information");
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_LOCK_SETTINGS,
				lockSettings, unitSettings, collection);
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
			Branch defaultBranch = organizationManagementService.getDefaultBranchForRegion(regionSettings.getIden());

			Map<String, Object> queries = new HashMap<String, Object>();
			queries.put(CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE);
			queries.put(CommonConstants.REGION_ID_COLUMN, regionSettings.getIden());
			queries.put(CommonConstants.BRANCH_ID_COLUMN, defaultBranch.getBranchId());

			LOG.debug("calling method to fetch user profiles under region :" + regionProfileName);
			List<UserProfile> userProfiles = userProfileDao.findByKeyValue(UserProfile.class, queries);

			if (userProfiles != null && !userProfiles.isEmpty()) {
				LOG.debug("Obtained userProfiles with size : " + userProfiles.size());
				users = new ArrayList<AgentSettings>();
				for (UserProfile userProfile : userProfiles) {
					users.add(organizationUnitSettingsDao.fetchAgentSettingsById(userProfile.getUser().getUserId()));
				}
			}
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
		regionSettings = generateRegionProfile(companySettings, regionSettings);
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

		branchSettings = generateBranchProfile(companySettings, regionSettings, branchSettings);

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
			UserProfile userProfile = userProfiles.get(0);

		}
		// TODO fetch final agent settings based on the locks

		LOG.info("Method getIndividualByProfileName executed successfully");
		return agentSettings;
	}

	/**
	 * Method to get aggregated reviews of all agents of a company
	 */
	@Override
	public List<SurveyDetails> getReviewsForCompany(long companyId) throws InvalidInputException {
		LOG.info("Method getReviewsForCompany called for companyId:" + companyId);
		List<SurveyDetails> surveyDetails = surveyDetailsDao.getAllFeedbacks(CommonConstants.COMPANY_ID_COLUMN, companyId);
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
		List<UserProfile> userProfiles = userProfileDao.findByKeyValue(UserProfile.class, queries);
		if (userProfiles != null && !userProfiles.isEmpty()) {
			users = new ArrayList<AgentSettings>();
			for (UserProfile userProfile : userProfiles) {
				users.add(organizationUnitSettingsDao.fetchAgentSettingsById(userProfile.getUser().getUserId()));
			}
			LOG.debug("Returning :" + users.size() + " individuals for branch : " + branchId);
		}
		LOG.info("Method getIndividualsByBranchId executed successfully");
		return null;
	}

}