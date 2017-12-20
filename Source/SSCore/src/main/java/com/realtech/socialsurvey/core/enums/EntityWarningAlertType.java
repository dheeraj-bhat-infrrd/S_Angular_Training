package com.realtech.socialsurvey.core.enums;

public enum EntityWarningAlertType
{
    LESS_TRANSACTION_IN_PAST_DAYS( "lessTransactionInPastDays" ),
    LESS_TRANSACTION_IN_PAST_WEEK( "lessTransactionInPastWeek" ),
    LESS_INVITATION_IN_PAST_WEEK( "lessInvitationInPastWeek" ),
    MORE_REMINDER_IN_PAST_WEEK( "moreReminderInPastWeek" ),
    LESS_SURVEY_COMPLETED_IN_PAST_DAYS( "lessSurveyCompletedInPastDays" );

    private String alertType;


    EntityWarningAlertType( String alertType )
    {
        this.alertType = alertType;
    }


    public String getAlertType()
    {
        return alertType;
    }

}
