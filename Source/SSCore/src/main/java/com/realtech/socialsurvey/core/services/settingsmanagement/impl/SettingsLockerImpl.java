package com.realtech.socialsurvey.core.services.settingsmanagement.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsLocker;

@Component
public class SettingsLockerImpl implements SettingsLocker {

	private static final Logger LOG = LoggerFactory.getLogger(SettingsLockerImpl.class);

	@Override
	public Company lockSettingsValueForCompany(Company company, SettingsForApplication settings, boolean hasBeenLocked) throws NonFatalException {
		if (company == null || settings == null) {
			LOG.warn("Invalid values set to lock the settings for company");
			throw new InvalidInputException("Invalid values set to lock the settings");
		}
		LOG.info("Setting lock values for company: " + company.getCompanyId() + " for settings " + settings + " with lock status: " + hasBeenLocked);
		long modifiedSettingsLockValue = getModifiedSetSettingsValue(OrganizationUnit.COMPANY, Long.parseLong(company.getSettingsLockStatus()),
				settings, hasBeenLocked);
		company.setSettingsLockStatus(String.valueOf(modifiedSettingsLockValue));
		LOG.info("Setting lock values for company: " + company.getCompanyId() + " completed.");
		return company;
	}

	@Override
	public Region lockSettingsValueForRegion(Region region, SettingsForApplication settings, boolean hasBeenLocked) throws NonFatalException {
		if (region == null || settings == null) {
			LOG.warn("Invalid values set to lock the settings for region");
			throw new InvalidInputException("Invalid values set to lock the region");
		}
		LOG.info("Setting lock values for region: " + region.getRegionId() + " for settings " + settings + " with lock status: " + hasBeenLocked);
		long modifiedSettingsLockValue = getModifiedSetSettingsValue(OrganizationUnit.REGION, Long.parseLong(region.getSettingsLockStatus()),
				settings, hasBeenLocked);
		region.setSettingsLockStatus(String.valueOf(modifiedSettingsLockValue));
		LOG.info("Setting lock values for region: " + region.getRegionId() + " completed.");
		return region;
	}

	@Override
	public Branch lockSettingsValueForBranch(Branch branch, SettingsForApplication settings, boolean hasBeenLocked) throws NonFatalException {
		if (branch == null || settings == null) {
			LOG.warn("Invalid values set to lock the settings for branch");
			throw new InvalidInputException("Invalid values set to lock the branch");
		}
		LOG.info("Setting lock values for branch: " + branch.getBranchId() + " for settings " + settings + " with lock status: " + hasBeenLocked);
		long modifiedSettingsLockValue = getModifiedSetSettingsValue(OrganizationUnit.BRANCH, Long.parseLong(branch.getSettingsLockStatus()),
				settings, hasBeenLocked);
		branch.setSettingsLockStatus(String.valueOf(modifiedSettingsLockValue));
		LOG.info("Setting lock values for branch: " + branch.getBranchId() + " completed.");
		return branch;
	}

	@Override
	public boolean isSettingsValueLocked(OrganizationUnit organizationUnit, long currentLockValue, SettingsForApplication settings) {
		LOG.info("Checking if the value is locked for the settings");
		boolean isValueLocked = false;
		String sCurrentLockValue = String.valueOf(currentLockValue);
		// find index of the settings to be checked for.
		// get the length of the value. Get the index from enum and deduct it from the length
		if (sCurrentLockValue.length() >= settings.getIndex()) {
			String sSettingNumber = sCurrentLockValue.substring(sCurrentLockValue.length() - settings.getIndex(),
					sCurrentLockValue.length() - settings.getIndex() + 1);
			isValueLocked = checkSettingsLockStatus(Integer.parseInt(sSettingNumber), organizationUnit);
		}
		else {
			// value is not locked at all
			isValueLocked = false;
		}
		return isValueLocked;
	}

	@Override
	public boolean checkSettingsLockStatus(int lockNumber, OrganizationUnit organizationUnit) {
		LOG.debug("Checking for lock status " + lockNumber + " and organiztion unit " + organizationUnit);
		boolean lockStatus = false;
		if (organizationUnit == OrganizationUnit.COMPANY) {
			if (lockNumber == 1 || lockNumber == 3 || lockNumber == 5 || lockNumber == 7) {
				lockStatus = true;
			}
		}
		else if (organizationUnit == OrganizationUnit.REGION) {
			if (lockNumber == 2 || lockNumber == 6) {
				lockStatus = true;
			}
		}
		else if (organizationUnit == OrganizationUnit.BRANCH) {
			if (lockNumber == 4) {
				lockStatus = true;
			}
		}
		return lockStatus;
	}

	private long getModifiedSetSettingsValue(OrganizationUnit organizationUnit, long currentLockValue, SettingsForApplication settings,
			boolean hasBeenLocked) throws InvalidSettingsStateException {
		LOG.debug("Finding the modified settings lock value");
		long valueToBeReturned = currentLockValue;
		long valueToBeAdded = 0l;
		boolean isValueAlreadyLocked = false; // check if the value can be added or not
		if (organizationUnit == OrganizationUnit.COMPANY) {
			LOG.debug("Setting lock for company");
			valueToBeAdded = settings.getOrder();
		}
		else if (organizationUnit == OrganizationUnit.REGION) {
			LOG.debug("Setting lock for region");
			valueToBeAdded = 2 * settings.getOrder();
		}
		else if (organizationUnit == OrganizationUnit.BRANCH) {
			LOG.debug("Setting lock for branch");
			valueToBeAdded = 4 * settings.getOrder();
		}
		isValueAlreadyLocked = isSettingsValueLocked(organizationUnit, currentLockValue, settings);
		if (hasBeenLocked) {
			if (!isValueAlreadyLocked) {
				valueToBeReturned = currentLockValue + valueToBeAdded;
			}
			else {
				LOG.warn("Cannot lock an already locked entity.");
				throw new InvalidSettingsStateException("Cannot lock an already locked entity.");
			}
		}
		else {
			if (isValueAlreadyLocked) {
				valueToBeReturned = currentLockValue - valueToBeAdded;
			}
			else {
				LOG.warn("Cannot unlock an already unlocked entity.");
				throw new InvalidSettingsStateException("Cannot lock an already unlocked entity.");
			}
		}
		return valueToBeReturned;
	}

}
