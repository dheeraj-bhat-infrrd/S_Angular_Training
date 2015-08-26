package com.realtech.socialsurvey.core.entities;

/**
 * Holds detials of the settings set by organization unit
 */
public class SettingsDetails {

	private long lockSettingsHolder;
	private long setSettingsHolder;

	public long getLockSettingsHolder() {
		return lockSettingsHolder;
	}

	public void setLockSettingsHolder(long lockSettingsHolder) {
		this.lockSettingsHolder = lockSettingsHolder;
	}

	public long getSetSettingsHolder() {
		return setSettingsHolder;
	}

	public void setSetSettingsHolder(long setSettingsHolder) {
		this.setSettingsHolder = setSettingsHolder;
	}

}
