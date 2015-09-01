package com.realtech.socialsurvey.core.services.settingsmanagement.impl;

import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsLocker;

@Component
public class SettingsLockerImpl implements SettingsLocker {

	@Override
	public Company lockSettingsValueForCompany(Company company, SettingsForApplication settings, boolean hasBeenLocked) throws NonFatalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Region lockSettingsValueForRegion(Region region, SettingsForApplication settings, boolean hasBeenLocked) throws NonFatalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Branch lockSettingsValueForBranch(Branch branch, SettingsForApplication settings, boolean hasBeenLocked) throws NonFatalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSettingsValueLocked(OrganizationUnit organizationUnit, long currentLockValue, SettingsForApplication settings) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkSettingsLockStatus(int lockNumber, OrganizationUnit organizationUnit) {
		// TODO Auto-generated method stub
		return false;
	}

}
