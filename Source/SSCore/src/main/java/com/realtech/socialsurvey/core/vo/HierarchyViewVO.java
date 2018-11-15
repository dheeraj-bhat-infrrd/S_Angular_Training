package com.realtech.socialsurvey.core.vo;

import java.io.Serializable;
import java.util.List;

public class HierarchyViewVO implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<RegionVO> regions;
    private List<BranchVO> branches;
    private List<UserFromSearchVO> users;
    private CompanyNotRegisteredVO companyNotRegistered;
    
    public CompanyNotRegisteredVO getCompanyNotRegistered()
    {
        return companyNotRegistered;
    }
    public void setCompanyNotRegistered( CompanyNotRegisteredVO companyNotRegistered )
    {
        this.companyNotRegistered = companyNotRegistered;
    }
    public List<RegionVO> getRegions()
    {
        return regions;
    }
    public void setRegions( List<RegionVO> regions )
    {
        this.regions = regions;
    }
    public List<BranchVO> getBranches()
    {
        return branches;
    }
    public void setBranches( List<BranchVO> branches )
    {
        this.branches = branches;
    }
    public List<UserFromSearchVO> getUsers()
    {
        return users;
    }
    public void setUsers( List<UserFromSearchVO> users )
    {
        this.users = users;
    }
    
    @Override
    public String toString()
    {
        return "HierarchyViewVO [regions=" + regions + ", branches=" + branches + ", users=" + users + ", companyNotRegistered="
            + companyNotRegistered + "]";
    }
}
