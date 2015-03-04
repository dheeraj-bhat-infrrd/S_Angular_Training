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

@Entity
@Table(name = "RETRIED_TRANSACTIONS")
@NamedQuery(name = "RetriedTransaction.findAll", query = "SELECT r FROM RetriedTransaction r")
public class RetriedTransaction implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RETRY_ID")
	private long retryId;

	@Column(name = "TRANSACTION_ID")
	private String transactionId;

	// bi-directional many-to-one association to AccountsMaster
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LICENSE_ID")
	private LicenseDetail licenseDetail;

	@Column(name = "PAYMENT_TOKEN")
	private String paymentToken;

	@Column(name = "AMOUNT")
	private float amount;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private Timestamp createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private Timestamp modifiedOn;

	private int status;

	public RetriedTransaction() {

	}

	public long getRetryId() {
		return retryId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public String getPaymentToken() {
		return paymentToken;
	}

	public float getAmount() {
		return amount;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public Timestamp getModifiedOn() {
		return modifiedOn;
	}

	public int getStatus() {
		return status;
	}

	public void setRetryId(long retryId) {
		this.retryId = retryId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public LicenseDetail getLicenseDetail() {
		return licenseDetail;
	}

	public void setLicenseDetail(LicenseDetail licenseDetail) {
		this.licenseDetail = licenseDetail;
	}

	public void setPaymentToken(String paymentToken) {
		this.paymentToken = paymentToken;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
