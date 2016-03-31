package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table ( name = "USER_REFERRAL_MAPPING")
public class UserReferralMapping implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "USER_REFERRAL_MAPPING_ID")
    private long userReferralMappingId;

    @OneToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "REFERRAL_INVITATION_ID")
    private ReferralInvitiation referralInvitation;

    @OneToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "USER_ID")
    private User user;

    @Column ( name = "STATUS")
    private int status;

    @Column ( name = "CREATED_ON")
    private Timestamp createdOn;

    @Column ( name = "CREATED_BY")
    private String createdBy;

    @Column ( name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @Column ( name = "MODIFIED_BY")
    private String modifiedBy;


    public long getUserReferralMappingId()
    {
        return userReferralMappingId;
    }


    public void setUserReferralMappingId( long userReferralMappingId )
    {
        this.userReferralMappingId = userReferralMappingId;
    }


    public ReferralInvitiation getReferralInvitation()
    {
        return referralInvitation;
    }


    public void setReferralInvitation( ReferralInvitiation referralInvitation )
    {
        this.referralInvitation = referralInvitation;
    }


    public User getUser()
    {
        return user;
    }


    public void setUser( User user )
    {
        this.user = user;
    }


    public int getStatus()
    {
        return status;
    }


    public void setStatus( int status )
    {
        this.status = status;
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


}
