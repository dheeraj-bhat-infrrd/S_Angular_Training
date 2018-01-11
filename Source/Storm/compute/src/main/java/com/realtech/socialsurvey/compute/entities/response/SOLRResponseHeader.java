package com.realtech.socialsurvey.compute.entities.response;

public class SOLRResponseHeader
{
    private int status;
    private int QTime;


    public int getStatus()
    {
        return status;
    }


    public void setStatus( int status )
    {
        this.status = status;
    }


    public int getQTime()
    {
        return QTime;
    }


    public void setQTime( int qTime )
    {
        QTime = qTime;
    }
}
