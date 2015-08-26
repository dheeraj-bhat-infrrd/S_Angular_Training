package com.realtech.socialsurvey.core.enums;

/**
 * Holds the available settings in the application and the 
 *
 */
public enum AvailableSettings {

	LOGO(1);
	
	private final int order; // the order is the not the index. 1 means units, 2 means tens decimal places and so on. 

	AvailableSettings(int order) {
		this.order = order;
	}

	public int getOrder() {
		return this.order;
	}
}
