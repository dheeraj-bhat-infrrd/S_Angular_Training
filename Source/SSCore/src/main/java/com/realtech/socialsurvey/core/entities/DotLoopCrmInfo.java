package com.realtech.socialsurvey.core.entities;

public class DotLoopCrmInfo extends CRMInfo {

	private String api;

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	@Override
	public String toString() {
		return "api : " + api;
	}

}
