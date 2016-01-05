package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the crm_master database table.
 */
@Entity
@Table ( name = "CRM_BATCH_TRACKER")
public class CrmBatchTracker implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @Column ( name = "ID")
    private int crmBatchTrackerId;
    
    @Column ( name = "COMPANY_ID")
    private long companyId;
    
    @Column ( name = "REGION_ID")
    private long regionId;
    
    @Column ( name = "BRANCH_ID")
    private long branchId;
    
    @Column ( name = "AGENT_ID")
    private long agentId;

    @Column ( name = "SOURCE")
    private String source;

    @Column ( name = "RECENT_RECORD_FETCHED_START_DATE")
    private Timestamp recentRecordFetchedStartDate;
    
    @Column ( name = "RECENT_RECORD_FETCHED_END_DATE")
    private Timestamp recentRecordFetchedEndDate;
    
    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;
    
    @Column ( name = "ERROR")
    private String error;
    
    @Column ( name = "DESCRIPTION")
    private String description;



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



    public long getCompanyId()
    {
        return companyId;
    }



    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }



    public long getRegionId()
    {
        return regionId;
    }



    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }



    public long getBranchId()
    {
        return branchId;
    }



    public void setBranchId( long branchId )
    {
        this.branchId = branchId;
    }



    public long getAgentId()
    {
        return agentId;
    }

    public void setAgentId( long agentId )
    {
        this.agentId = agentId;
    }

    public String getSource()
    {
        return source;
    }



    public void setSource( String source )
    {
        this.source = source;
    }



    public Timestamp getRecentRecordFetchedStartDate()
    {
        return recentRecordFetchedStartDate;
    }



    public void setRecentRecordFetchedStartDate( Timestamp recentRecordFetchedStartDate )
    {
        this.recentRecordFetchedStartDate = recentRecordFetchedStartDate;
    }



    public Timestamp getRecentRecordFetchedEndDate()
    {
        return recentRecordFetchedEndDate;
    }



    public void setRecentRecordFetchedEndDate( Timestamp recentRecordFetchedEndDate )
    {
        this.recentRecordFetchedEndDate = recentRecordFetchedEndDate;
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

}