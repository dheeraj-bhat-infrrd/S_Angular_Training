package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class LoneWolfTier
{
    private List<LoneWolfAgentCommission> AgentCommissions;


    public List<LoneWolfAgentCommission> getAgentCommissions()
    {
        return AgentCommissions;
    }


    public void setAgentCommissions( List<LoneWolfAgentCommission> agentCommissions )
    {
        AgentCommissions = agentCommissions;
    }
}
