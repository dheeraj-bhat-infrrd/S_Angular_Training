package com.realtech.socialsurvey.core.entities;

import java.util.List;


public class HierarchyUpload
{
    private String _id;
    private long companyId;

    // hierarchy upload data
    private List<RegionUploadVO> regions;
    private List<BranchUploadVO> branches;
    private List<UserUploadVO> users;


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
