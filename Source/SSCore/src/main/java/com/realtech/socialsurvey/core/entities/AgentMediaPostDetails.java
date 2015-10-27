package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class AgentMediaPostDetails
{
    private long agentId;

    private List<String> sharedOn;


    public long getAgentId()
    {
        return agentId;
    }


    public void setAgentId( long agentId )
    {
        this.agentId = agentId;
    }


    public List<String> getSharedOn()
    {
        return sharedOn;
    }


    public void setSharedOn( List<String> sharedOn )
    {
        this.sharedOn = sharedOn;
    }
}
