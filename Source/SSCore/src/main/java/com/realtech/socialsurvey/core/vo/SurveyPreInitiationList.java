package com.realtech.socialsurvey.core.vo;

import java.util.List;

import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;

public class SurveyPreInitiationList
{
    
    private long totalRecord;
    private  List<SurveyPreInitiation> surveyPreInitiationList;
    
    
    public long getTotalRecord()
    {
        return totalRecord;
    }
    public void setTotalRecord( long totalRecord )
    {
        this.totalRecord = totalRecord;
    }
    public List<SurveyPreInitiation> getSurveyPreInitiationList()
    {
        return surveyPreInitiationList;
    }
    public void setSurveyPreInitiationList( List<SurveyPreInitiation> surveyPreInitiationList )
    {
        this.surveyPreInitiationList = surveyPreInitiationList;
    } 

}
