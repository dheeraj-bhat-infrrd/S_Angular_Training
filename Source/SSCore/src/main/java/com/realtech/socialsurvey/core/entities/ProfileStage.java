package com.realtech.socialsurvey.core.entities;

/**
 * @author Kalmeshwar Class is used to store the profile stages for different settings.
 */
public class ProfileStage implements Comparable<ProfileStage> {

	private String profileStageKey;
	private Integer order;
	private int status;

	public String getProfileStageKey() {
		return profileStageKey;
	}

	public void setProfileStageKey(String profileStageKey) {
		this.profileStageKey = profileStageKey;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Override
	public int compareTo(ProfileStage profileStage) {

		if (profileStage == null)
			return 0;
		return this.getOrder().compareTo(profileStage.getOrder());
	}
}
