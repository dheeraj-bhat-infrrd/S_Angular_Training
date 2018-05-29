package com.realtech.socialsurvey.compute.enums;

public enum SocialFeedStatus
{
    NEW( 0 ), ESCALATED( 1 ), RESOLVED( 2 ), SUBMIT( 3 );

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
