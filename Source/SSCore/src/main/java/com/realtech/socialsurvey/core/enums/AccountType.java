package com.realtech.socialsurvey.core.enums;

// RM-05
/*
 * Enum for identifying type of account.
 */
public enum AccountType {

	INDIVIDUAL("Individual", 1), TEAM("Team", 2), COMPANY("Company", 3), ENTERPRISE("Enterprise", 4);

	private String name;
	private int value;

	private AccountType(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
