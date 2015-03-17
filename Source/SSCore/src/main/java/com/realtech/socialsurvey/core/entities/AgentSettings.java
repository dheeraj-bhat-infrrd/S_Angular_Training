package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class AgentSettings extends OrganizationUnitSettings {

	private List<String> expertise;
	private List<String> hobbies;

	public List<String> getExpertise() {
		return expertise;
	}

	public void setExpertise(List<String> expertise) {
		this.expertise = expertise;
	}

	public List<String> getHobbies() {
		return hobbies;
	}

	public void setHobbies(List<String> hobbies) {
		this.hobbies = hobbies;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}

}
