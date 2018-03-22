package com.realtech.socialsurvey.core.entities;

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

    private List<FeedIngestionEntityForSM> companiesTokens;

    private List<FeedIngestionEntityForSM> regionsTokens;

    private List<FeedIngestionEntityForSM> branchesTokens;

    private List<FeedIngestionEntityForSM> agentsTokens;
    
    private Map<Long, Long> regionCompanyIdMap;

    private Map<Long, Long> branchCompanyIdMap;

    private Map<Long, Long> agentCompanyIdMap;
    
    public SocialMediaTokensPaginated(){
        this.companiesTokens = new ArrayList<>();
        this.regionsTokens = new ArrayList<>();
        this.branchesTokens = new ArrayList<>();
        this.agentsTokens = new ArrayList<>();
    }

    public List<FeedIngestionEntityForSM> getCompaniesTokens()
    {
        return companiesTokens;
    }

    public void setCompaniesTokens( List<FeedIngestionEntityForSM> companiesTokens )
    {
        this.companiesTokens = companiesTokens;
    }

    public List<FeedIngestionEntityForSM> getRegionsTokens()
    {
        return regionsTokens;
    }

    public void setRegionsTokens( List<FeedIngestionEntityForSM> regionsTokens )
    {
        this.regionsTokens = regionsTokens;
    }

    public List<FeedIngestionEntityForSM> getBranchesTokens()
    {
        return branchesTokens;
    }

    public void setBranchesTokens( List<FeedIngestionEntityForSM> branchesTokens )
    {
        this.branchesTokens = branchesTokens;
    }

    public List<FeedIngestionEntityForSM> getAgentsTokens()
    {
        return agentsTokens;
    }

    public void setAgentsTokens( List<FeedIngestionEntityForSM> agentsTokens )
    {
        this.agentsTokens = agentsTokens;
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
    
    @Override
    public String toString()
    {
        return "SocialMediaTokensPaginated [companiesTokens=" + companiesTokens + ", regionsTokens=" + regionsTokens
            + ", branchesTokens=" + branchesTokens + ", agentsTokens=" + agentsTokens + "]";
    }
    
}
