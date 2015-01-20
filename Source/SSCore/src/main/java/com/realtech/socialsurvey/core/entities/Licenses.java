package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class Licenses {

	private List<String> authorized_in;
	private String license_disclaimer;

	public List<String> getAuthorized_in() {
		return authorized_in;
	}

	public void setAuthorized_in(List<String> authorized_in) {
		this.authorized_in = authorized_in;
	}

	public String getLicense_disclaimer() {
		return license_disclaimer;
	}

	public void setLicense_disclaimer(String license_disclaimer) {
		this.license_disclaimer = license_disclaimer;
	}

	@Override
	public String toString() {
		return "authorized_in: " + (authorized_in != null ? authorized_in.toString() : "null") + "\t license_disclaimer: " + license_disclaimer;
	}
}
