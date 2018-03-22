package com.realtech.socialsurvey.api.models.v2;

public class ServiceProviderInfoV2
{
    private String serviceProviderName;
    private String serviceProviderEmail;
    private long serviceProviderId;


    public long getServiceProviderId() {
		return serviceProviderId;
	}


	public void setServiceProviderId(long serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}


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
