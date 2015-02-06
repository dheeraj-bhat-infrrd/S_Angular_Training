package com.realtech.socialsurvey.core.entities;

public class LockSettings {

	private boolean isLogoLocked = true;
	private boolean isSurveySettingsLocked = true;

	public boolean isLogoLocked() {
		return isLogoLocked;
	}

	public void setLogoLocked(boolean isLogoLocked) {
		this.isLogoLocked = isLogoLocked;
	}

	public boolean isSurveySettingsLocked() {
		return isSurveySettingsLocked;
	}

	public void setSurveySettingsLocked(boolean isSurveySettingsLocked) {
		this.isSurveySettingsLocked = isSurveySettingsLocked;
	}

}
