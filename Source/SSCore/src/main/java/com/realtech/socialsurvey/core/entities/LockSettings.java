package com.realtech.socialsurvey.core.entities;

public class LockSettings {

	private boolean isLogoLocked;
	private boolean isDisplayNameLocked;
	private boolean isWebAddressLocked;
	private boolean isWorkPhoneLocked;
	private boolean isPersonalPhoneLocked;
	private boolean isFaxPhoneLocked;
	private boolean isAboutMeLocked;
	private boolean isAddressLocked;

	public boolean getIsLogoLocked() {
		return isLogoLocked;
	}

	public void setLogoLocked(boolean isLogoLocked) {
		this.isLogoLocked = isLogoLocked;
	}

	public boolean getIsDisplayNameLocked() {
		return isDisplayNameLocked;
	}

	public void setDisplayNameLocked(boolean isDisplayNameLocked) {
		this.isDisplayNameLocked = isDisplayNameLocked;
	}

	public boolean getIsWebAddressLocked() {
		return isWebAddressLocked;
	}

	public void setWebAddressLocked(boolean isWebAddressLocked) {
		this.isWebAddressLocked = isWebAddressLocked;
	}

	public boolean getIsWorkPhoneLocked() {
		return isWorkPhoneLocked;
	}

	public void setWorkPhoneLocked(boolean isWorkPhoneLocked) {
		this.isWorkPhoneLocked = isWorkPhoneLocked;
	}

	public boolean getIsPersonalPhoneLocked() {
		return isPersonalPhoneLocked;
	}

	public void setPersonalPhoneLocked(boolean isPersonalPhoneLocked) {
		this.isPersonalPhoneLocked = isPersonalPhoneLocked;
	}

	public boolean getIsFaxPhoneLocked() {
		return isFaxPhoneLocked;
	}

	public void setFaxPhoneLocked(boolean isFaxPhoneLocked) {
		this.isFaxPhoneLocked = isFaxPhoneLocked;
	}

	public boolean getIsAboutMeLocked() {
		return isAboutMeLocked;
	}

	public void setAboutMeLocked(boolean isAboutMeLocked) {
		this.isAboutMeLocked = isAboutMeLocked;
	}

	public boolean getIsAddressLocked() {
		return isAddressLocked;
	}

	public void setAddressLocked(boolean isAddressLocked) {
		this.isAddressLocked = isAddressLocked;
	}

	@Override
	public String toString() {
		return "LockSettings [isLogoLocked=" + isLogoLocked + ", isDisplayNameLocked=" + isDisplayNameLocked + ", isWebAddressLocked="
				+ isWebAddressLocked + ", isWorkPhoneLocked=" + isWorkPhoneLocked + ", isPersonalPhoneLocked=" + isPersonalPhoneLocked
				+ ", isFaxPhoneLocked=" + isFaxPhoneLocked + ", isAboutMeLocked=" + isAboutMeLocked + ", isAddressLocked=" + isAddressLocked + "]";
	}
}