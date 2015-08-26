package com.realtech.socialsurvey.core.enums;

/**
 * Holds the available settings in the application and the 
 *
 */
public enum SettingsForApplication {

	LOGO(1, 1), EMAIL_ADDRESS(10,2);
	
	private final long order; // the order is the not the index. 1 means units, 2 means tens decimal places and so on.
	private final int index;

	SettingsForApplication(long order, int index) {
		this.order = order;
		this.index = index;
	}

	public long getOrder() {
		return this.order;
	}
	
	public int getIndex(){
		return this.index;
	}
}
