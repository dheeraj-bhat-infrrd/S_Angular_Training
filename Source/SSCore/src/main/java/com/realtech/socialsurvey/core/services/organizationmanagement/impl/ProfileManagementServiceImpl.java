package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.LockSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.User;
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
					finalSettings = generateAgentProfile(settings.getCompanySettings(), null, null, settings.getAgentSettings().get(user.getUserId()));
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
					// finalSettings = settings.getBranchSettings().get(branchId);
				}

				// Individual
				else if (user.isAgent()) {
					// finalSettings = settings.getAgentSettings().get(user.getUserId());
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
					// finalSettings = settings.getRegionSettings().get(regionId);
				}

				// Branch Admin
				else if (user.isBranchAdmin()) {
					// finalSettings = settings.getBranchSettings().get(branchId);
				}

				// Individual
				else if (user.isAgent()) {
					// finalSettings = settings.getAgentSettings().get(user.getUserId());
				}
				break;

			default:
				throw new InvalidInputException("Account type is invalid in finalizeProfileDetail");
		}

		LOG.info("Method finalizeProfileDetail() finished from ProfileManagementService");
		return finalSettings;
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

	private void updateSettings(OrganizationUnitSettings settings, AgentSettings agentSettings, LockSettings agentLock) {
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