package com.realtech.socialsurvey.stream.messages.enumeration;

public enum EventType
{
    CLICK( "Click Event", 1 );

    private String name;
    private int value;


    private EventType( String name, int value )
    {
        this.name = name;
        this.value = value;
    }


    public String getName()
    {
        return name;

    }


    public int getValue()
    {
        return value;
    }
}
