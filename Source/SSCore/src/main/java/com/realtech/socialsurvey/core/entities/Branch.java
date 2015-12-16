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
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the branch database table.
 */
@Entity
@Table(name = "BRANCH")
@NamedQuery(name = "Branch.findAll", query = "SELECT b FROM Branch b")
public class Branch implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BRANCH_ID")
	private long branchId;

	@Column(name = "BRANCH")
	private String branch;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private Timestamp createdOn;

	@Column(name = "IS_DEFAULT_BY_SYSTEM")
	private int isDefaultBySystem;

	@Column(name = "SETTINGS_LOCK_STATUS")
	private String settingsLockStatus;

	@Column(name = "SETTINGS_SET_STATUS")
	private String settingsSetStatus;

	@Column(name = "PROFILE_NAME")
	private String profileName;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private Timestamp modifiedOn;

	@Column(name = "STATUS")
	private int status;

    @Column ( name = "IS_ZILLOW_CONNECTED")
    private int isZillowConnected;

    @Column ( name = "ZILLOW_REVIEW_COUNT")
    private int zillowReviewCount;

    @Column ( name = "ZILLOW_AVERAGE_SCORE")
    private double zillowAverageScore;

	@Transient
	private String branchName;

	@Transient
	private String address1;

	@Transient
	private String address2;

	@Transient
	private String country;

	@Transient
	private String countryCode;

	@Transient
	private String state;

	@Transient
	private String city;

	@Transient
	private String zipcode;

	// bi-directional many-to-one association to Company
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;

	// bi-directional many-to-one association to Region
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REGION_ID")
	private Region region;

	public Branch() {}

	public long getBranchId() {
		return this.branchId;
	}

	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}

	public String getBranch() {
		return this.branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
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

	public int getIsDefaultBySystem() {
		return this.isDefaultBySystem;
	}

	public void setIsDefaultBySystem(int isDefaultBySystem) {
		this.isDefaultBySystem = isDefaultBySystem;
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

	public Region getRegion() {
		return this.region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
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

    @Override
	public String toString() {
		return "Branch [branchId=" + branchId + ", branch=" + branch + ", createdBy=" + createdBy + ", createdOn=" + createdOn
				+ ", isDefaultBySystem=" + isDefaultBySystem + ", modifiedBy=" + modifiedBy + ", modifiedOn=" + modifiedOn + ", status=" + status
				+ ", address1=" + address1 + ", address2=" + address2 + "]";
	}
}