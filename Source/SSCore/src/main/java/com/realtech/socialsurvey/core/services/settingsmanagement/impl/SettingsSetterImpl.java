package com.realtech.socialsurvey.core.services.settingsmanagement.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SettingsSetterLevel;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsSetter;

@Component
public class SettingsSetterImpl implements SettingsSetter {

	private static final Logger LOG = LoggerFactory.getLogger(SettingsSetterImpl.class);

	@Override
	public Company setSettingsValueForCompany(Company company, SettingsForApplication settings, boolean hasBeenSet) throws NonFatalException {
		// Get the set settings value for the corresponding unit and set the value according to the
		// input sent
		if (company == null || settings == null) {
			throw new InvalidInputException("Invalid input sent for settings update");
		}
		LOG.info("Setting values to company " + company);
		long modifiedSetSettingsValue = getModifiedSetSettingsValue(OrganizationUnit.COMPANY, Long.parseLong(company.getSettingsSetStatus()),
				settings, hasBeenSet);
		company.setSettingsSetStatus(String.valueOf(modifiedSetSettingsValue));
		return company;
	}

	@Override
	public Region setSettingsValueForRegion(Region region, SettingsForApplication settings, boolean hasBeenSet) throws NonFatalException {
		// Get the set settings value for the corresponding unit and set the value according to the
		// input sent
		if (region == null || settings == null) {
			throw new InvalidInputException("Invalid input sent for settings update");
		}
		LOG.info("Setting values to region " + region);
		long modifiedSetSettingsValue = getModifiedSetSettingsValue(OrganizationUnit.REGION, Long.parseLong(region.getSettingsSetStatus()), settings,
				hasBeenSet);
		region.setSettingsSetStatus(String.valueOf(modifiedSetSettingsValue));
		return region;
	}

	@Override
	public Branch setSettingsValueForBranch(Branch branch, SettingsForApplication settings, boolean hasBeenSet) throws NonFatalException {
		// Get the set settings value for the corresponding unit and set the value according to the
		// input sent
		if (branch == null || settings == null) {
			throw new InvalidInputException("Invalid input sent for settings update");
		}
		LOG.info("Setting values to branch " + branch);
		long modifiedSetSettingsValue = getModifiedSetSettingsValue(OrganizationUnit.BRANCH, Long.parseLong(branch.getSettingsSetStatus()), settings,
				hasBeenSet);
		branch.setSettingsSetStatus(String.valueOf(modifiedSetSettingsValue));
		return branch;
	}

	long getModifiedSetSettingsValue(OrganizationUnit organizationUnit, long currentSetValue, SettingsForApplication settings,
			boolean hasBeenSet) {
		LOG.debug("Finding the modified set settings value");
		long valueToBeReturned = currentSetValue;
		long valueToBeAdded = 0l;
		boolean isValueAlreadySet = false; // check if the value can be added or not
		if (organizationUnit == OrganizationUnit.COMPANY) {
			LOG.debug("Setting set for company");
			valueToBeAdded = settings.getOrder();
		}
		else if (organizationUnit == OrganizationUnit.REGION) {
			LOG.debug("Setting set for region");
			valueToBeAdded = 2 * settings.getOrder();
		}
		else if (organizationUnit == OrganizationUnit.BRANCH) {
			LOG.debug("Setting set for branch");
			valueToBeAdded = 4 * settings.getOrder();
		}
		isValueAlreadySet = isSettingsValueSet(organizationUnit, currentSetValue, settings);
		if (hasBeenSet) {
			if (!isValueAlreadySet) {
				valueToBeReturned = currentSetValue + valueToBeAdded;
			}
		}
		else {
			if (isValueAlreadySet) {
				valueToBeReturned = currentSetValue - valueToBeAdded;
			}
		}
		return valueToBeReturned;
	}

	@Override
	public boolean isSettingsValueSet(OrganizationUnit organizationUnit, long currentSetValue, SettingsForApplication settings) {
		LOG.info("Checking if the value is set for the settings");
		boolean isValueSet = false;
		String sCurrentSetValue = String.valueOf(currentSetValue);
		// find index of the settings to be checked for.
		// get the length of the value. Get the index from enum and deduct it from the length
		if (sCurrentSetValue.length() >= settings.getIndex()) {
			String sSettingNumber = sCurrentSetValue.substring(sCurrentSetValue.length() - settings.getIndex(),
					sCurrentSetValue.length() - settings.getIndex() + 1);
			isValueSet = checkSettingsSetStatus(Integer.parseInt(sSettingNumber), organizationUnit);
		}
		else {
			// value is not set at all
			isValueSet = false;
		}
		return isValueSet;
	}

	@Override
	public boolean checkSettingsSetStatus(int settingNumber, OrganizationUnit organizationUnit) {
		LOG.debug("Checking for set status " + settingNumber + " and organiztion unit " + organizationUnit);
		boolean setStatus = false;
		if (organizationUnit == OrganizationUnit.COMPANY) {
			if (settingNumber == CommonConstants.SET_BY_COMPANY || settingNumber == CommonConstants.SET_BY_COMPANY_N_REGION
					|| settingNumber == CommonConstants.SET_BY_COMPANY_N_BRANCH || settingNumber == CommonConstants.SET_BY_COMPANY_N_REGION_N_BRANCH) {
				setStatus = true;
			}
		}
		else if (organizationUnit == OrganizationUnit.REGION) {
			if (settingNumber == CommonConstants.SET_BY_REGION || settingNumber == CommonConstants.SET_BY_COMPANY_N_REGION
					|| settingNumber == CommonConstants.SET_BY_REGION_N_BRANCH || settingNumber == CommonConstants.SET_BY_COMPANY_N_REGION_N_BRANCH) {
				setStatus = true;
			}
		}
		else if (organizationUnit == OrganizationUnit.BRANCH) {
			if (settingNumber == CommonConstants.SET_BY_BRANCH || settingNumber == CommonConstants.SET_BY_COMPANY_N_BRANCH
					|| settingNumber == CommonConstants.SET_BY_REGION_N_BRANCH || settingNumber == CommonConstants.SET_BY_COMPANY_N_REGION_N_BRANCH) {
				setStatus = true;
			}
		}
		return setStatus;
	}

	@Override
	public SettingsSetterLevel getSettingsSetLevel(long currentSetAggregateValue, SettingsForApplication settings) {
		LOG.info("Getting all the levels where settings are set for aggregate value: " + currentSetAggregateValue);
		SettingsSetterLevel level = new SettingsSetterLevel();
		String sCurrentSetValue = String.valueOf(currentSetAggregateValue);
		// find index of the settings to be checked for.
		// get the length of the value. Get the index from enum and deduct it from the length
		if (sCurrentSetValue.length() >= settings.getIndex()) {
			String sSettingNumber = sCurrentSetValue.substring(sCurrentSetValue.length() - settings.getIndex(),
					sCurrentSetValue.length() - settings.getIndex() + 1);
			int settingNumber = Integer.parseInt(sSettingNumber);
			if (settingNumber == CommonConstants.SET_BY_COMPANY) {
				level.setSetByCompany(true);
			}
			else if (settingNumber == CommonConstants.SET_BY_REGION) {
				level.setSetByRegion(true);
			}
			else if (settingNumber == CommonConstants.SET_BY_COMPANY_N_REGION) {
				level.setSetByCompany(true);
				level.setSetByRegion(true);
			}
			else if (settingNumber == CommonConstants.SET_BY_BRANCH) {
				level.setSetByBranch(true);
			}
			else if (settingNumber == CommonConstants.SET_BY_COMPANY_N_BRANCH) {
				level.setSetByCompany(true);
				level.setSetByBranch(true);
			}
			else if (settingNumber == CommonConstants.SET_BY_REGION_N_BRANCH) {
				level.setSetByRegion(true);
				level.setSetByBranch(true);
			}
			else if (settingNumber == CommonConstants.SET_BY_COMPANY_N_REGION_N_BRANCH) {
				level.setSetByCompany(true);
				level.setSetByRegion(true);
				level.setSetByBranch(true);
			}
		}
		return level;
	}

	@Override
	public OrganizationUnit getLowestSetterLevel(int setUnitValue) throws InvalidSettingsStateException {
		LOG.info("Getting the lowest setter level for " + setUnitValue);
		OrganizationUnit organizationUnit = null;
		if (setUnitValue == CommonConstants.SET_BY_NONE) {
			organizationUnit = null;
		}
		else if (setUnitValue == CommonConstants.SET_BY_BRANCH || setUnitValue == CommonConstants.SET_BY_REGION_N_BRANCH
				|| setUnitValue == CommonConstants.SET_BY_COMPANY_N_BRANCH || setUnitValue == CommonConstants.SET_BY_COMPANY_N_REGION_N_BRANCH) {
			LOG.debug("Settings set  by branch");
			organizationUnit = OrganizationUnit.BRANCH;
		}
		else if (setUnitValue == CommonConstants.SET_BY_REGION || setUnitValue == CommonConstants.SET_BY_COMPANY_N_REGION) {
			LOG.debug("Settings set  by region");
			organizationUnit = OrganizationUnit.REGION;
		}
		else if (setUnitValue == CommonConstants.SET_BY_COMPANY) {
			LOG.debug("Settings set  by company");
			organizationUnit = OrganizationUnit.COMPANY;
		}
		else {
			LOG.warn("Invalid value sent for checking the lowest setter");
			throw new InvalidSettingsStateException("Invalid value sent for checking the highest locker");
		}
		return organizationUnit;
	}
}
