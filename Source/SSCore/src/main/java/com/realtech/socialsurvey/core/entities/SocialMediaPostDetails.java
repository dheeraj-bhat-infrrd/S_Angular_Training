package com.realtech.socialsurvey.core.entities;

import java.util.List;


public class SocialMediaPostDetails
{

    private AgentMediaPostDetails agentMediaPostDetails;

    private List<BranchMediaPostDetails> branchMediaPostDetailsList;

    private List<RegionMediaPostDetails> regionMediaPostDetailsList;

    private CompanyMediaPostDetails companyMediaPostDetails;


    public AgentMediaPostDetails getAgentMediaPostDetails()
    {
        return agentMediaPostDetails;
    }


    public void setAgentMediaPostDetails( AgentMediaPostDetails agentMediaPostDetails )
    {
        this.agentMediaPostDetails = agentMediaPostDetails;
    }


    public List<BranchMediaPostDetails> getBranchMediaPostDetailsList()
    {
        return branchMediaPostDetailsList;
    }


    public void setBranchMediaPostDetailsList( List<BranchMediaPostDetails> branchMediaPostDetailsList )
    {
        this.branchMediaPostDetailsList = branchMediaPostDetailsList;
    }


    public List<RegionMediaPostDetails> getRegionMediaPostDetailsList()
    {
        return regionMediaPostDetailsList;
    }


    public void setRegionMediaPostDetailsList( List<RegionMediaPostDetails> regionMediaPostDetailsList )
    {
        this.regionMediaPostDetailsList = regionMediaPostDetailsList;
    }


    public CompanyMediaPostDetails getCompanyMediaPostDetails()
    {
        return companyMediaPostDetails;
    }


    public void setCompanyMediaPostDetails( CompanyMediaPostDetails companyMediaPostDetails )
    {
        this.companyMediaPostDetails = companyMediaPostDetails;
    }


}