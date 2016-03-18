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
@Table ( name = "UPLOAD_STATUS")
@NamedQuery ( name = "UploadStatus.findAll", query = "SELECT f FROM UploadStatus f")
public class UploadStatus
{
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "UPLOAD_STATUS_ID")
    private long uploadStatusId;
    @Column ( name = "ADMIN_USER_ID")
    private long adminUserId;
    @Column ( name = "MESSAGE")
    private String message;

    @Column ( name = "STATUS")
    private int status;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "COMPANY_ID")
    private Company company;


    /**
     * @return the uploadStatusId
     */
    public long getUploadStatusId()
    {
        return uploadStatusId;
    }


    /**
     * @param uploadStatusId the uploadStatusId to set
     */
    public void setUploadStatusId( long uploadStatusId )
    {
        this.uploadStatusId = uploadStatusId;
    }


    /**
     * @return the adminUserId
     */
    public long getAdminUserId()
    {
        return adminUserId;
    }


    /**
     * @param adminUserId the adminUserId to set
     */
    public void setAdminUserId( long adminUserId )
    {
        this.adminUserId = adminUserId;
    }


    /**
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }


    /**
     * @param message the message to set
     */
    public void setMessage( String message )
    {
        this.message = message;
    }


    /**
     * @return the status
     */
    public int getStatus()
    {
        return status;
    }


    /**
     * @param status the status to set
     */
    public void setStatus( int status )
    {
        this.status = status;
    }


    /**
     * @return the createdOn
     */
    public Timestamp getCreatedOn()
    {
        return createdOn;
    }


    /**
     * @param createdOn the createdOn to set
     */
    public void setCreatedOn( Timestamp createdOn )
    {
        this.createdOn = createdOn;
    }


    /**
     * @return the modifiedOn
     */
    public Timestamp getModifiedOn()
    {
        return modifiedOn;
    }


    /**
     * @param modifiedOn the modifiedOn to set
     */
    public void setModifiedOn( Timestamp modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    /**
     * @return the company
     */
    public Company getCompany()
    {
        return company;
    }


    /**
     * @param company the company to set
     */
    public void setCompany( Company company )
    {
        this.company = company;
    }
}
