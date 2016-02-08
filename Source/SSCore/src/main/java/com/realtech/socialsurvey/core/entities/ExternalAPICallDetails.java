package com.realtech.socialsurvey.core.entities;

import java.util.Date;


public class ExternalAPICallDetails
{
    private String request;
    private String response;
    private String httpMethod;
    private String source;
    private Date requestTime;


    /**
     * @return the request
     */
    public String getRequest()
    {
        return request;
    }


    /**
     * @param request the request to set
     */
    public void setRequest( String request )
    {
        this.request = request;
    }


    /**
     * @return the httpMethod
     */
    public String getHttpMethod()
    {
        return httpMethod;
    }


    /**
     * @param httpMethod the httpMethod to set
     */
    public void setHttpMethod( String httpMethod )
    {
        this.httpMethod = httpMethod;
    }


    /**
     * @return the source
     */
    public String getSource()
    {
        return source;
    }


    /**
     * @param source the source to set
     */
    public void setSource( String source )
    {
        this.source = source;
    }


    /**
     * @return the requestTime
     */
    public Date getRequestTime()
    {
        return requestTime;
    }


    /**
     * @param requestTime the requestTime to set
     */
    public void setRequestTime( Date requestTime )
    {
        this.requestTime = requestTime;
    }


    public String getResponse()
    {
        return response;
    }


    public void setResponse( String response )
    {
        this.response = response;
    }
}