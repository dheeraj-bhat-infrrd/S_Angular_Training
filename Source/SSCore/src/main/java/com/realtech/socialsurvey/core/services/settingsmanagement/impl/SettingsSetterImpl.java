package com.realtech.socialsurvey.core.services.settingsmanagement.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.enums.AvailableSettings;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.settingsmanagement.SetttingsSetter;

public class SettingsSetterImpl implements SetttingsSetter {

	private static final Logger LOG = LoggerFactory.getLogger(SettingsSetterImpl.class);

	@Override
	public long setSettingsValueForCompany(Company company, AvailableSettings settings, boolean hasBeenSet) throws NonFatalException {
		// Get the set settings value for the corresponding unit and set the value according to the input sent
		if(company == null || settings == null){
			throw new InvalidInputException("Invalid input sent for settings update");
		}
		LOG.info("Setting values to company "+company);
		return 0;
	}
	
	private long getModifiedSetSettingsValue(OrganizationUnit organizationUnit, long currentSetSettings, AvailableSettings settings, boolean hasBeenSet){
		LOG.debug("Finding the modified set settings value");
		
		return 0;
	}

}
