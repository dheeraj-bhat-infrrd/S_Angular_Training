package com.realtech.socialsurvey.core.entities;

public class Location {

	private Country country;
	private String name;

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
