package com.realtech.socialsurvey.core.entities;

import java.util.List;
import java.util.Map;


public class HierarchyUpload
{
    private String _id;
    private long companyId;
    private List<RegionUploadVO> regions;
    private List<BranchUploadVO> branches;
    private List<UserUploadVO> users;
    private Map<String, Long> regionSourceMapping;
    private Map<String, Long> branchSourceMapping;
    private Map<String, Long> userSourceMapping;


    public List<RegionUploadVO> getRegions()
    {
        return regions;
    }


    public String get_id()
    {
        return _id;
    }


    public void set_id( String _id )
    {
        this._id = _id;
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


    public Map<String, Long> getRegionSourceMapping()
    {
        return regionSourceMapping;
    }


    public void setRegionSourceMapping( Map<String, Long> regionSourceMapping )
    {
        this.regionSourceMapping = regionSourceMapping;
    }


    public Map<String, Long> getBranchSourceMapping()
    {
        return branchSourceMapping;
    }


    public void setBranchSourceMapping( Map<String, Long> branchSourceMapping )
    {
        this.branchSourceMapping = branchSourceMapping;
    }


    public Map<String, Long> getUserSourceMapping()
    {
        return userSourceMapping;
    }


    public void setUserSourceMapping( Map<String, Long> userSourceMapping )
    {
        this.userSourceMapping = userSourceMapping;
    }


    /**
     * @return the companyId
     */
    public long getCompanyId()
    {
        return companyId;
    }


    /**
     * @param companyId the companyId to set
     */
    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


}
