package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table ( name = "REFERRAL_INVITATION")
public class ReferralInvitiation implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final String REFERRAL_ID_COLUMN = "referralId";
    
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "REFERRAL_INVITATION_ID")
    private long referralInvitaitonId;

    @Column ( name = "REFERRAL_ID")
    private String referralId;

    @Column ( name = "REFERRAL_NAME")
    private String referralName;

    @Column ( name = "REFERRAL_DESCRIPTION")
    private String referralDescription;

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


    public long getReferralInvitaitonId()
    {
        return referralInvitaitonId;
    }


    public void setReferralInvitaitonId( long referralInvitaitonId )
    {
        this.referralInvitaitonId = referralInvitaitonId;
    }


    public String getReferralId()
    {
        return referralId;
    }


    public void setReferralId( String referralId )
    {
        this.referralId = referralId;
    }


    public String getReferralName()
    {
        return referralName;
    }


    public void setReferralName( String referralName )
    {
        this.referralName = referralName;
    }


    public String getReferralDescription()
    {
        return referralDescription;
    }


    public void setReferralDescription( String referralDescription )
    {
        this.referralDescription = referralDescription;
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
