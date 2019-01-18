package com.realtech.socialsurvey.compute.entities;

public class BranchVO
{
    private long branchId;

    private String branch;

    private long regionId;

    private String regionName;


    public long getRegionId()
    {
        return regionId;
    }


    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }


    public String getRegionName()
    {
        return regionName;
    }


    public void setRegionName( String regionName )
    {
        this.regionName = regionName;
    }


    public long getBranchId()
    {
        return branchId;
    }


    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }


    public String getBranch()
    {
        return branch;
    }


    public void setBranch( String branch )
    {
        this.branch = branch;
    }


    @Override
    public String toString()
    {
        return "BranchVO [branchId=" + branchId + ", branch=" + branch + ", regionId=" + regionId + ", regionName=" + regionName
            + "]";
    }

}
