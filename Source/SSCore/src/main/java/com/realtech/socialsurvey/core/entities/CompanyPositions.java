package com.realtech.socialsurvey.core.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds individual's company positions
 */
public class CompanyPositions implements Comparable<CompanyPositions> {

	private static final Logger LOG = LoggerFactory.getLogger(CompanyPositions.class);

	private String name;
	private int startMonth;
	private int startYear;
	private String startTime;
	private int endMonth;
	private int endYear;
	private String endTime;
	private String title;
	private boolean isCurrent;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(int startMonth) {
		this.startMonth = startMonth;
	}

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public int getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(int endMonth) {
		this.endMonth = endMonth;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

	public boolean getIsCurrent() {
		return isCurrent;
	}

	public void setIsCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

	@Override
	public int compareTo(CompanyPositions o) {
		LOG.debug("Comparing between positions: " + this.name + " with " + o.name);
		// if the company is current, then it is the lowest. Oherwise, check the year and then the
		// month
		if (this.isCurrent) {
			return -1;
		}
		else if (o.isCurrent) {
			return 1;
		}
		else {
			// both are previous company
			if(this.startYear == o.startYear){
				if(this.startMonth > o.startMonth){
					return -1;
				}else if(this.startMonth < o.startMonth){
					return 1;
				}else {
					return 0;
				}
			}else{
				if(this.startYear > o.startYear){
					return -1;
				}else{
					return 1;
				}
			}
		}
	}

}
