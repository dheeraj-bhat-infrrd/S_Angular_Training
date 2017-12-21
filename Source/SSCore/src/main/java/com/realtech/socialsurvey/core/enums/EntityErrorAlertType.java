package com.realtech.socialsurvey.core.enums;

public enum EntityErrorAlertType
{
    LESS_TRANSACTION_IN_PAST_DAYS( "lessTransactionInPastDays" ),
    LESS_INVITATION_IN_PAST_DAYS( "lessInvitationInPastDays" ),
    MORE_REMINDER_IN_PAST_DAYS( "moreReminderInPastDays" );

    private String alertType;


    EntityErrorAlertType( String alertType )
    {
        this.alertType = alertType;
    }


    public String getAlertType()
    {
        return alertType;
    }

}
