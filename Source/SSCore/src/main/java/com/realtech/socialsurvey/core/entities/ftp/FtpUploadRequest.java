package com.realtech.socialsurvey.core.entities.ftp;

import java.io.Serializable;

public class FtpUploadRequest implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private long companyId;
    private long ftpId;
    private String s3FileLocation;


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public long getFtpId()
    {
        return ftpId;
    }


    public void setFtpId( long ftpId )
    {
        this.ftpId = ftpId;
    }


    public String getS3FileLocation()
    {
        return s3FileLocation;
    }


    public void setS3FileLocation( String s3FileLocation )
    {
        this.s3FileLocation = s3FileLocation;
    }


    @Override
    public String toString()
    {
        return "FtpUploadRequest [companyId=" + companyId + ", ftpId=" + ftpId + ", s3FileLocation=" + s3FileLocation + "]";
    }

}
