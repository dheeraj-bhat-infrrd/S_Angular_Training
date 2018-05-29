package com.realtech.socialsurvey.core.entities;

import java.math.BigInteger;

/**
 * Holds detials of the settings set by organization unit
 */
public class SettingsDetails {

	private long lockSettingsHolder;
	private BigInteger setSettingsHolder;

	public long getLockSettingsHolder() {
		return lockSettingsHolder;
	}

	public void setLockSettingsHolder(long lockSettingsHolder) {
		this.lockSettingsHolder = lockSettingsHolder;
	}

	public BigInteger getSetSettingsHolder() {
		return setSettingsHolder;
	}

	public void setSetSettingsHolder(BigInteger setSettingsHolder) {
		this.setSettingsHolder = setSettingsHolder;
	}

}
