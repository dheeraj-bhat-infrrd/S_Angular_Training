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
	private long modifiedOn;

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

	public long getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(long modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((macroId == null) ? 0 : macroId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SocialMonitorMacro other = (SocialMonitorMacro) obj;
		if (macroId == null) {
			if (other.macroId != null)
				return false;
		} else if (!macroId.equals(other.macroId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SocialMonitorMacro [macroId=" + macroId + ", macroName=" + macroName + ", description=" + description
				+ ", actions=" + actions + ", active=" + active + ", count=" + count + ", createdOn=" + createdOn
				+ ", modifiedOn=" + modifiedOn + "]";
	}

}
