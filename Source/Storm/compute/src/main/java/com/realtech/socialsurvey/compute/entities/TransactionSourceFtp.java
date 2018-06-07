package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;
import java.util.Map;


public class TransactionSourceFtp implements Serializable
{
    private static final long serialVersionUID = 1L;
    private long ftpId;
    private String username;
    private String password;
    private String ftpServerLabel;
    private String ftpDirectoryPath;
    private Map<String, String> fileHeaderMapper;
    private String status;
    private long companyId;
    private String ftpSource;


    public long getFtpId()
    {
        return ftpId;
    }


    public void setFtpId( long ftpId )
    {
        this.ftpId = ftpId;
    }


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


    public String getFtpServerLabel()
    {
        return ftpServerLabel;
    }


    public void setFtpServerLabel( String ftpServerLabel )
    {
        this.ftpServerLabel = ftpServerLabel;
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


    public String getStatus()
    {
        return status;
    }


    public void setStatus( String status )
    {
        this.status = status;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public String getFtpSource()
    {
        return ftpSource;
    }


    public void setFtpSource( String ftpSource )
    {
        this.ftpSource = ftpSource;
    }


    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }


    @Override
    public String toString()
    {
        return "TransactionSourceFtp [ftpId=" + ftpId + ", username=" + username + ", password=" + password
            + ", ftpServerLabel=" + ftpServerLabel + ", ftpDirectoryPath=" + ftpDirectoryPath + ", fileHeaderMapper="
            + fileHeaderMapper + ", status=" + status + ", companyId=" + companyId + ", ftpSource=" + ftpSource + "]";
    }


}
