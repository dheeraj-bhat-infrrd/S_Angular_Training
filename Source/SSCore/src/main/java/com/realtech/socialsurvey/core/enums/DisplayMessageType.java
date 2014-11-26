package com.realtech.socialsurvey.core.enums;

/**
 * Enum for identifying the type of message(eg. success or failure)
 */
public enum DisplayMessageType {

	ERROR_MESSAGE("error_message", 1), SUCCESS_MESSAGE("success_message", 2);

	private String name;
	private int value;

	private DisplayMessageType(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}
}
