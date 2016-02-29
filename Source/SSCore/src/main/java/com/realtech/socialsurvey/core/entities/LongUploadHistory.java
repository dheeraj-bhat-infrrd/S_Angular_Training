package com.realtech.socialsurvey.core.entities;

import java.util.Date;

/**
 * Holds the upload history for long variables
 *
 */
public class LongUploadHistory
{
    private long value;
    private Date time;
    public long getValue()
    {
        return value;
    }
    public void setValue( long value )
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
