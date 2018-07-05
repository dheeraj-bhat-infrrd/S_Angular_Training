package com.realtech.socialsurvey.compute.entities;

import java.io.Serializable;

public class TransactionIngestionMessage implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private long companyId;
    private String s3FileLocation;
    private long ftpId;


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }

    public String getS3FileLocation()
    {
        return s3FileLocation;
    }


    public void setS3FileLocation( String s3FileLocation )
    {
        this.s3FileLocation = s3FileLocation;
    }

    public long getFtpId()
    {
        return ftpId;
    }


    public void setFtpId( long ftpId )
    {
        this.ftpId = ftpId;
    }


    @Override
    public String toString()
    {
        return "TransactionIngestionMessage [companyId=" + companyId + ", s3FileLocation=" + s3FileLocation + ", ftpId=" + ftpId
            + "]";
    }

}
