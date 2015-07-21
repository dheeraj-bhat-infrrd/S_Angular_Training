package com.realtech.socialsurvey.core.entities;

import java.util.Map;

/**
 * Holds the user hierarchy assigments
 */
public class UserHierarchyAssignments {

	private Map<Long, String> companies;
	private Map<Long, String> regions;
	private Map<Long, String> branches;
	private Map<Long, String> agents;

	public Map<Long, String> getCompanies() {
		return companies;
	}

	public void setCompanies(Map<Long, String> companies) {
		this.companies = companies;
	}

	public Map<Long, String> getRegions() {
		return regions;
	}

	public void setRegions(Map<Long, String> regions) {
		this.regions = regions;
	}

	public Map<Long, String> getBranches() {
		return branches;
	}

	public void setBranches(Map<Long, String> branches) {
		this.branches = branches;
	}

	public Map<Long, String> getAgents() {
		return agents;
	}

	public void setAgents(Map<Long, String> agents) {
		this.agents = agents;
	}
}