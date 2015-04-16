package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class AgentSettings extends OrganizationUnitSettings {

	private List<String> expertise;
	private List<String> hobbies;
	private long reviewCount;
	private List<CompanyPositions> positions;

	public List<String> getExpertise() {
		return expertise;
	}

	public void setExpertise(List<String> expertise) {
		this.expertise = expertise;
	}

	public List<String> getHobbies() {
		return hobbies;
	}

	public void setHobbies(List<String> hobbies) {
		this.hobbies = hobbies;
	}

	public long getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(long reviewCount) {
		this.reviewCount = reviewCount;
	}

	public List<CompanyPositions> getPositions() {
		return positions;
	}

	public void setPositions(List<CompanyPositions> positions) {
		this.positions = positions;
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
