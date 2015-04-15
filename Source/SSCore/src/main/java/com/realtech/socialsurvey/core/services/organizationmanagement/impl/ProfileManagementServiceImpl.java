package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SocialPostDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.Achievement;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Association;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyPositions;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.Licenses;
import com.realtech.socialsurvey.core.entities.LinkedInProfileData;
import com.realtech.socialsurvey.core.entities.LockSettings;
import com.realtech.socialsurvey.core.entities.MailIdSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.entities.WebAddressSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

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
	private SocialPostDao socialPostDao;

	@Autowired
	private Utils utils;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private SolrSearchService solrSearchService;

	@Autowired
	private EmailServices emailServices;

	@Autowired
	private URLGenerator urlGenerator;

	@Value("${APPLICATION_BASE_URL}")
	private String applicationBaseUrl;

	@Value("${ENABLE_KAFKA}")
	private String enableKafka;

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("afterPropertiesSet called for profile management service");
	}

	@Override
	public LockSettings aggregateParentLockSettings(User user, AccountType accountType, UserSettings settings, long branchId, long regionId,
			int profilesMaster) throws InvalidInputException {
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
		if (profilesMaster == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID) {
			LOG.debug("Setting default LockSettings for Company Admin");
			parentLockSettings = new LockSettings();
		}

		// If user is not Company Admin, Lock settings need to be aggregated
		else {
			switch (accountType) {
				case FREE:
				case INDIVIDUAL:
				case TEAM:
					// Individual
					if (profilesMaster == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
						LOG.debug("Setting company LockSettings for Agent of Individual/Team account type");
						parentLockSettings = settings.getCompanySettings().getLockSettings();
					}
					break;

				case COMPANY:
					// Branch Admin
					if (profilesMaster == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID) {
						LOG.debug("Setting company LockSettings for Branch Admin of Company account type");
						parentLockSettings = settings.getCompanySettings().getLockSettings();
					}

					// Individual
					else if (profilesMaster == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
						LOG.debug("Aggregating LockSettings till Branch for Agent of Company account type");
						parentLockSettings = lockSettingsTillBranch(settings.getCompanySettings(), null, settings.getBranchSettings().get(branchId));
					}
					break;

				case ENTERPRISE:
					// Region Admin
					if (profilesMaster == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID) {
						LOG.debug("Setting company LockSettings for Region Admin of Enterprise account type");
						parentLockSettings = settings.getCompanySettings().getLockSettings();
					}

					// Branch Admin
					else if (profilesMaster == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID) {
						LOG.debug("Aggregating LockSettings till Region for Branch Admin of Enterprise account type");
						parentLockSettings = lockSettingsTillRegion(settings.getCompanySettings(), settings.getRegionSettings().get(regionId));
					}

					// Individual
					else if (profilesMaster == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
						LOG.debug("Aggregating LockSettings till Branch for Agent of Enterprise account type");
						parentLockSettings = lockSettingsTillBranch(settings.getCompanySettings(), settings.getRegionSettings().get(regionId),
								settings.getBranchSettings().get(branchId));
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
		LockSettings parentLock = new LockSettings();
		parentLock = aggregateLockSettings(companySettings.getLockSettings(), parentLock);

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
		LockSettings parentLock = new LockSettings();
		parentLock = aggregateLockSettings(companySettings.getLockSettings(), parentLock);

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
			if (higherLock.getIsBlogAddressLocked()) {
				parentLock.setBlogAddressLocked(true);
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
	public OrganizationUnitSettings aggregateUserProfile(User user, AccountType accountType, UserSettings settings, long branchId, long regionId,
			int profilesMaster) throws InvalidInputException {
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
		if (profilesMaster == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID) {
			LOG.debug("Setting Company Profile for Company Admin");
			userProfile = settings.getCompanySettings();
		}

		// If user is not Company Admin, Profile need to be aggregated
		else {
			switch (accountType) {
				case FREE:
				case INDIVIDUAL:
				case TEAM:
					// Individual
					if (profilesMaster == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
						LOG.debug("Aggregate Profile for Agent of Individual/Team account type");
						userProfile = aggregateAgentProfile(settings.getCompanySettings(), null, null, settings.getAgentSettings());
					}
					break;

				case COMPANY:
					LOG.info("Company account type");
					// Branch Admin
					if (profilesMaster == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID) {
						LOG.debug("Aggregate Profile for BranchAdmin of Company account type");
						userProfile = aggregateBranchProfile(settings.getCompanySettings(), null, settings.getBranchSettings().get(branchId));
					}

					// Individual
					else if (profilesMaster == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
						LOG.debug("Aggregate Profile for Agent of Company account type");
						userProfile = aggregateAgentProfile(settings.getCompanySettings(), null, settings.getBranchSettings().get(branchId),
								settings.getAgentSettings());
					}
					break;

				case ENTERPRISE:
					LOG.info("Enterprise account type");
					// Region Admin
					if (profilesMaster == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID) {
						LOG.debug("Aggregate Profile for RegionAdmin of Enterprise account type");
						userProfile = aggregateRegionProfile(settings.getCompanySettings(), settings.getRegionSettings().get(regionId));
					}

					// Branch Admin
					else if (profilesMaster == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID) {
						LOG.debug("Aggregate Profile for BranchAdmin of Enterprise account type");
						userProfile = aggregateBranchProfile(settings.getCompanySettings(), settings.getRegionSettings().get(regionId), settings
								.getBranchSettings().get(branchId));
					}

					// Individual
					else if (profilesMaster == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
						LOG.debug("Aggregate Profile for Agent of Enterprise account type");
						userProfile = aggregateAgentProfile(settings.getCompanySettings(), settings.getRegionSettings().get(regionId), settings
								.getBranchSettings().get(branchId), settings.getAgentSettings());
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

		if (userProfile.getContact_details() == null) {
			userProfile.setContact_details(new ContactDetailsSettings());
		}
		if (userProfile.getContact_details().getWeb_addresses() == null) {
			userProfile.getContact_details().setWeb_addresses(new WebAddressSettings());
		}
		if (userProfile.getContact_details().getContact_numbers() == null) {
			userProfile.getContact_details().setContact_numbers(new ContactNumberSettings());
		}
		if (userProfile.getSurvey_settings() == null) {
			userProfile.setSurvey_settings(parentProfile.getSurvey_settings());
		}

		// Aggregate parentProfile data with userProfile
		LockSettings parentLock = parentProfile.getLockSettings();
		if (parentLock != null) {
			// Logo
			if (parentProfile.getLogo() != null) {
				if (parentLock.getIsLogoLocked() && !userLock.getIsLogoLocked()) {
					userProfile.setLogo(parentProfile.getLogo());
					userLock.setLogoLocked(true);
				}
				if (!parentLock.getIsLogoLocked() && !userLock.getIsLogoLocked()) {
					if (userProfile.getLogo() == null || userProfile.getLogo().equals("")) {
						userProfile.setLogo(parentProfile.getLogo());
					}
				}
			}

			// Basic Contact details
			if (parentProfile.getContact_details() != null) {
				if (parentLock.getIsDisplayNameLocked() && !userLock.getIsDisplayNameLocked() && parentProfile.getContact_details().getName() != null) {
					userProfile.getContact_details().setName(parentProfile.getContact_details().getName());
					userLock.setDisplayNameLocked(true);
				}
				if (parentLock.getIsAboutMeLocked() && !userLock.getIsAboutMeLocked() && parentProfile.getContact_details().getAbout_me() != null) {
					userProfile.getContact_details().setAbout_me(parentProfile.getContact_details().getAbout_me());
					userLock.setAboutMeLocked(true);
				}
			}

			// Web addresses
			if (parentProfile.getContact_details().getWeb_addresses() != null) {
				if (parentLock.getIsWebAddressLocked() && !userLock.getIsWebAddressLocked()
						&& userProfile.getContact_details().getWeb_addresses() != null) {
					userProfile.getContact_details().getWeb_addresses().setWork(parentProfile.getContact_details().getWeb_addresses().getWork());
					userLock.setWebAddressLocked(true);
				}
				if (parentLock.getIsBlogAddressLocked() && !userLock.getIsBlogAddressLocked()
						&& userProfile.getContact_details().getWeb_addresses() != null) {
					userProfile.getContact_details().getWeb_addresses().setBlogs(parentProfile.getContact_details().getWeb_addresses().getBlogs());
					userLock.setBlogAddressLocked(true);
				}
			}
			
			// Phone numbers
			if (parentProfile.getContact_details().getContact_numbers() != null) {
				if (parentLock.getIsWorkPhoneLocked() && !userLock.getIsWorkPhoneLocked()
						&& userProfile.getContact_details().getContact_numbers() != null) {
					userProfile.getContact_details().getContact_numbers().setWork(parentProfile.getContact_details().getContact_numbers().getWork());
					userLock.setWorkPhoneLocked(true);
				}
				if (parentLock.getIsPersonalPhoneLocked() && !userLock.getIsPersonalPhoneLocked()
						&& userProfile.getContact_details().getContact_numbers() != null) {
					userProfile.getContact_details().getContact_numbers()
							.setPersonal(parentProfile.getContact_details().getContact_numbers().getPersonal());
					userLock.setPersonalPhoneLocked(true);
				}
				if (parentLock.getIsFaxPhoneLocked() && !userLock.getIsFaxPhoneLocked()
						&& userProfile.getContact_details().getContact_numbers() != null) {
					userProfile.getContact_details().getContact_numbers().setFax(parentProfile.getContact_details().getContact_numbers().getFax());
					userLock.setFaxPhoneLocked(true);
				}
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
		if (associations == null) {
			throw new InvalidInputException("Association name passed can not be null");
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
		if (associations == null) {
			throw new InvalidInputException("Association name passed can not be null");
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
		// Update the seo content flag to true
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_SEO_CONTENT_MODIFIED, true, unitSettings, collection);
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
		// Update the seo content flag to true
		organizationUnitSettingsDao.updateParticularKeyAgentSettings(MongoOrganizationUnitSettingDaoImpl.KEY_SEO_CONTENT_MODIFIED, true, agentSettings);
		LOG.info("Contact details updated successfully");
		return contactDetailsSettings;
	}

	// Achievements
	@Override
	public List<Achievement> addAchievements(String collection, OrganizationUnitSettings unitSettings, List<Achievement> achievements)
			throws InvalidInputException {
		if (achievements == null) {
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
		if (achievements == null) {
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
	@Transactional
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
	 * 
	 * @throws NoRecordsFetchedException
	 */
	@Override
	@Transactional
	public OrganizationUnitSettings getIndividualByProfileName(String agentProfileName) throws InvalidInputException, NoRecordsFetchedException {
		LOG.info("Method getIndividualByProfileName called for agentProfileName:" + agentProfileName);

		OrganizationUnitSettings agentSettings = null;
		if (agentProfileName == null || agentProfileName.isEmpty()) {
			throw new InvalidInputException("agentProfileName is null or empty while getting agent settings");
		}

		agentSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName(agentProfileName,
				MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION);
		if (agentSettings == null) {
			throw new NoRecordsFetchedException("No settings found for agent while fetching agent profile");
		}

		User user = userDao.findById(User.class, agentSettings.getIden());

		LOG.debug("Fetching user profiles for agentId: " + agentSettings.getIden());
		List<UserProfile> userProfiles = user.getUserProfiles();
		UserProfile userProfile = null;
		if (userProfiles != null && !userProfiles.isEmpty()) {
			userProfile = userProfiles.get(0);
		}
		else {
			throw new NoRecordsFetchedException("User profiles not found while fetching agent profile");
		}

		long companyId = userProfile.getCompany().getCompanyId();
		LOG.debug("Fetching company settings for companyId: " + companyId);
		OrganizationUnitSettings companySettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(companyId,
				MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);

		long regionId = userProfile.getRegionId();
		OrganizationUnitSettings regionSettings = null;
		if (regionId > 0l) {
			LOG.debug("Fetching region settings for regionId: " + regionId);
			regionSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(regionId,
					MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION);
		}

		long branchId = userProfile.getBranchId();
		OrganizationUnitSettings branchSettings = null;
		if (branchId > 0l) {
			LOG.debug("Fetching branch settings for regionId: " + branchId);
			branchSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(branchId,
					MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION);
		}

		agentSettings = aggregateAgentProfile(companySettings, regionSettings, branchSettings, agentSettings);
		LOG.info("Method getIndividualByProfileName executed successfully");
		return agentSettings;
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
	public long getReviewsCountForCompany(long companyId, double minScore, double maxScore, boolean fetchAbusive) {
		LOG.info("Method getReviewsCountForCompany called for companyId:" + companyId + " minscore:" + minScore + " maxscore:" + maxScore);
		long reviewsCount = 0;
		reviewsCount = surveyDetailsDao.getFeedBacksCount(CommonConstants.COMPANY_ID_COLUMN, companyId, minScore, maxScore, fetchAbusive);
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

	/**
	 * Method to fetch reviews based on the profile level specified, iden is one of
	 * agentId/branchId/regionId or companyId based on the profile level
	 */
	@Override
	public List<SurveyDetails> getReviews(long iden, double startScore, double limitScore, int startIndex, int numOfRows, String profileLevel,
			boolean fetchAbusive) throws InvalidInputException {
		LOG.info("Method getReviews called for iden:" + iden + " startScore:" + startScore + " limitScore:" + limitScore + " startIndex:"
				+ startIndex + " numOfRows:" + numOfRows + " profileLevel:" + profileLevel);
		List<SurveyDetails> surveyDetails = null;
		if (iden <= 0l) {
			throw new InvalidInputException("iden is invalid while fetching reviews");
		}
		String idenColumnName = getIdenColumnNameFromProfileLevel(profileLevel);
		surveyDetails = surveyDetailsDao.getFeedbacks(idenColumnName, iden, startIndex, numOfRows, startScore, limitScore, fetchAbusive);
		return surveyDetails;
	}

	/**
	 * Method to get average ratings based on the profile level specified, iden is one of
	 * agentId/branchId/regionId or companyId based on the profile level
	 */
	@Override
	public double getAverageRatings(long iden, String profileLevel, boolean aggregateAbusive) throws InvalidInputException {
		LOG.info("Method getAverageRatings called for iden :" + iden + " profilelevel:" + profileLevel);
		if (iden <= 0l) {
			throw new InvalidInputException("iden is invalid for getting average rating os a company");
		}
		String idenColumnName = getIdenColumnNameFromProfileLevel(profileLevel);
		double averageRating = surveyDetailsDao.getRatingForPastNdays(idenColumnName, iden, -1, aggregateAbusive);

		LOG.info("Method getAverageRatings executed successfully.Returning: " + averageRating);
		return averageRating;
	}

	/**
	 * Method to get iden column name from profile level
	 * 
	 * @param profileLevel
	 * @return
	 * @throws InvalidInputException
	 */
	private String getIdenColumnNameFromProfileLevel(String profileLevel) throws InvalidInputException {
		LOG.debug("Getting iden column name for profile level:" + profileLevel);
		String idenColumnName = null;
		if (profileLevel == null || profileLevel.isEmpty()) {
			throw new InvalidInputException("profile level is null or empty while getting iden column name");
		}
		switch (profileLevel) {
			case CommonConstants.PROFILE_LEVEL_COMPANY:
				idenColumnName = CommonConstants.COMPANY_ID_COLUMN;
				break;
			case CommonConstants.PROFILE_LEVEL_REGION:
				idenColumnName = CommonConstants.REGION_ID_COLUMN;
				break;
			case CommonConstants.PROFILE_LEVEL_BRANCH:
				idenColumnName = CommonConstants.BRANCH_ID_COLUMN;
				break;
			case CommonConstants.PROFILE_LEVEL_INDIVIDUAL:
				idenColumnName = CommonConstants.AGENT_ID_COLUMN;
				break;
			default:
				throw new InvalidInputException("Invalid profile level while getting iden column name");
		}
		LOG.debug("Returning column name:" + idenColumnName + " for profile level:" + profileLevel);
		return idenColumnName;
	}

	/**
	 * Method to get reviews count based on the profile level specified, iden is one of
	 * agentId/branchId/regionId or companyId based on the profile level within limit of rating
	 * score specified
	 */
	@Override
	public long getReviewsCount(long iden, double minScore, double maxScore, String profileLevel, boolean fetchAbusive) throws InvalidInputException {
		LOG.info("Method getReviewsCount called for iden:" + iden + " minscore:" + minScore + " maxscore:" + maxScore + " profilelevel:"
				+ profileLevel);
		if (iden <= 0l) {
			throw new InvalidInputException("Iden is invalid for getting reviews count");
		}
		long reviewsCount = 0;
		String idenColumnName = getIdenColumnNameFromProfileLevel(profileLevel);
		reviewsCount = surveyDetailsDao.getFeedBacksCount(idenColumnName, iden, minScore, maxScore, fetchAbusive);

		LOG.info("Method getReviewsCount executed successfully. Returning reviewsCount:" + reviewsCount);
		return reviewsCount;
	}

	/**
	 * Method to get the list of individuals for branch/region or company as specified ide in one of
	 * branchId/regionId/companyId
	 * 
	 * @throws SolrException
	 */
	@Override
	public SolrDocumentList getProListByProfileLevel(long iden, String profileLevel, int start, int numOfRows) throws InvalidInputException,
			SolrException {
		LOG.info("Method getProListByProfileLevel called for iden: " + iden + " profileLevel:" + profileLevel + " start:" + start + " numOfRows:"
				+ numOfRows);
		if (iden <= 0l) {
			throw new InvalidInputException("iden is invalid in getProListByProfileLevel");
		}
		if (profileLevel == null || profileLevel.isEmpty()) {
			throw new InvalidInputException("profile level is null in getProListByProfileLevel");
		}
		String idenFieldName = null;
		SolrDocumentList solrSearchResult = null;
		switch (profileLevel) {
			case CommonConstants.PROFILE_LEVEL_COMPANY:
				idenFieldName = CommonConstants.COMPANY_ID_SOLR;
				break;
			case CommonConstants.PROFILE_LEVEL_REGION:
				idenFieldName = CommonConstants.REGIONS_SOLR;
				break;
			case CommonConstants.PROFILE_LEVEL_BRANCH:
				idenFieldName = CommonConstants.BRANCHES_SOLR;
				break;
			default:
				throw new InvalidInputException("profile level is invalid in getProListByProfileLevel");
		}
		solrSearchResult = solrSearchService.searchUsersByIden(iden, idenFieldName, true,start, numOfRows);

		LOG.info("Method getProListByProfileLevel finished successfully");
		return solrSearchResult;
	}

	@Override
	public void generateVerificationUrl(Map<String, String> urlParams, String applicationUrl, String recipientMailId, String recipientName)
			throws InvalidInputException, UndeliveredEmailException {
		String verficationUrl = urlGenerator.generateUrl(urlParams, applicationUrl);
		if (enableKafka.equals(CommonConstants.YES)) {
			emailServices.queueEmailVerificationMail(verficationUrl, recipientMailId, recipientName);
		}
		else {
			emailServices.sendEmailVerificationMail(verficationUrl, recipientMailId, recipientName);
		}
	}

	@Override
	public void updateEmailVerificationStatus(String urlParamsStr) throws InvalidInputException {
		Map<String, String> urlParams = urlGenerator.decryptParameters(urlParamsStr);
		if (urlParams == null || urlParams.isEmpty()) {
			throw new InvalidInputException("Url params are invalid for email verification");
		}

		ContactDetailsSettings contact = null;
		MailIdSettings mail = null;
		String collection = urlParams.get(CommonConstants.USER_PROFILE);
		String emailType = urlParams.get(CommonConstants.EMAIL_TYPE);
		String emailAddress = urlParams.get(CommonConstants.EMAIL_ID);
		long iden = Long.parseLong(urlParams.get(CommonConstants.USER_ID));

		if (!collection.equals(MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION)) {
			OrganizationUnitSettings unitSettings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsById(iden, collection);
			contact = unitSettings.getContact_details();
			mail = contact.getMail_ids();
			if (emailType.equals(CommonConstants.EMAIL_TYPE_WORK) && mail.getWork().equals(emailAddress)) {
				mail.setWorkEmailVerified(true);
			}
			else if (emailType.equals(CommonConstants.EMAIL_TYPE_PERSONAL) && mail.getPersonal().equals(emailAddress)) {
				mail.setPersonalEmailVerified(true);
			}
			contact.setMail_ids(mail);
			organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS,
					contact, unitSettings, collection);
		}
		else {
			AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById(iden);
			contact = agentSettings.getContact_details();
			mail = contact.getMail_ids();
			if (emailType.equals(CommonConstants.EMAIL_TYPE_WORK) && mail.getWork().equals(emailAddress)) {
				mail.setWorkEmailVerified(true);
			}
			else if (emailType.equals(CommonConstants.EMAIL_TYPE_PERSONAL) && mail.getPersonal().equals(emailAddress)) {
				mail.setPersonalEmailVerified(true);
			}
			contact.setMail_ids(mail);
			organizationUnitSettingsDao.updateParticularKeyAgentSettings(MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS, contact,
					agentSettings);
		}
	}

	/**
	 * Method to fetch reviews based on the profile level specified, iden is one of
	 * agentId/branchId/regionId or companyId based on the profile level
	 */
	@Override
	public List<SurveyDetails> getIncompleteSurvey(long iden, double startScore, double limitScore, int startIndex, int numOfRows, String profileLevel)
			throws InvalidInputException {
		LOG.info("Method getIncompleteSurvey() called for iden:" + iden + " startScore:" + startScore + " limitScore:" + limitScore + " startIndex:"
				+ startIndex + " numOfRows:" + numOfRows + " profileLevel:" + profileLevel);
		List<SurveyDetails> surveyDetails = null;
		if (iden <= 0l) {
			throw new InvalidInputException("iden is invalid while fetching incomplete reviews");
		}
		String idenColumnName = getIdenColumnNameFromProfileLevel(profileLevel);
		surveyDetails = surveyDetailsDao.getIncompleteSurvey(idenColumnName, iden, startIndex, numOfRows, startScore, limitScore);
		return surveyDetails;
	}

	/**
	 * Method to fetch all users for the list of branches specified
	 */
	@Override
	public List<AgentSettings> getIndividualsByBranchIds(Set<Long> branchIds) throws InvalidInputException {
		LOG.info("Method getIndividualsByBranchIds called for branchIds:" + branchIds);
		List<AgentSettings> users = null;
		if (branchIds != null && !branchIds.isEmpty()) {
			users = new ArrayList<AgentSettings>();
			for (long branchId : branchIds) {
				List<AgentSettings> tempUsers = getIndividualsByBranchId(branchId);
				if (tempUsers != null && !tempUsers.isEmpty()) {
					users.addAll(tempUsers);
				}
			}
		}
		LOG.info("Method getIndividualsByBranchIds executed successfully");
		return users;
	}

	/**
	 * Method to fetch all users under the specified list of regions
	 */
	@Override
	public List<AgentSettings> getIndividualsByRegionIds(Set<Long> regionIds) throws InvalidInputException, NoRecordsFetchedException {
		LOG.info("Method getIndividualsByBranchIds called for regionIds:" + regionIds);
		List<AgentSettings> users = null;
		if (regionIds != null && !regionIds.isEmpty()) {
			users = new ArrayList<AgentSettings>();
			for (long regionId : regionIds) {
				List<AgentSettings> tempUsers = getIndividualsByRegionId(regionId);
				if (tempUsers != null && !tempUsers.isEmpty()) {
					users.addAll(tempUsers);
				}
			}
		}
		LOG.info("Method getIndividualsByRegionIds executed successfully");
		return users;
	}
	
	/**
	 * Method that mails the contact us message to the respective individual,branch,region,company
	 * @param agentProfileName
	 * @param message
	 * @param senderMailId
	 * @param profileType
	 * @throws InvalidInputException
	 * @throws NoRecordsFetchedException
	 * @throws UndeliveredEmailException
	 */
	@Override
	public void findProfileMailIdAndSendMail(String profileName, String message,
			String senderMailId,String profileType) throws InvalidInputException, NoRecordsFetchedException, UndeliveredEmailException {
		
		if(profileName == null || profileName.isEmpty()){
			LOG.error("contactAgent : profile name parameter is empty or null!");
			throw new InvalidInputException("contactAgent : profile name parameter is empty or null!");
		}
		
		if(message == null || message.isEmpty()){
			LOG.error("contactAgent : message parameter is empty or null!");
			throw new InvalidInputException("contactAgent : message parameter is empty or null!");
		}
		
		if(senderMailId == null || senderMailId.isEmpty()){
			LOG.error("contactAgent : senderMailId parameter is empty or null!");
			throw new InvalidInputException("contactAgent : senderMailId parameter is empty or null!");
		}
		
		if(profileType == null || profileType.isEmpty()){
			LOG.error("contactAgent : profileType parameter is empty or null!");
			throw new InvalidInputException("contactAgent : profileType parameter is empty or null!");
		}
		
		OrganizationUnitSettings settings = null;
		
		if(profileType.equals(CommonConstants.PROFILE_LEVEL_INDIVIDUAL)){
			LOG.debug("Fetching the agent settings from mongo for the agent with profile name : " + profileName);
			settings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName(profileName,
					CommonConstants.AGENT_SETTINGS_COLLECTION);
			LOG.debug("Settings fetched from mongo!");
		}
		else if (profileType.equals(CommonConstants.PROFILE_LEVEL_COMPANY)) {
			LOG.debug("Fetching the company settings from mongo for the company with profile name : " + profileName);
			settings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName(profileName,
					CommonConstants.COMPANY_SETTINGS_COLLECTION);
			LOG.debug("Settings fetched from mongo!");
		}
		else if (profileType.equals(CommonConstants.PROFILE_LEVEL_REGION)) {
			LOG.debug("Fetching the region settings from mongo for the region with profile name : " + profileName);
			settings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName(profileName,
					CommonConstants.REGION_SETTINGS_COLLECTION);
			LOG.debug("Settings fetched from mongo!");
		}
		else if (profileType.equals(CommonConstants.PROFILE_LEVEL_BRANCH)) {
			LOG.debug("Fetching the branch settings from mongo for the branch with profile name : " + profileName);
			settings = organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName(profileName,
					CommonConstants.BRANCH_SETTINGS_COLLECTION);
			LOG.debug("Settings fetched from mongo!");
		}
		else {
			LOG.error("Profile level not known!");
			throw new InvalidInputException("Profile level not known!");
		}

		if (settings != null) {
			LOG.debug("Sending the contact us mail to the agent");
			emailServices.sendContactUsMail(settings.getContact_details().getMail_ids().getWork(), settings.getContact_details().getName(),
					senderMailId, message);
			LOG.debug("Contact us mail sent!");
		}
		else {
			LOG.error("No records found for agent settings of profile name : " + profileName + " in mongo");
			throw new NoRecordsFetchedException("No records found for agent settings of profile name : " + profileName + " in mongo");
		}
	}
		
		
	/*
	 * Method to store status of a user into the mongo.
	 */
	@Override
	public void addPostToUserProfile(long userId, String postText, String postedBy, String source, long time){
		LOG.info("Method to add post to a user's profile started.");
		SocialPost socialPost = new SocialPost();
		socialPost.setUserId(userId);
		socialPost.setPostedBy(postedBy);
		socialPost.setPostText(postText);
		socialPost.setSource("SocialSurvey");
		socialPost.setTimeInMillis(System.currentTimeMillis());
		socialPostDao.addPostToUserProfile(socialPost);
		LOG.info("Method to add post to a user's profile finished.");
	}
	
	/*
	 * Method to fetch social posts for a particular user.
	 */
	@Override
	public List<SocialPost> getPostsForUser(long userId, int startIndex, int batchSize){
		LOG.info("Method to fetch social posts for a particular user, getPostsForUser() started.");
		List<SocialPost> posts = socialPostDao.getPostsByUserId(userId, startIndex, batchSize);
		LOG.info("Method to fetch social posts for a particular user, getPostsForUser() finished.");
		return posts;
	}
	
	/*
	 * Method to fetch social posts for a particular user.
	 */
	@Override
	public long getPostsCountForUser(long userId){
		LOG.info("Method to fetch count of social posts for a particular user, getPostsCountForUser() started.");
		long postsCount = socialPostDao.getPostsCountByUserId(userId);
		LOG.info("Method to fetch count of social posts for a particular user, getPostsCountForUser() finished.");
		return postsCount;
	}

	@Override
	public void updateLinkedInProfileData(String collectionName, OrganizationUnitSettings organizationUnitSettings,
			LinkedInProfileData linkedInProfileData) throws InvalidInputException {
		LOG.info("Updating linked in profile data into "+collectionName);
		if (linkedInProfileData == null) {
			throw new InvalidInputException("LinkedInProfile details passed can not be null");
		}
		LOG.info("Updating linkedin profile detail information");
		organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(MongoOrganizationUnitSettingDaoImpl.KEY_LINKEDIN_PROFILEDATA,
				linkedInProfileData, organizationUnitSettings, collectionName);
		LOG.info("Updated the linkedin profile data.");
		
	}

	@Override
	public void updateAgentExpertise(AgentSettings agentSettings, List<String> expertise) throws InvalidInputException {
		if(expertise == null || expertise.isEmpty()){
			throw new InvalidInputException("Expertise list is not proper");
		}
		LOG.info("Updating agent expertise");
		organizationUnitSettingsDao.updateParticularKeyAgentSettings(MongoOrganizationUnitSettingDaoImpl.KEY_EXPERTISE, expertise, agentSettings);
		LOG.info("Updated agent expertise.");
	}

	@Override
	public void updateAgentCompanyPositions(AgentSettings agentSettings, List<CompanyPositions> companyPositions) throws InvalidInputException {
		if(companyPositions == null || companyPositions.isEmpty()){
			throw new InvalidInputException("Company positions passed are not proper");
		}
		LOG.info("Updating company positions");
		organizationUnitSettingsDao.updateParticularKeyAgentSettings(MongoOrganizationUnitSettingDaoImpl.KEY_COMPANY_POSITIONS, companyPositions, agentSettings);
		LOG.info("Updated company positions.");
	}
}