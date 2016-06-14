package com.realtech.socialsurvey.core.entities;

/**
 * Holds the company settings from mongo and the company object from mysql
 */
public class CompanyCompositeEntity
{
    private Company company;
    private OrganizationUnitSettings companySettings;


    public Company getCompany()
    {
        return company;
    }


    public void setCompany( Company company )
    {
        this.company = company;
    }


    public OrganizationUnitSettings getCompanySettings()
    {
        return companySettings;
    }


    public void setCompanySettings( OrganizationUnitSettings companySettings )
    {
        this.companySettings = companySettings;
    }
}
