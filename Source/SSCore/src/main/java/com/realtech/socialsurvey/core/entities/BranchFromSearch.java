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
	private String address;

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
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "BranchFromSearch [branchId=" + branchId + ", branchName=" + branchName + ", regionId=" + regionId + ", regionName=" + regionName
				+ ", companyId=" + companyId + ", isDefaultBySystem=" + isDefaultBySystem + ", status=" + status + ", address=" + address + "]";
	}

}