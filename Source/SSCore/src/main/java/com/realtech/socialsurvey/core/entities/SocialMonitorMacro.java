package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.Date;

public class SocialMonitorMacro implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String macroId;
	private String macroName;
	private String description;
	private Actions actions;
	private boolean active;
	private int count;
	private long createdOn;

	public String getMacroId() {
		return macroId;
	}

	public void setMacroId(String macroId) {
		this.macroId = macroId;
	}

	public String getMacroName() {
		return macroName;
	}

	public void setMacroName(String macroName) {
		this.macroName = macroName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Actions getActions() {
		return actions;
	}

	public void setActions(Actions actions) {
		this.actions = actions;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "SocialMonitorMacro [macroId=" + macroId + ", macroName=" + macroName + ", description=" + description
				+ ", actions=" + actions + ", active=" + active + ", count=" + count + ", createdOn=" + createdOn + "]";
	}

}
