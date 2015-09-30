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
@Table ( name = "COMPANY_DOTLOOP_PROFILEMAPPING")
@NamedQuery ( name = "CompanyDotloopProfileMapping.findAll", query = "SELECT cdpm FROM CompanyDotloopProfileMapping cdpm")
public class CompanyDotloopProfileMapping implements Serializable
{
    private static final long serialVersionUID = 1L;


    public CompanyDotloopProfileMapping()
    {}

    @Id
    @Column ( name = "COMPANY_PROFILE_MAPPING_ID")
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


}