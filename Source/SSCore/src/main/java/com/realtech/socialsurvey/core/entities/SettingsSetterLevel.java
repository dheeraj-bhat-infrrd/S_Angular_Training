package com.realtech.socialsurvey.core.entities;

/**
 * Check if the settings is set by company, region or branch
 */
public class SettingsSetterLevel {

	private boolean isSetByCompany;
	private boolean isSetByRegion;
	private boolean isSetByBranch;

	public boolean isSetByCompany() {
		return isSetByCompany;
	}

	public void setSetByCompany(boolean isSetByCompany) {
		this.isSetByCompany = isSetByCompany;
	}

	public boolean isSetByRegion() {
		return isSetByRegion;
	}

	public void setSetByRegion(boolean isSetByRegion) {
		this.isSetByRegion = isSetByRegion;
	}

	public boolean isSetByBranch() {
		return isSetByBranch;
	}

	public void setSetByBranch(boolean isSetByBranch) {
		this.isSetByBranch = isSetByBranch;
	}

}
