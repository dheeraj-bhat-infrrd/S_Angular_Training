package com.realtech.socialsurvey.core.entities;

import java.util.List;
import org.springframework.data.annotation.Id;

public class IndividualSettings {

	@Id
	private String id;
	private long userId;
	private String firstName;
	private String lastName;
	private String emailId;
	private String profileName;
	private OrganizationUnitSettings organizationUnitSettings;
	private List<String> expertise;
	private List<String> hobbies;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public OrganizationUnitSettings getOrganizationUnitSettings() {
		return organizationUnitSettings;
	}

	public void setOrganizationUnitSettings(OrganizationUnitSettings organizationUnitSettings) {
		this.organizationUnitSettings = organizationUnitSettings;
	}

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
		return "\t firstName: " + firstName + "\t lastName: " + lastName + "\t emailId: " + emailId;
	}
}