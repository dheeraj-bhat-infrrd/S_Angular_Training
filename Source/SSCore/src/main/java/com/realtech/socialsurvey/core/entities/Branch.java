package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the branch database table.
 * 
 */
@Entity
@Table(name="BRANCH")
@NamedQuery(name="Branch.findAll", query="SELECT b FROM Branch b")
public class Branch implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="BRANCH_ID")
	private long branchId;

	private String branch;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_ON")
	private Timestamp createdOn;

	@Column(name="IS_DEFAULT_BY_SYSTEM")
	private int isDefaultBySystem;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_ON")
	private Timestamp modifiedOn;

	private int status;
	
	@Transient
	private String address1;
	
	@Transient
	private String address2;

	//bi-directional many-to-one association to Company
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="COMPANY_ID")
	private Company company;

	//bi-directional many-to-one association to Region
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="REGION_ID")
	private Region region;

	public Branch() {
	}

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

	@Override
	public String toString() {
		return "Branch [branchId=" + branchId + ", branch=" + branch + ", isDefaultBySystem=" + isDefaultBySystem + ", status=" + status
				+ ", address1=" + address1 + ", address2=" + address2 + "]";
	}
	
}