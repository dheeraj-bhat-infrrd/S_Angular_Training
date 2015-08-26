package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the user_profile database table.
 */
@Entity
@Table(name = "USER_PROFILE")
@NamedQueries({
		@NamedQuery(name = "UserProfile.updateProfileByUser", query = "update UserProfile u set u.status = ?, u.modifiedBy = ?, u.modifiedOn = ? where u.user = ?"),
		@NamedQuery(name = "UserProfile.updateProfileByUserAndBranch", query = "update UserProfile u set u.status = ?, u.modifiedBy = ?, u.modifiedOn = ? where u.user = ? and u.branchId = ?"),
		@NamedQuery(name = "UserProfile.updateProfileByUserAndRegion", query = "update UserProfile u set u.status = ?, u.modifiedBy = ?, u.modifiedOn = ? where u.user = ? and u.regionId = ?") })
public class UserProfile implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USER_PROFILE_ID")
	private long userProfileId;

	@Column(name = "AGENT_ID")
	private long agentId;

	@Column(name = "BRANCH_ID")
	private long branchId;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private Timestamp createdOn;

	@Column(name = "EMAIL_ID")
	private String emailId;

	@Column(name = "IS_PROFILE_COMPLETE")
	private int isProfileComplete;

	@Column(name = "IS_PRIMARY")
	private int isPrimary;

	@Column(name = "PROFILE_COMPLETION_STAGE")
	private String profileCompletionStage;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private Timestamp modifiedOn;

	@Column(name = "REGION_ID")
	private long regionId;

	@Column(name = "STATUS")
	private int status;

	@Column(name = "USER_PROFILE_TYPE")
	private String userProfileType;

	// bi-directional many-to-one association to Company
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;

	// bi-directional many-to-one association to ProfilesMaster
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROFILES_MASTER_ID")
	private ProfilesMaster profilesMaster;

	// bi-directional many-to-one association to User
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private User user;

	public UserProfile() {}

	public long getUserProfileId() {
		return this.userProfileId;
	}

	public void setUserProfileId(long userProfileId) {
		this.userProfileId = userProfileId;
	}

	public long getAgentId() {
		return this.agentId;
	}

	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}

	public int getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(int isPrimary) {
		this.isPrimary = isPrimary;
	}

	public String getProfileCompletionStage() {
		return profileCompletionStage;
	}

	public void setProfileCompletionStage(String profileCompletionStage) {
		this.profileCompletionStage = profileCompletionStage;
	}

	public int getIsProfileComplete() {
		return isProfileComplete;
	}

	public void setIsProfileComplete(int isProfileComplete) {
		this.isProfileComplete = isProfileComplete;
	}

	public long getBranchId() {
		return this.branchId;
	}

	public void setBranchId(long branchId) {
		this.branchId = branchId;
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

	public String getEmailId() {
		return this.emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
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

	public long getRegionId() {
		return this.regionId;
	}

	public void setRegionId(long regionId) {
		this.regionId = regionId;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUserProfileType() {
		return this.userProfileType;
	}

	public void setUserProfileType(String userProfileType) {
		this.userProfileType = userProfileType;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public ProfilesMaster getProfilesMaster() {
		return this.profilesMaster;
	}

	public void setProfilesMaster(ProfilesMaster profilesMaster) {
		this.profilesMaster = profilesMaster;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}