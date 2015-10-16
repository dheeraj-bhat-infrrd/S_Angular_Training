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
@Table ( name = "COLLECTION_DOTLOOP_PROFILEMAPPING")
@NamedQuery ( name = "CollectionDotloopProfileMapping.findAll", query = "SELECT cdpm FROM CollectionDotloopProfileMapping cdpm")
public class CollectionDotloopProfileMapping implements Serializable
{
    private static final long serialVersionUID = 1L;


    public CollectionDotloopProfileMapping()
    {}

    @Id
    @Column ( name = "COLLECTION_PROFILE_MAPPING_ID")
    private int id;

    @Column ( name = "COMPANY_ID")
    private long companyId;

    @Column ( name = "PROFILE_ID")
    private String profileId;

    @Column ( name = "PROFILE_EMAIL_ADDRESS")
    private String profileEmailAddress;

    @Column ( name = "PROFILE_NAME")
    private String profileName;

    @Column ( name = "PROFILE_ACTIVE")
    private boolean active;

    @Column ( name = "REGION_ID")
    private long regionId;

    @Column ( name = "BRANCH_ID")
    private long branchId;

    @Column ( name = "AGENT_ID")
    private long agentId;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "MODIFIED_BY")
    private String modifiedBy;

    @Column ( name = "CREATED_BY")
    private String createdBy;


    public int getId()
    {
        return id;
    }


    public void setId( int id )
    {
        this.id = id;
    }


    public String getProfileId()
    {
        return profileId;
    }


    public void setProfileId( String profileId )
    {
        this.profileId = profileId;
    }


    public String getProfileEmailAddress()
    {
        return profileEmailAddress;
    }


    public void setProfileEmailAddress( String profileEmailAddress )
    {
        this.profileEmailAddress = profileEmailAddress;
    }


    public String getProfileName()
    {
        return profileName;
    }


    public void setProfileName( String profileName )
    {
        this.profileName = profileName;
    }


    public boolean isActive()
    {
        return active;
    }


    public void setActive( boolean active )
    {
        this.active = active;
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