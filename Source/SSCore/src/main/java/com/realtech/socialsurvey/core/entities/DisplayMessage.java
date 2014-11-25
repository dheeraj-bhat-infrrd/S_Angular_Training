package com.realtech.socialsurvey.core.entities;

import com.realtech.socialsurvey.core.enums.DisplayMessageType;

/**
 * Entity for displaying message to the user
 */
public class DisplayMessage {

	private String message;
	private DisplayMessageType type;

	public DisplayMessage(String message, DisplayMessageType type) {
		this.message = message;
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public DisplayMessageType getType() {
		return type;
	}

	public void setType(DisplayMessageType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return getMessage();
	}
}
