package com.realtech.socialsurvey.core.services.settingsmanagement;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.SettingsDetails;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;

/**
 * Operates on setting of the settings
 */
public interface SettingsManager {

	public Map<String, BigInteger> calculateSettingsScore(List<SettingsDetails> settingsDetails);

	public List<SettingsDetails> getScoreForCompleteHeirarchy(long companyId, long branchId, long regionId);

	/**
	 * Gets the oganization unit for each setting
	 * @param currentSetAggregateValue
	 * @param currentLockAggregateValue
	 * @return
	 * @throws InvalidSettingsStateException
	 */
	public Map<SettingsForApplication, OrganizationUnit> getClosestSettingLevel(String currentSetAggregateValue, String currentLockAggregateValue)
			throws InvalidSettingsStateException;
}
