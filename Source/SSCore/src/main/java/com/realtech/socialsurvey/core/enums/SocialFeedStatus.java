package com.realtech.socialsurvey.core.enums;

public enum SocialFeedStatus
{
    NEW( 0 ), ALERT(1), ESCALATED( 2 ), RESOLVED( 3 );

    private int value;

    SocialFeedStatus( int value )
    {
        this.value = value;
    }


    public int getValue()
    {
        return value;
    }

}
