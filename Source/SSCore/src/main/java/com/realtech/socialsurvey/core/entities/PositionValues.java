package com.realtech.socialsurvey.core.entities;

/**
 * LinkedIn position values
 *
 */
public class PositionValues {

	private LinkedInCompany company;
	private long id;
	private boolean isCurrent;
	private StartDate startDate;
	private EndDate endDate;
	private String summary;
	private String title;

	public LinkedInCompany getCompany() {
		return company;
	}

	public void setCompany(LinkedInCompany company) {
		this.company = company;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isCurrent() {
		return isCurrent;
	}

	public void setCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

	public StartDate getStartDate() {
		return startDate;
	}

	public void setStartDate(StartDate startDate) {
		this.startDate = startDate;
	}

	public EndDate getEndDate() {
		return endDate;
	}

	public void setEndDate(EndDate endDate) {
		this.endDate = endDate;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
