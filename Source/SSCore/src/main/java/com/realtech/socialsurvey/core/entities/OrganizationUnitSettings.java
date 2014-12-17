package com.realtech.socialsurvey.core.entities;

import java.util.List;

/**
 * Holds the company settings
 */
public class OrganizationUnitSettings {

	private int organization_id;
	private float profile_completion;
	private String logo;
	private ContactDetailsSettings contact_details;
	private CRMInfo crm_info;
	private Licenses licenses;
	private List<Association> associations;
	private List<Achievement> achievements;
	private SurveySettings survey_setings;

	public int getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(int company_id) {
		this.organization_id = company_id;
	}

	public float getProfile_completion() {
		return profile_completion;
	}

	public void setProfile_completion(float profile_completion) {
		this.profile_completion = profile_completion;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public ContactDetailsSettings getContact_details() {
		return contact_details;
	}

	public void setContact_details(ContactDetailsSettings contact_details) {
		this.contact_details = contact_details;
	}

	public CRMInfo getCrm_info() {
		return crm_info;
	}

	public void setCrm_info(CRMInfo crm_info) {
		this.crm_info = crm_info;
	}

	public Licenses getLicenses() {
		return licenses;
	}

	public void setLicenses(Licenses licenses) {
		this.licenses = licenses;
	}

	public List<Association> getAssociations() {
		return associations;
	}

	public void setAssociations(List<Association> associations) {
		this.associations = associations;
	}

	public List<Achievement> getAchievements() {
		return achievements;
	}

	public void setAchievements(List<Achievement> achievements) {
		this.achievements = achievements;
	}

	public SurveySettings getSurvey_setings() {
		return survey_setings;
	}

	public void setSurvey_setings(SurveySettings survey_setings) {
		this.survey_setings = survey_setings;
	}

}
