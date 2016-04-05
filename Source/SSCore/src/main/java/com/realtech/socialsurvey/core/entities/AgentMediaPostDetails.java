package com.realtech.socialsurvey.core.entities;


public class AgentMediaPostDetails extends MediaPostDetails
{
    private long agentId;


    public long getAgentId()
    {
        return agentId;
    }


    public void setAgentId( long agentId )
    {
        this.agentId = agentId;
    }
}
