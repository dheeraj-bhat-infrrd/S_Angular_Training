package com.realtech.socialsurvey.core.entities.remoteaccess;

public class RemoteAccessResponse
{
    private String response;
    private int status;


    public String getResponse()
    {
        return response;
    }


    public void setResponse( String response )
    {
        this.response = response;
    }


    public int getStatus()
    {
        return status;
    }


    public void setStatus( int status )
    {
        this.status = status;
    }


    @Override
    public String toString()
    {
        return "RemoteAccessResponse [response=" + response + ", status=" + status + "]";
    }


}
