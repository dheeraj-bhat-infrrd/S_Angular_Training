package com.realtech.socialsurvey.core.services.settingsmanagement;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.enums.AvailableSettings;
import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Operates on setting of the settings
 *
 */
public interface SetttingsSetter {

	/**
	 * Sets the value of the set setting fields of the organization unit value
	 * @param company
	 * @param hasBeenSet
	 * @return
	 * @throws NonFatalException
	 */
	public long setSettingsValueForCompany(Company company, AvailableSettings settings, boolean hasBeenSet) throws NonFatalException;
}
