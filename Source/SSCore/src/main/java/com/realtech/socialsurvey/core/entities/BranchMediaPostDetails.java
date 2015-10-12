package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class BranchMediaPostDetails
{
    private long branchId;

    private List<String> sharedOn;


    public long getBranchId()
    {
        return branchId;
    }


    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }


    public List<String> getSharedOn()
    {
        return sharedOn;
    }


    public void setSharedOn( List<String> sharedOn )
    {
        this.sharedOn = sharedOn;
    }
}
