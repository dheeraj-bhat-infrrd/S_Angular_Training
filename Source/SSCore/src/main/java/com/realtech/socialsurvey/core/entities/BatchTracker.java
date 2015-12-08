package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * 
 * @author rohit
 *
 */

@Entity
@Table ( name = "BATCH_TRACKER")
public class BatchTracker implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @Column ( name = "BATCH_TRACKER_ID")
    private int batchTrackerId;

    @Column ( name = "BATCH_TYPE")
    private String batchType;

    @Column ( name = "BATCH_NAME")
    private String batchName;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "LAST_START_TIME")
    private Timestamp lastStartTime;
    
    @Column ( name = "LAST_END_TIME")
    private Timestamp lastEndTime;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column ( name = "MODIFIED_BY")
    private String modifiedBy;

    @Column ( name = "ERROR")
    private String error;
    
    public String getBatchName()
    {
        return batchName;
    }


    public void setBatchName( String batchName )
    {
        this.batchName = batchName;
    }


    public Timestamp getLastStartTime()
    {
        return lastStartTime;
    }


    public void setLastStartTime( Timestamp lastStartTime )
    {
        this.lastStartTime = lastStartTime;
    }


    public Timestamp getLastEndTime()
    {
        return lastEndTime;
    }


    public void setLastEndTime( Timestamp lastEndTime )
    {
        this.lastEndTime = lastEndTime;
    }


    public String getError()
    {
        return error;
    }


    public void setError( String error )
    {
        this.error = error;
    }


    public String getDescription()
    {
        return description;
    }


    public void setDescription( String description )
    {
        this.description = description;
    }


    @Column ( name = "DESCRIPTION")
    private String description;

    public String getBatchType()
    {
        return batchType;
    }


    public void setBatchType( String batchType )
    {
        this.batchType = batchType;
    }


    public int getBatchTrackerId()
    {
        return batchTrackerId;
    }


    public void setBatchTrackerId( int batchTrackerId )
    {
        this.batchTrackerId = batchTrackerId;
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


    public String getModifiedBy()
    {
        return modifiedBy;
    }


    public void setModifiedBy( String modifiedBy )
    {
        this.modifiedBy = modifiedBy;
    }


}
