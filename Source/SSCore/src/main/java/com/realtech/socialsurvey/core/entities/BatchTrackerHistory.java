package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * @author rohit
 *
 */

@Entity
@Table ( name = "BATCH_TRACKER_HISTORY")
public class BatchTrackerHistory  implements Serializable 
{
    private static final long serialVersionUID = 1L;

    
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "HISTORY_ID")
    private long historyId;
    
    @ManyToOne
    @JoinColumn ( name = "BATCH_TRACKER_ID")
    private BatchTracker batchTracker;

    @Column ( name = "START_TIME")
    private Timestamp sartTime;
    
   
    @Column ( name = "END_TIME")
    private Timestamp endTime;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;
    
    @Column ( name = "CREATED_BY")
    private String createdBy;
    
    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column ( name = "MODIFIED_BY")
    private String modifiedBy;

    @Column ( name = "ERROR")
    private String error;

    public long getHistoryId()
    {
        return historyId;
    }

    public void setHistoryId( long historyId )
    {
        this.historyId = historyId;
    }

    
    public BatchTracker getBatchTracker()
    {
        return batchTracker;
    }

    public void setBatchTracker( BatchTracker batchTracker )
    {
        this.batchTracker = batchTracker;
    }

    public Timestamp getSartTime()
    {
        return sartTime;
    }

    public void setSartTime( Timestamp sartTime )
    {
        this.sartTime = sartTime;
    }

    public Timestamp getEndTime()
    {
        return endTime;
    }

    public void setEndTime( Timestamp endTime )
    {
        this.endTime = endTime;
    }

    
    public Timestamp getCreatedOn()
    {
        return createdOn;
    }

    public void setCreatedOn( Timestamp createdOn )
    {
        this.createdOn = createdOn;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
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

    public String getError()
    {
        return error;
    }

    public void setError( String error )
    {
        this.error = error;
    }

}
