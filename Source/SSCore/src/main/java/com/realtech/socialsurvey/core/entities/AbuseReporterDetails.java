package com.realtech.socialsurvey.core.entities;

import java.util.Set;


public class AbuseReporterDetails
{
    private String _id;
    private String surveyId;
    private Set<ReporterDetail> abuseReporters;


    public String get_id()
    {
        return _id;
    }


    public void set_id( String _id )
    {
        this._id = _id;
    }


    public String getSurveyId()
    {
        return surveyId;
    }


    public void setSurveyId( String surveyId )
    {
        this.surveyId = surveyId;
    }


    public Set<ReporterDetail> getAbuseReporters()
    {
        return abuseReporters;
    }


    public void setAbuseReporters( Set<ReporterDetail> abuseReporters )
    {
        this.abuseReporters = abuseReporters;
    }


}
