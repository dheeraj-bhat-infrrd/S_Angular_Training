package com.realtech.socialsurvey.core.enums;

public enum SocialFeedStatus
{
    NEW( 0 ), ESCALATED( 1 ), RESOLVED( 2 );

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
