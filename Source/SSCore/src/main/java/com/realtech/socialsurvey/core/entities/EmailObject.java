package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table ( name = "EMAIL_ENTITY")
public class EmailObject implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "ID", unique = true, nullable = false)
    private Long Id;

    @Column ( name = "EMAIL_OBJECT")
    private byte[] emailBinaryObject;


    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "MODIFIED_BY")
    private String modifiedBy;

    @Column ( name = "CREATED_BY")
    private String createdBy;


    public Long getId()
    {
        return Id;
    }


    public void setId( Long id )
    {
        Id = id;
    }


    public byte[] getEmailBinaryObject()
    {
        return emailBinaryObject;
    }


    public void setEmailBinaryObject( byte[] emailBinaryObject )
    {
        this.emailBinaryObject = emailBinaryObject;
    }


    public Timestamp getModifiedOn()
    {
        return modifiedOn;
    }


    public void setModifiedOn( Timestamp modifiedOn )
    {
        this.modifiedOn = modifiedOn;
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


    public String getCreatedBy()
    {
        return createdBy;
    }


    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }


}
