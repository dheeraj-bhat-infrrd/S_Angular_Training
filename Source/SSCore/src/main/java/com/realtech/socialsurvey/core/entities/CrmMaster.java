package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * The persistent class for the crm_master database table.
 */
@Entity
@Table(name = "crm_master")
@NamedQuery(name = "CrmMaster.findAll", query = "SELECT c FROM CrmMaster c")
public class CrmMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CRM_MASTER_ID")
	private int crmMasterId;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private Timestamp createdOn;

	@Column(name = "CRM_NAME")
	private String crmName;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private Timestamp modifiedOn;

	private int status;

	// bi-directional many-to-one association to VerticalCrmMapping
	@OneToMany(mappedBy = "crmMaster")
	private List<VerticalCrmMapping> verticalCrmMappings;

	public CrmMaster() {}

	public int getCrmMasterId() {
		return this.crmMasterId;
	}

	public void setCrmMasterId(int crmMasterId) {
		this.crmMasterId = crmMasterId;
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

	public String getCrmName() {
		return this.crmName;
	}

	public void setCrmName(String crmName) {
		this.crmName = crmName;
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

	public List<VerticalCrmMapping> getVerticalCrmMappings() {
		return this.verticalCrmMappings;
	}

	public void setVerticalCrmMappings(List<VerticalCrmMapping> verticalCrmMappings) {
		this.verticalCrmMappings = verticalCrmMappings;
	}

	public VerticalCrmMapping addVerticalCrmMapping(VerticalCrmMapping verticalCrmMapping) {
		getVerticalCrmMappings().add(verticalCrmMapping);
		verticalCrmMapping.setCrmMaster(this);

		return verticalCrmMapping;
	}

	public VerticalCrmMapping removeVerticalCrmMapping(VerticalCrmMapping verticalCrmMapping) {
		getVerticalCrmMappings().remove(verticalCrmMapping);
		verticalCrmMapping.setCrmMaster(null);

		return verticalCrmMapping;
	}

}