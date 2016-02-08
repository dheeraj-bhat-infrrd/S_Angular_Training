package com.realtech.socialsurvey.core.entities;

import java.util.List;
import java.util.Map;


public class HierarchyUpload
{
    private List<RegionUploadVO> regions;
    private List<BranchUploadVO> branches;
    private List<UserUploadVO> users;
    private Map<Long, String> regionSourceMapping;
    private Map<Long, String> branchSourceMapping;
    private Map<Long, String> userSourceMapping;


    public List<RegionUploadVO> getRegions()
    {
        return regions;
    }


    public void setRegions( List<RegionUploadVO> regions )
    {
        this.regions = regions;
    }


    public List<BranchUploadVO> getBranches()
    {
        return branches;
    }


    public void setBranches( List<BranchUploadVO> branches )
    {
        this.branches = branches;
    }


    public List<UserUploadVO> getUsers()
    {
        return users;
    }


    public void setUsers( List<UserUploadVO> users )
    {
        this.users = users;
    }


    public Map<Long, String> getRegionSourceMapping()
    {
        return regionSourceMapping;
    }


    public void setRegionSourceMapping( Map<Long, String> regionSourceMapping )
    {
        this.regionSourceMapping = regionSourceMapping;
    }


    public Map<Long, String> getBranchSourceMapping()
    {
        return branchSourceMapping;
    }


    public void setBranchSourceMapping( Map<Long, String> branchSourceMapping )
    {
        this.branchSourceMapping = branchSourceMapping;
    }


    public Map<Long, String> getUserSourceMapping()
    {
        return userSourceMapping;
    }


    public void setUserSourceMapping( Map<Long, String> userSourceMapping )
    {
        this.userSourceMapping = userSourceMapping;
    }


}
