package com.realtech.socialsurvey.core.entities.integration;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

/**
 * Wraps the engagement details
 */
public class EngagementWrapper {

	private List<Engagement> engagements;
	private long nextRecord = -1;

	public List<Engagement> getEngagements() {
		return engagements;
	}

	public void setEngagements(List<Engagement> engagements) {
		this.engagements = engagements;
	}

	public long getNextRecord() {
		return nextRecord;
	}

	public void setNextRecord(long nextRecord) {
		this.nextRecord = nextRecord;
	}
	
	public static void main(String[] args){
		Customer customer = new Customer();
		customer.setFirstName("Customer");
		customer.setLastName("A");
		customer.setName("Customer A");
		customer.setEmailId("customer@example.com");
		Agent agent = new Agent();
		agent.setAgentFirstName("Agent");
		agent.setAgentLastName("A");
		agent.setAgentName("Agent A");
		agent.setAgentEmailId("agent@example.com");
		Engagement engagement = new Engagement();
		List<Customer> customers = new ArrayList<>();
		customers.add(customer);
		customers.add(customer);
		customers.add(customer);
		engagement.setCustomers(customers);
		engagement.setAgent(agent);
		engagement.setEngagementId("1234");
		engagement.setCompanyId(1l);
		engagement.setEngagementStartTime(System.currentTimeMillis());
		engagement.setEngagementEndTime(System.currentTimeMillis());
		engagement.setSource("encompass");
		List<Engagement> engagements = new ArrayList<>();
		engagements.add(engagement);
		engagements.add(engagement);
		engagements.add(engagement);
		EngagementWrapper wrapper = new EngagementWrapper();
		wrapper.setEngagements(engagements);
		wrapper.setNextRecord(4l);
		String json = new Gson().toJson(wrapper);
	}

}
