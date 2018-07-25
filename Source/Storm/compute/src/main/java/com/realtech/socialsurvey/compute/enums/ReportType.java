package com.realtech.socialsurvey.compute.enums;

public enum ReportType {

    SURVEY_INVITATION_EMAIL_REPORT( "SURVEY_INVITATION_EMAIL_REPORT" ),
    SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD( "SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD" ),
    SOCIAL_MONITOR_DATE_REPORT( "SOCIAL_MONITOR_DATE_REPORT" ),
    WIDGET_REPORT( "WIDGET_REPORT" );

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
