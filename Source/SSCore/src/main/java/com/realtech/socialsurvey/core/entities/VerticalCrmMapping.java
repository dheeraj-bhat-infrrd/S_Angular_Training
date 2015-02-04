package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the vertical_crm_mapping database table.
 * 
 */
@Entity
@Table(name="vertical_crm_mapping")
@NamedQuery(name="VerticalCrmMapping.findAll", query="SELECT v FROM VerticalCrmMapping v")
public class VerticalCrmMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="VERTICAL_CRM_MAPPING_ID")
	private int verticalCrmMappingId;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_ON")
	private Timestamp createdOn;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_ON")
	private Timestamp modifiedOn;

	private int status;

	//bi-directional many-to-one association to CrmMaster
	@ManyToOne
	@JoinColumn(name="CRM_ID")
	private CrmMaster crmMaster;

	//bi-directional many-to-one association to VerticalsMaster
	@ManyToOne
	@JoinColumn(name="VERTICAL_ID")
	private VerticalsMaster verticalsMaster;

	public VerticalCrmMapping() {
	}

	public int getVerticalCrmMappingId() {
		return this.verticalCrmMappingId;
	}

	public void setVerticalCrmMappingId(int verticalCrmMappingId) {
		this.verticalCrmMappingId = verticalCrmMappingId;
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

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public CrmMaster getCrmMaster() {
		return this.crmMaster;
	}

	public void setCrmMaster(CrmMaster crmMaster) {
		this.crmMaster = crmMaster;
	}

	public VerticalsMaster getVerticalsMaster() {
		return this.verticalsMaster;
	}

	public void setVerticalsMaster(VerticalsMaster verticalsMaster) {
		this.verticalsMaster = verticalsMaster;
	}

}