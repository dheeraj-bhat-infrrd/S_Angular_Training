package com.realtech.socialsurvey.core.entities;

/**
 * 
 * @author rohit
 *
 */
public class ApiResponseEntity
{
    
    String header;
    
    String url;
    
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
