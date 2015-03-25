package com.realtech.socialsurvey.core.entities;

/**
 * Branch entity from the search
 */
public class BranchFromSearch {

	private long branchId;
	private String branchName;
	private long regionId;
	private String regionName;
	private long companyId;
	private long isDefaultBySystem;
	private int status;

	public long getBranchId() {
		return branchId;
	}

	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

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
		return "branchId: " + branchId + "\tbranchName: " + branchName + "regionId: " + regionId + "\tregionName: " + regionName + "\t\tcompanyId: "
				+ companyId + "\tisDefaultBySystem: " + isDefaultBySystem + "\tstatus: " + status;
	}
}