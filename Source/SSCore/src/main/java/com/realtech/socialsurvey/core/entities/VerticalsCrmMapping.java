package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the verticals_crm_mapping database table.
 * 
 */
@Entity
@Table(name="verticals_crm_mapping")
@NamedQuery(name="VerticalsCrmMapping.findAll", query="SELECT v FROM VerticalsCrmMapping v")
public class VerticalsCrmMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="VERTICAL_MAPPING_ID")
	private int verticalMappingId;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_ON")
	private Timestamp createdOn;

	@Column(name="CRM_SOURCE")
	private String crmSource;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_ON")
	private Timestamp modifiedOn;

	private int status;

	//bi-directional many-to-one association to VerticalsMaster
	@ManyToOne
	@JoinColumn(name="VERTICAL_ID")
	private VerticalsMaster verticalsMaster;

	public VerticalsCrmMapping() {
	}

	public int getVerticalMappingId() {
		return this.verticalMappingId;
	}

	public void setVerticalMappingId(int verticalMappingId) {
		this.verticalMappingId = verticalMappingId;
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

	public String getCrmSource() {
		return this.crmSource;
	}

	public void setCrmSource(String crmSource) {
		this.crmSource = crmSource;
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

	public VerticalsMaster getVerticalsMaster() {
		return this.verticalsMaster;
	}

	public void setVerticalsMaster(VerticalsMaster verticalsMaster) {
		this.verticalsMaster = verticalsMaster;
	}

}