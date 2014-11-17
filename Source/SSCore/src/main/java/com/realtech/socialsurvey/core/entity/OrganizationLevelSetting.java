package com.realtech.socialsurvey.core.entity;
//JIRA: SS-1: By RM06: BOC

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the organization_level_settings database table.
 * 
 */
@Entity
@Table(name="organization_level_settings")
@NamedQuery(name="OrganizationLevelSetting.findAll", query="SELECT o FROM OrganizationLevelSetting o")
public class OrganizationLevelSetting implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ORGANIZATION_LEVEL_SETTINGS_ID")
	private int organizationLevelSettingsId;

	@Column(name="AGENT_ID")
	private int agentId;

	@Column(name="BRANCH_ID")
	private int branchId;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_ON")
	private Timestamp createdOn;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_ON")
	private Timestamp modifiedOn;

	@Column(name="REGION_ID")
	private int regionId;

	@Column(name="SETTING_KEY")
	private String settingKey;

	@Column(name="SETTING_VALUE")
	private String settingValue;

	private int status;

	//bi-directional many-to-one association to Company
	@ManyToOne
	@JoinColumn(name="COMPANY_ID")
	private Company company;

	public OrganizationLevelSetting() {
	}

	public int getOrganizationLevelSettingsId() {
		return this.organizationLevelSettingsId;
	}

	public void setOrganizationLevelSettingsId(int organizationLevelSettingsId) {
		this.organizationLevelSettingsId = organizationLevelSettingsId;
	}

	public int getAgentId() {
		return this.agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}

	public int getBranchId() {
		return this.branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
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

	public int getRegionId() {
		return this.regionId;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	public String getSettingKey() {
		return this.settingKey;
	}

	public void setSettingKey(String settingKey) {
		this.settingKey = settingKey;
	}

	public String getSettingValue() {
		return this.settingValue;
	}

	public void setSettingValue(String settingValue) {
		this.settingValue = settingValue;
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

}

//JIRA: SS-1: By RM06: EOC