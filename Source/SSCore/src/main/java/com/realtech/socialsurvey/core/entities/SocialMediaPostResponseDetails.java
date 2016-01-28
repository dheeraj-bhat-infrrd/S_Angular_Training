package com.realtech.socialsurvey.core.entities;

import java.util.List;


/**
 * 
 * @author rohit
 *
 */
public class SocialMediaPostResponseDetails
{

    private AgentMediaPostResponseDetails agentMediaPostResponseDetails;

    private List<BranchMediaPostResponseDetails> branchMediaPostResponseDetailsList;

    private List<RegionMediaPostResponseDetails> regionMediaPostResponseDetailsList;

    private CompanyMediaPostResponseDetails companyMediaPostResponseDetails;

    
    public AgentMediaPostResponseDetails getAgentMediaPostResponseDetails()
    {
        return agentMediaPostResponseDetails;
    }

    public void setAgentMediaPostResponseDetails( AgentMediaPostResponseDetails agentMediaPostResponseDetails )
    {
        this.agentMediaPostResponseDetails = agentMediaPostResponseDetails;
    }

    public List<BranchMediaPostResponseDetails> getBranchMediaPostResponseDetailsList()
    {
        return branchMediaPostResponseDetailsList;
    }

    public void setBranchMediaPostResponseDetailsList( List<BranchMediaPostResponseDetails> branchMediaPostResponseDetailsList )
    {
        this.branchMediaPostResponseDetailsList = branchMediaPostResponseDetailsList;
    }

    public List<RegionMediaPostResponseDetails> getRegionMediaPostResponseDetailsList()
    {
        return regionMediaPostResponseDetailsList;
    }

    public void setRegionMediaPostResponseDetailsList( List<RegionMediaPostResponseDetails> regionMediaPostResponseDetailsList )
    {
        this.regionMediaPostResponseDetailsList = regionMediaPostResponseDetailsList;
    }

    public CompanyMediaPostResponseDetails getCompanyMediaPostResponseDetails()
    {
        return companyMediaPostResponseDetails;
    }

    public void setCompanyMediaPostResponseDetails( CompanyMediaPostResponseDetails companyMediaPostResponseDetails )
    {
        this.companyMediaPostResponseDetails = companyMediaPostResponseDetails;
    }

}
