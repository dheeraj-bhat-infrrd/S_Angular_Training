package com.realtech.socialsurvey.api.models.v2;

public class ServiceProviderInfoV2
{
    private String serviceProviderName;
    private String serviceProviderEmail;
    private long serviceProviderId;
    
    private String serviceProviderOfficeName;
    private String serviceProviderRegionName;
    private long serviceProviderOfficeId;
    private long serviceProviderRegionId;


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


    public long getServiceProviderOfficeId()
    {
        return serviceProviderOfficeId;
    }


    public void setServiceProviderOfficeId( long serviceProviderOfficeId )
    {
        this.serviceProviderOfficeId = serviceProviderOfficeId;
    }


    public long getServiceProviderRegionId()
    {
        return serviceProviderRegionId;
    }


    public void setServiceProviderRegionId( long serviceProviderRegionId )
    {
        this.serviceProviderRegionId = serviceProviderRegionId;
    }
	
}
