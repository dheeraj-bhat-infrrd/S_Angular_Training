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
 * The persistent class for the COMPANY database table.
 */
@Entity
@Table(name = "COMPANY")
@NamedQuery(name = "Company.findAll", query = "SELECT c FROM Company c")
public class Company implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "COMPANY_ID")
	private long companyId;

	@Column(name = "COMPANY")
	private String company;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private Timestamp createdOn;

	@Column(name = "IS_REGISTRATION_COMPLETE")
	private int isRegistrationComplete;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private Timestamp modifiedOn;

	@Column(name = "REGISTRATION_STAGE")
	private String registrationStage;

	@Column(name = "STATUS")
	private int status;

	@Column(name = "BILLING_MODE")
	private String billingMode;

	@Column(name = "SETTINGS_LOCK_STATUS")
	private String settingsLockStatus;

	@Column(name = "SETTINGS_SET_STATUS")
	private String settingsSetStatus;

	@ManyToOne
	@JoinColumn(name = "VERTICAL_ID")
	private VerticalsMaster verticalsMaster;

	// bi-directional many-to-one association to Branch
	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
	private List<Branch> branches;

	// bi-directional many-to-one association to LicenseDetail
	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
	private List<LicenseDetail> licenseDetails;

	// bi-directional many-to-one association to OrganizationLevelSetting
	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
	private List<OrganizationLevelSetting> organizationLevelSettings;

	// bi-directional many-to-one association to Region
	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
	private List<Region> regions;

	// bi-directional many-to-one association to SurveyCompanyMapping
	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
	private List<SurveyCompanyMapping> surveyCompanyMappings;

	// bi-directional many-to-one association to UsercountModificationNotification
	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
	private List<UsercountModificationNotification> usercountModificationNotifications;

	// bi-directional many-to-one association to User
	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
	private List<User> users;

	// bi-directional many-to-one association to UserInvite
	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
	private List<UserInvite> userInvites;

	// bi-directional many-to-one association to UserProfile
	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
	private List<UserProfile> userProfiles;

	// bi-directional many-to-one association to DisabledAccount
	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
	private List<DisabledAccount> disabledAccounts;

	// bi-directional many-to-one association to RemovedUser
	@OneToMany(mappedBy = "company")
	private List<RemovedUser> removedUsers;

    @Column ( name = "IS_ZILLOW_CONNECTED")
    private int isZillowConnected;

    @Column ( name = "ZILLOW_REVIEW_COUNT")
    private int zillowReviewCount;

    @Column ( name = "ZILLOW_AVERAGE_SCORE")
    private double zillowAverageScore;
    
    
    // bi-directional many-to-one association to UserEmailMapping
    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<UserEmailMapping> userEmailMappings;
    
    // bi-directional many-to-one association to UserEmailMapping
    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<CompanyIgnoredEmailMapping> companyIgnoredEmailMappingList;
    

	public List<UserEmailMapping> getUserEmailMappings()
    {
        return userEmailMappings;
    }

    public void setUserEmailMappings( List<UserEmailMapping> userEmailMappings )
    {
        this.userEmailMappings = userEmailMappings;
    }

    public Company() {}

	public long getCompanyId() {
		return this.companyId;
	}

	public void setCompanyId(long companyId) {
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

	public int getIsRegistrationComplete() {
		return this.isRegistrationComplete;
	}

	public void setIsRegistrationComplete(int isRegistrationComplete) {
		this.isRegistrationComplete = isRegistrationComplete;
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

	public String getRegistrationStage() {
		return this.registrationStage;
	}

	public void setRegistrationStage(String registrationStage) {
		this.registrationStage = registrationStage;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getBillingMode() {
		return billingMode;
	}

	public void setBillingMode(String billingMode) {
		this.billingMode = billingMode;
	}

	public String getSettingsLockStatus() {
		return settingsLockStatus;
	}

	public void setSettingsLockStatus(String settingsLockStatus) {
		this.settingsLockStatus = settingsLockStatus;
	}

	public String getSettingsSetStatus() {
		return settingsSetStatus;
	}

	public void setSettingsSetStatus(String settingsSetStatus) {
		this.settingsSetStatus = settingsSetStatus;
	}

	public String getDisplayBillingMode() {

		String billingModeStr = "";

		if (this.billingMode.equals("A")) {
			billingModeStr = "Auto Debit";
		}
		else if (this.billingMode.equals("I")) {
			billingModeStr = "Invoice";
		}

		return billingModeStr;
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

	public VerticalsMaster getVerticalsMaster() {
		return verticalsMaster;
	}

	public List<DisabledAccount> getDisabledAccounts() {
		return disabledAccounts;
	}

	public void setVerticalsMaster(VerticalsMaster verticalsMaster) {
		this.verticalsMaster = verticalsMaster;
	}

	public void setDisabledAccounts(List<DisabledAccount> disabledAccounts) {
		this.disabledAccounts = disabledAccounts;
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

	public List<SurveyCompanyMapping> getSurveyCompanyMappings() {
		return this.surveyCompanyMappings;
	}

	public void setSurveyCompanyMappings(List<SurveyCompanyMapping> surveyCompanyMappings) {
		this.surveyCompanyMappings = surveyCompanyMappings;
	}

	public SurveyCompanyMapping addSurveyCompanyMapping(SurveyCompanyMapping surveyCompanyMapping) {
		getSurveyCompanyMappings().add(surveyCompanyMapping);
		surveyCompanyMapping.setCompany(this);

		return surveyCompanyMapping;
	}

	public SurveyCompanyMapping removeSurveyCompanyMapping(SurveyCompanyMapping surveyCompanyMapping) {
		getSurveyCompanyMappings().remove(surveyCompanyMapping);
		surveyCompanyMapping.setCompany(null);

		return surveyCompanyMapping;
	}

	public List<UsercountModificationNotification> getUsercountModificationNotifications() {
		return this.usercountModificationNotifications;
	}

	public void setUsercountModificationNotifications(List<UsercountModificationNotification> usercountModificationNotifications) {
		this.usercountModificationNotifications = usercountModificationNotifications;
	}

	public UsercountModificationNotification addUsercountModificationNotification(UsercountModificationNotification usercountModificationNotification) {
		getUsercountModificationNotifications().add(usercountModificationNotification);
		usercountModificationNotification.setCompany(this);

		return usercountModificationNotification;
	}

	public UsercountModificationNotification removeUsercountModificationNotification(
			UsercountModificationNotification usercountModificationNotification) {
		getUsercountModificationNotifications().remove(usercountModificationNotification);
		usercountModificationNotification.setCompany(null);

		return usercountModificationNotification;
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

	public List<RemovedUser> getRemovedUsers() {
		return this.removedUsers;
	}

	public void setRemovedUsers(List<RemovedUser> removedUsers) {
		this.removedUsers = removedUsers;
	}

	public RemovedUser addRemovedUser(RemovedUser removedUser) {
		getRemovedUsers().add(removedUser);
		removedUser.setCompany(this);

		return removedUser;
	}

	public RemovedUser removeRemovedUser(RemovedUser removedUser) {
		getRemovedUsers().remove(removedUser);
		removedUser.setCompany(null);

		return removedUser;
	}

    /**
     * @return the isZillowConnected
     */
    public int getIsZillowConnected()
    {
        return isZillowConnected;
    }

    /**
     * @param isZillowConnected the isZillowConnected to set
     */
    public void setIsZillowConnected( int isZillowConnected )
    {
        this.isZillowConnected = isZillowConnected;
    }

    /**
     * @return the zillowReviewCount
     */
    public int getZillowReviewCount()
    {
        return zillowReviewCount;
    }

    /**
     * @param zillowReviewCount the zillowReviewCount to set
     */
    public void setZillowReviewCount( int zillowReviewCount )
    {
        this.zillowReviewCount = zillowReviewCount;
    }

    /**
     * @return the zillowAverageScore
     */
    public double getZillowAverageScore()
    {
        return zillowAverageScore;
    }

    /**
     * @param zillowAverageScore the zillowAverageScore to set
     */
    public void setZillowAverageScore( double zillowAverageScore )
    {
        this.zillowAverageScore = zillowAverageScore;
    }

    public List<CompanyIgnoredEmailMapping> getCompanyIgnoredEmailMappingList()
    {
        return companyIgnoredEmailMappingList;
    }

    public void setCompanyIgnoredEmailMappingList( List<CompanyIgnoredEmailMapping> companyIgnoredEmailMappingList )
    {
        this.companyIgnoredEmailMappingList = companyIgnoredEmailMappingList;
    }
}