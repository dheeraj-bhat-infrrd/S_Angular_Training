package com.realtech.socialsurvey.compute.enums;

public enum ReportType {

    SURVEY_INVITATION_EMAIL_REPORT( "SURVEY_INVITATION_EMAIL_REPORT" ),
    FILE_UPLOAD_SOCIAL_MONITOR_DATE_REPORT( "SOCIAL_MONITOR_DATE_REPORT" );

    private final String name;


    ReportType( String name )
    {
        this.name = name;
    }


    public String getName()
    {
        return this.name;
    }
}
