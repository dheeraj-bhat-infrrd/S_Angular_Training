package com.realtech.socialsurvey.core.enums;

import com.realtech.socialsurvey.core.exception.FatalException;

// RM-05
/*
 * Enum for identifying type of account.
 */
public enum AccountType {

	FREE("Free Account",5), INDIVIDUAL("Individual", 1), TEAM("Team", 2), COMPANY("Company", 3), ENTERPRISE("Enterprise", 4);

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

	/**
	 * Returns enum for the value provided
	 * 
	 * @param value
	 * @return
	 */
	public static AccountType getAccountType(int value) {
		for (AccountType accountType : values()) {
			if (accountType.value == (value)) {
				return accountType;
			}
		}
		throw new FatalException("Specified account type not found. account type: " + value);
	}
}
