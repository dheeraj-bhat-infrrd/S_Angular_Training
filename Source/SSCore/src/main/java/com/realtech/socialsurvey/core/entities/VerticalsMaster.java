package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the verticals_master database table.
 * 
 */
@Entity
@Table(name="verticals_master")
@NamedQuery(name="VerticalsMaster.findAll", query="SELECT v FROM VerticalsMaster v")
public class VerticalsMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="VERTICALS_MASTER_ID")
	private int verticalsMasterId;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_ON")
	private Timestamp createdOn;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_ON")
	private Timestamp modifiedOn;

	private int status;

	@Column(name="VERTICAL_NAME")
	private String verticalName;

	//bi-directional many-to-one association to VerticalsCrmMapping
	@OneToMany(mappedBy="verticalsMaster")
	private List<VerticalsCrmMapping> verticalsCrmMappings;

	public VerticalsMaster() {
	}

	public int getVerticalsMasterId() {
		return this.verticalsMasterId;
	}

	public void setVerticalsMasterId(int verticalsMasterId) {
		this.verticalsMasterId = verticalsMasterId;
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

	public String getVerticalName() {
		return this.verticalName;
	}

	public void setVerticalName(String verticalName) {
		this.verticalName = verticalName;
	}

	public List<VerticalsCrmMapping> getVerticalsCrmMappings() {
		return this.verticalsCrmMappings;
	}

	public void setVerticalsCrmMappings(List<VerticalsCrmMapping> verticalsCrmMappings) {
		this.verticalsCrmMappings = verticalsCrmMappings;
	}

	public VerticalsCrmMapping addVerticalsCrmMapping(VerticalsCrmMapping verticalsCrmMapping) {
		getVerticalsCrmMappings().add(verticalsCrmMapping);
		verticalsCrmMapping.setVerticalsMaster(this);

		return verticalsCrmMapping;
	}

	public VerticalsCrmMapping removeVerticalsCrmMapping(VerticalsCrmMapping verticalsCrmMapping) {
		getVerticalsCrmMappings().remove(verticalsCrmMapping);
		verticalsCrmMapping.setVerticalsMaster(null);

		return verticalsCrmMapping;
	}

}