package com.realtech.socialsurvey.core.entities;

public class DotLoopCrmInfo extends CRMInfo
{

    private String api;

    private long regionId;

    private long branchId;

    private long agentId;


    public String getApi()
    {
        return api;
    }


    public void setApi( String api )
    {
        this.api = api;
    }


    public long getRegionId()
    {
        return regionId;
    }


    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }


    public long getBranchId()
    {
        return branchId;
    }


    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }


    public long getAgentId()
    {
        return agentId;
    }


    public void setAgentId( long agentId )
    {
        this.agentId = agentId;
    }


    @Override
    public String toString()
    {
        return "api : " + api;
    }

}
