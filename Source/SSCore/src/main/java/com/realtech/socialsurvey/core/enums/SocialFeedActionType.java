package com.realtech.socialsurvey.core.enums;

public enum SocialFeedActionType
{
    NEW( 0 ), ALERT(1), ESCALATED( 2 ), RESOLVED( 3 ), SUBMIT( 4 );

    private int value;

    SocialFeedActionType( int value )
    {
        this.value = value;
    }


    public int getValue()
    {
        return value;
    }

}
