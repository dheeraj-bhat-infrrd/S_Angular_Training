package com.realtech.socialsurvey.api.models.v2;

public class ServiceProviderInfoV2
{
    private String serviceProviderName;
    private String serviceProviderEmail;
    private long serviceProviderId;
    
    private String serviceProviderOfficeName;
    private String serviceProviderRegionName;


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


	public String getServiceProviderOfficeName() {
		return serviceProviderOfficeName;
	}


	public void setServiceProviderOfficeName(String serviceProviderOfficeName) {
		this.serviceProviderOfficeName = serviceProviderOfficeName;
	}


	public String getServiceProviderRegionName() {
		return serviceProviderRegionName;
	}


	public void setServiceProviderRegionName(String serviceProviderRegionName) {
		this.serviceProviderRegionName = serviceProviderRegionName;
	}
}
