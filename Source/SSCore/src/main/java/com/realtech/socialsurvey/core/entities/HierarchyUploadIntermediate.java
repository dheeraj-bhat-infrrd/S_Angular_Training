package com.realtech.socialsurvey.core.entities;

import java.util.Map;


public class HierarchyUploadIntermediate
{
    private Company company;

    // hierarchy upload map
    private Map<String, RegionUploadVO> regions;
    private Map<String, BranchUploadVO> branches;
    private Map<String, UserUploadVO> users;

    // used in name duplication mechanism
    private Map<String, String> regionNameMap;
    private Map<String, String> branchNameMap;

    // used in error handling
    private int regionsProcessed;
    private int branchesProcessed;
    private int usersProcessed;


    public int getRegionsProcessed()
    {
        return regionsProcessed;
    }


    public void setRegionsProcessed( int regionsProcessed )
    {
        this.regionsProcessed = regionsProcessed;
    }


    public int getBranchesProcessed()
    {
        return branchesProcessed;
    }


    public void setBranchesProcessed( int branchesProcessed )
    {
        this.branchesProcessed = branchesProcessed;
    }


    public int getUsersProcessed()
    {
        return usersProcessed;
    }


    public void setUsersProcessed( int usersProcessed )
    {
        this.usersProcessed = usersProcessed;
    }


    public Company getCompany()
    {
        return company;
    }


    public void setCompany( Company company )
    {
        this.company = company;
    }


    public Map<String, RegionUploadVO> getRegions()
    {
        return regions;
    }


    public void setRegions( Map<String, RegionUploadVO> regions )
    {
        this.regions = regions;
    }


    public Map<String, BranchUploadVO> getBranches()
    {
        return branches;
    }


    public void setBranches( Map<String, BranchUploadVO> branches )
    {
        this.branches = branches;
    }


    public Map<String, UserUploadVO> getUsers()
    {
        return users;
    }


    public void setUsers( Map<String, UserUploadVO> users )
    {
        this.users = users;
    }


    public Map<String, String> getRegionNameMap()
    {
        return regionNameMap;
    }


    public void setRegionNameMap( Map<String, String> regionNameMap )
    {
        this.regionNameMap = regionNameMap;
    }


    public Map<String, String> getBranchNameMap()
    {
        return branchNameMap;
    }


    public void setBranchNameMap( Map<String, String> branchNameMap )
    {
        this.branchNameMap = branchNameMap;
    }

}
