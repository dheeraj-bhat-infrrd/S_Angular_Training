package com.realtech.socialsurvey.core.entities.integration;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

/**
 * Sends the processing status of each record processed
 */
public class EngagementProcessingStatus {

	private String engagementId;
	private int status;

	public String getEngagementId() {
		return engagementId;
	}

	public void setEngagementId(String engagementId) {
		this.engagementId = engagementId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public static void main(String[] args){
		EngagementProcessingStatus eps = new EngagementProcessingStatus();
		eps.setEngagementId("12345");
		eps.setStatus(1);
		List<EngagementProcessingStatus> epsList = new ArrayList<>();
		epsList.add(eps);
		epsList.add(eps);
		epsList.add(eps);
		System.out.println(new Gson().toJson(epsList));
	}

}
