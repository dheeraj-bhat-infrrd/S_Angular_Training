package com.realtech.socialsurvey.core.entities;

public class ApiRequestDetails
{
    
    private ApiRequestEntity request;
    
    private ApiResponseEntity response;
    

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
