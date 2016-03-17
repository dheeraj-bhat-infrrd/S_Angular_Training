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


/**
 * The persistent class for the USER_EMAIL_MAPPING database table.
 */
@Entity
@Table ( name = "USER_EMAIL_MAPPING")
@NamedQuery ( name = "UserEmailMapping.findAll", query = "SELECT s FROM UserEmailMapping s")
public class UserEmailMapping implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "USER_EMAIL_MAPPING_ID")
    private long userEmailMappingId;

    @Column ( name = "EMAIL_ID")
    private String emailId;

    // bi-directional many-to-one association to User
    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "USER_ID")
    private User user;
    
    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "COMPANY_ID")
    private Company company;

    @Column ( name = "CREATED_BY")
    private String createdBy;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "MODIFIED_BY")
    private String modifiedBy;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column ( name = "STATUS")
    private int status;


    /**
     * @return the userEmailMappingId
     */
    public long getUserEmailMappingId()
    {
        return userEmailMappingId;
    }


    /**
     * @param userEmailMappingId the userEmailMappingId to set
     */
    public void setUserEmailMappingId( long userEmailMappingId )
    {
        this.userEmailMappingId = userEmailMappingId;
    }


    public Company getCompany()
    {
        return company;
    }


    public void setCompany( Company company )
    {
        this.company = company;
    }
    
    /**
     * @return the emailId
     */
    public String getEmailId()
    {
        return emailId;
    }


    /**
     * @param emailId the emailId to set
     */
    public void setEmailId( String emailId )
    {
        this.emailId = emailId;
    }


    /**
     * @return the user
     */
    public User getUser()
    {
        return user;
    }


    /**
     * @param user the user to set
     */
    public void setUser( User user )
    {
        this.user = user;
    }


    /**
     * @return the createdBy
     */
    public String getCreatedBy()
    {
        return createdBy;
    }


    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
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
     * @return the modifiedBy
     */
    public String getModifiedBy()
    {
        return modifiedBy;
    }


    /**
     * @param modifiedBy the modifiedBy to set
     */
    public void setModifiedBy( String modifiedBy )
    {
        this.modifiedBy = modifiedBy;
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
     * @return the serialversionuid
     */
    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }
}
