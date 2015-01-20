package com.realtech.socialsurvey.core.entities;

import java.util.Map;

/**
 * Holds the user settings
 */
public class UserSettings {

	private OrganizationUnitSettings companySettings;
	private Map<Long, OrganizationUnitSettings> regionSettings;
	private Map<Long, OrganizationUnitSettings> branchSettings;
	private Map<Long, AgentSettings> agentSettings;

	public OrganizationUnitSettings getCompanySettings() {
		return companySettings;
	}

	public void setCompanySettings(OrganizationUnitSettings companySettings) {
		this.companySettings = companySettings;
	}

	public Map<Long, OrganizationUnitSettings> getRegionSettings() {
		return regionSettings;
	}

	public void setRegionSettings(Map<Long, OrganizationUnitSettings> regionSettings) {
		this.regionSettings = regionSettings;
	}

	public Map<Long, OrganizationUnitSettings> getBranchSettings() {
		return branchSettings;
	}

	public void setBranchSettings(Map<Long, OrganizationUnitSettings> branchSettings) {
		this.branchSettings = branchSettings;
	}

	public Map<Long, AgentSettings> getAgentSettings() {
		return agentSettings;
	}

	public void setAgentSettings(Map<Long, AgentSettings> agentSettings) {
		this.agentSettings = agentSettings;
	}

}
