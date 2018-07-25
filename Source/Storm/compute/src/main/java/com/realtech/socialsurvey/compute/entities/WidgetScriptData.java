package com.realtech.socialsurvey.compute.entities;

import java.util.List;


public class WidgetScriptData
{
    private List<WidgetScript> companyScript;
    private List<WidgetScript> regionScript;
    private List<WidgetScript> branchScript;
    private List<WidgetScript> agentScript;


    public List<WidgetScript> getCompanyScript()
    {
        return companyScript;
    }


    public void setCompanyScript( List<WidgetScript> companyScript )
    {
        this.companyScript = companyScript;
    }


    public List<WidgetScript> getRegionScript()
    {
        return regionScript;
    }


    public void setRegionScript( List<WidgetScript> regionScript )
    {
        this.regionScript = regionScript;
    }


    public List<WidgetScript> getBranchScript()
    {
        return branchScript;
    }


    public void setBranchScript( List<WidgetScript> branchScript )
    {
        this.branchScript = branchScript;
    }


    public List<WidgetScript> getAgentScript()
    {
        return agentScript;
    }


    public void setAgentScript( List<WidgetScript> agentScript )
    {
        this.agentScript = agentScript;
    }


    @Override
    public String toString()
    {
        return "WidgetScriptData [companyScript=" + companyScript + ", regionScript=" + regionScript + ", branchScript="
            + branchScript + ", agentScript=" + agentScript + "]";
    }

}
