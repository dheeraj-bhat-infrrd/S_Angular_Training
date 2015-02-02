package com.realtech.socialsurvey.core.entities;

import java.util.List;
import com.google.gson.annotations.SerializedName;

/**
 * JIRA:SS-117 by RM02 
 * Wrapper class for company details
 */
public class CompanyProfile {

	private List<Region> regions;
	@SerializedName("company_details")
	private OrganizationUnitSettings companySettings;
	
	public List<Region> getRegions() {
		return regions;
	}
	public void setRegions(List<Region> regions) {
		this.regions = regions;
	}
	public OrganizationUnitSettings getCompanySettings() {
		return companySettings;
	}
	public void setCompanySettings(OrganizationUnitSettings companySettings) {
		this.companySettings = companySettings;
	}	
	
}
