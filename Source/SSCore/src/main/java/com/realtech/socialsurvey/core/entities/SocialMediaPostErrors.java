package com.realtech.socialsurvey.core.entities;

import java.util.List;
/**
 * 
 * @author rohit
 *
 */
public class SocialMediaPostErrors
{

    private AgentMediaPostErrors agentMediaPostErrors;

    private List<BranchMediaPostErrors> branchMediaPostErrorsList;

    private List<RegionMediaPostErrors> regionMediaPostErrorsList;

    private CompanyMediaPostErrors companyMediaPostErrors;

    
    public AgentMediaPostErrors getAgentMediaPostErrors()
    {
        return agentMediaPostErrors;
    }

    public void setAgentMediaPostErrors( AgentMediaPostErrors agentMediaPostErrors )
    {
        this.agentMediaPostErrors = agentMediaPostErrors;
    }

    public List<BranchMediaPostErrors> getBranchMediaPostErrorsList()
    {
        return branchMediaPostErrorsList;
    }

    public void setBranchMediaPostErrorsList( List<BranchMediaPostErrors> branchMediaPostErrorsList )
    {
        this.branchMediaPostErrorsList = branchMediaPostErrorsList;
    }

    public List<RegionMediaPostErrors> getRegionMediaPostErrorsList()
    {
        return regionMediaPostErrorsList;
    }

    public void setRegionMediaPostErrorsList( List<RegionMediaPostErrors> regionMediaPostErrorsList )
    {
        this.regionMediaPostErrorsList = regionMediaPostErrorsList;
    }

    public CompanyMediaPostErrors getCompanyMediaPostErrors()
    {
        return companyMediaPostErrors;
    }

    public void setCompanyMediaPostErrors( CompanyMediaPostErrors companyMediaPostErrors )
    {
        this.companyMediaPostErrors = companyMediaPostErrors;
    }
    
}
