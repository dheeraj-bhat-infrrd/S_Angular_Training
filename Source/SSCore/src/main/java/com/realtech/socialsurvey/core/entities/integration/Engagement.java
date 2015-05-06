package com.realtech.socialsurvey.core.entities.integration;

import java.util.List;


/**
 * Holds the customer's and agent's business engagement details.
 */
public class Engagement {

	private String engagementId;
	private long companyId;
	private String source;
	private List<Customer> customers;
	private Agent agent;
	private long engagementStartTime;
	private long engagementEndTime;

	public String getEngagementId() {
		return engagementId;
	}

	public void setEngagementId(String engagementId) {
		this.engagementId = engagementId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<Customer> getCustomer() {
		return customers;
	}

	public void setCustomer(List<Customer> customers) {
		this.customers = customers;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public long getEngagementStartTime() {
		return engagementStartTime;
	}

	public void setEngagementStartTime(long engagementStartTime) {
		this.engagementStartTime = engagementStartTime;
	}

	public long getEngagementEndTime() {
		return engagementEndTime;
	}

	public void setEngagementEndTime(long engagementEndTime) {
		this.engagementEndTime = engagementEndTime;
	}
	
}
