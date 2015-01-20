package com.realtech.socialsurvey.core.enums;

import com.realtech.socialsurvey.core.exception.FatalException;

// RM-05
/*
 * Enum for identifying type of account.
 */
public enum AccountType {

	INDIVIDUAL("Individual", 1), TEAM("Team", 2), COMPANY("Company", 3), ENTERPRISE("Enterprise", 4);

	private String name;
	private long value;

	private AccountType(String name, long value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	/**
	 * Returns enum for the value provided
	 * 
	 * @param value
	 * @return
	 */
	public static AccountType getAccountType(long value) {
		for (AccountType accountType : values()) {
			if (accountType.value == (value)) {
				return accountType;
			}
		}
		throw new FatalException("Specified account type not found. account type: " + value);
	}
}
