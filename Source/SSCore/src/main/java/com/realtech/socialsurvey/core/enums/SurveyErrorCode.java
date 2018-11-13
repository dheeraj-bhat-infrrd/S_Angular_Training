package com.realtech.socialsurvey.core.enums;

public enum SurveyErrorCode
{
    DUPLICATE_RECORD( "Dupliacte record" ),
    IGNORED_RECORD( "Ignored record" ),
    OLD_RECORD( "Old record" ),
    CORRUPT_RECORD_AGENT_EMAIL_ID_NULL( "Agent email id is absent." ),
    CORRUPT_RECORD_CUSTOMER_FIRST_NAME_NULL( "Customer first name is absent." ),
    CORRUPT_RECORD_CUSTOMER_EMAIL_ID_NULL( "Customer email id is absent " ),
    MISMATCH_RECORD_AGENT_NOT_FOUND( "Agent does not exist in social survey." ),
    MISMATCH_RECORD_INCORRECT_COMPANY( "Agent does not belong to correct company." ),
    CORRUPT_RECORD_INCORRECT_REGION_BRANCH( "Agent does not belong to correct region or branch." ),
    NOT_KNOWN( "Reason not found." ),
    USER_DELETED( "Agent is deleted" ),
    SURVEY_NOT_ALLOWED( "Survey is not allowed for agent."),
    UNSUBSCRIBED_CUSTOMER_EMAIL( "Customer has unsubscribed emails either from company or from SocialSurvey Application." ),
    CORRUPT_RECORD_CUSTOMER_EMAIL_ID_INVALID( "Customer email id is invalid " );

    private String value;


    private SurveyErrorCode( String value )
    {
        this.value = value;
    }


    public String getValue()
    {
        return value;
    }
}
