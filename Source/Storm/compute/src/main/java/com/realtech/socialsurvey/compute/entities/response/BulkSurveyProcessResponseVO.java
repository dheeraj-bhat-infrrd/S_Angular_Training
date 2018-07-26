package com.realtech.socialsurvey.compute.entities.response;

import java.util.HashMap;
import java.util.Map;

public class BulkSurveyProcessResponseVO
{

    private boolean processed;
    
    private String errorMessage;
    
    private Map<Integer, Long> surveyIds = new HashMap<>();
    
    private int lineNumber;

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

    public Map<Integer, Long> getSurveyIds() {
        return surveyIds;
    }

    public void setSurveyIds(Map<Integer, Long> surveyIds) {
        this.surveyIds = surveyIds;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }

    public void setLineNumber( int lineNumber )
    {
        this.lineNumber = lineNumber;
    }
    
    
    
    

}
