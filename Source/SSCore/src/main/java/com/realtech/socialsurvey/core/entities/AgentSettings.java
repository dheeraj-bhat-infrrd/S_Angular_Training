package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class AgentSettings extends OrganizationUnitSettings {

	private List<String> expertise;
	private List<String> hobbies;
	private long reviewCount;
	private List<CompanyPositions> positions;
	private CompanyProfileData companyProfileData;
	private String surveyUrl;
	private String userEncryptedId;
	private boolean isLoginPrevented;

    public String getUserEncryptedId()
    {
        return userEncryptedId;
    }

    public void setUserEncryptedId( String userEncryptedId )
    {
        this.userEncryptedId = userEncryptedId;
    }

    public String getSurveyUrl()
    {
        return surveyUrl;
    }

    public void setSurveyUrl( String surveyUrl )
    {
        this.surveyUrl = surveyUrl;
    }

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

	public CompanyProfileData getCompanyProfileData() {
		return companyProfileData;
	}

	public void setCompanyProfileData(CompanyProfileData companyProfileData) {
		this.companyProfileData = companyProfileData;
	}

	public boolean isLoginPrevented() {
		return isLoginPrevented;
	}

	public void setLoginPrevented(boolean isLoginPrevented) {
		this.isLoginPrevented = isLoginPrevented;
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
