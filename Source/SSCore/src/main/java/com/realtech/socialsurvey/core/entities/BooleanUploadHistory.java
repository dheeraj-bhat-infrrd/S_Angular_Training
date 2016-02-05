package com.realtech.socialsurvey.core.entities;

import java.sql.Timestamp;


public class BooleanUploadHistory
{
    private boolean value;
    private Timestamp time;


    public boolean isValue()
    {
        return value;
    }


    public void setValue( boolean value )
    {
        this.value = value;
    }


    public Timestamp getTime()
    {
        return time;
    }


    public void setTime( Timestamp time )
    {
        this.time = time;
    }


}
