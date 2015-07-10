package com.realtech.socialsurvey.core.entities;

import java.sql.Timestamp;
import java.util.Set;

/**
 * Search object for Company Reports
 */
public class CompanyReportsSearch {

	public long companyId;
	public Set<Long> companyIds;
	public Timestamp lowerEndTime;
	public Timestamp higherEndTime;

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public Set<Long> getCompanyIds() {
		return companyIds;
	}

	public void setCompanyIds(Set<Long> companyIds) {
		this.companyIds = companyIds;
	}

	public Timestamp getLowerEndTime() {
		return lowerEndTime;
	}

	public void setLowerEndTime(Timestamp lowerEndTime) {
		this.lowerEndTime = lowerEndTime;
	}

	public Timestamp getHigherEndTime() {
		return higherEndTime;
	}

	public void setHigherEndTime(Timestamp higherEndTime) {
		this.higherEndTime = higherEndTime;
	}

	@Override
	public String toString(){
		return "companyId: "+companyId + "\t companyIds: "+companyIds.toString() + "\t lowerEndTime: "+(lowerEndTime != null?lowerEndTime.toString():"null")+ "\t higherEndTime: "+(higherEndTime != null?higherEndTime.toString():"null");
	}
}
