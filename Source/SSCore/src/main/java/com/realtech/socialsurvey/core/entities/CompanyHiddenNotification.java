package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the COMPANY_HIDDEN_NOTIFICATION database table.
 * 
 */
@Entity
@Table ( name = "COMPANY_HIDDEN_NOTIFICATION")
public class CompanyHiddenNotification implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "COMPANY_HIDDEN_NOTIFICATION_ID")
    private long companyHiddenNotificationId;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column ( name = "STATUS")
    private int status;

    @Column ( name = "HIDDEN")
    private boolean hidden;

    //bi-directional many-to-one association to Company
    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "COMPANY_ID")
    private Company company;


    public boolean isHidden()
    {
        return hidden;
    }


    public void setHidden( boolean hidden )
    {
        this.hidden = hidden;
    }


    public long getCompanyHiddenNotificationId()
    {
        return companyHiddenNotificationId;
    }


    public void setCompanyHiddenNotificationId( long companyHiddenNotificationId )
    {
        this.companyHiddenNotificationId = companyHiddenNotificationId;
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


    public int getStatus()
    {
        return status;
    }


    public void setStatus( int status )
    {
        this.status = status;
    }


    public Company getCompany()
    {
        return company;
    }


    public void setCompany( Company company )
    {
        this.company = company;
    }
}
