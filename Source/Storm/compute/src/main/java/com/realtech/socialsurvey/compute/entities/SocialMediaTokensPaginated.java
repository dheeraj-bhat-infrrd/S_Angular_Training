package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author manish
 *
 */
public class SocialMediaTokensPaginated implements Serializable
{
    private static final long serialVersionUID = 1L;

    private List<SocialMediaTokenResponse> companiesTokens;

    private List<SocialMediaTokenResponse> regionsTokens;

    private List<SocialMediaTokenResponse> branchesTokens;

    private List<SocialMediaTokenResponse> agentsTokens;
    
    private Map<Long, Long> regionCompanyIdMap;

    private Map<Long, Long> branchCompanyIdMap;

    private Map<Long, Long> agentCompanyIdMap;
    
    private int totalRecord;
    
    public SocialMediaTokensPaginated(){
        this.companiesTokens = new ArrayList<>();
        this.regionsTokens = new ArrayList<>();
        this.branchesTokens = new ArrayList<>();
        this.agentsTokens = new ArrayList<>();
    }
    
    public Map<Long, Long> getRegionCompanyIdMap()
    {
        return regionCompanyIdMap;
    }

    public void setRegionCompanyIdMap( Map<Long, Long> regionCompanyIdMap )
    {
        this.regionCompanyIdMap = regionCompanyIdMap;
    }


    public Map<Long, Long> getAgentCompanyIdMap()
    {
        return agentCompanyIdMap;
    }

    public void setAgentCompanyIdMap( Map<Long, Long> agentCompanyIdMap )
    {
        this.agentCompanyIdMap = agentCompanyIdMap;
    }
    
    public Map<Long, Long> getBranchCompanyIdMap()
    {
        return branchCompanyIdMap;
    }

    public void setBranchCompanyIdMap( Map<Long, Long> branchCompanyIdMap )
    {
        this.branchCompanyIdMap = branchCompanyIdMap;
    }
    
    public List<SocialMediaTokenResponse> getCompaniesTokens()
    {
        return companiesTokens;
    }

    public void setCompaniesTokens( List<SocialMediaTokenResponse> companiesTokens )
    {
        this.companiesTokens = companiesTokens;
    }

    public List<SocialMediaTokenResponse> getRegionsTokens()
    {
        return regionsTokens;
    }

    public void setRegionsTokens( List<SocialMediaTokenResponse> regionsTokens )
    {
        this.regionsTokens = regionsTokens;
    }

    public List<SocialMediaTokenResponse> getBranchesTokens()
    {
        return branchesTokens;
    }

    public void setBranchesTokens( List<SocialMediaTokenResponse> branchesTokens )
    {
        this.branchesTokens = branchesTokens;
    }

    public List<SocialMediaTokenResponse> getAgentsTokens()
    {
        return agentsTokens;
    }

    public void setAgentsTokens( List<SocialMediaTokenResponse> agentsTokens )
    {
        this.agentsTokens = agentsTokens;
    }
    
    public int getTotalRecord()
    {
        return totalRecord;
    }

    public void setTotalRecord( int totalRecord )
    {
        this.totalRecord = totalRecord;
    }

    @Override
    public String toString()
    {
        return "SocialMediaTokensPaginated [companiesTokens=" + companiesTokens + ", regionsTokens=" + regionsTokens
            + ", branchesTokens=" + branchesTokens + ", agentsTokens=" + agentsTokens + ", regionCompanyIdMap="
            + regionCompanyIdMap + ", branchCompanyIdMap=" + branchCompanyIdMap + ", agentCompanyIdMap=" + agentCompanyIdMap
            + ", totalResult=" + totalRecord + "]";
    }

}
