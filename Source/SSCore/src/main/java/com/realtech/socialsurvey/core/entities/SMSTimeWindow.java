/**
 *
 */
package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;

/**
 * @author manish
 *
 */
public class SMSTimeWindow implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String startTime;
    private String endTime;
    private String timeZone;

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime( String startTime )
    {
        this.startTime = startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime( String endTime )
    {
        this.endTime = endTime;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone( String timeZone ) {
        this.timeZone = timeZone;
    }

    @Override
    public String toString()
    {
        return "SMSTimeWindow [startTime=" + startTime + ", endTime=" + endTime + ", timeZone=" + timeZone + "]";
    }
}
