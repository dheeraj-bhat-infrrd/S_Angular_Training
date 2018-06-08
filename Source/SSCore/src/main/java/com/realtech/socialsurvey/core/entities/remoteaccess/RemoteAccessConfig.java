package com.realtech.socialsurvey.core.entities.remoteaccess;

import java.io.Serializable;

import com.realtech.socialsurvey.core.enums.remoteaccess.RemoteAccessAuthentication;


public class RemoteAccessConfig implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String host;
    private String userName;
    private int port;
    private RemoteAccessAuthentication preferredAuthentication;
    private String keyPath;
    private String password;
    private boolean allowKnownHostsOnly;
    private String knownHostsPath;


    public String getHost()
    {
        return host;
    }


    public void setHost( String host )
    {
        this.host = host;
    }


    public String getUserName()
    {
        return userName;
    }


    public void setUserName( String userName )
    {
        this.userName = userName;
    }


    public int getPort()
    {
        return port;
    }


    public void setPort( int port )
    {
        this.port = port;
    }


    public RemoteAccessAuthentication getPreferredAuthentication()
    {
        return preferredAuthentication;
    }


    public void setPreferredAuthentication( RemoteAccessAuthentication preferredAuthentication )
    {
        this.preferredAuthentication = preferredAuthentication;
    }


    public String getKeyPath()
    {
        return keyPath;
    }


    public void setKeyPath( String keyPath )
    {
        this.keyPath = keyPath;
    }


    public String getPassword()
    {
        return password;
    }


    public void setPassword( String password )
    {
        this.password = password;
    }


    public boolean isAllowKnownHostsOnly()
    {
        return allowKnownHostsOnly;
    }


    public void setAllowKnownHostsOnly( boolean allowKnownHostsOnly )
    {
        this.allowKnownHostsOnly = allowKnownHostsOnly;
    }


    public String getKnownHostsPath()
    {
        return knownHostsPath;
    }


    public void setKnownHostsPath( String knownHostsPath )
    {
        this.knownHostsPath = knownHostsPath;
    }


    @Override
    public String toString()
    {
        return "RemoteAccessConfig [host=" + host + ", userName=" + userName + ", port=" + port + ", preferredAuthentication="
            + preferredAuthentication + ", keyPath=" + keyPath + ", password=" + password + ", allowKnownHostsOnly="
            + allowKnownHostsOnly + ", knownHostsPath=" + knownHostsPath + "]";
    }

}
