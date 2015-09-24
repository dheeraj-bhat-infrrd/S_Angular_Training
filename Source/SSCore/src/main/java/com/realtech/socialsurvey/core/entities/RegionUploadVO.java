package com.realtech.socialsurvey.core.entities;

/*
 * The view class for Region
 */
public class RegionUploadVO {

	private long regionId;
	private String sourceRegionId;
	private String regionName;
	private String regionAddress1;
	private String regionAddress2;
	private String regionCountry;
	private String regionCountryCode;
	private String regionState;
	private String regionCity;
	private String regionZipcode;
	private boolean isAddressSet;

	public long getRegionId() {
		return regionId;
	}

	public void setRegionId(long regionId) {
		this.regionId = regionId;
	}

	public String getSourceRegionId() {
		return sourceRegionId;
	}

	public void setSourceRegionId(String sourceRegionId) {
		this.sourceRegionId = sourceRegionId;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getRegionAddress1() {
		return regionAddress1;
	}

	public void setRegionAddress1(String regionAddress1) {
		this.regionAddress1 = regionAddress1;
	}

	public String getRegionAddress2() {
		return regionAddress2;
	}

	public void setRegionAddress2(String regionAddress2) {
		this.regionAddress2 = regionAddress2;
	}

	public String getRegionCountry() {
		return regionCountry;
	}

	public void setRegionCountry(String regionCountry) {
		this.regionCountry = regionCountry;
	}

	public String getRegionCountryCode() {
		return regionCountryCode;
	}

	public void setRegionCountryCode(String regionCountryCode) {
		this.regionCountryCode = regionCountryCode;
	}

	public String getRegionCity() {
		return regionCity;
	}

	public void setRegionCity(String regionCity) {
		this.regionCity = regionCity;
	}

	public String getRegionState() {
		return regionState;
	}

	public void setRegionState(String regionState) {
		this.regionState = regionState;
	}

	public String getRegionZipcode() {
		return regionZipcode;
	}

	public void setRegionZipcode(String regionZipcode) {
		this.regionZipcode = regionZipcode;
	}

	public boolean isAddressSet() {
		return isAddressSet;
	}

	public void setAddressSet(boolean isAddressSet) {
		this.isAddressSet = isAddressSet;
	}

}
