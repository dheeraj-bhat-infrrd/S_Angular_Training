package com.realtech.socialsurvey.core.enums;

public enum EmailHeader {

	REGISTRATION("registration"), RESET_PASSWORD("reset_password"), REGISTRATION_COMPLETE("registration_complete"), SUBSCRIPTION_CHARGE_UNSUCESSFUL("subscription_charge_unsucessful"),
	VERFICATION("verification"), RETRY_CHARGE("retry_charge"), RETRY_EXHAUSTED("retry_exhausted"), ACCOUNT_DISABLED("account_disabled"), ACCOUNT_UPGRADE("account_upgrade");
	
	String name;
	
	private EmailHeader(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
