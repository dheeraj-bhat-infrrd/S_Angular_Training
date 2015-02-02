package com.realtech.socialsurvey.core.entities;

import java.util.List;

/**
 * Settings object for contact numbers of a profile
 */
public class ContactNumberSettings {

	private String work;
	private String personal;
	private String fax;
	private List<MiscValues> others;

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getPersonal() {
		return personal;
	}

	public void setPersonal(String personal) {
		this.personal = personal;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public List<MiscValues> getOthers() {
		return others;
	}

	public void setOthers(List<MiscValues> others) {
		this.others = others;
	}

	@Override
	public String toString() {
		return "work: " + work + "\t personal: " + personal + "\t others: " + (others != null ? others.toString() : "null");
	}

}
