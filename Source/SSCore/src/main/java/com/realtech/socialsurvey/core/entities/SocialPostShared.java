package com.realtech.socialsurvey.core.entities;

import java.util.List;
import java.util.Map;


public class SocialPostShared
{

    private long companyCount;

    //firt string denotes collection name
    // map is the mapping between id for that collection and list will tell what all places it has been posted too. 

    private Map<String, Map<Long, List<String>>> postSharedOn;

    private Map<Long, Long> regionCountMap;

    private Map<Long, Long> agentCountMap;

    private long companyId;

    private Map<Long, Long> branchCountMap;


    public long getCompanyCount()
    {
        return companyCount;
    }


    public void setCompanyCount( long companyCount )
    {
        this.companyCount = companyCount;
    }


    public Map<String, Map<Long, List<String>>> getPostSharedOn()
    {
        return postSharedOn;
    }


    public void setPostSharedOn( Map<String, Map<Long, List<String>>> postSharedOn )
    {
        this.postSharedOn = postSharedOn;
    }


    public Map<Long, Long> getRegionCountMap()
    {
        return regionCountMap;
    }


    public void setRegionCountMap( Map<Long, Long> regionCountMap )
    {
        this.regionCountMap = regionCountMap;
    }


    public Map<Long, Long> getAgentCountMap()
    {
        return agentCountMap;
    }


    public void setAgentCountMap( Map<Long, Long> agentCountMap )
    {
        this.agentCountMap = agentCountMap;
    }


    public Map<Long, Long> getBranchCountMap()
    {
        return branchCountMap;
    }


    public void setBranchCountMap( Map<Long, Long> branchCountMap )
    {
        this.branchCountMap = branchCountMap;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


}