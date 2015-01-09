package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the users database table.
 */
@Entity
@Table(name = "USERS")
@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USER_ID")
	private long userId;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private Timestamp createdOn;

	@Column(name = "FIRST_NAME")
	private String firstName;

	@Column(name = "LAST_NAME")
	private String lastName;

	@Column(name = "EMAIL_ID")
	private String emailId;

	@Column(name = "IS_ATLEAST_ONE_USERPROFILE_COMPLETE")
	private int isAtleastOneUserprofileComplete;

	@Column(name = "IS_OWNER")
	private int isOwner;

	private boolean agent;

	private boolean branchAdmin;

	private boolean regionAdmin;

	private boolean companyAdmin;

	public int getIsOwner() {
		return isOwner;
	}

	public void setIsOwner(int isOwner) {
		this.isOwner = isOwner;
	}

	@Column(name = "LAST_LOGIN")
	private Timestamp lastLogin;

	@Column(name = "LOGIN_NAME")
	private String loginName;

	@Column(name = "LOGIN_PASSWORD")
	private String loginPassword;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private Timestamp modifiedOn;

	private String source;

	@Column(name = "SOURCE_USER_ID")
	private int sourceUserId;

	private int status;

	// bi-directional many-to-one association to UserProfile
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<UserProfile> userProfiles;

	// bi-directional many-to-one association to Company
	@ManyToOne
	@JoinColumn(name = "COMPANY_ID")
	private Company company;

	// bi-directional many-to-one association to RemovedUser
	@OneToMany(mappedBy = "user")
	private List<RemovedUser> removedUsers;

	public User() {}

	public long getUserId() {
		return this.userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
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
		return this.emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public Timestamp getLastLogin() {
		return this.lastLogin;
	}

	public void setLastLogin(Timestamp lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getLoginName() {
		return this.loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public int getIsAtleastOneUserprofileComplete() {
		return isAtleastOneUserprofileComplete;
	}

	public void setIsAtleastOneUserprofileComplete(int isAtleastOneUserprofileComplete) {
		this.isAtleastOneUserprofileComplete = isAtleastOneUserprofileComplete;
	}

	public String getLoginPassword() {
		return this.loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
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

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getSourceUserId() {
		return this.sourceUserId;
	}

	public void setSourceUserId(int sourceUserId) {
		this.sourceUserId = sourceUserId;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<UserProfile> getUserProfiles() {
		return this.userProfiles;
	}

	public void setUserProfiles(List<UserProfile> userProfiles) {
		this.userProfiles = userProfiles;
	}

	public UserProfile addUserProfile(UserProfile userProfile) {
		getUserProfiles().add(userProfile);
		userProfile.setUser(this);

		return userProfile;
	}

	public UserProfile removeUserProfile(UserProfile userProfile) {
		getUserProfiles().remove(userProfile);
		userProfile.setUser(null);

		return userProfile;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public boolean isAgent() {
		return agent;
	}

	public void setAgent(boolean agent) {
		this.agent = agent;
	}

	public boolean isBranchAdmin() {
		return branchAdmin;
	}

	public void setBranchAdmin(boolean branchAdmin) {
		this.branchAdmin = branchAdmin;
	}

	public boolean isRegionAdmin() {
		return regionAdmin;
	}

	public void setRegionAdmin(boolean regionAdmin) {
		this.regionAdmin = regionAdmin;
	}

	public boolean isCompanyAdmin() {
		return companyAdmin;
	}

	public void setCompanyAdmin(boolean companyAdmin) {
		this.companyAdmin = companyAdmin;
	}

	public List<RemovedUser> getRemovedUsers() {
		return this.removedUsers;
	}

	public void setRemovedUsers(List<RemovedUser> removedUsers) {
		this.removedUsers = removedUsers;
	}

	public RemovedUser addRemovedUser(RemovedUser removedUser) {
		getRemovedUsers().add(removedUser);
		removedUser.setUser(this);

		return removedUser;
	}

	public RemovedUser removeRemovedUser(RemovedUser removedUser) {
		getRemovedUsers().remove(removedUser);
		removedUser.setUser(null);

		return removedUser;
	}
}