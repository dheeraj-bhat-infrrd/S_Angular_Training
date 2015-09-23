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
@Table ( name = "DOTLOOP_PROFILE_LOOP_MAPPING")
@NamedQuery ( name = "LoopProfileMapping.findAll", query = "SELECT lpm FROM LoopProfileMapping lpm")
public class LoopProfileMapping implements Serializable
{
    private static final long serialVersionUID = 1L;


    public LoopProfileMapping()
    {}

    @Id
    @Column ( name = "PROFILE_LOOP_MAPPING_ID")
    private long id;

    @Column ( name = "PROFILE_ID")
    private String profileId;

    @Column ( name = "PROFILE_LOOP_ID")
    private String profileLoopId;

    @Column ( name = "PROFILE_LOOP_VIEW_ID")
    private String profileViewId;

    @Column ( name = "LOOP_CLOSED_TIME")
    private Timestamp loopClosedTime;


    public long getId()
    {
        return id;
    }


    public void setId( long id )
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


    public String getProfileLoopId()
    {
        return profileLoopId;
    }


    public void setProfileLoopId( String profileLoopId )
    {
        this.profileLoopId = profileLoopId;
    }


    public String getProfileViewId()
    {
        return profileViewId;
    }


    public void setProfileViewId( String profileViewId )
    {
        this.profileViewId = profileViewId;
    }


    public Timestamp getLoopClosedTime()
    {
        return loopClosedTime;
    }


    public void setLoopClosedTime( Timestamp loopClosedTime )
    {
        this.loopClosedTime = loopClosedTime;
    }


}