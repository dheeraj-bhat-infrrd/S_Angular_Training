package com.realtech.socialsurvey.core.entities;

public class ApiRequestEntity
{
    
    String header;
    
    String url;
    
    String requestMethod;
    
    String statusCode;
    
    String body;
    
    public String getHeader()
    {
        return header;
    }

    public void setHeader( String header )
    {
        this.header = header;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public String getRequestMethod()
    {
        return requestMethod;
    }

    public void setRequestMethod( String requestMethod )
    {
        this.requestMethod = requestMethod;
    }
    
    public String getStatusCode()
    {
        return statusCode;
    }

    public void setStatusCode( String statusCode )
    {
        this.statusCode = statusCode;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody( String body )
    {
        this.body = body;
    }


}
