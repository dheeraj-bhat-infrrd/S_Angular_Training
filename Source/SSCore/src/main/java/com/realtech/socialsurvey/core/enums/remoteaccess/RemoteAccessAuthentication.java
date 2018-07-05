package com.realtech.socialsurvey.core.enums.remoteaccess;


/**
 * Enum for identifying the method of authentication 
 */
public enum RemoteAccessAuthentication
{

    USING_PUBLIC_KEY( "using_public_key", 1 ), USING_PASSWORD( "using_password", 2 );

    private final String preferredAuthentication;
    private final int value;


    private RemoteAccessAuthentication( String preferredAuthentication, int value )
    {
        this.preferredAuthentication = preferredAuthentication;
        this.value = value;
    }


    public String getPreferredAuthentication()
    {
        return this.preferredAuthentication;
    }


    public int getValue()
    {
        return this.value;
    }
}
