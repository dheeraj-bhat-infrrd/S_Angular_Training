package com.realtech.socialsurvey.stream.enums;

/**
 * @author Lavanya
 */

/*Represents message type of each action*/
public enum MessageType {

    EMAIL( 0 ),PRIVATE_MESSAGE( 1 ), EMAIL_REPLY(2);

    private int value;

    MessageType( int value )
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
