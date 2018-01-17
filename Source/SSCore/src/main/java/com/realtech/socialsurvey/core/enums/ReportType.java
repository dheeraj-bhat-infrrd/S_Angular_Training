package com.realtech.socialsurvey.core.enums;


public enum ReportType
{
    SURVEY_INVITATION_EMAIL_REPORT( "SURVEY_INVITATION_EMAIL_REPORT" );

    private final String name;

    ReportType( String name ) {
        this.name = name;
    }


    public String getName() {
        return this.name;
    }
}
