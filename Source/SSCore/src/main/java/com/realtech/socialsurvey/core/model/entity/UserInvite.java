package com.realtech.socialsurvey.core.model.entity;
//JIRA: SS-1: By RM06: BOC

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the user_invite database table.
 * 
 */
@Entity
@Table(name="user_invite")
@NamedQuery(name="UserInvite.findAll", query="SELECT u FROM UserInvite u")
public class UserInvite implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="USER_INVITE_ID")
	private int userInviteId;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_ON")
	private Timestamp createdOn;

	@Column(name="INVITATION_EMAIL_ID")
	private String invitationEmailId;

	@Column(name="INVITATION_KEY")
	private String invitationKey;

	@Column(name="INVITATION_SENT_BY")
	private int invitationSentBy;

	@Column(name="INVITATION_TIME")
	private Timestamp invitationTime;

	@Column(name="INVITATION_VALID_UNTIL")
	private Timestamp invitationValidUntil;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_ON")
	private Timestamp modifiedOn;

	private int status;

	//bi-directional many-to-one association to Company
	@ManyToOne
	@JoinColumn(name="COMPANY_ID")
	private Company company;

	//bi-directional many-to-one association to ProfilesMaster
	@ManyToOne
	@JoinColumn(name="PROFILE_MASTERS_ID")
	private ProfilesMaster profilesMaster;

	public UserInvite() {
	}

	public int getUserInviteId() {
		return this.userInviteId;
	}

	public void setUserInviteId(int userInviteId) {
		this.userInviteId = userInviteId;
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

	public String getInvitationEmailId() {
		return this.invitationEmailId;
	}

	public void setInvitationEmailId(String invitationEmailId) {
		this.invitationEmailId = invitationEmailId;
	}

	public String getInvitationKey() {
		return this.invitationKey;
	}

	public void setInvitationKey(String invitationKey) {
		this.invitationKey = invitationKey;
	}

	public int getInvitationSentBy() {
		return this.invitationSentBy;
	}

	public void setInvitationSentBy(int invitationSentBy) {
		this.invitationSentBy = invitationSentBy;
	}

	public Timestamp getInvitationTime() {
		return this.invitationTime;
	}

	public void setInvitationTime(Timestamp invitationTime) {
		this.invitationTime = invitationTime;
	}

	public Timestamp getInvitationValidUntil() {
		return this.invitationValidUntil;
	}

	public void setInvitationValidUntil(Timestamp invitationValidUntil) {
		this.invitationValidUntil = invitationValidUntil;
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

}

//JIRA: SS-1: By RM06: EOC