package com.realtech.socialsurvey.core.vo;

import java.io.Serializable;


/**
 * @author manish
 *
 */
public class SocialMediaStatusVO implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String url;
    private boolean connected;


    public String getUrl()
    {
        return url;
    }


    public void setUrl( String url )
    {
        this.url = url;
    }


    public boolean isConnected()
    {
        return connected;
    }


    public void setConnected( boolean connected )
    {
        this.connected = connected;
    }


    @Override
    public String toString()
    {
        return "SocialMediaStatusVO [url=" + url + ", connected=" + connected + "]";
    }
}
