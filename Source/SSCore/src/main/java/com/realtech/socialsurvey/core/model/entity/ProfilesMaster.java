package com.realtech.socialsurvey.core.model.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the profiles_master database table.
 * 
 */
@Entity
@Table(name="profiles_master")
@NamedQuery(name="ProfilesMaster.findAll", query="SELECT p FROM ProfilesMaster p")
public class ProfilesMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="PROFILE_ID")
	private int profileId;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_ON")
	private Timestamp createdOn;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_ON")
	private Timestamp modifiedOn;

	private String profile;

	private int status;

	//bi-directional many-to-one association to UserInvite
	@OneToMany(mappedBy="profilesMaster")
	private List<UserInvite> userInvites;

	//bi-directional many-to-one association to UserProfile
	@OneToMany(mappedBy="profilesMaster")
	private List<UserProfile> userProfiles;

	public ProfilesMaster() {
	}

	public int getProfileId() {
		return this.profileId;
	}

	public void setProfileId(int profileId) {
		this.profileId = profileId;
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

	public String getProfile() {
		return this.profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<UserInvite> getUserInvites() {
		return this.userInvites;
	}

	public void setUserInvites(List<UserInvite> userInvites) {
		this.userInvites = userInvites;
	}

	public UserInvite addUserInvite(UserInvite userInvite) {
		getUserInvites().add(userInvite);
		userInvite.setProfilesMaster(this);

		return userInvite;
	}

	public UserInvite removeUserInvite(UserInvite userInvite) {
		getUserInvites().remove(userInvite);
		userInvite.setProfilesMaster(null);

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
		userProfile.setProfilesMaster(this);

		return userProfile;
	}

	public UserProfile removeUserProfile(UserProfile userProfile) {
		getUserProfiles().remove(userProfile);
		userProfile.setProfilesMaster(null);

		return userProfile;
	}

}