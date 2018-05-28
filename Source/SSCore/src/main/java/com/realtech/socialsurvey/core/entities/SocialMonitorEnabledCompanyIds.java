package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.util.List;


public class SocialMonitorEnabledCompanyIds implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<Long> companyIds;


    public List<Long> getCompanyIds()
    {
        return companyIds;
    }


    public void setCompanyIds( List<Long> companyIds )
    {
        this.companyIds = companyIds;
    }


}
