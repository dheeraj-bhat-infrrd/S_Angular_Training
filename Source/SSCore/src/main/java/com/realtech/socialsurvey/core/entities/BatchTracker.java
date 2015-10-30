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

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "LAST_RUN_TIME")
    private Timestamp lastRunTime;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column ( name = "MODIFIED_BY")
    private String modifiedBy;


    public String getBatchType()
    {
        return batchType;
    }


    public void setBatchType( String batchType )
    {
        this.batchType = batchType;
    }


    public Timestamp getLastRunTime()
    {
        return lastRunTime;
    }


    public void setLastRunTime( Timestamp lastRunTime )
    {
        this.lastRunTime = lastRunTime;
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
