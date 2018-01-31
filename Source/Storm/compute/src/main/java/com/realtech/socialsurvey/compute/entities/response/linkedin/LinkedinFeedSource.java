
package com.realtech.socialsurvey.compute.entities.response.linkedin;

import java.io.Serializable;


/**
 * @author manish
 *
 */
public class LinkedinFeedSource implements Serializable
{
    private static final long serialVersionUID = 1L;

    private LinkedinFeedServiceProvider serviceProvider;
    private String serviceProviderShareId;


    public LinkedinFeedServiceProvider getServiceProvider()
    {
        return serviceProvider;
    }


    public void setServiceProvider( LinkedinFeedServiceProvider serviceProvider )
    {
        this.serviceProvider = serviceProvider;
    }


    public String getServiceProviderShareId()
    {
        return serviceProviderShareId;
    }


    public void setServiceProviderShareId( String serviceProviderShareId )
    {
        this.serviceProviderShareId = serviceProviderShareId;
    }


    @Override
    public String toString()
    {
        return "LinkedinFeedSource [serviceProvider=" + serviceProvider + ", serviceProviderShareId=" + serviceProviderShareId
            + "]";
    }
}
