package com.realtech.socialsurvey.core.entities;

public class ReporterDetail
{
    private String reporterName;
    private String reporterEmail;

    public ReporterDetail(String reporterName, String reporterEmail )
    {
        this.reporterName = reporterName;
        this.reporterEmail = reporterEmail;
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
    
    @Override
    public String toString() {
        return "reporterName: " + reporterName + "\tuserProfileName: " + reporterEmail;
    }
}
