package com.realtech.socialsurvey.api.models.v2;

public class ServiceProviderInfoV2
{
    private String serviceProviderName;
    private String serviceProviderEmail;


    public String getServiceProviderName()
    {
        return serviceProviderName;
    }


    public void setServiceProviderName( String serviceProviderName )
    {
        this.serviceProviderName = serviceProviderName;
    }


    public String getServiceProviderEmail()
    {
        return serviceProviderEmail;
    }


    public void setServiceProviderEmail( String serviceProviderEmail )
    {
        this.serviceProviderEmail = serviceProviderEmail;
    }
}
