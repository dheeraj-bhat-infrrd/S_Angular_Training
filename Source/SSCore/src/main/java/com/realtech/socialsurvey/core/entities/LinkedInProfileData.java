package com.realtech.socialsurvey.core.entities;

/**
 * Linkedin profile data from linkedin. The structure is same as linkedin
 */
public class LinkedInProfileData {

	private String firstName;
	private String headline;
	private String id;
	private String industry;
	private String summary;
	private Location location;
	private String lastName;
	private String pictureUrl;
	private String specialties;
	private Positions positions;
	private Skills skills;
	private String associations;
	private String interests;
	private PictureUrls pictureUrls;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public String getSpecialties() {
		return specialties;
	}

	public void setSpecialties(String specialties) {
		this.specialties = specialties;
	}

	public Positions getPositions() {
		return positions;
	}

	public void setPositions(Positions positions) {
		this.positions = positions;
	}

	public Skills getSkills() {
		return skills;
	}

	public void setSkills(Skills skills) {
		this.skills = skills;
	}

	public String getAssociations() {
		return associations;
	}

	public void setAssociations(String associations) {
		this.associations = associations;
	}

	public String getInterests() {
		return interests;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}

	public PictureUrls getPictureUrls() {
		return pictureUrls;
	}

	public void setPictureUrls(PictureUrls pictureUrls) {
		this.pictureUrls = pictureUrls;
	}

}
