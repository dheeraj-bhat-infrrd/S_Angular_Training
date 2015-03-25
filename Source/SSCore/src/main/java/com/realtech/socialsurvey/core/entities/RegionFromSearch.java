package com.realtech.socialsurvey.core.entities;

/**
 * Region entity from the search
 */
public class RegionFromSearch {

	private long regionId;
	private String regionName;
	private long companyId;
	private long isDefaultBySystem;
	private int status;

	public long getRegionId() {
		return regionId;
	}

	public void setRegionId(long regionId) {
		this.regionId = regionId;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public long getIsDefaultBySystem() {
		return isDefaultBySystem;
	}

	public void setIsDefaultBySystem(long isDefaultBySystem) {
		this.isDefaultBySystem = isDefaultBySystem;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "regionId: " + regionId + "\tregionName: " + regionName + "\tcompanyId: " + companyId + "\tisDefaultBySystem: " + isDefaultBySystem
				+ "\tstatus: " + status;
	}
}