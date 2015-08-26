package com.realtech.socialsurvey.core.services.settingsmanagement.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.settingsmanagement.SetttingsSetter;

public class SettingsSetterImpl implements SetttingsSetter {

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
	public Region setSettingsValueForCompany(Region region, SettingsForApplication settings, boolean hasBeenSet) throws NonFatalException {
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
	public Branch setSettingsValueForCompany(Branch branch, SettingsForApplication settings, boolean hasBeenSet) throws NonFatalException {
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

	private long getModifiedSetSettingsValue(OrganizationUnit organizationUnit, long currentSetValue, SettingsForApplication settings,
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
			if (settingNumber == 1 || settingNumber == 3 || settingNumber == 5 || settingNumber == 7) {
				setStatus = true;
			}
		}
		else if (organizationUnit == OrganizationUnit.REGION) {
			if (settingNumber == 2 || settingNumber == 6) {
				setStatus = true;
			}
		}
		else if (organizationUnit == OrganizationUnit.BRANCH) {
			if (settingNumber == 4) {
				setStatus = true;
			}
		}
		return setStatus;
	}
}
