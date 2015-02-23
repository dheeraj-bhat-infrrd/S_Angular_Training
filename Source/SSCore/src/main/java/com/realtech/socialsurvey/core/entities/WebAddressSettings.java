package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class WebAddressSettings {

	private String work;
	private String personal;
	private String blogs;
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
	
	public String getBlogs() {
		return blogs;
	}

	public void setBlogs(String blogs) {
		this.blogs = blogs;
	}

	public List<MiscValues> getOthers() {
		return others;
	}

	public void setOthers(List<MiscValues> others) {
		this.others = others;
	}

	@Override
	public String toString() {
		return "WebAddressSettings [work=" + work + ", personal=" + personal + ", blogs=" + blogs + ", others=" + others + "]";
	}
}
