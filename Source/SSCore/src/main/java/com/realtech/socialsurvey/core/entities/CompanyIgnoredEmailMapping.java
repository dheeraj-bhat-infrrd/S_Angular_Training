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
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table ( name = "COMPANY_IGNORED_EMAIL_MAPPING")
@NamedQuery ( name = "CompanyIgnoredEmailMapping.findAll", query = "SELECT s FROM CompanyIgnoredEmailMapping s")
public class CompanyIgnoredEmailMapping implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "COMPANY_IGNORED_EMAIL_MAPPING_ID")
    private long CompanyIgnoredEmailMappingId;

    @Column ( name = "EMAIL_ID")
    private String emailId;

    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "COMPANY_ID")
    private Company company;
    
    @Column(name = "STATUS")
    private int status;

    @Column ( name = "CREATED_BY")
    private String createdBy;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "MODIFIED_BY")
    private String modifiedBy;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    public long getCompanyIgnoredEmailMappingId()
    {
        return CompanyIgnoredEmailMappingId;
    }

    public void setCompanyIgnoredEmailMappingId( long companyIgnoredEmailMappingId )
    {
        CompanyIgnoredEmailMappingId = companyIgnoredEmailMappingId;
    }

    public String getEmailId()
    {
        return emailId;
    }

    public void setEmailId( String emailId )
    {
        this.emailId = emailId;
    }

    public Company getCompany()
    {
        return company;
    }

    public void setCompany( Company company )
    {
        this.company = company;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedOn()
    {
        return createdOn;
    }

    public void setCreatedOn( Timestamp createdOn )
    {
        this.createdOn = createdOn;
    }

    public String getModifiedBy()
    {
        return modifiedBy;
    }

    public void setModifiedBy( String modifiedBy )
    {
        this.modifiedBy = modifiedBy;
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
}