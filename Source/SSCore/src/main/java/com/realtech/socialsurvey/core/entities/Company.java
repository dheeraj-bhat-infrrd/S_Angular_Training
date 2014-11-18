package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the company database table.
 * 
 */
@Entity
@NamedQuery(name="Company.findAll", query="SELECT c FROM Company c")
public class Company implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="COMPANY_ID")
	private int companyId;

	private String company;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_ON")
	private Timestamp createdOn;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_ON")
	private Timestamp modifiedOn;

	private int status;

	//bi-directional many-to-one association to Branch
	@OneToMany(mappedBy="company")
	private List<Branch> branches;

	//bi-directional many-to-one association to LicenseDetail
	@OneToMany(mappedBy="company")
	private List<LicenseDetail> licenseDetails;

	//bi-directional many-to-one association to OrganizationLevelSetting
	@OneToMany(mappedBy="company")
	private List<OrganizationLevelSetting> organizationLevelSettings;

	//bi-directional many-to-one association to Region
	@OneToMany(mappedBy="company")
	private List<Region> regions;

	//bi-directional many-to-one association to Survey
	@OneToMany(mappedBy="company")
	private List<Survey> surveys;

	//bi-directional many-to-one association to SurveyQuestion
	@OneToMany(mappedBy="company")
	private List<SurveyQuestion> surveyQuestions;

	//bi-directional many-to-one association to UserInvite
	@OneToMany(mappedBy="company")
	private List<UserInvite> userInvites;

	//bi-directional many-to-one association to UserProfile
	@OneToMany(mappedBy="company")
	private List<UserProfile> userProfiles;

	//bi-directional many-to-one association to User
	@OneToMany(mappedBy="company")
	private List<User> users;

	public Company() {
	}

	public int getCompanyId() {
		return this.companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getCompany() {
		return this.company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public String getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getModifiedOn() {
		return this.modifiedOn;
	}

	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<Branch> getBranches() {
		return this.branches;
	}

	public void setBranches(List<Branch> branches) {
		this.branches = branches;
	}

	public Branch addBranch(Branch branch) {
		getBranches().add(branch);
		branch.setCompany(this);

		return branch;
	}

	public Branch removeBranch(Branch branch) {
		getBranches().remove(branch);
		branch.setCompany(null);

		return branch;
	}

	public List<LicenseDetail> getLicenseDetails() {
		return this.licenseDetails;
	}

	public void setLicenseDetails(List<LicenseDetail> licenseDetails) {
		this.licenseDetails = licenseDetails;
	}

	public LicenseDetail addLicenseDetail(LicenseDetail licenseDetail) {
		getLicenseDetails().add(licenseDetail);
		licenseDetail.setCompany(this);

		return licenseDetail;
	}

	public LicenseDetail removeLicenseDetail(LicenseDetail licenseDetail) {
		getLicenseDetails().remove(licenseDetail);
		licenseDetail.setCompany(null);

		return licenseDetail;
	}

	public List<OrganizationLevelSetting> getOrganizationLevelSettings() {
		return this.organizationLevelSettings;
	}

	public void setOrganizationLevelSettings(List<OrganizationLevelSetting> organizationLevelSettings) {
		this.organizationLevelSettings = organizationLevelSettings;
	}

	public OrganizationLevelSetting addOrganizationLevelSetting(OrganizationLevelSetting organizationLevelSetting) {
		getOrganizationLevelSettings().add(organizationLevelSetting);
		organizationLevelSetting.setCompany(this);

		return organizationLevelSetting;
	}

	public OrganizationLevelSetting removeOrganizationLevelSetting(OrganizationLevelSetting organizationLevelSetting) {
		getOrganizationLevelSettings().remove(organizationLevelSetting);
		organizationLevelSetting.setCompany(null);

		return organizationLevelSetting;
	}

	public List<Region> getRegions() {
		return this.regions;
	}

	public void setRegions(List<Region> regions) {
		this.regions = regions;
	}

	public Region addRegion(Region region) {
		getRegions().add(region);
		region.setCompany(this);

		return region;
	}

	public Region removeRegion(Region region) {
		getRegions().remove(region);
		region.setCompany(null);

		return region;
	}

	public List<Survey> getSurveys() {
		return this.surveys;
	}

	public void setSurveys(List<Survey> surveys) {
		this.surveys = surveys;
	}

	public Survey addSurvey(Survey survey) {
		getSurveys().add(survey);
		survey.setCompany(this);

		return survey;
	}

	public Survey removeSurvey(Survey survey) {
		getSurveys().remove(survey);
		survey.setCompany(null);

		return survey;
	}

	public List<SurveyQuestion> getSurveyQuestions() {
		return this.surveyQuestions;
	}

	public void setSurveyQuestions(List<SurveyQuestion> surveyQuestions) {
		this.surveyQuestions = surveyQuestions;
	}

	public SurveyQuestion addSurveyQuestion(SurveyQuestion surveyQuestion) {
		getSurveyQuestions().add(surveyQuestion);
		surveyQuestion.setCompany(this);

		return surveyQuestion;
	}

	public SurveyQuestion removeSurveyQuestion(SurveyQuestion surveyQuestion) {
		getSurveyQuestions().remove(surveyQuestion);
		surveyQuestion.setCompany(null);

		return surveyQuestion;
	}

	public List<UserInvite> getUserInvites() {
		return this.userInvites;
	}

	public void setUserInvites(List<UserInvite> userInvites) {
		this.userInvites = userInvites;
	}

	public UserInvite addUserInvite(UserInvite userInvite) {
		getUserInvites().add(userInvite);
		userInvite.setCompany(this);

		return userInvite;
	}

	public UserInvite removeUserInvite(UserInvite userInvite) {
		getUserInvites().remove(userInvite);
		userInvite.setCompany(null);

		return userInvite;
	}

	public List<UserProfile> getUserProfiles() {
		return this.userProfiles;
	}

	public void setUserProfiles(List<UserProfile> userProfiles) {
		this.userProfiles = userProfiles;
	}

	public UserProfile addUserProfile(UserProfile userProfile) {
		getUserProfiles().add(userProfile);
		userProfile.setCompany(this);

		return userProfile;
	}

	public UserProfile removeUserProfile(UserProfile userProfile) {
		getUserProfiles().remove(userProfile);
		userProfile.setCompany(null);

		return userProfile;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public User addUser(User user) {
		getUsers().add(user);
		user.setCompany(this);

		return user;
	}

	public User removeUser(User user) {
		getUsers().remove(user);
		user.setCompany(null);

		return user;
	}

}