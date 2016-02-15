package com.realtech.socialsurvey.core.entities;

import java.sql.Timestamp;
import java.util.List;


public class StringListUploadHistory
{
    private List<String> value;
    private Timestamp time;


    public List<String> getValue()
    {
        return value;
    }


    public void setValue( List<String> value )
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
