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

	@Override
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
				LOG.info("Company account type");
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
			if (lock.isLogoLocked() && !finalLock.isLogoLocked()) {
				lowerSettings.setLogo(higherSettings.getLogo());
				finalLock.setLogoLocked(true);
			}
			if (lock.isLocationLocked() && !finalLock.isLocationLocked()) {
				lowerSettings.setLocationEnabled(higherSettings.getIsLocationEnabled());
				finalLock.setLocationLocked(true);
			}
			if (lock.isVerticalLocked() && !finalLock.isVerticalLocked()) {
				lowerSettings.setVertical(higherSettings.getVertical());
				finalLock.setVerticalLocked(true);
			}
			if (lock.isCRMInfoLocked() && !finalLock.isCRMInfoLocked()) {
				lowerSettings.setCrm_info(higherSettings.getCrm_info());
				finalLock.setCRMInfoLocked(true);
			}
			if (lock.isMailContentLocked() && !finalLock.isMailContentLocked()) {
				lowerSettings.setMail_content(higherSettings.getMail_content());
				finalLock.setMailContentLocked(true);
			}
			if (lock.isLicensesLocked() && !finalLock.isLicensesLocked()) {
				lowerSettings.setLicenses(higherSettings.getLicenses());
				finalLock.setLicensesLocked(true);
			}
			if (lock.isAssociationsLocked() && !finalLock.isAssociationsLocked()) {
				lowerSettings.setAssociations(higherSettings.getAssociations());
				finalLock.setAssociationsLocked(true);
			}
			if (lock.isAcheivementsLocked() && !finalLock.isAcheivementsLocked()) {
				lowerSettings.setAchievements(higherSettings.getAchievements());
				finalLock.setAcheivementsLocked(true);
			}
			if (lock.isSocialTokensLocked() && !finalLock.isSocialTokensLocked()) {
				lowerSettings.setSocialMediaTokens(higherSettings.getSocialMediaTokens());
				finalLock.setSocialTokensLocked(true);
			}
			if (lock.isSurveySettingsLocked() && !finalLock.isSurveySettingsLocked()) {
				lowerSettings.setSurvey_settings(higherSettings.getSurvey_settings());
				finalLock.setSurveySettingsLocked(true);
			}
		}
	}
}