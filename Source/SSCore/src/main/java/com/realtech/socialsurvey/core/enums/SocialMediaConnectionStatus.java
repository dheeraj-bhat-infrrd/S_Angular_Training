package com.realtech.socialsurvey.core.enums;

public enum SocialMediaConnectionStatus
{
    NOT_CONNECTED(0), CONNECTED(1), EXPIRED(2);

    private int value;


    SocialMediaConnectionStatus( int value )
    {
        this.value = value;
    }


    public int getValue()
    {
        return value;
    }


    public void setValue( int value )
    {
        this.value = value;
    }
}
