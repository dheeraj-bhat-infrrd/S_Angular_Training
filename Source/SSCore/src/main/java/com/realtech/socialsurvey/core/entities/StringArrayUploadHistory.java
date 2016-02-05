package com.realtech.socialsurvey.core.entities;

import java.sql.Timestamp;


public class StringArrayUploadHistory
{
    private String[] value;
    private Timestamp time;


    public String[] getValue()
    {
        return value;
    }


    public void setValue( String[] value )
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
