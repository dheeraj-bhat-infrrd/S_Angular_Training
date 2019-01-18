package com.realtech.socialsurvey.core.vo;

/**
 * @author manish
 *
 */
public class BranchVO
{
    private long branchId;
    private String branch;
    private String address1;
    private String address2;
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
    public String getAddress1()
    {
        return address1;
    }
    public void setAddress1( String address1 )
    {
        this.address1 = address1;
    }
    public String getAddress2()
    {
        return address2;
    }
    public void setAddress2( String address2 )
    {
        this.address2 = address2;
    }
    
    
}
