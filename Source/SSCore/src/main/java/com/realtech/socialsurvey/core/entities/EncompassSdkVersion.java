package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author rohit
 *
 */

@Entity
@Table(name = "ENCOMPASS_SDK_VERSION")
public class EncompassSdkVersion implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private long id;

    @Column(name = "SDK_VERSION")
    private String sdkVersion;

    @Column(name = "HOST_NAME")
    private String hostName;
    
    @Column(name = "STATUS")
    private int status;
    
    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;
    
    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public String getSdkVersion()
    {
        return sdkVersion;
    }

    public void setSdkVersion( String sdkVersion )
    {
        this.sdkVersion = sdkVersion;
    }

    public String getHostName()
    {
        return hostName;
    }

    public void setHostName( String hostName )
    {
        this.hostName = hostName;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus( int status )
    {
        this.status = status;
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



}
