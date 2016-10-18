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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the accounts_master database table.
 * 
 */
@Entity
@Table(name="ACCOUNTS_MASTER")
@NamedQuery(name="AccountsMaster.findAll", query="SELECT a FROM AccountsMaster a")
public class AccountsMaster implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ACCOUNTS_MASTER_ID")
    private int accountsMasterId;

    @Column(name="ACCOUNT_NAME")
    private String accountName;

    @Column(name="CREATED_BY")
    private String createdBy;

    @Column(name="CREATED_ON")
    private Timestamp createdOn;

    @Column(name="MAX_TIME_VALIDITY_ALLOWED_IN_DAYS")
    private int maxTimeValidityAllowedInDays;

    @Column(name="MAX_USERS_ALLOWED")
    private int maxUsersAllowed;
    
    @Column(name="MIN_USERS_ALLOWED")
    private int minUsersAllowed;

    @Column(name="AMOUNT")
    private float amount;

    @Column(name="MODIFIED_BY")
    private String modifiedBy;

    @Column(name="MODIFIED_ON")
    private Timestamp modifiedOn;

    private int status;

    //bi-directional many-to-one association to CompanyInvitationLicenseKey
    @OneToMany(mappedBy="accountsMaster",fetch = FetchType.LAZY)
    private List<CompanyInvitationLicenseKey> companyInvitationLicenseKeys;

    //bi-directional many-to-one association to LicenseDetail
    @OneToMany(mappedBy="accountsMaster",fetch = FetchType.LAZY)
    private List<LicenseDetail> licenseDetails;

    public AccountsMaster() {
    }

    public int getAccountsMasterId() {
        return this.accountsMasterId;
    }

    public void setAccountsMasterId(int accountsMasterId) {
        this.accountsMasterId = accountsMasterId;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
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

    public int getMaxTimeValidityAllowedInDays() {
        return this.maxTimeValidityAllowedInDays;
    }

    public void setMaxTimeValidityAllowedInDays(int maxTimeValidityAllowedInDays) {
        this.maxTimeValidityAllowedInDays = maxTimeValidityAllowedInDays;
    }

    public int getMaxUsersAllowed() {
        return this.maxUsersAllowed;
    }

    public void setMaxUsersAllowed(int maxUsersAllowed) {
        this.maxUsersAllowed = maxUsersAllowed;
    }
    
    public int getMinUsersAllowed()
    {
        return this.minUsersAllowed;
    }

    public void setMinUsersAllowed( int minUsersAllowed )
    {
        this.minUsersAllowed = minUsersAllowed;
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
    
    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public List<CompanyInvitationLicenseKey> getCompanyInvitationLicenseKeys() {
        return this.companyInvitationLicenseKeys;
    }

    public void setCompanyInvitationLicenseKeys(List<CompanyInvitationLicenseKey> companyInvitationLicenseKeys) {
        this.companyInvitationLicenseKeys = companyInvitationLicenseKeys;
    }

    public CompanyInvitationLicenseKey addCompanyInvitationLicenseKey(CompanyInvitationLicenseKey companyInvitationLicenseKey) {
        getCompanyInvitationLicenseKeys().add(companyInvitationLicenseKey);
        companyInvitationLicenseKey.setAccountsMaster(this);

        return companyInvitationLicenseKey;
    }

    public CompanyInvitationLicenseKey removeCompanyInvitationLicenseKey(CompanyInvitationLicenseKey companyInvitationLicenseKey) {
        getCompanyInvitationLicenseKeys().remove(companyInvitationLicenseKey);
        companyInvitationLicenseKey.setAccountsMaster(null);

        return companyInvitationLicenseKey;
    }

    public List<LicenseDetail> getLicenseDetails() {
        return this.licenseDetails;
    }

    public void setLicenseDetails(List<LicenseDetail> licenseDetails) {
        this.licenseDetails = licenseDetails;
    }

    public LicenseDetail addLicenseDetail(LicenseDetail licenseDetail) {
        getLicenseDetails().add(licenseDetail);
        licenseDetail.setAccountsMaster(this);

        return licenseDetail;
    }

    public LicenseDetail removeLicenseDetail(LicenseDetail licenseDetail) {
        getLicenseDetails().remove(licenseDetail);
        licenseDetail.setAccountsMaster(null);

        return licenseDetail;
    }

}