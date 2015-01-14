package com.realtech.socialsurvey.core.entities;

public class BranchSettings extends OrganizationUnitSettings {

	private String regionName;
	private long regionId;

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

}
