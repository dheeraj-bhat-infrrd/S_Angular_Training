package com.realtech.socialsurvey.core.entities;

import java.util.Date;


public class BooleanUploadHistory
{
    private boolean value;
    private Date time;


    public boolean isValue()
    {
        return value;
    }


    public void setValue( boolean value )
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
