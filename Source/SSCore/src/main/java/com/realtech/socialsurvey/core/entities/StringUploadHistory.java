package com.realtech.socialsurvey.core.entities;

import java.util.Date;


public class StringUploadHistory
{

    private String value;
    private Date time;


    public StringUploadHistory()
    {}


    public StringUploadHistory( String value, Date time )
    {
        this.value = value;
        this.time = time;
    }


    public String getValue()
    {
        return value;
    }


    public void setValue( String value )
    {
        this.value = value;
    }


    public Date getTime()
    {
        return time;
    }


    public void setTime( Date time )
    {
        this.time = time;
    }


}
