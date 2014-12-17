package com.realtech.socialsurvey.core.entities;

import java.util.List;
import org.springframework.data.annotation.Id;

/**
 * Holds the company settings
 */
public class OrganizationUnitSettings {

	@Id
	private String id;
	private int iden;
	private float profile_completion;
	private String logo;
	private ContactDetailsSettings contact_details;
	private CRMInfo crm_info;
	private Licenses licenses;
	private List<Association> associations;
	private List<Achievement> achievements;
	private SurveySettings survey_setings;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getIden() {
		return iden;
	}

	public void setIden(int iden) {
		this.iden = iden;
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

	@Override
	public String toString() {
		return "iden: " + iden + "\t profile_completion: " + profile_completion + "\t logo: " + logo + "\t contact_details: "
				+ contact_details.toString() + "\t crm_info: " + crm_info + "\t licenses: " + licenses.toString() + "\t associations: "
				+ associations.toString() + "\t achievements: " + achievements.toString() + "\t survey_setings: " + survey_setings.toString();
	}

}
