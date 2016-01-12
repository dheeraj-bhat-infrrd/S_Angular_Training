package com.realtech.socialsurvey.core.entities;

/**
 * 
 * @author rohit
 *
 */
public class AgentMediaPostResponseDetails extends EntityMediaPostResponseDetails
{

    long agentId;

    public long getAgentId()
    {
        return agentId;
    }

    public void setAgentId( long agentId )
    {
        this.agentId = agentId;
    }
}
