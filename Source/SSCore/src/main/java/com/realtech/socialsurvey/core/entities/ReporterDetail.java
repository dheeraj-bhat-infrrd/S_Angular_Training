package com.realtech.socialsurvey.core.entities;

public class ReporterDetail
{
    private String reporterName;
    private String reporterEmail;
    private String reportReason;


    public ReporterDetail( String reporterName, String reporterEmail, String reportReason )
    {
        this.reporterName = reporterName;
        this.reporterEmail = reporterEmail;
        this.reportReason = reportReason;
    }


    public String getReporterName()
    {
        return reporterName;
    }


    public void setReporterName( String reporterName )
    {
        this.reporterName = reporterName;
    }


    public String getReporterEmail()
    {
        return reporterEmail;
    }


    public void setReporterEmail( String reporterEmail )
    {
        this.reporterEmail = reporterEmail;
    }


    public String getReportReason()
    {
        return reportReason;
    }


    public void setReportReason( String reportReason )
    {
        this.reportReason = reportReason;
    }


    @Override
    public String toString()
    {
        return "reporterName: " + reporterName + "\tuserProfileName: " + reporterEmail;
    }
}
