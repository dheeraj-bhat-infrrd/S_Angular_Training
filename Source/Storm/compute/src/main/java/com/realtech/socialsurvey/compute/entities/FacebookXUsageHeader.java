package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;


/**
 * @author manish
 * {
 *  "call_count"    : x,  
 *  "total_time"    : y, 
 *  "total_cputime" : z 
 * }
 *
 *
 */
public class FacebookXUsageHeader implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @SerializedName("call_count")
    private int callCount;
    
    @SerializedName("total_time")
    private int totalTime;
    
    @SerializedName("total_cputime")
    private int totalCputime;


    public int getCallCount()
    {
        return callCount;
    }


    public void setCallCount( int callCount )
    {
        this.callCount = callCount;
    }


    public int getTotalTime()
    {
        return totalTime;
    }


    public void setTotalTime( int totalTime )
    {
        this.totalTime = totalTime;
    }


    public int getTotalCputime()
    {
        return totalCputime;
    }


    public void setTotalCputime( int totalCputime )
    {
        this.totalCputime = totalCputime;
    }

    @Override
    public String toString()
    {
        return "FacebookXAppUsageHeader [callCount=" + callCount + ", totalTime=" + totalTime + ", totalCputime=" + totalCputime
            + "]";
    }
}
