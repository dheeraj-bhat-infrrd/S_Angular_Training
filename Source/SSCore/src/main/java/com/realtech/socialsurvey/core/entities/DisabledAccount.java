package com.realtech.socialsurvey.core.entities;
//JIRA: SS-61: By RM03

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
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistant class for DISABLED_ACCOUNTS table in the database.
 */
@Entity
@Table ( name = "DISABLED_ACCOUNTS")
@NamedQuery ( name = "DisabledAccount.findAll", query = "SELECT d FROM DisabledAccount d")
public class DisabledAccount implements Serializable
{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "ID")
    private long id;

    // bi-directional many-to-one association to Company
    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "COMPANY_ID")
    private Company company;

    // bi-directional many-to-one association to Company
    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "LICENSE_ID")
    private LicenseDetail licenseDetail;

    @Column ( name = "DISABLE_DATE")
    private Timestamp disableDate;

    @Column ( name = "STATUS")
    private int status;

    @Column ( name = "CREATED_BY")
    private String createdBy;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "MODIFIED_BY")
    private String modifiedBy;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column ( name = "IS_FORCE_DELETE")
    private boolean isForceDelete;


    public boolean isForceDelete()
    {
        return isForceDelete;
    }


    public void setForceDelete( boolean isForceDelete )
    {
        this.isForceDelete = isForceDelete;
    }


    public long getId()
    {
        return id;
    }


    public Company getCompany()
    {
        return company;
    }


    public LicenseDetail getLicenseDetail()
    {
        return licenseDetail;
    }


    public Timestamp getDisableDate()
    {
        return disableDate;
    }


    public int getStatus()
    {
        return status;
    }


    public String getCreatedBy()
    {
        return createdBy;
    }


    public Timestamp getCreatedOn()
    {
        return createdOn;
    }


    public String getModifiedBy()
    {
        return modifiedBy;
    }


    public Timestamp getModifiedOn()
    {
        return modifiedOn;
    }


    public void setId( long id )
    {
        this.id = id;
    }


    public void setCompany( Company company )
    {
        this.company = company;
    }


    public void setLicenseDetail( LicenseDetail licenseDetail )
    {
        this.licenseDetail = licenseDetail;
    }


    public void setDisableDate( Timestamp disableDate )
    {
        this.disableDate = disableDate;
    }


    public void setStatus( int status )
    {
        this.status = status;
    }


    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }


    public void setCreatedOn( Timestamp createdOn )
    {
        this.createdOn = createdOn;
    }


    public void setModifiedBy( String modifiedBy )
    {
        this.modifiedBy = modifiedBy;
    }


    public void setModifiedOn( Timestamp modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }

}
