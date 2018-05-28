package com.realtech.socialsurvey.core.services.settingsmanagement;

import java.math.BigInteger;

import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SettingsSetterLevel;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;

/**
 * Operates on setting of the settings
 */
public interface SettingsSetter {

	/**
	 * Sets the value of the set setting fields of the company. The latest value of setter is set in
	 * the company and returned.
	 * 
	 * @param company
	 * @param hasBeenSet
	 * @return modified company
	 * @throws NonFatalException
	 */
	public Company setSettingsValueForCompany(Company company, SettingsForApplication settings, boolean hasBeenSet) throws NonFatalException;

	/**
	 * Sets the value of the set setting fields of the region. The latest value of setter is set in
	 * the region and returned.
	 * 
	 * @param region
	 * @param settings
	 * @param hasBeenSet
	 * @return
	 * @throws NonFatalException
	 */
	public Region setSettingsValueForRegion(Region region, SettingsForApplication settings, boolean hasBeenSet) throws NonFatalException;

	/**
	 * Sets the value of the set setting fields of the branch. The latest value of setter is set in
	 * the branch and returned.
	 * 
	 * @param branch
	 * @param settings
	 * @param hasBeenSet
	 * @return
	 * @throws NonFatalException
	 */
	public Branch setSettingsValueForBranch(Branch branch, SettingsForApplication settings, boolean hasBeenSet) throws NonFatalException;

	/**
	 * Check if the settings value is set for the organization unit
	 * 
	 * @param organizationUnit
	 * @param currentSetValue
	 * @param settings
	 * @return
	 */
	public boolean isSettingsValueSet(OrganizationUnit organizationUnit, BigInteger currentSetValue, SettingsForApplication settings);

	/**
	 * Check the settings set status
	 * 
	 * @param settingNumber
	 * @param organizationUnit
	 * @return
	 */
	public boolean checkSettingsSetStatus( double settingNumber, OrganizationUnit organizationUnit);

	/**
	 * Finds out all te level who have set the settings value
	 * 
	 * @param currentSetAggregateValue
	 * @param settings
	 * @return
	 */
	public SettingsSetterLevel getSettingsSetLevel(long currentSetAggregateValue, SettingsForApplication settings);

	/**
	 * Get the lowest hierarchy level who has set the settings
	 * 
	 * @param setUnitValue
	 * @return
	 * @throws InvalidSettingsStateException
	 */
	public OrganizationUnit getLowestSetterLevel( double setUnitValue) throws InvalidSettingsStateException;
}
