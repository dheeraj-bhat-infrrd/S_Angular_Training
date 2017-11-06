package com.realtech.socialsurvey.core.entities;

import java.util.Date;
import java.util.Set;


public class StringSetUploadHistory
{
    private Set<String> value;
    private Date time;


    public StringSetUploadHistory()
    {}


    public StringSetUploadHistory( Set<String> history, Date date )
    {
        this.value = history;
        this.time = date;
    }


    public Set<String> getValue()
    {
        return value;
    }


    public void setValue( Set<String> value )
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
