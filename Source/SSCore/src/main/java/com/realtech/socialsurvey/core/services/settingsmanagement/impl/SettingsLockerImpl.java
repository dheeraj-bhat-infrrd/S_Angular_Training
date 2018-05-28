package com.realtech.socialsurvey.core.services.settingsmanagement.impl;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
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
		LOG.debug("Setting lock values for company: " + company.getCompanyId() + " for settings " + settings + " with lock status: " + hasBeenLocked);
		BigInteger modifiedSettingsLockValue = getModifiedSetSettingsValue(OrganizationUnit.COMPANY,  new BigInteger(company.getSettingsLockStatus()),
				settings, hasBeenLocked);
		company.setSettingsLockStatus(String.valueOf(modifiedSettingsLockValue));
		LOG.debug("Setting lock values for company: " + company.getCompanyId() + " completed.");
		return company;
	}

	@Override
	public Region lockSettingsValueForRegion(Region region, SettingsForApplication settings, boolean hasBeenLocked) throws NonFatalException {
		if (region == null || settings == null) {
			LOG.warn("Invalid values set to lock the settings for region");
			throw new InvalidInputException("Invalid values set to lock the region");
		}
		LOG.debug("Setting lock values for region: " + region.getRegionId() + " for settings " + settings + " with lock status: " + hasBeenLocked);
		BigInteger modifiedSettingsLockValue = getModifiedSetSettingsValue(OrganizationUnit.REGION, new BigInteger(region.getSettingsLockStatus()),
				settings, hasBeenLocked);
		region.setSettingsLockStatus(String.valueOf(modifiedSettingsLockValue));
		LOG.debug("Setting lock values for region: " + region.getRegionId() + " completed.");
		return region;
	}

	@Override
	public Branch lockSettingsValueForBranch(Branch branch, SettingsForApplication settings, boolean hasBeenLocked) throws NonFatalException {
		if (branch == null || settings == null) {
			LOG.warn("Invalid values set to lock the settings for branch");
			throw new InvalidInputException("Invalid values set to lock the branch");
		}
		LOG.debug("Setting lock values for branch: " + branch.getBranchId() + " for settings " + settings + " with lock status: " + hasBeenLocked);
		BigInteger modifiedSettingsLockValue = getModifiedSetSettingsValue(OrganizationUnit.BRANCH, new BigInteger(branch.getSettingsLockStatus()),
				settings, hasBeenLocked);
		branch.setSettingsLockStatus(String.valueOf(modifiedSettingsLockValue));
		LOG.debug("Setting lock values for branch: " + branch.getBranchId() + " completed.");
		return branch;
	}

	@Override
	public boolean isSettingsValueLocked(OrganizationUnit organizationUnit, BigInteger currentLockValue, SettingsForApplication settings) {
		LOG.debug("Checking if the value is locked for the settings");
		LOG.info("Checking is setting value locked for organizationUnit " + organizationUnit + " lock value " + currentLockValue + " and settings " + settings.toString() );
		boolean isValueLocked = false;
		String sCurrentLockValue = String.valueOf(currentLockValue);
		LOG.info("string sCurrentLockValue is " + sCurrentLockValue);
		LOG.info("string index is " + settings.getIndex());
		// find index of the settings to be checked for.
		// get the length of the value. Get the index from enum and deduct it from the length
		if (sCurrentLockValue.length() >= settings.getIndex()) {
			String sSettingNumber = sCurrentLockValue.substring(sCurrentLockValue.length() - settings.getIndex(), sCurrentLockValue.length()
					- settings.getIndex() + 1);
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
			if (lockNumber == CommonConstants.SET_BY_COMPANY || lockNumber == CommonConstants.SET_BY_COMPANY_N_REGION
					|| lockNumber == CommonConstants.SET_BY_COMPANY_N_BRANCH || lockNumber == CommonConstants.SET_BY_COMPANY_N_REGION_N_BRANCH) {
				lockStatus = true;
			}
		}
		else if (organizationUnit == OrganizationUnit.REGION) {
			if (lockNumber == CommonConstants.SET_BY_REGION || lockNumber == CommonConstants.SET_BY_COMPANY_N_REGION
					|| lockNumber == CommonConstants.SET_BY_REGION_N_BRANCH || lockNumber == CommonConstants.SET_BY_COMPANY_N_REGION_N_BRANCH) {
				lockStatus = true;
			}
		}
		else if (organizationUnit == OrganizationUnit.BRANCH) {
			if (lockNumber == CommonConstants.SET_BY_BRANCH || lockNumber == CommonConstants.SET_BY_COMPANY_N_BRANCH
					|| lockNumber == CommonConstants.SET_BY_REGION_N_BRANCH || lockNumber == CommonConstants.SET_BY_COMPANY_N_REGION_N_BRANCH) {
				lockStatus = true;
			}
		}
		return lockStatus;
	}

	@Override
	public OrganizationUnit getHighestLockerLevel(int lockUnitValue) throws InvalidSettingsStateException {
		LOG.debug("Getting highest locker for " + lockUnitValue);
		OrganizationUnit organizationUnit = null;
		if (lockUnitValue == CommonConstants.LOCKED_BY_NONE) {
			organizationUnit = null;
		}
		else if (lockUnitValue == CommonConstants.SET_BY_COMPANY || lockUnitValue == CommonConstants.SET_BY_COMPANY_N_REGION
				|| lockUnitValue == CommonConstants.SET_BY_COMPANY_N_BRANCH || lockUnitValue == CommonConstants.SET_BY_COMPANY_N_REGION_N_BRANCH) {
			organizationUnit = OrganizationUnit.COMPANY;
		}
		else if (lockUnitValue == CommonConstants.SET_BY_REGION || lockUnitValue == CommonConstants.SET_BY_REGION_N_BRANCH) {
			organizationUnit = OrganizationUnit.REGION;
		}
		else if (lockUnitValue == CommonConstants.SET_BY_REGION || lockUnitValue == CommonConstants.SET_BY_BRANCH) {
			organizationUnit = OrganizationUnit.BRANCH;
		}
		else {
			LOG.warn("Invalid value sent for checking the highest locker");
			throw new InvalidSettingsStateException("Invalid value sent for checking the highest locker");
		}
		return organizationUnit;
	}

	BigInteger getModifiedSetSettingsValue(OrganizationUnit organizationUnit, BigInteger currentLockValue, SettingsForApplication settings,
									   boolean hasBeenLocked) throws InvalidSettingsStateException {
		LOG.debug("Finding the modified settings lock value");
		BigInteger valueToBeReturned = currentLockValue;
		BigInteger valueToBeAdded = BigInteger.valueOf(0);
		boolean isValueAlreadyLocked = false; // check if the value can be added or not
		if (organizationUnit == OrganizationUnit.COMPANY) {
			LOG.debug("Setting lock for company");
			valueToBeAdded = settings.getOrder();
		}
		else if (organizationUnit == OrganizationUnit.REGION) {
			LOG.debug("Setting lock for region");
			valueToBeAdded = (BigInteger.valueOf(2) ).multiply(settings.getOrder() );
		}
		else if (organizationUnit == OrganizationUnit.BRANCH) {
			LOG.debug("Setting lock for branch");
			valueToBeAdded = (BigInteger.valueOf(4) ).multiply(settings.getOrder());
		}
		isValueAlreadyLocked = isSettingsValueLocked(organizationUnit, currentLockValue, settings);
		if (hasBeenLocked) {
			if (!isValueAlreadyLocked) {
				valueToBeReturned = currentLockValue.add( valueToBeAdded );
			}
			else {
				LOG.warn("Cannot lock an already locked entity.");
				throw new InvalidSettingsStateException("Cannot lock an already locked entity.");
			}
		}
		else {
			if (isValueAlreadyLocked) {
				valueToBeReturned = currentLockValue.subtract( valueToBeAdded );
			}
			else {
				LOG.warn("Cannot unlock an already unlocked entity.");
				throw new InvalidSettingsStateException("Cannot lock an already unlocked entity.");
			}
		}
		return valueToBeReturned;
	}

}
