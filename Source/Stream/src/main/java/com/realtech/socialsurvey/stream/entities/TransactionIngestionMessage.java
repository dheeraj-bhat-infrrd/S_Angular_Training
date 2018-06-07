package com.realtech.socialsurvey.stream.entities;

public class TransactionIngestionMessage
{
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
        return "TransactionIngestionMessage [companyId=" + companyId + ", ftpId=" + ftpId + ", s3FileLocation=" + s3FileLocation
            + "]";
    }

}
