package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class AgentDisableApiEntity
{

    private boolean isAgentProfileDisabled;
    private List<Long> agentIds;


    public boolean isAgentProfileDisabled()
    {
        return isAgentProfileDisabled;
    }


    public void setIsAgentProfileDisabled( boolean isAgentProfileDisabled )
    {
        this.isAgentProfileDisabled = isAgentProfileDisabled;
    }


    public List<Long> getAgentIds()
    {
        return agentIds;
    }


    public void setAgentIds( List<Long> agentIds )
    {
        this.agentIds = agentIds;
    }
}
