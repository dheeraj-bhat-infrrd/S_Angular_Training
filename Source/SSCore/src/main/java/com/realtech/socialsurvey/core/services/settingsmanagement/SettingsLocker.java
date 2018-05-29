package com.realtech.socialsurvey.core.services.settingsmanagement;

import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;

/**
 * Lock on settings
 */
public interface SettingsLocker {

	/**
	 * Sets the lock settings for company
	 * 
	 * @param company
	 * @param settings
	 * @param hasBeenLocked
	 * @return
	 * @throws NonFatalException
	 */
	public Company lockSettingsValueForCompany(Company company, SettingsForApplication settings, boolean hasBeenLocked) throws NonFatalException;

	/**
	 * Sets the lock settings for region
	 * 
	 * @param region
	 * @param settings
	 * @param hasBeenLocked
	 * @return
	 * @throws NonFatalException
	 */
	public Region lockSettingsValueForRegion(Region region, SettingsForApplication settings, boolean hasBeenLocked) throws NonFatalException;

	/**
	 * Sets the lock settings for branch
	 * 
	 * @param branch
	 * @param settings
	 * @param hasBeenLocked
	 * @return
	 * @throws NonFatalException
	 */
	public Branch lockSettingsValueForBranch(Branch branch, SettingsForApplication settings, boolean hasBeenLocked) throws NonFatalException;

	/**
	 * Check if the settings value is locked
	 * 
	 * @param organizationUnit
	 * @param currentLockValue
	 * @param settings
	 * @return
	 */
	public boolean isSettingsValueLocked(OrganizationUnit organizationUnit, Double currentLockValue, SettingsForApplication settings);

	/**
	 * Checks the settings lock status
	 * 
	 * @param lockNumber
	 * @param organizationUnit
	 * @return
	 */
	public boolean checkSettingsLockStatus(int lockNumber, OrganizationUnit organizationUnit);

	/**
	 * Checks the highest unit who has locked the settings
	 * 
	 * @param lockUnitValue
	 * @return
	 * @throws InvalidSettingsStateException
	 */
	public OrganizationUnit getHighestLockerLevel(int lockUnitValue) throws InvalidSettingsStateException;
}
