package com.realtech.socialsurvey.api.models.v2;

import java.util.HashMap;
import java.util.Map;

public class BulkSurveyProcessResponseVO {
	
	private boolean isProcessed;
	
	private String errorMessage;
	
	private Map<String, Long> surveyIds = new HashMap<String, Long>();

	public boolean isProcessed() {
		return isProcessed;
	}

	public void setProcessed(boolean isProcessed) {
		this.isProcessed = isProcessed;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Map<String, Long> getSurveyIds() {
		return surveyIds;
	}

	public void setSurveyIds(Map<String, Long> surveyIds) {
		this.surveyIds = surveyIds;
	}
	
	

}
