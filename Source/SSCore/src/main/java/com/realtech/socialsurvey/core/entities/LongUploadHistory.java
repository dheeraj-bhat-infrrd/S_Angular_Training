package com.realtech.socialsurvey.core.entities;

import java.sql.Timestamp;

/**
 * Holds the upload history for long variables
 *
 */
public class LongUploadHistory
{
    private long value;
    private Timestamp time;
    public long getValue()
    {
        return value;
    }
    public void setValue( long value )
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
