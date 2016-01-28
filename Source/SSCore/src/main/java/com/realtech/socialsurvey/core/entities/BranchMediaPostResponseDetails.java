package com.realtech.socialsurvey.core.entities;

/**
 * 
 * @author rohit
 *
 */
public class BranchMediaPostResponseDetails extends EntityMediaPostResponseDetails
{

    private long branchId;

    private long regionId;

    
    public long getBranchId()
    {
        return branchId;
    }

    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }

    public long getRegionId()
    {
        return regionId;
    }

    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }
    
    
}
