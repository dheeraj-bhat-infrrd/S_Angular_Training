package com.realtech.socialsurvey.web.api.entities;

import java.io.Serializable;
import java.util.Map;


public class FtpCreateRequest implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String ftpServerUrl;
    private String ftpDirectoryPath;
    private Map<String, String> fileHeaderMapper;


    public String getUsername()
    {
        return username;
    }


    public void setUsername( String username )
    {
        this.username = username;
    }


    public String getPassword()
    {
        return password;
    }


    public void setPassword( String password )
    {
        this.password = password;
    }


    public String getFtpServerUrl()
    {
        return ftpServerUrl;
    }


    public void setFtpServerUrl( String ftpServerUrl )
    {
        this.ftpServerUrl = ftpServerUrl;
    }


    public String getFtpDirectoryPath()
    {
        return ftpDirectoryPath;
    }


    public void setFtpDirectoryPath( String ftpDirectoryPath )
    {
        this.ftpDirectoryPath = ftpDirectoryPath;
    }


    public Map<String, String> getFileHeaderMapper()
    {
        return fileHeaderMapper;
    }


    public void setFileHeaderMapper( Map<String, String> fileHeaderMapper )
    {
        this.fileHeaderMapper = fileHeaderMapper;
    }


    @Override
    public String toString()
    {
        return "FtpCreateRequest [username=" + username + ", password=" + password + ", ftpServerUrl=" + ftpServerUrl
            + ", ftpDirectoryPath=" + ftpDirectoryPath + ", fileHeaderMapper=" + fileHeaderMapper + "]";
    }
}
