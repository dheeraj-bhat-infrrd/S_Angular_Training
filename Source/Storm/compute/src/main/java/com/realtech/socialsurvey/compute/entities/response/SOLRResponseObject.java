package com.realtech.socialsurvey.compute.entities.response;

/**
 * Successful response from SOLR
 * @author nishit
 *
 */
public class SOLRResponseObject<T>
{
    private SOLRResponseHeader responseHeader;
    private SOLRResponse<T> response;


    public SOLRResponseHeader getResponseHeader()
    {
        return responseHeader;
    }


    public void setResponseHeader( SOLRResponseHeader responseHeader )
    {
        this.responseHeader = responseHeader;
    }


    public SOLRResponse<T> getResponse()
    {
        return response;
    }


    public void setResponse( SOLRResponse<T> response )
    {
        this.response = response;
    }


}
