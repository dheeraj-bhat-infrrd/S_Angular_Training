package com.realtech.socialsurvey.core.entities;

/**
 * Holds the CRM information
 */
public class CRMInfo {

	private String crm_source;
	private String crm_username;
	private String crm_password;
	private boolean connection_successful;

	public String getCrm_source() {
		return crm_source;
	}

	public void setCrm_source(String crm_source) {
		this.crm_source = crm_source;
	}

	public String getCrm_username() {
		return crm_username;
	}

	public void setCrm_username(String crm_username) {
		this.crm_username = crm_username;
	}

	public String getCrm_password() {
		return crm_password;
	}

	public void setCrm_password(String crm_password) {
		this.crm_password = crm_password;
	}

	public boolean isConnection_successful() {
		return connection_successful;
	}

	public void setConnection_successful(boolean connection_successful) {
		this.connection_successful = connection_successful;
	}

}
