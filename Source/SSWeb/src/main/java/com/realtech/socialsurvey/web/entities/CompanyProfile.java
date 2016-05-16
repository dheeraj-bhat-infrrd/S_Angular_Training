package com.realtech.socialsurvey.web.entities;

import com.realtech.socialsurvey.core.entities.Location;
import com.realtech.socialsurvey.core.entities.Phone;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;


public class CompanyProfile
{
    private String companyName;
    private String companyLogo;
    private VerticalsMaster industry;
    private Location locaton;
    private String address;
    private String city;
    private String state;
    private String zip;
    private Phone officePhone;


    public VerticalsMaster getIndustry()
    {
        return industry;
    }


    public void setIndustry( VerticalsMaster industry )
    {
        this.industry = industry;
    }


    public Location getLocaton()
    {
        return locaton;
    }


    public void setLocaton( Location locaton )
    {
        this.locaton = locaton;
    }


    public String getAddress()
    {
        return address;
    }


    public void setAddress( String address )
    {
        this.address = address;
    }


    public String getCity()
    {
        return city;
    }


    public void setCity( String city )
    {
        this.city = city;
    }


    public String getState()
    {
        return state;
    }


    public void setState( String state )
    {
        this.state = state;
    }


    public String getZip()
    {
        return zip;
    }


    public void setZip( String zip )
    {
        this.zip = zip;
    }


    public Phone getOfficePhone()
    {
        return officePhone;
    }


    public void setOfficePhone( Phone officePhone )
    {
        this.officePhone = officePhone;
    }


    public String getCompanyName()
    {
        return companyName;
    }


    public void setCompanyName( String companyName )
    {
        this.companyName = companyName;
    }


    public String getCompanyLogo()
    {
        return companyLogo;
    }


    public void setCompanyLogo( String companyLogo )
    {
        this.companyLogo = companyLogo;
    }
}
