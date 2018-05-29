package com.realtech.socialsurvey.core.enums;

public enum TextActionType {

	SEND_EMAIL(0), PRIVATE_NOTE(1);

	private int value;

	TextActionType(int value){
        this.value = value;
    }

	public int getValue() {
		return value;
	}
}
