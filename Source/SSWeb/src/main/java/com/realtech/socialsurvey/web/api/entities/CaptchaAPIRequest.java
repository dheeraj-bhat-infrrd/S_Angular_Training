package com.realtech.socialsurvey.web.api.entities;

import java.io.Serializable;


/**
 * Captcha request to be sent to API
 */
public class CaptchaAPIRequest implements Serializable
{
    private String remoteAddress;
    private String captchaResponse;


    public String getRemoteAddress()
    {
        return remoteAddress;
    }


    public void setRemoteAddress( String remoteAddress )
    {
        this.remoteAddress = remoteAddress;
    }


    public String getCaptchaResponse()
    {
        return captchaResponse;
    }


    public void setCaptchaResponse( String captchaResponse )
    {
        this.captchaResponse = captchaResponse;
    }


    @Override public String toString()
    {
        return "CaptchaRequest{" +
            "remoteAddress='" + remoteAddress + '\'' +
            ", captchaResponse='" + captchaResponse + '\'' +
            '}';
    }
}
