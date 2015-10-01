package com.realtech.socialsurvey.core.entities;

public class AbusiveSurveyReportWrapper
{
    private SurveyDetails survey;
    private AbuseReporterDetails abuseReporterDetails;


    public AbusiveSurveyReportWrapper( SurveyDetails survey, AbuseReporterDetails abuseReporterDetails )
    {
        this.survey = survey;
        this.abuseReporterDetails = abuseReporterDetails;
    }


    public SurveyDetails getSurvey()
    {
        return survey;
    }


    public void setSurvey( SurveyDetails survey )
    {
        this.survey = survey;
    }


    public AbuseReporterDetails getAbuseReporterDetails()
    {
        return abuseReporterDetails;
    }


    public void setAbuseReporterDetails( AbuseReporterDetails abuseReporterDetails )
    {
        this.abuseReporterDetails = abuseReporterDetails;
    }

}
