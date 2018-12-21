package com.realtech.socialsurvey.core.entities;

public class DisplayData {

	private String displayName;
	private long displayOrder;
	private boolean defaultSet;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public long getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(long displayOrder) {
		this.displayOrder = displayOrder;
	}

	public boolean isDefaultSet() {
		return defaultSet;
	}

	public void setDefaultSet(boolean defaultSet) {
		this.defaultSet = defaultSet;
	}

}
