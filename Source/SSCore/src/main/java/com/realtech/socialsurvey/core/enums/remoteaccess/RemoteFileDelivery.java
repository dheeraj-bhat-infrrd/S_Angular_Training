package com.realtech.socialsurvey.core.enums.remoteaccess;


/**
 * Enum for identifying the method of authentication 
 */
public enum RemoteFileDelivery
{

    SEND( "send", 1 ), RECEIVE( "receive", 2 );

    private final String sendOrReceive;
    private final int value;


    private RemoteFileDelivery( String sendOrReceive, int value )
    {
        this.sendOrReceive = sendOrReceive;
        this.value = value;
    }


    public String getSendOrReceive()
    {
        return this.sendOrReceive;
    }


    public int getValue()
    {
        return this.value;
    }
}
