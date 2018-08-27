package com.realtech.socialsurvey.core.entities.integration;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

/**
 * Holds the encompass credentials
 */
public class EncompassCredentials {

	private long companyId;
	private String apiKey;

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

}
