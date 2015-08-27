package com.realtech.socialsurvey.core.enums;

/**
 * Holds the available settings in the application and the
 */
public enum SettingsForApplication {

	LOGO(1l, 1), ADDRESS(10l, 2), PHONE(100l, 3), LOCATION(1000l, 4), FACEBOOK(10000l, 5), TWITTER(100000l, 6), LINKED_IN(1000000l, 7), GOOGLE_PLUS(
			10000000l, 8), YELP(100000000l, 9), ZILLOW(1000000000l, 10), REALTOR(10000000000l, 11);

	private final long order;
	private final int index; // the order is the not the index. 1 means units, 2 means tens decimal
								// places and so on.

	SettingsForApplication(long order, int index) {
		this.order = order;
		this.index = index;
	}

	public long getOrder() {
		return this.order;
	}

	public int getIndex() {
		return this.index;
	}
}
