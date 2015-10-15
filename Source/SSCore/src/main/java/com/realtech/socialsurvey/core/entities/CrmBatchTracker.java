package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the crm_master database table.
 */
@Entity
@Table ( name = "CRM_BATCH_TRACKER")
@NamedQuery ( name = "CrmMaster.findAll", query = "SELECT c FROM CrmBatchTracker c")
public class CrmBatchTracker implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @Column ( name = "ID")
    private int crmBatchTrackerId;

    @Column ( name = "SOURCE")
    private String source;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "RECENT_RECORD_FETCHED_DATE")
    private Timestamp recentRecordFetchedDate;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column ( name = "COMPANY_ID")
    private long companyId;


    public CrmBatchTracker()
    {}


    public int getCrmBatchTrackerId()
    {
        return crmBatchTrackerId;
    }


    public void setCrmBatchTrackerId( int crmBatchTrackerId )
    {
        this.crmBatchTrackerId = crmBatchTrackerId;
    }


    public String getSource()
    {
        return source;
    }


    public void setSource( String source )
    {
        this.source = source;
    }


    public Timestamp getCreatedOn()
    {
        return createdOn;
    }


    public void setCreatedOn( Timestamp createdOn )
    {
        this.createdOn = createdOn;
    }


    public Timestamp getRecentRecordFetchedDate()
    {
        return recentRecordFetchedDate;
    }


    public void setRecentRecordFetchedDate( Timestamp recentRecordFetchedDate )
    {
        this.recentRecordFetchedDate = recentRecordFetchedDate;
    }


    public Timestamp getModifiedOn()
    {
        return modifiedOn;
    }


    public void setModifiedOn( Timestamp modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


}