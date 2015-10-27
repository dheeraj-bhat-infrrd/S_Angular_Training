package com.realtech.socialsurvey.core.entities;

import java.util.List;


public class CompanyMediaPostDetails
{
    private long companyId;

    private List<String> sharedOn;


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
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
