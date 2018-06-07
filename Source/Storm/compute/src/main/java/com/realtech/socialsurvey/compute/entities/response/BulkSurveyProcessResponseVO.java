package com.realtech.socialsurvey.compute.entities.response;

import java.util.HashMap;
import java.util.Map;

public class BulkSurveyProcessResponseVO
{

    private boolean processed;
    
    private String errorMessage;
    
    private Map<String, Long> surveyIds = new HashMap<String, Long>();

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
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
