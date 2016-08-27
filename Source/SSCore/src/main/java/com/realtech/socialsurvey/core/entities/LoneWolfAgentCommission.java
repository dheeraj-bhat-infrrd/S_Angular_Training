package com.realtech.socialsurvey.core.entities;

public class LoneWolfAgentCommission
{
    private LoneWolfAgent Agent;
    private String EndCode;

    public LoneWolfAgent getAgent()
    {
        return Agent;
    }


    public void setAgent( LoneWolfAgent agent )
    {
        Agent = agent;
    }


    public String getEndCode()
    {
        return EndCode;
    }


    public void setEndCode( String endCode )
    {
        EndCode = endCode;
    }
}
