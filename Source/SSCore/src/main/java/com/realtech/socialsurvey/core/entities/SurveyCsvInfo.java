package com.realtech.socialsurvey.core.entities;

import java.util.Date;


public class SurveyCsvInfo
{
    private String _id;
    private String hierarchyType;
    private long hierarchyId;
    private String fileUrl;
    private String fileName;
    private Date uploadedDate;
    private long initiatedUserId;
    private long companyId;
    private String uploaderEmail;
    private int status;
    private Date csvUploadCompletedDate;


    public String get_id()
    {
        return _id;
    }


    public void set_id( String _id )
    {
        this._id = _id;
    }


    public int getStatus()
    {
        return status;
    }


    public void setStatus( int status )
    {
        this.status = status;
    }


    public Date getCsvUploadCompletedDate()
    {
        return csvUploadCompletedDate;
    }


    public void setCsvUploadCompletedDate( Date csvUploadCompletedDate )
    {
        this.csvUploadCompletedDate = csvUploadCompletedDate;
    }


    public String getHierarchyType()
    {
        return hierarchyType;
    }


    public void setHierarchyType( String hierarchyType )
    {
        this.hierarchyType = hierarchyType;
    }


    public long getHierarchyId()
    {
        return hierarchyId;
    }


    public void setHierarchyId( long hierarchyId )
    {
        this.hierarchyId = hierarchyId;
    }


    public String getFileUrl()
    {
        return fileUrl;
    }


    public void setFileUrl( String fileUrl )
    {
        this.fileUrl = fileUrl;
    }


    public String getFileName()
    {
        return fileName;
    }


    public void setFileName( String fileName )
    {
        this.fileName = fileName;
    }


    public Date getUploadedDate()
    {
        return uploadedDate;
    }


    public void setUploadedDate( Date uploadedDate )
    {
        this.uploadedDate = uploadedDate;
    }


    public long getInitiatedUserId()
    {
        return initiatedUserId;
    }


    public void setInitiatedUserId( long initiatedUserId )
    {
        this.initiatedUserId = initiatedUserId;
    }


    public String getUploaderEmail()
    {
        return uploaderEmail;
    }


    public void setUploaderEmail( String uploaderEmail )
    {
        this.uploaderEmail = uploaderEmail;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }
}
