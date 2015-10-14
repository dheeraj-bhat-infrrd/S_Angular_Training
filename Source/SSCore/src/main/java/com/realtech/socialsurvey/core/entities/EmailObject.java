package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
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


}
