package com.realtech.socialsurvey.core.entities;


/**
 * @author Kalmeshwar
 * 
 * Class is used to store the profile stages for different settings.
 *
 */
public class ProfileStage {
	//implements Comparable<ProfileStage>
	private String profileStageKey;
	private int order;
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
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	/*@Override
	public int compareTo(ProfileStage profileStage) {
		
		if(profileStage == null)
			return 0;
		
		if(this.order <profileStage.getOrder())
			return -1;
		else if(this.order > profileStage.getOrder())
			return 1;
		else
			return 0;
	}*/
}
