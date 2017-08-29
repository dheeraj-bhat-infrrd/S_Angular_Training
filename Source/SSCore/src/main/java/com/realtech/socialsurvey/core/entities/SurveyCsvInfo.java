package com.realtech.socialsurvey.core.entities;

import java.util.Date;


public class SurveyCsvInfo
{
    private String hierarchyType;
    private long hierarchyId;
    private String fileUrl;
    private String fileName;
    private Date uploadedDate;
    private long userId;


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


    public long getUserId()
    {
        return userId;
    }


    public void setUserId( long userId )
    {
        this.userId = userId;
    }

}
