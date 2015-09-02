package com.realtech.socialsurvey.core.enums;

/**
 * Holds the available settings in the application and the
 */
public enum SettingsForApplication {

	LOGO(1l, 1), ADDRESS(10l, 2), PHONE(100l, 3), LOCATION(1000l, 4), FACEBOOK(10000l, 5), TWITTER(100000l, 6), LINKED_IN(1000000l, 7), GOOGLE_PLUS(
			10000000l, 8), YELP(100000000l, 9), ZILLOW(1000000000l, 10), REALTOR(10000000000l, 11), LENDING_TREE(100000000000l, 12);

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
	
	public static SettingsForApplication getSettingForApplicationFromIndex(int index){
		if(index == 1){
			return LOGO;
		}else if(index == 2){
			return ADDRESS;
		}else if(index == 3){
			return PHONE;
		}else if(index == 4){
			return LOCATION;
		}else if(index == 5){
			return FACEBOOK;
		}else if(index == 6){
			return TWITTER;
		}else if(index == 7){
			return LINKED_IN;
		}else if(index == 8){
			return GOOGLE_PLUS;
		}else if(index == 9){
			return YELP;
		}else if(index == 10){
			return ZILLOW;
		}else if(index == 11){
			return REALTOR;
		}else if(index == 12){
			return LENDING_TREE;
		}else{
			return null;
		}
	}
}
