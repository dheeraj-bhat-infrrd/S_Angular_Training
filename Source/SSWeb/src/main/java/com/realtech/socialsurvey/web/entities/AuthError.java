package com.realtech.socialsurvey.web.entities;

import java.io.Serializable;


/**
 * Object ot convert Oauth error
 */
public class AuthError implements Serializable
{
    private String status;
    private String errorCode;
    private String reason;


    public String getStatus()
    {
        return status;
    }


    public void setStatus( String status )
    {
        this.status = status;
    }


    public String getErrorCode()
    {
        return errorCode;
    }


    public void setErrorCode( String errorCode )
    {
        this.errorCode = errorCode;
    }


    public String getReason()
    {
        return reason;
    }


    public void setReason( String reason )
    {
        this.reason = reason;
    }


    @Override public String toString()
    {
        return "AuthError{" +
            "errorCode='" + errorCode + '\'' +
            ", reason='" + reason + '\'' +
            '}';
    }
}
