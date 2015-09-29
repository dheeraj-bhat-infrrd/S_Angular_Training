package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;

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
    private static final Long serialVersionUID = 1L;


    public CollectionDotloopProfileMapping()
    {}

    @Id
    @Column ( name = "COLLECTION_PROFILE_MAPPING_ID")
    private int id;

    @Column ( name = "COMPANY_ID")
    private Long companyId;

    @Column ( name = "PROFILE_ID")
    private String profileId;

    @Column ( name = "PROFILE_EMAIL_ADDRESS")
    private String profileEmailAddress;

    @Column ( name = "PROFILE_NAME")
    private String profileName;

    @Column ( name = "PROFILE_ACTIVE")
    private boolean active;

    @Column ( name = "REGION_ID")
    private Long regionId;

    @Column ( name = "BRANCH_ID")
    private Long branchId;

    @Column ( name = "AGENT_ID")
    private Long agentId;


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


    public Long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( Long companyId )
    {
        this.companyId = companyId;
    }


    public Long getRegionId()
    {
        return regionId;
    }


    public void setRegionId( Long regionId )
    {
        this.regionId = regionId;
    }


    public Long getBranchId()
    {
        return branchId;
    }


    public void setBranchId( Long branchId )
    {
        this.branchId = branchId;
    }


    public Long getAgentId()
    {
        return agentId;
    }


    public void setAgentId( Long agentId )
    {
        this.agentId = agentId;
    }


}