package com.realtech.socialsurvey.core.entities;


public class JobLogDetailsResponse
{

    String status;
    String timestampInEst;


    public String getStatus()
    {
        return status;
    }


    public void setStatus( String status )
    {
        this.status = status;
    }


    public String getTimestampInEst()
    {
        return timestampInEst;
    }


    public void setTimestampInEst( String timestampInEst )
    {
        this.timestampInEst = timestampInEst;
    }


    @Override
    public String toString()
    {
        return "JobLogDetailsResponse [status=" + status + ", timestampInEst=" + timestampInEst + "]";
    }
    
}

