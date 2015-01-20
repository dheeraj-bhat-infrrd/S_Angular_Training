package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.sql.Timestamp;
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
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.BranchSettings;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
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

	@Autowired
	private GenericDao<UserProfile, Long> userProfileDao;

	@Autowired
	private OrganizationUnitSettingsDao organizationUnitSettingsDao;

	@Autowired
	private OrganizationManagementService organizationManagementService;

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
		UserProfile userProfile = createUserProfile(user, assigneeUser.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID, branchId,
				CommonConstants.DEFAULT_REGION_ID, CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID, CommonConstants.DASHBOARD_STAGE,
				CommonConstants.STATUS_ACTIVE, String.valueOf(assigneeUser.getUserId()), String.valueOf(assigneeUser.getUserId()));
		userProfileDao.save(userProfile);

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
		UserProfile userProfile = createUserProfile(user, assigneeUser.getCompany(), user.getEmailId(), CommonConstants.DEFAULT_AGENT_ID,
				CommonConstants.DEFAULT_BRANCH_ID, regionId, CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID,
				CommonConstants.DASHBOARD_STAGE, CommonConstants.STATUS_ACTIVE, String.valueOf(assigneeUser.getUserId()),
				String.valueOf(assigneeUser.getUserId()));
		userProfileDao.save(userProfile);

		LOG.info("Method to createRegionAdmin finished for regionId : " + regionId + " and userId : " + userId);

		return user;
	}

	private UserProfile createUserProfile(User user, Company company, String emailId, long agentId, long branchId, long regionId,
			int profileMasterId, String profileCompletionStage, int isProfileComplete, String createdBy, String modifiedBy) {
		LOG.debug("Method createUserProfile called for username : " + user.getLoginName());
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

	/**
	 * Method to update a user's status
	 */
	@Override
	@Transactional
	public void updateUserStatus(long userId, int status) throws InvalidInputException {
		LOG.info("Method updateUserStatus of user management services called for userId : " + userId + " and status :" + status);
		User user = userDao.findById(User.class, userId);
		if (user == null) {
			throw new InvalidInputException("No user present for the specified userId");
		}
		user.setStatus(status);
		user.setModifiedBy(String.valueOf(userId));
		user.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		userDao.update(user);

		LOG.info("Successfully completed method to update user status");
	}

	@Override
	public UserSettings getCanonicalUserSettings(User user, AccountType accountType) throws InvalidInputException, NoRecordsFetchedException {
		if (user == null) {
			throw new InvalidInputException("User is not set.");
		}
		if (accountType == null) {
			throw new InvalidInputException("Invalid account type.");
		}
		UserSettings canonicalUserSettings = new UserSettings();
		Map<Long, AgentSettings> agentSettings = null;
		Map<Long, OrganizationUnitSettings> branchesSettings = null;
		Map<Long, OrganizationUnitSettings> regionsSettings = null;
		LOG.info("Getting the canonical settings for the user: " + user.toString());
		// get the settings according to the profile and account type
		LOG.info("Getting the company settings for the user");
		OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings(user);
		canonicalUserSettings.setCompanySettings(companySettings);

		switch (accountType) {
			case INDIVIDUAL:
			case TEAM:
				LOG.debug("Individual/ Team account type");
				// get the agent profile as well
				LOG.debug("Gettings agent settings");
				agentSettings = getAgentSettingsForUserProfiles(user.getUserProfiles());
				canonicalUserSettings.setAgentSettings(agentSettings);
				break;
			case COMPANY:
				LOG.debug("Company account type");
				// get the agent settings. If the user is not an agent then there would agent
				// settings would be null
				LOG.debug("Gettings agent settings");
				agentSettings = getAgentSettingsForUserProfiles(user.getUserProfiles());
				canonicalUserSettings.setAgentSettings(agentSettings);
				// get the branches profiles and then resolve the parent organization unit.
				LOG.debug("Gettings branch settings for user profiles");
				branchesSettings = getBranchesSettingsForUserProfile(user.getUserProfiles(), agentSettings);
				canonicalUserSettings.setBranchSettings(branchesSettings);
				break;
			case ENTERPRISE:
				LOG.debug("Company account type");
				// get the agent settings. If the user is not an agent then there would agent
				// settings would be null
				LOG.debug("Gettings agent settings");
				agentSettings = getAgentSettingsForUserProfiles(user.getUserProfiles());
				canonicalUserSettings.setAgentSettings(agentSettings);
				// get the branches profiles and then resolve the parent organization unit.
				LOG.debug("Gettings branch settings for user profiles");
				branchesSettings = getBranchesSettingsForUserProfile(user.getUserProfiles(), agentSettings);
				canonicalUserSettings.setBranchSettings(branchesSettings);
				// get the regions profiles and then resolve the parent organization unit.
				LOG.debug("Gettings region settings for user profiles");
				regionsSettings = getRegionSettingsForUserProfile(user.getUserProfiles(), branchesSettings);
				canonicalUserSettings.setRegionSettings(regionsSettings);
				break;
			default:
				throw new InvalidInputException("Account type is invalid in isMaxBranchAdditionExceeded");
		}
		return canonicalUserSettings;
	}

	private Map<Long, OrganizationUnitSettings> getRegionSettingsForUserProfile(List<UserProfile> userProfiles,
			Map<Long, OrganizationUnitSettings> branchesSettings) throws InvalidInputException {
		LOG.debug("Getting regions settings for the user profile list");
		Map<Long, OrganizationUnitSettings> regionsSettings = organizationManagementService.getRegionSettingsForUserProfiles(userProfiles);
		// if branches settings is not null, the resolve the settings of region associated with the
		// user's branch profiles
		if (branchesSettings != null && branchesSettings.size() > 0) {
			LOG.debug("Resolving regions settings for branch profiles");
			for (UserProfile userProfile : userProfiles) {
				if (userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID) {
					// get the branch profile if it is not present in the branch settings
					if (userProfile.getRegionId() > 0l) {
						if (regionsSettings == null) {
							// there were no branch profiles associated with the profile.
							LOG.debug("No regions associated with the profile");
							regionsSettings = new HashMap<Long, OrganizationUnitSettings>();
						}
						if (!regionsSettings.containsKey(userProfile.getRegionId())) {
							OrganizationUnitSettings regionSetting = organizationManagementService.getRegionSettings(userProfile.getBranchId());
							regionsSettings.put(userProfile.getRegionId(), regionSetting);
						}
					}
				}
			}
		}
		return regionsSettings;
	}

	private Map<Long, OrganizationUnitSettings> getBranchesSettingsForUserProfile(List<UserProfile> userProfiles,
			Map<Long, AgentSettings> agentSettings) throws InvalidInputException, NoRecordsFetchedException {
		LOG.debug("Getting branches settings for the user profile list");
		Map<Long, OrganizationUnitSettings> branchesSettings = organizationManagementService.getBranchSettingsForUserProfiles(userProfiles);
		// if agent settings is not null, the resolve the settings of branch associated with the
		// user's agent profiles
		if (agentSettings != null && agentSettings.size() > 0) {
			LOG.debug("Resolving branches settings for agent profiles");
			for (UserProfile userProfile : userProfiles) {
				if (userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
					// get the branch profile if it is not present in the branch settings
					if (userProfile.getBranchId() > 0l) {
						if (branchesSettings == null) {
							// there were no branch profiles associated with the profile.
							LOG.debug("No branches associated with the profile");
							branchesSettings = new HashMap<Long, OrganizationUnitSettings>();
						}
						if (!branchesSettings.containsKey(userProfile.getBranchId())) {
							BranchSettings branchSetting = organizationManagementService.getBranchSettings(userProfile.getBranchId());
							branchesSettings.put(userProfile.getBranchId(), branchSetting.getOrganizationUnitSettings());
						}
					}
				}
			}
		}
		return branchesSettings;
	}

	@Override
	public AgentSettings getUserSettings(long agentId) throws InvalidInputException {
		LOG.info("Getting agent settings for agent id: " + agentId);
		if (agentId <= 0l) {
			throw new InvalidInputException("Invalid agent id for fetching user settings");
		}
		AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById(agentId);
		return agentSettings;
	}

	@Override
	public Map<Long, AgentSettings> getAgentSettingsForUserProfiles(List<UserProfile> userProfiles) throws InvalidInputException {
		Map<Long, AgentSettings> agentSettings = null;
		if (userProfiles != null && userProfiles.size() > 0) {
			LOG.info("Get agent settings for the user profiles: " + userProfiles.toString());
			agentSettings = new HashMap<Long, AgentSettings>();
			AgentSettings agentSetting = null;
			// get the agent profiles and get the settings for each of them.
			for (UserProfile userProfile : userProfiles) {
				agentSetting = new AgentSettings();
				if (userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID) {
					LOG.debug("Getting settings for " + userProfile);
					// get the agent id and get the profile
					if (userProfile.getAgentId() > 0l) {
						agentSetting = getUserSettings(userProfile.getAgentId());
						if (agentSetting != null) {
							agentSettings.put(userProfile.getAgentId(), agentSetting);
						}
					}
					else {
						LOG.warn("Not a valid agent id for user profile: " + userProfile + ". Skipping the record");
					}
				}
			}
		}
		else {
			throw new InvalidInputException("User profiles are not set");
		}

		return agentSettings;
	}

}
