package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class AgentSettings extends OrganizationUnitSettings {

	private List<String> expertise;
	private List<String> hobbies;
	private String designation;

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

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	@Override
	public String toString() {
		return super.toString() + "\t expertise: " + expertise.toString() + "\t hobbies: " + hobbies.toString();
	}

}
