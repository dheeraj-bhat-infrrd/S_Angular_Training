package com.realtech.socialsurvey.core.entities;

public class BranchSettings {

	private OrganizationUnitSettings organizationUnitSettings;
	private String regionName;
	private long regionId;

	
	public OrganizationUnitSettings getOrganizationUnitSettings() {
		return organizationUnitSettings;
	}

	public void setOrganizationUnitSettings(OrganizationUnitSettings organizationUnitSettings) {
		this.organizationUnitSettings = organizationUnitSettings;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public long getRegionId() {
		return regionId;
	}

	public void setRegionId(long regionId) {
		this.regionId = regionId;
	}

	@Override
	public String toString() {
		return "BranchSettings [organizationUnitSettings=" + organizationUnitSettings + ", regionName=" + regionName + ", regionId=" + regionId + "]";
	}
	
	

}
