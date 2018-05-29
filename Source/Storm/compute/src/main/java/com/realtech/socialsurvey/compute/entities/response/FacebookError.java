package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * @author manish
 *
 */
public class FacebookError implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String message;
    private int code;
    private String type;
    @SerializedName("error_subcode")
    private String errorSubcode;

    @SerializedName("fbtrace_id")
    private String fbTraceId;


    public String getMessage()
    {
        return message;
    }


    public void setMessage( String message )
    {
        this.message = message;
    }


    public String getType()
    {
        return type;
    }


    public void setType( String type )
    {
        this.type = type;
    }


    public String getErrorSubcode()
    {
        return errorSubcode;
    }


    public void setErrorSubcode( String errorSubcode )
    {
        this.errorSubcode = errorSubcode;
    }


    public String getFbTraceId()
    {
        return fbTraceId;
    }


    public void setFbTraceId( String fbTraceId )
    {
        this.fbTraceId = fbTraceId;
    }


    public int getCode()
    {
        return code;
    }


    public void setCode( int code )
    {
        this.code = code;
    }
}