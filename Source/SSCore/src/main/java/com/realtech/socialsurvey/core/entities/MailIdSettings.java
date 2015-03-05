package com.realtech.socialsurvey.core.entities;

import java.util.List;

/**
 * Holds the mail ids for a profile
 */
public class MailIdSettings {

	private String work;
	private boolean isWorkEmailVerified = true;
	private String personal;
	private boolean isPersonalEmailVerified = true;
	private List<MiscValues> others;

	// Need to implement verification for others

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public boolean getIsWorkEmailVerified() {
		return isWorkEmailVerified;
	}

	public void setWorkEmailVerified(boolean isWorkEmailVerified) {
		this.isWorkEmailVerified = isWorkEmailVerified;
	}

	public String getPersonal() {
		return personal;
	}

	public void setPersonal(String personal) {
		this.personal = personal;
	}

	public boolean getIsPersonalEmailVerified() {
		return isPersonalEmailVerified;
	}

	public void setPersonalEmailVerified(boolean isPersonalEmailVerified) {
		this.isPersonalEmailVerified = isPersonalEmailVerified;
	}

	public List<MiscValues> getOthers() {
		return others;
	}

	public void setOthers(List<MiscValues> others) {
		this.others = others;
	}

	@Override
	public String toString() {
		return "work: " + work + "\t isWorkEmailVerified: " + isWorkEmailVerified + "\t personal: " + personal + "\t isPersonalEmailVerified: "
				+ isPersonalEmailVerified + "\t others: " + (others != null ? others.toString() : "null");
	}
}