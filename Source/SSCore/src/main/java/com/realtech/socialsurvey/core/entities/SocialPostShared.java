package com.realtech.socialsurvey.core.entities;

import java.util.List;
import java.util.Map;


public class SocialPostShared
{

    private long totalCount;

    //firt string denotes collection name
    // map is the mapping between id for that collection and list will tell what all places it has been posted too. 

    private Map<String, Map<Long, List<String>>> postSharedOn;

    private Map<Long, Long> regionCountMap;

    private Map<Long, Long> agentCountMap;

    private Map<Long, Long> companyCountMap;

    private Map<Long, Long> branchCountMap;


    public long getTotalCount()
    {
        return totalCount;
    }


    public void setTotalCount( long totalCount )
    {
        this.totalCount = totalCount;
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


    public Map<Long, Long> getCompanyCountMap()
    {
        return companyCountMap;
    }


    public void setCompanyCountMap( Map<Long, Long> companyCountMap )
    {
        this.companyCountMap = companyCountMap;
    }


    public Map<Long, Long> getBranchCountMap()
    {
        return branchCountMap;
    }


    public void setBranchCountMap( Map<Long, Long> branchCountMap )
    {
        this.branchCountMap = branchCountMap;
    }


}