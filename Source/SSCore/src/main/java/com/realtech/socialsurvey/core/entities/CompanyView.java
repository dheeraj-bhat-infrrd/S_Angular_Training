package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;


public class CompanyView implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private long companyId;
    private String company;


    public CompanyView()
    {}


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public String getCompany()
    {
        return company;
    }


    public void setCompany( String company )
    {
        this.company = company;
    }


    @Override
    public String toString()
    {
        return "CompanyView [companyId=" + companyId + ", company=" + company + "]";
    }


}
