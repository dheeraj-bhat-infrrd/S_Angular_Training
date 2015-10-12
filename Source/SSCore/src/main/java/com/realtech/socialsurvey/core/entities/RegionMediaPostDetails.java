package com.realtech.socialsurvey.core.entities;

import java.util.List;

public class RegionMediaPostDetails
{
    private long regionId;

    private List<String> sharedOn;


    public long getRegionId()
    {
        return regionId;
    }


    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
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
