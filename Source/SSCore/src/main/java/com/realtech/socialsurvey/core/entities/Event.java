package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@Table ( name = "EVENT")
@NamedQuery ( name = "Event.findAll", query = "SELECT e FROM Event e")
public class Event implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Column ( name = "AGENT_ID")
    private int agentId;

    @Column ( name = "BRANCH_ID")
    private int branchId;

    @Column ( name = "REGION_ID")
    private int regionId;

    @Id
    @Column ( name = "COMPANY_ID")
    private long companyId;

    @Column ( name = "EVENT_TYPE")
    private String eventType;

    @Column ( name = "ACTION")
    private String action;

    @Id
    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column ( name = "MODIFIED_BY")
    private String modifiedBy;


    public int getAgentId()
    {
        return agentId;
    }


    public void setAgentId( int agentId )
    {
        this.agentId = agentId;
    }


    public int getBranchId()
    {
        return branchId;
    }


    public void setBranchId( int branchId )
    {
        this.branchId = branchId;
    }


    public int getRegionId()
    {
        return regionId;
    }


    public void setRegionId( int regionId )
    {
        this.regionId = regionId;
    }


    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public String getEventType()
    {
        return eventType;
    }


    public void setEventType( String eventType )
    {
        this.eventType = eventType;
    }


    public String getAction()
    {
        return action;
    }


    public void setAction( String action )
    {
        this.action = action;
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
