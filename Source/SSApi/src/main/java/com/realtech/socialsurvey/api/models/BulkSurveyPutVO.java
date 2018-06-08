package com.realtech.socialsurvey.api.models;

import java.util.List;

public class BulkSurveyPutVO {

	private long companyId;
	
	private String source;
	
	private List<SurveyPutVO> surveys;

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<SurveyPutVO> getSurveys() {
		return surveys;
	}

	public void setSurveys(List<SurveyPutVO> surveys) {
		this.surveys = surveys;
	}
}
