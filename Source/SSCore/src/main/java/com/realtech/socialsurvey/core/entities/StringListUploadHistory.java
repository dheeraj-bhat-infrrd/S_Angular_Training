package com.realtech.socialsurvey.core.entities;

import java.util.Date;
import java.util.List;


public class StringListUploadHistory
{
    private List<String> value;
    private Date time;


    public List<String> getValue()
    {
        return value;
    }


    public void setValue( List<String> value )
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
