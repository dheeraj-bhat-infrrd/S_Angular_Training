package com.realtech.socialsurvey.core.enums;

/**
 * @author manish
 *
 */
public enum ProfileType
{
    COMPANY( 0 ), REGION( 1 ), BRANCH( 2 ), AGENT( 3 );

    private int value;

    ProfileType( int value )
    {
        this.value = value;
    }


    public int getValue()
    {
        return value;
    }
}