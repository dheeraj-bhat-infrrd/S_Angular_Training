package com.realtech.socialsurvey.core.entities;

import java.util.Date;

public class ApiRequestDetails
{
    
    private ApiRequestEntity request;
    
    private ApiResponseEntity response;
    
    private Long companyId ;
    
    private Date createdOn;

    
    public Long getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId( Long companyId )
    {
        this.companyId = companyId;
    }
    

    public Date getCreatedOn()
    {
        return createdOn;
    }

    public void setCreatedOn( Date createdOn )
    {
        this.createdOn = createdOn;
    }

    public ApiRequestEntity getRequest()
    {
        return request;
    }

    public void setRequest( ApiRequestEntity request )
    {
        this.request = request;
    }

    public ApiResponseEntity getResponse()
    {
        return response;
    }

    public void setResponse( ApiResponseEntity response )
    {
        this.response = response;
    }

}
