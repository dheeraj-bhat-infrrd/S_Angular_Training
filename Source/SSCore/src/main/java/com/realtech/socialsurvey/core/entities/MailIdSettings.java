package com.realtech.socialsurvey.core.entities;

import java.util.List;

/**
 * Holds the mail ids for a profile
 */
public class MailIdSettings {

	private String work;
	private String personal;
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

	public List<MiscValues> getOthers() {
		return others;
	}

	public void setOthers(List<MiscValues> others) {
		this.others = others;
	}

}
