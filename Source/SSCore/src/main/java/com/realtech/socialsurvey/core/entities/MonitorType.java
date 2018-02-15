package com.realtech.socialsurvey.core.entities;

public enum MonitorType {
	
	KEYWORD_MONITOR(0), GOOGLE_ALERTS(1);

	private int value;

	MonitorType(int value){
        this.value = value;
    }

	public int getValue() {
		return value;
	}
}
