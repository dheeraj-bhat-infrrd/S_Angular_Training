package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.List;

public class FilterKeywordsResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int count;
	private String monitorType;
	private List<Keyword> filterKeywords;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getMonitorType() {
		return monitorType;
	}

	public void setMonitorType(String monitorType) {
		this.monitorType = monitorType;
	}

	public List<Keyword> getFilterKeywords() {
		return filterKeywords;
	}

	public void setFilterKeywords(List<Keyword> filterKeywords) {
		this.filterKeywords = filterKeywords;
	}

	@Override
	public String toString() {
		return "FilterKeywordsResponse [count=" + count + ", monitorType=" + monitorType + ", filterKeywords="
				+ filterKeywords + "]";
	}

}
