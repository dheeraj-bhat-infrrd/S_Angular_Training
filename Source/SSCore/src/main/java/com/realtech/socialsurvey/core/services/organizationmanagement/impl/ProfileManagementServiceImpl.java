package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.LockSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;

@DependsOn("generic")
@Component
public class ProfileManagementServiceImpl implements ProfileManagementService, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(ProfileManagementServiceImpl.class);

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("afterPropertiesSet called for profile management service");
	}

	@Override
	public OrganizationUnitSettings finalizeProfileDetail(User user, AccountType accountType, UserSettings settings) throws InvalidInputException {
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

		List<UserProfile> userProfiles = user.getUserProfiles();
		OrganizationUnitSettings finalSettings = null;
		long agentId;
		long branchId;
		long regionId;
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
					agentId = userProfiles.get(0).getAgentId();
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
					branchId = userProfiles.get(0).getBranchId();
					finalSettings = generateBranchProfile(settings.getCompanySettings(), null, settings.getBranchSettings().get(branchId));
				}

				// Individual
				else if (user.isAgent()) {
					agentId = userProfiles.get(0).getAgentId();
					branchId = userProfiles.get(0).getBranchId();
					finalSettings = generateAgentProfile(settings.getCompanySettings(), null, settings.getBranchSettings().get(branchId), settings
							.getAgentSettings().get(agentId));
				}
				break;

			case ENTERPRISE:
				LOG.info("Company account type");
				// Company Admin
				if (user.isCompanyAdmin()) {
					finalSettings = settings.getCompanySettings();
				}

				// Region Admin
				else if (user.isRegionAdmin()) {
					regionId = userProfiles.get(0).getRegionId();
					finalSettings = generateRegionProfile(settings.getCompanySettings(), settings.getRegionSettings().get(regionId));
				}

				// Branch Admin
				else if (user.isBranchAdmin()) {
					branchId = userProfiles.get(0).getBranchId();
					regionId = userProfiles.get(0).getRegionId();
					finalSettings = generateBranchProfile(settings.getCompanySettings(), settings.getRegionSettings().get(regionId), settings
							.getBranchSettings().get(branchId));
				}

				// Individual
				else if (user.isAgent()) {
					agentId = userProfiles.get(0).getAgentId();
					branchId = userProfiles.get(0).getBranchId();
					regionId = userProfiles.get(0).getRegionId();
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

	private void updateSettings(OrganizationUnitSettings settings, OrganizationUnitSettings agentSettings, LockSettings agentLock) {
		LockSettings lock = settings.getLockSettings();
		if (lock != null) {
			if (lock.isLogoLocked() && !agentLock.isLogoLocked()) {
				agentSettings.setLogo(settings.getLogo());
				agentLock.setLogoLocked(true);
			}
			if (lock.isLocationLocked() && !agentLock.isLocationLocked()) {
				agentSettings.setLocationEnabled(settings.getIsLocationEnabled());
				agentLock.setLocationLocked(true);
			}
			if (lock.isVerticalLocked() && !agentLock.isVerticalLocked()) {
				agentSettings.setVertical(settings.getVertical());
				agentLock.setVerticalLocked(true);
			}
			if (lock.isCRMInfoLocked() && !agentLock.isCRMInfoLocked()) {
				agentSettings.setCrm_info(settings.getCrm_info());
				agentLock.setCRMInfoLocked(true);
			}
			if (lock.isMailContentLocked() && !agentLock.isMailContentLocked()) {
				agentSettings.setMail_content(settings.getMail_content());
				agentLock.setMailContentLocked(true);
			}
			if (lock.isLicensesLocked() && !agentLock.isLicensesLocked()) {
				agentSettings.setLicenses(settings.getLicenses());
				agentLock.setLicensesLocked(true);
			}
			if (lock.isAssociationsLocked() && !agentLock.isAssociationsLocked()) {
				agentSettings.setAssociations(settings.getAssociations());
				agentLock.setAssociationsLocked(true);
			}
			if (lock.isAcheivementsLocked() && !agentLock.isAcheivementsLocked()) {
				agentSettings.setAchievements(settings.getAchievements());
				agentLock.setAcheivementsLocked(true);
			}
			if (lock.isSocialTokensLocked() && !agentLock.isSocialTokensLocked()) {
				agentSettings.setSocialMediaTokens(settings.getSocialMediaTokens());
				agentLock.setSocialTokensLocked(true);
			}
			if (lock.isSurveySettingsLocked() && !agentLock.isSurveySettingsLocked()) {
				agentSettings.setSurvey_settings(settings.getSurvey_settings());
				agentLock.setSurveySettingsLocked(true);
			}
		}
	}
}