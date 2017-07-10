package com.realtech.socialsurvey.core.entities;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@Table ( name = "FILE_UPLOAD")
@NamedQuery ( name = "FileUpload.findAll", query = "SELECT f FROM FileUpload f")
public class FileUpload
{

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "FILE_UPLOAD_ID")
    private long fileUploadId;
    @Column ( name = "ADMIN_USER_ID")
    private long adminUserId;
    @Column ( name = "FILE_NAME")
    private String fileName;
    @Column ( name = "UPLOAD_TYPE")
    private int uploadType;
    @Column ( name = "STATUS")
    private int status;
    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;
    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;
    @Column ( name = "START_DATE")
    private Timestamp startDate;
    @Column ( name = "END_DATE")
    private Timestamp endDate;
    @Column ( name = "PROFILE_LEVEL")
    private String profileLevel;
    @Column ( name = "PROFILE_VALUE")
    private long profileValue;
    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "COMPANY_ID")
    private Company company;
    @Column ( name = "SHOW_ON_UI")
    private boolean showOnUI;


    public String getProfileLevel()
    {
        return profileLevel;
    }


    public void setProfileLevel( String profileLevel )
    {
        this.profileLevel = profileLevel;
    }


    public long getProfileValue()
    {
        return profileValue;
    }


    public void setProfileValue( long profileValue )
    {
        this.profileValue = profileValue;
    }


    public Timestamp getStartDate()
    {
        return startDate;
    }


    public void setStartDate( Timestamp startDate )
    {
        this.startDate = startDate;
    }


    public Timestamp getEndDate()
    {
        return endDate;
    }


    public void setEndDate( Timestamp endDate )
    {
        this.endDate = endDate;
    }


    public long getFileUploadId()
    {
        return fileUploadId;
    }


    public void setFileUploadId( long fileUploadId )
    {
        this.fileUploadId = fileUploadId;
    }


    public long getAdminUserId()
    {
        return adminUserId;
    }


    public void setAdminUserId( long adminUserId )
    {
        this.adminUserId = adminUserId;
    }


    public String getFileName()
    {
        return fileName;
    }


    public void setFileName( String fileName )
    {
        this.fileName = fileName;
    }


    public int getUploadType()
    {
        return uploadType;
    }


    public void setUploadType( int uploadType )
    {
        this.uploadType = uploadType;
    }


    public int getStatus()
    {
        return status;
    }


    public void setStatus( int status )
    {
        this.status = status;
    }


    public Timestamp getCreatedOn()
    {
        return createdOn;
    }


    public void setCreatedOn( Timestamp createdOn )
    {
        this.createdOn = createdOn;
    }


    public Timestamp getModifiedOn()
    {
        return modifiedOn;
    }


    public void setModifiedOn( Timestamp modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    public Company getCompany()
    {
        return company;
    }


    public void setCompany( Company company )
    {
        this.company = company;
    }


    public boolean isShowOnUI()
    {
        return showOnUI;
    }


    public void setShowOnUI( boolean showOnUI )
    {
        this.showOnUI = showOnUI;
    }
    
    
    

}
