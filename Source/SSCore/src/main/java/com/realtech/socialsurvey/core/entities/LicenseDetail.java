package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the license_details database table.
 * 
 */
@Entity
@Table(name="LICENSE_DETAILS")
@NamedQuery(name="LicenseDetail.findAll", query="SELECT l FROM LicenseDetail l")
public class LicenseDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="LICENSE_ID")
	private int licenseId;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_ON")
	private Timestamp createdOn;

	@Column(name="LICENSE_END_DATE")
	private Timestamp licenseEndDate;

	@Column(name="LICENSE_START_DATE")
	private Timestamp licenseStartDate;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_ON")
	private Timestamp modifiedOn;

	@Column(name="PAYMENT_MODE")
	private String paymentMode;
	
	@Column(name="NEXT_RETRY_TIME")
	private Timestamp nextRetryTime;

	@Column(name="PAYMENT_RETRIES")
	private int paymentRetries;
	
	private int status;

	//bi-directional many-to-one association to AccountsMaster
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ACCOUNTS_MASTER_ID")
	private AccountsMaster accountsMaster;

	//bi-directional many-to-one association to Company
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="COMPANY_ID")
	private Company company;

	public LicenseDetail() {
	}

	public int getLicenseId() {
		return this.licenseId;
	}

	public void setLicenseId(int licenseId) {
		this.licenseId = licenseId;
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

	public Timestamp getLicenseEndDate() {
		return this.licenseEndDate;
	}

	public void setLicenseEndDate(Timestamp licenseEndDate) {
		this.licenseEndDate = licenseEndDate;
	}

	public Timestamp getLicenseStartDate() {
		return this.licenseStartDate;
	}

	public void setLicenseStartDate(Timestamp licenseStartDate) {
		this.licenseStartDate = licenseStartDate;
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

	public String getPaymentMode() {
		return this.paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public Timestamp getNextRetryTime() {
		return nextRetryTime;
	}

	public void setNextRetryTime(Timestamp nextRetryTime) {
		this.nextRetryTime = nextRetryTime;
	}

	public int getPaymentRetries() {
		return paymentRetries;
	}

	public void setPaymentRetries(int paymentRetries) {
		this.paymentRetries = paymentRetries;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public AccountsMaster getAccountsMaster() {
		return this.accountsMaster;
	}

	public void setAccountsMaster(AccountsMaster accountsMaster) {
		this.accountsMaster = accountsMaster;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}